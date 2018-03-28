package massCrawl;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

public class ListingCrawler {

	private static final Logger logger = LoggerFactory.getLogger(ListingCrawler.class);

	HashMap<SubCategory, ArrayList<Listing>> listings = new HashMap<>();

	/**
	 * Update selected sub categories
	 */
	public void updateSubCategory(List<SubCategory> newSelectedSubCategories) {
		//Remove

		Iterator<Entry<SubCategory, ArrayList<Listing>>> it = listings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<SubCategory, ArrayList<Listing>> pair = (Map.Entry<SubCategory, ArrayList<Listing>>) it.next();

			SubCategory subCat = pair.getKey();

			if (!newSelectedSubCategories.contains(subCat)) {
				logger.info("Removed category '" + subCat.getName() + "' from ListingCrawler.");
				it.remove(); //remove category from listings
			}
		}

		for (SubCategory newSubCategory : newSelectedSubCategories) {
			if (!listings.containsKey(newSubCategory)) {
				listings.put(newSubCategory, new ArrayList<Listing>());
				logger.info("Added category '" + newSubCategory.getName() + "' in ListingCrawler.");
			}
		}

	}


	boolean isCurrentlyReloading = false;

	public void updateListings() {

		if (!isCurrentlyReloading) {


			logger.info("Crawling all " + listings.size() + " selected sub categories...");

			for (Entry<SubCategory, ArrayList<Listing>> pair : listings.entrySet()) {
				SubCategory subCat = pair.getKey();
				ArrayList<Listing> existingListings = pair.getValue();

				logger.info("Getting all listings for sub category '" + subCat.toString() + "'...");

				List<Listing> allListings = getAllListingsForSubCategory(subCat);

				for (Listing listing : allListings) {
					if (!existingListings.contains(listing)) {
						logger.info("Added new listing '" + listing.titre + "' in sub category '" + subCat.getName() + "'...");
						listings.get(subCat).add(listing);
					}
				}

			}

			isCurrentlyReloading = false;

		} else {
			logger.error("Currently crawling, waiting for next reload.");
		}


	}


	public List<Listing> getAllListingsForSubCategory(SubCategory s) {
		String url = s.getUrl();

		//Make sure we get all the listings in a single page (TODO : Veirify it doesn't get blocked at a certain #)
		if (url.endsWith("&nbr=10")) {
			url += "10";
		}

		List<Listing> listings = new ArrayList<Listing>();


		//


		boolean isInsideListings = false;

		try {

			Listing listing = new Listing();


			URL oracle = null;
			oracle = new URL(url);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(oracle.openStream()));

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


					/**
					 <div class='titre_liste_annonce'>Chambre et pension pour travailleurs - Amos</div>
					 <div class='date_liste_annonce'>Date de parution : 27 mars 2018</div>
					 <div class='ville_liste_annonce'>Lieu de vente : Amos</div>
					 <div class='numero_liste_annonce'>Numéro d'annonce : 213311</div>
					 <div class='description_liste_annonce'></div>
					 <div class='prix_liste_annonce'>Prix : n/d</div>
					 <div class='image_annonce'><div class='image_liste_annonce'><img src='http://tvc9.cablevision.qc.ca/sites/default/files/styles/pac_liste/public/pacs/immobilier/payant/213311_img_346693.jpg?itok=mDQ-QfmQ' style="max-width:120px; max-height:90px;"></div>
					 */


					if (inputLine.contains("titre_liste_annonce")) {
						listing.titre = inbetween(inputLine, "<div class='titre_liste_annonce'>", "</div>");
					} else if (inputLine.contains("date_liste_annonce")) {
						listing.date = inbetween(inputLine, "Date de parution : ", "</div>");
					} else if (inputLine.contains("ville_liste_annonce")) {
						listing.ville = inbetween(inputLine, "Lieu de vente : ", "</div>");
					} else if (inputLine.contains("numero_liste_annonce")) {
						listing.numero = inbetween(inputLine, "Numéro d'annonce : ", "</div>");
					} else if (inputLine.contains("description_liste_annonce")) {
						if (!inputLine.contains("\"</div>")) { // Sometimes it doesn't end on a closing div!
							listing.description = inbetween(inputLine, "liste_annonce'>", 1);
						} else {
							listing.description = inbetween(inputLine, "description_liste_annonce'>", "</div>");
						}
					} else if (inputLine.contains("prix_liste_annonce")) {
						listing.prix = inbetween(inputLine, "Prix : ", "</div>");
					} else if (inputLine.contains("image_annonce")) {
						listing.image = inbetween(inputLine, "annonce'>", "</div>");
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

	private String inbetween(String inputLine, String start, int removeTrailingChars) {
		if (!inputLine.contains(start)) {
			logger.error("Error : line '" + inputLine + "' doesn't contain string : " + start);
			return "null";
		}

		return inputLine.substring(inputLine.indexOf(start)+start.length(), removeTrailingChars);
	}

	private String inbetween(String inputLine, String start, String end) {

		//Escape special encoding (I.E. ; è being shown as &egrave;)
		inputLine = StringEscapeUtils.unescapeHtml4(inputLine);

		if (!inputLine.contains(start)) {
			logger.error("Error : line '" + inputLine + "' doesn't contain string : " + start);
			return "null";
		} else if (!inputLine.contains(end)) {
			logger.error("Error : line '" + inputLine + "' doesn't contain string : " + end);
			return "null";
		} else {
			return inputLine.substring(inputLine.indexOf(start) + start.length(), inputLine.indexOf(end));
		}
	}


}
