package nts.uk.ctx.sys.auth.app.query.role;


import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ロールリストを取得する
 * UKDesign.UniversalK.共通.KCP_共通部品.CCG025_ロールリスト.メニュー別OCD.ロールリストを取得する
 */

@Stateless
public class GetRoleListQuery {
    @Inject
    RoleRepository roleRepository;

    public List<DtoRole> getListRole(RolesParam param) {
        RoleType roleType = EnumAdaptor.valueOf(param.getRoleType(), RoleType.class);

        RoleAtr assignAtr;
        if (param.getAssignAtr() == null) {
            assignAtr = RoleAtr.INCHARGE;
        } else {
            assignAtr =  EnumAdaptor.valueOf(param.getAssignAtr(), RoleAtr.class);
        }
        val cid = AppContexts.user().companyId();
        val domains = roleRepository.findByTypeAndRoleAtr(cid, roleType.value, assignAtr.value);
        return domains.stream().sorted(Comparator.comparing(e -> e.getRoleCode().v()))
                .map(DtoRole::fromDomain).collect(Collectors.toList());
    }
}
