package exercise.coding.kiwee.ai.shared.art;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Song extends Artform {

	@JsonProperty("duration")
	private int duration; // Duration in seconds

	// Default constructor
	public Song() {
	}

	// Parameterized constructor
	public Song(Long id, String title, String artist, LocalDate releaseDate, int duration) {
		super(id, title, artist, releaseDate);
		this.duration = duration;
	}

	// Getters and Setters
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Song{" + super.toString() + ", duration=" + duration + '}';
	}

	// Override equals()
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true; // Same reference
		if (!(o instanceof Song))
			return false; // Type check
		if (!super.equals(o))
			return false; // Compare superclass fields

		Song song = (Song) o;
		return duration == song.duration;
	}

	// Override hashCode()
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), duration);
	}
}
