package com.crumbs.repositories;

import com.crumbs.entities.Product;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by low on 16/2/17 11:26 PM.
 */
public interface ProductRepo extends CrudRepository<Product, String>{
}
