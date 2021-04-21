package it.polimi.db2.services;

import it.polimi.db2.entities.MarketingAnswer;
import it.polimi.db2.entities.User;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MarketingAnswerService {

    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;
    public MarketingAnswerService() {
    }

    public void insertAnswer(String content, int qnId, int mktQuesId ) throws InvalidInsert{
        MarketingAnswer answer = null;
        answer = em.createNamedQuery("answer.insertNewAnswer", MarketingAnswer.class).setParameter(1, qnId).setParameter(2,mktQuesId).getSingleResult();

        if(answer != null){
            throw new InvalidInsert("You've already answer this question");
        }
        else{
            answer.setAnswer(content);
            answer.setMktQuestionId(mktQuesId);
            answer.setQuestionnaireId(qnId);
            this.em.persist(answer);
            this.em.flush();
        }
    }
}
