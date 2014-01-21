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
public class ViewEntity extends EntityBase{
private List<MemberEntity> memberEntitries;

    public List<MemberEntity> getMemberEntitries() {
        return memberEntitries;
    }

    public void setMemberEntitries(List<MemberEntity> memberEntitries) {
        this.memberEntitries = memberEntitries;
    }

}
