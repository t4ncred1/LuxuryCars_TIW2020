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

	public UserBean checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  userid, username, role FROM users  WHERE username = ? AND password =?";
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
					closeConnection(result, pstatement);
					return user;
				}
			}
		}
	}

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

	public void registerUser(String usrn, String pwd, String firstName, String lastName, String role)
			throws SQLException {
		String query = "INSERT INTO " + role + " (username, password, name, surname) VALUES (?, ?, ?, ?)";

		PreparedStatement pStatement = con.prepareStatement(query);
		pStatement.setString(1, usrn);
		pStatement.setString(2, pwd);
		pStatement.setString(3, firstName);
		pStatement.setString(4, lastName);
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
