package it.polimi.db2.services;


import it.polimi.db2.entities.MarketingQuestion;

import it.polimi.db2.entities.Product;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless
public class MarketingQuestionService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;

    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService prodService;

    public void insertQuesToProd(String questionContent, Product product) throws InvalidInsert {
        MarketingQuestion marketingQuestion = new MarketingQuestion();
        marketingQuestion.setQuestionContent(questionContent);
        marketingQuestion.setProduct(product);
        try {
            this.em.persist(marketingQuestion);
        }
        catch(Exception e){
            throw new InvalidInsert("More than one test mission without project");
        }
    }


}