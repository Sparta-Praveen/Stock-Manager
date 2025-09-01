package com.ofss;

import java.time.LocalDate;

public class Stock extends Object{
	
	private int stockId;	
	private String stockName;	
	private double stockPrice;	
	private long stockVolume;	
	private long listingPrice;	
	private LocalDate listedDate;
	private String stockExchange;
	
	
	public Stock() {
		this.stockId=0;
		this.stockName=null;
		this.stockPrice=0.0;
		this.stockVolume=0L;
		this.listingPrice=0L;
		this.listedDate=LocalDate.now();
		this.stockExchange=null;
	}
	
	public Stock(int stockId,String stockName,double stockPrice,long stockVolume,
			long listingPrice,LocalDate listedDate,String listedExchange) {
		this.stockId=stockId;
		this.stockName=stockName;
		this.stockPrice=stockPrice;
		this.stockVolume=stockVolume;
		this.listingPrice=listingPrice;
		this.listedDate=listedDate;
		this.stockExchange=listedExchange;
	}
	
	
	public int getStockId() {
		return stockId;
	}
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public double getStockPrice() {
		return stockPrice;
	}
	public void setStockPrice(double stockPrice) {
		this.stockPrice = stockPrice;
	}
	public long getStockVolume() {
		return stockVolume;
	}
	public void setStockVolume(long stockVolume) {
		this.stockVolume = stockVolume;
	}
	public long getListingPrice() {
		return listingPrice;
	}
	public void setListingPrice(long listingPrice) {
		this.listingPrice = listingPrice;
	}
	public LocalDate getListedDate() {
		return listedDate;
	}
	public void setListedDate(LocalDate listedDate) {
		this.listedDate = listedDate;
	}
	public String getStockExchange() {
		return stockExchange;
	}

	public void setStockExchange(String stockExchange) {
		this.stockExchange = stockExchange;
	}

	
	
//	@Override
//	public String toString() {
//		System.out.println("toString method is called");
//		return null;
//	}
}
