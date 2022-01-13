package nts.uk.screen.com.app.find.webmenu.webmenu;


import lombok.val;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;


/**
 * ScreenQuery: ロールとWebメニュー紐付けを取得する
 */
@Stateless
public class GetRoleAndWebMenuScreenQuery {
    @Inject
    GetRoleScreenQuery getRoleScreenQuery;

    @Inject
    private RoleByRoleTiesRepository roleTiesRepository;

    public RoleAndMenuDto getRoleAndWebMenu(RolesParam param) {
        val cid = AppContexts.user().companyId();
        Optional<Role> role = getRoleScreenQuery.getRole(param);
        Optional<RoleByRoleTies> roleByRoleTies =
                roleTiesRepository.getByRoleIdAndCompanyId(param.getRoleId(), cid);
        return new RoleAndMenuDto(roleByRoleTies, role);
    }
}
