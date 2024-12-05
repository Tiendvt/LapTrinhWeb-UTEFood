package vn.iotstar.security.service;

import vn.iotstar.security.model.User;

public interface UserService {
    User saveUser(User user);

	Boolean existsEmail(String email);
	public User getUserByEmail(String email);
	public User getUserByToken(String token);
	public void updateUserResetToken(String email, String resetToken);
	public User updateUser(User user);
}
