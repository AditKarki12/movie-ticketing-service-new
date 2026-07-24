package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.responses.TheaterStateResponse;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TheaterStateMapper {

    default TheaterStateResponse toTheaterStateResponse(StringTermsBucket bucket) {
        TheaterStateResponse response = new TheaterStateResponse();
        response.setTheaterState(List.of(bucket.key().stringValue()));
        response.setTheaterCount(bucket.docCount());
        response.setAverageScreensTotal(round(bucket.aggregations().get("averageScreensTotal").avg().value()));
        return response;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}