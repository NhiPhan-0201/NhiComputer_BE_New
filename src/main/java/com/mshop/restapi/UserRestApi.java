package com.mshop.restapi;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mshop.entity.Cart;
import com.mshop.entity.User;
import com.mshop.repository.CartRepository;
import com.mshop.repository.UserRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("api/users")
public class UserRestApi {
	@Autowired
	UserRepository repo;

	@Autowired
	CartRepository Crepo;

	@GetMapping
	public ResponseEntity<List<User>> getAll() {
		return ResponseEntity.ok(repo.findByStatusTrueAndRoleFalse());
	}

	@GetMapping("{id}")
	public ResponseEntity<User> getOne(@PathVariable("id") Long id) {
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.findById(id).get());
	}

	@GetMapping("email/{email}")
	public ResponseEntity<User> getOneByEmail(@PathVariable("email") String email) {
		return ResponseEntity.ok(repo.findByEmail(email));
	}

	@PostMapping
	public ResponseEntity<User> post(@RequestBody User user) {
		if(repo.existsByEmail(user.getEmail())) {
			return ResponseEntity.notFound().build();
		}
		if(repo.existsById(user.getUserId())) {
			return ResponseEntity.badRequest().build();
		}
		User u =  repo.save(user);
		Cart c = new Cart(0L, 0.0, user.getAddress(), user.getPhone(), true, u);
		Crepo.save(c);
		return ResponseEntity.ok(u);
	}

	@PutMapping("{id}")
	public ResponseEntity<User> put(@PathVariable("id") Long id, @RequestBody User user) {
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		if(!id.equals(user.getUserId())) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(repo.save(user));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if(!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		User u = repo.findById(id).get();
		u.setStatus(false);
		repo.save(u);
		return ResponseEntity.ok().build();
	}
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// Inner class dùng nhận dữ liệu đổi mật khẩu, an toàn hơn entity User
	public static class ChangePasswordRequest {
		public String email;
		public String oldPassword;
		public String newPassword;
	}

	// email: email tài khoản, password: mật khẩu cũ, name: mật khẩu mới
	@PostMapping("/change_password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
		User u = repo.findByEmail(request.email);
		if (u == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Email không tồn tại");
		}

		// So sánh mật khẩu
		if (!u.getPassword().equals(request.oldPassword)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Mật khẩu cũ không đúng");
		}

		// Cập nhật mật khẩu mới
		u.setPassword(request.newPassword);
		repo.save(u);

		// Trả về 200 OK với message
		return ResponseEntity.ok()
				.body(Map.of("message", "Đổi mật khẩu thành công", "status", 200));
	}
}