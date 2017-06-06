/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.ExpirationTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;

/**
 * The Class EmploymentCompensatoryManageSetting.
 */
@Getter
public class EmploymentCompensatoryManageSetting extends DomainObject {

	// 管理区分
	/** The is managed. */
	private ManageDistinct isManaged;

	// 使用期限
	/** The expiration time. */
	private ExpirationTime expirationTime;

	// 先取り許可
	/** The preemption permit. */
	private ApplyPermission preemptionPermit;

	/**
	 * Instantiates a new employment compensatory manage setting.
	 *
	 * @param memento the memento
	 */
	public EmploymentCompensatoryManageSetting(EmploymentManageGetMemento memento) {
		this.isManaged = memento.getIsManaged();
		this.expirationTime = memento.getExpirationTime();
		this.preemptionPermit = memento.getPreemptionPermit();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(EmploymentManageSetMemento memento) {
		memento.setIsManaged(this.isManaged);
		memento.setExpirationTime(this.expirationTime);
		memento.setPreemptionPermit(this.preemptionPermit);
	}
}
