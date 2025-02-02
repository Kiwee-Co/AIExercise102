package exercise.coding.kiwee.ai.shared.person;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Include only non-null fields during serialization
public class Celebrity {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("age")
	private Integer age;

	@JsonProperty("gender")
	private String gender;

	// Default constructor
	public Celebrity() {
	}

	// Parameterized constructor
	public Celebrity(Long id, String name, Integer age, String gender) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	// Override equals()
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true; // Same reference
		if (o == null || getClass() != o.getClass())
			return false; // Type check
		Celebrity celebrity = (Celebrity) o;
		return Objects.equals(id, celebrity.id) && Objects.equals(name, celebrity.name)
				&& Objects.equals(age, celebrity.age) && Objects.equals(gender, celebrity.gender);
	}

	// Override hashCode()
	@Override
	public int hashCode() {
		return Objects.hash(id, name, age, gender);
	}

	@Override
	public String toString() {
		return "Celebrity{" + "id=" + id + ", name='" + name + '\'' + ", age=" + age + ", gender='" + gender + '\''
				+ '}';
	}
}
