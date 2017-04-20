package org.apache.jsp.pages;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Enumeration;

public final class content_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList<String>(2);
    _jspx_dependants.add("/WEB-INF/jspf/header.jspf");
    _jspx_dependants.add("/WEB-INF/jspf/footer.jspf");
  }

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("        <title>Library</title>\n");
      out.write("        <link href=\"/03_Library_1/css/allPages.css\" rel=\"stylesheet\" type=\"text/css\">\n");
      out.write("        <link rel=\"shortcut icon\" href=\"/03_Library_1/favicon.ico\">\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        ");


            request.setCharacterEncoding("UTF-8");
            String searchString = "";

            if (request.getParameter("search_string") != null) {
                searchString = request.getParameter("search_string");
                session.setAttribute("search_string", searchString);
            } else if (session.getAttribute("search_string") != null) {
                searchString = session.getAttribute("search_string").toString();
            } else {
                session.setAttribute("search_string", searchString);
            }
            
            if (request.getParameter("username") != null) {
                if (request.getParameter("username").equals("")) session.setAttribute("username", "Аноним");
                else session.setAttribute("username", request.getParameter("username"));
            }
            else if (session.getAttribute("username")==null) session.setAttribute("username", "Аноним");

        
      out.write("\n");
      out.write("\n");
      out.write("        <div class=\"container\">\n");
      out.write("\n");
      out.write("            <div class=\"header\">\n");
      out.write("                <div class=\"logo\">\n");
      out.write("                    <a href=\"main.jsp\"><img src=\"../images/libraryLogo.png\" alt=\"Логотип\" name=\"logo\" /></a>\n");
      out.write("\n");
      out.write("                </div>\n");
      out.write("                <div class=\"descr\">\n");
      out.write("                    <h3>Онлайн библиотека проекта от скромняги Андрюльбана! <br/> Хорошие книги только у меня!</h3>\n");
      out.write("\n");
      out.write("                </div>\n");
      out.write("                <div class=\"welcome\">\n");
      out.write("                    <h5>Добро пожаловать, ");
      out.print(session.getAttribute("username"));
      out.write(" !</h5>\n");
      out.write("                    <h6><a href=\"/03_Library_1/index.html\">Выход</a></h6>\n");
      out.write("                            </div>\n");
      out.write("                            <div class=\"search_form\">\n");
      out.write("                                <form name=\"search_form\" method=\"GET\" action=\"books.jsp\">\n");
      out.write("                                    <input type=\"text\" name=\"search_string\" value=\"");
      out.print(searchString);
      out.write("\" size=\"105\"/>\n");
      out.write("                                    <input class=\"search_button\" type=\"submit\" value=\"Поиск\"/>\n");
      out.write("                                    <select name=\"search_option\">\n");
      out.write("                                        <option>Название</option>\n");
      out.write("                                        <option>Автор</option>\n");
      out.write("                                    </select>\n");
      out.write("                                </form>\n");
      out.write("                            </div>\n");
      out.write("                            </div>");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("        <title>JSP Page</title>\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("\n");
      out.write("        <div class=\"pdf_viewer\">\n");
      out.write("            <applet CODE=\"EmbedPDF.class\" archive=\"");
      out.print(getServletContext().getContextPath());
      out.write("/jars/EmbedPDF.jar\" WIDTH=\"850\" HEIGHT=\"900\">\n");
      out.write("                <!-- The URL of the PDF document that we want to show: -->\n");
      out.write("\n");
      out.write("                <param name=\"pdf\" value=\"");
      out.print( request.getContextPath());
      out.write("/PdfContent?index=");
      out.print(request.getParameter("index"));
      out.write("&session_id=");
      out.print(request.getSession().getId());
      out.write("\"/> \n");
      out.write("\n");
      out.write("                <!-- Whether users may open the PDF document in a new window: -->\n");
      out.write("                <param name=\"enableOpenWindow\" value=\"true\"/>\n");
      out.write("\n");
      out.write("                <!-- Whether the PDF is rendered with subpixel-antialiasing (may be slow and needs more memory) -->\n");
      out.write("                <param name=\"enableSubpixAA\" value=\"true\"/>\n");
      out.write("\n");
      out.write("\n");
      out.write("                <!-- The following parameters are recommended to improve usability and\n");
      out.write("                     performance of the applet when run with Sun's Java Plugin: -->\n");
      out.write("\n");
      out.write("                <!-- whether language-specific texts shall be looked up on the server. -->\n");
      out.write("                <param name=\"codebase_lookup\" value=\"false\"/>\n");
      out.write("\n");
      out.write("                <!-- whether the code of the applet shall be shared with other applets. -->\n");
      out.write("                <param name=\"classloader_cache\" value=\"false\"/>\n");
      out.write("\n");
      out.write("                <!-- Whether the server provides a highly compressed .pack.gz-version of the applet.\n");
      out.write("                     The amount of memory that the applet may use (128m is 128 mega bytes).\n");
      out.write("                -->\n");
      out.write("                <param name=\"java_arguments\" value=\"-Djnlp.packEnabled=true -Xmx128m\"/>\n");
      out.write("\n");
      out.write("                <!-- the splash screen to show, while the applet loads. -->\n");
      out.write("                <param name=\"image\" value=\"");
      out.print(getServletContext().getContextPath());
      out.write("/images/splash.gif\"/>\n");
      out.write("\n");
      out.write("                <!-- the border of the splash screen. -->\n");
      out.write("                <param name=\"boxborder\" value=\"false\"/>\n");
      out.write("\n");
      out.write("                <!-- whether the splash screen shall be centered. -->\n");
      out.write("                <param name=\"centerimage\" value=\"true\"/>\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("            </applet>\n");
      out.write("        </div>\n");
      out.write("    </body>\n");
      out.write("</html>\n");
      out.write("\n");
      out.write("\n");
      out.write("<div style=\"clear: both; width:1100px;\">&nbsp</div>\n");
      out.write("</div><!-- end .container -->\n");
      out.write("    </body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
