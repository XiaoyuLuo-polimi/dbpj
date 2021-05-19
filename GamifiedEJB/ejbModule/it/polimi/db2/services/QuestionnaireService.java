package it.polimi.db2.services;

import it.polimi.db2.entities.*;
import it.polimi.db2.exceptions.HasBeenBlocked;
import it.polimi.db2.exceptions.InvalidInsert;
import it.polimi.db2.exceptions.OffensiveWordsInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class QuestionnaireService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;
    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService prjService;

    public QuestionnaireService() {
    }

    public List<Questionnaire> getAllSubmittedQuestionnaire(){
        List<Questionnaire> questionnairesList = new ArrayList<>();
        questionnairesList = em.createNamedQuery("questionnaire.getAllquestionnaire", Questionnaire.class).getResultList();
        for(Questionnaire q:questionnairesList){
            em.refresh(q);
        }
        return questionnairesList;
    }

    public Questionnaire getQuestionnaireByUserId(int uId, int pId) throws NoResultException {
        Questionnaire questionnaire=null;
        try{
            questionnaire = em.createNamedQuery("questionnaire.getQuesByUserId", Questionnaire.class).setParameter(1, uId).setParameter(2,pId).getSingleResult();
        }catch(NoResultException noResultException){
            questionnaire=null;
        }

        return questionnaire;
    }



    public void submitQuestionnaire(User user, Product product, int age, String sex, String expLevel, LocalDateTime dateTime,
                                    Map<MarketingQuestion,String> mktqaMap) throws InvalidInsert, OffensiveWordsInsert, HasBeenBlocked {

        int userId=user.getId();
        int pId=product.getId();

        Questionnaire qn=getQuestionnaireByUserId(userId,pId);
        if(qn == null) {
            Questionnaire questionnaire = new Questionnaire();
//            System.out.println("############## function" + userId + "," + pId + "," + age + "," + sex + "," + expLevel + "," + dateTime+","+mktqaMap);
            questionnaire.setUser(user);
//            System.out.println("############## function" + userId);
            questionnaire.setProduct(product);
            questionnaire.setAge(age);
            questionnaire.setExpertiseLevel(expLevel);
            questionnaire.setSex(sex);
            questionnaire.setCreateTime(dateTime);
            questionnaire.setMarketingAnswerMap(mktqaMap);
            try{
                this.em.persist(questionnaire);
                this.em.flush();
            }
            catch(Exception e){
                if(e.getMessage().contains("offensive words")){
                    System.out.println("offensive words");
                    throw new OffensiveWordsInsert("Detect offensive words in the marketing answers");
                }else if(e.getMessage().contains("has been blocked")){
                    System.out.println("This user has been blocked, cannot insert into qn.");
                    throw new HasBeenBlocked("Detect offensive words in the marketing answers");
                }
            }
        }else{
            throw new InvalidInsert("You've already answer this question");
        }
    }


    public void cancelAQuestionnaire(User user,Product product, LocalDateTime dateTime) throws HasBeenBlocked, InvalidInsert {

        int userId=user.getId();
        int pId=product.getId();

        Questionnaire qn=getQuestionnaireByUserId(userId,pId);
        if(qn == null) {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setUser(user);
            questionnaire.setProduct(product);
            questionnaire.setCreateTime(dateTime);
            questionnaire.setIsCancelled(1);
            try {
                this.em.persist(questionnaire);
                this.em.flush();
            } catch (Exception e) {
                if (e.getMessage().contains("has been blocked")) {
                    System.out.println("This user has been blocked, cannot insert into qn.");
                    throw new HasBeenBlocked("Detect offensive words in the marketing answers");
                }
            }
        }else{
            throw new InvalidInsert("You've already answer this question");
        }
    }


    public void deleteQuestionnaireByModifyField(int questionnaireId, Administrator administrator) throws NoResultException {
        Questionnaire questionnaire = new Questionnaire();
        try{
            questionnaire = em.find(Questionnaire.class,questionnaireId);
        }catch(PersistenceException e){
            throw new NoResultException("ERROR");
        }
        questionnaire.setAdministrator(administrator);
        try{
            this.em.persist(questionnaire);
            this.em.flush();
        }catch(PersistenceException e){
            throw new NoResultException("ERROR");
        }
    }

}
