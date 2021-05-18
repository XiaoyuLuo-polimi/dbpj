package it.polimi.db2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "questionnaire", schema = "db2")
@NamedQuery(name = "questionnaire.getQuesByUserId", query = "SELECT r FROM Questionnaire r  WHERE r.user.id = ?1 and r.product.id= ?2 and r.isCancelled=0")
@NamedQuery(name = "questionnaire.getAllquestionnaire", query = "SELECT r FROM Questionnaire r  WHERE r.isCancelled=0 and r.administrator.id=null")
public class Questionnaire implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int age;

    private String sex;

    @Column(name = "expertise_level")
    private String expertiseLevel;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "iscancelled")
    private int isCancelled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getExpertiseLevel() {
        return expertiseLevel;
    }

    public void setExpertiseLevel(String expertiseLevel) {
        this.expertiseLevel = expertiseLevel;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public int getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(int isCancelled) {
        this.isCancelled = isCancelled;
    }



    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Administrator administrator;

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
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



    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "marketing_answer",
            joinColumns = @JoinColumn(name = "questionnaire_id")
    )
    //if the map key type is another entity.
    @MapKeyJoinColumn(name = "mkt_question_id")
    @Column(name = "answer")
    private Map<MarketingQuestion,String> marketingAnswers;



    @ManyToMany
    //joinColumns specify the field associated with its own ID in the intermediate table.
    //inverseJoinColumns specify the field associated with the counterparty ID in the intermediate table.
    //referencedColumnName is the key of associated table.
    @JoinTable(name="marketing_answer",
            joinColumns = @JoinColumn(name = "questionnaire_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "mkt_question_id", referencedColumnName = "id"))
    private List<MarketingQuestion> marketingQuestions;


    public List<MarketingQuestion> getMarketingQuestions() {
        return marketingQuestions;
    }

    public void setMarketingQuestions(List<MarketingQuestion> marketingQuestions) {
        this.marketingQuestions = marketingQuestions;
    }


    public Map<MarketingQuestion, String> getMarketingAnswerMap() {
        return marketingAnswers;
    }

    public void setMarketingAnswerMap(Map<MarketingQuestion,String> marketingAnswerMaps) {
        this.marketingAnswers = marketingAnswerMaps;
    }


    public void removeMarketingAnswer(MarketingQuestion mq){
        marketingAnswers.remove(mq);
    }




}

