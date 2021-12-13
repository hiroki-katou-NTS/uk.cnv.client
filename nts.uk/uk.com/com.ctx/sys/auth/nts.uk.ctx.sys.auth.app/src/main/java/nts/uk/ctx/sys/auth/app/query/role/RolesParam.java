package nts.uk.ctx.sys.auth.app.query.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.sys.auth.dom.role.RoleType;


@AllArgsConstructor
@Getter
public class RolesParam {
    private int roleType;
    private Integer assignAtr;
}
