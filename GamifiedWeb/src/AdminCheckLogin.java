import it.polimi.db2.entities.Administrator;
import it.polimi.db2.services.AdministratorService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/AdminCheckLogin")
public class AdminCheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.services/AdminService")
    private AdministratorService administratorService;

    public AdminCheckLogin() {
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
        // Redirect to the Home page and add missions to the parameters
        String path = "/AdminIndex.html";
        ServletContext servletContext = getServletContext();
        HttpSession session = request.getSession();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // obtain and escape params
        String usrn = null;
        String pwd = null;
        try {
            usrn = StringEscapeUtils.escapeJava(request.getParameter("adminname"));
            pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
            if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
                throw new Exception("Missing or empty credential value");
            }
        } catch (Exception e) {
            // for debugging only e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
            return;
        }
        Administrator administrator;
//        try {
//            // query db to authenticate for administrator
//            administrator = administratorService.checkCredentials(usrn, pwd);
//        } catch (NonUniqueResultException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
//            return;
//        }
        administrator = administratorService.checkCredentials(usrn, pwd);
        // If the administrator exists, add info to the session and go to home page, otherwise
        // show login page with error message

        String path;
        if (administrator == null) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", "Incorrect username or password");
            path = "/AdminIndex.html";
            templateEngine.process(path, ctx, response.getWriter());

        } else {

            request.getSession().setAttribute("administrator", administrator);
            path = getServletContext().getContextPath() + "/AdminHome";
            response.sendRedirect(path);
        }

    }

    public void destroy() {
    }
}