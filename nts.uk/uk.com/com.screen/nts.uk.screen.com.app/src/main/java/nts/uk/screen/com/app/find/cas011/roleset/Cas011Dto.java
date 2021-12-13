package nts.uk.screen.com.app.find.cas011.roleset;


import lombok.Data;
import nts.uk.ctx.sys.portal.app.find.webmenu.WebMenuSimpleDto;



import java.util.List;

@Data
public class Cas011Dto {
    private List<RoleDto> rolesPersonalInfo;
    private List<RoleDto> rolesEmployment;
    private List<WebMenuSimpleDto> webMenuSimpleDtos;
    private RoleSetAndRoleDefaultDto roleDefaultDto;
}
