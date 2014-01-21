/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.write.WritableWorkbook;

/**
 *
 * @author sindukur
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String loc = "./home/vadmin/Desktop/VBiz0.1/common.xml";
        Map<String, EntityBase> data = getEntityMap(loc);
        for(String name: data.keySet()){
            EntityBase e= data.get(name);
          
            for(Relation r :e.getRelations()){
                String rel = r.getRelEntity();
                if(!data.containsKey(rel)){
                    System.out.println("------------------------------------------------");
                    System.out.println("Source Entry Name : "+e.getEntityName());
                    System.out.println("Source Entry Owner : "+e.getOwner());
                    System.out.println("Missing Entry Name : "+rel);

                }

            }
            if(e instanceof ViewEntity){
                ViewEntity ve = (ViewEntity)e;
                for(MemberEntity me :ve.getMemberEntitries()){
                  

                    if(!data.containsKey(me.getEntityName())){
                        System.out.println("------------------------------------------------");
                    System.out.println("Source view Entry Name : "+e.getEntityName());
                    System.out.println("Source view Entry Owner : "+e.getOwner());
                    System.out.println("Missing Entry Name : "+me.getEntityName());
                    }
                }
            }
        }


    }


    public static Map<String, EntityBase> getEntityMap(String loc) throws Exception {
        Map<String, List<String>> entitiesList = EntityXMLUtil.getEntityFilesWithProducts(loc);

        Map<String, EntityBase> data = new HashMap<String, EntityBase>();
        for (String app : entitiesList.keySet()) {

            for (String file : entitiesList.get(app)) {
                for (EntityBase e : EntityXMLUtil.readEntityFile(file, app)) {
                    data.put(e.getEntityName(), e);
                }

            }
        }
        return data;
    }

public static void writeEntitiesToExce() throws Exception {
        WritableWorkbook workbook = Workbook.createWorkbook(new File("d:\\OfBiz10.4.xls"));

        Map<String, List<String>> entitiesList = EntityXMLUtil.getEntityFilesWithProducts("D:\\SrinivasaRajuI\\Vasista\\Projects\\VBiz\\CODE\\10.4\\untouched release10.04");
        int i = 0;
        for (String app : entitiesList.keySet()) {
            List<EntityBase> data = new ArrayList<EntityBase>();
            for (String file : entitiesList.get(app)) {

                data.addAll(EntityXMLUtil.readEntityFile(file, app));
            }
            EntityExcelUtil.writeToExcel(workbook, app, i, data);
            i++;
        }
        workbook.write();
        workbook.close();

    }
}
