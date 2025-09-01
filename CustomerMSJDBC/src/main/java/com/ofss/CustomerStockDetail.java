package com.ofss;

public class CustomerStockDetail {
    public CustomerStockDetail() {
		super();
	}
	public CustomerStockDetail(int stockId, String stockName, Integer currentVolume, Double netInvested,
			Double currentPrice, Double currentValue) {
		super();
		this.stockId = stockId;
		this.stockName = stockName;
		this.currentVolume = currentVolume;
		this.netInvested = netInvested;
		this.currentPrice = currentPrice;
		this.currentValue = currentValue;
	}
	private int stockId;
    private String stockName;
    private Integer currentVolume;
    private Double netInvested;
    private Double currentPrice;  // from stocks table
    private Double currentValue;  // currentVolume * currentPrice
    // Getters & setters...
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
	public Integer getCurrentVolume() {
		return currentVolume;
	}
	public void setCurrentVolume(Integer currentVolume) {
		this.currentVolume = currentVolume;
	}
	public Double getNetInvested() {
		return netInvested;
	}
	public void setNetInvested(Double netInvested) {
		this.netInvested = netInvested;
	}
	public Double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}
	public Double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(Double currentValue) {
		this.currentValue = currentValue;
	}
	@Override
	public String toString() {
		return "CustomerStockDetail [stockId=" + stockId + ", stockName=" + stockName + ", currentVolume="
				+ currentVolume + ", netInvested=" + netInvested + ", currentPrice=" + currentPrice + ", currentValue="
				+ currentValue + "]";
	}
}