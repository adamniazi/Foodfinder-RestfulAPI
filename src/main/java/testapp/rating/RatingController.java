package testapp.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import testapp.user.Restaurant;

import java.util.List;


@RestController
@RequestMapping(value="/rating")
public class RatingController {

    private RatingService ratingService;

    public RatingController(RatingService rS){
        ratingService = rS;
    }

    //method to add and update rating
    //CAREFUL!! IF RATING ALREADY EXISTS, ITS IS DELETED AND REPLACED WITH THE NEW RATING RECEIVED
    @RequestMapping(value = "/{email:.+}/{restaurant}", method = RequestMethod.POST)
    public ResponseEntity<Rating> addRating(@PathVariable String email, @PathVariable String restaurant, @RequestBody Rating rate){
        Rating insertedRating = ratingService.addRating(email, restaurant, rate);
        if(!insertedRating.getErrors().isEmpty()){
            return new ResponseEntity<Rating>(insertedRating, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Rating>(insertedRating, HttpStatus.OK);
        }
    }

    //method to get all of user's ratings
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET)
    public List<Rating> getRatings(@PathVariable String email){
        List<Rating> ratings = ratingService.findAllRatings(email);
        return ratings;
    }

    //method to get specific rating
    @RequestMapping(value = "/{email:.+}/{restaurant}", method = RequestMethod.GET)
    public ResponseEntity<Rating> getRating(@PathVariable String email, @PathVariable String restaurant){
        //System.out.println("restaurant is: " + restaurant);
        Rating rating = ratingService.findRating(email, restaurant);
        ResponseEntity<Rating> ratingResponse;
        if(rating != null){
            ratingResponse = new ResponseEntity<Rating>(rating, HttpStatus.OK);
        } else {
            ratingResponse = new ResponseEntity<Rating>(rating, HttpStatus.NOT_FOUND);
        }
        return ratingResponse;
    }

    //method to update all ratings
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.POST)
    public ResponseEntity<List> updateRatings(@PathVariable String email, @RequestBody List<Rating> ratings){
        List<Rating> updated = ratingService.updateRatings(ratings);
        ResponseEntity<List> updatedRatings;
        if(updated != null){
            updatedRatings = new ResponseEntity<List>(updated, HttpStatus.OK);
        } else {
            updatedRatings = new ResponseEntity<List>(updated, HttpStatus.CONFLICT);
        }

        return updatedRatings;
    }
}
