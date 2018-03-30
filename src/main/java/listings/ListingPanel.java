package listings;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListingPanel extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(ListingPanel.class);

	Listing listing;

	public ListingPanel(Listing listing) {
		this.listing = listing;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel mainInfoPanel = new JPanel();
		add(mainInfoPanel);
		mainInfoPanel.setLayout(new BoxLayout(mainInfoPanel, BoxLayout.Y_AXIS));

		JPanel titlePanel = new JPanel();
		mainInfoPanel.add(titlePanel);

		JLabel titleLabel = new JLabel(listing.titre);
		titlePanel.add(titleLabel);

		JPanel descImagePanel = new JPanel();
		mainInfoPanel.add(descImagePanel);
		descImagePanel.setLayout(new BoxLayout(descImagePanel, BoxLayout.X_AXIS));

		JPanel descPanel = new JPanel();
		descImagePanel.add(descPanel);

		JLabel lblDesc = new JLabel(listing.description);
		descPanel.add(lblDesc);

		JPanel imagePanel = new JPanel();
		descImagePanel.add(imagePanel);

		//Load imageUrl

		JLabel lblImage = new JLabel("Image");

		URL url;
		try {
			url = new URL(listing.imageUrl);
			BufferedImage image = ImageIO.read(url);

			lblImage.setIcon(new ImageIcon(image));
		} catch (MalformedURLException e) {
			logger.error("Invalid URL for imageUrl : " + listing.imageUrl);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error when trying to get imageUrl from " + listing.imageUrl);
			e.printStackTrace();
		}

		imagePanel.add(lblImage);

		add(getDetailsPanel());

	}

	private JPanel getDetailsPanel() {

		JPanel detailsPanel = new JPanel(new BorderLayout(4, 2));
		detailsPanel.setBorder(new EmptyBorder(0, 6, 0, 6));

		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 2, 2));
		detailsPanel.add(labelPanel, BorderLayout.WEST);

		JPanel detailPanel = new JPanel(new GridLayout(0, 1, 2, 2));
		detailsPanel.add(detailPanel, BorderLayout.CENTER);

		labelPanel.add(new JLabel("Num�ro", JLabel.TRAILING));
		detailPanel.add(new JLabel(listing.numero));

		labelPanel.add(new JLabel("Ville", JLabel.TRAILING));
		detailPanel.add(new JLabel(listing.ville));

		labelPanel.add(new JLabel("Date", JLabel.TRAILING));
		detailPanel.add(new JLabel(listing.date));

		labelPanel.add(new JLabel("Prix", JLabel.TRAILING));
		detailPanel.add(new JLabel(listing.prix));

		JButton visitButton = new JButton("Visiter sur le site");
		visitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Visit : " + listing.url);
			}
		});
		detailsPanel.add(visitButton, BorderLayout.SOUTH);

		return detailsPanel;

	}

}
