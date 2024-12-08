package vn.iotstar.security.util;

public enum OrderStatus {

	NEW_ORDER(1, "New Order"), ORDER_CONFIRMED(2, "Order Confirmed"), PRODUCT_PACKED(3, "In Transit"),
	DELIVERED(4, "Delivered"), CANCELLED(5, "Cancelled"),RETURNED_REFUNDED(6,"Returned - Refunded");

	private Integer id;

	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
