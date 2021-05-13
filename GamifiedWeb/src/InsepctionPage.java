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

@WebServlet("/InsepctionPage")
public class InsepctionPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db2.services/UserService")
    private UserService userService;

    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService productService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        // If admin user is not logged in (not present in session) redirect to the login
        String pathContext = getServletContext().getContextPath();
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(pathContext+ "/AdminIndex.html");
            return;
        }

        String path = "/WEB-INF/InspectionPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        if (session.getAttribute("errorMessage") != null){
            ctx.setVariable("errorMsg", session.getAttribute("errorMessage"));
            request.getSession().removeAttribute("errorMessage");
        }else if (session.getAttribute("date") != null){

            ctx.setVariable("submittedQuestionnaireList", session.getAttribute("submittedQuestionnaireList"));
            ctx.setVariable("cancelledQuestionnaireList", session.getAttribute("cancelledQuestionnaireList"));
            ctx.setVariable("date", session.getAttribute("date"));

            request.getSession().removeAttribute("submittedQuestionnaireList");
            request.getSession().removeAttribute("cancelledQuestionnaireList");
            request.getSession().removeAttribute("date");
        }

        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // If admin user is not logged in (not present in session) redirect to the login
        String pathContext = getServletContext().getContextPath();
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("admin") == null) {
            response.sendRedirect(pathContext+ "/AdminLogin.html");
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
        try {
            product = productService.getProductByDate(date);
        }catch(PersistenceException e){
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(pathContext + "/InspectionPage");
            return;
        }

        List<Questionnaire> todayQuestionnaireList = product.getQuestionnaires();

        List<Questionnaire> submittedQuestionnaireList = new ArrayList<>();
        List<Questionnaire> cancelledQuestionnaireList = new ArrayList<>();

        for(Questionnaire q:todayQuestionnaireList){
            if(q.getIsCancelled() == 1){
                cancelledQuestionnaireList.add(q);
            }
            else{
                submittedQuestionnaireList.add(q);
            }
        }

//
//        if (submittedQuestionnaireList.isEmpty() && cancelledQuestionnaireList.isEmpty()){
//            request.getSession().setAttribute("errorMessage", "No value");
//            response.sendRedirect(pathContext + "/InspectionPage");
//            return;
//        }

        request.getSession().setAttribute("date", date);
        request.getSession().setAttribute("submittedQuestionnaireList", submittedQuestionnaireList);
        request.getSession().setAttribute("cancelledQuestionnaireList", cancelledQuestionnaireList);
        request.getSession().setAttribute("product", product);

        response.sendRedirect(pathContext + "/InspectionPage");

    }

}
