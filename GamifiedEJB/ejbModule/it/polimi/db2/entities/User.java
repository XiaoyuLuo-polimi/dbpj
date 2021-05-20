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
@NamedQuery(name = "User.getUserOrderByPoint", query = "SELECT r FROM User r WHERE r.points>0 ORDER BY r.points DESC")
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


	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@OrderBy("createTime ASC")
	private List<Questionnaire> questionnaires;

	public List<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(List<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}

}