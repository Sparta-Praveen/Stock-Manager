package com.ofss;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	@Autowired
    private RestTemplate restTemplate;
	
	@PostMapping("/transaction")
    public ResponseEntity<String> performTransaction(@RequestBody Transaction t) {
		t.setTransactionPrice(t.getVolume() * getTransactionPrice(t.getStockId()));
		t.setTransactionTime(LocalDateTime.now());
        boolean result = transactionService.performTransaction(t);
        if(result) {
            return ResponseEntity.ok("Transaction successful!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed.");
        }
    }
	
	public double getTransactionPrice(long stockId) {
		Stocks stock = restTemplate.getForObject(
		        "http://STOCKSMSJDBC/stock/{stockId}", 
		        Stocks.class, 
		        stockId
		    );
		System.out.println(stock);

		    // Make sure to check for nulls to avoid NullPointerException
		    if (stock != null) {
		        return stock.getStockPrice(); // Use your getter for stock price
		    } else {
		        // Handle the case where no stock was found; you can throw or return a default
		        throw new RuntimeException("Stock not found for ID: " + stockId);
		    }  
	}

}
