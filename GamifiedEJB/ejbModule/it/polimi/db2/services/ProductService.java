package it.polimi.db2.services;

import it.polimi.db2.entities.Product;
import it.polimi.db2.exceptions.DataNotExist;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.xml.crypto.Data;
import java.time.LocalDate;

@Stateless
public class ProductService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;

    public ProductService() {
    }

    public void setNewProduct(String name , int adminId, byte[] image) throws InvalidInsert,DataNotExist{
        Product product = new Product();

        LocalDate date  = LocalDate.now();
        product.setProductDate(date);
        product.setAdminId(adminId);
        product.setName(name);
        product.setImage(image);
//        //设置日期格式
//        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
//        //获取string类型日期
//        String today=dateFormat.format(date);
        try {
            this.em.persist(product);
            this.em.flush();
        }
        catch (Exception e){
            throw new InvalidInsert("Insert falues");
        }
    }
    public Product getTodayProduct() throws DataNotExist{
        Product product = new Product();

        LocalDate date  = LocalDate.now();;
        try {
            product = em.createNamedQuery("product.getProdByDate", Product.class).setParameter(1, date).getSingleResult();
        } catch (PersistenceException var3) {
            throw new DataNotExist("Cannot load projects");
        }
        return product;
    }
    public int getTodayProductId() throws DataNotExist{
        Product product = null;

        LocalDate date  = LocalDate.now();;
        try {
            product = em.createNamedQuery("product.getProdByDate", Product.class).setParameter(1, date).getSingleResult();
        } catch (PersistenceException var3) {
            throw new DataNotExist("There are no project today");
        }

        if(product != null){
            return product.getId();
        }
        else{
            return 0;
        }
    }
}
