package it.polimi.db2.services;

import javax.ejb.Stateless;
import javax.persistence.*;

import it.polimi.db2.exceptions.UpdateProfileException;
import it.polimi.db2.entities.User;
import it.polimi.db2.exceptions.*;
import org.eclipse.persistence.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "GamifiedEJB")
	private EntityManager em;

	public UserService() {
	}
	public List<User> getUserOrderByPoint() throws NonUniqueResultException{
		List<User> userList = new ArrayList<>();
		try {
			userList = em.createNamedQuery("User.getUserOrderByPoint", User.class).getResultList();
		}catch (PersistenceException e) {
			System.out.print("Could not verify credentals");
		}
		return userList;
	}
	public User checkCredentials(String usrn, String pwd) throws NonUniqueResultException, CannotConnectToDB {
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, usrn).setParameter(2, pwd)
					.getResultList();
		}catch(NoResultException e){
			return null;
		} catch (PersistenceException e) {
			throw new CannotConnectToDB(e.getMessage());
		}

		if (uList.isEmpty())
			return null;
		else if (uList.size() == 1)
			return uList.get(0);
		throw new NonUniqueResultException("More than one user registered with same credentials");


	}

	public void registerNewUser(String username,String pwd,String email) throws InvalidInsert, InvalidFormat{

		User user = null;
		try{
			user = em.createNamedQuery("User.checkUsername", User.class).setParameter(1, username).getSingleResult();
		}catch (NoResultException noResultException){
			user = null;
		}

		if(user != null){
			throw new InvalidInsert("Username already exist");
		}
		else{
			User new_user=new User();
			new_user.setEmail(email);
			new_user.setUsername(username);
			new_user.setPassword(pwd);
			new_user.setPoints(0);
			try{
				this.em.persist(new_user);
				this.em.flush();
			}catch (Exception e){
				if(e.getMessage().contains("emailcheck")){
					throw new InvalidFormat("The format of email is incorrect.");
				}
			}
		}

	}

	public String getUsernameById(int id){
		User user = em.find(User.class,id);
		return user.getUsername();
	}

	public void blockUserById(int id){
		User user = em.find(User.class,id);
		user.setIsblocked(1);
		this.em.persist(user);
		this.em.flush();
	}

	public void updateProfile(User u) throws UpdateProfileException {
		try {
			em.merge(u);
		} catch (PersistenceException e) {
			throw new UpdateProfileException("Could not change profile");
		}
	}
}
