package com.ofss;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StockService {

	// StockService depends on StockDAO
	@Autowired
	StockDAO stockDAO;
	
	//add single stock
	public ResponseEntity<Object> addAStock(Stock newStock) {
		return stockDAO.addAStock(newStock);
	}
	
	// fetch all stocks
	public ResponseEntity<ArrayList<Stock>> fetchAllStocks() {
		return stockDAO.fetchAllStocks();
	}
	
	//delete a stock by id 
	public ResponseEntity<Object> deleteAStockById(int sid) {
		return stockDAO.deleteAStockById(sid);
	}

}
