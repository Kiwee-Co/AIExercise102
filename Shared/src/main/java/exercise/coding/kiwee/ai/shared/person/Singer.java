package exercise.coding.kiwee.ai.shared.person;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import exercise.coding.kiwee.ai.shared.art.Song;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Singer extends Celebrity {

	@JsonProperty("genre")
	private String genre;

	@JsonProperty("debut_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate debutDate;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("label_company")
	private String labelCompany;

	@JsonProperty("artforms")
	private List<Song> artforms; // List of songs

	// Default constructor
	public Singer() {
	}

	// Parameterized constructor
	@JsonCreator
	public Singer(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("age") int age,
			@JsonProperty("gender") String gender, @JsonProperty("genre") String genre,
			@JsonProperty("debut_date") LocalDate debutDate, @JsonProperty("active") boolean active,
			@JsonProperty("label_company") String labelCompany, @JsonProperty("artforms") List<Song> artforms) {
		super(id, name, age, gender);
		this.genre = genre;
		this.debutDate = debutDate;
		this.active = active;
		this.labelCompany = labelCompany;
		this.artforms = artforms;
	}

	// Getters and Setters
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public LocalDate getDebutDate() {
		return debutDate;
	}

	public void setDebutDate(LocalDate debutDate) {
		this.debutDate = debutDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLabelCompany() {
		return labelCompany;
	}

	public void setLabelCompany(String labelCompany) {
		this.labelCompany = labelCompany;
	}

	public List<Song> getArtforms() {
		return artforms;
	}

	public void setArtforms(List<Song> artforms) {
		this.artforms = artforms;
	}

	@Override
	public String toString() {
		return "Singer{" + super.toString() + ", genre='" + genre + '\'' + ", debut_date=" + debutDate + ", active="
				+ active + ", label_company='" + labelCompany + '\'' + ", artforms=" + artforms + '}';
	}

	// Override equals()
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true; // Same reference
		if (!(o instanceof Singer))
			return false; // Type check
		if (!super.equals(o))
			return false; // Compare superclass fields

		Singer singer = (Singer) o;
		return active == singer.active && Objects.equals(genre, singer.genre)
				&& Objects.equals(debutDate, singer.debutDate) && Objects.equals(labelCompany, singer.labelCompany)
				&& Objects.equals(artforms, singer.artforms);
	}

	// Override hashCode()
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), genre, debutDate, active, labelCompany, artforms);
	}
}
