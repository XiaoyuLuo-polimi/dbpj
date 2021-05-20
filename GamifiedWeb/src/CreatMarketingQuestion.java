import it.polimi.db2.entities.Product;
import it.polimi.db2.services.MarketingQuestionService;
import it.polimi.db2.services.ProductService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

@MultipartConfig
@WebServlet("/CreateMarketingQuestion")
public class CreatMarketingQuestion extends HttpServlet {
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
        String path = "/WEB-INF/CreateMarketingQuestion.html";
        ServletContext servletContext = getServletContext();

        String loginpath = getServletContext().getContextPath() + "/AdminIndex.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("administrator") == null) {
            response.sendRedirect(loginpath);
            return;
        }
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("marketingQuestionNumber", session.getAttribute("marketingQuestionNumber"));
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

        Product product=null;
        boolean isBadRequest = false;
        //Get the insert product date from session (which is save by previous /CreateProduct)
        LocalDate date = (LocalDate)session.getAttribute("InsertProductDate");

        try {
            product = productService.getProductByDate(date);
        } catch (Exception e) {
            String loginpath = getServletContext().getContextPath() + "/AdminHome?errorMsg=Can not get a Product";
            response.sendRedirect(loginpath);
            return;
        }

        String questionContent = null;

        try {
            questionContent = StringEscapeUtils.escapeJava(request.getParameter("question"));
        } catch (NumberFormatException | NullPointerException e) {
            isBadRequest = true;

        }
        int marketingQuestionNumber = 0;
        marketingQuestionNumber = (int)session.getAttribute("marketingQuestionNumber");
        try {
            marketingQuestionService.insertQuesToProd(questionContent,product);
            marketingQuestionNumber = marketingQuestionNumber+1;
            session.setAttribute("marketingQuestionNumber",marketingQuestionNumber);

        } catch (Exception e) {
            String loginpath = getServletContext().getContextPath() + "/AdminHome";
            response.sendRedirect(loginpath);return;
        }

        String ctxpath = getServletContext().getContextPath();
        String path = ctxpath + "/CreateMarketingQuestion";
        response.sendRedirect(path);
        return;
    }

    public void destroy(){
    }
}
