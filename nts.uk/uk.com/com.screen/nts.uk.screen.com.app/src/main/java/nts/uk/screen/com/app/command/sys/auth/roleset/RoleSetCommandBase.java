/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.app.command.sys.auth.roleset;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.sys.portal.app.command.webmenu.WebMenuCommand;

/**
* The Class RoleSetCommandBase.
* @author HieuNV
*/
@Value
public class RoleSetCommandBase {
    /** ロールセットコード. */
    private String roleSetCd;

    /** 会社ID */
    private String companyId;

    /** ロールセット名称*/
    private String roleSetName;

    /** ロールID: 個人情報ロール */
    private String personInfRoleId;

    /** ロールID: 就業ロール */
    private String employmentRoleId;

    private Boolean defaultRoleSet;

    /** List of web menu code **/
    private List<WebMenuCommand> webMenus;
}
