package vn.iotstar.security.service.impl;

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
        return shopRepository.findByOwnerEmail(email);
    }

    @Override
    public Shop updateShopDetails(Shop shop) {
        return shopRepository.save(shop);
    }
}
