package com.dheeresh.batchprocessingdemo.repository;

import com.dheeresh.batchprocessingdemo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
