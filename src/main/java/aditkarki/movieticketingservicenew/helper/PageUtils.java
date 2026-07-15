package aditkarki.movieticketingservicenew.helper;

import aditkarki.movieticketingservicenew.dto.Page;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class PageUtils {
    public static Page pagination(int pageNumber, int size) {
        int pageNum = Math.max(pageNumber, 0);
        int pageSize = Math.max(size, 10);
        return Page.builder()
                .pageNumber(pageNum)
                .size(pageSize)
                .build();
    }

    public static Page pageBuilder(int pageNumber, int size, SearchResponse<?> response) {

        long totalElements = response.hits().total() != null ? response.hits().total().value() : 0;

        int totalPages = (int) Math.ceil(totalElements / (double) size);

        Page pageBuilder = Page.builder()
                .pageNumber(pageNumber)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        return pageBuilder;
    }

    public static Page pageBuilder(int pageNumber, int size, LinkedList<?> parsedResponse) {

        long totalElements = parsedResponse.size();

        int totalPages = (int) Math.ceil(totalElements / (double) size);

        Page pageBuilder = Page.builder()
                .pageNumber(pageNumber)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();

        return pageBuilder;
    }

}