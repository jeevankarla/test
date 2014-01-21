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

public class Screens extends JPanel {

    public void action() {
            String path = "/home/vadmin/Desktop/VBiz0.1/applications/party/widget/partymgr";
            String files;
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {

                if (listOfFiles[i].isFile()) {
                    files = listOfFiles[i].getName().toString();
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
                        Label l = new Label(0, 0, "Screen name", cf);
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
                        XPathExpression expr = XPathUtil.getXPathExpression("/screens/screen");
                        NodeList screens = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);


                        for (int j = 0, p = 0; p < screens.getLength(); j++, p++) {
                            System.out.println("");
                            XPathExpression servicesm = XPathUtil.getXPathExpression("/screens/screen");
                            NodeList servi = (NodeList) servicesm.evaluate(doc, XPathConstants.NODESET);
                            XPathExpression expr1 = XPathUtil.getXPathExpression("@name");
                            l = new Label(0, p + j + 1, (String) expr1.evaluate(screens.item(j), XPathConstants.STRING).toString(), cf);
                            s.addCell(l);
                            l = new Label(1, p + j + 1, filepath, cf);
                            s.addCell(l);
                            XPathExpression efields = XPathUtil.getXPathExpression("section/actions/set");
                            NodeList fields = (NodeList) efields.evaluate(screens.item(j), XPathConstants.NODESET);


                            for (int f = 0; f < fields.getLength(); f++) {
                                XPathExpression expfield = XPathUtil.getXPathExpression("@value");
                                l = new Label(2, f + j + 1, expfield.evaluate(fields.item(f), XPathConstants.STRING).toString(), cf);
                                s.addCell(l);
                            }


                            XPathExpression screenservice = XPathUtil.getXPathExpression("section/actions/service");
                             NodeList services = (NodeList) screenservice.evaluate(screens.item(j), XPathConstants.NODESET);
                             
                             
                             for (int f = 0; f < services.getLength(); f++) {
                           XPathExpression servicename = XPathUtil.getXPathExpression("@service-name");
                            l = new Label(3,f+j+1, (String) servicename.evaluate(services.item(f), XPathConstants.STRING), cf);
                            s.addCell(l);
                             }
                            XPathExpression screenentity = XPathUtil.getXPathExpression("section/actions/entity-one");
                            NodeList entities = (NodeList) screenentity.evaluate(screens.item(j), XPathConstants.NODESET);


                            for (int f = 0; f < entities.getLength(); f++) {
                                XPathExpression expfield = XPathUtil.getXPathExpression("@entity-name");
                                String entityname = expfield.evaluate(entities.item(f), XPathConstants.STRING).toString();
                                l = new Label(4,f+ j + 1, expfield.evaluate(entities.item(f), XPathConstants.STRING).toString(), cf);
                                s.addCell(l);
                                System.out.println("Entity:" + entityname);
                            }


                            XPathExpression includescreenname = XPathUtil.getXPathExpression("section/widgets/include-screen");
                            NodeList incscreen = (NodeList) includescreenname.evaluate(screens.item(j), XPathConstants.NODESET);


                            for (int f = 0; f < incscreen.getLength(); f++) {
                                XPathExpression expfield = XPathUtil.getXPathExpression("@name");
                                //String incscreenname = (String) expfield.evaluate(incscreen.item(f), XPathConstants.STRING);
                                l = new Label(5, j+f + 1, (String) expfield.evaluate(incscreen.item(f), XPathConstants.STRING), cf);
                                s.addCell(l);
                                XPathExpression includescreenlocation = XPathUtil.getXPathExpression("section/widgets/include-screen/@location");
                                l = new Label(6, f+j + 1, (String) includescreenlocation.evaluate(screens.item(j), XPathConstants.STRING), cf);
                                s.addCell(l);

                            }
                            j=j+Math.max(Math.max(Math.max(fields.getLength(), entities.getLength()), incscreen.getLength()),services.getLength());
                        }
                        workbook.write();
                        workbook.close();
                    } catch (Exception e){
                    }
                }  
            }
    }

    public static void main(String args[]) {
        Screens ex = new Screens();
        ex.action();
    }
}

 