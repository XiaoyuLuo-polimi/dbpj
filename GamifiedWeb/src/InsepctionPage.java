import it.polimi.db2.entities.User;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class InsepctionPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db2.services/UserService")
    private UserService userService;

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


        String path = "/WEB-INF/InspectionPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        if (session.getAttribute("errorMessage") != null){
            ctx.setVariable("errorMsg", session.getAttribute("errorMessage"));
            request.getSession().removeAttribute("errorMessage");
        }else if (session.getAttribute("date") != null){

            ctx.setVariable("submittedUserList", session.getAttribute("submittedUserList"));
            ctx.setVariable("cancelledUserList", session.getAttribute("cancelledUserList"));
            ctx.setVariable("date", session.getAttribute("date"));

            request.getSession().removeAttribute("submittedUserList");
            request.getSession().removeAttribute("cancelledUserList");
            request.getSession().removeAttribute("date");
        }

        templateEngine.process(path, ctx, response.getWriter());
    }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//
//        // If admin user is not logged in (not present in session) redirect to the login
//        String pathContext = getServletContext().getContextPath();
//        HttpSession session = request.getSession();
//        if (session.isNew() || session.getAttribute("admin") == null) {
//            response.sendRedirect(pathContext+ "/AdminLogin.html");
//            return;
//        }
//
//
//        Date date;
//        List<User> submittedUserList;
//        List<User> cancelledUserList;
//        try {
//            date = Date.valueOf(StringEscapeUtils.escapeJava(request.getParameter("Date")));
//
//            if (date == null) {
//                throw new Exception("No empty filed!");
//            }
//
//        }catch (Exception e){
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//            return;
//        }
//
//
//        try {
//            submittedUserList = uService.findUsersAnsweredQuestionsOnADay(date);
//            cancelledUserList = uService.findUsersCancelledQuestionnaireOnADay(date);
//        }catch (PersistenceException e){
//            request.getSession().setAttribute("errorMessage", e.getMessage());
//            response.sendRedirect(pathContext + "/AdminInspectionPage");
//            return;
//        }
//
//        if (submittedUserList.isEmpty() && cancelledUserList.isEmpty()){
//            request.getSession().setAttribute("errorMessage", "No value");
//            response.sendRedirect(pathContext + "/AdminInspectionPage");
//            return;
//        }
//
//        request.getSession().setAttribute("date", date);
//        request.getSession().setAttribute("submittedUserList", submittedUserList);
//        request.getSession().setAttribute("cancelledUserList", cancelledUserList);
//
//        response.sendRedirect(pathContext + "/AdminInspectionPage");
//
//    }

}
