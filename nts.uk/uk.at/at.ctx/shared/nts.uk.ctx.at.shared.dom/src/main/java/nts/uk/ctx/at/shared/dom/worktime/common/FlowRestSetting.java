/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.common;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * The Class FlowRestSetting.
 */
//流動休憩設定
@Getter
public class FlowRestSetting extends DomainObject {

	/** The flow rest time. */
	// 流動休憩時間
	private AttendanceTime flowRestTime;

	/** The flow passage time. */
	// 流動経過時間
	private AttendanceTime flowPassageTime;

	/**
	 * Instantiates a new flow rest setting.
	 *
	 * @param memento the memento
	 */
	public FlowRestSetting(FlowRestSettingGetMemento memento) {
		this.flowRestTime = memento.getFlowRestTime();
		this.flowPassageTime = memento.getFlowPassageTime();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(FlowRestSettingSetMemento memento) {
		memento.setFlowRestTime(this.flowRestTime);
		memento.setFlowPassageTime(this.flowPassageTime);
	}
}
