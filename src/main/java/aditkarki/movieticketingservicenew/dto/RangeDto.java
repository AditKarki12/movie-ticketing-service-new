package aditkarki.movieticketingservicenew.dto;

import aditkarki.movieticketingservicenew.CustomOperator;
import lombok.Data;

@Data
public class RangeDto {

    String value1;

    String value2;

    CustomOperator operator;

}
