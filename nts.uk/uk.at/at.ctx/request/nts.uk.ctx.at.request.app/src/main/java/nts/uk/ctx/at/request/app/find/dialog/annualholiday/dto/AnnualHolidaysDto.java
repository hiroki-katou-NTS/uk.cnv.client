package nts.uk.ctx.at.request.app.find.dialog.annualholiday.dto;

import java.util.List;

import lombok.AllArgsConstructor;

import lombok.Data;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.InforAnnualHolidaysAccHolidayDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;

@Data
@AllArgsConstructor
public class AnnualHolidaysDto {
	private List<EmployeeImport> employeeImports;
	
	// 年休・積休残数詳細情報DTO
	private InforAnnualHolidaysAccHolidayDto accHolidayDto;
	
	// Mode 0 : single; 1 : multi
	private int mode; 
}
