package com.ofss;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

@Repository
public class StocksDAO extends JdbcDaoSupport {

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void init() {
        setDataSource(dataSource);
        System.out.println("Stock DAO initialized with datasource " + dataSource);
    }

    public JdbcTemplate giveJdbcTemplate() {
        return getJdbcTemplate();
    }

    public void addAStock(Stocks s) { 
    	String sql = "INSERT INTO stock (STOCK_ID, STOCK_NAME, STOCK_PRICE, STOCK_VOLUME, LISTED_PRICE, LISTED_DATE, LISTED_EXCHANGE) VALUES (?,?,?,?,?,?,?)";
        giveJdbcTemplate().update(sql, s.getStockId(), s.getStockName(), s.getStockPrice(), s.getStockVolume(),
                s.getListedPrice(), s.getListedDate(), s.getListedExchange());
    }

    public ArrayList<Stocks> getAllStocks() {
        String sql = "SELECT * FROM stock";
        List<Stocks> stocks = giveJdbcTemplate().query(sql, stockRowMapper);
        return new ArrayList<>(stocks);
    }

    public Stocks getStockById(int id) {
        String sql = "SELECT * FROM stock WHERE STOCK_ID = ?";
        return giveJdbcTemplate().queryForObject(sql, stockRowMapper, id);
    }

    public ResponseEntity<Object> deleteAStockById(int sid) {
        int rowsAffected = giveJdbcTemplate().update("DELETE FROM stock WHERE STOCK_ID = ?", sid);
        if (rowsAffected > 0)
            return ResponseEntity.status(200).body("Stock deleted successfully");
        else
            return ResponseEntity.status(404).body("Stock not found");
    }

    public ResponseEntity<Object> updateStock(int sid, Stocks s) {
        String sql = "UPDATE stock SET STOCK_NAME = ?, STOCK_PRICE = ?, STOCK_VOLUME = ?, LISTED_PRICE = ?, LISTED_DATE = ?, LISTED_EXCHANGE = ? WHERE STOCK_ID = ?";
        int rowsAffected = giveJdbcTemplate().update(sql, s.getStockName(), s.getStockPrice(), s.getStockVolume(),
                s.getListedPrice(), s.getListedDate(), s.getListedExchange(), sid);
        if (rowsAffected > 0) {
            return ResponseEntity.status(200).body("Stock updated successfully");
        } else {
            return ResponseEntity.status(404).body("Stock not found");
        }
    }

    public ResponseEntity<Object> patchStock(int sid, Stocks s) {
        StringBuilder sql = new StringBuilder("UPDATE stock SET ");
        List<Object> params = new ArrayList<>();
        if (s.getStockName() != null) {
            sql.append("STOCK_NAME = ?, ");
            params.add(s.getStockName());
        }
        if (s.getStockPrice() != 0) {
            sql.append("STOCK_PRICE = ?, ");
            params.add(s.getStockPrice());
        }
        if (s.getStockVolume() != 0) {
            sql.append("STOCK_VOLUME = ?, ");
            params.add(s.getStockVolume());
        }
        if (s.getListedPrice() != 0) {
            sql.append("LISTED_PRICE = ?, ");
            params.add(s.getListedPrice());
        }
        if (s.getListedDate() != null) {
            sql.append("LISTED_DATE = ?, ");
            params.add(s.getListedDate());
        }
        if (s.getListedExchange() != null) {
            sql.append("LISTED_EXCHANGE = ?, ");
            params.add(s.getListedExchange());
        }
        if (params.isEmpty()) {
            return ResponseEntity.status(400).body("No data provided to update");
        }
        // Remove trailing comma and space
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE STOCK_ID = ?");
        params.add(sid);

        int rowsAffected = giveJdbcTemplate().update(sql.toString(), params.toArray());
        if (rowsAffected > 0) {
            return ResponseEntity.status(200).body("Stock patched successfully");
        } else {
            return ResponseEntity.status(404).body("Stock not found");
        }
    }

    // RowMapper for Stocks
    private RowMapper<Stocks> stockRowMapper = (rs, rowNum) -> {
        Stocks stock = new Stocks();
        stock.setStockId(rs.getInt("STOCK_ID"));
        stock.setStockName(rs.getString("STOCK_NAME"));
        stock.setStockPrice(rs.getDouble("STOCK_PRICE"));
        stock.setStockVolume(rs.getInt("STOCK_VOLUME"));
        stock.setListedPrice(rs.getDouble("LISTED_PRICE"));
        stock.setListedDate(rs.getDate("LISTED_DATE"));
        stock.setListedExchange(rs.getString("LISTED_EXCHANGE"));
        return stock;
    };
}