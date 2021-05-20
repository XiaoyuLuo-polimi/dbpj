import it.polimi.db2.entities.Administrator;
import it.polimi.db2.entities.Product;
import it.polimi.db2.entities.Questionnaire;
import it.polimi.db2.services.ProductService;
import it.polimi.db2.services.QuestionnaireService;
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

import it.polimi.db2.entities.Product;


@WebServlet("/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db2.services/UserService")
    private UserService userService;

    @EJB(name = "it.polimi.db2.services/ProductService")
    private ProductService productService;

    @EJB(name = "it.polimi.db2.services/QuestionnaireService")
    private QuestionnaireService questionnaireService;

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

        String path = "/WEB-INF/DeletePage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        Integer questionnaireID;
        String errorMessage = null;

        try {
            questionnaireID = Integer.valueOf(StringEscapeUtils.escapeJava(request.getParameter("questionnaireID")));
        }catch (NumberFormatException e){
            questionnaireID = null;
        }
        Administrator administrator = new Administrator();
        administrator = (Administrator) session.getAttribute("administrator");

        if (questionnaireID != null && questionnaireID >= 0) {
            try {
                questionnaireService.deleteQuestionnaireByModifyField(questionnaireID,administrator);
            } catch (Exception e) {
                ctx.setVariable("errorMsg", e.getMessage());
                templateEngine.process(path, ctx, response.getWriter());
                return;
            }
            errorMessage = "Deleted questionnaire successfully!";
        }

        List<Questionnaire> submittedQuestionnaireList;

        try {
            submittedQuestionnaireList = questionnaireService.getAllSubmittedQuestionnaire();
        }catch (PersistenceException e){
            ctx.setVariable("errorMsg", e.getMessage());
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        if (submittedQuestionnaireList.isEmpty()){
            ctx.setVariable("errorMsg", "No questionnaire yet");
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        ctx.setVariable("errorMsg",errorMessage);
        ctx.setVariable("submittedQuestionnaireList", submittedQuestionnaireList);
        templateEngine.process(path, ctx, response.getWriter());

    }

}
