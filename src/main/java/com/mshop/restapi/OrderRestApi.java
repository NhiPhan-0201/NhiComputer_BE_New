package com.mshop.restapi;

import java.util.List;

import com.mshop.entity.User;
import com.mshop.entity.Order;
import com.mshop.repository.OrderDetailRepository;
import com.mshop.repository.OrderRepository;
import com.mshop.repository.UserRepository;
import com.mshop.service.NotificationService;
import com.mshop.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/orders")
public class OrderRestApi {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	NotificationService notificationService;

	@GetMapping()
	public ResponseEntity<List<Order>> getAll() {
		return ResponseEntity.ok(orderRepository.findAllOrderDesc());
	}

	@GetMapping("/wait")
	public ResponseEntity<List<Order>> getAllWait() {
		return ResponseEntity.ok(orderRepository.findAllOrderWait());
	}

	@GetMapping("{id}")
	public ResponseEntity<Order> getOne(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return orderRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<List<Order>> getAllByUser(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findAllOrderByUserId(id));
	}

	@GetMapping("/user/wait/{id}")
	public ResponseEntity<List<Order>> getAllWaitByUser(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findAllOrderWaitByUserId(id));
	}

	@GetMapping("/user/confirmed/{id}")
	public ResponseEntity<List<Order>> getAllConfirmedByUser(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findAllOrderConfirmedByUserId(id));
	}

	@GetMapping("/user/paid/{id}")
	public ResponseEntity<List<Order>> getAllPaidByUser(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findAllOrderPaidByUserId(id));
	}

	@GetMapping("/user/cancel/{id}")
	public ResponseEntity<List<Order>> getAllCancelByUser(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findAllOrderCancelByUserId(id));
	}

	// ========== POST ĐẶT HÀNG ==========
	@PostMapping
	public ResponseEntity<Order> post(@RequestBody Order order) {
		// Kiểm tra user
		if (order.getUser() == null || order.getUser().getUserId() == null || !userRepository.existsById(order.getUser().getUserId())) {
			return ResponseEntity.notFound().build();
		}
		// Nếu finalAmount null thì lấy bằng amount
		if (order.getFinalAmount() == null) {
			order.setFinalAmount(order.getAmount());
		}
		// Nếu discountAmount null thì bằng 0
		if (order.getDiscountAmount() == null) {
			order.setDiscountAmount(0.0);
		}
		// Lưu order
		Order savedOrder = orderRepository.save(order);

		// Gửi mail xác nhận
		User user = userRepository.findById(order.getUser().getUserId()).orElseThrow();
		sendMailService.queue(
				user.getEmail(),
				"Đơn hàng của anh/chị " + user.getName() + " mua thành công ",
				"Tổng giá của đơn hàng là " + savedOrder.getFinalAmount() + " mua vào ngày " + savedOrder.getOrderDate() + " . Vận chuyển tới " + savedOrder.getAddress()
		);

		return ResponseEntity.ok(savedOrder);
	}

	@PutMapping("{id}")
	public ResponseEntity<Order> put(@PathVariable("id") Long id, @RequestBody Order order) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		if (!id.equals(order.getId())) {
			return ResponseEntity.badRequest().build();
		}
		Order o = orderRepository.save(order);
		return ResponseEntity.ok(o);
	}

	// ======= Các API cập nhật trạng thái đơn hàng =======
	@PutMapping("/user/cancel/{id}")
	public ResponseEntity<?> requestCancelOrder(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).orElseThrow();
		if (order.getStatus() == 0) {
			return ResponseEntity.badRequest().body("Đơn hàng đã bị hủy trước đó.");
		}
		if (order.getStatus() == 2) {
			return ResponseEntity.badRequest().body("Đơn hàng đang được giao, không thể yêu cầu hủy.");
		}
		if (order.getStatus() == 3) {
			return ResponseEntity.badRequest().body("Đơn hàng đã giao, không thể yêu cầu hủy.");
		}
		order.setStatus(4); // Chờ admin duyệt hủy
		Order o = orderRepository.save(order);
		return ResponseEntity.ok(o);
	}

	@PutMapping("/admin/delivered/{id}")
	public ResponseEntity<?> deliveredOrder(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).orElseThrow();

		if (order.getStatus() != 2) { // 2: Đang giao
			return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái đang giao.");
		}

		order.setStatus(3); // 3: Đã giao
		Order o = orderRepository.save(order);

		// Gửi thông báo đã giao hàng thành công
		notificationService.createNotification(
				order.getUser().getUserId(),
				order.getId(),
				"Đơn hàng #" + order.getId() + " của bạn đã được <b>giao thành công</b>! Cảm ơn bạn đã mua hàng."
		);

		return ResponseEntity.ok(o);
	}

	@PutMapping("/admin/confirm/{id}")
	public ResponseEntity<?> confirmOrder(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).orElseThrow();
		if (order.getStatus() != 1) { // 1: Chờ xác nhận
			return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái chờ xác nhận.");
		}
		order.setStatus(2); // 2: Đang giao
		Order o = orderRepository.save(order);

		// Gửi thông báo xác nhận đơn hàng
		notificationService.createNotification(
				order.getUser().getUserId(),
				order.getId(),
				"Đơn hàng #" + order.getId() + " của bạn đã được <b>xác nhận</b> và đang được giao!"
		);

		return ResponseEntity.ok(o);
	}

	@PutMapping("/admin/cancel/{id}")
	public ResponseEntity<?> approveCancelOrder(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).orElseThrow();

		if (order.getStatus() != 4) {
			return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái chờ duyệt hủy.");
		}

		order.setStatus(0); // Đã hủy
		Order o = orderRepository.save(order);

		// Gửi thông báo duyệt hủy
		notificationService.createNotification(
				order.getUser().getUserId(),
				order.getId(),
				"Đơn hàng #" + order.getId() + " của bạn đã được <b>hủy thành công</b>."
		);

		return ResponseEntity.ok(o);
	}

	@PutMapping("/admin/cancel/deny/{id}")
	public ResponseEntity<?> denyCancelOrder(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).orElseThrow();

		if (order.getStatus() != 4) {
			return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái chờ duyệt hủy.");
		}

		order.setStatus(2); // Quay lại trạng thái đang giao (hoặc trạng thái trước đó)
		Order o = orderRepository.save(order);

		// Gửi thông báo từ chối hủy
		notificationService.createNotification(
				order.getUser().getUserId(),
				order.getId(),
				"Yêu cầu hủy đơn hàng #" + order.getId() + " của bạn đã bị <b>từ chối</b>. Đơn hàng sẽ tiếp tục được giao."
		);

		return ResponseEntity.ok(o);
	}

	@PutMapping("/confirm-received/{id}")
	public ResponseEntity<?> confirmReceived(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).orElseThrow();

		if (order.getStatus() != 2) { // 2: Đang giao
			return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái đang giao.");
		}

		order.setStatus(3); // 3: Đã giao
		Order o = orderRepository.save(order);

		// Gửi thông báo cho admin hoặc user nếu muốn

		return ResponseEntity.ok(o);
	}
}