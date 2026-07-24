package aditkarki.movieticketingservicenew.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "theaters", createIndex = false)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheaterDocument {
    @Id
    @JsonProperty("theaterId")
    private String theaterId;

    @Field(type = FieldType.Text)
    @JsonProperty("theaterName")
    private String theaterName;

    @Field(type = FieldType.Text)
    @JsonProperty("theaterAddress")
    private String theaterAddress;

    @Field(type = FieldType.Keyword)
    @JsonProperty("theaterCity")
    private String theaterCity;

    @Field(type = FieldType.Keyword)
    @JsonProperty("theaterState")
    private String theaterState;

    @Field(type = FieldType.Integer)
    @JsonProperty("screensTotal")
    private Integer screensTotal;
}
