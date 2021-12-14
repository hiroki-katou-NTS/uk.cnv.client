package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

@Getter
/** 作業内容 */
public class WorkContent implements DomainObject {

	/** 勤務先: 応援別勤務の勤務先 */
	private WorkplaceOfWorkEachOuen workplace;
	
	/** 作業: 作業グループ */
	private Optional<WorkGroup> work;
	
	/** 備考: 作業入力備考 */
	private Optional<WorkinputRemarks> workRemarks;

	private WorkContent(WorkplaceOfWorkEachOuen workplace, Optional<WorkGroup> work, Optional<WorkinputRemarks> workRemarks) {
		super();
		this.workplace = workplace;
		this.work = work;
		this.workRemarks = workRemarks;
	}
	
	public static WorkContent create(WorkplaceOfWorkEachOuen workplace, Optional<WorkGroup> work, Optional<WorkinputRemarks> workRemarks) {
		
		return new WorkContent(workplace, work, workRemarks);
	}

	public void setWork(Optional<WorkGroup> work) {
		this.work = work;
	}

	public void setWorkplace(WorkplaceOfWorkEachOuen workplace) {
		this.workplace = workplace;
	}
}
