import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Server extends Tftp {
	protected String serverPath = "./";
	protected boolean isRunning = true;

	void createTransfer(InetAddress addr, int port, short request,
			String path, String file, String mode) {

		Transfer transfer = 
				new Transfer(addr, port, request, path, file, mode);

		new Thread(transfer).start();
	}

	public void run() {
		try {
			byte[] rcvBuffer = new byte[PKG_LEN];
			DatagramSocket socket = new DatagramSocket(TFTP_PORT);
			DatagramPacket packet = new DatagramPacket(rcvBuffer,
					rcvBuffer.length);

			while (true) {
				if (!isRunning) {
					Thread.sleep(100);
					continue;
				}

				receivePacket(socket, packet, rcvBuffer);

				short opcode;
				String filename;
				String mod;

				opcode = getOpcode(rcvBuffer);
				System.out.println("opcode:   " + opcode);

				switch (opcode) {
					case RRQ :
					case WRQ :
						filename = getFilename(rcvBuffer);
						mod = getMod(rcvBuffer);
						System.out.println("filename: " + filename);
						System.out.println("mod:      " + mod);

						System.out.println(packet.getSocketAddress());
						createTransfer(packet.getAddress(), packet.getPort(), opcode,
								serverPath, filename, mod);
						
						break;

					default :

						break;
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
