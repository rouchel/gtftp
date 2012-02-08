import java.net.InetAddress;

import javax.swing.JProgressBar;

public class TransferInfo extends Transfer {
	JProgressBar progressBar;
	
	public TransferInfo(InetAddress remoteAddress, int remotePort,
			short opcode, String filename, String mod) {
		super(remoteAddress, remotePort, opcode, filename, mod);
		// TODO Auto-generated constructor stub

		progressBar = new JProgressBar(1, 100);
		progressBar.setStringPainted(true);
	}

	@Override
	protected void progressMsg() {
		// TODO Auto-generated method stub
		super.progressMsg();
		progressBar.setValue(percent);
		progressBar.setString(sendMsg);
	}
	
}
