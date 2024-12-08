package vn.iotstar.security.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.User;

public interface UserService {
	User saveUser(User user);

	Boolean existsEmail(String email);

	public User getUserByEmail(String email);

	public User getUserByResetToken(String token);

	public void updateUserResetToken(String email, String resetToken);

	public User updateUser(User user);

	public List<User> getUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);

	public User saveAdmin(User user);

	public User updateUserProfile(User user, MultipartFile img);

	public void increaseFailedAttempt(User user);

	public void userAccountLock(User user);

	public boolean unlockAccountTimeExpired(User user);

	public User getUserByActiveToken(String token);

	public void updateUserActiveToken(String email, String activeToken);

	public Page<User> searchUsersPagination(String role, Integer pageNo, Integer pageSize, String ch);

	public Page<User> getAllUsersPagination(String role, Integer pageNo, Integer pageSize);

	public Boolean updateAccountRole(Integer id, String role);
}
