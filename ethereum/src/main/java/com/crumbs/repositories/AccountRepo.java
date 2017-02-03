package com.crumbs.repositories;

import com.crumbs.models.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by low on 3/2/17 10:43 PM.
 */
@Repository
public interface AccountRepo extends CrudRepository<Account, Integer> {
}
