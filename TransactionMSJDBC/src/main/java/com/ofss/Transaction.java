package com.ofss;
import java.time.LocalDateTime;

public class Transaction {
    private Long transactionId;
    private Long stockId;
    private Long customerId;
    private String transactionType;     // "BUY" or "SELL"
    private Double transactionPrice;
    private Integer volume;
    private LocalDateTime transactionTime;

    public Transaction() {}

    public Transaction(Long transactionId, Long stockId, Long customerId, String transactionType,
                       Double transactionPrice, Integer volume, LocalDateTime transactionTime) {
        this.transactionId = transactionId;
        this.stockId = stockId;
        this.customerId = customerId;
        this.transactionType = transactionType;
        this.transactionPrice = transactionPrice;
        this.volume = volume;
        this.transactionTime = transactionTime;
    }

    // Getters and setters

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(Double transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}