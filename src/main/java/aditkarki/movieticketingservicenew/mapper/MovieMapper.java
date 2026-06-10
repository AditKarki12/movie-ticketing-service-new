package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.UserRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResponse toResponse(Movie movie);

    MovieResponse toResponse(MovieDocument movieDocument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    Movie toEntity(MovieRequest request);

    @Mapping(target = "id", ignore = true)
    MovieDocument toDocument(MovieRequest request);

    MovieDocument toDocument(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    void updateEntityFromRequest(MovieRequest request, @MappingTarget Movie movie);
}