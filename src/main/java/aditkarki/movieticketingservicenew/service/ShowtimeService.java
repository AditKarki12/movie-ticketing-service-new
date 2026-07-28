package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.enums.CustomOperator;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.ShowtimeDocument;
import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.ShowtimeRequest;
import aditkarki.movieticketingservicenew.dto.requests.ShowtimeSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.ShowtimeResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.entity.Movie;
import aditkarki.movieticketingservicenew.entity.Screen;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.ShowtimeSeat;
import aditkarki.movieticketingservicenew.entity.Theater;
import aditkarki.movieticketingservicenew.enums.ShowtimeSeatStatus;
import aditkarki.movieticketingservicenew.exception.BookingConflictException;
import aditkarki.movieticketingservicenew.exception.InvalidRequestException;
import aditkarki.movieticketingservicenew.exception.ReflectionAccessException;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.helper.SortingHelper;
import aditkarki.movieticketingservicenew.manager.BookingManager;
import aditkarki.movieticketingservicenew.manager.MovieManager;
import aditkarki.movieticketingservicenew.manager.ScreenManager;
import aditkarki.movieticketingservicenew.manager.ShowtimeManager;
import aditkarki.movieticketingservicenew.manager.ShowtimeSeatManager;
import aditkarki.movieticketingservicenew.manager.TheaterManager;
import aditkarki.movieticketingservicenew.mapper.ShowtimeMapper;
import aditkarki.movieticketingservicenew.repository.SeatRepository;
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
public class ShowtimeService {
    private final ShowtimeManager showtimeManager;
    private final ShowtimeMapper showtimeMapper;
    private final TheaterManager theaterManager;
    private final MovieManager movieManager;
    private final BookingManager bookingManager;
    private final ScreenManager screenManager;
    private final ShowtimeSeatManager showtimeSeatManager;
    private final SeatRepository seatRepository;
    private final QueryFilterHelper queryFilterHelper;
    private final ElasticsearchClient elasticsearchClient;

    public ShowtimeResponse createShowtime(ShowtimeRequest showtimeRequest) {
        if(showtimeRequest.getTotalSeats() == null || showtimeRequest.getTotalSeats() <= 0){
            throw new InvalidRequestException("Total seats must be greater than 0");
        }
        if(showtimeRequest.getAvailableSeats() == null){
            showtimeRequest.setAvailableSeats(showtimeRequest.getTotalSeats());
        } else if(showtimeRequest.getAvailableSeats() < 0 || showtimeRequest.getAvailableSeats() > showtimeRequest.getTotalSeats()){
            throw new InvalidRequestException("Available seats must be between 0 and total seats");
        }
        if(showtimeRequest.getTicketPrice() == null || showtimeRequest.getTicketPrice().signum() < 0){
            throw new InvalidRequestException("Ticket price must not be negative");
        }
        Movie movie = movieManager.findById(showtimeRequest.getMovieId());
        Theater theater = theaterManager.findTheaterById(showtimeRequest.getTheaterId());
        Screen screen = showtimeRequest.getScreenId() != null
                ? screenManager.findById(showtimeRequest.getScreenId())
                : null;
        Showtime showtime = showtimeMapper.toEntity(showtimeRequest);
        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setScreen(screen);

        Showtime savedShowtime = showtimeManager.saveshowtime(showtime);

        if (screen != null) {
            generateShowtimeSeats(savedShowtime, screen);
        }

        return showtimeMapper.toResponse(savedShowtime);
    }

    private void generateShowtimeSeats(Showtime showtime, Screen screen) {
        List<ShowtimeSeat> showtimeSeats = seatRepository.findByScreen(screen).stream()
                .map(seat -> {
                    ShowtimeSeat showtimeSeat = new ShowtimeSeat();
                    showtimeSeat.setShowtime(showtime);
                    showtimeSeat.setSeat(seat);
                    showtimeSeat.setStatus(ShowtimeSeatStatus.AVAILABLE);
                    return showtimeSeat;
                })
                .collect(Collectors.toList());
        showtimeSeatManager.saveAll(showtimeSeats);
    }

    public ShowtimeResponse getShowtimeById(Long id){
        Showtime showtime = showtimeManager.findShowtimeById(id);
        return showtimeMapper.toResponse(showtime);
    }

    public List<ShowtimeResponse> getAllShowtimes(){
        return showtimeManager.findAllshowtimes().stream().map(showtimeMapper::toResponse).toList();
    }

