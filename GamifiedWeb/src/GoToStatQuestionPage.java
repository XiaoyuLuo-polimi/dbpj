import it.polimi.db2.entities.MarketingQuestion;
import it.polimi.db2.entities.Product;
import it.polimi.db2.services.MarketingQuestionService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet("/StatQuestionPage")
public class GoToStatQuestionPage extends HttpServlet {

    int QuestionAnsweredNum=0;

    @EJB(name="it.polimi.db2.services/MarketingQuestionService")
    private MarketingQuestionService mktqService;

    private TemplateEngine templateEngine;

//    public GoToStatQuestionPage(){
//        super();
//    }

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

        String path = "/WEB-INF/StatisticalQuestion.html";
        ServletContext servletContext = getServletContext();
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }

        Product product= (Product) request.getSession().getAttribute("product");

        List<MarketingQuestion> mktQuestions = null;
        mktQuestions = product.getMarketingQuestionsList();

        if (mktQuestions != null) {
            String mktAnswer = null;
            for (int i = 0; i < mktQuestions.size(); i++) {
                mktAnswer = StringEscapeUtils.escapeJava(request.getParameter("mktq" + String.valueOf(mktQuestions.get(i).getId())));
                request.getSession().setAttribute("mkta" + String.valueOf(mktQuestions.get(i).getId()), mktAnswer);
                if (mktAnswer.length()>0) {
                    QuestionAnsweredNum = QuestionAnsweredNum + 1;
                }
            }


            if (QuestionAnsweredNum == mktQuestions.size()) {
//                System.out.println("###################"+product.getName());
                Map<MarketingQuestion,String> questionAnswerMap = new HashMap<>();

                //put Marketing Question and the corresponding answer into the Map.
                for(MarketingQuestion marketingQuestion: product.getMarketingQuestionsList()){
//                    System.out.println(String.valueOf(marketingQuestion.getId()));
                    mktAnswer = StringEscapeUtils.escapeJava(request.getParameter("mktq" + String.valueOf(marketingQuestion.getId())));
//                    System.out.println(mktAnswer);
                    questionAnswerMap.put(marketingQuestion,mktAnswer);
                }
                request.getSession().setAttribute("mktqaMap",questionAnswerMap);
//                request.getSession().setAttribute("errorMsg",null);
                QuestionAnsweredNum=0;

                final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
                templateEngine.process(path, ctx, response.getWriter());
            } else {
                request.getSession().setAttribute("errorMsg","Attention:You have "+(mktQuestions.size()-QuestionAnsweredNum)+" question(s) that have not been anwered.");
                path = getServletContext().getContextPath() + "/MktQuestionPage";
                QuestionAnsweredNum=0;
                response.sendRedirect(path);
            }

        }else if(mktQuestions==null){
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsgStat", "No statistical questions");
            path = "/WEB-INF/StatisticalQuestion.html";
            templateEngine.process(path, ctx, response.getWriter());
        }
    }


    public void destroy() {
    }





}
