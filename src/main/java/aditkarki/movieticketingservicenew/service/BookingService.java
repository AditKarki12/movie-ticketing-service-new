package aditkarki.movieticketingservicenew.service;

import aditkarki.movieticketingservicenew.enums.BookingStatus;
import aditkarki.movieticketingservicenew.enums.CustomAggregation;
import aditkarki.movieticketingservicenew.enums.CustomOperator;
import aditkarki.movieticketingservicenew.enums.CustomSorting;
import aditkarki.movieticketingservicenew.constants.ElasticSearchConstants;
import aditkarki.movieticketingservicenew.document.BookingDocument;
import aditkarki.movieticketingservicenew.dto.DateRangeDto;
import aditkarki.movieticketingservicenew.dto.NumericRangeDto;
import aditkarki.movieticketingservicenew.dto.Page;
import aditkarki.movieticketingservicenew.dto.RangeDto;
import aditkarki.movieticketingservicenew.dto.requests.BookingRequest;
import aditkarki.movieticketingservicenew.dto.requests.BookingSearchRequest;
import aditkarki.movieticketingservicenew.dto.responses.BookingResponse;
import aditkarki.movieticketingservicenew.dto.responses.TableResponse;
import aditkarki.movieticketingservicenew.entity.Booking;
import aditkarki.movieticketingservicenew.entity.Showtime;
import aditkarki.movieticketingservicenew.entity.User;
import aditkarki.movieticketingservicenew.exception.BookingConflictException;
import aditkarki.movieticketingservicenew.exception.InvalidRequestException;
import aditkarki.movieticketingservicenew.exception.ReflectionAccessException;
import aditkarki.movieticketingservicenew.exception.ResourceNotFoundException;
import aditkarki.movieticketingservicenew.exception.SearchException;
import aditkarki.movieticketingservicenew.helper.PageUtils;
import aditkarki.movieticketingservicenew.helper.QueryFilterHelper;
import aditkarki.movieticketingservicenew.helper.SortingHelper;
import aditkarki.movieticketingservicenew.manager.BookingManager;
import aditkarki.movieticketingservicenew.manager.ShowtimeManager;
import aditkarki.movieticketingservicenew.manager.UserManager;
import aditkarki.movieticketingservicenew.mapper.BookingMapper;
import aditkarki.movieticketingservicenew.repository.ShowtimeRepository;
import aditkarki.movieticketingservicenew.repository.UserRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingManager bookingManager;
    private final BookingMapper bookingMapper;
    private final UserManager userManager;
    private final ShowtimeManager showtimeManager;
    private final QueryFilterHelper queryFilterHelper;
    private final ElasticsearchClient elasticsearchClient;

    public BookingResponse createBooking(BookingRequest bookingRequest, Authentication authentication) {
        if(bookingRequest.getSeatCount() == null || bookingRequest.getSeatCount() <= 0){
            throw new InvalidRequestException("Seat count must be greater than 0");
        }

        if(showtimeManager.reserveSeats(bookingRequest.getShowtimeId(), bookingRequest.getSeatCount()) <= 0){
            throw new BookingConflictException("Not enough available seats for this showtime");
        }
        try {
            User user = resolveBookingUser(bookingRequest.getUserId(), authentication);
            Showtime showtime = showtimeManager.findShowtimeById(bookingRequest.getShowtimeId());
            Booking booking = bookingMapper.toEntity(bookingRequest);
            booking.setUser(user);
            booking.setShowtime(showtime);
            booking.setTotalPrice(BigDecimal.valueOf(bookingRequest.getSeatCount()).multiply(showtimeManager.findTicketPrice(bookingRequest.getShowtimeId())));
            booking.setBookingTime(LocalDateTime.now());
            booking.setStatus(BookingStatus.CONFIRMED);
            Booking bookingSaved = bookingManager.saveBooking(booking);
            return bookingMapper.toResponse(bookingSaved);
        } catch (RuntimeException e) {
            showtimeManager.releaseSeats(bookingRequest.getShowtimeId(), bookingRequest.getSeatCount());
            log.error(e.getMessage());
            throw e;
        }
    }

    public BookingResponse getBookingById(Long bookingId, Authentication authentication) {
        Booking booking = bookingManager.findBookingById(bookingId);
        assertOwnerOrAdmin(booking, authentication);
        return bookingMapper.toResponse(booking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingManager.findAllBookings().stream().map(bookingMapper::toResponse).toList();
    }

    public BookingResponse updateBooking(Long bookingId, BookingRequest bookingRequest, Authentication authentication) {
        Booking existingBooking = bookingManager.findBookingById(bookingId);
        assertOwnerOrAdmin(existingBooking, authentication);
        if(bookingRequest.getSeatCount() != null && !Objects.equals(bookingRequest.getSeatCount(), existingBooking.getSeatCount())){
            updateBookingSeatCount(existingBooking, bookingRequest);
        }
        bookingMapper.updateEntityFromRequest(bookingRequest, existingBooking);
        Booking updatedBooking = bookingManager.saveBooking(existingBooking);
        return bookingMapper.toResponse(updatedBooking);
    }

    public BookingResponse cancelBooking(Long bookingId, Authentication authentication) {
        Booking booking = bookingManager.findBookingById(bookingId);
        assertOwnerOrAdmin(booking, authentication);
        if(booking.getStatus() == BookingStatus.CANCELLED){
            log.info("Booking is already cancelled");
            return bookingMapper.toResponse(booking);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        showtimeManager.releaseSeats(booking.getShowtime().getShowtimeId(), booking.getSeatCount());
        bookingManager.saveBooking(booking);
        return bookingMapper.toResponse(booking);
    }

    public void deleteBooking(Long bookingId, Authentication authentication) {
        Booking booking = bookingManager.findBookingById(bookingId);
        assertOwnerOrAdmin(booking, authentication);
        if(booking.getStatus() != BookingStatus.CANCELLED){
            showtimeManager.releaseSeats(booking.getShowtime().getShowtimeId(), booking.getSeatCount());
        }
        bookingManager.deleteBooking(bookingId);
    }

    private User resolveBookingUser(Long requestedUserId, Authentication authentication) {
        if (requestedUserId != null && isAdmin(authentication)) {
            return userManager.findUserById(requestedUserId);
        }
        return userManager.findUserByEmail(authentication.getName());
    }

    private void assertOwnerOrAdmin(Booking booking, Authentication authentication) {
        if (isAdmin(authentication)) {
            return;
        }
        if (!booking.getUser().getUserEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("You do not have permission to access this booking");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    public TableResponse getBooking(BookingSearchRequest bookingSearchRequest, int pageNumber, int size, String sortBy, CustomSorting customSorting) {
        // Query Builder
        BoolQuery.Builder boolQuery =  new BoolQuery.Builder();
        bookingQueryFiltering(bookingSearchRequest, boolQuery);

        // Pagination
        Page pagination = PageUtils.pagination(pageNumber, size);

        // Sorting
        SortOrder sortOrder = SortingHelper.validSortOrder(customSorting);
        String sortingField = SortingHelper.validSortBy(sortBy);

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(ElasticSearchConstants.BOOKINGS_INDEX)
                .query(boolQuery.build()._toQuery())
                .from(pagination.getPageNumber() * pagination.getSize())
                .size(pagination.getSize())
                .sort(so -> so.field(f -> f.field(sortingField).order(sortOrder)))
                .trackTotalHits(t -> t.enabled(true)));

        log.info("Booking Search request {}", searchRequest);

        SearchResponse<BookingDocument> response;
        try{
            response = elasticsearchClient.search(searchRequest, BookingDocument.class);
        } catch (IOException e){
            log.error(e.getMessage());
            throw new SearchException("Cannot search booking documents");
        }

        LinkedList<BookingResponse> data = response.hits().hits()
                .stream()
                .map(Hit::source)
                .map(bookingMapper::toResponse)
                .collect(Collectors.toCollection(LinkedList::new));

        Page pageBuilder = PageUtils.pageBuilder(pagination.getPageNumber(), pagination.getSize(), data);

        return TableResponse.builder()
                .data(data)
                .page(pageBuilder)
                .build();
    }

    private void bookingQueryFiltering(BookingSearchRequest bookingSearchRequest, BoolQuery.Builder boolQuery) {
        for(Field field: bookingSearchRequest.getClass().getDeclaredFields()){
            field.setAccessible(true);
            String fieldName = field.getName();

            Object fieldValue;
            try{
                fieldValue = field.get(bookingSearchRequest);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
                throw new ReflectionAccessException("Cannot access field " + fieldName);
            }
            if(RangeDto.class.isAssignableFrom(field.getType())){
                RangeDto value = (RangeDto) fieldValue;
                if(value == null || value.getValue1() == null || value.getOperator() == null){
                    continue;
                }
                if(value.getOperator() == CustomOperator.EQ) {
                    queryFilterHelper.addTermsFilter(boolQuery, fieldName, value);
                } else if(value instanceof NumericRangeDto){
                    queryFilterHelper.addRangeFilter(boolQuery, fieldName, value);
                } else if(value instanceof DateRangeDto){
                    queryFilterHelper.addDateRangeFilter(boolQuery, fieldName, value);
                }
            } else if(String.class.isAssignableFrom(field.getType())){
                String value = (String) fieldValue;
                if(value == null){
                    continue;
                }
                if(field.getName().equals("userEmail")){
                    if(!userManager.existsByUserEmail(value)){
                        throw new ResourceNotFoundException("User " + value + " does not exist");
                    }
                    queryFilterHelper.addTermsFilter(boolQuery, fieldName, value);
                } else{
                    queryFilterHelper.addMatchFilter(boolQuery, fieldName, value);
                }

            } else if(BookingStatus.class.isAssignableFrom(field.getType())){
                BookingStatus value = (BookingStatus) fieldValue;
                if(value == null){
                    continue;
                }
                queryFilterHelper.addTermsFilter(boolQuery, fieldName, value);
            }
        }

    }

    private void updateBookingSeatCount(Booking existingBooking, BookingRequest bookingRequest){
        int existingSeatCount = existingBooking.getSeatCount();
        int newSeatCount = bookingRequest.getSeatCount();
        Long showtimeId = existingBooking.getShowtime().getShowtimeId();

        if(newSeatCount > existingSeatCount){
            int addedSeats = newSeatCount - existingSeatCount;
            if(showtimeManager.reserveSeats(showtimeId, addedSeats) <= 0){
                throw new BookingConflictException("Not enough available seats for this showtime");
            }
        } else if(newSeatCount < existingSeatCount){
            int seatsToRelease = existingSeatCount - newSeatCount;
            if(showtimeManager.releaseSeats(showtimeId, seatsToRelease) <= 0){
                throw new BookingConflictException("Unable to release seats for this showtime");
            }
        }
    }
}
