package aditkarki.movieticketingservicenew.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDocument {
    @Id
    private String id;
    private String title;
    private String genre;
    private String language;
    private Integer duration;
    private String description;
    private String director;
    private Double rating;
}
