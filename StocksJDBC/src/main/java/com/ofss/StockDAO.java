package com.ofss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.PostConstruct;
// Convenient superclass for JDBC-based data access objects. 
// Requires a DataSource to be set, providing a JdbcTemplate based on it to subclasses.
// If you use this annotation before any class, Spring container will automatically create an object
// as a part of IoC
@Repository
public class StockDAO extends JdbcDaoSupport{

	// This object will be created by Spring Container (IoC)
	@Autowired
	DataSource dataSource;
	
	// This method will be called after the object of this class is created
	@PostConstruct
	public void init() {
		// This method is available in the JdbcDaoSupport class
		setDataSource(dataSource);
		System.out.println("StockDAO initialized with datasource: " + dataSource);
    }
	
	public JdbcTemplate giveJdbcTemplate() {
		return getJdbcTemplate(); // returns an object of JdbcTemplate class
		
	}
	
	//adding a single stock
	public ResponseEntity<Object> addAStock(Stock newStock) {
		JdbcTemplate jdbcTemplate=giveJdbcTemplate();
		int rowsAffected=jdbcTemplate.update("insert into stocks values(?,?,?,?)",newStock.getStockId(),newStock.getStockName(),newStock.getStockPrice(),newStock.getStockExchange());
		if (rowsAffected>0)
            return ResponseEntity.status(201).body("Stock added successfully");
		else
			return ResponseEntity.status(500).body("Failed to add stock");
	} 
	
	//fetching all stocks
	public ResponseEntity<ArrayList<Stock>> fetchAllStocks() {
		ArrayList<Stock> allFetchedStocks=new ArrayList<Stock>();
		JdbcTemplate jdbcTemplate=giveJdbcTemplate();
		String sql="SELECT * FROM STOCKS";
		List<Map<String, Object>> allStocks =jdbcTemplate.queryForList(sql);
		// returns a list of Map objects
		// Every element is a map, with String as a key and Object as a value
		// Iterating the list of Map objects, every map is a row
		for (Map<String, Object> st:allStocks) {
			// For each and every row, I should create an object of Stock class
			Stock s=new Stock();// empty stock object
			int stockId=Integer.valueOf(((java.math.BigDecimal)st.get("STOCK_ID")).toString());
			s.setStockId(stockId);
			String stockName=(String)st.get("STOCK_NAME");
			s.setStockName(stockName);
			double stockPrice=Double.valueOf(((java.math.BigDecimal)st.get("STOCK_PRICE")).toString());
			s.setStockPrice(stockPrice);
			s.setStockExchange((String)st.get("EXCHANGE_NAME"));
			allFetchedStocks.add(s);
		}
		return ResponseEntity.status(200).body(allFetchedStocks);
	}
	
	//deleting a stock by id
	public ResponseEntity<Object> deleteAStockById(int sid) {
		JdbcTemplate jdbcTemplate = giveJdbcTemplate();
		int rowsAffected = jdbcTemplate.update("DELETE FROM STOCKS WHERE STOCK_ID=?", sid);
		if (rowsAffected > 0)
			return ResponseEntity.status(200).body("Stock deleted successfully");
		else
			return ResponseEntity.status(404).body("Stock not found");
	}

	//fetching a single stock by id
	public ResponseEntity<Object> fetchAStockById(int sid){
		JdbcTemplate jdbcTemplate = giveJdbcTemplate();
		int rowsAffeted = jdbcTemplate.update("");
	}

}
