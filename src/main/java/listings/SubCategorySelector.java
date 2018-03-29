package listings;

import jtable.ListingTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import checkboxJtree.SubCategoryNodeData;
import checkboxJtree.SubCategoryNodeEditor;
import checkboxJtree.SubCategoryNodeRenderer;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Curtis Rueden (Original Java2s.com tutorial)
 * @author John Zukowski
 * @author Alde
 */
@SuppressWarnings("serial")
public class SubCategorySelector extends JScrollPane {

	private static final Logger logger = LoggerFactory.getLogger(SubCategorySelector.class);

	private static SubCategorySelector subCategorySelector = new SubCategorySelector();
	private static ListingTable listingTable = new ListingTable();
	private static ListingCrawler listingCrawler = new ListingCrawler(listingTable);

	private Map<String, List<SubCategory>> categories;

	/**
	 * @return Singleton SubCategorySelector (this, JTree)
	 */
	public static SubCategorySelector getCategorySelector() {
		return subCategorySelector;
	}

	/**
	 * @return Singleton ListingCrawler
	 */
	public static ListingCrawler getListingCrawler() {
		return listingCrawler;
	}

	/**
	 * @return Singleton ListingTable (JTable)
	 */
	public static ListingTable getListingTable() {
		return listingTable;
	}

	private SubCategorySelector() {
		super();

		CategoryCrawler categoryCrawler = new CategoryCrawler();
		categories = categoryCrawler.getAllCategories();

		logger.info("Chargement des catégories et sous catégories en tant qu'arbre...");

		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

		for (Map.Entry<String, List<SubCategory>> pair : categories.entrySet()) {

			String categoryName = pair.getKey();
			List<SubCategory> subCat = pair.getValue();

			final DefaultMutableTreeNode category = new DefaultMutableTreeNode(categoryName);

			for (SubCategory subCategory : subCat) {
				DefaultMutableTreeNode e = new DefaultMutableTreeNode(new SubCategoryNodeData(subCategory));
				category.add(e);
			}

			root.add(category);
		}

		final JTree tree = new JTree(root);

		final SubCategoryNodeRenderer renderer = new SubCategoryNodeRenderer();
		tree.setCellRenderer(renderer);

		final SubCategoryNodeEditor editor = new SubCategoryNodeEditor(tree);
		tree.setCellEditor(editor);
		tree.setEditable(true);

		// listen for changes in the selection
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(final TreeSelectionEvent e) {
				System.out.println(System.currentTimeMillis() + ": selection changed");
			}
		});

		// listen for changes in the model (including check box toggles)
		tree.getModel().addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeNodesChanged(final TreeModelEvent e) {
				System.out.println(System.currentTimeMillis() + ": nodes changed");


				//Reflect changes in listings

				List<SubCategory> selectedCategories = new ArrayList<>();

				for (Map.Entry<String, List<SubCategory>> pair : categories.entrySet()) {

					for (SubCategory s : pair.getValue()) {
						if (s.isChecked()) { //Check box is checked
							selectedCategories.add(s);
						}
					}
				}



				listingCrawler.addOrRemoveSubCategoriesFromCrawler(selectedCategories); //Update listing crawler
			}

			@Override
			public void treeNodesInserted(final TreeModelEvent e) {
				System.out.println(System.currentTimeMillis() + ": nodes inserted");
			}

			@Override
			public void treeNodesRemoved(final TreeModelEvent e) {
				System.out.println(System.currentTimeMillis() + ": nodes removed");
			}

			@Override
			public void treeStructureChanged(final TreeModelEvent e) {
				System.out.println(System.currentTimeMillis() + ": structure changed");
			}
		});

		setViewportView(tree);
	}

}

class CategoryCrawler {

	private static final Logger logger = LoggerFactory.getLogger(CategoryCrawler.class);

	private static final String TVC9_ROOT = "http://tvc9.cablevision.qc.ca/";
	private static final String PETITES_ANNONCES_ORIGIN = TVC9_ROOT + "?q=pa";

	public static void main(String[] args) {

		logger.info("Test running CategoryCrawler as standalone...");

		new CategoryCrawler().getAllCategories();
	}

	public Map<String, List<SubCategory>> getAllCategories() {

		//SubCategory, upper category name (IE. Meubles, Immobilier)
		HashMap<String, List<SubCategory>> categoryHashMap = new HashMap<>();

		logger.info("Chargement des categories depuis " + PETITES_ANNONCES_ORIGIN + "...");

		/**/

		final String CATEG_DELIMIT_START = "<div id=\"annonce\">";
		final String CATEG_DELIMIT_END = "</article>";

		boolean isInsideCategories = false;

		String currentCategory = "";

		ArrayList<SubCategory> bufferSubCategories = new ArrayList<>();

		URL url;
		try {
			url = new URL(PETITES_ANNONCES_ORIGIN);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains(CATEG_DELIMIT_START)) { //Entered categories
					logger.info("Trouvé le début des catégories...");
					isInsideCategories = true;
				} else if (inputLine.contains(CATEG_DELIMIT_END)) { //Left categories
					logger.info("Trouvé la fin des catégories...");
					isInsideCategories = false;
					break;
				}

				if (isInsideCategories) {

					if (inputLine.contains("<li><span>")) { //Beginning of new category

						//<li><span><a href="/vehicules?ensemble=&code_e=vehicule&detail=&secteur=&mot=&page=0&trie=&affichage=1&nbr=10">Véhicules</a></span></li>

						logger.info("Début de nouvelle catégorie trouvé...");

						categoryHashMap.put(currentCategory, bufferSubCategories); //Add all subCategories before changing to next

						bufferSubCategories = new ArrayList<>(); //reset buffer

						//Change current category
						currentCategory = inputLine.substring(inputLine.indexOf("10\">") + 4,
								inputLine.indexOf("</a></span>"));

						logger.info("Catégorie : " + currentCategory);

					} else if (inputLine.contains("<li><a href=")) { //Sub category
						//<li><a href="/vehicules?ensemble=&code_e=vehicule&detail=Automobiles&secteur=&mot=&page=0&trie=&affichage=1&nbr=10">Automobiles&nbsp;(65)</a></li>

						//Nom

						String scName = inputLine.substring(inputLine.indexOf("10\">") + 4, inputLine.indexOf("("));
						scName = scName.replaceAll("&nbsp;", " "); //Replace the symbol with spaces
						scName = scName.trim(); //Remove trialing whitespace
						logger.debug("Nom : " + scName);

						//Url

						String scUrl = TVC9_ROOT
								+ inputLine.substring(inputLine.indexOf("a href=\"") + 8, inputLine.indexOf("\">"));
						logger.debug("Url : " + scUrl);

						//Nombre d'offres

						int scNumber = Integer
								.parseInt(inputLine.substring(inputLine.indexOf("(") + 1, inputLine.indexOf(")")));
						logger.debug("Nombre d'offres : " + scNumber);

						//

						logger.info(" > " + scName);
						bufferSubCategories.add(new SubCategory(currentCategory, scName, scNumber, scUrl));

					}

				}

			}
			in.close();
		} catch (MalformedURLException e) {
			logger.error("Fatal error when trying to read URL " + TVC9_ROOT + "...");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return categoryHashMap;

	}

}
