package it.polimi.db2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
@Entity
@Table(name = "questionnaire", schema = "db2")
@NamedQuery(name = "questionnaire.getQuesById", query = "SELECT r FROM Questionnaire r  WHERE r.productId = ?1")
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

    @Column(name = "product_id")
    private int productId;

    @Column(name = "admin_id")
    private int adminId;

    @Column(name = "user_id")
    private int userId;

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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(int isCancelled) {
        this.isCancelled = isCancelled;
    }
}

