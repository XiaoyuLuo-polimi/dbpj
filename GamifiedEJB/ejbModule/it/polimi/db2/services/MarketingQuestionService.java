package it.polimi.db2.services;


import it.polimi.db2.entities.MarketingQuestion;

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

    public MarketingQuestionService() {
    }

    ;

    public void insertQuesToProd(String questionContent, int productId) throws InvalidInsert {
        MarketingQuestion marketingQuestion = new MarketingQuestion();
        marketingQuestion.setQuestionContent(questionContent);
        marketingQuestion.setProductId(productId);
        try {
            this.em.persist(marketingQuestion);
        }
        catch(Exception e){
            throw new InvalidInsert("More than one test mission without project");
        }
    }

    public List<MarketingQuestion> getTodayQuestion() throws NoResultException {
        try {
            int prodId = prodService.getTodayProductId();
            List<MarketingQuestion> marketingQuestionList = null;
            marketingQuestionList = em.createNamedQuery("answer.getTodayQuestionByProdId",MarketingQuestion.class).setParameter(1, prodId).getResultList();
            return marketingQuestionList;
        }
        catch (PersistenceException var3){
            throw new NoResultException("Today do not exist product");
        }
    }

}