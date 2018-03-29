package listings;

import properties.Properties;
import properties.Property;

import java.util.ArrayList;
import java.util.Objects;

public class SubCategory {

	private String subCategory;
	private int numberOfOffers;

	private String url;

	ArrayList<Listing> listings;

	/**
	 * @param parentCategory Used to generate unique key for saving in properties checked/unchecked state
	 * @param subCategory    Name of this category
	 * @param numberOfOffers Total number of offers
	 * @param url            Listings url
	 */
	public SubCategory(String parentCategory, String subCategory, int numberOfOffers, String url) {
		this.subCategory = subCategory;
		this.numberOfOffers = numberOfOffers;
		this.url = url;

		// Since some subCategories have the same name, (I.E. Divers, Je recherche)
		// we use the name of the parent category to build the property key
		isChecked = Properties.generateBasicBooleanProperty(parentCategory + ":" + subCategory);

		listings = new ArrayList<>();
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

	public String getUrl() {
		return url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubCategory that = (SubCategory) o;
		return Objects.equals(subCategory, that.subCategory);
	}

	@Override
	public int hashCode() {

		return Objects.hash(subCategory);
	}
}
