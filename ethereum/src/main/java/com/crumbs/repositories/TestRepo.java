package com.crumbs.repositories;

import com.crumbs.models.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by low on 2/2/17 12:03 PM.
 */

@Repository
public interface TestRepo extends CrudRepository<Test, Long> {
}
