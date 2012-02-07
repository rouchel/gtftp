
public class ServerGUI extends Server {
	GUI gui;

	public ServerGUI() {
		// TODO Auto-generated constructor stub
		gui = new GUI("TFTP Server");
	}

	@Override
	void processTransferInfo(TransferInfo info) {
		// TODO Auto-generated method stub
		super.processTransferInfo(info);
		gui.addProcessBar(info.progressBar);
	}
	
	public static void main(String[] args) {
		ServerGUI server = new ServerGUI();
		server.run();
	}
}
