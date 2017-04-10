package testapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService uS){
        userService = uS;
    }

    /*method to add user to database.*/
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@RequestBody User u){
        System.out.println("received a user");
        User addedUser = userService.addUser(u);
        if(!addedUser.getErrors().isEmpty()){
            System.out.println("return conflict");
            return new ResponseEntity<User>(addedUser, HttpStatus.CONFLICT);
        } else {
            System.out.println("return OKAY!");
            return new ResponseEntity<User>(addedUser, HttpStatus.OK);
        }
    }

    /*method to find user in database to allow log in.*/
    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET)
    public ResponseEntity<User> login(@PathVariable String email){
        User found = userService.findUser(email);
        if(!found.getErrors().isEmpty()){
            return new ResponseEntity<User>(found, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<User>(found, HttpStatus.OK);
        }
    }

    /*Update a user's profile.*/
    @RequestMapping(value = "/{email}/update", method = RequestMethod.POST)
    public ResponseEntity<User> updateProfile(@PathVariable String email, @RequestBody User u){
        User updated = userService.updateProfile(email, u);
        if(!updated.getErrors().isEmpty()){
            return new ResponseEntity<User>(u, HttpStatus.NOT_FOUND);
        } else{
            return new ResponseEntity<User>(updated, HttpStatus.OK);
        }
    }
}
