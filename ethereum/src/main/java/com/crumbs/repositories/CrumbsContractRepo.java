package com.crumbs.repositories;

import com.crumbs.models.CrumbsContract;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by low on 4/2/17 3:44 PM.
 */
@Repository
public interface CrumbsContractRepo extends CrudRepository<CrumbsContract, String>{
}
