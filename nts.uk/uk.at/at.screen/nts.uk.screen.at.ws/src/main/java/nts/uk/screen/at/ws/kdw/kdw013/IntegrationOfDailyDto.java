package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class IntegrationOfDailyDto {
	// 社員ID
	private String employeeId;

	// 年月日
	private GeneralDate ymd;
	
	// 勤務情報: 日別勤怠の勤務情報
	private WorkInfoOfDailyAttendanceDto workInformationDto;
	
	public static IntegrationOfDailyDto toDto(IntegrationOfDaily domain) {
		IntegrationOfDailyDto result = new IntegrationOfDailyDto();
		
		result.setEmployeeId(domain.getEmployeeId());
		result.setYmd(domain.getYmd());
		result.setWorkInformationDto(WorkInfoOfDailyAttendanceDto.toDto(domain.getWorkInformation()));
		
		return result;
	}
}
