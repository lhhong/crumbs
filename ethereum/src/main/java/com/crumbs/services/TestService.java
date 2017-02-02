package com.crumbs.services;

import com.crumbs.models.Test;
import com.crumbs.repositories.TestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by low on 2/2/17 12:10 PM.
 */
@Service
public class TestService {

	@Autowired
	TestRepo testRepo;

	public Test getById(long id) {
		return testRepo.findOne(id);
	}
}
