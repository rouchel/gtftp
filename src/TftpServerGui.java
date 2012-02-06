import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.ScrollBarUI;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class TftpServerGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton statButton;
	private JButton pathButton;
	private JTextField hostText;
	private JTextField pathText;
	private JScrollBar scrollBar = new JScrollBar();
	// private JPanel inforPanel = new JPanel();
	private JTextArea inforPanel = new JTextArea();
	
	public TftpServerGui() {
		// TODO Auto-generated constructor stub

		setBounds(100, 100, 800, 600);
		
		statButton = new JButton("Start");
		statButton.setBounds(20, 20, 80, 20);
		
		hostText = new JTextField();
		hostText.setBounds(110, 20, 600, 20);

		pathButton = new JButton("Path");
		pathButton.setBounds(20, 60, 80, 20);
	
		pathText = new JTextField();
		pathText.setBounds(110, 60, 600, 20);
		
		inforPanel.setBounds(20, 100, 700, 400);
		inforPanel.setBackground(Color.GRAY);
		inforPanel.setLayout(new GridLayout(20, 1));
		
		scrollBar.setBounds(720, 100, 10, 400);
		scrollBar.setMaximum(10);

		System.out.println(scrollBar.getValue());
		
		JProgressBar progressBar = new JProgressBar(1, 100);
		JTextField   infoTextP  = new JTextField();

		inforPanel.add(progressBar);
		inforPanel.add(infoTextP);
		
		add(pathButton);
		add(statButton);
		add(hostText);
		add(pathText);
		add(inforPanel);
		add(scrollBar);

		setLayout(new LayoutManager() {
			
			@Override
			public void removeLayoutComponent(Component comp) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Dimension preferredLayoutSize(Container parent) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Dimension minimumLayoutSize(Container parent) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void layoutContainer(Container parent) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addLayoutComponent(String name, Component comp) {
				// TODO Auto-generated method stub
				
			}
		});
		
		setVisible(true);
		
		for (int i = 0; i <= 100; i++) {
			progressBar.setValue(i);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		TftpServerGui tftp = new TftpServerGui();
	}
}
