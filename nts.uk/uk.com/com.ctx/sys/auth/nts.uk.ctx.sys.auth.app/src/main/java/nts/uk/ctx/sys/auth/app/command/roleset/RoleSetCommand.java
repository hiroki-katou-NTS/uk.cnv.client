/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.app.command.roleset;

import lombok.Value;

/**
 * The Class RoleSetCommand.
 *
 * @author HieuNV
 */
@Value
public class RoleSetCommand {
    /**
     * ロールセットコード.
     */
    private String roleSetCd;

    /**
     * 会社ID
     */
    private String companyId;

    /**
     * ロールセット名称
     */
    private String roleSetName;

    /**
     * ロールID: 個人情報ロール
     */
    private String personInfRoleId;

    /**
     * ロールID: 就業ロール
     */
    private String employmentRoleId;

}
