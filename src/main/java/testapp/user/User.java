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
@Document(collection = "users")
public class User extends DomainEntity {

    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;


}
