package com.ofss;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {
	@Autowired
	StocksService ss;
	@RequestMapping(value = "/stock", method = RequestMethod.POST)
	public String addStock(@RequestBody Stocks s) {
		ss.addStock(s);
		return "Stock Added Successfully";
	}
	@RequestMapping(value = "/stock", method = RequestMethod.GET)
	public ArrayList<Stocks> getStocks() {
		System.out.println("Fetching all stocks...");
		return ss.getStocks(); 
	} 
	@RequestMapping(value="/stock/{stockId}", method=RequestMethod.GET)
	public Stocks getAStock(@PathVariable("stockId") int sid) {
			return ss.getAStock(sid);
	}
	@RequestMapping(value="/stock/{stockId}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteAStockById(@PathVariable("stockId") int sid) {
		System.out.println("Deleting a stock by id called from the controller");
		return ss.deleteAStockById(sid);
	}
	@RequestMapping(value="/stock/{stockId}", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateAStockById(@PathVariable("stockId") int sid,@RequestBody Stocks s) {
		System.out.println("Updated a stock by id");
		return ss.updateStockById(sid,s);
	}
	@RequestMapping(value="/stock/{stockId}", method=RequestMethod.PATCH)
	public ResponseEntity<Object> PatchAStockById(@PathVariable("stockId") int sid,@RequestBody Stocks s) {
		System.out.println("PAtched a stock by id");
		return ss.PatchStockById(sid,s);
	}

}
