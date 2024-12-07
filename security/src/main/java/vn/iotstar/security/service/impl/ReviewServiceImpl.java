package vn.iotstar.security.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.Review;
import vn.iotstar.security.repository.CategoryRepository;
import vn.iotstar.security.repository.ReviewRepository;
import vn.iotstar.security.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService{
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	FileStorageService fileStorageService;
	@Override
	public boolean existsByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		return reviewRepository.existsByOrderId(orderId);
	}

	@Override
	public Review getReviewByOrderId(Integer orderId) {
		return reviewRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Review not found for orderId: " + orderId));
	}

	@Override
	public void updateReview(Integer reviewId, String comment, MultipartFile[] files) {
		Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Update comment
        review.setComment(comment);

        // Process file uploads if any
        if (files != null && files.length > 0) {
            List<String> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                String filePath = fileStorageService.storeFile(file);
                uploadedFiles.add(filePath);
            }
            review.setFileUrls(uploadedFiles); // Update file URLs
        }

        reviewRepository.save(review); // Save updated review
		
	}

}