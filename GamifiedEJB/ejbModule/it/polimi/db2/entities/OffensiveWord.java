package it.polimi.db2.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "offensive_word", schema = "db2")
@NamedQuery(name = "offensiveword.checkWord", query = "SELECT r FROM OffensiveWord r  WHERE r.word = ?1")
public class OffensiveWord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String word;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
