import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.Buffer;
import java.util.RandomAccess;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;

public class TftpServer {

	static final short RRQ = 1;
	static final short WRQ = 2;
	static final short DAT = 3;
	static final short ACK = 4;
	static final short ERR = 5;
	static final int DATA_LEN = 516;
	static final int TFTP_PORT = 69;

	static final int RetryNum = 5;

	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] rcvBuffer;
	private byte[] sndBuffer;
	private String mod;

	public TftpServer() throws SocketException {
		// TODO Auto-generated constructor stub

		socket = new DatagramSocket(TFTP_PORT);
		rcvBuffer = new byte[DATA_LEN];
		sndBuffer = new byte[DATA_LEN];

		packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
	}

	public TftpServer(SocketAddress binAddress) throws SocketException {
		// TODO Auto-generated constructor stub

		socket = new DatagramSocket(binAddress);
		rcvBuffer = new byte[DATA_LEN];
		sndBuffer = new byte[DATA_LEN];

		packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
	}

	public TftpServer(int port) throws SocketException {
		// TODO Auto-generated constructor stub

		socket = new DatagramSocket(port);
		rcvBuffer = new byte[DATA_LEN];
		sndBuffer = new byte[DATA_LEN];

		packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
	}

	public TftpServer(int port, InetAddress laddr) throws SocketException {
		// TODO Auto-generated constructor stub

		socket = new DatagramSocket(port, laddr);
		rcvBuffer = new byte[DATA_LEN];
		sndBuffer = new byte[DATA_LEN];

		packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
	}

	public int receivePacket() {
		int len;

		packet.setData(rcvBuffer, 0, DATA_LEN);
		try {
			socket.receive(packet);
			len = packet.getLength();
		} catch (Exception e) {
			// TODO: handle exception
			len = -1;
		}

		return len;
	}

	public int sendPacket(int len) {
		int sndLen;

		packet.setData(sndBuffer, 0, len);
		try {
			socket.send(packet);
			sndLen = packet.getLength();
		} catch (Exception e) {
			// TODO: handle exception
			sndLen = -1;
		}

		return sndLen;
	}

	public short getOpcode() {
		return (short) ((rcvBuffer[0] << 8) + rcvBuffer[1]);
	}

	public void setOpcode(short op) {
		sndBuffer[0] = (byte) ((op >> 8) & 0xff);
		sndBuffer[1] = (byte) (op & 0xff);
	}

	private int getBlknum() {
		return (int) ((rcvBuffer[2] << 8) + rcvBuffer[3]);
	}

	private void setBlknum(int blknum) {
		sndBuffer[2] = (byte) ((blknum >> 8) & 0xff);
		sndBuffer[3] = (byte) (blknum & 0xff);
	}

	public String getFilename() {
		String filename = "";

		for (int i = 2; rcvBuffer[i] != 0; i++) {
			filename = filename + (char) rcvBuffer[i];
		}

		return filename;
	}

	private void getMod() {
		int i;

		for (i = 2; rcvBuffer[i] != 0; i++)
			;

		for (mod = "", i++; rcvBuffer[i] != 0; i++) {
			mod = mod + (char) rcvBuffer[i];
		}
	}

	public void rcveFile(String filename) throws IOException {

	}

	public void sendFile(String filename) throws IOException {
		RandomAccessFile af = new RandomAccessFile(filename, "r");
		int blknum = 0;
		int len;

		getMod();

		while (true) {
			len = af.read(sndBuffer, 4, DATA_LEN - 4);
			setOpcode(DAT);
			blknum++;
			setBlknum(blknum);

			for (int i = 0; i < RetryNum; i++) {
				sendPacket(len + 4);
				receivePacket();

				switch (getOpcode()) {
				case ACK:
					if (getBlknum() == blknum)
						i = RetryNum;
					break;
				case ERR:
					break;
				}
			}

			if (len < DATA_LEN - 4)
				break;
		}
		af.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stubs
		String filename;

		try {
			TftpServer server = new TftpServer();
			while (true) {
				server.receivePacket();
				switch (server.getOpcode()) {
				case RRQ:
					filename = server.getFilename();
					server.sendFile(filename);
					break;
				case WRQ:
					filename = server.getFilename();
					server.rcveFile(filename);
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
		}

	}

}
