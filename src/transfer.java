import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

class transfer extends tftp implements Runnable {

	private DatagramSocket subSocket;
	private DatagramPacket subPacket;

	private byte[] rcvBuffer;
	private byte[] sndBuffer;
	private InetAddress remoteAddress;
	private int remotePort;
	private short opcode;
	private String filename;
	private String mod;

	static final int RetryNum = 5;

	public transfer(InetAddress remoteAddress, int remotePort,
			short opcode, String filename, String mod) {
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.opcode = opcode;
		this.filename = filename;
		this.mod = mod;
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
		subPacket = new DatagramPacket(rcvBuffer, PKG_LEN, remoteAddress,
				remotePort);
		RandomAccessFile af = null;

		try {
			try {
				af = new RandomAccessFile(filename, "r");
			} catch (FileNotFoundException e1) {
				setError(sndBuffer, (short) 1, filename + " is not exist!");
				sendPacket(subSocket, subPacket, sndBuffer, PKG_LEN);
				e1.printStackTrace();
				return;
			}

			subSocket.setSoTimeout(2000);
			short blknum = 0;
			int len;
			while (true) {
				len = af.read(sndBuffer, 4, PKG_LEN - 4);
				setOpcode(sndBuffer, DAT);
				blknum++;
				setBlknum(sndBuffer, blknum);

				for (int i = 0; i < RetryNum; i++) {
					sendPacket(subSocket, subPacket, sndBuffer, len + 4);

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
					case ACK:
						if (getBlknum(rcvBuffer) == blknum)
							i = RetryNum;
						break;
					case ERR:
						return;
					}
				}

				if (len < PKG_LEN - 4)
					break;

			}

			System.out.println(filename + " send to " + remoteAddress
					+ " is done!~");
		} finally {
			af.close();
			subSocket.close();
		}
	}

	private void wrq() throws IOException {
		subSocket = new DatagramSocket();
		subPacket = new DatagramPacket(sndBuffer, PKG_LEN, remoteAddress,
				remotePort);
		RandomAccessFile af = new RandomAccessFile(filename, "rw");

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

		System.out.println(filename + " receive from " + remoteAddress
				+ " is done!~");
	}

}
