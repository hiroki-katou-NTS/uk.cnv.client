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
//	@Setter
//	private Optional<WorkinputRemarks> workRemarks;
	
	/** 作業補足情報 */
	private Optional<WorkSuppInfo> workSuppInfo;

	private WorkContent(WorkplaceOfWorkEachOuen workplace, Optional<WorkGroup> work, Optional<WorkSuppInfo> workSuppInfo) {
		super();
		this.workplace = workplace;
		this.work = work;
		this.workSuppInfo = workSuppInfo;
	}
	
	public static WorkContent create(WorkplaceOfWorkEachOuen workplace, Optional<WorkGroup> work, Optional<WorkSuppInfo> workSuppInfo) {
		
		return new WorkContent(workplace, work, workSuppInfo);
	}

	public void setWork(Optional<WorkGroup> work) {
		this.work = work;
	}

	public void setWorkplace(WorkplaceOfWorkEachOuen workplace) {
		this.workplace = workplace;
	}
}
