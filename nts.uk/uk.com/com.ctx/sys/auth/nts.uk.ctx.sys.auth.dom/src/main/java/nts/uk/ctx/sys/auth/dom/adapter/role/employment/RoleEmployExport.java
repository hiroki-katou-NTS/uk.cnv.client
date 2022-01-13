package nts.uk.ctx.sys.auth.dom.adapter.role.employment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.sys.auth.dom.wkpmanager.EmployeeCode;

@AllArgsConstructor
@Getter
@Setter
public class RoleEmployExport {
    /** 会社ID */
    private String companyId;

    /** 個人ID */
    private String personId;

    /** 社員ID */
    private String employeeId;

    /** 社員コード */
    private String employeeCode;

    /** The employee name. */
    private String employeeName;


}
