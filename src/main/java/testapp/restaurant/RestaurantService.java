package testapp.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testapp.user.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private RestaurantRepository resRepository;

    @Autowired
    public RestaurantService(RestaurantRepository rR){
        resRepository = rR;
    }

    /*method to add restaurant
    * returns restaurant object with errors if restaurant already exists
    * returns restaurant object with no errors if restaurant added successfully*/
    public Restaurant addRestaurant(Restaurant r){
        Restaurant returnRestaurant;
        if(null != resRepository.findByName(r.getName())){
            returnRestaurant = new Restaurant();
            returnRestaurant.addError("Restaurant already exists!");
        } else {
            returnRestaurant = resRepository.save(r);
        }
        return returnRestaurant;
    }

    public Restaurant getRestaurant(String s){
        return resRepository.findByName(s);
    }

    public List<Restaurant> getAllRestaurants(){
        return resRepository.findAll();
    }

    public List<Restaurant> getRestaurantsWithKeyword(String keyword){
        List<Restaurant> allRestaurants = resRepository.findAll();
        List<Restaurant> restaurantWithKeyword = new ArrayList<>();
        //System.out.println("searching for keyword:" + keyword);
        for(Restaurant restaurant : allRestaurants){
            if(restaurant.getName().toLowerCase().contains(keyword.toLowerCase())){
                System.out.println("found");
                //restaurantWithKeyword.add(restaurant);
            }
        }
        return restaurantWithKeyword;
    }
}
