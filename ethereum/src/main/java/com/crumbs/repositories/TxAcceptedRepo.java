package com.crumbs.repositories;

import com.crumbs.entities.TxAccepted;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by low on 6/2/17 11:31 PM.
 */
@Repository
public interface TxAcceptedRepo extends CrudRepository<TxAccepted, String>{
	List<TxAccepted> findByIncludedAndPendingAndDone (boolean included, boolean pending, boolean done);
	List<TxAccepted> findByIncludedAndDone (boolean included, boolean done);
	List<TxAccepted> findByIncluded (boolean included);
}
