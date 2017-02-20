package com.crumbs.repositories;

import com.crumbs.entities.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by low on 7/2/17 12:05 PM.
 */
@Repository
public interface MemberRepo extends CrudRepository<Member, byte[]>{
	List<Member> findByOwn(boolean own);
}
