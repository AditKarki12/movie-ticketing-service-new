package aditkarki.movieticketingservicenew.mapper;

import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "heading", source = "title")
    MovieResponse toResponse(Movie movie);

    @Mapping(target = "heading", source = "title")
    MovieResponse toResponse(MovieDocument movieDocument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    Movie toEntity(MovieRequest request);

    MovieDocument toDocument(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    void updateEntityFromRequest(MovieRequest request, @MappingTarget Movie movie);
}