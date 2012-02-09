import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ServerGUI extends Server {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JLabel label;
	private JScrollPane sp;
	private JButton btStat;
	private JButton btExit;
	private JButton btPath;
	private JTextField pathTf;
	private JPanel panel;
	// Map<Long, Thread> threadList;
	HashMap<Long, Thread> threadList;

	void stopServer() {
		isRunning = false;

		for (Thread thread : threadList.values()) {
			if (thread.isAlive()) {
				thread.stop();
			}
		}

		btStat.setText("Start");
	}

	void startServer() {
		isRunning = true;
		btStat.setText("Stop");
	}

	private void addProcessBar(JProgressBar bar) {
		panel.add(bar);
		panel.updateUI();
	}

	public ServerGUI() {
		threadList = new HashMap<Long, Thread>();

		frame = new JFrame("TFTP Server");
		frame.setLayout(null);
		frame.setBackground(Color.white);

		label = new JLabel("MaxWit Tftp Server");
		frame.add(label);
		label.setLocation(300, 10);
		label.setSize(200, 45);
		label.setFont(new Font("", 0, 19));
		label.setForeground(Color.black);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(740, 420));
		sp = new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.add(sp);
		sp.setBounds(30, 55, 740, 420);
		panel.setLayout(new GridLayout(20, 1));

		pathTf = new JTextField();
		pathTf.setText(SERVER_PATH);
		frame.add(pathTf);
		pathTf.setBounds(120, 500, 220, 30);

		pathTf.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyChar() == '\n')
					serverPath = pathTf.getText();
			}
		});

		btPath = new JButton("PATH");
		frame.add(btPath);
		btPath.setBounds(30, 500, 80, 30);
		btPath.addActionListener(actlis);

		btStat = new JButton();
		frame.add(btStat);
		btStat.setBounds(690, 500, 80, 30);
		btStat.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if (isRunning) {
					stopServer();
				} else {
					startServer();
				}
			}
		});

		btExit = new JButton("EXIT");
		frame.add(btExit);
		btExit.setBounds(600, 500, 80, 30);
		btExit.addActionListener(actlis);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(250, 120, 800, 600);
		frame.setResizable(false);
		frame.setVisible(true);
		startServer();
	}

	private ActionListener actlis = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ae) {
			// TODO Auto-generated method stub
			if (ae.getSource() == btPath) {
				JFrame frame = new JFrame();
				FileDialog fileDialog = new FileDialog(frame, "File Dialog");
				fileDialog.setVisible(true);
				pathTf.setText(fileDialog.getDirectory());
				serverPath = pathTf.getText();
			}
			if (ae.getSource() == btExit) {
				System.exit(0);
			}
		}
	};

	@Override
	void createTransfer(InetAddress addr, int port, short request, String path,
			String file, String mode) {
		// TODO Auto-generated method stub
		Thread transferThread;

		TransferGui transfer = new TransferGui(addr, port, request, path, file,
				mode);

		addProcessBar(transfer.progressBar);
		transferThread = new Thread(transfer);
		System.out.println(transferThread.getId());
		transferThread.start();

		threadList.put(transferThread.getId(), transferThread);
	}

	public static void main(String[] args) {
		ServerGUI server = new ServerGUI();
		server.run();
	}
}
