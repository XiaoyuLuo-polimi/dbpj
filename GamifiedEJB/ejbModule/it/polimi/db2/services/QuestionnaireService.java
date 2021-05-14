package it.polimi.db2.services;

import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.entities.Questionnaire;
import it.polimi.db2.exceptions.HasBeenBlocked;
import it.polimi.db2.exceptions.InvalidInsert;
import it.polimi.db2.exceptions.OffensiveWordsInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Stateless
public class QuestionnaireService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;
    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService prjService;

    public QuestionnaireService() {
    }

    public Questionnaire getQuestionnaireById(int id){
        Questionnaire questionnaire = em.find(Questionnaire.class,id);
        return questionnaire;
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



    public void submitQuestionnaire(int userId, int pId, int age, String sex, String expLevel, LocalDateTime dateTime,
                                    Map<MarketingQuestion,String> mktqaMap) throws InvalidInsert, OffensiveWordsInsert, HasBeenBlocked {
        LocalDateTime time=LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
        String strtime = dtf.format(time);
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime new_time=LocalDateTime.parse(strtime,dtf2);

        Questionnaire qn=getQuestionnaireByUserId(userId,pId);
        if(qn == null) {
            Questionnaire questionnaire = new Questionnaire();
//            System.out.println("############## function" + userId + "," + pId + "," + age + "," + sex + "," + expLevel + "," + dateTime+","+mktqaMap);
            questionnaire.setUserId(userId);
//            System.out.println("############## function" + userId);
            questionnaire.setProductId(pId);
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


    public void cancelAQuestionnaire(int userId, int pId, LocalDateTime dateTime) throws HasBeenBlocked {

            Questionnaire questionnaire=new Questionnaire();
            questionnaire.setUserId(userId);
            questionnaire.setProductId(pId);
            questionnaire.setCreateTime(dateTime);
            questionnaire.setIsCancelled(1);
            try{
                this.em.persist(questionnaire);
                this.em.flush();
            }
            catch(Exception e) {
                if (e.getMessage().contains("has been blocked")) {
                    System.out.println("This user has been blocked, cannot insert into qn.");
                    throw new HasBeenBlocked("Detect offensive words in the marketing answers");
                }
            }


    }

    public void deleteQuestionnaire(int questionnaireId) throws NoResultException {
        Questionnaire questionnaire = em.find(Questionnaire.class,questionnaireId);
        if(questionnaire == null){
            throw new NoResultException("Does not exist that questionnaire");
        }
        else{
            em.remove(questionnaire);
        }
    }


}
