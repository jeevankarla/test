/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package documentation;

import java.util.List;

/**
 *
 * @author sindukur
 */
public class Relation {
String type;
String fkName;
String relEntity;
List<String> fieldsName;

    public List<String> getFieldsName() {
        return fieldsName;
    }

    public void setFieldsName(List<String> fieldsName) {
        this.fieldsName = fieldsName;
    }

    public String getFkName() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    public String getRelEntity() {
        return relEntity;
    }

    public void setRelEntity(String relEntity) {
        this.relEntity = relEntity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
