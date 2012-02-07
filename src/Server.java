import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Tftp implements Runnable {

	void processTransferInfo(TransferInfo info) {

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

					TransferInfo subTransferThread = new TransferInfo(
							packet.getAddress(), packet.getPort(), opcode,
							filename, mod);
					System.out.println(packet.getSocketAddress());

					processTransferInfo(subTransferThread);

					new Thread(subTransferThread).start();
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


	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
