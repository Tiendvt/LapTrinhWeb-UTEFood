package vn.iotstar.security.service;

import vn.iotstar.security.model.Shop;

public interface ShopService {

    Shop getShopByOwnerEmail(String email);

    Shop updateShopDetails(Shop shop);

    boolean doesShopExistForOwner(String email);

    Shop saveShop(Shop shop);
    void updateShopSoldAndRevenue(Integer shopId, Double revenue);
}




