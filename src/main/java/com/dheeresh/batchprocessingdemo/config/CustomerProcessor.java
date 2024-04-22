package com.dheeresh.batchprocessingdemo.config;

import com.dheeresh.batchprocessingdemo.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

        /*if(customer.getCountry().equalsIgnoreCase("United States of America")){
            return customer;
        }else {
            return null;
        }*/
        return customer;
    }
}
