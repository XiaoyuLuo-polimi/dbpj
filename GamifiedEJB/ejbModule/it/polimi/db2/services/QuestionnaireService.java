package it.polimi.db2.services;

import it.polimi.db2.entities.Product;
import it.polimi.db2.entities.Questionnaire;
import it.polimi.db2.exceptions.DataNotExist;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

@Stateless
public class QuestionnaireService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;
    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService prjService;

    public QuestionnaireService() {
    }
    public boolean validateIsAlreadySubmitQuestionnaire(){
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