    @Transactional
    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest showtimeRequest){
        Showtime existingShowtime = showtimeManager.findShowtimeById(id);
        showtimeMapper.updateEntityFromRequest(showtimeRequest, existingShowtime);
        if (showtimeRequest.getMovieId() != null) {
            existingShowtime.setMovie(movieManager.findById(showtimeRequest.getMovieId()));
        }
        if (showtimeRequest.getTheaterId() != null) {
            existingShowtime.setTheater(theaterManager.findTheaterById(showtimeRequest.getTheaterId()));
        }
        if (showtimeRequest.getScreenId() != null) {
            Screen newScreen = screenManager.findById(showtimeRequest.getScreenId());
            Screen currentScreen = existingShowtime.getScreen();
            if (currentScreen == null || !currentScreen.getId().equals(newScreen.getId())) {
                if (bookingManager.hasActiveBookingsForShowtime(id)) {
                    throw new BookingConflictException("Cannot change screen for a showtime with active bookings");
                }
                showtimeSeatManager.deleteByShowtimeId(id);
                existingShowtime.setScreen(newScreen);
                generateShowtimeSeats(existingShowtime, newScreen);
            }
        }
        if (existingShowtime.getTotalSeats() == null || existingShowtime.getTotalSeats() <= 0 || existingShowtime.getAvailableSeats() == null || existingShowtime.getAvailableSeats() < 0 || existingShowtime.getAvailableSeats() > existingShowtime.getTotalSeats()) {
            throw new InvalidRequestException("Seats not available");
        }
        if (existingShowtime.getTicketPrice() == null || existingShowtime.getTicketPrice().signum() < 0) {
            throw new InvalidRequestException("Ticket price must not be negative");
        }
        Showtime updatedShowtime = showtimeManager.saveshowtime(existingShowtime);
        return showtimeMapper.toResponse(updatedShowtime);
    }

    public void deleteShowtime(Long id){
        if(bookingManager.hasActiveBookingsForShowtime(id)){
            throw new BookingConflictException("Cannot delete showtime with active bookings");
        }
        showtimeManager.deleteshowtime(id);
    }

    public TableResponse getShowtime(ShowtimeSearchRequest showtimeSearchRequest, int pageNumber, int size, String sortBy, CustomSorting customSorting){

        // Query Builder
        BoolQuery.Builder boolquery = new BoolQuery.Builder();
        showtimeQueryFiltering(showtimeSearchRequest, boolquery);

        // Pagination
        Page pagination = PageUtils.pagination(pageNumber, size);

        // Sorting
        SortOrder sortOrder = SortingHelper.validSortOrder(customSorting);
        String sortingField = SortingHelper.validSortBy(sortBy);

        SearchRequest request = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.SHOWTIMES_INDEX)
                .query(boolquery.build()._toQuery())
                .from(pagination.getPageNumber() * pagination.getSize())
                .size(pagination.getSize())
                .sort(so -> so.field(f -> f.field(sortingField).order(sortOrder)))
                .trackTotalHits(t -> t.enabled(true)));

        log.info("Showtime Search request: {}", request);

        SearchResponse<ShowtimeDocument> response;
        try{
            response = elasticsearchClient.search(request, ShowtimeDocument.class);
        } catch (IOException e){
            log.error(e.getMessage());
            throw new SearchException("Cannot search showtime documents");
        }

        LinkedList<ShowtimeResponse> data = response.hits().hits()
                .stream()
                .map(Hit::source)
                .map(showtimeMapper::toResponse)
                .collect(Collectors.toCollection(LinkedList::new));

        Page pageBuilder = PageUtils.pageBuilder(pagination.getPageNumber(), pagination.getSize(), response);

        return TableResponse.builder()
                .data(data)
                .page(pageBuilder)
                .build();
    }

    private void showtimeQueryFiltering(ShowtimeSearchRequest showtimeSearchRequest, BoolQuery.Builder boolquery) {
        for(Field field : showtimeSearchRequest.getClass().getDeclaredFields()){
            field.setAccessible(true);
            String fieldName = field.getName();

            Object fieldValue;
            try{
                fieldValue = field.get(showtimeSearchRequest);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new ReflectionAccessException(e.getMessage());
            }

            if(RangeDto.class.isAssignableFrom(field.getType())){
                RangeDto value = (RangeDto) fieldValue;
                if(value == null || value.getValue1() == null || value.getOperator() == null){
                    continue;
                }
                if(value.getOperator() == CustomOperator.EQ){
                    queryFilterHelper.addTermsFilter(boolquery, fieldName, value);
                } else if(value.getClass() == NumericRangeDto.class){
                    queryFilterHelper.addRangeFilter(boolquery, fieldName, value);
                } else if(value.getClass() == DateRangeDto.class){
                    queryFilterHelper.addDateRangeFilter(boolquery, fieldName, value);
                }
            } else if(String.class.isAssignableFrom(field.getType())){
                String value = (String) fieldValue;
                if(value == null || value.isEmpty()){
                    continue;
                }
                if(fieldName.equals("movieTitle")){
                    if(!movieManager.existsByTitle(value)){
                        throw new ResourceNotFoundException("Movie not available");
                    }
                    queryFilterHelper.addMatchFilter(boolquery, fieldName, value);
                }
                if(fieldName.equals("theaterName")){
                    if(!theaterManager.existsByTheaterName(value)){
                        throw new ResourceNotFoundException("Theater not available");
                    }
                    queryFilterHelper.addMatchFilter(boolquery, fieldName, value);
                }
            }
        }
    }
}
