package aditkarki.movieticketingservicenew.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import aditkarki.movieticketingservicenew.enums.BookingStatus;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "bookings", createIndex = false)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDocument {
    @Id
    @JsonProperty("bookingId")
    private String bookingId;

    @Field(type = FieldType.Long)
    @JsonProperty("userId")
    private Long userId;

    @Field(type = FieldType.Keyword)
    @JsonProperty("userEmail")
    private String userEmail;

    @Field(type = FieldType.Long)
    @JsonProperty("showtimeId")
    private Long showtimeId;

    @Field(type = FieldType.Text)
    @JsonProperty("movieTitle")
    private String movieTitle;

    @Field(type = FieldType.Text)
    @JsonProperty("theaterName")
    private String theaterName;

    @Field(type = FieldType.Integer)
    @JsonProperty("seatCount")
    private Integer seatCount;

    @Field(type = FieldType.Double)
    @JsonProperty("totalPrice")
    private Double totalPrice;

    @Field(type = FieldType.Date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("bookingTime")
    private LocalDateTime bookingTime;

    @Field(type = FieldType.Keyword)
    @JsonProperty("status")
    private BookingStatus status;
}
