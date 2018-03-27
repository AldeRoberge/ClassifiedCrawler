package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UI {

	private JFrame frame;
	private JPanel htmlOutput;
	private JPanel categorySelector;
	private JPanel classifiedTable;
	private JLabel statusLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					
					
					
					// Start UI
					
					UI window = new UI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(UI.class.getResource("/com/sun/javafx/scene/web/skin/DecreaseIndent_16x16_JFX.png")));
		frame.setTitle("Outils de recherche - TVC9 Classified");
		frame.setBounds(100, 100, 606, 426);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel statusPanel = new JPanel();
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		
		statusLabel = new JLabel("Loading...");
		statusPanel.add(statusLabel);
		
		JSplitPane horizonPane = new JSplitPane();
		horizonPane.setResizeWeight(0.5);
		horizonPane.setDividerLocation(200);
		horizonPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(horizonPane, BorderLayout.CENTER);
		
		JSplitPane verticalPane = new JSplitPane();
		verticalPane.setResizeWeight(0.5);
		horizonPane.setLeftComponent(verticalPane);
		
		categorySelector = new JPanel();
		verticalPane.setLeftComponent(categorySelector);
		
		classifiedTable = new JPanel();
		verticalPane.setRightComponent(classifiedTable);
		
		htmlOutput = new JPanel();
		horizonPane.setRightComponent(htmlOutput);
	}

}
