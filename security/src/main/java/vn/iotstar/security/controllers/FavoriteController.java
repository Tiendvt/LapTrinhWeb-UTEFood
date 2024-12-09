package vn.iotstar.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.iotstar.security.service.FavoriteService;

import java.security.Principal;
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Toggle favorite product
    @PostMapping("/product/toggle")
    public ResponseEntity<String> toggleFavoriteProduct(@RequestParam Integer productId, Principal principal) {

    	 if (principal == null) {
    	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthenticated");
    	    }
    	
         boolean added = favoriteService.toggleFavoriteProduct(principal.getName(), productId);
         return ResponseEntity.ok(added ? "added" : "removed");
     }

  
}
