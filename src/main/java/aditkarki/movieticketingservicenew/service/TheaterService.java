package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.enums.CustomOperator;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.TheaterDocument;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.TheaterRequest;
import aditkarki.movieticketingservicenew.dto.requests.TheaterSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.dto.responses.TheaterResponse;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.exception.BookingConflictException;
import aditkarki.movieticketingservicenew.exception.DuplicateResourceException;
import aditkarki.movieticketingservicenew.exception.ReflectionAccessException;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.helper.SortingHelper;
import aditkarki.movieticketingservicenew.manager.ShowtimeManager;
import aditkarki.movieticketingservicenew.manager.TheaterManager;
import aditkarki.movieticketingservicenew.mapper.TheaterMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheaterService {
    private final TheaterManager theaterManager;
    private final TheaterMapper theaterMapper;
    private final ShowtimeManager showtimeManager;
    private final QueryFilterHelper queryFilterHelper;
    private final ElasticsearchClient elasticsearchClient;

    public TheaterResponse createTheater(TheaterRequest theaterRequest) {
        if (theaterManager.existsByTheaterName(theaterRequest.getTheaterName())) {
            throw new DuplicateResourceException(theaterRequest.getTheaterName());
        }
        Theater theater = theaterMapper.toEntity(theaterRequest);
        Theater theaterSaved = theaterManager.saveTheater(theater);
        return theaterMapper.toResponse(theaterSaved);
    }

    public TheaterResponse getTheaterById(Long theaterId) {
        Theater theater = theaterManager.findTheaterById(theaterId);
        return theaterMapper.toResponse(theater);
    }

    public List<TheaterResponse> getAllTheaters() {
        return theaterManager.findAllTheaters().stream().map(theaterMapper::toResponse).toList();
    }

    public TheaterResponse updateTheater(Long theaterId, TheaterRequest theaterRequest) {
        Theater existingTheater = theaterManager.findTheaterById(theaterId);
        if (theaterRequest.getTheaterName() != null
                && !theaterRequest.getTheaterName().equals(existingTheater.getTheaterName())
                && theaterManager.existsByTheaterName(theaterRequest.getTheaterName())) {
            throw new DuplicateResourceException(theaterRequest.getTheaterName());
        }
        theaterMapper.updateEntityFromRequest(theaterRequest, existingTheater);
        Theater updatedTheater = theaterManager.saveTheater(existingTheater);
        return theaterMapper.toResponse(updatedTheater);
    }

    public void deleteTheater(Long theaterId) {
        if (showtimeManager.existsByTheaterId(theaterId)) {
            throw new BookingConflictException("Cannot delete theater with existing showtimes");
        }
        theaterManager.deleteTheater(theaterId);
    }

    public TableResponse getTheater(TheaterSearchRequest theaterSearchRequest, int pageNumber, int size, String sortBy, CustomSorting customSorting) {

        // Query Builder
        BoolQuery.Builder boolquery = new BoolQuery.Builder();
        theaterQueryFiltering(theaterSearchRequest, boolquery);

        // Pagination
        Page pagination = PageUtils.pagination(pageNumber, size);

        // Sort
        SortOrder sortOrder = SortingHelper.validSortOrder(customSorting);
        String sortingField = SortingHelper.validSortBy(sortBy);

        // Request Builder
        SearchRequest request = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.THEATERS_INDEX)
                .query(boolquery.build()._toQuery())
                .from(pagination.getPageNumber() * pagination.getSize())
                .size(pagination.getSize())
                .sort(so -> so.field(f -> f.field(sortingField).order(sortOrder)))
                .trackTotalHits(t -> t.enabled(true)));

        log.info("Theater Search request: {}", request);

        SearchResponse<TheaterDocument> response;
        try{
            response = elasticsearchClient.search(request, TheaterDocument.class);
        } catch (IOException e){
            log.error(e.getMessage());
            throw new SearchException("Cannot search theater documents");
        }

        LinkedList<TheaterResponse> data = response.hits().hits()
                .stream()
                .map(Hit::source)
                .map(theaterMapper::toResponse)
                .collect(Collectors.toCollection(LinkedList::new));

        Page pageBuilder = PageUtils.pageBuilder(pagination.getPageNumber(), pagination.getSize(), response);

        return TableResponse.builder()
                .data(data)
                .page(pageBuilder)
                .build();
    }

    private void theaterQueryFiltering(TheaterSearchRequest theaterSearchRequest, BoolQuery.Builder boolquery) {
        for(Field field : theaterSearchRequest.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();

            Object fieldValue;
            try{
                fieldValue = field.get(theaterSearchRequest);
            } catch (IllegalAccessException e){
                log.error(e.getMessage());
                throw new ReflectionAccessException(e.getMessage());
            }

            if(String.class.equals(field.getType())) {
                String value = (String) fieldValue;
                if(value == null || value.isEmpty()){
                    continue;
                }
                if(fieldName.equals("theaterCity") || fieldName.equals("theaterState")) {
                    queryFilterHelper.addTermsFilter(boolquery, fieldName, value);
                } else{
                    queryFilterHelper.addMatchFilter(boolquery, fieldName, value);
                }
            } else if(RangeDto.class.isAssignableFrom(field.getType())) {
                RangeDto value = (RangeDto) fieldValue;
                if(value == null || value.getValue1() == null || value.getOperator() == null){
                    continue;
                }
                if(value.getOperator() == CustomOperator.EQ){
                    queryFilterHelper.addTermsFilter(boolquery, fieldName, value);
                } else if(value.getClass() == NumericRangeDto.class){
                    queryFilterHelper.addRangeFilter(boolquery, fieldName, value);
                }
            }
        }
    }

}
