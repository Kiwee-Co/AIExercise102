package exercise.coding.kiwee.ai.shared.art;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Movie extends Artform {

	@JsonProperty("genre")
	private String genre; // Movie genre (e.g., Action, Comedy)

	// Default constructor
	public Movie() {
	}

	// Parameterized constructor
	public Movie(Long id, String title, String artist, LocalDate releaseDate, String genre) {
		super(id, title, artist, releaseDate);
		this.genre = genre;
	}

	// Getters and Setters
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	@Override
	public String toString() {
		return "Movie{" + super.toString() + ", genre='" + genre + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true; // Same reference
		if (!(o instanceof Movie))
			return false; // Type check
		if (!super.equals(o))
			return false; // Compare superclass fields

		Movie movie = (Movie) o;
		return Objects.equals(genre, movie.genre);
	}

	// Override hashCode()
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), genre);
	}
}
