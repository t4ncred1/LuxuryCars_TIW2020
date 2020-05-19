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
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					UserBean user = new UserBean();
					user.setUserid(result.getInt("userid"));
					user.setRole(result.getString("role"));
					user.setUsername(result.getString("username"));
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
				if (!result.isBeforeFirst())
					/* The isBeforeFirst() method of the ResultSet interface is used to
					 * determine whether the cursor is at the default position of the ResultSet.
					 * If the cursor is at the default position means that no user
					 * has been found with the required username*/
					return false;
				else {
					return true;
				}
			}
		}
	}
	
}
