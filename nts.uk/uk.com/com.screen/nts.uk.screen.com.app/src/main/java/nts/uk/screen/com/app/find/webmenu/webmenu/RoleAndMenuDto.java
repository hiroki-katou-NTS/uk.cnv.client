package nts.uk.screen.com.app.find.webmenu.webmenu;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;

import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
public class RoleAndMenuDto {
    private Optional<RoleByRoleTies> roleTies;
    private Optional<Role> role;
}
