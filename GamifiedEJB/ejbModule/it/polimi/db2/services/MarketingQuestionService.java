package it.polimi.db2.services;

import it.polimi.db2.entities.MarketingAnswer;
import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.exceptions.DataNotExist;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
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

    public void insertQuesToProd(String questionContent, String productId) throws DataNotExist {

    }

    public List<MarketingQuestion> getTodayQuestion() throws DataNotExist{
        int prodId = prodService.getTodayProductId();
        if(prodId == 0){
            throw new NonUniqueResultException("Today do not exist product");

        }
        else{
            List<MarketingQuestion> marketingQuestionList = null;
            marketingQuestionList = em.createNamedQuery("answer.getTodayQuestionByProdId",MarketingQuestion.class).setParameter(1, prodId).getResultList();
            return marketingQuestionList;
        }
    }
}