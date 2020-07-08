package it.polimi.tiw.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;

public class QuotationDAO {
	private Connection con;

	public QuotationDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<QuotationBean> getFreeQuotations(String language) throws SQLException {
		List<QuotationBean> freeQuotations = new ArrayList<>();
		String query = "SELECT Q.quotationId, Q.date, Q.productId, P.name,	Q.clientId, C.username "
							+ "FROM quotation AS Q, client AS C, product AS P "
							+ "WHERE Q.productId = P.productId AND C.idclient = Q.clientId AND Q.workerid IS NULL";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					QuotationBean quotation = new QuotationBean();
					int qID = result.getInt("Q.quotationId");
					quotation.setQuotationId(qID);
					quotation.setClientId(result.getInt("Q.clientId"));
					quotation.setClientUsername(result.getString("C.username"));
					quotation.setDate(result.getString("Q.Date"));
					quotation.setProductId(result.getInt("Q.productId"));
					quotation.setProductName(result.getNString("P.name"));
					OptionDAO opDAO = new OptionDAO(con);
					quotation.setOptions(opDAO.getOptionByQuotation(qID, language));
					freeQuotations.add(quotation);
				}
			}
		}
		return freeQuotations;
	}
	
	
	public List<QuotationBean> getWorkerQuotations(int workerId, String language) throws SQLException {
		List<QuotationBean> workerQuotations = new ArrayList<>();
		String query = "SELECT Q.quotationId, Q.price, Q.date, Q.productId, P.name,	Q.clientId, C.username, Q.workerId, W.username "
							+ "FROM quotation AS Q, client AS C, product AS P, worker AS W "
							+ "WHERE Q.productId = P.productId AND C.idclient = Q.clientId AND W.idworker = Q.workerId AND Q.workerid = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, workerId);
			try (ResultSet result = pstatement.executeQuery();) {
				OptionDAO opDAO = new OptionDAO(con);
				while(result.next()) {
					QuotationBean quotation = new QuotationBean();
					int qID = result.getInt("Q.quotationId");
					quotation.setQuotationId(qID);
					quotation.setClientId(result.getInt("Q.clientId"));
					quotation.setClientUsername(result.getString("C.username"));
					quotation.setDate(result.getString("Q.Date"));
					quotation.setProductId(result.getInt("Q.productId"));
					quotation.setProductName(result.getNString("P.name"));
					quotation.setValue(Double.valueOf(result.getInt("Q.price"))/100);
					quotation.setWorkerId(result.getInt("Q.workerId"));
					quotation.setWorkerUsername(result.getString("W.username"));
					opDAO.getOptionByQuotation(qID, language);
					workerQuotations.add(quotation);
				}
			}
		}
		return workerQuotations;
	}
	
	
	public List<QuotationBean> getClientQuotations(int clientId, String language) throws SQLException {
		List<QuotationBean> clientQuotations = new ArrayList<>();
		String query = "SELECT Q.quotationId, Q.price, Q.date, Q.productId, P.name "
							+ "FROM quotation AS Q, product AS P "
							+ "WHERE Q.productId = P.productId AND Q.clientId = ? "
							+ "ORDER BY Q.date ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, clientId);
			try (ResultSet result = pstatement.executeQuery();) {
				OptionDAO opDAO = new OptionDAO(con);
				while(result.next()) {
					QuotationBean quotation = new QuotationBean();
					int qID = result.getInt("Q.quotationId");
					quotation.setQuotationId(qID);
					quotation.setClientId(clientId);
					quotation.setDate(result.getString("Q.Date"));
					quotation.setProductId(result.getInt("Q.productId"));
					quotation.setProductName(result.getNString("P.name"));
					quotation.setValue(Double.valueOf(result.getInt("Q.price"))/100);
					opDAO.getOptionByQuotation(qID, language);
					clientQuotations.add(quotation);
				}
			}
		}
		return clientQuotations;
	}
	
	public QuotationBean getQuotationById(int quotationId, String language) throws SQLException{
		String query = "SELECT Q.quotationId, Q.date, Q.productId, P.name, Q.clientId, C.username, Q.price "
				+ "FROM quotation AS Q, client AS C, product AS P "
				+ "WHERE Q.productId = P.productId AND C.idclient = Q.clientId AND Q.quotationId = ?";
		OptionDAO opDAO = new OptionDAO(con);
		PreparedStatement pstatement = con.prepareStatement(query); 
		pstatement.setInt(1, quotationId);
		ResultSet result = pstatement.executeQuery();
		QuotationBean quotation = null;
		if (result.next() != false) {
			quotation = new QuotationBean();
			quotation.setQuotationId(result.getInt("Q.quotationId"));
			quotation.setClientId(result.getInt("Q.clientId"));
			quotation.setClientUsername(result.getString("C.username"));
			quotation.setDate(result.getString("Q.Date"));
			quotation.setProductId(result.getInt("Q.productId"));
			quotation.setProductName(result.getNString("P.name"));
			if (result.getInt("Q.price") != 0) {
				quotation.setValue(Double.valueOf(result.getInt("Q.price"))/100);
			}
			quotation.setOptions(opDAO.getOptionByQuotation(quotationId, language));
		}
		return quotation;
	}
	
	public void setQuotationPrice(int quotationId, int price, int workerId) throws SQLException{
		String query = "UPDATE quotation SET price = ?, workerId = ? WHERE quotationId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setInt(1, price);
			pstatement.setInt(2, workerId);
			pstatement.setInt(3, quotationId);
			pstatement.executeUpdate();
		}
	}
	
	
	public void addQuotation(int clientId, int productId, List<Integer> options) throws SQLException{
		String insertion = "INSERT into quotation (clientId, productId)   VALUES(?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(insertion);) {
			pstatement.setInt(1, clientId);
			pstatement.setInt(2, productId);
			pstatement.executeUpdate();
		}
		
		String lastId = "SELECT LAST_INSERT_ID() AS id FROM quotation";
		int id = 0;
		try (PreparedStatement pstatement = con.prepareStatement(lastId);) {
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					id = result.getInt("id");
				}
			}
		}		
		
		for(Integer o : options) {
			insertion = "INSERT into quotation_option (optionId, quotationId)   VALUES(?, ?)";
			try (PreparedStatement pstatement = con.prepareStatement(insertion);) {
				pstatement.setInt(1, o);
				pstatement.setInt(2, id);
				pstatement.executeUpdate();
			}
		}
		
	}
	
}