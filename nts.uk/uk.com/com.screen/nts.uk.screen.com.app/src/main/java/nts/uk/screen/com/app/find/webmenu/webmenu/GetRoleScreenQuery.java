package nts.uk.screen.com.app.find.webmenu.webmenu;


import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

/**
 * ScreenQuery: ロールを取得する
 */
@Stateless
public class GetRoleScreenQuery {
    @Inject
    RoleRepository roleRepository;

    public Optional<Role> getRole(RolesParam param) {
        return roleRepository.findByRoleId(param.getRoleId());

    }
}
