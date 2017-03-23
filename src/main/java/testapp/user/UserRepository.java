package testapp.user;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by adam on 3/6/2017.
 */

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
}
