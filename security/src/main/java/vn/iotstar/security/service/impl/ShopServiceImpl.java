package vn.iotstar.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.security.model.Shop;
import vn.iotstar.security.repository.ShopRepository;
import vn.iotstar.security.service.ShopService;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Override
    public Shop getShopByOwnerEmail(String email) {
        return shopRepository.findByOwnerEmail(email); // Return null if no shop exists
    }


    @Override
    public Shop updateShopDetails(Shop shop) {
        
        return shopRepository.save(shop);
    }

    @Override
    public boolean doesShopExistForOwner(String email) {
        return shopRepository.existsByOwnerEmail(email);
    }

    @Override
    public Shop saveShop(Shop shop) {
        if (doesShopExistForOwner(shop.getOwner().getEmail())) {
            throw new RuntimeException("A shop already exists for the owner email: " + shop.getOwner().getEmail());
        }
        return shopRepository.save(shop);
    }


    @Transactional
    @Override
    public void updateShopSoldAndRevenue(Integer shopId, Double revenue,int quantity) {
        shopRepository.updateShopSoldAndRevenue(shopId, revenue, quantity);
    }


	@Override
	public List<Shop> getAll() {
		return shopRepository.findAll();
	}

}

