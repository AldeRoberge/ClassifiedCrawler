package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import listings.Listing;
import listings.ListingPanel;
import listings.SubCategorySelector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UI {

	private static final Logger logger = LoggerFactory.getLogger(UI.class);

	private JFrame frame;

	private JLabel statusLabel;
	private static JPanel htmlOutput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		/* Some vanity */

		logger.info("Bienvenue sur Classified Crawler");
		logger.info("");
		logger.warn("     _/_/_/  _/_/_/      _/_/    _/          _/  _/        _/_/_/_/  _/_/_/    ");
		logger.warn("  _/        _/    _/  _/    _/  _/          _/  _/        _/        _/    _/   ");
		logger.warn(" _/        _/_/_/    _/_/_/_/  _/    _/    _/  _/        _/_/_/    _/_/_/      ");
		logger.warn("_/        _/    _/  _/    _/    _/  _/  _/    _/        _/        _/    _/     ");
		logger.warn(" _/_/_/  _/    _/  _/    _/      _/  _/      _/_/_/_/  _/_/_/_/  _/    _/      ");
		logger.info("");
		logger.info("par Alde");
		logger.info("");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI window = new UI();
					window.frame.setLocationRelativeTo(null); //Centers the frame in the middle
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
	private UI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		//

		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(UI.class.getResource("/com/sun/javafx/scene/web/skin/DecreaseIndent_16x16_JFX.png")));
		frame.setTitle("Outils de recherche - TVC9 Classified");
		frame.setBounds(100, 100, 743, 421);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel statusPanel = new JPanel();
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);

		statusLabel = new JLabel("Loading...");
		statusPanel.add(statusLabel);
		
		JButton btnReload = new JButton("Rechargement automatique");
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubCategorySelector.getListingCrawler().updateListings();
			}
		});
		statusPanel.add(btnReload);

		JSplitPane horizonPane = new JSplitPane();
		horizonPane.setResizeWeight(0.5);
		horizonPane.setDividerLocation(200);
		horizonPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(horizonPane, BorderLayout.CENTER);

		JSplitPane verticalPane = new JSplitPane();
		verticalPane.setResizeWeight(0.5);
		horizonPane.setLeftComponent(verticalPane);

		JScrollPane categorySelector = SubCategorySelector.getCategorySelector();
		verticalPane.setLeftComponent(categorySelector);

		JPanel classifiedTable = SubCategorySelector.getListingTable();
		verticalPane.setRightComponent(classifiedTable);

		JScrollPane scrollPane = new JScrollPane();
		htmlOutput = new JPanel();
		scrollPane.setViewportView(htmlOutput);

		horizonPane.setRightComponent(scrollPane);

		//

	}

	public static void updateHtmlOutput(Listing listing) {
		htmlOutput.removeAll();
		htmlOutput.add(new ListingPanel(listing));
	}

}
