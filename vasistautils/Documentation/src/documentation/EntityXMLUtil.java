/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentation;

import in.vasista.util.FileUtil;
import in.vasista.util.XMLUtil;
import in.vasista.util.XPathUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author sindukur
 */
public class EntityXMLUtil {
    public static Map<String,String> getAppNamesWithLocation(String homePath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
        Map<String,String> data= new HashMap<String,String>();

        List<File> componets = FileUtil.listFiles(new File(homePath),"component-load.xml",true);
        for(File f: componets){
            Log.LogInfoMessage(f.getAbsolutePath());
            Document doc = XMLUtil.getDocument(f);
             XPathExpression expr = XPathUtil.getXPathExpression("/component-loader/load-component/@component-location");
             NodeList apps = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < apps.getLength(); i++) {
                String l1= apps.item(i).getNodeValue();
                Log.LogInfoMessage(f.getParent()+File.separatorChar+l1);
                data.put(l1,f.getParent()+File.separatorChar+l1);
            }
        }
        return data;
    }
    public static Map<String,List<String>> getEntityFilesWithProducts(String homePath) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Log.LogInfoMessage("readEntityFile started for " + homePath);
        List<String> files;
        Map<String,List<String>> returnValue = new HashMap<String,List<String>>();
        Map<String,String> componets = getAppNamesWithLocation(homePath);
        for(String c : componets.values()){
             Document doc = XMLUtil.getDocument(c+File.separatorChar+"ofbiz-component.xml");
             XPathExpression expr = XPathUtil.getXPathExpression("/ofbiz-component/@name");
             String Name = (String) expr.evaluate(doc, XPathConstants.STRING);
             Log.LogInfoMessage(Name);
             expr = XPathUtil.getXPathExpression("/ofbiz-component/entity-resource[@type='model']/@location");
            NodeList locations = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            List<String> entityFiles = new ArrayList<String>();
            for (int i = 0; i < locations.getLength(); i++) {
                String l1= locations.item(i).getNodeValue();
                String loc = c+File.separator+l1;
                entityFiles.add(loc);
                Log.LogInfoMessage(loc);

            }
            if(entityFiles.size()>0){
                returnValue.put(Name, entityFiles);
            }

        }
          Log.LogInfoMessage("readEntityFile ended for " + homePath);
        return returnValue;
    }
    public static List<EntityBase> readEntityFile(String filePath,String owner) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Log.LogInfoMessage("readEntityFile started for " + filePath);
        File f = new File(filePath);
        Log.LogInfoMessage("readEntityFile started for " + f.exists());
        if(!f.exists())
        {
            Log.LogDebugMessage("File missing:"+filePath);
            return new ArrayList<EntityBase>();
        }
        Document doc = XMLUtil.getDocument(f);
        XPathExpression expr = XPathUtil.getXPathExpression("/entitymodel/entity");

        NodeList entites = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        List<EntityBase> EntitiesList = new ArrayList<EntityBase>();
        for (int i = 0; i < entites.getLength(); i++) {
            Entity ent = new Entity();
            ent.setOwner(owner);
            expr = XPathUtil.getXPathExpression("@entity-name");
            String entityName = (String) expr.evaluate(entites.item(i), XPathConstants.STRING);
            ent.setEntityName(entityName);
            expr = XPathUtil.getXPathExpression("@package-name");
            String packageName = (String) expr.evaluate(entites.item(i), XPathConstants.STRING);
            ent.setPackageName(packageName);
            expr = XPathUtil.getXPathExpression("@title");
            String title = (String) expr.evaluate(entites.item(i), XPathConstants.STRING);
            ent.setTitle(title);
            expr = XPathUtil.getXPathExpression("field");
            NodeList attributes = (NodeList) expr.evaluate(entites.item(i), XPathConstants.NODESET);
            List<Attribute> AttributesList = new ArrayList<Attribute>();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attribute attr = new Attribute();
                expr = XPathUtil.getXPathExpression("@name");
                String attrName = (String) expr.evaluate(attributes.item(j), XPathConstants.STRING);
                attr.setName(attrName);
                expr = XPathUtil.getXPathExpression("@type");
                String attrType = (String) expr.evaluate(attributes.item(j), XPathConstants.STRING);
                attr.setType(attrType);
                AttributesList.add(attr);
            }
            ent.setAttr(AttributesList);
            expr = XPathUtil.getXPathExpression("prim-key");
            NodeList pks = (NodeList) expr.evaluate(entites.item(i), XPathConstants.NODESET);
            List<PrimaryKey> PrimaryKeys = new ArrayList<PrimaryKey>();
            for (int j = 0; j < pks.getLength(); j++) {
                PrimaryKey pk = new PrimaryKey();
                expr = XPathUtil.getXPathExpression("@field");
                String attrName = (String) expr.evaluate(pks.item(j), XPathConstants.STRING);
                pk.setAttrName(attrName);
                PrimaryKeys.add(pk);
            }
            ent.setPks(PrimaryKeys);
            expr = XPathUtil.getXPathExpression("relation");
            NodeList relations = (NodeList) expr.evaluate(entites.item(i), XPathConstants.NODESET);
            List<Relation> Relations = new ArrayList<Relation>();
            for (int j = 0; j < relations.getLength(); j++) {
                Relation r1 = new Relation();
                expr = XPathUtil.getXPathExpression("@type");
                String type = (String) expr.evaluate(relations.item(j), XPathConstants.STRING);
                r1.setType(type);
                expr = XPathUtil.getXPathExpression("@fk-name");
                String fkName = (String) expr.evaluate(relations.item(j), XPathConstants.STRING);
                r1.setFkName(fkName);
                expr = XPathUtil.getXPathExpression("@rel-entity-name");
                String relEntityName = (String) expr.evaluate(relations.item(j), XPathConstants.STRING);
                r1.setRelEntity(relEntityName);
                expr = XPathUtil.getXPathExpression("key-map");
                NodeList keymap = (NodeList) expr.evaluate(relations.item(j), XPathConstants.NODESET);
                List<String> fieldsNames = new ArrayList<String>();
                for (int k = 0; k < keymap.getLength(); k++) {
                    expr = XPathUtil.getXPathExpression("@field-name");
                    String fieldName = (String) expr.evaluate(keymap.item(k), XPathConstants.STRING);
                    fieldsNames.add(fieldName);
                }
                r1.setFieldsName(fieldsNames);
                Relations.add(r1);
            }
            ent.setRelations(Relations);
            EntitiesList.add(ent);
        }
