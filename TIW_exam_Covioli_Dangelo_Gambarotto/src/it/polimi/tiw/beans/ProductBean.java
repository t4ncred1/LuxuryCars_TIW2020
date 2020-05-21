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
	
}
