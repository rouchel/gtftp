import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

class message {
	String hostIp;
}

public class Server extends tftp implements Runnable {

	message msg = null;

	public Server(message msg) throws UnknownHostException {
		this.msg = msg;
	}

	@Override
	public void run() {
		try {
			byte[] rcvBuffer = new byte[PKG_LEN];
			DatagramSocket socket = new DatagramSocket(TFTP_PORT);
			DatagramPacket packet = new DatagramPacket(rcvBuffer,
					rcvBuffer.length);

			while (true) {
				receivePacket(socket, packet, rcvBuffer);

				short opcode;
				String filename;
				String mod;

				opcode = getOpcode(rcvBuffer);
				System.out.println("opcode:   " + opcode);
				
				switch (opcode) {
				case RRQ:
				case WRQ:
					filename = getFilename(rcvBuffer);
					mod = getMod(rcvBuffer);
					System.out.println("filename: " + filename);
					System.out.println("mod:      " + mod);
					
					transfer t2 = new transfer(
							packet.getAddress(), packet.getPort(), opcode,
							filename, mod);
					System.out.println(packet.getSocketAddress());
					
					new Thread(t2).start();
					break;

				default:

					break;
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (@SuppressWarnings("hiding") IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
