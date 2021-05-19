import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.entities.Product;
import it.polimi.db2.services.MarketingQuestionService;
import it.polimi.db2.services.ProductService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet("/MktQuestionPage")
public class GoToMktQuestionPage extends HttpServlet{

    @EJB(name="it.polimi.db2.services/MarketingQuestionService")
    private MarketingQuestionService mktqService;

    @EJB(name="it.polimi.db2.services/ProductService")
    private ProductService productService;

    private TemplateEngine templateEngine;

    public GoToMktQuestionPage(){
        super();
    }

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
        //Redirect to the login page if session is new.
        String path = "/WEB-INF/MarketingQuestion.html";
        ServletContext servletContext = getServletContext();
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }

        //obtain the marketing questions from the today's product;
        //if there is no product, then remind user corresponding message.
        //if there is a product of today, then insert the product into session for later use.
        Product product=productService.getTodayProduct();
        List<MarketingQuestion> mktQuestions = null;
        if(product != null) {
            mktQuestions = product.getMarketingQuestionsList();
        }else{
            mktQuestions = null;
        }

        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        if (mktQuestions == null) {
            ctx.setVariable("errorMsg", "No marketing questions");
        } else {
            request.getSession().setAttribute("product",product);
            ctx.setVariable("marketingQuestions", mktQuestions);
        }
        templateEngine.process(path, ctx, response.getWriter());


    }

    public void destroy() {
    }

}
