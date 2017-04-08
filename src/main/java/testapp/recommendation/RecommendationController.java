package testapp.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import testapp.user.Restaurant;

import java.util.List;


@RestController
@RequestMapping(value="/recommend")
public class RecommendationController {

    private RecommendationService recService;

    @Autowired
    public RecommendationController(RecommendationService rS){
        recService = rS;
    }

    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET)
    public ResponseEntity<Restaurant> recommend(@PathVariable String email){
        Restaurant recommendation = recService.getRecommendation(email);
        return new ResponseEntity<Restaurant>(recommendation, HttpStatus.OK);
    }

}
