package testapp.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import testapp.user.Restaurant;

import java.util.List;


@RestController
@RequestMapping(value="/restaurant")
public class RestaurantController {

    private RestaurantService resService;

    @Autowired
    public RestaurantController(RestaurantService rS){
        resService = rS;
    }

    /*method to add restaurant to restaurant repository
    * returns restaurant object with HTTP status OK if added successfully
    * returns restaurant object with HTTP status CONFLICT if unsuccessful*/
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant res){
        Restaurant addedRestaurant = resService.addRestaurant(res);
        if(!addedRestaurant.getErrors().isEmpty()){
            return new ResponseEntity<Restaurant>(addedRestaurant, HttpStatus.CONFLICT);
        }
        else{
            return new ResponseEntity<Restaurant>(addedRestaurant, HttpStatus.OK);
        }
    }

    /*method to get restaurant
    * returns restaurant if successful
    * or null if does not exist*/
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public Restaurant findRestaurant(@PathVariable String name){
        return resService.getRestaurant(name);
    }

    /*method to get all restaurants
    * returns a list of restaurants*/
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Restaurant> getRestaurants(@RequestParam(value = "keyword", required=false) String keyword){
        if(null != keyword){
            return resService.getRestaurantsWithKeyword(keyword);
        }
        else{
            return resService.getAllRestaurants();
        }
    }
}
