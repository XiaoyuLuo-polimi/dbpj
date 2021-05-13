import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        String loginpath = this.getServletContext().getContextPath() + "/adminindex.html";

        String path = "/WEB-INF/adminindex.html";
        System.out.print("********************************"+ "\n");
        System.out.print(this.getServletContext().getContextPath());
        System.out.print("********************************"+ "\n");
        System.out.print(loginpath);
        System.out.print("********************************"+ "\n");
        response.sendRedirect(loginpath);
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