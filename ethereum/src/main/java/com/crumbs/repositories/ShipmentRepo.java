package com.crumbs.repositories;

import com.crumbs.entities.Product;
import com.crumbs.entities.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by low on 17/2/17 3:25 PM.
 */
@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
	List<Shipment> findByProductAndQuantityNotAndExpiryAfter(Product product, int quantity, Date expiry);
	List<Shipment> findByProductAndQuantityNotAndExpiryAfterAndShipDateBefore(Product product, int quantity, Date expiry, Date shipDate);
}
