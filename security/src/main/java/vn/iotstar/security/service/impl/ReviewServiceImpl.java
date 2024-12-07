package vn.iotstar.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.security.repository.CategoryRepository;
import vn.iotstar.security.repository.ReviewRepository;
import vn.iotstar.security.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService{
	@Autowired
	private ReviewRepository reviewRepository;

	@Override
	public boolean existsByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		return reviewRepository.existsByOrderId(orderId);
	}

}
