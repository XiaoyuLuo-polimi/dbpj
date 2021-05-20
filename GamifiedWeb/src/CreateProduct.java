import it.polimi.db2.entities.Administrator;
import it.polimi.db2.exceptions.DuplicateInsertion;
import it.polimi.db2.exceptions.InvalidInsert;
import it.polimi.db2.services.ProductService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@MultipartConfig
@WebServlet("/CreateProduct")
public class CreateProduct extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService productService;

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         
        String path = "/WEB-INF/CreateProduct.html";
        ServletContext servletContext = getServletContext();
        String loginpath = getServletContext().getContextPath() + "/AdminIndex.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(loginpath);
            return;
        }
        session.removeAttribute("InsertProductDate");
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
        return;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ctxpath = getServletContext().getContextPath();
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            String adminHomePath = getServletContext().getContextPath() + "/AdminIndex.html";
            response.sendRedirect(adminHomePath);
            return;
        }
        //create a string format date to get the date from front-end(html)
        String strDate;
        try {
            strDate = StringEscapeUtils.escapeJava(request.getParameter("date"));
            if (strDate == null) {
                throw new Exception("No empty filed!");
            }
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
       //convert the date format form String to Localdate and give it a yyyymmdd format
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(strDate, fmt);
        //set a initinal value for product ID which is 0
        int productId = 0;
        // if there exist a product in that date, it will return the productId of that date
        // if not, return a zero (which help us judge the product exist condition in the rest code)
        productId = productService.isExistProductInThatDate(date);
        
        // check is the creat product date is equal or after today(LocalDate.now())
        LocalDate todayDate  = LocalDate.now();
        if(todayDate.compareTo(date)>0){
            //if is true, return a error msg
            String adminHomePage = getServletContext().getContextPath() + "/AdminHome?errorMsg=You cannot insert a product of the day preceding the current day";
            response.sendRedirect(adminHomePage);
            return;
        }

        //set the create product date into session, let subsequent page can use it to judge which date to chooise
        session.setAttribute("InsertProductDate",date);
        //set the reponse head, let "image" can submit by it
        response.setContentType("multipart/form-data;charset=utf-8");
        // Get and parse all parameters from request
        boolean isBadRequest = false;
        String productName = null;
        Part filePart = null;
        InputStream imageInputStream = null;
        try {
            productName = StringEscapeUtils.escapeJava(request.getParameter("productName"));
            //make a new file part
            filePart = request.getPart("image");
            String contentType = filePart.getContentType();
            imageInputStream = filePart.getInputStream();
            if (productName == null || productName.isEmpty() ||
                    contentType == null || imageInputStream.available()<=0) {
                String adminHomePage = getServletContext().getContextPath() + "/AdminHome?errorMsg=The product file can not be empty or wrong format";
                response.sendRedirect(adminHomePage);
                return;
            }
            // constrain the type we can upload
            if (!contentType.contains("jpg")
                    && !contentType.contains("jpeg")
                    && !contentType.contains("png")){
                String adminHomePage = getServletContext().getContextPath() + "/AdminHome?errorMsg=You can only upload png jpg jpeg format file";
                response.sendRedirect(adminHomePage);
                return;

            }
        } catch (NumberFormatException | NullPointerException e) {
            isBadRequest = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isBadRequest) {
            String adminHomePage = getServletContext().getContextPath() + "/AdminHome?errorMsg=Incorrect or missing param values";
            response.sendRedirect(adminHomePage);
            return;
        }
        
        Administrator admin = (Administrator) session.getAttribute("administrator");

        int imageInputLength = (int) filePart.getSize();
        byte[] bytesImage = new byte[imageInputLength];
        imageInputStream.read(bytesImage);
        imageInputStream.close();

        if(productId == 0) {
            try {
                productService.setNewProductAfterYesterday(productName, admin, bytesImage,date);
            } catch (DuplicateInsertion e) {
                String loginpath = getServletContext().getContextPath() + "/AdminHome?errorMsg=Already have product today, cannot create new one";
                response.sendRedirect(loginpath);
                return;
            } catch (InvalidInsert e){
                String loginpath = getServletContext().getContextPath() + "/AdminHome?errorMsg=You cannot insert a product of the day preceding the current day";
                response.sendRedirect(loginpath);
                return;
            }
            String path = getServletContext().getContextPath() + "/ CreateMarketingQuestion";
            response.sendRedirect(path);return;
        }
        else{
//            String path;
//            ServletContext servletContext = getServletContext();
//            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
//            ctx.setVariable("errorMsg", "Already exist product today, can not create new one");
//            path = "/AdminHome.html";
//            templateEngine.process(path, ctx, response.getWriter());
//            return;
            String loginpath = getServletContext().getContextPath() + "/AdminHome?errorMsg=Already Exist prod in that date!";
            response.sendRedirect(loginpath);return;
        }
    }

    public void destroy(){

    }


}


