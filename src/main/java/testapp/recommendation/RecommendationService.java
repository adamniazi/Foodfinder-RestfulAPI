package testapp.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.Tree;
import testapp.rating.Rating;
import testapp.rating.RatingRepository;
import testapp.restaurant.RestaurantRepository;
import testapp.user.Restaurant;
import testapp.user.UserRepository;

import java.util.*;

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
        if(userRepo.findByEmail(email) == null){
            Restaurant tempRes = new Restaurant();
            tempRes.setName("User does not exist");
            tempRes.setAddress("Please register the user");
            return tempRes;
        }
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
                System.out.println("new max: " + max);
            }
        }
        //4.2 remove all users below max value
        Iterator<String> uItr2 = users.iterator();
        while(uItr2.hasNext()){
            String str = uItr2.next();
            if(numSame.get(str) < max){
                numSame.remove(str);
            } else {
                System.out.println("Added user: " + str + " to usersMax");
                usersMax.add(str);
            }
        }
        System.out.println("found " + usersMax.size() + " users who visited the max: " + max);
        //5. build another key-value pair where the key is the other users, value is the score of the other users
        //5.1 score is based on taking rating of the other users and comparing it to the requesting user's rating for
        //    a given restaurant, taking absolute value.
        TreeMap<String, Integer> scores = new TreeMap<>();
        TreeSet<String> users1 = new TreeSet<>();
        Iterator<String> uItr3 = usersMax.iterator();
        while(uItr3.hasNext()){
            System.out.println("----------------------------------------------------");
            String str = uItr3.next();//current user being compared to requesting user
            System.out.println("current user being compared: " + str);
            int score = 0;
            if(numSame.get(str) != null){
                System.out.println("str != null");
                for(int i = 0; i < PPP.size(); i++){
                  Rating temp = PPP.get(i);
                  Rating temp2 = ratingRepo.findByEmailAndRestaurant(str, temp.getRestaurant());
                  if ((temp != null) && (temp2 != null)){
                      int reqUScore = Integer.parseInt(temp.getRating());
                      int checkUScore = Integer.parseInt(temp2.getRating());
                      int abScore = Math.abs(reqUScore - checkUScore);
                      System.out.println(email+"'s score is " + reqUScore + " " + str + "'s score is " + checkUScore);
                      System.out.println("abs score is " + abScore);
                      score = score + abScore;
                  }
                }
                System.out.println("User " + str +"'s score is " + score);
            }
            scores.put(str, score);
            users1.add(str);
            System.out.println("----------------------------------------------------");
        }
        //5.2 find users with the lowest absolute value
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

        //5.2.3 make recommendation using the max average score of a restaurant from the min scored users
        TreeSet<String> minUsers = new TreeSet<>();
        Iterator<String> uItr6 = users1.iterator();
        while(uItr6.hasNext()){
            String minUser = uItr6.next();
            if(scores.get(minUser) != null){
                //add all left over min score users
                minUsers.add(minUser);
            }
        }
        System.out.println("There are " + minUsers.size() + " users in the minUsers TreeSet");

        //if no users, return that no users match
        if(minUsers.isEmpty()){
            Restaurant returnRes = new Restaurant();
            returnRes.setName("Cannot recommend a restaurant");
            returnRes.setAddress("No users match your ratings");
            return returnRes;
        }

        //from the minUsers set, find average rating for each restaurant
        Iterator<String> uItr7 = minUsers.iterator();
        TreeSet<String> recReses = new TreeSet<>(); //recommendable restaurants
        TreeMap<String, Recommendation> recScores = new TreeMap<>();

        while(uItr7.hasNext()){
            String curr = uItr7.next();
            System.out.println("----------------------------------------------------");
            System.out.println("Current user being checked from minUsers is: " + curr);

            List<Rating> currRatings = ratingRepo.findByEmail(curr); //get all of the restaurants by the current user being checked
            for(int i = 0; i < currRatings.size(); i++){
                String tempName = currRatings.get(i).getRestaurant();
                if(recReses.add(tempName)){
                    //added in the recommendable restaurants set
                    System.out.println(tempName + " added to the recommendable restaurant set");
                    Recommendation rec = new Recommendation();
                    rec.setName(tempName);
                    rec.setRating(Integer.parseInt(currRatings.get(i).getRating()));
                    rec.setNumUsers(1);
                    recScores.put(tempName, rec);
                    System.out.println(tempName + " updated with numUsers: "+ rec.getNumUsers() + " and rating: " + rec.getRating());
                } else {
                    //already in the recommendable restaurants set
                    Recommendation tempRec = recScores.remove(tempName);
                    int tempRating = tempRec.getRating();
                    int tempNumUsers = tempRec.getNumUsers();
                    String tempRes = tempRec.getName();
                    Recommendation newRec = new Recommendation();
                    newRec.setName(tempRes);
                    newRec.setNumUsers(tempNumUsers + 1);
                    newRec.setRating(tempRating + Integer.parseInt(currRatings.get(i).getRating()));
                    recScores.put(tempName, newRec);
                    System.out.println(tempName + " updated with numUsers: "+ tempRec.getNumUsers() + " and rating: " + tempRec.getRating());
                }
            }
            System.out.println("----------------------------------------------------");
        }
        System.out.println("recScores: " + recScores.size() + " recReses: " + recReses.size());
        System.out.println("----------------------------------------------------------------");

        System.out.println("from the recommendable set, remove all restaurants requesting user has already visited");
        List<Rating> reqUsersRatings = ratingRepo.findByEmail(email);
        ArrayList<String> reqUsersRestaurants = new ArrayList<>();
        for(int i = 0; i < reqUsersRatings.size(); i ++){
            reqUsersRestaurants.add(reqUsersRatings.get(i).getRestaurant());
        }

        for(int i = 0; i < reqUsersRestaurants.size(); i++){
            if(recReses.contains(reqUsersRestaurants.get(i))){
                recReses.remove(reqUsersRestaurants.get(i));
                recScores.remove(reqUsersRestaurants.get(i));
            }
        }
        System.out.println("recScores: " + recScores.size() + " recReses: " + recReses.size());
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Building a treemap with the remaining restaurants and their average scores");
        TreeMap<String, Integer> averageResScores = new TreeMap<>();
        TreeSet<String> above2 = new TreeSet<>();
        Iterator<String> uItr8 = recReses.iterator();
        while(uItr8.hasNext()){
            String resName = uItr8.next();
            int resNumUsers = recScores.get(resName).getNumUsers();
            int totalRating = recScores.get(resName).getRating();
            int averageScore = totalRating / resNumUsers;
            System.out.println(resName + " has the numUsers: " + resNumUsers + " rating: " + totalRating + " and average " + averageScore);
            if(averageScore > 2){
                averageResScores.put(resName, averageScore);
                above2.add(resName);
            }
        }

        if(above2.isEmpty()){
            Restaurant returnRes = new Restaurant();
            returnRes.setName("Cannot recommend a restaurant");
            returnRes.setAddress("all recommendable restaurants are rated under 3/5 for you");
            return returnRes;
        }

        Restaurant restaurantName = null;
        int maxResScore = Integer.MIN_VALUE;
        Iterator<String> uItr9 = above2.iterator();
        while(uItr9.hasNext()){
            String resName = uItr9.next();
            if(averageResScores.get(resName) > maxResScore){
                restaurantName = resRepo.findByName(resName);
                maxResScore = averageResScores.get(resName);
            }
        }


        if(restaurantName == null){
            Restaurant returnRes = new Restaurant();
            returnRes.setName("Cannot recommend a restaurant");
            returnRes.setAddress("all recommendable restaurants are rated under 3/5");
            return returnRes;
        } else{
            return restaurantName;
        }



        //6. recommend restaurant based on user with lowest absolute score's rating of a restaurant that was rated highest
        //   that requesting user has not visited.


    }

    //public TreeSet<String> getUsersVisitingSameRes()
}
