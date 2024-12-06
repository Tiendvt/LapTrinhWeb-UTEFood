package vn.iotstar.security.service;

import vn.iotstar.security.model.User;

public interface UserService {
    User saveUser(User user);

	Boolean existsEmail(String email);
   

}
