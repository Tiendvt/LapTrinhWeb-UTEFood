package vn.iotstar.security.service;

import vn.iotstar.security.model.Shop;

public interface ShopService {

    Shop getShopByOwnerEmail(String email);

    Shop updateShopDetails(Shop shop);
}




