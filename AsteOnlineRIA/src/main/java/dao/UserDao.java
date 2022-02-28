package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import beans.User;

public class UserDao {
	private Connection con;

	public UserDao(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  userId,  name, surname, username FROM user  WHERE username = ? AND password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setUserId(result.getInt("userId"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	
	public User findUserById(int userId) throws SQLException {
		User user = new User();
		String query = "SELECT  userId,  name, indirizzo FROM user  WHERE UserId = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					user.setName(result.getString("name"));
					user.setIndirizzo(result.getString("indirizzo"));
					
				}
			}
		}
		return user;
	}
	
	
	
}
