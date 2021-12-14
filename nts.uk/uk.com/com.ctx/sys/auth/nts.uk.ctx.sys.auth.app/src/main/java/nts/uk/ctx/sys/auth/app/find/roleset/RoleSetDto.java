/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.app.find.roleset;

import java.util.List;

import lombok.Data;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;

/**
* The Class RoleSetDto.
* @author HieuNV
*/
@Data
public class RoleSetDto {

    /** ロールセットコード. */
    private String roleSetCd;

    /** 会社ID */
    private String companyId;

    /** ロールセット名称*/
    private String roleSetName;

    /** 承認権限*/
    private boolean approvalAuthority;

    /** ロールID: オフィスヘルパーロール */
    private String officeHelperRoleId;

    /** ロールID: マイナンバーロール */
    private String myNumberRoleId;

    /** ロールID: 人事ロール */
    private String humanResourceRoleId;

    /** ロールID: 個人情報ロール */
    private String personInfRoleId;

    /** ロールID: 就業ロール */
    private String employmentRoleId;

    /** ロールID: 給与ロール */
    private String salaryRoleId;
    
    /** list of web menu code */
    private List<WebMenuImportDto> webMenus;
    
    /**
     * Transfer data from Domain into Dto to response to client
     * @param roleSet
     * @return
     */
    public static RoleSetDto build(RoleSet roleSet, List<WebMenuImportDto> listWebMenuDto) {
        RoleSetDto result = new RoleSetDto();
        result.setApprovalAuthority(roleSet.hasApprovalAuthority());
        result.setCompanyId(roleSet.getCompanyId());
        result.setEmploymentRoleId(roleSet.getEmploymentRoleId());
        result.setHumanResourceRoleId(roleSet.getHRRoleId());
        result.setMyNumberRoleId(roleSet.getMyNumberRoleId());
        result.setOfficeHelperRoleId(roleSet.getOfficeHelperRoleId());
        result.setPersonInfRoleId(roleSet.getPersonInfRoleId());
        result.setRoleSetCd(roleSet.getRoleSetCd().v());
        result.setRoleSetName(roleSet.getRoleSetName().v());
        result.setSalaryRoleId(roleSet.getSalaryRoleId());
        result.setWebMenus(listWebMenuDto);
        return result;
    }
}
