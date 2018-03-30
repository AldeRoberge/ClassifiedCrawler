package listings;

import jtable.ListingTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StringsUtility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListingCrawler {

	private static final Logger logger = LoggerFactory.getLogger(ListingCrawler.class);
	private final ListingTable listingTable;

	private ArrayList<SubCategory> listings = new ArrayList<>();

	public ListingCrawler(ListingTable listingTable) {
		this.listingTable = listingTable;
	}

	/**
	 * @return All listings from every sub categories
	 */
	public List<SubCategory> getAllListings() {
		return listings;
	}

	/**
	 * Update selected sub categories
	 */
	public void addOrRemoveSubCategoriesFromCrawler(List<SubCategory> newSelectedSubCategories) {

		for (SubCategory oldSubCategory : listings) { // Removed
			if (!newSelectedSubCategories.contains(oldSubCategory)) {
				logger.info("Sous catégorie '" + oldSubCategory.getName() + "' enlevée.");
				listings.remove(oldSubCategory);
			}
		}

		for (SubCategory newSubCategory : newSelectedSubCategories) { // Added
			if (!listings.contains(newSubCategory)) {
				logger.info("Sous catégorie '" + newSubCategory.getName() + "' ajoutée.");
				listings.add(newSubCategory);
			}
		}

	}

	private boolean isCurrentlyReloading = false;

	public void updateListings() {

		try {

			if (!isCurrentlyReloading) {
				isCurrentlyReloading = true;

				logger.info("Recherche dans les " + listings.size() + " sous-catégories sélectionnées...");

				for (SubCategory subCategory : listings) {

					List<Listing> allListings = getAllListingsForSubCategory(subCategory);

					for (Listing listing : allListings) {
						if (!subCategory.listings.contains(listing)) {
							logger.info("Added new listing '" + listing.titre + "' in sub category '"
									+ subCategory.getName() + "'...");
							subCategory.listings.add(listing);
						}
					}
				}

				isCurrentlyReloading = false;

			} else {
				logger.error("Recherche déja en cours, attente du prochain reload.");
			}

			updateTable();

		} catch (Exception e) { //Very often a concurent exception due to changes while reloading
			e.printStackTrace();
			logger.error("Error with reloading listings!");
		}

	}

	private void updateTable() {

		List<Listing> allListings = new ArrayList<>();
		for (SubCategory s : listings) {
			allListings.addAll(s.listings);
		}

		listingTable.setTableData(allListings);

	}

	private List<Listing> getAllListingsForSubCategory(SubCategory s) {
		String url = s.getUrl();

		//Make sure we get all the listings in a single page (TODO : Veirify it doesn't get blocked at a certain #)
		if (url.endsWith("&nbr=10")) {
			url += "10";
		}

		List<Listing> listings = new ArrayList<>();

		//

		boolean isInsideListings = false;

		try {

			Listing listing = new Listing();

			URL oracle = null;
			oracle = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				if (inputLine.contains("<div id=\"annonce_categorie\">")) {
					isInsideListings = true;
				} else if (inputLine.contains("pager_annonce")) {
					isInsideListings = false;
				}

				if (isInsideListings) {

					if (inputLine.contains("<div class=\"clear\">")) { //Reached a new listing
						listings.add(listing);

						//logger.info(listing.toString());

						listing = new Listing();
					}

					/*
					 <div class='titre_liste_annonce'>Chambre et pension pour travailleurs - Amos</div>
					 <div class='date_liste_annonce'>Date de parution : 27 mars 2018</div>
					 <div class='ville_liste_annonce'>Lieu de vente : Amos</div>
					 <div class='numero_liste_annonce'>Numéro d'annonce : 213311</div>
					 <div class='description_liste_annonce'></div>
					 <div class='prix_liste_annonce'>Prix : n/d</div>
					 <div class='image_annonce'><div class='image_liste_annonce'><img src='http://tvc9.cablevision.qc.ca/sites/default/files/styles/pac_liste/public/pacs/immobilier/payant/213311_img_346693.jpg?itok=mDQ-QfmQ' style="max-width:120px; max-height:90px;"></div>
					 */

					if (inputLine.contains("titre_liste_annonce")) {
						listing.titre = StringsUtility.inbetween(inputLine, "<div class='titre_liste_annonce'>",
								"</div>");
					} else if (inputLine.contains("date_liste_annonce")) {
						listing.date = StringsUtility.inbetween(inputLine, "Date de parution : ", "</div>");
					} else if (inputLine.contains("ville_liste_annonce")) {
						listing.ville = StringsUtility.inbetween(inputLine, "Lieu de vente : ", "</div>");
					} else if (inputLine.contains("numero_liste_annonce")) {
						listing.numero = StringsUtility.inbetween(inputLine, "Numéro d'annonce : ", "</div>");
					} else if (inputLine.contains("description_liste_annonce")) {
						if (!inputLine.contains("\"</div>")) { // Sometimes it doesn't end on a closing div!
							listing.description = StringsUtility.inbetween(inputLine, "liste_annonce'>", 1);
						} else {
							listing.description = StringsUtility.inbetween(inputLine, "description_liste_annonce'>",
									"</div>");
						}
					} else if (inputLine.contains("prix_liste_annonce")) {
						listing.prix = StringsUtility.inbetween(inputLine, "Prix : ", "</div>");
					} else if (inputLine.contains("image_annonce")) {


						String imageUrl = StringsUtility.inbetween(inputLine, "annonce'>", "</div>");


						if (imageUrl.contains("<img src='") && imageUrl.contains("' style")) {
							imageUrl = StringsUtility.inbetween(imageUrl, "<img src='", "' style");
						} else {
							logger.info(imageUrl);
						}

						listing.imageUrl = imageUrl;


					}

				}

			}
			in.close();

		} catch (Exception e) {
			logger.error("Error with listing URL : " + url);
			e.printStackTrace();
		}

		return listings;

	}

}
