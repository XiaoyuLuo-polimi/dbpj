import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/GoAdminLoginPage")
public class GoToAdminCheckLoginPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    public GoToAdminCheckLoginPage() {
    }
    public void init() throws ServletException {
        ServletContext servletContext = this.getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginpath = this.getServletContext().getContextPath() + "/AdminIndex.html";

        String path = "/WEB-INF/AdminIndex.html";


        System.out.print(this.getServletContext().getContextPath());
        System.out.print(loginpath);
        response.sendRedirect(path);
    }
}
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String loginpath = this.getServletContext().getContextPath() + "/index.html";
//        HttpSession session = request.getSession();
//        if (!session.isNew() && session.getAttribute("user") != null) {
//        }
//        else {
//            response.sendRedirect(loginpath);
//        }
//    }