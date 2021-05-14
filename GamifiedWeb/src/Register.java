import it.polimi.db2.exceptions.InvalidFormat;
import it.polimi.db2.exceptions.InvalidInsert;
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
import java.io.IOException;

@WebServlet("/Register")
public class Register extends HttpServlet {
    @EJB(name = "it.polimi.db2.services/UserService")
    private UserService usrService;

    private TemplateEngine templateEngine;

    public Register() {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // obtain and escape params
        String usrn = null;
        String pwd1 = null;
        String pwd2 = null;
        String email = null;


        usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
        pwd1 = StringEscapeUtils.escapeJava(request.getParameter("pwd1"));
        pwd2 = StringEscapeUtils.escapeJava(request.getParameter("pwd2"));
        email = StringEscapeUtils.escapeJava(request.getParameter("email"));

        String errorMsg = null;

        if (usrn.length() == 0 || pwd1.length() == 0 || pwd2.length() == 0 || email.length() == 0) {
            errorMsg = "Required field missing.";
        }else if(!pwd1.equals(pwd2)){
            errorMsg = "The two passwords you entered were inconsistent.";
        }else{
            try{
                usrService.registerNewUser(usrn,pwd1,email);
            } catch (InvalidInsert invalidInsert) {
                errorMsg = "The username has already existed.";
            } catch (InvalidFormat invalidFormat){
                errorMsg = "The format of email is incorrect.";
            }
        }

        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        String path = null;
        if(errorMsg!=null){
            path = "/Register.html";
            ctx.setVariable("errorMsg", errorMsg);
        }else{
            path = "/index.html";
            ctx.setVariable("errorMsg","Succeed in registration.");
        }
        templateEngine.process(path, ctx, response.getWriter());

    }

    public void destroy(){

    }


}
