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
public class EntityBase {
private String entityName;
private String packageName;
private String title;
private String owner;
private List<Relation> relations;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
