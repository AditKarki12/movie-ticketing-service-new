package aditkarki.movieticketingservicenew.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(indexName = "showtimes", createIndex = false)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowtimeDocument {
    @Id
    @JsonProperty("showtimeId")
    private String showtimeId;

    @Field(type = FieldType.Text)
    @JsonProperty("movieTitle")
    private String movieTitle;

    @Field(type = FieldType.Text)
    @JsonProperty("theaterName")
    private String theaterName;

    @Field(type = FieldType.Text)
    @JsonProperty("screenName")
    private String screenName;

    @Field(type = FieldType.Date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("localDate")
    private LocalDate localDate;

    @Field(type = FieldType.Date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @JsonProperty("localTime")
    private LocalTime localTime;

    @Field(type = FieldType.Integer)
    @JsonProperty("totalSeats")
    private Integer totalSeats;

    @Field(type = FieldType.Integer)
    @JsonProperty("availableSeats")
    private Integer availableSeats;

    @Field(type = FieldType.Double)
    @JsonProperty("ticketPrice")
    private Double ticketPrice;
}
