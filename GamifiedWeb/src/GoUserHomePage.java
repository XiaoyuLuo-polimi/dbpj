import it.polimi.db2.content.HomePageShowContent;
import it.polimi.db2.exceptions.DataNotExist;
import it.polimi.db2.services.MarketingAnswerService;
import it.polimi.db2.services.QuestionnaireService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/UserHome")
public class GoUserHomePage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.services/MarketingAnswerService")
    private MarketingAnswerService marketingAnswerService;


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
        String path = "/WEB-INF/UserHome.html";
        ServletContext servletContext = getServletContext();

        List<HomePageShowContent> homePageShowContents = new ArrayList<HomePageShowContent>();
        try {
            homePageShowContents = marketingAnswerService.getTodayAnswer();
        } catch (DataNotExist dataNotExist) {
            dataNotExist.printStackTrace();
        }

        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        ctx.setVariable("homePageShowContents", homePageShowContents);
        templateEngine.process(path, ctx, response.getWriter());
    }
}
