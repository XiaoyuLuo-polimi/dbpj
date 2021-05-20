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

@WebServlet("/InspectionShowPage")
public class GoToInspectionShowPage extends HttpServlet {
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

        String path = "/WEB-INF/InspectionShowPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        //if we can get a product, that means we save a product success in SelectPage
        if (session.getAttribute("product") != null){
            //set all para into session
            ctx.setVariable("submittedQuestionnaireList", session.getAttribute("submittedQuestionnaireList"));
            ctx.setVariable("cancelledQuestionnaireList", session.getAttribute("cancelledQuestionnaireList"));
            ctx.setVariable("product", session.getAttribute("product"));
            ctx.setVariable("date", session.getAttribute("date"));
            //remove them
            request.getSession().removeAttribute("submittedQuestionnaireList");
            request.getSession().removeAttribute("cancelledQuestionnaireList");
            request.getSession().removeAttribute("product");
            request.getSession().removeAttribute("date");
        }

        templateEngine.process(path, ctx, response.getWriter());
    }


    public void destroy(){

    }

}
