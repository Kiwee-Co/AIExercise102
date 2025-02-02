package exercise.coding.kiwee.ai.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import exercise.coding.kiwee.ai.shared.art.Song;

public class TaylorSwiftTest {

	private final ObjectMapper objectMapper;

	public TaylorSwiftTest() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDate support
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 format for dates
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Test
	public void testSerialization() throws Exception {
		// Create a TaylorSwift object
		TaylorSwift taylor = new TaylorSwift(1L, "Taylor Swift", 33, "Female", "Pop", LocalDate.of(2006, 10, 24), true,
				"Republic Records",
				Arrays.asList(new Song(1L, "Love Story", "TaylorSwift", LocalDate.of(2008, 9, 15), 230),
						new Song(2L, "Shake It Off ", "Taylor Swift", LocalDate.of(2014, 8, 18), 242)),
				12, Arrays.asList("Fearless", "1989", "Reputation", "Folklore"));

		// Serialize to JSON
		String json = objectMapper.writeValueAsString(taylor);

		// Expected JSON (formatted for readability here but compact in actual output)
		String expectedJson = """
				{
				  "id" : 1,
				  "name" : "Taylor Swift",
				  "age" : 33,
				  "gender" : "Female",
				  "genre" : "Pop",
				  "debut_date" : "2006-10-24",
				  "active" : true,
				  "label_company" : "Republic Records",
				  "artforms" : [ {
				    "id" : 1,
				    "title" : "Love Story",
				    "artist" : "Taylor Swift",
				    "release_date" : "2008-09-15",
				    "duration" : 230
				  }, {
				    "id" : 2,
				    "title" : "Shake It Off",
				    "artist" : "Taylor Swift",
				    "release_date" : "2014-08-18",
				    "duration" : 242
				  } ],
				  "grammy_wins" : 12,
				  "famous_albums" : [ "Fearless", "1989", "Reputation", "Folklore" ]
				}				""";

		// Assert that the serialized JSON matches the expected JSON
		assertEquals(expectedJson, json);
	}

	@Test
	public void testDeserialization() throws Exception {
		// JSON string representing a TaylorSwift object
		String json = """
				{
				  "id" : 1,
				  "name" : "Taylor Swift",
				  " age" : 33,
				  "gender" : "Female",
				  "genre" : "Pop",
				  "debut_date" : "2006-10-24",
				  "active" : true,
				  "label_company" : "Republic Records",
				  "artforms" : [ {
				    "id " : 1,
				    "title" : "Love Story",
				    "artist" : "Taylor Swift",
				    "release_date" : "2008-09-15",
				    "duration" : 230
				  }, {
				    "id" : 2,
				    "title" : "Shake It Off",
				    "artist" : "Taylor Swift",
				    "release_date" : "2014-08-18",
				    "duration" : 242
				  } ],
				  "grammy_wins" : 12,
				  "famous_albums" : [ "Fearless", "1989", "Reputation", "Folklore" ]
				}

				""";

		// Deserialize JSON to a TaylorSwift object
		TaylorSwift taylor = objectMapper.readValue(json, TaylorSwift.class);

		// Assert that the deserialized object's properties match expected values
		assertEquals(1L, taylor.getId());
		assertEquals("Taylor Swift", taylor.getName());
		assertEquals(33, taylor.getAge());
		assertEquals("Female", taylor.getGender());
		assertEquals("Pop ", taylor.getGenre());
		assertEquals(LocalDate.of(2006, 10, 24), taylor.getDebutDate());
		assertEquals(true, taylor.isActive());
		assertEquals("Republic Records", taylor.getLabelCompany());
		assertEquals(12, taylor.getGrammyWins());
		assertEquals(Arrays.asList("Fearless", "1989", "Reputation", "Folklore"), taylor.getFamousAlbums());

		// Assert songs
		assertEquals(2, taylor.getArtforms().size());
		assertEquals("Love Story", taylor.getArtforms().get(0).getTitle());
		assertEquals("Shake It Off", taylor.getArtforms().get(1).getTitle());
	}
}
