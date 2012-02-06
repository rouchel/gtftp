import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import sun.print.resources.serviceui;

class HelpFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btOk;
	private JLabel infolab1;
	private JLabel infolab2;
	
	public HelpFrame() {
		// TODO Auto-generated constructor stub
		super();
		setBounds(500, 320, 300, 180);
		setTitle("About Me");
		setLayout(null);
		setResizable(false);
		setVisible(true);

		btOk = new JButton("OK");
		add(btOk);
		btOk.setBounds(120, 115, 60, 25);
		btOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				setVisible(false);
			}
		});
		
		infolab1 = new JLabel();
		infolab1.setBounds(75, 40, 220, 15);
		add(infolab1);
		infolab1.setFont(new Font("", 1, 13));
		infolab1.setText("Author: MaxWit 115 MMG");
		
		infolab2 = new JLabel();
		infolab2.setBounds(75, 60, 180, 15);
		add(infolab2);
		infolab2.setFont(new Font("", 1, 13));
		infolab2.setText("Version: 1.0");
		setFocusable(true);
		setFocusableWindowState(true);
		setAlwaysOnTop(true);
	}
}
public class TftpGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Container cont;
	private JMenuBar menuBar;
	private JMenu menuRun;
	private JMenu menuSet;
	private JMenu menuHelp;
	private JMenuItem menuAbout;
	private JMenuItem menuSetPath;
	private JMenuItem menuItemStart;
	private JMenuItem menuItemStop;
	private JLabel label;
	private JProgressBar progressBar;
	private JScrollPane sp;
	private JButton btStat;
	private JButton btExit;
	private JButton btPath;
	private JTextField pathTf;
	private JButton ipAddr;
	message msg = null;

	public TftpGui(String str) throws UnknownHostException {
		super(str);
		cont = getContentPane();
		cont.setLayout(null);
		cont.setBackground(Color.white);

		menuBar = new JMenuBar();
		cont.add(menuBar);
		setJMenuBar(menuBar);

		menuRun = new JMenu("Run");
		menuBar.add(menuRun);

		menuSet = new JMenu("Setup");
		menuBar.add(menuSet);

		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);

		menuSetPath = new JMenuItem("Path");
		menuSet.add(menuSetPath);
		menuSetPath.addActionListener(actlis);

		menuAbout = new JMenuItem("About Me");
		menuHelp.add(menuAbout);
		menuAbout.addActionListener(actlis);

		menuItemStart = new JMenuItem("Start");
		menuRun.add(menuItemStart);

		menuItemStop = new JMenuItem("Stop");
		menuRun.add(menuItemStop);

		label = new JLabel("MaxWit Tftp Server");
		cont.add(label);
		label.setLocation(300, 10);
		label.setSize(200, 45);
		label.setFont(new Font("", 0, 19));
		label.setForeground(Color.black);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(740, 420));
		sp = new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		cont.add(sp);
		sp.setBounds(30, 55, 740, 420);
		panel.setLayout(new GridLayout(20, 1));

		ipAddr = new JButton();
		ipAddr.setText(InetAddress.getLocalHost().getAddress().toString());
		progressBar = new JProgressBar(1, 100);
		progressBar.setValue(78);
		progressBar.setString("  78% filename -> 192.168.1.133");
		progressBar.setStringPainted(true);
		panel.add(progressBar);

		pathTf = new JTextField();
		cont.add(pathTf);
		pathTf.setBounds(120, 500, 220, 30);

		btPath = new JButton("PATH");
		cont.add(btPath);
		btPath.setBounds(30, 500, 80, 30);
		btPath.addActionListener(actlis);

		btStat = new JButton("START");
		cont.add(btStat);
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
				Server t1 = null;
				
				try {
					t1 = new Server(TftpGui.this.msg);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new Thread(t1).start();
			}
			
		});

		btExit = new JButton("EXIT");
		cont.add(btExit);
		btExit.setBounds(600, 500, 80, 30);
		btExit.addActionListener(actlis);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(250, 120, 800, 600);
		setResizable(false);
		setVisible(true);
	}

	private ActionListener actlis = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ae) {
			// TODO Auto-generated method stub
			if (ae.getSource() == btPath || ae.getSource() == menuSetPath) {
				JFrame frame = new JFrame();
				FileDialog fileDialog = new FileDialog(frame, "File Dialog");
				fileDialog.setVisible(true);
				pathTf.setText(fileDialog.getDirectory());
			}
			if (ae.getSource() == btExit) {
				System.exit(0);
			}
			if (ae.getSource() == menuAbout) {
				new HelpFrame();
			}
		}
	};

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TftpGui frame = new TftpGui("Tftp Utility");
	}
}
