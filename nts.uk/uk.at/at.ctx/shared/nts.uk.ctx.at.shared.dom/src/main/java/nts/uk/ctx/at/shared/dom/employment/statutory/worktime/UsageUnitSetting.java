/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.employment.statutory.worktime;

import nts.arc.layer.dom.DomainObject;

/**
 * The Class UsageUnitSetting.
 */
// 労働時間と日数の設定の利用単位の設定
public class UsageUnitSetting extends DomainObject {

	/** The employee. */
	// 社員の労働時間と日数の管理をする
	private boolean employee;

	/** The work place. */
	// 職場の労働時間と日数の管理をする
	private boolean workPlace;

	/** The employment. */
	// 雇用の労働時間と日数の管理をする
	private boolean employment;

	/**
	 * Instantiates a new usage unit setting.
	 *
	 * @param memento the memento
	 */
	public UsageUnitSetting(UsageUnitSettingGetMemento memento) {
		this.employee = memento.getEmployee();
		this.workPlace = memento.getWorkPlace();
		this.employment = memento.getEmployment();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(UsageUnitSettingSetMemento memento) {
		memento.setEmployee(this.employee);
		memento.setWorkPlace(this.workPlace);
		memento.setEmployment(this.employment);
	}

}
