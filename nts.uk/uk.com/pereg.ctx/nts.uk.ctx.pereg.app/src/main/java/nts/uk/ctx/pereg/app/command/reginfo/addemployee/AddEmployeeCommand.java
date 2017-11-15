package nts.uk.ctx.pereg.app.command.reginfo.addemployee;

import java.util.List;

import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.app.command.employee.LayoutPersonInfoCommand;

@Data
public class AddEmployeeCommand {

	private String employeeCopyId;
	private String InitSettingId;
	private String employeeName;
	private String employeeCode;
	private GeneralDate hireDate;
	private String cardNo;
	private String avatarId;
	private int createType;

	private List<LayoutPersonInfoCommand> itemDataList;

}
