package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.enums.CustomAggregation;
import aditkarki.movieticketingservicenew.enums.CustomOperator;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.MovieDocument;
import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.MovieRequest;
import aditkarki.movieticketingservicenew.dto.requests.MovieSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.MovieResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.exception.ReflectionAccessException;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.helper.SearchRequestHelper;
import aditkarki.movieticketingservicenew.helper.SortingHelper;
import aditkarki.movieticketingservicenew.manager.MovieManager;
import aditkarki.movieticketingservicenew.mapper.MovieMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService implements MovieServiceInterface{
    private final MovieManager movieManager;
    private final MovieMapper movieMapper;
    private final ElasticsearchClient elasticsearchClient;
    private final QueryFilterHelper queryFilterHelper;
    private final SearchRequestHelper searchRequestHelper;

    // Disconnected from the Postgres `movies` table (no MovieManager/MovieRepository involvement) and unsafe
    // (createMovieES's id is `count()+1`, which collides with existing ids once any movie has been deleted).
    // A movie created/updated/deleted here never exists as a real Movie entity, so it can't be scheduled via
    // ShowtimeService and drifts out of sync with the real data. Disabled until replaced with a MovieManager-backed path.
    /*
    public MovieResponse createMovieES(MovieRequest movieRequest) {
        MovieDocument movieDocument = movieMapper.toDocument(movieRequest);
        if(movieManager.existsByTitle(movieDocument.getTitle())) {
            throw new DuplicateResourceException(movieDocument.getTitle());
        }
        try{
            String newId = String.valueOf(elasticsearchClient.count(c -> c
                    .index(ElasticSearchConstants.MOVIES_INDEX)
            ).count() + 1);

            elasticsearchClient.index(i -> i
                    .index(ElasticSearchConstants.MOVIES_INDEX)
                    .id(newId)
                    .document(movieDocument));
        } catch (IOException e){
            log.error(e.getMessage());
        }

        return movieMapper.toResponse(movieDocument);
    }

    public MovieResponse updateMovieES(MovieRequest movieRequest, String title) {
        if(!movieManager.existsByTitle(movieRequest.getTitle())) {
            throw new ResourceNotFoundException(title);
        }
        // Searches for the movie by title then picks the hit whose title is an exact match because match is fuzzy
        SearchResponse<MovieDocument> searchResponse = searchMovieByTitle(movieRequest, title);
        Hit<MovieDocument> matchingHit = searchResponse.hits().hits().stream()
                .filter(hit -> hit.source() != null && title.equalsIgnoreCase(hit.source().getTitle()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(title));

        MovieDocument document = matchingHit.source();
        String id = matchingHit.id();
        movieMapper.updateDocumentFromRequest(movieRequest, document);

        try{
            elasticsearchClient.update(u -> u
                            .index(ElasticSearchConstants.MOVIES_INDEX)
                            .id(id)
                            .doc(document)
                    , MovieDocument.class);
        } catch (IOException e){
            log.error(e.getMessage());
            throw new ResourceNotFoundException(title);
        }

        return movieMapper.toResponse(document);

    }

    public void deleteMovieES(MovieRequest movieRequest) {
        MovieDocument movieDocument = movieMapper.toDocument(movieRequest);
        if(!movieManager.existsByTitle(movieDocument.getTitle())) {
            throw new ResourceNotFoundException(movieDocument.getTitle());
        }

        movieManager.deleteByTitle(movieDocument.getTitle());
    }
    */

    public MovieResponse createMovie(MovieRequest movieRequest) {
        if(movieManager.existsByTitle(movieRequest.getTitle())) {
            throw new DuplicateResourceException(movieRequest.getTitle());
        }
        Movie movie = movieMapper.toEntity(movieRequest);
        Movie savedMovie = movieManager.saveMovie(movie);
        return movieMapper.toResponse(savedMovie);
    }

    public MovieResponse getMovieById(Long movieId) {
        Movie movie = movieManager.findById(movieId);
        return movieMapper.toResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieManager.findAll().stream().map(movieMapper::toResponse).toList();
    }

    public MovieResponse updateMovie(Long movieId, MovieRequest movieRequest) {
        Movie existingMovie = movieManager.findById(movieId);
        if (movieRequest.getTitle() != null
                && !movieRequest.getTitle().equals(existingMovie.getTitle())
                && movieManager.existsByTitle(movieRequest.getTitle())) {
            throw new DuplicateResourceException(movieRequest.getTitle());
        }
        movieMapper.updateEntityFromRequest(movieRequest, existingMovie);
        movieManager.saveMovie(existingMovie);
        return movieMapper.toResponse(existingMovie);
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        movieManager.deleteMovie(movieId);
    }

    public TableResponse getMovie(MovieSearchRequest movieSearchRequest, int pageNumber, int size, String sortBy, CustomSorting customSorting) {

        // Query Build
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        movieQueryFiltering(movieSearchRequest, boolQuery);

        // Pagination
        Page pagination = PageUtils.pagination(pageNumber, size);

        // Sorting
        SortOrder sortOrder = SortingHelper.validSortOrder(customSorting);
        String sortingField = SortingHelper.validSortBy(sortBy);


        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.MOVIES_INDEX)
                .query(boolQuery.build()._toQuery())
                .from(pagination.getPageNumber() * pagination.getSize())
                .size(pagination.getSize())
                .sort(so -> so.field(f -> f.field(sortingField).order(sortOrder)))
                .trackTotalHits(t -> t.enabled(true)));



        log.info("Movie Search request {}", searchRequest);

        // Finish filtering & sorting, then paginate

        SearchResponse<MovieDocument> response;
        try {
            response = elasticsearchClient.search(searchRequest, MovieDocument.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new SearchException("Cannot search movie documents");
        }


        LinkedList<MovieResponse> data = response.hits().hits()
                .stream()
                .map(Hit::source)
                .map(movieMapper::toResponse)
                .collect(Collectors.toCollection(LinkedList::new));

        Page pageBuilder = PageUtils.pageBuilder(pagination.getPageNumber(), pagination.getSize(), response);

        return TableResponse.builder()
                .data(data)
                .page(pageBuilder)
                .build();
    }

    private void movieQueryFiltering(MovieSearchRequest movieSearchRequest, BoolQuery.Builder boolQuery) {
        for(Field field: movieSearchRequest.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Gives us access to private fields
            String fieldName = field.getName();

            Object fieldValue;
            try{
                fieldValue = field.get(movieSearchRequest);
            } catch(IllegalAccessException e){
                log.error(e.getMessage());
                throw new ReflectionAccessException("Cannot access field " + fieldName);
            }

            if(List.class.isAssignableFrom(field.getType())) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) fieldValue;
                queryFilterHelper.handleListField(boolQuery, fieldName, values);

            } else if(String.class.isAssignableFrom(field.getType())) {
                String value = (String) fieldValue;
                if(ElasticSearchConstants.MATCH_FIELDS.contains(fieldName)) {
                    queryFilterHelper.addMatchFilter(boolQuery, fieldName, value);
                } else{
                    queryFilterHelper.addTermsFilter(boolQuery, fieldName, value);
                }

            } else if (Boolean.class.isAssignableFrom(field.getType())) {
                Boolean value = (Boolean) fieldValue;
                queryFilterHelper.addTermsFilter(boolQuery, fieldName, value);

            } else if(RangeDto.class.isAssignableFrom(field.getType())) {
                RangeDto value = (RangeDto) fieldValue;
                if (value == null || value.getValue1() == null || value.getOperator() == null)
                    continue;

                if(value.getOperator() == CustomOperator.EQ) {
                    queryFilterHelper.addTermsFilter(boolQuery, fieldName, value);
                } else if (value instanceof DateRangeDto) {
                    queryFilterHelper.addDateRangeFilter(boolQuery, fieldName, value);
                } else if (value instanceof NumericRangeDto) {
                    queryFilterHelper.addRangeFilter(boolQuery, fieldName, value);
                }

            }
        }
    }

    // Only used by the disabled *ES methods above.
    /*
    private SearchResponse<MovieDocument> searchMovieByTitle(MovieRequest movieRequest, String title) {
        try{
            SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                    .index(ElasticSearchConstants.MOVIES_INDEX)
                    .query(q -> q
                            .match(m -> m
                                    .field("title")
                                    .query(title)
                            )), MovieDocument.class);
            return response;
        } catch (IOException e){
            throw new ResourceNotFoundException("Movie does not exist");
        }
    }
    */
    }






