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
public class Entity extends EntityBase{

private List<Attribute> attr;
private List<PrimaryKey> pks;

    public List<Attribute> getAttr() {
        return attr;
    }

    public void setAttr(List<Attribute> attr) {
        this.attr = attr;
    }

    public List<PrimaryKey> getPks() {
        return pks;
    }

    public void setPks(List<PrimaryKey> pks) {
        this.pks = pks;
    }

}
