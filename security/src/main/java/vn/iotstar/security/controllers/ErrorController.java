package vn.iotstar.security.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
	@GetMapping("/403")
    public String accessDenied() {
        return "403.html"; // Tên của view template 403.html
    }
}
