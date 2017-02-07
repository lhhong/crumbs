package com.crumbs.repositories;

import com.crumbs.models.TxSent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by low on 6/2/17 11:31 PM.
 */
@Repository
public interface TxSentRepo extends CrudRepository<TxSent, String>{
	List<TxSent> findByIncludedAndPending(boolean included, boolean pending);
	List<TxSent> findByIncluded(boolean included);
}
