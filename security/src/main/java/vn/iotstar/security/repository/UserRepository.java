package vn.iotstar.security.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.security.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	public Boolean existsByEmail(String email);
	public User findByEmail(String email);
	public User findByResetToken(String token);
	public List<User> findByRole(String role);
	public User findByActiveToken(String token);
	Page<User> findByRoleAndEmailContainingIgnoreCaseOrRoleAndNameContainingIgnoreCase(String role1, String ch, String role2, String ch2, Pageable pageable);
	Page<User> findByRole(String role, Pageable pageable);
	User findById(int id);
}
