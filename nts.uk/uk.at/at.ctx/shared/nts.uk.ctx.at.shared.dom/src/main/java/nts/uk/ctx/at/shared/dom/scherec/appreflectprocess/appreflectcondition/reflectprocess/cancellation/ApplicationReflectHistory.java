package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation;

import java.util.List;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

/**
 * @author thanh_nx
 *
 *         申請反映履歴
 */
@Getter
public class ApplicationReflectHistory implements DomainAggregate {

	// 社員ID
	private String employeeId;

	// 年月日
	private GeneralDate date;

	// 申請ID
	private String applicationId;

	// 反映時刻
	private GeneralDateTime reflectionTime;

	// 予定実績区分
	private ScheduleRecordClassifi classification;

	// 取消区分
	private boolean cancellationCate;

	// 反映前
	private List<AttendanceBeforeApplicationReflect> lstAttBeforeAppReflect;

	public ApplicationReflectHistory(String employeeId, GeneralDate date, String applicationId,
			GeneralDateTime reflectionTime, ScheduleRecordClassifi classification, boolean cancellationCate,
			List<AttendanceBeforeApplicationReflect> lstAttBeforeAppReflect) {
		super();
		this.employeeId = employeeId;
		this.date = date;
		this.applicationId = applicationId;
		this.reflectionTime = reflectionTime;
		this.classification = classification;
		this.cancellationCate = cancellationCate;
		this.lstAttBeforeAppReflect = lstAttBeforeAppReflect;
	}

}
