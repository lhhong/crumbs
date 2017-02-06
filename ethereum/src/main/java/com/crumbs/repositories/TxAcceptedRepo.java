package com.crumbs.repositories;

import com.crumbs.models.TxAccepted;
import com.crumbs.models.TxSent;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by low on 6/2/17 11:31 PM.
 */
public interface TxAcceptedRepo extends CrudRepository<TxAccepted, String>{
}
