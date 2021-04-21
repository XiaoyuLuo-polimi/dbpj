package it.polimi.db2.services;

import it.polimi.db2.entities.OffensiveWord;
import it.polimi.db2.entities.Product;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

@Stateless
public class OffensiveWordService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;
    public OffensiveWordService() {
    }
    public void checkReplica(String word) throws InvalidInsert {
        OffensiveWord ofword = null;
        ofword = em.createNamedQuery("offensiveword.checkWord", OffensiveWord.class).setParameter(1, word).getSingleResult();
        if(ofword == null){
            throw new InvalidInsert("word already exist");
        }
        else{
            ofword.setWord(word);
            this.em.persist(ofword);
            this.em.flush();
        }
    }
}
