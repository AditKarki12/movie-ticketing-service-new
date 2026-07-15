package aditkarki.movieticketingservicenew.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Page {

    // This says what page we lookin at
    private Integer pageNumber;

    // Says total amount matching documents before we paginate our data
    private Long totalElements;

    // Gives us the amount of elements per page
    private Integer size;

    // Total amount of existing pages
    private Integer totalPages;

}
