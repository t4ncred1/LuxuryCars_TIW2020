package it.polimi.tiw.beans;

import java.util.List;

public class QuotationBean {

	private int quotationId;
	private int clientId;
	private String clientUsername;
	private int workerId;
	private String workerUsername;
	private String date;
	private int productId;
	private String productName;
	private List<String> options;
	private int value;
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(int quotationId) {
		this.quotationId = quotationId;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getClientUsername() {
		return clientUsername;
	}
	public void setClientUsername(String clientUsername) {
		this.clientUsername = clientUsername;
	}
	public int getWorkerId() {
		return workerId;
	}
	public void setWorkerId(int workerId) {
		this.workerId = workerId;
	}
	public String getWorkerUsername() {
		return workerUsername;
	}
	public void setWorkerUsername(String workerUsername) {
		this.workerUsername = workerUsername;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	
}
