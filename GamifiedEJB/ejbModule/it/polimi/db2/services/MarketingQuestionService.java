package it.polimi.db2.services;

import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.exceptions.DuplicateInsertion;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    public void insertQuesToProd(String questionContent, String productId) throws DuplicateInsertion {

    }

//    public List<MarketingQuestion> getTodayQuestion() throws NoResultException {
//        int prodId = prodService.getTodayProductId();
//
//        if(prodId == 0){
//            return null;
//        }
//        else{
//            List<MarketingQuestion> marketingQuestionList = null;
//            marketingQuestionList = em.createNamedQuery("answer.getTodayQuestionByProdId",MarketingQuestion.class).setParameter(1, prodId).getResultList();
//            return marketingQuestionList;
//        }
//    }

}