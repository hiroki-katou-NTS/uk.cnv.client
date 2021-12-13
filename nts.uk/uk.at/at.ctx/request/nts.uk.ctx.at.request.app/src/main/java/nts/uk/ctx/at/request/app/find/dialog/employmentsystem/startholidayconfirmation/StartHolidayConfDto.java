package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.startholidayconfirmation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.RemainNumberConfirmDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;

@Getter
@Setter
@NoArgsConstructor
public class StartHolidayConfDto {

	// Mode 0 : single; 1 : multi
	private int mode; 
	
	// 残数確認ダイアログDTO
	private RemainNumberConfirmDto remainNumConfirmDto;
	
	//　List＜社員情報＞
	private List<EmployeeImport> listEmployeeImport = new ArrayList<>();

	public StartHolidayConfDto(int mode, RemainNumberConfirmDto remainNumConfirmDto,
			List<EmployeeImport> listEmployeeImport) {
		super();
		this.mode = mode;
		this.remainNumConfirmDto = remainNumConfirmDto;
		this.listEmployeeImport = listEmployeeImport;
	}
	
}
