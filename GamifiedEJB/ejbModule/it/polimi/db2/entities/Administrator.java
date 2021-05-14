package it.polimi.db2.entities;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
/**
 * The persistent class for the usertable database table.
 *
 */
@Entity
@Table(name = "administrator", schema = "db2")
@NamedQuery(name = "Administrator.checkCredentials", query = "SELECT r FROM Administrator r  WHERE r.username = ?1 and r.password = ?2")
public class Administrator implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String username;

    private String password;

    private String email;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(mappedBy = "administrator",fetch = FetchType.LAZY)
    private List<Product> products;
}
