package aditkarki.movieticketingservicenew.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "movies", createIndex = false)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDocument {
    @Id
    @JsonProperty("id")
    private String id;

    @Field(type = FieldType.Text)
    @JsonProperty("title")
    private String title;

    @Field(type = FieldType.Keyword)
    @JsonProperty("genres")
    private String genres;

    @Field(type = FieldType.Keyword)
    @JsonProperty("language")
    private String language;

    @Field(type = FieldType.Integer)
    @JsonProperty("duration")
    private Integer duration;

    @Field(type = FieldType.Text)
    @JsonProperty("description")
    private String description;

    @Field(type = FieldType.Keyword)
    @JsonProperty("director")
    private String director;

    @Field(type = FieldType.Double)
    @JsonProperty("rating")
    private Double rating;

    @Field(type = FieldType.Date)
    @JsonProperty("releaseDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Field(type = FieldType.Boolean)
    @JsonProperty("isActive")
    private Boolean isActive;

    @Field(type = FieldType.Keyword)
    @JsonProperty("tags")
    private String tags;
}