//For View
         expr = XPathUtil.getXPathExpression("/entitymodel/view-entity");

         entites = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
         Log.LogDebugMessage(" COUNT:"+entites.getLength());
        
        for (int i = 0; i < entites.getLength(); i++) {
            ViewEntity ent = new ViewEntity();
            ent.setOwner(owner);
            expr = XPathUtil.getXPathExpression("@entity-name");
            String entityName = (String) expr.evaluate(entites.item(i), XPathConstants.STRING);
            Log.LogInfoMessage(" View entity "+entityName);
            ent.setEntityName(entityName);
            expr = XPathUtil.getXPathExpression("@package-name");
            String packageName = (String) expr.evaluate(entites.item(i), XPathConstants.STRING);
            ent.setPackageName(packageName);
            expr = XPathUtil.getXPathExpression("@title");
            String title = (String) expr.evaluate(entites.item(i), XPathConstants.STRING);
            ent.setTitle(title);
            expr = XPathUtil.getXPathExpression("field");

            expr = XPathUtil.getXPathExpression("member-entity");
            NodeList memeberE = (NodeList) expr.evaluate(entites.item(i), XPathConstants.NODESET);
            List<MemberEntity> MemberEntitys = new ArrayList<MemberEntity>();
            for (int j = 0; j < memeberE.getLength(); j++) {
                MemberEntity r1 = new MemberEntity();
                expr = XPathUtil.getXPathExpression("@entity-alias");
                String type = (String) expr.evaluate(memeberE.item(j), XPathConstants.STRING);
                Log.LogDebugMessage(type);
                r1.setEntityAlias(type);
                expr = XPathUtil.getXPathExpression("@entity-name");
                String fkName = (String) expr.evaluate(memeberE.item(j), XPathConstants.STRING);
                r1.setEntityName(fkName);
                Log.LogDebugMessage(fkName);

                MemberEntitys.add(r1);
            }
            ent.setMemberEntitries(MemberEntitys);


            expr = XPathUtil.getXPathExpression("relation");
            NodeList relations = (NodeList) expr.evaluate(entites.item(i), XPathConstants.NODESET);
            List<Relation> Relations = new ArrayList<Relation>();
            for (int j = 0; j < relations.getLength(); j++) {
                Relation r1 = new Relation();
                expr = XPathUtil.getXPathExpression("@type");
                String type = (String) expr.evaluate(relations.item(j), XPathConstants.STRING);
                r1.setType(type);
                expr = XPathUtil.getXPathExpression("@fk-name");
                String fkName = (String) expr.evaluate(relations.item(j), XPathConstants.STRING);
                r1.setFkName(fkName);
                expr = XPathUtil.getXPathExpression("@rel-entity-name");
                String relEntityName = (String) expr.evaluate(relations.item(j), XPathConstants.STRING);
                r1.setRelEntity(relEntityName);
                expr = XPathUtil.getXPathExpression("key-map");
                NodeList keymap = (NodeList) expr.evaluate(relations.item(j), XPathConstants.NODESET);
                List<String> fieldsNames = new ArrayList<String>();
                for (int k = 0; k < keymap.getLength(); k++) {
                    expr = XPathUtil.getXPathExpression("@field-name");
                    String fieldName = (String) expr.evaluate(keymap.item(k), XPathConstants.STRING);
                    fieldsNames.add(fieldName);
                }
                r1.setFieldsName(fieldsNames);
                Relations.add(r1);
            }
            ent.setRelations(Relations);
            EntitiesList.add(ent);
        }

        Log.LogInfoMessage("readEntityFile Ended for " + filePath);
        return EntitiesList;
    }
}
