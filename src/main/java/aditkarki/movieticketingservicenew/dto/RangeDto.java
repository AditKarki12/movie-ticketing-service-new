package aditkarki.movieticketingservicenew.dto;

import aditkarki.movieticketingservicenew.enums.CustomOperator;
import lombok.Data;

@Data
public class RangeDto {

    String value1;

    String value2;

    CustomOperator operator;

}
