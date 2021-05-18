import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.entities.User;
import it.polimi.db2.exceptions.HasBeenBlocked;
import it.polimi.db2.exceptions.InvalidInsert;
import it.polimi.db2.exceptions.OffensiveWordsInsert;
import it.polimi.db2.services.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@WebServlet("/SubmitQuestionnaire")
public class SubmitQuestionnaire extends HttpServlet {

    @EJB(name="it.polimi.db2.services/UserService")
    private UserService uService;
    @EJB(name="it.polimi.db2.services/ProductService")
    private ProductService pService;
    @EJB(name="it.polimi.db2.services/QuestionnaireService")
    private QuestionnaireService qnService;
    @EJB(name="it.polimi.db2.services/MarketingAnswerService")

    private MarketingQuestionService mktqService;

    private TemplateEngine templateEngine;

    String age=null;
    String gender=null;
    String expLevel=null;

    public SubmitQuestionnaire(){
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
        // Redirect to the Home page and add missions to the parameters
        ServletContext servletContext = getServletContext();
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }


        age = StringEscapeUtils.escapeJava(request.getParameter("age"));
        request.getSession().setAttribute("age",age);
        gender=StringEscapeUtils.escapeJava(request.getParameter("gender"));
        request.getSession().setAttribute("gender",gender);
        expLevel=StringEscapeUtils.escapeJava(request.getParameter("expLevel"));
        request.getSession().setAttribute("expLevel",expLevel);

        int pId= 0;
        try {
            pId = pService.getTodayProductId();
        } catch (NoResultException noResultException) {
            pId = -1;
        }

        LocalDateTime time=LocalDateTime.now();

        User user= (User) request.getSession().getAttribute("user");
        int uId = 0;
        uId = user.getId();

        if(StringEscapeUtils.escapeJava(request.getParameter("complete")).equals("Previous Page")){
            String path = getServletContext().getContextPath() + "/MktQuestionPage";
            response.sendRedirect(path);
        }else if (StringEscapeUtils.escapeJava(request.getParameter("complete")).equals("Submit")){
//            System.out.println("#############submit1###############");
            String mktAnswer = null;
            String new_gender = null;

//            System.out.println("#############submit2###############");

            if(pId>0){
                int int_age=0;
                if(age.length()>0){
                    int_age=Integer.parseInt(age);
                }

                try{
                    if(gender.equals("Male")){
                        new_gender="m";
                    }else if(gender.equals("Female")){
                        new_gender="f";
                    }else{
                        new_gender="0";
                    }

                    System.out.println(gender.equals("Male"));
                    System.out.println(new_gender);

                    if(expLevel.length() == 0){
                        expLevel="0";
                    }

                    Map<MarketingQuestion,String> mktqaMap =
                            (Map<MarketingQuestion,String>) session.getAttribute("mktqaMap");

                    System.out.println(user.getId()+","+pId+","+int_age+","+new_gender+","+expLevel+","+time+","+mktqaMap);
                    qnService.submitQuestionnaire(uId,pId,int_age,new_gender,expLevel,time,mktqaMap);

                    System.out.println("submit Questionnaire successfully");

                    request.getSession().setAttribute("errorMsgHome","You submit the questionnaire successfully.");
                    String path = null;
                    path = getServletContext().getContextPath() + "/UserHome";
                    response.sendRedirect(path);

                }catch(OffensiveWordsInsert exception){
//                    System.out.println("******************"+uId);
                    uService.blockUserById(uId);
                    System.out.println("OffensiveWordsInsert,blocked.");
                    request.getSession().setAttribute("errorMsgHome","Attention: Your account is blocked, since the system detected the offsensive words in the answers.");
                    String path = null;
                    path = getServletContext().getContextPath() + "/UserHome";
                    response.sendRedirect(path);
                }catch (HasBeenBlocked hasBeenBlocked){
                    request.getSession().setAttribute("errorMsgHome","Attention: Your account is blocked.");
                    String path = null;
                    path = getServletContext().getContextPath() + "/UserHome";
                    response.sendRedirect(path);
                }catch (InvalidInsert exception){
                    request.getSession().setAttribute("errorMsgHome","Attention: Failed, since you have already submitted a questionnaire today.");
                    System.out.println("InvalidInsert");
                    String path = null;
                    path = getServletContext().getContextPath() + "/UserHome";
                    response.sendRedirect(path);
                }
            }
        }else if (StringEscapeUtils.escapeJava(request.getParameter("complete")).equals("Cancel")){
            try{
                qnService.cancelAQuestionnaire(uId,pId,time);
                request.getSession().setAttribute("errorMsgHome","You cancel the questionnaire successfully.");
                String path = null;
                path = getServletContext().getContextPath() + "/UserHome";
                response.sendRedirect(path);
            }catch(HasBeenBlocked hasBeenBlocked){
                request.getSession().setAttribute("errorMsgHome","Attention: Your account is blocked.");
                String path = null;
                path = getServletContext().getContextPath() + "/UserHome";
                response.sendRedirect(path);
            }catch (InvalidInsert invalidInsert){
                request.getSession().setAttribute("errorMsgHome","Attention: Failed, since you have already submitted a questionnaire today.");
                String path = null;
                path = getServletContext().getContextPath() + "/UserHome";
                response.sendRedirect(path);
            }
        }

    }


    public void destroy() {
    }


}
