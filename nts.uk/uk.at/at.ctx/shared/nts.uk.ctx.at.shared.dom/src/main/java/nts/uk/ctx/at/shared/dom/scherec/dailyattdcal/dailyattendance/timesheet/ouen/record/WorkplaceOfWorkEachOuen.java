package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;

@Getter
/** 応援別勤務の勤務先 */
public class WorkplaceOfWorkEachOuen implements DomainObject {

	/** 職場: 職場ID */
	@Setter
	private WorkplaceId workplaceId;
	
	/** 場所: 勤務場所コード */
	@Setter
	private Optional<WorkLocationCD> workLocationCD;

	private WorkplaceOfWorkEachOuen(WorkplaceId workplaceId, WorkLocationCD workLocationCD) {
		super();
		this.workplaceId = workplaceId;
		this.workLocationCD = Optional.ofNullable(workLocationCD);
	}
	
	public static WorkplaceOfWorkEachOuen create(WorkplaceId workplaceId, WorkLocationCD workLocationCD) {
		
		return new WorkplaceOfWorkEachOuen(workplaceId, workLocationCD);
	}
}
