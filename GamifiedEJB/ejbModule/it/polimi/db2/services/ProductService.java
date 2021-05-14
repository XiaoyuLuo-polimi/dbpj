package it.polimi.db2.services;

import it.polimi.db2.entities.Product;
import it.polimi.db2.exceptions.InvalidInsert;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

@Stateless
public class ProductService {
    @PersistenceContext(unitName = "GamifiedEJB")
    private EntityManager em;

    public ProductService() {
    }
    public void setNewProduct(String name , int adminId, byte[] imagePath) throws InvalidInsert{
        Product product = new Product();

        LocalDate date  = LocalDate.now();;
//        //设置日期格式
//        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
//        //获取string类型日期
//        String today=dateFormat.format(date);

        product = em.createNamedQuery("product.getProdByDate", Product.class).setParameter(1, date).getSingleResult();

        if(product != null){
            throw new InvalidInsert("Today already exist product");
        }
        else{
            product.setProductDate(date);
            product.setAdminId(adminId);
            product.setName(name);
            product.setImage(imagePath);

            this.em.persist(product);
            this.em.flush();

        }
    }
    public int getTodayProductId() throws NoResultException{
        Product product = null;

        LocalDate date  = LocalDate.now();
        try {
            product = em.createNamedQuery("product.getProdByDate", Product.class).setParameter(1, date).getSingleResult();
        }catch (NoResultException e){
            return 0;
        }

        if(product != null){
            return product.getId();
        }else{
            return 0;
        }
    }

    public Product getProductByDate(LocalDate date) throws NoResultException{
        Product product = null;
        try {
            product = em.createNamedQuery("product.getProdByDate", Product.class).setParameter(1, date).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return product;
    }

    public Product getTodayProduct() throws NoResultException{
        Product product = null;

        LocalDate date  = LocalDate.now();
        try {
            product = em.createNamedQuery("product.getProdByDate", Product.class).setParameter(1, date).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return product;
    }
}
