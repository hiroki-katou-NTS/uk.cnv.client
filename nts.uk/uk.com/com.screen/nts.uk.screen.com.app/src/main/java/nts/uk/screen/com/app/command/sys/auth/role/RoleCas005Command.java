package nts.uk.screen.com.app.command.sys.auth.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleCas005Command {

    private String roleId;

    // コード RoleCode
    private String roleCode;

    // ロール種類 RoleType
    private int roleType;

    // 参照範囲 EmployeeReferenceRange
    private int employeeReferenceRange;

    // ロール名称 RoleName
    private String name;

    // 契約コード ContractCode
    private String contractCode;

    // 担当区分 RoleAtr
    private int assignAtr;

    // 会社ID
    private String companyId;

    private String webMenuCd;

    private int futureDateRefPermit;

    private Integer approvalAuthority;
}
