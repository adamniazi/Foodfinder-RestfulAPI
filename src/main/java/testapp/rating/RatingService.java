package testapp.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testapp.restaurant.RestaurantRepository;
import testapp.user.Restaurant;
import testapp.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private RatingRepository ratingRepo;
    private RestaurantRepository resRepo;
    private UserRepository userRepo;

    @Autowired
    public RatingService (RatingRepository rR, UserRepository uR, RestaurantRepository resR){
        ratingRepo = rR;
        resRepo = resR;
        userRepo = uR;
    }

    public Rating addRating(String email, String restaurant, Rating rating){
        Rating addedRating = ratingRepo.findByEmailAndRestaurant(email, restaurant);
        if((userRepo.findByEmail(email) != null) && (resRepo.findByName(restaurant) != null)){
            if(addedRating != null){
                ratingRepo.deleteByEmailAndRestaurant(email, restaurant);
                return ratingRepo.save(rating);
            } else {
                ratingRepo.save(rating);
                return rating;
            }
        } else {
            Rating none = new Rating();
            none.addError("user or restaurant not found");
            return none;
        }
    }

    public List<Rating> findAllRatings(String email){
        return ratingRepo.findByEmail(email);
    }

    public Rating findRating(String email, String restaurant){
        return ratingRepo.findByEmailAndRestaurant(email, restaurant);
    }

    public List<Rating> updateRatings(List<Rating> ratings){
        String email = ratings.get(0).getEmail();
        while(!ratings.isEmpty()){
            Rating curr = ratings.get(0);
            addRating(curr.getEmail(), curr.getRestaurant(), curr);
            ratings.remove(0);
        }
        return findAllRatings(email);
    }
}
