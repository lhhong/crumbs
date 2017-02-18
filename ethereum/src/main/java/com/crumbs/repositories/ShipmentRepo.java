package com.crumbs.repositories;

import com.crumbs.models.Product;
import com.crumbs.models.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by low on 17/2/17 3:25 PM.
 */
@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
	List<Shipment> findByProductAndQuantityNotAndExpiryAfter(Product product, int quantity, LocalDate expiry);
	List<Shipment> findByProductAndQuantityNotAndExpiryAfterAndShipDateBefore(Product product, int quantity, LocalDate expiry, LocalDate shipDate);
}
