package vn.iotstar.security.service;

import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.Review;

public interface ReviewService {
	boolean existsByOrderId(Integer orderId);
	Review getReviewByOrderId(Integer orderId);
	void updateReview(Integer reviewId, String comment, MultipartFile[] files);
}
