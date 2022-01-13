package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.WorkGroupDto;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.WorkplaceOfWorkEachOuenDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;

@AllArgsConstructor
@Getter
public class WorkContentDto {
	/** 勤務先: 応援別勤務の勤務先 */
	private WorkplaceOfWorkEachOuenDto workplace;

	/** 作業: 作業グループ */
	private WorkGroupDto work;

	/** 作業補足情報 */
	private WorkSuppInfoDto workSuppInfo;

	public static WorkContentDto fromDomain(WorkContent domain) {
		return new WorkContentDto(
				WorkplaceOfWorkEachOuenDto.from(domain.getWorkplace()), 
				domain.getWork().map(x-> WorkGroupDto.from(x)).orElse(null) ,
				domain.getWorkSuppInfo().map(x-> WorkSuppInfoDto.from(x)).orElse(null) 
				);
	}

}
