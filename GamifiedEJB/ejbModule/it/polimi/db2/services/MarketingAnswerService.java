package it.polimi.db2.services;

import it.polimi.db2.content.HomePageShowContent;
import it.polimi.db2.entities.MarketingAnswer;
import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.entities.Questionnaire;
import it.polimi.db2.exceptions.DataNotExist;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Stateless
public class MarketingAnswerService {

    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService prodService;

    @EJB(name = "it.polimi.db2.services/MarketingQuestionService")
    private MarketingQuestionService quesService;

    @EJB(name = "it.polimi.db2.services/questionnaireService")
    private QuestionnaireService questionnaireService;

    @EJB(name = "it.polimi.db2.services/User")
    private UserService userService;

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
    public List<HomePageShowContent> getTodayAnswer() throws DataNotExist {
        List<HomePageShowContent> listShowContent = new ArrayList<HomePageShowContent>();

        List<MarketingQuestion> marketingQuestionList = null;
        //得到今天的全部问题 Q1 Q2
        marketingQuestionList = quesService.getTodayQuestion();
        List<MarketingAnswer> answerList = null;

        //FIRST LOOP 摘出来每个问题的全部用户的回答
        for(MarketingQuestion question : marketingQuestionList) {
            HomePageShowContent singShowContent = new HomePageShowContent();
            //把摘出来的问题 扔到内容里
            singShowContent.questionContent = question.getQuestionContent();

            //得到每个人所回答的该问题
            answerList = em.createNamedQuery("answer.getTodayAnswerByMktQuestionId", MarketingAnswer.class).setParameter(1, question.getId()).getResultList();

            HashMap<String,String> userAnswer = new HashMap<>();
            //List<String> answerTypeTwo = new ArrayList<String>();

            for(MarketingAnswer answer : answerList){

                //通过答案表获得问卷ID
                Questionnaire questionnaire = questionnaireService.getQuestionnaireById(answer.getQuestionnaireId());
                //此处可以得到questionnaireId
                //通过questionnaireID 查到questionnaire
                // 通过questionnaire查到userid 通过userId查到username
                String username = null;
                username = userService.getUsernameById(questionnaire.getUserId());
                //answerTypeTwo.add(username+" : "+answer.getAnswer());
                userAnswer.put(username,answer.getAnswer());
            }
            singShowContent.userAnswer = userAnswer;

            listShowContent.add(singShowContent);
        }

        return listShowContent;
    }


}
