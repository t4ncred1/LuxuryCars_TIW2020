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
	
	
	/* This function returns a list of quotations that have not
	 * been priced yet. The language in which the options are written
	 * is the one specified in the parameter */
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
				closeConnection(result, pstatement);
			}
		}
		return freeQuotations;
	}
	
	
	/* This function return a list containing all the quotations that
	 * have been managed by worker specified as parameter. */
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
				closeConnection(result, pstatement);
			}
		}
		return workerQuotations;
	}
	
	
	/* This function returns a list of all the quotation requests
	 * submitted by the user specified as parameter */
	public List<QuotationBean> getClientQuotations(int clientId, String language) throws SQLException {
		List<QuotationBean> clientQuotations = new ArrayList<>();
		String query = "SELECT Q.quotationId, Q.price, Q.date, Q.productId, P.name, Q.workerId "
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
					quotation.setWorkerId(result.getInt("Q.workerId"));
					quotation.setValue(Double.valueOf(result.getInt("Q.price"))/100);
					opDAO.getOptionByQuotation(qID, language);
					clientQuotations.add(quotation);
				}
				closeConnection(result, pstatement);
			}
		}
		return clientQuotations;
	}
	
	
	/* This function returns the quotationBean related to the
	 * quotation whose id is specified in the parameter. */
	public QuotationBean getQuotationById(int quotationId, String language) throws SQLException{
		String query = "SELECT Q.quotationId, Q.date, Q.productId, P.name, Q.clientId, C.username, Q.price, Q.workerId "
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
			quotation.setWorkerId(result.getInt("Q.workerId"));
			quotation.setProductName(result.getNString("P.name"));
			if (result.getInt("Q.price") != 0) {
				quotation.setValue(Double.valueOf(result.getInt("Q.price"))/100);
			}
			quotation.setOptions(opDAO.getOptionByQuotation(quotationId, language));
		}
		closeConnection(result, pstatement);
		return quotation;
	}
	
	
	/* This function allows to set a price to a quotation. It also
	 * update the workerId field with the id of the worker that submitted the
	 * quotation price. */
	public void setQuotationPrice(int quotationId, int price, int workerId) throws SQLException{
		String query = "UPDATE quotation SET price = ?, workerId = ? WHERE quotationId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setInt(1, price);
			pstatement.setInt(2, workerId);
			pstatement.setInt(3, quotationId);
			pstatement.executeUpdate();
			closeConnection(pstatement);
		}
	}
	
	
	/* This function allows to add a new quotation request, specifying the
	 * id of the client that submitted it, the id of the product it is related to
	 * and the list with the options selected by the user. */
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
				closeConnection(result, pstatement);
			}
		}		
		
		for(Integer o : options) {
			insertion = "INSERT into quotation_option (optionId, quotationId)   VALUES(?, ?)";
			try (PreparedStatement pstatement = con.prepareStatement(insertion);) {
				pstatement.setInt(1, o);
				pstatement.setInt(2, id);
				pstatement.executeUpdate();
				closeConnection(pstatement);
			}
		}
		
	}
	
	
	private void closeConnection(PreparedStatement pStatement) throws SQLException {
		try {
			pStatement.close();
		} catch (SQLException e) {
			throw new SQLException("Cannot close statement");
		}
	}

	private void closeConnection(ResultSet res, PreparedStatement pStatement) throws SQLException {
		try {
			res.close();
		} catch (SQLException e) {
			throw new SQLException("Cannot close result");
		}
		try {
			pStatement.close();
		} catch (SQLException e) {
			throw new SQLException("Cannot close statement");
		}
	}

	
}