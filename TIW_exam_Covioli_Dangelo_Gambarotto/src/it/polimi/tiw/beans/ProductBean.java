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

/* ProductBean class
 * This class represents a single product
 */

package it.polimi.tiw.beans;

import java.util.List;

public class ProductBean {
	
	private int id;
	private String name;
	private List<OptionBean> options;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String string) {
		this.name = string;
	}
	public List<OptionBean> getOptions() {
		return options;
	}
	public void setOptions(List<OptionBean> options) {
		this.options = options;
	}
	
	public boolean isAValidOption(int optionId){
		for(OptionBean o : options) {
			if(o.getId()==optionId) return true;
		}
		return false;
	}
	
}