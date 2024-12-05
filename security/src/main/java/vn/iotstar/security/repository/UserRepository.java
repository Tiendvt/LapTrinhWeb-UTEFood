package vn.iotstar.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import vn.iotstar.security.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	public Boolean existsByEmail(String email);
	public User findByEmail(String email);
	public User findByResetToken(String token);
}
