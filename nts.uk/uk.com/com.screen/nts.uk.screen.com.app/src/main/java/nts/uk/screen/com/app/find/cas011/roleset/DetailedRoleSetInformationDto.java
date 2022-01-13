package nts.uk.screen.com.app.find.cas011.roleset;


import lombok.Data;

import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetLinkWebMenuImport;
import nts.uk.ctx.sys.portal.dom.adapter.role.DefaultRoleSetDto;
import nts.uk.ctx.sys.portal.dom.adapter.roleset.RoleSetDto;


import java.util.List;

@Data
public class DetailedRoleSetInformationDto {
    public RoleSetDto roleSetDtos;
    public DefaultRoleSetDto defaultRoleSet;
    List<RoleSetLinkWebMenuImport> linkWebMenuImportList;
}
