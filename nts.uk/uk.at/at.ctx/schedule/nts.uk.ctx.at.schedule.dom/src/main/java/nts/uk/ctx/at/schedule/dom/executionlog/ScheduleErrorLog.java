/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.dom.executionlog;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.i18n.resource.I18NResourcesForUK;

/**
 * The Class ScheduleErrorLog.
 */
// Domain: スケジュール作成エラーログ
@Getter
public class ScheduleErrorLog extends AggregateRoot {

	/** The error content. */
	// エラー内容
	private String errorContent;

	/** The execution id. */
	// 実行ID
	private String executionId;

	/** The date. */
	// 年月日
	private GeneralDate date;

	/** The employee id. */
	// 社員ID
	private String employeeId;

	/**
	 * Instantiates a new schedule error log.
	 *
	 * @param memento the memento
	 */
	public ScheduleErrorLog(ScheduleErrorLogGetMemento memento) {
		this.errorContent = memento.getErrorContent();
		this.executionId = memento.getExecutionId();
		this.date = memento.getDate();
		this.employeeId = memento.getEmployeeId();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(ScheduleErrorLogSetMemento memento) {
		memento.setErrorContent(this.errorContent);
		memento.setExecutionId(this.executionId);
		memento.setDate(this.date);
		memento.setEmployeeId(this.employeeId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((employeeId == null) ? 0 : employeeId.hashCode());
		result = prime * result + ((executionId == null) ? 0 : executionId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleErrorLog other = (ScheduleErrorLog) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		if (executionId == null) {
			if (other.executionId != null)
				return false;
		} else if (!executionId.equals(other.executionId))
			return false;
		return true;
	}

	public ScheduleErrorLog(String errorContent, String executionId, GeneralDate date, String employeeId) {
		super();
		this.errorContent = errorContent;
		this.executionId = executionId;
		this.date = date;
		this.employeeId = employeeId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	
	public static ScheduleErrorLog createErrorLog(I18NResourcesForUK internationalization, String execId, String sid,
			GeneralDate date, String message) {
		return createErrorLog(internationalization, execId, sid, date, message, '#' + message);
	}
	
	public static ScheduleErrorLog createErrorLog(I18NResourcesForUK internationalization, String execId, String sid,
			GeneralDate date, String message, String... messageParam) {
		String errorContent = internationalization.localize(message, messageParam).get();
		return new ScheduleErrorLog(errorContent, execId, date, sid);
	}
}
