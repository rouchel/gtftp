import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class tftp {

	static final short RRQ = 1;
	static final short WRQ = 2;
	static final short DAT = 3;
	static final short ACK = 4;
	static final short ERR = 5;

	static final int TFTP_PORT = 69;
	static final int PKG_LEN = 516;

	public short getOpcode(byte[] rcvBuffer) {
		return (short) ((((short) rcvBuffer[0]) << 8) + (short) rcvBuffer[1] & 0x00ff);
	}

	public void setOpcode(byte[] sndBuffer, short op) {
		sndBuffer[0] = (byte) ((op >> 8) & 0xff);
		sndBuffer[1] = (byte) (op & 0xff);
	}

	public void setAck(byte[] sndBuffer, short blknum) {
		setOpcode(sndBuffer, ACK);
		sndBuffer[2] = (byte) ((blknum >> 8) & 0xFF);
		sndBuffer[3] = (byte) (blknum & 0xFF);
	}

	public void setError(byte[] sndBuffer, Short errNum, String str) {
		setOpcode(sndBuffer, ERR);
		sndBuffer[2] = (byte) ((errNum >> 8) & 0xFF);
		sndBuffer[3] = (byte) (errNum & 0xFF);

		byte[] tmp = str.getBytes();
		for (int i = 0; i < tmp.length; i++) {
			sndBuffer[i + 4] = tmp[i];
		}
	}

	public short getBlknum(byte[] rcvBuffer) {
		return (short) ((((short) rcvBuffer[2]) << 8) + ((short) rcvBuffer[3] & 0x00ff));
	}

	public void setBlknum(byte[] sndBuffer, int blknum) {
		sndBuffer[2] = (byte) ((blknum >> 8) & 0xff);
		sndBuffer[3] = (byte) (blknum & 0xff);
	}

	public String getFilename(byte[] rcvBuffer) {
		String filename = "";

		for (int i = 2; rcvBuffer[i] != 0; i++) {
			filename = filename + (char) rcvBuffer[i];
		}

		return filename;
	}

	public String getMod(byte[] rcvBuffer) {
		int i;
		String mod;

		for (i = 2; rcvBuffer[i] != 0; i++)
			;

		for (mod = "", i++; rcvBuffer[i] != 0; i++) {
			mod = mod + (char) rcvBuffer[i];

		}

		return mod;
	}

	// public int receivePacket(DatagramSocket socket, DatagramPacket packet,
	// byte[] rcvBuffer) {
	// int len;
	//
	// packet.setData(rcvBuffer, 0, PKG_LEN);
	// try {
	// socket.receive(packet);
	// len = packet.getLength();
	// } catch (Exception e) {
	// // TODO: handle exception
	// len = -1;
	// System.out.println("the length: " + len);
	// }
	//
	// return len;
	// }

	public int receivePacket(DatagramSocket socket, DatagramPacket packet,
			byte[] rcvBuffer) throws IOException {
		int len;

		packet.setData(rcvBuffer, 0, PKG_LEN);
		socket.receive(packet);
		len = packet.getLength();

		return len;
	}

	public int sendPacket(DatagramSocket socket, DatagramPacket packet,
			byte[] sndBuffer, int len) {
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

}