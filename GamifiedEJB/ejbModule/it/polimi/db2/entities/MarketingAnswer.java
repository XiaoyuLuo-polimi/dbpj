package it.polimi.db2.entities;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "marketing_answer", schema = "db2")
@NamedQuery(name = "answer.insertNewAnswer", query = "SELECT r FROM MarketingAnswer r  WHERE r.questionnaireId = ?1 and r.mktQuestionId = ?2")
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

    private String answer;

    public MarketingAnswer() {
    }


}
