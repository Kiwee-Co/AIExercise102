package exercise.coding.kiwee.ai.shared.art;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Artform {

   @JsonProperty("id")
   private Long id;

   @JsonProperty("title")
   private String title;

   @JsonProperty("artist")
   private String artist;

   @JsonProperty("release_date")
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate releaseDate;

   // Default constructor
   public Artform() {}

   // Parameterized constructor
   public Artform(Long id, String title, String artist, LocalDate releaseDate) {
       this.id = id;
       this.title = title;
       this.artist = artist;
       this.releaseDate = releaseDate;
   }

   // Getters and Setters
   public Long getId() {
       return id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public String getTitle() {
       return title;
   }

   public void setTitle(String title) {
       this.title = title;
   }

   public String getArtist() {
       return artist;
   }

   public void setArtist(String artist) {
       this.artist = artist;
   }

   public LocalDate getReleaseDate() {
       return releaseDate;
   }

   public void setReleaseDate(LocalDate releaseDate) {
       this.releaseDate = releaseDate;
   }

  @Override
  public String toString() {
      return "Artform{" +
              "id=" + id +
              ", title='" + title + '\'' +
              ", artist='" + artist + '\'' +
              ", release_date=" + releaseDate +
              '}';
  }
}
