package testapp.restaurant;

import org.springframework.data.mongodb.repository.MongoRepository;
import testapp.user.Restaurant;

import java.util.List;


public interface RestaurantRepository extends MongoRepository<Restaurant, String> {

    Restaurant findByName(String s);

    List <Restaurant> findAll();
}
