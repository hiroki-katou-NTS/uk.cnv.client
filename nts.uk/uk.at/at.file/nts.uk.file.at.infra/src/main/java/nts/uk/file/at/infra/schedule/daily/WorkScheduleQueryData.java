package nts.uk.file.at.infra.schedule.daily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceResultImport;
import nts.uk.ctx.at.function.dom.dailyworkschedule.AttendanceItemsDisplay;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WkpHistImport;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo;
import nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputQuery;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;

/**
 * The Class WorkScheduleQueryData.
 *
 * @author HoangNDH
 * Work schedule query data container
 */
@Data
public class WorkScheduleQueryData {
	
	/** The lst workplace config info. */
	List<WorkplaceConfigInfo> lstWorkplaceConfigInfo;
	
	/** The date period. */
	List<GeneralDate> datePeriod = new ArrayList<>();
	
	/** The lst work type. */
	List<WorkType> lstWorkType;
	
	/** The lst work time. */
	List<WorkTimeSetting> lstWorkTime;
	
	/** The lst workplace info. */
	List<CodeName> lstWorkplaceInfo;
	
	/** The lst work location. */
	List<CodeName> lstWorkLocation;
	
	/** The lst reason. */
	List<CodeName> lstReason;
	
	/** The lst classification. */
	List<CodeName> lstClassification;
	
	/** The lst position. */
	List<CodeName> lstPosition;
	
	/** The lst employment. */
	List<CodeName> lstEmployment;
	
	/** The lst employment. */
	List<CodeName> lstBusiness;
	
	/** The lst optional item. */
	List<OptionalItem> lstOptionalItem;
	
	/** The lst display item. */
	List<AttendanceItemsDisplay> lstDisplayItem;
	
	/** The lst attendance result import. */
	List<AttendanceResultImport> lstAttendanceResultImport;
	
	/** The query. */
	WorkScheduleOutputQuery query;
	
	/** The lst workplace import. */
	Map<String, WkpHistImport> lstWorkplaceImport = new HashMap<>();
	
	/** The remark data containter. */
	RemarkQueryDataContainer remarkDataContainter;
	
	/** The addition. */
	List<CodeName> lstAddition;
	
	/** The lst work  */
	List<CodeName> lstWork;
	
	/** The lst work  */
	List<CodeName> lstWorkSupOption;
}
