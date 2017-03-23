package testapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository uR){
        userRepository = uR;
    }

    /*method to add user to database.*/
    public User addUser(User u){
        User addedUser;
        if(userRepository.findByEmail(u.getEmail()) != null){
            addedUser = new User();
            addedUser.addError("User already exists!");
        } else {
            addedUser = userRepository.save(u);
        }
        return addedUser;
    }

    /*method to find user in database.*/
    public User findUser(String email){
        User found = userRepository.findByEmail(email);
        if (found != null) {
            return found;
        } else {
            User notFound = new User();
            notFound.addError("User not found");
            return notFound;
        }
    }

    /*method to update a user's profile*/
    public User updateProfile(String email, User u){
        User result = userRepository.findByEmail(email);

        if(result != null){
            return userRepository.save(u);
        } else {
            User notFound = new User();
            notFound.addError("User not updated");
            return notFound;
        }
    }
}
