package com.ofss;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@RequestMapping(value = "/customer", method = RequestMethod.POST)
	public ResponseEntity<String> addNewCustomer(@RequestBody Customer c) {
        boolean isAdded = customerService.addNewCustomer(c);
        if (isAdded) {
            return ResponseEntity.ok("Customer Added Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to add customer");
        }
    }

	@RequestMapping(value = "/customer/{email}", method = RequestMethod.GET)
	public ResponseEntity<Customer> getCustomerDetails(@PathVariable("email") String email) {
	    Customer c = customerService.getCustomerDetailsByEmail(email);
	    if (c != null) {
	        return ResponseEntity.ok(c);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}

	@RequestMapping(value = "/customer/login", method = RequestMethod.POST)
	public ResponseEntity<String> loginCustomer(@RequestBody Customer c) {
	    boolean valid = customerService.validateLogin(c.getEmailId(), c.getPassword());
	    if (valid) {
	        return ResponseEntity.ok("Login successful!");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
	    }
	}
	
	@RequestMapping(value = "/customer/stocksDetails/{customerId}", method = RequestMethod.GET)
	public ResponseEntity<List<CustomerStockDetail>> getCustomerStockDetails(@PathVariable("customerId") int customerId) {
		System.out.println("I am in customer controller");
		List<CustomerStockDetail> details = customerService.getCustomerStockDetails(customerId);
        return ResponseEntity.ok(details);
	}

}
