package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import java.util.List;

import lombok.Data;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;

/**
 * 
 * @author phongtq
 *
 */
@Data
public class HolidaySubstituteDto {
	
	// 個人社員基本情報
	private List<EmployeeImport> empImport;
	
	// 残数確認ダイアログDTO
	private RemainNumberConfirmDto remainNumConfirmDto;
	
	// Mode 0 : single; 1 : multi
	private int mode; 
	
	public HolidaySubstituteDto(List<EmployeeImport> empImport, RemainNumberConfirmDto remainNumConfirmDto) {
		super();
		this.empImport = empImport;
		this.remainNumConfirmDto = remainNumConfirmDto;
	}
	
	public HolidaySubstituteDto(List<EmployeeImport> empImport, RemainNumberConfirmDto remainNumConfirmDto, int mode) {
		super();
		this.empImport = empImport;
		this.remainNumConfirmDto = remainNumConfirmDto;
		this.mode = mode;
	}
}
