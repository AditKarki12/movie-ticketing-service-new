package aditkarki.movieticketingservicenew.document;

import aditkarki.movieticketingservicenew.enums.CustomRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users", createIndex = false)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument {
    @Id
    @JsonProperty("userId")
    private String userId;

    @Field(type = FieldType.Text)
    @JsonProperty("userFirstName")
    private String userFirstName;

    @Field(type = FieldType.Text)
    @JsonProperty("userLastName")
    private String userLastName;

    @Field(type = FieldType.Keyword)
    @JsonProperty("userEmail")
    private String userEmail;

    @Field(type = FieldType.Keyword)
    @JsonProperty("role")
    private CustomRole role;
}
