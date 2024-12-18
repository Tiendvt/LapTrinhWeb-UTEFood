package vn.iotstar.security.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;


import vn.iotstar.security.model.User;
import vn.iotstar.security.repository.UserRepository;
import vn.iotstar.security.service.UserService;
import vn.iotstar.security.util.AppConstant;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	public User saveUser(User user) {
		user.setRole("ROLE_USER");
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		User saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User getUserByResetToken(String token) {
		return userRepository.findByResetToken(token);
	}
	@Override
	public void updateUserResetToken(String email, String resetToken) {
		User findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
	}
	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<User> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<User> findByuser = userRepository.findById(id);

		if (findByuser.isPresent()) {
			User user = findByuser.get();
			user.setIsEnable(status);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public User saveAdmin(User user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);

		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		User saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public User updateUserProfile(User user, MultipartFile img) {
		User dbUser = userRepository.findById(user.getId()).get();

		if (!img.isEmpty()) {
			dbUser.setProfileImage(img.getOriginalFilename());
		}

		if (!ObjectUtils.isEmpty(dbUser)) {

			dbUser.setName(user.getName());
			dbUser.setMobileNumber(user.getMobileNumber());
			dbUser.setAddress(user.getAddress());
			dbUser.setCity(user.getCity());
			dbUser.setProvince(user.getProvince());
			dbUser.setPincode(user.getPincode());
			dbUser = userRepository.save(dbUser);
		}

		try {
			if (!img.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ img.getOriginalFilename());

				Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbUser;
	}

	@Override
	public void increaseFailedAttempt(User user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);		
	}

	@Override
	public void userAccountLock(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);		
	}

	@Override
	public boolean unlockAccountTimeExpired(User user) {
		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

		long currentTime = System.currentTimeMillis();

		if (unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void updateUserActiveToken(String email, String activeToken) {
		User findByEmail = userRepository.findByEmail(email);
		findByEmail.setActiveToken(activeToken);
		userRepository.save(findByEmail);
		
	}

	@Override
	public User getUserByActiveToken(String token) {
		return userRepository.findByActiveToken(token);
	}

	@Override
	public Page<User> searchUsersPagination(String role, Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return userRepository.findByRoleAndEmailContainingIgnoreCaseOrRoleAndNameContainingIgnoreCase(role, ch, role, ch, pageable);
	}

	@Override
	public Page<User> getAllUsersPagination(String role, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return userRepository.findByRole(role, pageable);
	}

	@Override
	public Boolean updateAccountRole(Integer id, String role) {
		Optional<User> findByuser = userRepository.findById(id);

		if (findByuser.isPresent()) {
			User user = findByuser.get();
			user.setRole(role);
			userRepository.save(user);
			return true;
		}
		return false;
	}


}
