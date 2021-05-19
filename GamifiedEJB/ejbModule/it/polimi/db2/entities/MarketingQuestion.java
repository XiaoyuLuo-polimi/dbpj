package it.polimi.db2.entities;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "marketing_question", schema = "db2")
public class MarketingQuestion implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private int id;
    @Column(name = "question_content")
    private String questionContent;
//    @Column(name = "product_id")
//    private int productId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public int getProductId() {
//        return productId;
//    }
//
//    public void setProductId(int productId) {
//        this.productId = productId;
//    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }




    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "marketing_answer",
            joinColumns = @JoinColumn(name = "mkt_question_id")
    )
    @MapKeyJoinColumn(name = "questionnaire_id")
    @Column(name = "answer")
    private Map<Questionnaire,String> questionnaireMap;

    public Map<Questionnaire, String> getQuestionnaireMap() {
        return questionnaireMap;
    }

    public void setQuestionnaireMap(Map<Questionnaire, String> questionnaireMap) {
        this.questionnaireMap = questionnaireMap;
    }


    @ManyToMany(mappedBy = "marketingQuestions")
    private List<Questionnaire> questionnaires;

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }




    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }









}
