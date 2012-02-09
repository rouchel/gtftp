import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DecimalFormat;

class Transfer extends Tftp implements Runnable {

	private DatagramSocket subSocket;
	private DatagramPacket subPacket;

	private byte[] rcvBuffer;
	private byte[] sndBuffer;
	private InetAddress destAddr;
	private int destPort;
	private short opcode;
	private String serverPath;
	private String fileName;
	private double filesize;
	private double sendsize;
	protected String sendMsg;
	protected int percent;
	protected DecimalFormat df;

	static final int RetryNum = 5;

	protected void progressMsg() {
		// System.out.print(sendMsg + '\r');
	}

	public Transfer(InetAddress addr, int port, short request,
			String path, String file, String mode) {
		destAddr = addr;
		destPort = port;
		opcode = request;
		serverPath = path + "/";
		fileName = file;
		rcvBuffer = new byte[PKG_LEN];
		sndBuffer = new byte[PKG_LEN];
		df = new DecimalFormat("#0.00");
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
		subPacket = new DatagramPacket(rcvBuffer, PKG_LEN, destAddr,
				destPort);
		RandomAccessFile af = null;

		try {
			try {
				af = new RandomAccessFile(serverPath + fileName, "r");
			} catch (FileNotFoundException e1) {
				setError(sndBuffer, (short) 1, fileName + " is not exist!");
				sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
				e1.printStackTrace();
				return;
			}

			filesize = af.length();

			subSocket.setSoTimeout(2000);
			short blknum = 0;
			int len;
			while (true) {
				len = af.read(sndBuffer, 4, PKG_LEN - 4);
				setOpcode(sndBuffer, DAT);
				blknum++;
				setBlknum(sndBuffer, blknum);

				int i;
				for (i = 0; i < RetryNum; i++) {
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
							System.out.println("Timeout!");
							return;
						}

						continue;
					}

					switch (getOpcode(rcvBuffer)) {
						case ACK :
							if (getBlknum(rcvBuffer) == blknum)
								i = RetryNum + 1;
							break;
						case ERR :
							return;
					}
				}

				if (len >= 0) {
					sendsize += len;
				}
				if (i == RetryNum) {
					sendMsg = fileName + " -> "
							+ destAddr.getHostAddress() + " Failed!";
				} else {					
					percent = (int) (sendsize * 100 / filesize);
					sendMsg = Long.toString(percent) + "% " + "( "
							+ getSizeString(sendsize) + getSizeUnit(sendsize) + " / "
							+ getSizeString(filesize) + getSizeUnit(filesize) +  " )" + fileName
							+ " -> " + destAddr.getHostAddress();
				}

				progressMsg();

				if (len < PKG_LEN - 4) {
					System.out.println(len);
					break;
				}

			}

			System.out.println(fileName + " send to " + destAddr
					+ " is done!~");
		} finally {
			af.close();
			subSocket.close();
		}
	}
	
	private String getSizeString (double size) {
		if (size < 1000) {
			return Double.toString(size);
		} else if ((size > 1000) && (size < 1000000)) {
			return  df.format(size / 1000);
		} else if ((size > 1000000) & (size < 1000000000)){
			return df.format(size / 1000000);
		} else {
			return df.format(size / 1000000000);
		}
	}
	
	private String getSizeUnit(double size) {
		if (size < 1000) {
			return "B";
		} else if ((size > 1000) && (size < 1000000)) {
			return "K";
		} else if ((size > 1000000) & (size < 1000000000)){
			return "M";
		} else {
			return "G";
		}
	}
	
	private void wrq() throws IOException {
		subSocket = new DatagramSocket();
		subPacket = new DatagramPacket(sndBuffer, PKG_LEN, destAddr,
				destPort);
		RandomAccessFile af = new RandomAccessFile(fileName, "rw");

		try {
			short blknum = 0;
			int len = 0;
			while (true) {
				setAck(sndBuffer, blknum);
				sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
				len = receivePacket(subSocket, subPacket, rcvBuffer);
				blknum = (short) getBlknum(rcvBuffer);

				System.out.println(blknum);

				af.write(rcvBuffer, 0, len);

				if (len < PKG_LEN - 4) {
					break;
				}
			}

			setAck(sndBuffer, blknum);
			sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
		} finally {
			subSocket.close();
			af.close();
		}

		System.out.println(fileName + " receive from " + destAddr
				+ " is done!~");
	}

}
