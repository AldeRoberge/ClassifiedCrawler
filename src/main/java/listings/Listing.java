package listings;

import java.util.Objects;

public class Listing {

	public String titre;
	public String description;
	public String numero;
	public String ville;
	public String date;
	public String prix;
	public String imageUrl;
	
	//Details on page TODO
	
	public String url;
	
	public String name;
	public String nombreVisites;
	public String etat;

	@Override
	public String toString() {
		return "Listing{" +
				"titre='" + titre + '\'' +
				", description='" + description + '\'' +
				", numero='" + numero + '\'' +
				", ville='" + ville + '\'' +
				", date='" + date + '\'' +
				", prix='" + prix + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Listing listing = (Listing) o;
		return Objects.equals(numero, listing.numero);
	}

	@Override
	public int hashCode() {
		return Objects.hash(numero);
	}
}
