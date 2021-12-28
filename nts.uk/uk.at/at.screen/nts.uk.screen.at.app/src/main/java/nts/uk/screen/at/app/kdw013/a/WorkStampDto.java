package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.WorkTimeInformationDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkStampDto {
	/*
	 * 時刻
	 */
	private WorkTimeInformationDto timeDay;

	/*
	 * 場所コード
	 */
	private String locationCode;

	public static WorkStampDto fromDomain(WorkStamp domain) {
		return new WorkStampDto(WorkTimeInformationDto.fromDomain(domain.getTimeDay()),
				domain.getLocationCode().map(x -> x.v()).orElse(null));
	}
}
