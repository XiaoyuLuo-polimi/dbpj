package it.polimi.db2.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * The persistent class for the usertable database table.
 * 
 */
@Entity
@Table(name = "user", schema = "db2")
@NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r  WHERE r.username = ?1 and r.password = ?2")
@NamedQuery(name = "User.checkUsername", query = "SELECT r FROM User r  WHERE r.username = ?1")

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String password;

	private String username;

	private int points;

	private String email;

	private int isblocked;

	// Bidirectional many-to-one association to Mission
	/*
	 * Fetch type EAGER allows resorting the relationship list content also in the
	 * client Web servlet after the creation of a new mission. If you leave the
	 * default LAZY policy, the relationship is sorted only at the first access but
	 * then adding a new mission does not trigger the reloading of data from the
	 * database and thus the sort method in the client does not actually re-sort the
	 * list of missions. MERGE is not cascaded because we will modify and merge only
	 * username and surname attributes of the user and do not want to cascade
	 * detached changes to relationship.
	 */
	public User() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int point) {
		this.points = point;
	}

	public int getIsblocked() {
		return isblocked;
	}

	public void setIsblocked(int isblocked) {
		this.isblocked = isblocked;
	}

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH,CascadeType.REMOVE})
	private List<Questionnaire> questionnaires;

}