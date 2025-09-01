package com.ofss;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;

@Repository
public class TransactionDAO extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void init() {
        setDataSource(dataSource);
    }
    public JdbcTemplate createJdbcTemplate() {
        return getJdbcTemplate();
    }
    public void saveTransaction(Transaction t) {
        // 1. Get next transaction_id from sequence
        String getIdSQL = "SELECT transaction_seq.NEXTVAL FROM dual";
        Long transactionId = createJdbcTemplate().queryForObject(getIdSQL, Long.class);

        // 2. Insert into transactions table
        String insertSQL = "INSERT INTO transactions "
            + "(transaction_id, stock_id, customer_id, transaction_type, transaction_price, volume, transaction_time) "
            + "VALUES (?,?,?,?,?,?,?)";
        createJdbcTemplate().update(
            insertSQL,
            transactionId,
            t.getStockId(),
            t.getCustomerId(),
            t.getTransactionType(),
            t.getTransactionPrice(),
            t.getVolume(),
            (t.getTransactionTime() != null) ? Timestamp.valueOf(t.getTransactionTime()) : new Timestamp(System.currentTimeMillis())
        );
    }
}