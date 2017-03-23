package testapp.rating;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import testapp.DomainEntity;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "ratings")
public class Rating extends DomainEntity {

    @Id
    private String id;
    private String email;
    private String restaurant;
    private String rating;

}
