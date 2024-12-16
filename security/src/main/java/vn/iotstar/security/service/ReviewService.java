package vn.iotstar.security.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.Review;

public interface ReviewService {
	boolean existsByOrderId(Integer orderId);
	Review getReviewByOrderId(Integer orderId);
	void updateReview(Integer reviewId, String comment, Integer rating, MultipartFile[] files);
	List<Review> findReviewsByProductId(Integer  ProductId);
}
