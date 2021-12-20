package nts.uk.ctx.sys.auth.dom.adapter.role.employment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Value
public class RoleEmployExportDto {

    private String userId;

    /** 会社ID */
    private String companyId;

    private DatePeriod datePeriod;

    /** 個人ID */
    private String personId;

    /** 社員ID */
    private String employeeId;

    /** 社員コード */
    private String employeeCode;

    /** The employee name. */
    private String employeeName;

}
