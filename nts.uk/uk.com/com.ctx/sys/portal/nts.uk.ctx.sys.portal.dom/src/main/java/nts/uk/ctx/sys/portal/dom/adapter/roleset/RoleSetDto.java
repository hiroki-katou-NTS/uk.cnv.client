package nts.uk.ctx.sys.portal.dom.adapter.roleset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleSetDto {
	
	private String roleSetCd;

    private String companyId;

    private String roleSetName;

    private String officeHelperRoleId;

    private String myNumberRoleId;

    private String hRRoleId;

    private String personInfRoleId;

    private String employmentRoleId;

    private String salaryRoleId;

}
