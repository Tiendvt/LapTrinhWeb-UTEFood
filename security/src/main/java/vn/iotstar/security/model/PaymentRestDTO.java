package vn.iotstar.security.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRestDTO implements Serializable{
	private String status;
	private String message;
	private String URL;
	
}
