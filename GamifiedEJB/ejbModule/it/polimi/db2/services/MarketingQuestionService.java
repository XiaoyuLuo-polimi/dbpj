package it.polimi.db2.services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
@Stateless
public class MarketingQuestionService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;

    public MarketingQuestionService(){};
    public void insertQuesToProd(String questionContent,String productId){

    }
}
