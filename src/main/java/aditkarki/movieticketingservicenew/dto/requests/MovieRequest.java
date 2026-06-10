package aditkarki.movieticketingservicenew.dto.requests;

import lombok.Data;

@Data
public class MovieRequest {
    private String title;
    private String genre;
    private String language;
    private Integer duration;
    private String description;
    private String director;
}
