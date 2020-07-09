/*  _______ _______          __                                    
 * |__   __|_   _\ \        / /                                    
 *    | |    | |  \ \  /\  / /                                     
 *    | |    | |   \ \/  \/ /                                      
 *    | |   _| |_   \  /\  /                                       
 *    |_|  |_____|   \/  \/   
 * 
 * exam project - a.y. 2019-2020
 * Politecnico di Milano
 * 
 * Tancredi Covioli   mat. 944834
 * Alessandro Dangelo mat. 945149
 * Luca Gambarotto    mat. 928094
 */

/* OptionBean class
 * This class represents a single option
 */

package it.polimi.tiw.beans;

public class OptionBean {
	
	private int id;
	/* the name parameter may be in
	 * different languages. Each OptionBean
	 * is associated to a single language only */
	private String name;
	/*T his boolean is 0 if the product is
	 * normal, 1 if it is in offer */
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