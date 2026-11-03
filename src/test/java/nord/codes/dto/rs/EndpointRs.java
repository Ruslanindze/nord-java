package nord.codes.dto.rs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointRs {
    String result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;
}
