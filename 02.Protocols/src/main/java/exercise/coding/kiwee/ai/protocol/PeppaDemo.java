package exercise.coding.kiwee.ai.protocol;

import java.time.LocalDate;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import exercise.coding.kiwee.ai.shared.art.Song;
import exercise.coding.kiwee.ai.shared.person.Singer;

public class PeppaDemo {
	public static void main(String[] args) throws Exception {
		// Configure ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDate support
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 format for dates

		// Peppa's Songs
		Song bingBongSong = new Song(1L, "Bing Bong Song", "Peppa Pig", LocalDate.of(2010, 5, 1), 120);
		Song rainbowSong = new Song(2L, "Rainbow Song", "Peppa Pig", LocalDate.of(2011, 6, 15), 150);
		Song muddyPuddlesSong = new Song(3L, "Muddy Puddles Song", "Peppa Pig", LocalDate.of(2012, 7, 20), 180);

		// Create Peppa as a Singer
		Singer peppa = new Singer(1L, "Peppa Pig", 4, "Female", "Children", LocalDate.of(2010, 1, 1), true,
				"Peppa Records", Arrays.asList(bingBongSong, rainbowSong, muddyPuddlesSong));

		// Serialize Peppa to JSON
		String peppaJson = objectMapper.writeValueAsString(peppa);
		System.out.println("Peppa JSON:");
		System.out.println(peppaJson);

		// Deserialize JSON back to Singer object
		Singer peppaFromJson = objectMapper.readValue(peppaJson, Singer.class);
		System.out.println("\nPeppa From JSON:");
		System.out.println(peppaFromJson);

		// George's Songs
		Song dinosaurSong = new Song(4L, "Dinosaur", "George", LocalDate.of(2015, 3, 10), 90);
		Song jumpingInMuddyPuddles = new Song(5L, "Jumping in Muddy Puddles", "George", LocalDate.of(2016, 8, 12), 100);

		// Create George as a Singer
		Singer george = new Singer(2L, "George", 2, "Male", "Children", LocalDate.of(2015, 1, 1), true, "George Tunes",
				Arrays.asList(dinosaurSong, jumpingInMuddyPuddles));

		// Serialize George to JSON
		String georgeJson = objectMapper.writeValueAsString(george);
		System.out.println("\nGeorge JSON:");
		System.out.println(georgeJson);

		// Deserialize JSON back to Singer object
		Singer georgeFromJson = objectMapper.readValue(georgeJson, Singer.class);
		System.out.println("\nGeorge From JSON:");
		System.out.println(georgeFromJson);
	}
}
