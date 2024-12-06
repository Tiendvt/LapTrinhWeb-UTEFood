package vn.iotstar.security.configs;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{
	 @Override
	    public void handle(HttpServletRequest request, HttpServletResponse response,
	                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
	        // Kiểm tra người dùng hiện tại
	        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous";
	        System.out.println("Access Denied for user: " + username);

	        // Chuyển hướng đến trang 403.html
	        response.sendRedirect("/403");
	    }
}
