package ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import listings.SubCategorySelector;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

class UI {

	private static final Logger logger = LoggerFactory.getLogger(UI.class);

	private JFrame frame;

	private JLabel statusLabel;

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

		JPanel htmlOutput = new JPanel();
		horizonPane.setRightComponent(htmlOutput);

		//


		startTimer();
	}

	private void startTimer() {

		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			//int i = Properties.SECONDS_BETWEEN_RELOADS.getValueAsInt();

			int i = 10;

			public void run() {

				i--;

				statusLabel.setText("Reloading in " + i + " second(s)...");

				if ((i % 10 == 0) && i != 0)  { //When i is a multiple of 10
					logger.info("Reloading in " + i + " second(s)...");
				}

				if (i < 0) {
					logger.info("Temp écoulé, rechargement...");

					SubCategorySelector.getListingCrawler().updateListings();

					timer.cancel();
					startTimer();
				}
			}
		}, 0, 1000);

	}

}
