package vn.iotstar.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import vn.iotstar.security.model.User;
import vn.iotstar.security.repository.UserRepository;
import vn.iotstar.security.service.UserService;
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
	public User getUserByToken(String token) {
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
	
}
