package aditkarki.movieticketingservicenew.dto.responses;

import aditkarki.movieticketingservicenew.dto.Page;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Builder
@Data
public class TableResponse {

    private LinkedList<?> data;

    private Page page;

//    private LinkedHashMap<Object, Object> metadata;

}
