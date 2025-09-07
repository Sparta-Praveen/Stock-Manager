package com.ofss;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.netflix.spectator.impl.PatternExpr.And;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


@Repository
public class CustomerDAO extends JdbcDaoSupport{

	@Autowired
	DataSource dataSource;
	
	@PostConstruct
	public void init() {
		setDataSource(dataSource);
		System.out.println("Customer DAO initialized "+ dataSource);
	}
	
	public JdbcTemplate createJdbcTemplate() {
		return getJdbcTemplate();
	}

	public void addNewCustomer(Customer c) {
	    // 1. Get next customer_id from sequence
	    String getIdSQL = "SELECT customer_seq.NEXTVAL FROM dual";
	    Long customerId = createJdbcTemplate().queryForObject(getIdSQL, Long.class);

	    // 2. Insert into customers
	    String insertCustomer = "INSERT INTO customers (customer_id, first_name, last_name, phone_number, email) VALUES (?,?,?,?,?)";
	    createJdbcTemplate().update(insertCustomer, customerId, c.getFirstName(), c.getLastName(), c.getPhoneNumber(), c.getEmailId());

	    // 3. Insert into credentials (use the same customer ID)
	    String insertCredentials = "INSERT INTO credentials (customer_id, email, password) VALUES (?,?,?)";
	    createJdbcTemplate().update(insertCredentials, customerId, c.getEmailId(), c.getPassword());
		
	}
	
	public List<Customer> getAllCustomers(){
		String sql="SELECT customer_id, first_name, last_name,  phone_number,email FROM customers";
		return createJdbcTemplate().query(sql, (rs,rowNum) -> {
			Customer c= new Customer();
			c.setCustomerId(rs.getInt("customer_id"));
			c.setFirstName(rs.getString("first_name"));
			c.setLastName(rs.getString("last_name"));
			c.setPhoneNumber(rs.getLong("phone_number")); // or setPhone(rs.getString("phone"))
			c.setEmailId(rs.getString("email")); // or rs.getString("mail") if that’s your column
			return c;		
		});
   
	}

	public Customer getCustomerDetailsByEmail(String email) {
	    String sql = "SELECT customer_id, first_name, last_name, phone_number, email FROM customers WHERE email = ?";
	    return createJdbcTemplate().queryForObject(
	        sql,
	        (rs, rowNum) -> {
	            Customer c = new Customer();
	            c.setCustomerId(rs.getInt("customer_id"));
	            c.setFirstName(rs.getString("first_name"));
	            c.setLastName(rs.getString("last_name"));
	            c.setPhoneNumber(rs.getLong("phone_number"));
	            c.setEmailId(rs.getString("email"));
	            return c;
	        },
	        email
	    );
	}
	
	public boolean validateLogin(String email, String password) {
		String sql = "SELECT COUNT(*) FROM credentials WHERE email = ? AND password = ?";
		Integer count = createJdbcTemplate().queryForObject(sql, Integer.class, email, password);
		return count != null && count > 0;
	}
	
	public List<Transaction> getAllTransactionsForCustomer(int customerId) {
        String sql = "SELECT transaction_id,stock_id, customer_id,transaction_type, transaction_price, volume, transaction_time " +
                     "FROM transactions WHERE customer_id = ? ORDER BY transaction_id";
        return createJdbcTemplate().query(sql, (rs, rowNum) -> {
            Transaction t = new Transaction();
            t.setTransactionId(rs.getLong("transaction_id"));
            t.setStockId(rs.getLong("stock_id"));
            t.setCustomerId(rs.getLong("customer_id"));
            t.setTransactionType(rs.getString("transaction_type"));
            t.setTransactionPrice(rs.getDouble("transaction_price"));
            t.setVolume(rs.getInt("volume"));
            t.setTransactionTime(rs.getTimestamp("transaction_time").toLocalDateTime());
            return t;
        }, customerId);
    }
	
}
