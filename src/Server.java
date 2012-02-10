import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Tftp {
	protected String serverPath = SERVER_PATH;

	void createTransfer(InetAddress addr, int port, short request, String path,
			String file, String mode) {

		Transfer transfer = new Transfer(addr, port, request, path, file, mode);

		new Thread(transfer).start();
	}

	public void run() {
		byte[] rcvBuffer = new byte[PKG_LEN];
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(TFTP_PORT);
			socket.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		DatagramPacket packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);

		while (!Thread.interrupted()) {

			try {
				receivePacket(socket, packet, rcvBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if (e.getMessage().equalsIgnoreCase("Receive timed out")) {
					continue;
				}
				e.printStackTrace();
				break;
			}

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
				// System.out.println("filename: " + filename);
				// System.out.println("mod:      " + mod);

				System.out.println(packet.getSocketAddress());
				createTransfer(packet.getAddress(), packet.getPort(), opcode,
						serverPath, filename, mod);

				break;

			default:

				break;
			}
		}

		socket.close();
		System.out.println("server closed!");

	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
