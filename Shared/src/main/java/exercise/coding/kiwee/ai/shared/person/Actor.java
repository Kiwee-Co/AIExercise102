package exercise.coding.kiwee.ai.shared.person;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Actor extends Celebrity {

	@JsonProperty("movies_count")
	private Integer moviesCount;

	@JsonProperty("debut_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate debutDate;

	// Default constructor
	public Actor() {
	}

	// Parameterized constructor
	public Actor(Long id, String name, Integer age, String gender, Integer moviesCount, LocalDate debutDate) {
		super(id, name, age, gender);
		this.moviesCount = moviesCount;
		this.debutDate = debutDate;
	}

	// Getters and Setters
	public Integer getMoviesCount() {
		return moviesCount;
	}

	public void setMoviesCount(Integer moviesCount) {
		this.moviesCount = moviesCount;
	}

	public LocalDate getDebutDate() {
		return debutDate;
	}

	public void setDebutDate(LocalDate debutDate) {
		this.debutDate = debutDate;
	}

	@Override
	public String toString() {
		return "Actor{" + super.toString() + ", moviesCount=" + moviesCount + ", debutDate=" + debutDate + '}';
	}

	// Override equals()
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true; // Same reference
		if (!(o instanceof Actor))
			return false; // Type check
		if (!super.equals(o))
			return false; // Compare superclass fields

		Actor actor = (Actor) o;
		return Objects.equals(moviesCount, actor.moviesCount) && Objects.equals(debutDate, actor.debutDate);
	}

	// Override hashCode()
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), moviesCount, debutDate);
	}

}
