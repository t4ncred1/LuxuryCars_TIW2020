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

import it.polimi.tiw.beans.*;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	/* This function checks if the username and the password passed
	 * as paramters correspond to a user registered in the system. If the
	 * credentials are correct the related UserBean is returned, otherwise
	 * a null object is returned; */
	public UserBean checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  userid, username, role, name, surname, email FROM users  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) { // no results, credential check failed
					closeConnection(result, pstatement);
					return null;
				} else {
					result.next();
					UserBean user = new UserBean();
					user.setUserid(result.getInt("userid"));
					user.setRole(result.getString("role"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setEmail(result.getString("email"));
					closeConnection(result, pstatement);
					return user;
				}
			}
		}
	}

	/* This function returns true if the username passed as parameter
	 * corresponds to a GENERIC USER registered in the system. */
	public boolean existsUser(String usrn) throws SQLException {
		String query = "SELECT userid FROM users  WHERE username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) {
					/*
					 * The isBeforeFirst() method of the ResultSet interface is used to determine
					 * whether the cursor is at the default position of the ResultSet. If the cursor
					 * is at the default position means that no user has been found with the
					 * required username
					 */
					closeConnection(result, pstatement);
					return false;
				} else {
					closeConnection(result, pstatement);
					return true;
				}
			}
		}
	}

	/* This function returns true if the username passed as parameter
	 * corresponds to a WORKER registered in the system. */
	public boolean existsWorker(String usrn) throws SQLException {
		String query = "SELECT idworker FROM worker  WHERE username = ?";
		PreparedStatement pStatement = con.prepareStatement(query);
		pStatement.setString(1, usrn);
		ResultSet result = pStatement.executeQuery();
		if (!result.isBeforeFirst()) {
			closeConnection(result, pStatement);
			return false;
		} else {
			closeConnection(result, pStatement);
			return true;
		}
	}

	/* This function returns true if the username passed as parameter
	 * corresponds to a CLIENT registered in the system. */
	public boolean existsClient(String usrn) throws SQLException {
		String query = "SELECT idclient FROM client  WHERE username = ?";
		PreparedStatement pStatement = con.prepareStatement(query);
		pStatement.setString(1, usrn);
		ResultSet result = pStatement.executeQuery();
		if (!result.isBeforeFirst()) {
			closeConnection(result, pStatement);
			return false;
		} else {
			closeConnection(result, pStatement);
			return true;
		}
	}

	/* This function allows to register a new user in the system,
	 * specifying its personal information and its role. */
	public void registerUser(String usrn, String pwd, String firstName, String lastName, String role, String email)
			throws SQLException {
		String query = "INSERT INTO " + role + " (username, password, name, surname, email) VALUES (?, ?, ?, ?, ?)";

		PreparedStatement pStatement = con.prepareStatement(query);
		pStatement.setString(1, usrn);
		pStatement.setString(2, pwd);
		pStatement.setString(3, firstName);
		pStatement.setString(4, lastName);
		pStatement.setString(5, email);
		pStatement.execute();

		closeConnection(pStatement);
		return;
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
