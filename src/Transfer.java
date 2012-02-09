import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

class Transfer extends Tftp implements Runnable {

	private DatagramSocket subSocket;
	private DatagramPacket subPacket;

	private byte[] rcvBuffer;
	private byte[] sndBuffer;
	private byte[] dataBuffer;
	private InetAddress destAddr;
	private int destPort;
	private short opcode;
	private String serverPath;
	private String fileName;
	private String mod;
	private long filesize;
	private long sendsize;
	private long recvsize;
	protected String sendMsg;
	protected int percent;

	static final int RetryNum = 5;

	protected void progressMsg(boolean isRight) {
		// System.out.print(sendMsg + '\r');
	}

	public Transfer(InetAddress addr, int port, short request, String path,
			String file, String mode) {
		destAddr = addr;
		destPort = port;
		opcode = request;
		serverPath = path + "/";
		fileName = file;
		mod = mode;
		rcvBuffer = new byte[PKG_LEN];
		sndBuffer = new byte[PKG_LEN];
	}

	@Override
	public void run() {
		try {
			if (opcode == RRQ) {
				rrq();
			} else if (opcode == WRQ) {
				wrq();
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void rrq() throws IOException {
		subSocket = new DatagramSocket();
		subPacket = new DatagramPacket(rcvBuffer, PKG_LEN, destAddr, destPort);
		subSocket.setSoTimeout(2000);

		RandomAccessFile af;
		try {
			af = new RandomAccessFile(serverPath + fileName, "r");
		} catch (FileNotFoundException e1) {
			setError(sndBuffer, (short) 1, fileName + " is not exist!");
			sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
			sendMsg = fileName + " -> " + destAddr.getHostAddress()
					+ " Failed! (file is not exist!)";
			progressMsg(false);
			subSocket.close();
			return;
		}

		filesize = af.length();
		short blknum = 0;
		int len;
		L1 : while (true) {
			len = af.read(sndBuffer, 4, PKG_LEN - 4);
			setOpcode(sndBuffer, DAT);
			blknum++;
			setBlknum(sndBuffer, blknum);

			for (int i = 0; i < RetryNum; i++) {
				if (len > 0) {
					sendPacket(subSocket, subPacket, sndBuffer, len + 4);
				} else {
					sendPacket(subSocket, subPacket, sndBuffer, 4);
				}

				try {
					receivePacket(subSocket, subPacket, rcvBuffer);
				} catch (Exception e) {
					System.out.println("rcvRetry: " + (i + 1));

					if (i == RetryNum - 1) {
						// System.out.println("Timeout!");
						sendMsg = fileName + " -> " + destAddr.getHostAddress()
								+ " Failed! (TimeOut!)";
						progressMsg(false);
						break L1;
					}
					continue;
				}

				switch (getOpcode(rcvBuffer)) {
					case ACK :
						if (getBlknum(rcvBuffer) == blknum)
							i = RetryNum + 1;
						break;
					case ERR :
						break L1;
				}
			}

			if (len >= 0) {
				sendsize += len;
			}

			percent = (int) (sendsize * 100 / filesize);
			sendMsg = Long.toString(percent) + "% " + "( "
					+ Long.toString(sendsize) + " / " + Long.toString(filesize)
					+ " )" + fileName + " -> " + destAddr.getHostAddress();
			progressMsg(true);

			if (len < PKG_LEN - 4) {
				System.out.println(len);
				break;
			}
		}

		// System.out.println(fileName + " send to " + destAddr + " is done!~");
		af.close();
		subSocket.close();
	}

	private void wrq() throws IOException {
		subSocket = new DatagramSocket();
		subPacket = new DatagramPacket(sndBuffer, PKG_LEN, destAddr, destPort);
		subSocket.setSoTimeout(2000);

		RandomAccessFile af = new RandomAccessFile(serverPath + fileName, "rw");

		try {
			short blknum = 0;
			int len = 0;
			while (true) {
				setAck(sndBuffer, blknum);

				blknum++;

				for (int i = 0; i < RetryNum; i++) {
					sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);

					try {
						len = receivePacket(subSocket, subPacket, rcvBuffer);
					} catch (Exception e) {
						System.out.println("rcvRetry: " + (i + 1));

						if (i == RetryNum - 1) {
							// System.out.println("Timeout!");
							sendMsg = fileName + " <- "
									+ destAddr.getHostAddress()
									+ " Failed! (TimeOut!)";
							progressMsg(false);
							return;
						}
						continue;
					}

					switch (getOpcode(rcvBuffer)) {
						case DAT :
							if (getBlknum(rcvBuffer) == blknum)
								i = RetryNum + 1;
							break;
						case ERR :
							return;
					}

					dataBuffer = Arrays.copyOfRange(rcvBuffer, 4, PKG_LEN);
					try {
						af.write(dataBuffer, 0, len - 4);
					} catch (Exception e) {
						setError(sndBuffer, (short) 1,
								"maybe the folder is full");
						sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
						sendMsg = fileName + " -> " + destAddr.getHostAddress()
								+ " Failed! (maybe the folder is full!)";
						progressMsg(false);
						subSocket.close();
						return;
					}

					recvsize += len - 4;

					sendMsg = fileName + "( " + Long.toString(recvsize) + " )"
							+ " <- " + destAddr.getHostAddress();
					progressMsg(true);
				}

				if (len < PKG_LEN - 4) {
					break;
				}
			}

			setAck(sndBuffer, blknum);
			sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
			sendMsg = fileName + "( " + Long.toString(recvsize) + " )" + " <- "
					+ destAddr.getHostAddress() + " is done!";
			progressMsg(true);

		} finally {
			subSocket.close();
			af.close();
		}

		System.out.println(fileName + " receive from " + destAddr
				+ " is done!~");
	}
}
