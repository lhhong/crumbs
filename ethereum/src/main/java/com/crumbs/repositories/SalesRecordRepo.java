package com.crumbs.repositories;

import com.crumbs.models.Product;
import com.crumbs.models.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by low on 17/2/17 3:04 PM.
 */
@Repository
public interface SalesRecordRepo extends JpaRepository<SalesRecord, Long> {
	List<SalesRecord> findByProductOrderByDateAsc(Product product);
	List<SalesRecord> findByProductOrderByDateBeforeByDateAsc(Product product, Date date);
}
