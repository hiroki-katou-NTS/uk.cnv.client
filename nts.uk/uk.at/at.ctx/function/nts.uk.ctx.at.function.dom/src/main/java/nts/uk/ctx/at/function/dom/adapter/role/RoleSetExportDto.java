package nts.uk.ctx.at.function.dom.adapter.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoleSetExportDto {

    private String roleSetCd;

    private String companyId;

    private String roleSetName;

//    private int approvalAuthority;

    private String officeHelperRoleId;

    private String myNumberRoleId;

    private String hRRoleId;

    private String personInfRoleId;

    private String employmentRoleId;

    private String salaryRoleId;
}
