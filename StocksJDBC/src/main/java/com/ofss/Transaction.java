package com.ofss;

import java.time.LocalDate;

public class Transaction {
	private int txnId;
	private int customerId;
	private int stockId;
	private LocalDate transactionDate;
	private String transactionType;
	
	public Transaction(int txnId, int customerId, int stockId, LocalDate transactionDate, String transactionType) {
		this.txnId = txnId;
		this.customerId = customerId;
		this.stockId = stockId;
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
	}

	public int getTxnId() {
		return txnId;
	}

	public void setTxnId(int txnId) {
		this.txnId = txnId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	
}
