package testapp.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import testapp.DomainEntity;

@Component
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "restaurants")
public class Restaurant extends DomainEntity{

    @Id
    private String id;
    private String name;
    private String address;


}
