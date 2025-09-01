package com.ofss;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import jakarta.annotation.PostConstruct;

public class CustomerCredentialDAO extends JdbcDaoSupport {
	
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

}
