package documentation;

import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import java.beans.Statement;
import java.io.File;
import java.util.Locale;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.w3c.dom.NodeList;

/**
 *
 * @author vadmin
 */
public class Master
{
     private static WorkbookSettings ws;
    private static WritableWorkbook workbook;
    private static WritableSheet s;
    private static Label l;
    public static Statement st;
 public static  void main(String[] arr) throws Exception {

       try{
            ws= new WorkbookSettings();
                        ws.setLocale(new Locale("en", "EN"));
                        workbook = Workbook.createWorkbook(new File("entfiles.xls"), ws);
                        s = workbook.createSheet("Sheet1", 0);
                        l = new Label(0, 0, "Child Table Name");
                        s.addCell(l);
                        l = new Label(1, 0, "Master Table Name");
                        s.addCell(l);
                        l = new Label(2, 0, "Refferance field Master table");
                        s.addCell(l);
                        l = new Label(3, 0, "Reffered field Child table");
                        s.addCell(l);
                        l = new Label(4, 0, "");
                        s.addCell(l);
        org.w3c.dom.Document doc = (org.w3c.dom.Document) XMLUtil.getDocument("/home/vadmin/Desktop/VBiz_src/applications/humanres/entitydef/entitymodel.xml");
        XPathExpression expr = XPathUtil.getXPathExpression("/entitymodel/entity");
        NodeList entitys = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        //System.out.println(entitys.getLength());
        for (int i = 0,p=1; i < entitys.getLength(); i++,p++)
        {
             XPathExpression entiyname = XPathUtil.getXPathExpression("@entity-name");
             String entityname= (String) entiyname.evaluate(entitys.item(i), XPathConstants.STRING).toString();
             System.out.println("Child Table Name:"+entityname);
             XPathExpression referancetable = XPathUtil.getXPathExpression("relation");
             NodeList fktables = (NodeList) referancetable.evaluate(entitys.item(i), XPathConstants.NODESET);
            int j;
             for(j=0;j<fktables.getLength();j++)
             {
                 l = new Label(0, p+j, entityname);
                        s.addCell(l);

             XPathExpression refftablename = XPathUtil.getXPathExpression("@rel-entity-name");
             String refftable=(String) refftablename.evaluate(fktables.item(j), XPathConstants.STRING).toString();
             System.out.println("Master Table Name:"+refftable);
             l = new Label(1, p+j, refftable);
                        s.addCell(l);
             XPathExpression refffieldname = XPathUtil.getXPathExpression("key-map/@field-name");
             String fieldname =(String) refffieldname.evaluate(fktables.item(j), XPathConstants.STRING).toString();

             XPathExpression mrefffieldname = XPathUtil.getXPathExpression("key-map/@rel-field-name");
             String reffieldname =(String) mrefffieldname.evaluate(fktables.item(j), XPathConstants.STRING).toString();
             if(reffieldname=="")
             {
             System.out.println("Refferance field Master table:"+fieldname);
             l = new Label(2, p+j, fieldname);
                        s.addCell(l);
             }
 else{
             System.out.println("Refferance field Master table:"+reffieldname);
             l = new Label(2, p+j, reffieldname);
                        s.addCell(l);
                 }
             System.out.println("Reffered field Child table:"+fieldname);
             l = new Label(3, p+j, fieldname);
                        s.addCell(l);
                   
            }
            p=p+j-1;
             System.out.println();
           }
        workbook.write();
        workbook.close();
    }catch(Exception e)
       {
e.printStackTrace();
     }
    }
}