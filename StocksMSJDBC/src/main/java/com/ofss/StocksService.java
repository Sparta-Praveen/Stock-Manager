package com.ofss;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StocksService {
	@Autowired
	StocksDAO stockDAO;
	
	public void addStock(Stocks s) {
		stockDAO.addAStock(s);
		
	}


	public ArrayList<Stocks> getStocks() {
		// TODO Auto-generated method stub
		return stockDAO.getAllStocks();
	}
	
	public Stocks getAStock(int sid) {
		return stockDAO.getStockById(sid);
	}
	public ResponseEntity<Object> deleteAStockById(int sid) {
		return stockDAO.deleteAStockById(sid);
	}


	public ResponseEntity<Object> updateStockById(int sid, Stocks s) {
		return stockDAO.updateStock(sid,s);
	}


	public ResponseEntity<Object> PatchStockById(int sid, Stocks s) {
		// TODO Auto-generated method stub
		return stockDAO.patchStock(sid,s);
	}

}
