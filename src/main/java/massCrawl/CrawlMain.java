package massCrawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlMain {


    private static final Logger logger = LoggerFactory.getLogger(CrawlMain.class);

    public static final String TVC9_ROOT = "http://tvc9.cablevision.qc.ca/";
    public static final String PETITES_ANNONCES_ORIGIN = TVC9_ROOT + "?q=pa";

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, List<SubCategory>> run() throws IOException {


        //SubCategory, upper category name (IE. Meubles, Immobilier)
        HashMap<String, List<SubCategory>> categoryHashMap = new HashMap<>();

        logger.info("Chargement des categories depuis " + PETITES_ANNONCES_ORIGIN + "...");

        /**/

        final String CATEG_DELIMIT_START = "<div id=\"annonce\">";
        final String CATEG_DELIMIT_END = "</article>";

        boolean isInsideCategories = false;

        String currentCategory = "";

        ArrayList<SubCategory> bufferSubCategories = new ArrayList<>();

        /**/

        URL url = new URL(PETITES_ANNONCES_ORIGIN);
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
                    currentCategory = inputLine.substring(inputLine.indexOf("10\">") + 4, inputLine.indexOf("</a></span>"));

                    logger.info("Catégorie : " + currentCategory);

                } else if (inputLine.contains("<li><a href=")) { //Sub category
                    //<li><a href="/vehicules?ensemble=&code_e=vehicule&detail=Automobiles&secteur=&mot=&page=0&trie=&affichage=1&nbr=10">Automobiles&nbsp;(65)</a></li>

                    //Nom

                    String scName = inputLine.substring(inputLine.indexOf("10\">") + 4, inputLine.indexOf("("));
                    scName = scName.replaceAll("&nbsp;", " "); //Replace the symbol with spaces
                    scName = scName.trim(); //Remove trialing whitespace
                    logger.debug("Nom : " + scName);

                    //Url

                    String scUrl = TVC9_ROOT + inputLine.substring(inputLine.indexOf("a href=\"") + 8, inputLine.indexOf("\">"));
                    logger.debug("Url : " + scUrl);

                    //Nombre d'offres

                    int scNumber = Integer.parseInt(inputLine.substring(inputLine.indexOf("(") + 1, inputLine.indexOf(")")));
                    logger.debug("Nombre d'offres : " + scNumber);

                    //

                    logger.info(" > " + scName);
                    bufferSubCategories.add(new SubCategory(currentCategory, scName, scNumber, scUrl));

                }


            }


        }
        in.close();


        return categoryHashMap;

    }


}
