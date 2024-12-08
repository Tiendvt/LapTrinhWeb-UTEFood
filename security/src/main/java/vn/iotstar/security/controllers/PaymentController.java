
package vn.iotstar.security.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import vn.iotstar.security.configs.PaymentConfig;
import vn.iotstar.security.model.PaymentRestDTO;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	@GetMapping("/create_payment")
	public ResponseEntity<?> createPayment(HttpServletRequest req) throws UnsupportedEncodingException {
	    String orderType = "billpayment";
	    double rawAmount = Double.parseDouble(req.getParameter("amount"));
	    String vnp_TxnRef = PaymentConfig.getRandomNumber(8);
	    String vnp_IpAddr = PaymentConfig.getIpAddress(req);
	    long amount = (long) (rawAmount * 100);
	    String vnp_TmnCode = PaymentConfig.vnp_TmnCode;
	    
	    Map<String, String> vnp_Params = new HashMap<>();
	    vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
	    vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
	    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
	    vnp_Params.put("vnp_Amount", String.valueOf(amount));
	    vnp_Params.put("vnp_CurrCode", "VND");
	    vnp_Params.put("vnp_BankCode", "NCB");
	    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
	    vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
	    vnp_Params.put("vnp_OrderType", orderType);

	    String locate = req.getParameter("language");
	    vnp_Params.put("vnp_Locale", (locate != null && !locate.isEmpty()) ? locate : "vn");
	    vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
	    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

	    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String vnp_CreateDate = formatter.format(cld.getTime());
	    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

	    cld.add(Calendar.MINUTE, 15);
	    String vnp_ExpireDate = formatter.format(cld.getTime());
	    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

	    List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
	    Collections.sort(fieldNames);

	    StringBuilder hashData = new StringBuilder();
	    StringBuilder query = new StringBuilder();

	    for (String fieldName : fieldNames) {
	        String fieldValue = vnp_Params.get(fieldName);
	        if (fieldValue != null && !fieldValue.isEmpty()) {
	            hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
	            query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
	                 .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
	            if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
	                query.append('&');
	                hashData.append('&');
	            }
	        }
	    }

	    String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
	    String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

	    // Redirect user to the payment URL
	    return ResponseEntity.status(HttpStatus.FOUND)
	            .header("Location", paymentUrl)
	            .build();
	}

}
