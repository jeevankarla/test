package documentation;

import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import java.io.File;
import java.util.Locale;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Forms2 {

    public static void main(String[] args) {
try {
                    
        WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook =
            Workbook.createWorkbook(new File("workeffortforms.xls"), ws);
        // Directory path here
        String path = "/home/vadmin/Desktop/Vbiz_v1_01/applications/workeffort/widget";

        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                System.out.println();
                System.out.println();
                System.out.println(i + files);

                int z=1;
                     
            WritableSheet s = workbook.createSheet(files, 0);
            WritableFont wf = new WritableFont(WritableFont.ARIAL,10, WritableFont.BOLD);
            WritableCellFormat cf = new WritableCellFormat(wf);
            cf.setWrap(true);
                        Label l = new Label(0,0,"Form Name",cf);
                        s.addCell(l);
                        l = new Label(1,0,"Service",cf);
                        s.addCell(l);
                        l = new Label(2, 0, "FormPath",cf);
                        s.addCell(l);
                        l = new Label(3, 0, "Fields",cf);
                        s.addCell(l);
                        l = new Label(4, 0, "Set Fields",cf);
                        s.addCell(l);
                        l = new Label(5, 0, "Entity name",cf);
                        s.addCell(l);
                    System.out.println();
                    String filepath=path+"/"+files.toString();
                    Document doc = (Document) XMLUtil.getDocument(path + "/" + files.toString());
                    XPathExpression expr = XPathUtil.getXPathExpression("/forms/form");
                    NodeList formnode = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int j = 0; j< formnode.getLength(); j++) {
                        System.out.println("");
                        XPathExpression servicesm = XPathUtil.getXPathExpression("/forms/form");
                        NodeList servi = (NodeList) servicesm.evaluate(doc, XPathConstants.NODESET);
                        XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
                        System.out.print("Form Name:");
                        l = new Label(0, z, (String) expr1.evaluate(formnode.item(j), XPathConstants.STRING).toString());
                            s.addCell(l);
                        System.out.println((String) expr1.evaluate(formnode.item(j), XPathConstants.STRING).toString());
                        XPathExpression target = XPathUtil.getXPathExpression("@target");
                        l = new Label(1, z, (String) target.evaluate(formnode.item(j), XPathConstants.STRING).toString());
                            s.addCell(l);
                        System.out.print("Service:");
                        System.out.println((String) target.evaluate(formnode.item(j), XPathConstants.STRING).toString());

                       // System.out.println((String) expr1.evaluate(formnode.item(j), XPathConstants.STRING).toString());

                        System.out.println("FormPath:"+filepath);
                        l = new Label(2, z, filepath);
                            s.addCell(l);
                            String str="";
                        XPathExpression formfields = XPathUtil.getXPathExpression("field");
                        NodeList fieldnode=(NodeList)formfields.evaluate(formnode.item(j),XPathConstants.NODESET);
                        for(int f=0;f<fieldnode.getLength();f++)
                        {
                            XPathExpression fieldname = XPathUtil.getXPathExpression("@name");
                           String fieldss = (String) fieldname.evaluate(fieldnode.item(f), XPathConstants.STRING);
                           str=str+","+(String) fieldname.evaluate(fieldnode.item(f), XPathConstants.STRING);
                        System.out.println("Fields:" + fieldss);
                        }
                         l = new Label(3, z, str);
                            s.addCell(l);
                        XPathExpression set = XPathUtil.getXPathExpression("actions/set");
                        NodeList setnode=(NodeList)set.evaluate(formnode.item(j),XPathConstants.NODESET);
                        String str1="";
                        for(int f=0;f<setnode.getLength();f++)
                        {
                            XPathExpression setname = XPathUtil.getXPathExpression("@field");
                           String sets = (String) setname.evaluate(setnode.item(f), XPathConstants.STRING);
                           str1=str1+","+(String) setname.evaluate(setnode.item(f), XPathConstants.STRING);
                        System.out.println("Set Fields:" + sets);
                        }l = new Label(4, z, str1);
                            s.addCell(l);

                        XPathExpression entity = XPathUtil.getXPathExpression("actions/entity-and/@entity-name");
                        String entitys = (String) entity.evaluate(formnode.item(j), XPathConstants.STRING);
                        String str2="";
                        if(entitys!=null){
                            str2=str2+","+entitys;
                            System.out.println("Entity name:"+entitys);}
                      l = new Label(5, z, str2);
                            s.addCell(l);
                            z++;
                            System.out.println(z);
                    } 
                } 
            }workbook.write();

      workbook.close();
        }catch (Exception e) {
                    e.printStackTrace();
                }
    }
}