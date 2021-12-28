package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;

@AllArgsConstructor
@Getter
public class WorkplaceOfWorkEachOuenCommand {

	/** 職場: 職場ID */
	private String workplaceId;

	/** 場所: 勤務場所コード */

	private String workLocationCD;

	public WorkplaceOfWorkEachOuen toDomain() {
		return WorkplaceOfWorkEachOuen.create(new WorkplaceId(this.getWorkplaceId()),
				new WorkLocationCD(this.getWorkLocationCD()));
	}

}
