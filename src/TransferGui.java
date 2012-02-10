import java.awt.Color;
import java.net.InetAddress;

import javax.swing.JProgressBar;

public class TransferGui extends Transfer {
	JProgressBar progressBar;

	public TransferGui(InetAddress remoteAddress, int remotePort, short opcode,
			String path, String filename, String mod) {
		super(remoteAddress, remotePort, opcode, path, filename, mod);
		// TODO Auto-generated constructor stub

		progressBar = new JProgressBar(1, 100);
		progressBar.setStringPainted(true);
	}

	@Override
	protected void progressMsg(boolean isRight) {
		// TODO Auto-generated method stub
		// super.progressMsg();
		progressBar.setValue(percent);
		progressBar.setString(sendMsg);

		if (percent == 100) {
			progressBar.setValue(0);
			progressBar.setBackground(Color.cyan);
		} else if (!isRight) {
			progressBar.setValue(0);
			progressBar.setBackground(Color.red);
		}
	}

}
