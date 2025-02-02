package exercise.coding.kiwee.ai.protocol;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import exercise.coding.kiwee.ai.shared.art.Song;
import exercise.coding.kiwee.ai.shared.person.Singer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaylorSwift extends Singer {

	@JsonProperty("grammy_wins")
	private int grammyWins;

	@JsonProperty("famous_albums")
	private List<String> famousAlbums;

	// Default constructor
	public TaylorSwift() {
	}

	// Parameterized constructor
	@JsonCreator
	public TaylorSwift(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("age") int age,
			@JsonProperty("gender") String gender, @JsonProperty("genre") String genre,
			@JsonProperty("debut_date") LocalDate debutDate, @JsonProperty("active") boolean active,
			@JsonProperty("label_company") String labelCompany, @JsonProperty("artforms") List<Song> artforms,
			@JsonProperty("grammy_wins") int grammyWins, @JsonProperty("famous_albums") List<String> famousAlbums) {
		super(id, name, age, gender, genre, debutDate, active, labelCompany, artforms);
		this.grammyWins = grammyWins;
		this.famousAlbums = famousAlbums;
	}

	// Getters and Setters
	public int getGrammyWins() {
		return grammyWins;
	}

	public void setGrammyWins(int grammyWins) {
		this.grammyWins = grammyWins;
	}

	public List<String> getFamousAlbums() {
		return famousAlbums;
	}

	public void setFamousAlbums(List<String> famousAlbums) {
		this.famousAlbums = famousAlbums;
	}

	// Override equals()
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true; // Same reference
		if (o == null || getClass() != o.getClass())
			return false; // Type check

		// Cast and compare fields
		TaylorSwift that = (TaylorSwift) o;
		return grammyWins == that.grammyWins && Objects.equals(getId(), that.getId())
				&& Objects.equals(getName(), that.getName()) && Objects.equals(getAge(), that.getAge())
				&& Objects.equals(getGender(), that.getGender()) && Objects.equals(getGenre(), that.getGenre())
				&& Objects.equals(getDebutDate(), that.getDebutDate()) && Objects.equals(isActive(), that.isActive())
				&& Objects.equals(getLabelCompany(), that.getLabelCompany())
				&& Objects.equals(getArtforms(), that.getArtforms()) && Objects.equals(famousAlbums, that.famousAlbums);
	}

	// Override hashCode()
	@Override
	public int hashCode() {
		return Objects.hash(getId(), getName(), getAge(), getGender(), getGenre(), getDebutDate(), isActive(),
				getLabelCompany(), getArtforms(), grammyWins, famousAlbums);
	}

	@Override
	public String toString() {
		return "TaylorSwift{" + super.toString() + ", grammyWins=" + grammyWins + ", famousAlbums=" + famousAlbums
				+ '}';
	}
}
