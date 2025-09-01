package com.ofss;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {

	@Autowired
	CustomerDAO customerDAO;
	@Autowired
	RestTemplate restTemplate;

	public boolean addNewCustomer(Customer c) {
		try {
	        customerDAO.addNewCustomer(c);
	        return true;   // Success
	    } catch (Exception ex) {
	        // Optionally log the exception
	        return false;  // Failure
	    }
	}
	
	public Customer getCustomerDetailsByEmail(String email) {
	    return customerDAO.getCustomerDetailsByEmail(email);
	}
	
	public boolean validateLogin(String email, String password) {
	    return customerDAO.validateLogin(email, password);
	}
	
	public List<CustomerStockDetail> getCustomerStockDetails(int customerId) {
        List<Transaction> allTransactions = customerDAO.getAllTransactionsForCustomer(customerId);
        // Group transactions by stock_id
        Map<Long, List<Transaction>> grouped = allTransactions.stream()
            .collect(Collectors.groupingBy(Transaction::getStockId, LinkedHashMap::new, Collectors.toList()));

        List<CustomerStockDetail> result = new ArrayList<>();
        for (Map.Entry<Long, List<Transaction>> entry : grouped.entrySet()) {
            List<Transaction> txns = entry.getValue();
            int currentVolume = LofoCostBasisCalculator.getCurrentVolume(txns);
            if (currentVolume <= 0) continue; // skip stocks fully sold

            double netInvested = LofoCostBasisCalculator.calculateNetInvested(txns);
            Stocks stock = getStockById(entry.getKey());
            CustomerStockDetail detail = new CustomerStockDetail();
            detail.setStockId(stock.getStockId());
            detail.setStockName(stock.getStockName());
            detail.setCurrentVolume(currentVolume);
            detail.setNetInvested(netInvested);
            detail.setCurrentPrice(stock.getStockPrice());
            detail.setCurrentValue(currentVolume * stock.getStockPrice());
            result.add(detail);
        }
        return result;
    }
	
	public Stocks getStockById(long stockId) {
		Stocks stock = restTemplate.getForObject(
		        "http://STOCKSMSJDBC/stock/{stockId}", 
		        Stocks.class, 
		        stockId
		    );
		System.out.println(stock);

		    // Make sure to check for nulls to avoid NullPointerException
		    if (stock != null) {
		        return stock; // Use your getter for stock price
		    } else {
		        // Handle the case where no stock was found; you can throw or return a default
		        throw new RuntimeException("Stock not found for ID: " + stockId);
		    }  
	}
	
}
