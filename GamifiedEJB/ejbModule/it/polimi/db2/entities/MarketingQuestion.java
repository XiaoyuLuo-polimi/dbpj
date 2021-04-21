package it.polimi.db2.entities;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "marketing_question", schema = "db2")

public class MarketingQuestion implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private int id;
    @Column(name = "question_content")
    private String questionContent;
    @Column(name = "product_id")
    private int productId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }




}
