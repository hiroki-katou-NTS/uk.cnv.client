package nts.uk.ctx.at.function.dom.adapter.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import nts.arc.time.GeneralDate;

@Getter
@AllArgsConstructor
public class MailExportRolesDto {
	private String roleId;
	private String roleName;
	private Integer employeeReferenceRange;
}
