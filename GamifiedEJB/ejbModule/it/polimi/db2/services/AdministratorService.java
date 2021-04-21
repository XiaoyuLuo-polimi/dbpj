package it.polimi.db2.services;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.NonUniqueResultException;

import it.polimi.db2.entities.Administrator;
import it.polimi.db2.exceptions.UpdateProfileException;
import it.polimi.db2.entities.User;
import it.polimi.db2.exceptions.*;
import java.util.List;
@Stateless
public class AdministratorService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;

    public AdministratorService() {
    }

    public Administrator checkCredentials(String usrn, String pwd) throws NonUniqueResultException {
        List<Administrator> uList = null;
        try {
            uList = em.createNamedQuery("Administrator.checkCredentials", Administrator.class).setParameter(1, usrn).setParameter(2, pwd)
                    .getResultList();
        } catch (PersistenceException e) {
            System.out.print("Could not verify credentals");
        }
        if (uList.isEmpty())
            return null;
        else if (uList.size() == 1)
            return uList.get(0);
        throw new NonUniqueResultException("More than one user registered with same credentials");

    }
}
