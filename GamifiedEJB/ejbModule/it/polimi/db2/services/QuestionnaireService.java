package it.polimi.db2.services;

import it.polimi.db2.entities.Product;
import it.polimi.db2.entities.Questionnaire;
import it.polimi.db2.entities.User;
import it.polimi.db2.exceptions.DataNotExist;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class QuestionnaireService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;
    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService prjService;

    public QuestionnaireService() {
    }
    public boolean validateIsAlreadySubmitQuestionnaire() throws DataNotExist{
        Questionnaire questionnaire = null;

        int pdId = prjService.getTodayProductId();

        questionnaire = em.createNamedQuery("questionnaire.getQuesById", Questionnaire.class).setParameter(1, pdId).getSingleResult();
        if(pdId!=0 && questionnaire != null){
            return true;
        }
        else if(pdId==0){
            return false;
        }
        return true;
    }
    public Questionnaire getQuestionnaireById(int id){
        Questionnaire questionnaire = em.find(Questionnaire.class,id);
        return questionnaire;
    }
    public List<Questionnaire> getSubmittedQuestionnaireByDate(Date date) throws PersistenceException {
        List<Questionnaire> questionnaireList = new ArrayList<>();
        int isCanceled = 0;
        try {
            questionnaireList = em.createNamedQuery("questionnaire.getQuesById", Questionnaire.class).setParameter(1, date).setParameter(2, isCanceled).getResultList();
        }catch(PersistenceException e){
            throw new PersistenceException(e.getMessage());
        }
        return questionnaireList;
    }
    public void submitQuestionnaire(int age,String sex,String expLevel){
        Questionnaire questionnaire = null;
        questionnaire.setAge(age);
        questionnaire.setExpertiseLevel(expLevel);
        questionnaire.setSex(sex);

    }
    public void deletQuestionnaire(int questionnaireId) throws DataNotExist {
        Questionnaire questionnaire = em.find(Questionnaire.class,questionnaireId);
        if(questionnaire == null){
            throw new DataNotExist("Does not exist that questionnaire");
        }
        else{
            em.remove(questionnaire);
        }
    }
}
