package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.responses.LanguageResponse;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    default LanguageResponse toLanguageResponse(StringTermsBucket bucket) {
        LanguageResponse response = new LanguageResponse();
        response.setLanguage(List.of(bucket.key().stringValue()));
        response.setMovieCount(bucket.docCount());
        response.setAverageMovieRating(round(bucket.aggregations().get("averageRating").avg().value()));
        response.setAverageMovieDuration(round(bucket.aggregations().get("averageDuration").avg().value()));
        return response;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}