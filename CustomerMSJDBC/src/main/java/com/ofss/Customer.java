package com.ofss;

public class Customer {
	private int customerId;
	private String firstName;
	private String lastName;
	private long phoneNumber;
	private String emailId;
	private String password;
	private int stockIds[];
	
	private static final String admin_email = "admin@oracle.com";
	private static final String admin_password = "admin#123";
	
	public static String getAdminEmail() {
		return admin_email;
	}

	public static String getAdminPassword() {
		return admin_password;
	}

	public Customer(int customerId, String firstName, String lastName, long phoneNumber, String emailId,
			int[] stockIds, String password) {
		super();
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.emailId = emailId;
		this.stockIds = stockIds;
		this.setPassword(password);
	}

	public Customer() {
		// TODO Auto-generated constructor stub
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public int[] getStockIds() {
		return stockIds;
	}

	public void setStockIds(int[] stockIds) {
		this.stockIds = stockIds;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
