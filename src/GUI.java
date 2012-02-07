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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private Container cont;

	private JLabel label;
	private JScrollPane sp;
	private JButton btStat;
	private JButton btExit;
	private JButton btPath;
	private JTextField pathTf;
	private JButton ipAddr;
	 private JPanel panel;

	public void addProcessBar(JProgressBar bar) {
		panel.add(bar);
		panel.updateUI();
		System.out.println("add bar!");
	}

	public GUI(String str) {	
		cont = getContentPane();
		cont.setLayout(null);
		cont.setBackground(Color.white);

		label = new JLabel("MaxWit Tftp Server");
		cont.add(label);
		label.setLocation(300, 10);
		label.setSize(200, 45);
		label.setFont(new Font("", 0, 19));
		label.setForeground(Color.black);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(740, 420));
		sp = new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		cont.add(sp);
		sp.setBounds(30, 55, 740, 420);
		panel.setLayout(new GridLayout(20, 1));
		// ipAddr = new JButton();
		// ipAddr.setText(InetAddress.getLocalHost().getAddress().toString());

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
			if (ae.getSource() == btPath) {
				JFrame frame = new JFrame();
				FileDialog fileDialog = new FileDialog(frame, "File Dialog");
				fileDialog.setVisible(true);
				pathTf.setText(fileDialog.getDirectory());
			}
			if (ae.getSource() == btExit) {
				System.exit(0);
			}
		}
	};
}
