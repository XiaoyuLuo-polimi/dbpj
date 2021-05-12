package it.polimi.db2.entities;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "marketing_answer", schema = "db2")
@NamedQuery(name = "answer.insertNewAnswer", query = "SELECT r FROM MarketingAnswer r  WHERE r.questionnaireId = ?1 and r.mktQuestionId = ?2")
@NamedQuery(name = "answer.getTodayAnswerByMktQuestionId", query = "SELECT r FROM MarketingAnswer r  WHERE r.mktQuestionId = ?1")
public class MarketingAnswer implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "questionnaire_id")
    private int questionnaireId;

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public int getMktQuestionId() {
        return mktQuestionId;
    }

    public void setMktQuestionId(int mktQuestionId) {
        this.mktQuestionId = mktQuestionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Id
    @Column(name = "mkt_question_id")
    private int mktQuestionId;

    //外键在谁哪谁是爸爸
    //爸爸名字写外键
    //爸爸是JoinColum
    @ManyToOne
    @JoinColumn(name = "mkt_question_id",insertable = false,updatable = false)
    private MarketingQuestion marketingQuestion;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id",insertable = false,updatable = false)
    private Questionnaire questionnaire;

    public MarketingQuestion getMarketingQuestion() {
        return marketingQuestion;
    }

    public void setMarketingQuestion(MarketingQuestion marketingQuestion) {
        this.marketingQuestion = marketingQuestion;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    private String answer;

    public MarketingAnswer() {
    }


}
