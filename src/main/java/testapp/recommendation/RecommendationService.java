package testapp.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testapp.rating.Rating;
import testapp.rating.RatingRepository;
import testapp.restaurant.RestaurantRepository;
import testapp.user.Restaurant;
import testapp.user.User;
import testapp.user.UserRepository;

import java.util.*;

import java.util.stream.Collectors;

@Service

public class RecommendationService {

    private RatingRepository ratingRepo;
    private RestaurantRepository resRepo;
    private UserRepository userRepo;

    @Autowired
    public RecommendationService(RatingRepository rRepo, RestaurantRepository resR, UserRepository uR){
        ratingRepo = rRepo;
        resRepo = resR;
        userRepo = uR;
    }

    public Restaurant getRecommendation (String email){
        //1. get all of the requesting user's ratings
        List<Rating> PPP = ratingRepo.findByEmail(email);
        //2. get all the users who have visited the restaurants the user has rated
        TreeSet<String> users = new TreeSet<>(); //set of users
        Iterator<String> uItr = users.iterator(); //iterator to traverse the users list
        TreeMap<String, Integer> numSame = new TreeMap<>(); //map to hold user and number of same restaurants
        for(int i = 0; i < PPP.size(); i++){
            //all users who have rated given restaurant
            List<Rating> allResRatings = ratingRepo.findByRestaurant(PPP.get(i).getRestaurant());
            //add people to users if they don't already exist in set
            for(int j = 0; j < users.size(); j++){
                String temp2 = allResRatings.get(i).getEmail();
                //3. build a key-value pair with the key being the other user's email, value being the number of restaurants
                //   that user matches with the user who is asking for the recommendation
                if(!email.equals(temp2) && users.add(temp2)){
                    numSame.put(temp2, 1);
                } else {
                    numSame.put(temp2, numSame.remove(temp2)+1);
                }
            }
        }
        //4. if many users have the highest value, remove all other users, if only one, remove all other users
        //4.1 find the max value
        int max = 0;

        while(uItr.hasNext()){
            String str = uItr.next();
            if(numSame.get(str) > max){
                max = numSame.get(str);
            }
        }
        //4.2 remove all users below max value
        Iterator<String> uItr2 = users.iterator();
        while(uItr2.hasNext()){
            String str = uItr2.next();
            if(numSame.get(str) < max){
                numSame.remove(str);
            }
        }
        //5. build another key-value pair where the key is the other users, value is the score of the other users
        //5.1 score is based on taking rating of the other users and comparing it to the requesting user's rating for
        //    a given restaurant, taking absolute value.
        TreeMap<String, Integer> scores = new TreeMap<>();
        TreeSet<String> users1 = new TreeSet<>();
        Iterator<String> uItr3 = users.iterator();
        while(uItr3.hasNext()){
            String str = uItr3.next();//current user being compared to requesting user
            int score = 0;
            if(numSame.get(str) != null){
                for(int i = 0; i < PPP.size(); i++){
                  Rating temp = PPP.get(i);
                  Rating temp2 = ratingRepo.findByEmailAndRestaurant(str, temp.getRestaurant());
                  score = score + Math.abs((Integer.parseInt(temp.getRating())) - Integer.parseInt(temp2.getRating()));
                }
            }
            scores.put(str, score);
            users1.add(str);
        }
        //5.2 use user with the lowest absolute value, if more than 1, use the first user
        //5.2.1 find the min score
        int minScore = Integer.MAX_VALUE;
        Iterator<String> uItr4 = users1.iterator();
        while(uItr4.hasNext()){
            String str = uItr4.next();
            if(scores.get(str) < minScore){
                minScore = scores.get(str);
            }
        }
        //5.2.2 remove all users above the min score
        Iterator<String> uItr5 = users1.iterator();
        while(uItr5.hasNext()){
            String str = uItr5.next();
            if(scores.get(str) > minScore){
                scores.remove(str);
            }
        }
        //5.2.3 make recommendation using the min scored user
        List<Rating> recUser;
        if(scores.size() == 1){
            // recommend a res
            String k = scores.firstKey();
            recUser = ratingRepo.findByEmail(k);
            for(int i = 0; i < PPP.size(); i++){
                for(int j = 0; j < recUser.size(); j++){
                    if(PPP.get(i).getRestaurant().equals(recUser.get(j).getRestaurant())){
                        recUser.remove(j);
                    }
                }
            }
        } else{
            //try different users
            String k = scores.firstKey();
            recUser = ratingRepo.findByEmail(k);
            for(int i = 0; i < PPP.size(); i++){
                for(int j = 0; j < recUser.size(); j++){
                    if(PPP.get(i).getRestaurant().equals(recUser.get(j).getRestaurant())){
                        recUser.remove(j);
                    }
                }
            }
        }
        int maxRating = 0;
        int pos = 0;
        String rec;
        for(int i = 0; i < recUser.size(); i++){
            if(Integer.parseInt(recUser.get(i).getRating()) > maxRating){
                maxRating = Integer.parseInt(recUser.get(i).getRating());
                pos = i;
            }
        }

        if(recUser.size() > 0){
            rec = recUser.get(pos).getRestaurant();
        } else{
            rec = "No restaurant found";
        }

        //6. recommend restaurant based on user with lowest absolute score's rating of a restaurant that was rated highest
        //   that requesting user has not visited.

        return resRepo.findByName(rec);
    }

    //public TreeSet<String> getUsersVisitingSameRes()
}
