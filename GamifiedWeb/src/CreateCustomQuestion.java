import it.polimi.db2.services.MarketingQuestionService;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@MultipartConfig
@WebServlet("/CreateCustomQuestion")
public class CreateCustomQuestion extends HttpServlet {
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.services/MarketingQuestionService")
    private MarketingQuestionService marketingQuestionService;
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
        String path = "/WEB-INF/CreateCustomQuestion.html";
        ServletContext servletContext = getServletContext();

        String loginpath = getServletContext().getContextPath() + "/AdminIndex.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(loginpath);
            return;
        }
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            String loginpath = getServletContext().getContextPath() + "/AdminIndex.html";
            response.sendRedirect(loginpath);
            return;
        }

        int productId = 0;
        try {
            productId = productService.getTodayProductId();
        } catch (Exception e) {
            // response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There are no product today");
        }

        boolean isBadRequest = false;
        try {
            productId = productService.getTodayProductId();
        } catch (Exception e) {
            String loginpath = getServletContext().getContextPath() + "/AdminIndex.html";
            response.sendRedirect(loginpath);return;
        }

        String question = null;

        try {
            question = StringEscapeUtils.escapeJava(request.getParameter("question"));

        } catch (NumberFormatException | NullPointerException e) {
            isBadRequest = true;

        }

        try {
            marketingQuestionService.insertQuesToProd(question,productId);
        } catch (Exception e) {
            String loginpath = getServletContext().getContextPath() + "/AdminHome";
            response.sendRedirect(loginpath);return;
        }
        String ctxpath = getServletContext().getContextPath();
        String path = ctxpath + "/CreateCustomQuestion";
        response.sendRedirect(path);
        return;
    }

    public void destroy(){

    }


}
