import it.polimi.db2.entities.Product;
import it.polimi.db2.entities.Questionnaire;
import it.polimi.db2.services.ProductService;
import it.polimi.db2.services.UserService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/InspectionSelectPage")
public class GoToInspectionSelectPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db2.services/UserService")
    private UserService userService;

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
         
        String pathContext = getServletContext().getContextPath();
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(pathContext+ "/AdminIndex.html");
            return;
        }
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        if (session.getAttribute("errorMsg") != null){
            ctx.setVariable("errorMsg", session.getAttribute("errorMsg"));
            request.getSession().removeAttribute("errorMsg");

        }
            String path = "/WEB-INF/InspectionSelectPage.html";


        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // If admin user is not logged in (not present in session) redirect to the login
        String pathContext = getServletContext().getContextPath();
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(pathContext+ "/AdminIndex.html");
            return;
        }

        String strDate;
        try {
            strDate = StringEscapeUtils.escapeJava(request.getParameter("Date"));
            if (strDate == null) {
                throw new Exception("No empty filed!");
            }
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(strDate, fmt);
        Product product = new Product();
        product = productService.getProductByDate(date);


        if(product == null){
            request.getSession().setAttribute("errorMsg", "Wrong date, there not exist a product.");
            response.sendRedirect(pathContext + "/InspectionSelectPage");
            return;
        }
        else{
            List<Questionnaire> ProductQuestionnaireList = new ArrayList<>();
            try {
                ProductQuestionnaireList = product.getQuestionnaires();
            }catch(PersistenceException e){

            }
            List<Questionnaire> submittedQuestionnaireList = new ArrayList<>();
            List<Questionnaire> cancelledQuestionnaireList = new ArrayList<>();

            for(Questionnaire q:ProductQuestionnaireList){
                //select the cancelled questionnaire
                if(q.getIsCancelled() == 1){
                    cancelledQuestionnaireList.add(q);
                }
                //select the questionnaire which is not be deleted
                else if(q.getAdministrator() == null){
                    submittedQuestionnaireList.add(q);
                }
            }
            //save those parameter into session
            request.getSession().setAttribute("date", date);
            request.getSession().setAttribute("submittedQuestionnaireList", submittedQuestionnaireList);
            request.getSession().setAttribute("cancelledQuestionnaireList", cancelledQuestionnaireList);
            request.getSession().setAttribute("product", product);
            response.sendRedirect(pathContext + "/InspectionShowPage");
        }



//
//        if (submittedQuestionnaireList.isEmpty() && cancelledQuestionnaireList.isEmpty()){
//            request.getSession().setAttribute("errorMessage", "No value");
//            response.sendRedirect(pathContext + "/InspectionPage");
//            return;
//        }



    }
}
