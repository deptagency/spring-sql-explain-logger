package com.deptagency.sqlexplain.customer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

	Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private CustomerRepository repository;

	@Autowired
	public CustomerController(CustomerRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/customers")
	public String customers() {

		// Customer customer = new Customer("test_fn_insert", "test_save_d");
		// repository.save(customer);
		List<Customer> customers = repository.findByLastName("test_save_d");

		//List<Customer>  allCustomers = repository.findAll();

		Customer customerd = repository.findById(1);

		//allCustomers = repository.findAll();

		return "Greetings from Spring Boot! " + customers;
	}

}
