package it.polimi.tiw.beans;

public class OptionBean {
	
	private int id;
	private String name;
	/*This boolean is 0 if the product is
	 * normal, 1 if it is in offer*/
	private boolean inOffer;
	private int product;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isInOffer() {
		return inOffer;
	}
	public void setInOffer(boolean inOffer) {
		this.inOffer = inOffer;
	}
	public int getProduct() {
		return product;
	}
	public void setProduct(int product) {
		this.product = product;
	}
	
}
