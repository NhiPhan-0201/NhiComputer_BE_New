package com.mshop.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String address;
	private Double amount; // tổng tiền trước giảm giá
	private Date orderDate;
	private String phone;
	private Integer status;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "discount_code")
	private String discountCode;

	@Column(name = "discount_amount")
	private Double discountAmount;

	@Column(name = "final_amount")
	private Double finalAmount;

	// Getter, Setter...
}
