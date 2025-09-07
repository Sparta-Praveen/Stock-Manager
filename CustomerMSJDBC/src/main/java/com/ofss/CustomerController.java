package com.ofss;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	
	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	public ResponseEntity<List<Customer>> getAllCustomers() {
		List<Customer> c = customerService.getAllCustomers();
		if (c != null) {
	        return ResponseEntity.ok(c);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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

	@RequestMapping(
			value = "/customer/login", 
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> loginCustomer(@RequestBody Customer c) {
		String email = c.getEmailId();
		String pwd = c.getPassword();
		
		// admin validation
		if (email != null && pwd != null && Customer.getAdminEmail().equalsIgnoreCase(email) && Customer.getAdminPassword().equals(pwd)) {
			return ResponseEntity.ok(Map.of("message", "Admin login successful!", "role", "admin"));
		}
		
		// customer validation
		boolean valid = customerService.validateLogin(email, pwd);
		if (valid) {		
			return ResponseEntity.ok(Map.of("message", "Login successful!", "role", "customer"));
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid email or password"));
		}
	}
	
	@RequestMapping(value = "/customer/stocksDetails/{customerId}", method = RequestMethod.GET)
	public ResponseEntity<List<CustomerStockDetail>> getCustomerStockDetails(@PathVariable("customerId") int customerId) {
		System.out.println("I am in customer controller");
		List<CustomerStockDetail> details = customerService.getCustomerStockDetails(customerId);
        return ResponseEntity.ok(details);
	}
	
	@RequestMapping(value = "/customer/transactions/{customerId}",method = RequestMethod.GET)
	public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable("customerId") int customerId){
		List<Transaction> transactions=customerService.getAllTransactions(customerId);
		
		return ResponseEntity.ok(transactions);
	}

}
