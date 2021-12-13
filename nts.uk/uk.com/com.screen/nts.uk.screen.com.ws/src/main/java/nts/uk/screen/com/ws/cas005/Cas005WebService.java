package nts.uk.screen.com.ws.cas005;


import lombok.val;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuSimpleDto;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.screen.com.app.find.webmenu.webmenu.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Optional;

@Path("sys/portal/webmenu")
@Produces("application/json")

public class Cas005WebService extends WebService {
    @Inject
    GetWebMenuListScreenQuery getWebMenuListScreenQuery;

    @Inject
    GetRoleAndWebMenuScreenQuery getRoleAndWebMenuScreenQuery;

    @POST
    @Path("findallmenu")
    public List<WebMenuSimpleDto> findAllMenu() {
        return this.getWebMenuListScreenQuery.findAllMenu();
    }

    @POST
    @Path("findroleandwebmenu")
    public RoleAndMenuDto findRoleAndWebMenu(RolesParam param) {
        val rs = new RoleAndMenuDto();
        val domain = getRoleAndWebMenuScreenQuery.getRoleAndWebMenu(param);
        Optional<Role> roleOptional = domain.getRole();
        Optional<RoleByRoleTies> roleTiesOptional = domain.getRoleTies();
        if (roleTiesOptional.isPresent()) {
            val roleTies = roleTiesOptional.get();
            RoleByRoleTiesDto roleTiesDto = new RoleByRoleTiesDto(
                    roleTies.getRoleId(),
                    roleTies.getWebMenuCd().v(),
                    roleTies.getCompanyId());
            rs.setRoleByRoleTies(roleTiesDto);
        }
        if (roleOptional.isPresent()) {
            val role = roleOptional.get();
            rs.setRole(DtoRole.fromDomain(role));
        }
        return rs;
    }
}
