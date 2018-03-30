package jtable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import listings.Listing;
import ui.UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListingTable extends JPanel implements ActionListener {

	/**
	 * currently selected Listing.
	 */
	private Listing selectedListing;

	/**
	 * Directory listing
	 */
	private JTable table;

	/**
	 * Table model for Listing array.
	 */
	private ListingTableModel listingTableModel;
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;

	public ListingTable() {

		setLayout(new BorderLayout(3, 3));
		setBorder(new EmptyBorder(5, 5, 5, 5));

		table = new JTable();
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.setShowVerticalLines(false);
		table.setSelectionBackground(Color.PINK);
		table.setSelectionForeground(Color.WHITE);

		/*
		Popup menu
		*/
		JPopupMenu popupMenu = new JPopupMenu();
		table.setComponentPopupMenu(popupMenu);

		listingTableModel = new ListingTableModel();
		table.setModel(listingTableModel);

		//set font bold for column 1 (see CellRenderer at the bottom of this class)
		table.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer());

		listSelectionListener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();

				boolean isAdjusting = e.getValueIsAdjusting();

				if (!lsm.isSelectionEmpty()) {
					// Find out which indexes are selected.
					int minIndex = lsm.getMinSelectionIndex();
					int maxIndex = lsm.getMaxSelectionIndex();

					for (int i = minIndex; i <= maxIndex; i++) {
						if (lsm.isSelectedIndex(i)) {

							//Fixes row being incorrect after sortings
							int row = table.convertRowIndexToModel(i);

							selectedListing = listingTableModel.getListing(row);

							UI.updateHtmlOutput(selectedListing);

						}
					}

				}

			}

		};
		table.getSelectionModel().addListSelectionListener(listSelectionListener);

		JScrollPane tableScroll = new JScrollPane(table);
		Dimension d = tableScroll.getPreferredSize();
		tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
		add(tableScroll, BorderLayout.CENTER);

	}

	/**
	 * Update the table on the EDT
	 */
	public void setTableData(final List<Listing> newListings) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (listingTableModel == null) {
					listingTableModel = new ListingTableModel();
					table.setModel(listingTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				listingTableModel.setListings(newListings);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					table.setRowHeight(40);
					cellSizesSet = true;
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() instanceof JMenuItem) {
			JMenuItem menu = (JMenuItem) event.getSource();

		}

	}

	public void addListings(ArrayList<Listing> listingsToAdd) {
		final List<Listing> listings = listingTableModel.getListings();
		listings.addAll(listingsToAdd);

		setTableData(listings);
	}

	private void removeListings(ArrayList<Listing> listingsToRemove) {
		final List<Listing> listings = listingTableModel.getListings();

		for (Iterator<Listing> iterator = listings.iterator(); iterator.hasNext();) {
			Listing listing = iterator.next();

			if (listingsToRemove.contains(listing)) {
				iterator.remove();
			}
		}

		setTableData(listings);

	}

	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width < 0) {
			// use the preferred width of the header..
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			// altered 10->14 as per camickr comment.
			width = (int) preferred.getWidth() + 14;
		}
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	//UPDATE POPUPMENU END

}

class ListingTableModel extends AbstractTableModel {

	private List<Listing> listings;
	private String[] columns = { "Titre", "Description", "Numéro", "Ville", "Date", "Prix", "Image" };

	ListingTableModel() {
		listings = new ArrayList<>();
	}

	public Object getValueAt(int row, int column) {
		Listing listing = listings.get(row);
		switch (column) {
		case 0:
			return listing.titre;
		case 1:
			return listing.description;
		case 2:
			return listing.numero;
		case 3:
			return listing.ville;
		case 4:
			return listing.date;
		case 5:
			return listing.prix;
		case 6:
			return listing.imageUrl;
		default:
			System.err.println("Logic Error");
		}
		return "";
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Class<?> getColumnClass(int column) {
		/*switch (column) {
		 case 0:
		 return ImageIcon.class;
		 case 3:
		 return Long.class;
		 case 4:
		 return Date.class;
		 }*/
		return String.class;
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	public int getRowCount() {
		return listings.size();
	}

	public Listing getListing(int row) {
		return listings.get(row);
	}

	public void setListings(List<Listing> listings) {
		this.listings = listings;
		fireTableDataChanged();
	}

	public List<Listing> getListings() {
		return listings;
	}

	public void addListing(Listing listing) {
		listings.add(listing);
		fireTableDataChanged();
	}

}

class CellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// if (value>17 value<26) {
		this.setValue(table.getValueAt(row, column));
		this.setFont(this.getFont().deriveFont(Font.PLAIN));
		//}
		return this;
	}
}
