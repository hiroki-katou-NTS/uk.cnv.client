/**
 * 11:38:35 AM Nov 2, 2017
 */
package nts.uk.ctx.at.shared.dom.alarmList.extractionResult.worktype;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.enums.FilterByCompare;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.enums.WorkCheckResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.snapshot.SnapShot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * @author hungnm
 *
 */
// 勤務種類の条件
public class WorkTypeCondition extends DomainObject {

	// 勤務種類の条件を使用する
	private boolean useAtr;

	// 予実比較による絞り込み方法
	@Getter
	private FilterByCompare comparePlanAndActual;

	/* Constructor */
	protected WorkTypeCondition(boolean useAtr, FilterByCompare comparePlanAndActual) {
		super();
		this.useAtr = useAtr;
		this.comparePlanAndActual = comparePlanAndActual;
	}

	/** 勤務種類をチェックする */
	public WorkCheckResult checkWorkType() {
		return WorkCheckResult.NOT_CHECK;
	}

	/** 勤務種類をチェックする */
	public WorkCheckResult checkWorkType(WorkInfoOfDailyAttendance workInfo, Optional<SnapShot> snapshot) {
		return WorkCheckResult.NOT_CHECK;
	}
	public boolean isUse() {
		return this.useAtr;
	}
	
	public void clearDuplicate() { }
	
	public void addWorkType(WorkTypeCode plan, WorkTypeCode actual){ }
	
	public void addWorkType(List<WorkTypeCode> plan, List<WorkTypeCode> actual){ }
	
	public void setupWorkType(boolean usePlan, boolean useActual){ }
	
	public WorkTypeCondition chooseOperator(Integer operator) {
		return this;
	}
}
