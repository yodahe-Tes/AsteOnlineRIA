package beans;

import java.sql.Date;

public class User {

	private int userId;
	private String username;
	private String name;
	private String surname;
	private Date LoginTime;
	private String indirizzo;


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getLoginTime() {
		return LoginTime;
	}

	public void setLoginTime(Date loginTime) {
		LoginTime = loginTime;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

}
