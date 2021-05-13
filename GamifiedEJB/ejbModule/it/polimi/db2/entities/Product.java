package it.polimi.db2.entities;

import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Entity
@Table(name = "product", schema = "db2")

@NamedQuery(name = "product.getProdByDate", query = "SELECT r FROM Product r  WHERE r.productTime = ?1")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Lob
    private byte[] image;

    @Column(name = "product_date")
    private LocalDate productTime;

    @Column(name = "admin_id")
    private int adminId;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private List<MarketingQuestion> marketingQuestion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private List<Questionnaire> questionnaires;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",insertable = false,updatable = false)
    private Administrator admin;

    public String getImageData() {
        return Base64.getMimeEncoder().encodeToString(image);
    }


    public void setProductTime(LocalDate productTime) {
        this.productTime = productTime;
    }

    public Administrator getAdmin() {
        return admin;
    }

    public void setAdmin(Administrator admin) {
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public LocalDate getProductTime() {
        return productTime;
    }

    public void setProductDate(LocalDate productTime) {
        this.productTime = productTime;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public List<MarketingQuestion> getMarketingQuestion() {
        return marketingQuestion;
    }

    public void setMarketingQuestion(List<MarketingQuestion> marketingQuestion) {
        this.marketingQuestion = marketingQuestion;
    }
}
