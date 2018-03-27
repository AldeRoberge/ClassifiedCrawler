package massCrawl;

import properties.Properties;
import properties.Property;

public class SubCategory {

    private String subCategory;
    private int numberOfOffers;

    private String url;

    /**
     * @param parentCategory Used to generate unique key for saving in properties checked/unchecked state
     * @param subCategory Name of this category
     * @param numberOfOffers Total number of offers
     * @param url Listings url
     */
    public SubCategory(String parentCategory, String subCategory, int numberOfOffers, String url) {
        this.subCategory = subCategory;
        this.numberOfOffers = numberOfOffers;
        this.url = url;

        // Since some subCategories have the same name, (I.E. Divers, Je recherche)
        // we use the name of the parent category to build the property key
        isChecked = Properties.generateBasicBooleanProperty(parentCategory+":" + subCategory);
    }

    private Property isChecked;

    public boolean isChecked() {
        return isChecked.getValueAsBoolean();
    }

    public void setSelected(boolean checked) {
        isChecked.setNewValue(checked);
    }


    public String getName() {
        return subCategory;
    }


    @Override
    public String toString() {
        return subCategory + " (" + numberOfOffers + ")";
    }

}
