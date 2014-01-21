package documentation;

import jxl.write.*;
import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.File;
import org.w3c.dom.*;
import java.util.Locale;
import javax.swing.JPanel;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class Forms extends JPanel {

    public void action() {
            String path = "/home/vadmin/Desktop/VBiz0.1/applications/party/widget/partymgr";
            String files;
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
int z=1;
                if (listOfFiles[i].isFile()) {

                    files = listOfFiles[i].getName().toString();
                    if(files.endsWith("forms.xml")){
                    System.out.println();
                    System.out.println();
                    System.out.println(i + files);

                    try {
                        WorkbookSettings ws = new WorkbookSettings();
                        ws.setLocale(new Locale("en", "EN"));
                        WritableWorkbook workbook = Workbook.createWorkbook(new File(files+".xls"), ws);
                        WritableSheet s = workbook.createSheet("Sheet1", 0);
                        WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                        WritableCellFormat cf = new WritableCellFormat(wf);
                        cf.setWrap(true);
                        Label l = new Label(0, 0, "form name", cf);
                        s.addCell(l);
                        l = new Label(1, 0, "filepath", cf);
                        s.addCell(l);
                        l = new Label(2, 0, "fields", cf);
                        s.addCell(l);
                        l = new Label(3, 0, "services", cf);
                        s.addCell(l);
                        l = new Label(4, 0, "entities", cf);
                        s.addCell(l);
                        l = new Label(5, 0, "include screen", cf);
                        s.addCell(l);
                        l = new Label(6, 0, "include screen path", cf);
                        s.addCell(l);
                        System.out.println();
                        String filepath = path + "/" + files.toString();
                        Document doc = (Document) XMLUtil.getDocument(path + "/" + files.toString());
                        XPathExpression expr = XPathUtil.getXPathExpression("/forms/form");
                        NodeList screens = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                        for (int j = 0, p = 0; p < screens.getLength(); j++, p++) {        
                            XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
                            l = new Label(0, z, (String) expr1.evaluate(screens.item(p), XPathConstants.STRING).toString(), cf);
                            s.addCell(l);
                            XPathExpression target = XPathUtil.getXPathExpression("@target");
                            l = new Label(1, z, (String) target.evaluate(screens.item(p), XPathConstants.STRING).toString(), cf);
                            s.addCell(l);
                            l = new Label(2, z, filepath, cf);
                            s.addCell(l);
String stri="";
                            XPathExpression formset = XPathUtil.getXPathExpression("actions/set");
                             NodeList set = (NodeList) formset.evaluate(screens.item(j), XPathConstants.NODESET);
                             for(int f=0;f<set.getLength();f++)
                             {
                             XPathExpression expr11 = XPathUtil.getXPathExpression("@field");
                            stri=stri+","+(String) expr11.evaluate(screens.item(f), XPathConstants.STRING).toString();
                             }l = new Label(0, z,stri);
                            s.addCell(l);
                            String stri1="";
                            XPathExpression formfield = XPathUtil.getXPathExpression("field");
                             NodeList field = (NodeList) formfield.evaluate(screens.item(j), XPathConstants.NODESET);
                             for(int fj=0;fj<field.getLength();fj++)
                             {
                             XPathExpression expr112 = XPathUtil.getXPathExpression("@name");
                             stri1=stri+","+(String) expr112.evaluate(screens.item(fj), XPathConstants.STRING).toString();
                             }l = new Label(0, z,stri1);
                            s.addCell(l);
                       z++;
                       
                    } workbook.write();
                        workbook.close();
                } catch (Exception e){}
            }
    }}}

    public static void main(String args[]) {
        Forms ex = new Forms();
        ex.action();
    }
}

