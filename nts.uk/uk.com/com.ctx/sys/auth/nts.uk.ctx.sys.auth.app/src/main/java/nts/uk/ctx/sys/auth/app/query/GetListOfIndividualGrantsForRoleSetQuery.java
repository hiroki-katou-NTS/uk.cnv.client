package nts.uk.ctx.sys.auth.app.query;


import lombok.val;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Query: ロールセット個人別付与一覧を取得する
 *
 * @author : chinh.hm
 */
@Stateless
public class GetListOfIndividualGrantsForRoleSetQuery {
    @Inject
    private RoleSetGrantedPersonRepository roleSetGrantedPersonRepository;

    public List<RoleSetGrantedPerson> getListIndividualRollSetGrant() {
        val cid = AppContexts.user().companyId();
        return roleSetGrantedPersonRepository.getByCid(cid);
    }
}
