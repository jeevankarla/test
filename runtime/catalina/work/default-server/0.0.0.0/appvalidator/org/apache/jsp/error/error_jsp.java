package org.apache.jsp.error;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.*;

public final class error_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
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
      response.setContentType("text/html");
      response.addHeader("X-Powered-By", "JSP/2.1");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("<title>Open For Business Message</title>\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n");
      out.write("</head>\n");
      out.write("\n");
 String errorMsg = (String) request.getAttribute("_ERROR_MESSAGE_"); 
      out.write("\n");
      out.write("\n");
      out.write("<body bgcolor=\"#FFFFFF\">\n");
      out.write("<div align=\"center\">\n");
      out.write("  <br/>\n");
      out.write("  <table width=\"100%\" border=\"1\" height=\"200\">\n");
      out.write("    <tr>\n");
      out.write("      <td>");
      out.write("\n");
      out.write("\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("<title>vbiz- Vasista</title>\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n");
      out.write("<link rel=\"shortcut icon\" href=\"/images/vbiz.ico\" />\n");
      out.write("</head>\n");
      out.write("\n");
 String errorMsg = (String) request.getAttribute("_ERROR_MESSAGE_"); 
      out.write("\n");
      out.write("\n");
      out.write("<body bgcolor=\"#FFFFFF\">\n");
      out.write("<div align=\"center\">\n");
      out.write("  <br/>\n");
      out.write("  <table width=\"100%\" border=\"1\" height=\"200\">\n");
      out.write("    <tr>\n");
      out.write("      <td>\n");
      out.write("        <table width=\"100%\" border=\"0\" height=\"200\">\n");
      out.write("          <tr bgcolor=\"#F97103\">\n");
      out.write("            <td height=\"45\">\n");
      out.write("              <div align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"4\" color=\"#FFFFFF\"><b>:ERROR MESSAGE:</b></font></div>\n");
      out.write("            </td>\n");
      out.write("          </tr>\n");
      out.write("          <tr>\n");
      out.write("            <td>\n");
      out.write("              <div align=\"left\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\">");
      out.print(UtilFormatOut.replaceString(errorMsg, "org.ofbiz.webapp.control.RequestHandlerException", "Vbiz Exception"));
      out.write("</font></div>\n");
      out.write("            </td>\n");
      out.write("          </tr>\n");
      out.write("        </table>\n");
      out.write("      </td>\n");
      out.write("    </tr>\n");
      out.write("  </table>\n");
      out.write("</div>\n");
      out.write("<div align=\"center\"><img src=\"/vasista/powered_by_vasista.gif\"></div>\n");
      out.write("\n");
      out.write("</body>\n");
      out.write("</html>\n");
      out.write("      \n");
      out.write("        <table width=\"100%\" border=\"0\" height=\"200\">\n");
      out.write("          <tr bgcolor=\"#CC6666\">\n");
      out.write("            <td height=\"45\">\n");
      out.write("              <div align=\"center\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"4\" color=\"#FFFFFF\"><b>:ERROR MESSAGE:</b></font></div>\n");
      out.write("            </td>\n");
      out.write("          </tr>\n");
      out.write("          <tr>\n");
      out.write("            <td>\n");
      out.write("              <div align=\"left\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\">");
      out.print(UtilFormatOut.replaceString(errorMsg, "\n", "<br/>"));
      out.write("</font></div>\n");
      out.write("            </td>\n");
      out.write("          </tr>\n");
      out.write("        </table>\n");
      out.write("      </td>\n");
      out.write("    </tr>\n");
      out.write("  </table>\n");
      out.write("</div>\n");
      out.write("<div align=\"center\"></div>\n");
      out.write("</body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
