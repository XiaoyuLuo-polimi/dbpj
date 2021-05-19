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
@WebServlet("/CreateQuestionnary")
public class CreateQuestionnary extends HttpServlet {
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
        // Redirect to the Home page and add missions to the parameters
        String path = "/WEB-INF/CreateQuestionnary.html";
        ServletContext servletContext = getServletContext();
        String loginpath = getServletContext().getContextPath() + "/AdminIndex.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(loginpath);
            return;
        }
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(strDate, fmt);

        int productId = 0;
        try {
            productId = productService.isExistProductInThatDate(date);
        } catch (Exception e) {
           // response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There are no product today");
        }
        // Get and parse all parameters from request
        boolean isBadRequest = false;
        String productName = null;
        Part filePart = null;
        InputStream imageStream = null;

        LocalDate todayDate  = LocalDate.now();

        if(todayDate.compareTo(date)>0){
            String adminHomePage = getServletContext().getContextPath() + "/AdminHome?errorMsg=You cannot insert a product of the day preceding the current day";
            response.sendRedirect(adminHomePage);
            return;
        }

        response.setContentType("multipart/form-data;charset=utf-8");
        try {
            productName = StringEscapeUtils.escapeJava(request.getParameter("productName"));
            filePart = request.getPart("image");
            String contentType = filePart.getContentType();
            imageStream = filePart.getInputStream();
            if (productName == null || productName.isEmpty() ||
                    contentType == null || imageStream.available()<=0) {
                String adminHomePage = getServletContext().getContextPath() + "/AdminHome?errorMsg=The product file can not be empty or wrong format";
                response.sendRedirect(adminHomePage);
                return;
            }
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
            return;
        }

        //
        // Create mission in DB
        Administrator admin = (Administrator) session.getAttribute("administrator");

        int imageLength = (int) filePart.getSize();
        byte[] bytesImage = new byte[imageLength];
        imageStream.read(bytesImage);
        imageStream.close();

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
            String path = getServletContext().getContextPath() + "/CreateCustomQuestion";
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


