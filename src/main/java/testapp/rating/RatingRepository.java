package testapp.rating;

import org.springframework.data.mongodb.repository.MongoRepository;
import testapp.user.Restaurant;

import java.util.List;

public interface RatingRepository extends MongoRepository<Rating, String>{

    Rating findByEmailAndRestaurant(String email, String restaurant);
    long deleteByEmailAndRestaurant(String email, String restaurant);
    List<Rating> findByEmail(String email);
}
