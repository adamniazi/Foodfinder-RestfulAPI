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
        System.out.println("User requesting a recommendation is: " + email);
        List<Rating> PPP = ratingRepo.findByEmail(email);
        System.out.println("Found " + PPP.size() + " ratings for the user");
        if(PPP.isEmpty()){
            Restaurant tempRes = new Restaurant();
            tempRes.setName("You have not rating any restaurants yet");
            tempRes.setAddress("Please rate some restaurants");
            return tempRes;
        }
        //2. get all the users who have visited the restaurants the user has rated
        TreeSet<String> users = new TreeSet<>(); //set of users
        TreeMap<String, Integer> numSame = new TreeMap<>(); //map to hold user and number of same restaurants
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("----------------------------BUILDING A LIST OF USERS--------------------------");
        System.out.println("------------------------------------------------------------------------------");
        for(int i = 0; i < PPP.size(); i++){
            //all users who have rated given restaurant
            System.out.println("looking for users who have visited " + PPP.get(i).getRestaurant());
            System.out.println("--------------------------------------------------------");
            List<Rating> allResRatings = ratingRepo.findByRestaurant(PPP.get(i).getRestaurant());
            System.out.println("found " + allResRatings.size() + " users who have visited " + PPP.get(i).getRestaurant());
            //add people to users if they don't already exist in set
            //boolean same = true;
            //int look = 0;
            System.out.println("--------------------------------------------------------");
            System.out.println("Building users list for " + PPP.get(i).getRestaurant());
            for(int j = 0; j < allResRatings.size(); j++){
                //System.out.println("users list is = 0");
                String checkUser = allResRatings.get(j).getEmail();
                System.out.println("User being checked is " + checkUser);
                if(!checkUser.equals(email)){
                    if( users.add(checkUser)){
                        //numSame.put(checkUser, 0);
                        System.out.println(" - User added: " + checkUser);
                    } else {
                        System.out.println(checkUser + " already exists in the users list");
                    }
                } else {
                    System.out.println(checkUser + " is the requesting user");
                }
            }
            if(users.isEmpty()){
                Restaurant tempRes = new Restaurant();
                tempRes.setName("No users have similar tastes to you");
                tempRes.setAddress("Please rate more restaurants or try again when other users have rated more restaurants");
            }
            System.out.println("--------------------------------------------------------");
            System.out.println("Building users map for " + PPP.get(i).getRestaurant());
            for(int j = 0; j < allResRatings.size(); j++){
                String temp2 = allResRatings.get(j).getEmail();
                System.out.println("user being checked is " + temp2);
                //3. build a key-value pair with the key being the other user's email, value being the number of restaurants
                //   that user matches with the user who is asking for the recommendation
                if(!email.equals(temp2) && !users.add(temp2)){
                    System.out.println(temp2 + " is not requesting user and in user list with value " + numSame.get(temp2));
                   if(numSame.get(temp2) != null){
                       int oldScore = numSame.remove(temp2);
                       System.out.println(temp2 + " is in the map, don't add " + temp2 +" but remove score " + oldScore);
                       numSame.put(temp2, (oldScore + 1));
                   } else {
                       int newScore = 1;
                       System.out.println(temp2 + " is not in the map, add " + temp2 +" with score " + newScore);
                       numSame.put(temp2, newScore);
                   }
                } else if (temp2.equals(email)){
                    System.out.println("requesting user, do noting");
                } else {
                    int newScore = 1;
                    System.out.println(temp2 + " is not in the map, add " + temp2 +" with score " + newScore);
                    numSame.put(temp2, (newScore + 1));
                }
            }
        }
        System.out.println("found " + users.size() + " users who have visited similar restaurants, now looking for max");
        //4. if many users have the highest value, remove all other users, if only one, remove all other users
        //4.1 find the max value
        int max = 0;

        System.out.println("----------------------------------------------------------------------");
        Iterator<String> uItr = users.iterator(); //iterator to traverse the users list
        TreeSet<String> usersMax = new TreeSet<>();
        while(uItr.hasNext()){
            String str = uItr.next();
            int score = numSame.get(str);
            System.out.println(str + " has the score " + score);
            if(score > max){
                max = numSame.get(str);
                //usersMax.add(str);
                System.out.println("add user to userMax with, new max: " + max);
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
        System.out.println("found " + users.size() + " users who visited the max: " + max);
        //5. build another key-value pair where the key is the other users, value is the score of the other users
        //5.1 score is based on taking rating of the other users and comparing it to the requesting user's rating for
        //    a given restaurant, taking absolute value.
        TreeMap<String, Integer> scores = new TreeMap<>();
        TreeSet<String> users1 = new TreeSet<>();
        Iterator<String> uItr3 = users.iterator();
        while(uItr3.hasNext()){
            String str = uItr3.next();//current user being compared to requesting user
            System.out.println("current user being compared: " + str);
            int score = 0;
            if(numSame.get(str) != null){
                System.out.println("str != null");
                for(int i = 0; i < PPP.size(); i++){
                  Rating temp = PPP.get(i);
                  Rating temp2 = ratingRepo.findByEmailAndRestaurant(str, temp.getRestaurant());
                  if ((temp != null) && (temp2 != null)){
                      score = score + Math.abs((Integer.parseInt(temp.getRating())) - Integer.parseInt(temp2.getRating()));
                  }
                }
                System.out.println("User " + str +"'s score is " + score);
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
        System.out.println("Only kept " + users1.size() + "users");

        //5.2.3 make recommendation using the min scored user
        List<Rating> recUser;
        if(scores.size() == 1){
            // recommend a res
            String k = scores.firstKey();
            recUser = ratingRepo.findByEmail(k);
            System.out.println("Using " + k +  "'s ratings to make a recommendation using " + recUser.size() + " ratings");
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
            System.out.println("Using " + k+  "'s ratings to make a recommendation using " + recUser.size() + " ratings");
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
        System.out.println("max rating is " + max + " at position " + pos);

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
