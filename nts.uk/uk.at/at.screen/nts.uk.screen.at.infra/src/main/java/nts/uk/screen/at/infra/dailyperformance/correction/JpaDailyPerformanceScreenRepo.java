/**
 * 2:14:20 PM Aug 21, 2017
 */
package nts.uk.screen.at.infra.dailyperformance.correction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.tuple.Pair;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.at.function.infra.entity.dailymodification.KfnmtApplicationCall;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtAuthorityDailyItem;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtAuthorityDailyItemPK;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtAuthorityFormSheet;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtDailyPerformanceDisplay;
import nts.uk.ctx.at.record.dom.optitem.OptionalItemAtr;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.SettingUnitType;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessFormatSheet;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessTypeDaily;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTime;
import nts.uk.ctx.at.record.infra.entity.divergencetime.KmkmtDivergenceReason;
import nts.uk.ctx.at.record.infra.entity.editstate.KrcdtDailyRecEditSet;
import nts.uk.ctx.at.record.infra.entity.monthly.erroralarm.KrcdtEmployeeMonthlyPerError;
import nts.uk.ctx.at.record.infra.entity.monthly.mergetable.KrcdtMonMerge;
import nts.uk.ctx.at.record.infra.entity.monthlyaggrmethod.flex.KrcstFlexShortageLimit;
import nts.uk.ctx.at.record.infra.entity.workinformation.KrcdtDaiPerWorkInfo;
import nts.uk.ctx.at.record.infra.entity.workrecord.actuallock.KrcstActualLock;
import nts.uk.ctx.at.record.infra.entity.workrecord.actuallock.KrcstActualLockPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.KrcdtSyainDpErList;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcstErAlApplication;
import nts.uk.ctx.at.record.infra.entity.workrecord.identificationstatus.KrcdtIdentificationStatus;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtApprovalProcess;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtApprovalProcessPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDaiPerformEdFun;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDaiPerformEdFunPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDaiPerformanceAut;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtFormatPerformance;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtFormatPerformancePk;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtIdentityProcess;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtIdentityProcessPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtWorktypeChangeable;
import nts.uk.ctx.at.record.infra.entity.workrecord.workfixed.KrcstWorkFixed;
import nts.uk.ctx.at.record.infra.entity.workrecord.workfixed.KrcstWorkFixedPK;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KrcmtDailyAttendanceItem;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KshstControlOfAttendanceItems;
import nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem.KshstDailyServiceTypeControl;
import nts.uk.ctx.at.shared.infra.entity.workingcondition.KshmtWorkingCond;
import nts.uk.ctx.at.shared.infra.entity.workplace.KshmtWorkTimeWorkplace;
import nts.uk.ctx.at.shared.infra.entity.workrule.closure.KclmpClosureEmploymentPK;
import nts.uk.ctx.at.shared.infra.entity.workrule.closure.KclmtClosureEmployment;
import nts.uk.ctx.bs.employee.infra.entity.classification.BsymtClassification;
import nts.uk.ctx.bs.employee.infra.entity.employee.history.BsymtAffCompanyHist;
import nts.uk.ctx.bs.employee.infra.entity.employee.mngdata.BsymtEmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.infra.entity.employment.BsymtEmployment;
import nts.uk.ctx.bs.employee.infra.entity.employment.BsymtEmploymentPK;
import nts.uk.ctx.bs.employee.infra.entity.employment.history.BsymtEmploymentHistItem;
import nts.uk.ctx.bs.employee.infra.entity.workplace.BsymtWorkplaceInfo;
import nts.uk.ctx.bs.employee.infra.entity.workplace.affiliate.BsymtAffiWorkplaceHistItem;
import nts.uk.ctx.bs.person.infra.entity.person.info.BpsmtPerson;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceScreenRepo;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.WorkTimeWorkplaceDto;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.WorkTypeChangedDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ActualLockDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AffEmploymentHistoryDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApplicationType;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalUseSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AuthorityFomatDailyDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AuthorityFormatInitialDisplayDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.AuthorityFormatSheetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ClosureDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ClosureEmploymentDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItemControl;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPBusinessTypeControl;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPErrorSettingDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPSheetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceEmployeeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyRecEditSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DivergenceTimeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.EmploymentDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.FormatDPCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.IdentityProcessUseSetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.OperationOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.OptionalItemDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.WorkFixedDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.WorkInfoOfDailyPerformanceDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.checkshowbutton.DailyPerformanceAuthorityDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.companyhist.AffComHistItemAtScreen;
import nts.uk.screen.at.app.dailyperformance.correction.dto.month.AttendenceTimeMonthDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.reasondiscrepancy.ReasonCodeName;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.CalculationStateDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.NotUseAttributeDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.ScheduleTimeSheetDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.WorkInfoOfDailyPerformanceDetailDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.WorkInformationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workplacehist.WorkPlaceHistTemp;
import nts.uk.screen.at.app.dailyperformance.correction.flex.change.ErrorFlexMonthDto;
import nts.uk.screen.at.app.dailyperformance.correction.lock.ConfirmationMonthDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceAuthorityDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * @author hungnm
 *
 */
@Stateless
public class JpaDailyPerformanceScreenRepo extends JpaRepository implements DailyPerformanceScreenRepo {

	private final static String SEL_BUSINESS_TYPE;

	private final static String SEL_FORMAT_DP_CORRECTION;

	private final static String SEL_FORMAT_DP_CORRECTION_MULTI;

	@SuppressWarnings("unused")
	private final static String SEL_CLOSURE_IDS;

	private final static String SEL_EMPLOYMENT_BY_CLOSURE;

	private final static String SEL_WORKPLACE;

	private final static String FIND_EMP_WORKPLACE;

	private final static String SEL_WORKPLACE_ALL;

	private final static String SEL_CLASSIFICATION = "SELECT c FROM BsymtClassification c WHERE c.bsymtClassificationPK.cid = :companyId";

	private final static String SEL_EMPLOYEE;

	private final static String SEL_EMPLOYEE_WITH_SID;

	private final static String SEL_PERSON = "SELECT p FROM BpsmtPerson p WHERE p.bpsmtPersonPk.pId IN :lstPersonId";

	private final static String SEL_DAILY_WORK_INFO = "SELECT d FROM KrcdtDaiPerWorkInfo d "
			+ "WHERE d.krcdtDaiPerWorkInfoPK.ymd IN :lstDate "
			+ "AND d.krcdtDaiPerWorkInfoPK.employeeId IN :lstEmployee";

	private final static String SEL_DP_TYPE_CONTROL;

	private final static String SEL_ATTENDANCE_ITEM;

	private final static String SEL_ATTENDANCE_ITEM_CONTROL = "SELECT c FROM KshstControlOfAttendanceItems c WHERE c.kshstControlOfAttendanceItemsPK.companyID = :companyId AND c.kshstControlOfAttendanceItemsPK.itemDailyID IN :lstItem";

	private final static String SEL_DP_ERROR_EMPLOYEE;

	private final static String SEL_DP_ERROR_EMPLOYEE_CONDITION_ERRORS;

	
//	private final static String SEL_ERROR_SETTING;

	private final static String SEL_FORMAT_SHEET;

	private final static String SEL_EMPLOYMENT_HISTORY;

	private final static String SEL_DAILY_REC_EDIT_SET;

	
//	private final static String SEL_FIND_WORK_FIXED;

	private final static String SEL_AUTHOR_DAILY_ITEM;

	private final static String SEL_DAILY_PERFORMACE_DISPLAY;

	private final static String SEL_AUTHOR_FORM_SHEET;

	private final static String SEL_DIVERGENCE_REASON;

	
//	private final static String SEL_FIND_JOB_INFO;

	private final static String SEL_FIND_JOB_INFO_JDBC;

	private final static String SEL_FIND_ID_JOB_INFO;

	
//	private final static String SEL_FIND_CLASSIFICATION;

	private final static String SEL_FIND_CLASSIFICATION_JDBC = "SELECT CLSCD, CLSNAME FROM BSYMT_CLASSIFICATION WHERE CID = ? ";

	
//	private final static String SEL_FIND_WORKPLACE_LOCATION;

	
//	private final static String SEL_ALL_WORKPLACE;

	private final static String SEL_FIND_WORKPLACE;

	private final static String SEL_FIND_ER_AL_APP;

	private final static String FIND_DVGC_TIME;

	private final static String FIND_WORK_CONDITION;

	private final static String GET_DAI_PER_AUTH_WITH_ROLE = "SELECT da FROM KrcmtDaiPerformanceAut da WHERE da.pk.roleId =:roleId";

//	private final static String GET_DAI_PER_AUTH_WITH_ROLE_AND_AVAILABILITY = "SELECT da FROM KrcmtDaiPerformanceAut da WHERE da.pk.roleId =:roleId AND da.availability = :availability";

	private final static String SELECT_WORKTIME_WORKPLACE_BYID = "SELECT a FROM KshmtWorkTimeWorkplace a "
			+ " WHERE a.kshmtWorkTimeWorkplacePK.companyID = :companyID "
			+ " AND a.kshmtWorkTimeWorkplacePK.workplaceID = :workplaceID ";

//	private final static String FIND_WORK_TIME_ZONE = "SELECT a FROM KshmtWorkTimeSet a "
//			+ "WHERE a.kshmtWorkTimeSetPK.cid = :companyID";

	private final static String FIND_WORK_TIME_ZONE_JDBC = "SELECT WORKTIME_CD, NAME FROM KSHMT_WORK_TIME_SET WHERE CID = ?";

	private final static String GET_ALL_WORK_TYPE_CHANGED = "SELECT wtc FROM KrcmtWorktypeChangeable wtc"
			+ " WHERE wtc.pk.cid = :companyId AND wtc.pk.empCode = :employeeCode";

//	private final static String SELECT_WORKTYPE = " SELECT c FROM KshmtWorkType c WHERE c.kshmtWorkTypePK.companyId = :companyId";

	private final static String SELECT_WORKTYPE_JDBC = " SELECT CD, NAME FROM KSHMT_WORKTYPE WHERE CID = ?";

	private final static String SEL_FIND_WORKPLACE_LOCATION_JDBC = "SELECT WORK_LOCATION_CD, WORK_LOCATION_NAME FROM KWLMT_WORK_LOCATION WHERE CID = ?";

	private final static String SEL_EMPLOYMENT_BY_CLOSURE_JDBC = "SELECT CODE, NAME FROM BSYMT_EMPLOYMENT WHERE CID = ?";

	private final static String SEL_ALL_WORKPLACE_JDBC;

//	private final static String SELECT_ALL_DIVREASON = "SELECT c FROM KrcstDvgcReason c"
//			+ " WHERE c.id.cid = :companyId";

	private final static String SELECT_ALL_DIVREASON_JDBC = "SELECT REASON, [NO], REASON_CD FROM KRCST_DVGC_REASON c WHERE CID = ?";

	private final static String SELECT_CONFIRM_DAY = "SELECT c FROM KrcdtIdentificationStatus c"
			+ " WHERE c.krcdtIdentificationStatusPK.companyID = :companyID"
			+ " AND c.krcdtIdentificationStatusPK.employeeId IN :sids"
			+ " AND c.krcdtIdentificationStatusPK.processingYmd IN :processingYmds";

	public final static String SELECT_BY_LIST_EMPID = "SELECT e FROM BsymtEmployeeDataMngInfo e WHERE e.bsymtEmployeeDataMngInfoPk.sId IN :listSid ";

	private static final String SELECT_BY_EMPLOYEE_ID_AFF_COM = "SELECT c FROM BsymtAffCompanyHist c WHERE c.bsymtAffCompanyHistPk.sId IN :sIds and c.companyId = :cid ORDER BY c.startDate ";
	
	private static final String SELECT_BY_SID_DATE_AF_COM = "SELECT c.bsymtAffCompanyHistPk.sId FROM BsymtAffCompanyHist c WHERE c.bsymtAffCompanyHistPk.sId IN :sIds and c.companyId = :cid and c.startDate <= :endDate and c.endDate >= :startDate ";

	private static final String SELECT_BY_LISTSID_WPH;

	private static final String SELECT_BY_LISTSID_NAME;

	private final static String FIND_APPLICATION_CALL = "SELECT a FROM KfnmtApplicationCall a WHERE a.kfnmtApplicationCallPK.companyId = :companyId ORDER BY a.kfnmtApplicationCallPK.applicationType";

	private final static String FIND_PERIOD_ORDER_BY_STR_D_FOR_MULTI = "SELECT wi FROM KshmtWorkingCond wi "
			+ "WHERE wi.kshmtWorkingCondPK.sid = :employeeId " + "AND wi.strD <= :endDate "
			+ "AND wi.endD >= :startDate " + "AND wi.kshmtWorkingCondItem.laborSys = "
			+ WorkingSystem.FLEX_TIME_WORK.value;

	private final static String GET_LIMIT_FLEX_MON = "SELECT f FROM KrcstFlexShortageLimit f WHERE f.companyId = :companyId";

//	private final static String GET_EMP_ALL = "SELECT e FROM BsymtEmploymentHistItem e JOIN BsymtEmploymentHist h ON e.hisId = h.hisId WHERE "
//			+ " h.strDate <= :endDate AND h.endDate >= :startDate AND h.companyId = :companyId AND h.sid IN :sIds";

	private static final String FIND_BY_PERIOD_INTO_END = "SELECT a FROM KrcdtMonMerge a "
			+ "WHERE a.krcdtMonMergePk.employeeId IN :employeeIds " + "AND a.endYmd >= :startDate "
			+ "AND a.endYmd <= :endDate " + "ORDER BY a.startYmd ";

	private final static String GET_MONTH_ERROR;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT DISTINCT b.businessTypeCode");
		builderString.append(" FROM KrcmtBusinessTypeOfEmployee b");
		builderString.append(" JOIN KrcmtBusinessTypeOfHistory h");
		builderString
				.append(" ON b.krcmtBusinessTypeOfEmployeePK.historyId = h.krcmtBusinessTypeOfHistoryPK.historyId");
		builderString.append(" WHERE b.sId IN :lstSID");
		builderString.append(" AND h.startDate <= :endYmd");
		builderString.append(" AND h.endDate >= :startYmd");
		builderString.append(" ORDER BY b.businessTypeCode ASC");
		SEL_BUSINESS_TYPE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT b");
		builderString.append(" FROM KrcmtBusinessTypeDaily b");
		builderString.append(" WHERE b.krcmtBusinessTypeDailyPK.companyId = :companyId");
		builderString.append(" AND b.krcmtBusinessTypeDailyPK.businessTypeCode IN :lstBusinessTypeCode ");
		builderString.append(" ORDER BY b.order ASC, b.krcmtBusinessTypeDailyPK.attendanceItemId ASC");
		SEL_FORMAT_DP_CORRECTION = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT b");
		builderString.append(" FROM KrcmtBusinessTypeDaily b INNER JOIN");
		builderString.append(" KrcstBusinessTypeSorted s");
		builderString.append(" WHERE s.krcstBusinessTypeSortedPK.companyId = :companyId");
		builderString.append(
				" AND b.krcmtBusinessTypeDailyPK.attendanceItemId = s.krcstBusinessTypeSortedPK.attendanceItemId");
		builderString.append(" AND b.krcmtBusinessTypeDailyPK.companyId = :companyId");
		builderString.append(" AND b.krcmtBusinessTypeDailyPK.businessTypeCode IN :lstBusinessTypeCode ");
		builderString.append(" ORDER BY s.order ASC, b.krcmtBusinessTypeDailyPK.attendanceItemId ASC");
		SEL_FORMAT_DP_CORRECTION_MULTI = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT NEW ");
		builderString.append(ClosureDto.class.getName());
		builderString.append(
				" (closure.kclmtClosurePK.cid, closure.kclmtClosurePK.closureId , closure.useClass, closure.closureMonth, '',emp.kclmpClosureEmploymentPK.employmentCD) ");
		builderString.append("FROM KclmtClosure closure JOIN ");
		builderString.append("KclmtClosureEmployment emp ");
		builderString.append("WHERE emp.kclmpClosureEmploymentPK.companyId = :companyId ");
		builderString.append("AND emp.kclmpClosureEmploymentPK.employmentCD IN :emptcd ");
		builderString.append("AND closure.kclmtClosurePK.cid = :companyId ");
		builderString.append("AND closure.kclmtClosurePK.closureId = emp.closureId ");
		builderString.append("AND closure.useClass = 1");
		SEL_CLOSURE_IDS = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e FROM BsymtEmployment e WHERE e.bsymtEmploymentPK.cid = :companyId");
		SEL_EMPLOYMENT_BY_CLOSURE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT w FROM BsymtAffiWorkplaceHistItem w JOIN ");
		builderString.append("BsymtAffiWorkplaceHist a ON  w.hisId = a.hisId ");
		builderString.append("WHERE a.sid = :sId ");
		builderString.append("AND a.strDate <= :endDate ");
		builderString.append("AND a.endDate >= :startDate ");
		builderString.append("AND w.sid = a.sid ");
		SEL_WORKPLACE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT w FROM BsymtAffiWorkplaceHistItem w JOIN ");
		builderString.append("BsymtAffiWorkplaceHist a ON  w.hisId = a.hisId ");
		builderString.append("WHERE a.sid IN :sIds ");
		builderString.append("AND a.strDate <= :baseDate ");
		builderString.append("AND a.endDate >= :baseDate ");
		builderString.append("AND w.sid = a.sid ");
		SEL_WORKPLACE_ALL = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT w.sid FROM BsymtAffiWorkplaceHistItem w JOIN ");
		builderString.append("BsymtAffiWorkplaceHist a ON  w.hisId = a.hisId ");
		builderString.append("WHERE w.workPlaceId IN :workPlaceId ");
		builderString.append("AND a.strDate <= :endDate ");
		builderString.append("AND a.endDate >= :startDate ");
		FIND_EMP_WORKPLACE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT DISTINCT s FROM BsymtEmployeeDataMngInfo s ");
		// builderString.append("JOIN KmnmtAffiliClassificationHist c ");
		// builderString.append("JOIN KmnmtAffiliEmploymentHist e ");
		// builderString.append("JOIN KmnmtAffiliJobTitleHist j ");
		builderString.append("JOIN BsymtAffiWorkplaceHistItem w ");
		builderString.append("WHERE w.workPlaceId IN :lstWkp ");
		// builderString.append("AND e.kmnmtEmploymentHistPK.emptcd IN :lstEmp
		// ");
		// builderString.append("AND j.kmnmtJobTitleHistPK.jobId IN :lstJob ");
		// builderString.append("AND c.kmnmtClassificationHistPK.clscd IN
		// :lstClas ");
		builderString.append("AND s.bsymtEmployeeDataMngInfoPk.sId = w.sid ");
		// builderString.append("OR s.bsymtEmployeePk.sId =
		// e.kmnmtEmploymentHistPK.empId ");
		// builderString.append("OR s.bsymtEmployeePk.sId =
		// j.kmnmtJobTitleHistPK.empId ");
		// builderString.append("OR s.bsymtEmployeePk.sId =
		// c.kmnmtClassificationHistPK.empId ");
		SEL_EMPLOYEE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT DISTINCT s FROM BsymtEmployeeDataMngInfo s ");
		builderString.append("JOIN BsymtAffiWorkplaceHistItem w ");
		builderString.append("WHERE s.bsymtEmployeeDataMngInfoPk.sId IN :sids ");
		builderString.append("AND s.bsymtEmployeeDataMngInfoPk.sId = w.sid ");
		SEL_EMPLOYEE_WITH_SID = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT c FROM KshstDailyServiceTypeControl c ");
		builderString.append("WHERE c.kshstDailyServiceTypeControlPK.authorityDailyID = :authorityDailyID ");
		builderString.append("AND c.kshstDailyServiceTypeControlPK.itemDailyID IN :lstItem ");
		builderString.append("AND c.toUse = 1");
		SEL_DP_TYPE_CONTROL = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT t FROM KrcmtDailyAttendanceItem t ");
		builderString.append("WHERE t.krcmtDailyAttendanceItemPK.companyId = :companyId ");
		builderString.append("AND t.krcmtDailyAttendanceItemPK.attendanceItemId IN :lstItem");
		SEL_ATTENDANCE_ITEM = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e FROM KrcdtSyainDpErList e ");
		builderString.append("WHERE ( e.processingDate BETWEEN :startDate AND :endDate ) ");
		builderString.append("AND e.employeeId IN :lstEmployee");
		SEL_DP_ERROR_EMPLOYEE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e FROM KrcdtSyainDpErList e ");
		builderString.append("WHERE ( e.processingDate BETWEEN :startDate AND :endDate ) ");
		builderString.append("AND e.employeeId IN :lstEmployee ");
		builderString.append("AND e.errorCode IN :errorCodes");
		SEL_DP_ERROR_EMPLOYEE_CONDITION_ERRORS = builderString.toString();

		// builderString = new StringBuilder();
		// builderString.append("SELECT s FROM KwrmtErAlWorkRecord s");
		// builderString.append(" WHERE s.kwrmtErAlWorkRecordPK.companyId =
		// :companyId ");
		// builderString.append("AND s.kwrmtErAlWorkRecordPK.errorAlarmCode IN
		// :lstCode");
//		builderString = new StringBuilder();
//		builderString.append("SELECT s , u.ERAL_CHECK_ID ");
//		builderString.append("FROM KRCMT_ERAL_SET s JOIN KRCMT_ERAL_CONDITION u ");
//		builderString.append("ON s.ERAL_CHECK_ID = u.ERAL_CHECK_ID ");
//		builderString.append("WHERE s.CID = :companyId ");
//		builderString.append("AND s.ERROR_ALARM_CD IN :lstCode ");
//		SEL_ERROR_SETTING = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT s FROM KrcmtBusinessFormatSheet s ");
		builderString.append("WHERE s.krcmtBusinessFormatSheetPK.companyId = :companyId ");
		builderString.append("AND s.krcmtBusinessFormatSheetPK.businessTypeCode IN :lstBusinessTypeCode");
		SEL_FORMAT_SHEET = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e FROM BsymtEmploymentHistItem e ");
		builderString.append("JOIN  BsymtEmploymentHist b ");
		builderString.append("ON e.hisId =  b.hisId ");
		builderString.append("WHERE b.sid = :empId ");
		builderString.append("AND b.companyId = :companyId ");
		builderString.append("AND b.strDate <= :baseDate ");
		builderString.append("AND b.endDate >= :baseDate");
		SEL_EMPLOYMENT_HISTORY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDailyRecEditSet a ");
		builderString.append("WHERE a.krcdtDailyRecEditSetPK.employeeId IN :employeeIds ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.processingYmd IN :ymds ");
		SEL_DAILY_REC_EDIT_SET = builderString.toString();

//		builderString = new StringBuilder();
//		builderString.append("SELECT a ");
//		builderString.append("FROM KrcstWorkFixed a ");
//		builderString.append("WHERE a.krcstWorkFixedPK.closureId = :closureId ");
//		builderString.append("AND a.krcstWorkFixedPK.cid = :cid ");
//		builderString.append("AND a.krcstWorkFixedPK.cid = :cid ");
//		builderString.append("AND a.confirmCls = 1 ");
//		SEL_FIND_WORK_FIXED = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAuthorityDailyItem a ");
		builderString.append("WHERE a.kfnmtAuthorityDailyItemPK.companyId = :companyId ");
		builderString
				.append("AND a.kfnmtAuthorityDailyItemPK.dailyPerformanceFormatCode IN :dailyPerformanceFormatCodes ");
		SEL_AUTHOR_DAILY_ITEM = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtDailyPerformanceDisplay a ");
		builderString.append("WHERE a.kfnmtDailyPerformanceDisplayPK.companyId = :companyId ");
		SEL_DAILY_PERFORMACE_DISPLAY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAuthorityFormSheet a ");
		builderString.append("WHERE a.kfnmtAuthorityFormSheetPK.companyId = :companyId ");
		builderString
				.append("AND a.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode IN :dailyPerformanceFormatCode ");
		builderString.append("AND a.kfnmtAuthorityFormSheetPK.sheetNo IN :sheetNo ");
		builderString.append("ORDER BY a.kfnmtAuthorityFormSheetPK.sheetNo ASC");
		SEL_AUTHOR_FORM_SHEET = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KmkmtDivergenceReason a ");
		builderString.append("WHERE a.kmkmtDivergenceReasonPK.companyId = :companyId ");
		builderString.append("AND a.kmkmtDivergenceReasonPK.divTimeId = :divTimeId ");
		SEL_DIVERGENCE_REASON = builderString.toString();

//		builderString = new StringBuilder();
//		builderString.append("SELECT NEW ");
//		builderString.append(CodeName.class.getName());
//		builderString.append("(i.jobCd , i.jobName, i.bsymtJobInfoPK.jobId)");
//		builderString.append("FROM BsymtJobInfo i ");
//		builderString.append("JOIN BsymtJobHist h ");
//		builderString.append("ON i.bsymtJobInfoPK.cid = h.bsymtJobHistPK.cid ");
//		builderString.append("AND i.bsymtJobInfoPK.histId = h.bsymtJobHistPK.histId ");
//		builderString.append("AND i.bsymtJobInfoPK.jobId = h.bsymtJobHistPK.jobId ");
//		builderString.append("WHERE i.bsymtJobInfoPK.cid = :companyId ");
//		builderString.append("AND h.startDate <= :date ");
//		builderString.append("AND h.endDate >= :date ");
//		SEL_FIND_JOB_INFO = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT w.JOB_CD, w.JOB_NAME, w.JOB_ID FROM BSYMT_JOB_INFO w JOIN BSYMT_JOB_HIST a ");
		builderString.append("ON w.JOB_ID = a.JOB_ID AND w.CID = a.CID AND w.HIST_ID = a.HIST_ID ");
		builderString.append("WHERE a.CID = ? AND a.START_DATE <= ? AND a.END_DATE >= ? ");
		SEL_FIND_JOB_INFO_JDBC = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT NEW ");
		builderString.append(CodeName.class.getName());
		builderString.append("(i.jobCd , i.jobName, i.bsymtJobInfoPK.jobId)");
		builderString.append("FROM BsymtJobInfo i ");
		builderString.append("JOIN BsymtJobHist h ");
		builderString.append("ON i.bsymtJobInfoPK.cid = h.bsymtJobHistPK.cid ");
		builderString.append("AND i.bsymtJobInfoPK.histId = h.bsymtJobHistPK.histId ");
		builderString.append("AND i.bsymtJobInfoPK.jobId = h.bsymtJobHistPK.jobId ");
		builderString.append("WHERE i.bsymtJobInfoPK.cid = :companyId ");
		builderString.append("AND i.bsymtJobInfoPK.jobId = :jobId ");
		builderString.append("AND h.startDate <= :date ");
		builderString.append("AND h.endDate >= :date ");
		SEL_FIND_ID_JOB_INFO = builderString.toString();

//		builderString = new StringBuilder();
//		builderString.append("SELECT a ");
//		builderString.append("FROM BsymtClassification a ");
//		builderString.append("WHERE a.bsymtClassificationPK.cid = :companyId ");
//		SEL_FIND_CLASSIFICATION = builderString.toString();

//		builderString = new StringBuilder();
//		builderString.append("SELECT C ");
//		builderString.append("FROM KwlmtWorkLocation c ");
//		builderString.append("WHERE c.kwlmtWorkLocationPK.companyID = :companyId");
//		SEL_FIND_WORKPLACE_LOCATION = builderString.toString();

//		builderString = new StringBuilder();
//		builderString.append("SELECT w FROM BsymtWorkplaceInfo w JOIN ");
//		builderString.append("BsymtWorkplaceHist a ");
//		builderString.append("ON w.bsymtWorkplaceInfoPK.wkpid = a.bsymtWorkplaceHistPK.wkpid ");
//		builderString.append("AND w.bsymtWorkplaceInfoPK.cid = a.bsymtWorkplaceHistPK.cid ");
//		builderString.append("AND w.bsymtWorkplaceInfoPK.historyId = a.bsymtWorkplaceHistPK.historyId ");
//		builderString.append("WHERE a.bsymtWorkplaceHistPK.cid = :cid ");
//		builderString.append("AND a.strD <= :baseDate ");
//		builderString.append("AND a.endD >= :baseDate ");
//		SEL_ALL_WORKPLACE = builderString.toString();

		builderString = new StringBuilder();
		builderString
				.append("SELECT w.WKPCD, w.WKP_NAME, w.WKPID FROM BSYMT_WORKPLACE_INFO w JOIN BSYMT_WORKPLACE_HIST a ");
		builderString.append("ON w.WKPID = a.WKPID AND w.CID = a.CID AND w.HIST_ID = a.HIST_ID ");
		builderString.append("WHERE a.CID = ? AND a.START_DATE <= ? AND a.END_DATE >= ? ");
		SEL_ALL_WORKPLACE_JDBC = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT w FROM BsymtWorkplaceInfo w JOIN ");
		builderString.append("BsymtWorkplaceHist a ");
		builderString.append("ON w.bsymtWorkplaceInfoPK.wkpid = a.bsymtWorkplaceHistPK.wkpid ");
		builderString.append("AND w.bsymtWorkplaceInfoPK.cid = a.bsymtWorkplaceHistPK.cid ");
		builderString.append("AND w.bsymtWorkplaceInfoPK.historyId = a.bsymtWorkplaceHistPK.historyId ");
		builderString.append("WHERE a.bsymtWorkplaceHistPK.cid = :cid ");
		builderString.append("AND a.bsymtWorkplaceHistPK.wkpid = :wkpid ");
		builderString.append("AND a.strD <= :baseDate ");
		builderString.append("AND a.endD >= :baseDate ");
		SEL_FIND_WORKPLACE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e FROM KrcstErAlApplication e ");
		builderString.append("WHERE e.krcstErAlApplicationPK.cid = :cid ");
		builderString.append("AND e.krcstErAlApplicationPK.errorCd IN :errorCd");
		SEL_FIND_ER_AL_APP = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT d FROM KrcstDvgcTime d ");
		builderString.append("WHERE d.id.cid = :cid ");
		builderString.append("AND d.id.no IN :no");
		FIND_DVGC_TIME = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT NEW ");
		builderString.append(WorkPlaceHistTemp.class.getName());
		builderString.append("(aw.sid , awit.workPlaceId)");
		builderString.append(" FROM BsymtAffiWorkplaceHist aw ");
		builderString.append(" JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId");
		builderString.append(" WHERE aw.sid IN :listSid");
		builderString.append(" AND aw.strDate <= :baseDate");
		builderString.append(" AND aw.endDate >= :baseDate");
		SELECT_BY_LISTSID_WPH = builderString.toString();

		builderString = new StringBuilder();
		builderString.append(" SELECT bwi.bsymtWorkplaceInfoPK.wkpid, bwi.wkpDisplayName");
		builderString.append(" FROM BsymtWorkplaceInfo bwi ");
		builderString.append(
				" JOIN BsymtWorkplaceHist bwh ON bwi.bsymtWorkplaceInfoPK.historyId = bwh.bsymtWorkplaceHistPK.historyId");
		builderString.append(" AND bwi.bsymtWorkplaceInfoPK.wkpid = bwh.bsymtWorkplaceHistPK.wkpid");
		builderString.append(" AND bwi.bsymtWorkplaceInfoPK.cid = bwh.bsymtWorkplaceHistPK.cid");
		builderString.append(" WHERE bwi.bsymtWorkplaceInfoPK.cid = :companyId");
		builderString.append(" AND bwi.bsymtWorkplaceInfoPK.wkpid IN :wkpids");
		builderString.append(" AND bwh.strD <= :baseDate");
		builderString.append(" AND bwh.endD >= :baseDate");
		SELECT_BY_LISTSID_NAME = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT w FROM KshmtWorkingCond w");
		builderString.append(" WHERE w.kshmtWorkingCondPK.historyId IN :historyId");
		builderString.append(" AND w.kshmtWorkingCondPK.sid = :sid");
		builderString.append(" ORDER BY w.endD DESC");
		FIND_WORK_CONDITION = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT m FROM KrcdtEmployeeMonthlyPerError m");
		builderString.append(" WHERE m.krcdtEmployeeMonthlyPerErrorPK.employeeID = :employeeId");
		builderString.append(" AND m.krcdtEmployeeMonthlyPerErrorPK.errorType = :errorType");
		builderString.append(" AND m.krcdtEmployeeMonthlyPerErrorPK.yearMonth = :yearMonth");
		builderString.append(" AND m.krcdtEmployeeMonthlyPerErrorPK.closureId = :closureId");
		builderString.append(" AND m.krcdtEmployeeMonthlyPerErrorPK.closeDay = :closeDay");
		builderString.append(" AND m.krcdtEmployeeMonthlyPerErrorPK.isLastDay = :isLastDay");
		GET_MONTH_ERROR = builderString.toString();

	}

	@Override
	public List<ClosureDto> getClosureId(Map<String, String> empCodes, GeneralDate baseDate) {
		// get employment codes
		if (empCodes.isEmpty()) {
			return new ArrayList<>();
		}

		List<ClosureDto> closureDtos = new ArrayList<>();
		CollectionUtil.split(empCodes.values().stream().collect(Collectors.toList()),
				DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
					try (PreparedStatement statement = this.connection()
							.prepareStatement("select * from KCLMT_CLOSURE c" + " inner join KCLMT_CLOSURE_EMPLOYMENT e"
									+ " on c.CLOSURE_ID = e.CLOSURE_ID" + " where e.CID = ?" + " and EMPLOYMENT_CD in ("
									+ subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {

						statement.setString(1, AppContexts.user().companyId());
						for (int i = 0; i < subList.size(); i++) {
							statement.setString(i + 2, subList.get(i));
						}

						List<ClosureDto> results = new NtsResultSet(statement.executeQuery()).getList(rec -> {
							ClosureDto dto = new ClosureDto();
							dto.setCompanyId(rec.getString("CID"));
							dto.setClosureId(rec.getInt("CLOSURE_ID"));
							dto.setUseAtr(rec.getInt("USE_ATR"));
							dto.setClosureMonth(rec.getInt("CLOSURE_MONTH"));
							dto.setSid("");
							dto.setEmploymentCode(rec.getString("EMPLOYMENT_CD"));
							return dto;
						});

						closureDtos.addAll(results);

					} catch (SQLException ex) {
						throw new RuntimeException(ex);
					}
				});

		List<ClosureDto> result = new ArrayList<>();
		empCodes.forEach((key, value) -> {
			Optional<ClosureDto> optional = closureDtos.stream().filter(item -> item.getEmploymentCode().equals(value))
					.findFirst();
			if (optional.isPresent()) {
				ClosureDto closureDto = new ClosureDto();
				closureDto.setSid(key);
				closureDto.setEmploymentCode(optional.get().getEmploymentCode());
				closureDto.setClosureId(optional.get().getClosureId());
				closureDto.setClosureMonth(optional.get().getClosureMonth());
				closureDto.setCompanyId(optional.get().getCompanyId());
				closureDto.setUseAtr(optional.get().getUseAtr());
				closureDto.setDatePeriod(closureDto.getDatePeriod());
				result.add(closureDto);
			}
		});
		return result;
	}

	// @Override
	// public YearHolidaySettingDto getYearHolidaySetting() {
	// Optional<KalmtAnnualPaidLeave> entity =
	// this.queryProxy().find(AppContexts.user().companyId(),
	// KalmtAnnualPaidLeave.class);
	// if (entity.isPresent()) {
	// return new YearHolidaySettingDto();
	// }
	// return null;
	// }
	//
	// @Override
	// public SubstVacationDto getSubstVacationDto() {
	// Optional<KsvstComSubstVacation> entity =
	// this.queryProxy().find(AppContexts.user().companyId(),
	// KsvstComSubstVacation.class);
	// if (entity.isPresent()) {
	// return new SubstVacationDto(entity.get().getCid(),
	// entity.get().getIsManage() == 1 ? true : false,
	// entity.get().getExpirationDateSet(), entity.get().getAllowPrepaidLeave()
	// == 1 ? true : false);
	// }
	// return null;
	// }
	//
	// @Override
	// public CompensLeaveComDto getCompensLeaveComDto() {
	// Optional<KclmtCompensLeaveCom> entity =
	// this.queryProxy().find(AppContexts.user().companyId(),
	// KclmtCompensLeaveCom.class);
	// if (entity.isPresent()) {
	// return new CompensLeaveComDto(entity.get().getCid(),
	// entity.get().getManageAtr() == 1 ? true : false);
	// }
	// return null;
	// }
	//
	// @Override
	// public Com60HVacationDto getCom60HVacationDto() {
	// Optional<KshstCom60hVacation> entity =
	// this.queryProxy().find(AppContexts.user().companyId(),
	// KshstCom60hVacation.class);
	// if (entity.isPresent()) {
	// return new Com60HVacationDto(entity.get().getCid(),
	// entity.get().getManageDistinct() == 1 ? true : false,
	// entity.get().getSixtyHourExtra(), entity.get().getTimeDigestTive());
	// }
	// return null;
	// }

	@Override
	public List<String> getListJobTitle(DateRange dateRange) {
		return null;
	}

	@Override
	public List<String> getListEmployment() {
		return this.queryProxy().query(SEL_EMPLOYMENT_BY_CLOSURE, BsymtEmployment.class)
				.setParameter("companyId", AppContexts.user().companyId()).getList().stream().map(e -> {
					return e.getBsymtEmploymentPK().getCode();
				}).collect(Collectors.toList());
	}

	@Override
	public Map<String, String> getListWorkplace(String employeeId, DateRange dateRange) {
		Map<String, String> lstWkp = new HashMap<>();
		List<BsymtAffiWorkplaceHistItem> bsymtAffiWorkplaceHistItem = this.queryProxy()
				.query(SEL_WORKPLACE, BsymtAffiWorkplaceHistItem.class).setParameter("sId", employeeId)
				.setParameter("startDate", dateRange.getEndDate()).getList();
		List<String> workPlaceIds = bsymtAffiWorkplaceHistItem.stream().map(item -> item.getWorkPlaceId())
				.collect(Collectors.toList());

		String query = "SELECT w FROM BsymtWorkplaceInfo w WHERE w.bsymtWorkplaceInfoPK.wkpid IN :wkpId AND w.bsymtWorkplaceHist.strD <= :baseDate AND w.bsymtWorkplaceHist.endD >= :baseDate";
		this.queryProxy().query(query, BsymtWorkplaceInfo.class).setParameter("wkpId", workPlaceIds)
				.setParameter("baseDate", dateRange.getEndDate()).getList().stream().forEach(w -> {
					lstWkp.put(w.getBsymtWorkplaceInfoPK().getWkpid(), w.getWkpName());
				});
		return lstWkp;
	}

	@Override
	public Map<String, String> getListWorkplaceAllEmp(List<String> employeeId, GeneralDate dateRange) {
		if (employeeId.isEmpty())
			return Collections.emptyMap();
		Map<String, String> lstWkp = new HashMap<>();
		List<BsymtAffiWorkplaceHistItem> bsymtAffiWorkplaceHistItem = this.queryProxy()
				.query(SEL_WORKPLACE_ALL, BsymtAffiWorkplaceHistItem.class).setParameter("sIds", employeeId)
				.setParameter("baseDate", dateRange).getList();
		List<String> workPlaceIds = bsymtAffiWorkplaceHistItem.stream().map(item -> item.getWorkPlaceId())
				.collect(Collectors.toList());

		String query = "SELECT w FROM BsymtWorkplaceInfo w WHERE w.bsymtWorkplaceInfoPK.wkpid IN :wkpId AND w.bsymtWorkplaceHist.strD <= :baseDate AND w.bsymtWorkplaceHist.endD >= :baseDate";
		this.queryProxy().query(query, BsymtWorkplaceInfo.class).setParameter("wkpId", workPlaceIds)
				.setParameter("baseDate", dateRange).getList().stream().forEach(w -> {
					lstWkp.put(w.getBsymtWorkplaceInfoPK().getWkpid(), w.getWkpName());
				});
		return lstWkp;
	}

	@Override
	public List<String> getListEmpInDepartment(String employeeId, DateRange dateRange) {
		List<BsymtAffiWorkplaceHistItem> bsymtAffiWorkplaceHistItem = this.queryProxy()
				.query(SEL_WORKPLACE, BsymtAffiWorkplaceHistItem.class).setParameter("sId", employeeId)
				.setParameter("startDate", dateRange.getStartDate()).setParameter("endDate", dateRange.getEndDate()).getList();
		List<String> workPlaceIds = bsymtAffiWorkplaceHistItem.stream().map(item -> item.getWorkPlaceId())
				.collect(Collectors.toList());
		if (workPlaceIds.isEmpty())
			return Collections.emptyList();
		return this.queryProxy().query(FIND_EMP_WORKPLACE, String.class).setParameter("workPlaceId", workPlaceIds)
				.setParameter("startDate", dateRange.getStartDate()).setParameter("endDate", dateRange.getEndDate()).getList();
	}

	@Override
	public List<WorkInfoOfDailyPerformanceDto> getListWorkInfoOfDailyPerformance(List<String> lstEmployee,
			DateRange dateRange) {
		if (lstEmployee.isEmpty())
			return new ArrayList<>();
		List<WorkInfoOfDailyPerformanceDto> results = new ArrayList<>();
		CollectionUtil.split(lstEmployee, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			results.addAll(this.queryProxy().query(SEL_DAILY_WORK_INFO, KrcdtDaiPerWorkInfo.class)
					.setParameter("lstDate", dateRange.toListDate()).setParameter("lstEmployee", subList).getList(e -> {
						return new WorkInfoOfDailyPerformanceDto(e.krcdtDaiPerWorkInfoPK.employeeId, e.calculationState,
								e.krcdtDaiPerWorkInfoPK.ymd, e.recordWorkWorktypeCode, e.recordWorkWorktimeCode,
								e.scheduleWorkWorktypeCode, e.scheduleWorkWorktimeCode,
								e.scheduleTimes == null ? false : true);
					}));
		});
		return results;
	}

	@Override
	public List<String> getListClassification() {
		return this.queryProxy().query(SEL_CLASSIFICATION, BsymtClassification.class)
				.setParameter("companyId", AppContexts.user().companyId()).getList().stream().map(c -> {
					return c.getBsymtClassificationPK().getClscd();
				}).collect(Collectors.toList());
	}

	@Override
	public List<DailyPerformanceEmployeeDto> getListEmployee(List<String> lstJobTitle, List<String> lstEmployment,
			Map<String, String> lstWorkplace, List<String> lstClassification) {
		List<BsymtEmployeeDataMngInfo> lstEmployee = new ArrayList<>();
		CollectionUtil.split(lstWorkplace.keySet().stream().collect(Collectors.toList()),
				DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
					lstEmployee.addAll(this.queryProxy().query(SEL_EMPLOYEE, BsymtEmployeeDataMngInfo.class)
							.setParameter("lstWkp", subList).getList());
				});

		List<String> ids = lstEmployee.stream().map((employee) -> {
			return employee.bsymtEmployeeDataMngInfoPk.pId.trim();
		}).collect(Collectors.toList());
		List<BpsmtPerson> lstPerson = new ArrayList<>();
		CollectionUtil.split(ids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstPerson.addAll(this.queryProxy().query(SEL_PERSON, BpsmtPerson.class).setParameter("lstPersonId", subList)
					.getList());
		});

		return lstEmployee.stream().map((employee) -> {
			for (BpsmtPerson person : lstPerson) {
				if (person.bpsmtPersonPk.pId.equals(employee.bsymtEmployeeDataMngInfoPk.pId)) {
					return new DailyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId,
							employee.employeeCode, person.personName, lstWorkplace.values().stream().findFirst().get(),
							lstWorkplace.keySet().stream().findFirst().get(), "", false);
				}
			}
			return new DailyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId, employee.employeeCode, "",
					lstWorkplace.values().stream().findFirst().get(), lstWorkplace.keySet().stream().findFirst().get(),
					"", false);
		}).collect(Collectors.toList());
	}

	@Override
	public List<DailyPerformanceEmployeeDto> getListEmployee(List<String> sids) {
		if (sids.isEmpty())
			return Collections.emptyList();
		List<BsymtEmployeeDataMngInfo> resultList = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_LIST_EMPID, BsymtEmployeeDataMngInfo.class)
					.setParameter("listSid", subList).getList());
		});

		List<String> ids = resultList.stream().map((employee) -> {
			return employee.bsymtEmployeeDataMngInfoPk.pId.trim();
		}).collect(Collectors.toList());
		List<BpsmtPerson> lstPerson = new ArrayList<>();
		CollectionUtil.split(ids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			lstPerson.addAll(this.queryProxy().query(SEL_PERSON, BpsmtPerson.class).setParameter("lstPersonId", subList)
					.getList());
		});
		return resultList.stream().map((employee) -> {
			for (BpsmtPerson person : lstPerson) {
				if (person.bpsmtPersonPk.pId.equals(employee.bsymtEmployeeDataMngInfoPk.pId)) {
					return new DailyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId,
							employee.employeeCode, person.personName, "", "", "", false);
				}
			}
			return new DailyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId, employee.employeeCode, "",
					"", "", "", false);
		}).collect(Collectors.toList());
	}

	@Override
	public List<String> getListBusinessType(List<String> lstEmployee, DateRange dateRange) {
		List<String> businessTypes = new ArrayList<>();
		CollectionUtil.split(lstEmployee, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			businessTypes.addAll(this.queryProxy().query(SEL_BUSINESS_TYPE, String.class)
					.setParameter("lstSID", subList).setParameter("startYmd", dateRange.getStartDate())
					.setParameter("endYmd", dateRange.getEndDate()).getList());
		});
		return businessTypes;
	}

	@Override
	public List<FormatDPCorrectionDto> getListFormatDPCorrection(List<String> lstBusinessType) {
		if (lstBusinessType.size() > 1) {
			return this.queryProxy().query(SEL_FORMAT_DP_CORRECTION_MULTI, KrcmtBusinessTypeDaily.class)
					.setParameter("companyId", AppContexts.user().companyId())
					.setParameter("lstBusinessTypeCode", lstBusinessType).getList().stream()
					.map(f -> new FormatDPCorrectionDto(f.krcmtBusinessTypeDailyPK.companyId,
							f.krcmtBusinessTypeDailyPK.businessTypeCode, f.krcmtBusinessTypeDailyPK.attendanceItemId,
							String.valueOf(f.krcmtBusinessTypeDailyPK.sheetNo),
							f.order, f.columnWidth != null
									? f.columnWidth.intValue() > 0 ? f.columnWidth.intValue() : 100 : 100))
					.distinct().collect(Collectors.toList());
		} else {
			return this.queryProxy().query(SEL_FORMAT_DP_CORRECTION, KrcmtBusinessTypeDaily.class)
					.setParameter("companyId", AppContexts.user().companyId())
					.setParameter("lstBusinessTypeCode", lstBusinessType).getList().stream()
					.map(f -> new FormatDPCorrectionDto(f.krcmtBusinessTypeDailyPK.companyId,
							f.krcmtBusinessTypeDailyPK.businessTypeCode, f.krcmtBusinessTypeDailyPK.attendanceItemId,
							String.valueOf(f.krcmtBusinessTypeDailyPK.sheetNo),
							f.order, f.columnWidth != null
									? f.columnWidth.intValue() > 0 ? f.columnWidth.intValue() : 100 : 100))
					.distinct().collect(Collectors.toList());
		}
	}

	@Override
	public List<DPBusinessTypeControl> getListBusinessTypeControl(String companyId, String authorityDailyID,
			List<Integer> lstAttendanceItem, boolean use) {
		return this.queryProxy().query(SEL_DP_TYPE_CONTROL, KshstDailyServiceTypeControl.class)
				.setParameter("authorityDailyID", authorityDailyID).setParameter("lstItem", lstAttendanceItem).getList()
				.stream().map(c -> {
					return new DPBusinessTypeControl(c.kshstDailyServiceTypeControlPK.itemDailyID, c.toUse == 1,
							c.canBeChangedByOthers == 1, c.youCanChangeIt == 1);
				}).collect(Collectors.toList());
	}

	@Override
	public List<DPAttendanceItem> getListAttendanceItem(List<Integer> lstAttendanceItem) {
		if (lstAttendanceItem.size() <= 0) {
			return Collections.emptyList();
		}

		String companyId = AppContexts.user().companyId();
		List<KrcmtDailyAttendanceItem> entities = this.queryProxy()
				.query(SEL_ATTENDANCE_ITEM, KrcmtDailyAttendanceItem.class).setParameter("companyId", companyId)
				.setParameter("lstItem", lstAttendanceItem).getList();
		return entities.stream().map(i -> {
			return new DPAttendanceItem(i.krcmtDailyAttendanceItemPK.attendanceItemId, i.attendanceItemName,
					i.displayNumber, i.userCanSet == 1 ? true : false, i.nameLineFeedPosition, i.dailyAttendanceAtr,
					i.typeOfMaster != null ? i.typeOfMaster.intValue() : null,
					i.primitiveValue == null ? null : i.primitiveValue.intValue());
		}).collect(Collectors.toList());
	}

	@Override
	public List<DPAttendanceItemControl> getListAttendanceItemControl(String companyId,
			List<Integer> lstAttendanceItem) {
		if (lstAttendanceItem.isEmpty())
			return Collections.emptyList();
		return this.queryProxy().query(SEL_ATTENDANCE_ITEM_CONTROL, KshstControlOfAttendanceItems.class)
				.setParameter("companyId", companyId).setParameter("lstItem", lstAttendanceItem).getList().stream()
				.map(c -> {
					return new DPAttendanceItemControl(c.kshstControlOfAttendanceItemsPK.itemDailyID,
							c.inputUnitOfTimeItem, c.headerBgColorOfDailyPer != null ? c.headerBgColorOfDailyPer : "",
							null);
				}).collect(Collectors.toList());
	}

	@Override
	public List<DPErrorDto> getListDPError(DateRange dateRange, List<String> lstEmployee) {
		List<DPErrorDto> listDPError = new ArrayList<>();
		CollectionUtil.split(lstEmployee, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listDPError.addAll(this.queryProxy().query(SEL_DP_ERROR_EMPLOYEE, KrcdtSyainDpErList.class)
					.setParameter("startDate", dateRange.getStartDate()).setParameter("endDate", dateRange.getEndDate())
					.setParameter("lstEmployee", subList).getList().stream().map(e -> {
						return new DPErrorDto(e.errorCode, "", e.employeeId, e.processingDate,
								!e.erAttendanceItem.isEmpty() ? e.erAttendanceItem.stream()
										.map(x -> x.krcdtErAttendanceItemPK.attendanceItemId)
										.collect(Collectors.toList()) : Collections.emptyList(),
								e.errorCancelable.intValue() == 1 ? true : false, e.errorAlarmMessage);
					}).collect(Collectors.toList()));
		});
		return listDPError;
	}

	@Override
	public List<DPErrorDto> getListDPError(DateRange dateRange, List<String> lstEmployee, List<String> errorCodes) {
		List<DPErrorDto> dpErrors = new ArrayList<>();
		CollectionUtil.split(lstEmployee, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			dpErrors.addAll(this.queryProxy().query(SEL_DP_ERROR_EMPLOYEE_CONDITION_ERRORS, KrcdtSyainDpErList.class)
					.setParameter("startDate", dateRange.getStartDate()).setParameter("endDate", dateRange.getEndDate())
					.setParameter("lstEmployee", subList).setParameter("errorCodes", errorCodes).getList().stream()
					.map(e -> {
						return new DPErrorDto(e.errorCode, "", e.employeeId, e.processingDate,
								!e.erAttendanceItem.isEmpty() ? e.erAttendanceItem.stream()
										.map(x -> x.krcdtErAttendanceItemPK.attendanceItemId)
										.collect(Collectors.toList()) : Collections.emptyList(),
								e.errorCancelable.intValue() == 1 ? true : false, e.errorAlarmMessage);
					}).collect(Collectors.toList()));
		});
		return dpErrors;
	}

	@Override
	public List<DPErrorSettingDto> getErrorSetting(String companyId, List<String> listErrorCode, boolean showError,
			boolean showAlarm, boolean showOther) {
		return getErrorSettingN(companyId, listErrorCode, showError, showAlarm, showOther);
		// List<Object[]> enities = this.queryProxy().query(SEL_ERROR_SETTING,
		// Object[].class).setParameter("companyId",
		// companyId).setParameter("lstCode", listErrorCode)
		// .getList();
		// return enities.stream().map(x ->{
		// KwrmtErAlWorkRecord alWorkRecord = (KwrmtErAlWorkRecord) x[0];
		// String alCondition = (String) x[1];
		// return new
		// DPErrorSettingDto(alWorkRecord.kwrmtErAlWorkRecordPK.companyId,
		// alWorkRecord.kwrmtErAlWorkRecordPK.errorAlarmCode,
		// alWorkRecord.errorAlarmName, alWorkRecord.fixedAtr == 1 ? true :
		// false,
		// alWorkRecord.useAtr == 1 ? true : false, alWorkRecord.typeAtr,
		// alCondition == null ? "" : alCondition,
		// alWorkRecord.boldAtr == 1 ? true : false, alWorkRecord.messageColor,
		// alWorkRecord.cancelableAtr == 1 ? true : false,
		// alWorkRecord.errorDisplayItem == null ? null :
		// alWorkRecord.errorDisplayItem.intValue());
		// }).collect(Collectors.toList());
	}

	private List<DPErrorSettingDto> getErrorSettingN(String companyId, List<String> listErrorCode, boolean showError,
			boolean showAlarm, boolean showOther) {
		List<DPErrorSettingDto> dtos = new ArrayList<>();
		String textIn = "";
		for (int i = 0; i < listErrorCode.size(); i++) {
			String text = listErrorCode.get(i);
			if (i == (listErrorCode.size() - 1)) {
				textIn += "'" + text + "'";
			} else {
				textIn += "'" + text + "'" + ",";
			}
		}

		List<Integer> atr = new ArrayList<>();
		if (showError) {
			atr.add(0);
		}
		if (showAlarm) {
			atr.add(1);
		}

		if (showOther) {
			atr.add(2);
		}

		Connection con = this.getEntityManager().unwrap(Connection.class);
		String query = "SELECT s.*, u.MESSAGE_DISPLAY FROM KRCMT_ERAL_SET as s JOIN KRCMT_ERAL_CONDITION as u ON s.ERAL_CHECK_ID = u.ERAL_CHECK_ID WHERE s.CID = ? AND s.ERROR_ALARM_CD IN ("
				+ textIn + ")";
		if (atr.isEmpty())
			return new ArrayList<>();
		String typeAtrParam = "AND ERAL_ATR IN ( ";
		for (int i = 0; i < atr.size(); i++) {
			Integer text = atr.get(i);
			if (i == (atr.size() - 1)) {
				typeAtrParam += "" + text + " )";
			} else {
				typeAtrParam += "" + text + ",";
			}
		}
		query = query.concat(typeAtrParam);
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, companyId);
			// Array array = pstatement.getConnection().createArrayOf("ANY ",
			// new Object[]{"A", "B","C"});
			// pstatement.setArray(2, array);
			ResultSet rs = pstatement.executeQuery();

			while (rs.next()) {

				String errorAlarmCode = rs.getString("ERROR_ALARM_CD");
				String errorAlarmName = rs.getString("ERROR_ALARM_NAME");
				int fixedAtr = rs.getInt("FIXED_ATR");
				int useAtr = rs.getInt("USE_ATR");
				int typeAtr = rs.getInt("ERAL_ATR");
				int boldAtr = rs.getInt("BOLD_ATR");
				String messageColor = rs.getString("MESSAGE_COLOR");
				int cancelableAtr = rs.getInt("CANCELABLE_ATR");
				Integer errorDisplayItem = rs.getInt("ERROR_DISPLAY_ITEM");
				String messageDisplay = rs.getString("MESSAGE_DISPLAY");

				dtos.add(new DPErrorSettingDto(companyId, errorAlarmCode, errorAlarmName, fixedAtr == 1 ? true : false,
						useAtr == 1 ? true : false, typeAtr, messageDisplay == null ? "" : messageDisplay,
						boldAtr == 1 ? true : false, messageColor, cancelableAtr == 1 ? true : false,
						errorDisplayItem));
			}

			return dtos;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dtos;
	}

	@Override
	public List<DPSheetDto> getFormatSheets(List<String> lstBusinessType) {
		return this.queryProxy().query(SEL_FORMAT_SHEET, KrcmtBusinessFormatSheet.class)
				.setParameter("companyId", AppContexts.user().companyId())
				.setParameter("lstBusinessTypeCode", lstBusinessType).getList().stream().map(s -> {
					return new DPSheetDto(String.valueOf(s.krcmtBusinessFormatSheetPK.sheetNo), s.sheetName);
				}).collect(Collectors.toList());
	}

	@Override
	public AffEmploymentHistoryDto getAffEmploymentHistory(String companyId, String employeeId, GeneralDate date) {
		List<BsymtEmploymentHistItem> entity = this.queryProxy()
				.query(SEL_EMPLOYMENT_HISTORY, BsymtEmploymentHistItem.class).setParameter("empId", employeeId)
				.setParameter("baseDate", date).setParameter("companyId", companyId).getList();
		return entity.isEmpty() ? null : new AffEmploymentHistoryDto(entity.get(0).empCode, employeeId);
	}

	@Override
	public EmploymentDto findEmployment(String companyId, String employmentCode) {
		return this.queryProxy().find(new BsymtEmploymentPK(companyId, employmentCode), BsymtEmployment.class).map(
				e -> new EmploymentDto(companyId, employmentCode, e.getName(), e.getEmpExternalCode(), e.getMemo()))
				.orElse(null);
	}

	@Override
	public List<CodeName> findEmployment(String companyId) {
		try (PreparedStatement statement = this.connection().prepareStatement(SEL_EMPLOYMENT_BY_CLOSURE_JDBC)) {
			statement.setString(1, companyId);
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("CODE"), rs.getString("NAME"), "");
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CodeName> findJobInfo(String companyId, GeneralDate date) {
		try (PreparedStatement statement = this.connection().prepareStatement(SEL_FIND_JOB_INFO_JDBC)) {
			statement.setString(1, companyId);
			statement.setDate(2, Date.valueOf(date.localDate()));
			statement.setDate(3, Date.valueOf(date.localDate()));
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("JOB_CD"), rs.getString("JOB_NAME"), rs.getString("JOB_ID"));
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CodeName> findClassification(String companyId) {
		try (PreparedStatement statement = this.connection().prepareStatement(SEL_FIND_CLASSIFICATION_JDBC)) {
			statement.setString(1, companyId);
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("CLSCD"), rs.getString("CLSNAME"), "");
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CodeName> findWorkplace(String companyId, GeneralDate date) {
		try (PreparedStatement statement = this.connection().prepareStatement(SEL_ALL_WORKPLACE_JDBC)) {
			statement.setString(1, companyId);
			statement.setDate(2, Date.valueOf(date.localDate()));
			statement.setDate(3, Date.valueOf(date.localDate()));
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("WKPCD"), rs.getString("WKP_NAME"), rs.getString("WKPID"));
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CodeName> findWorkplaceLocation(String companyId) {
		String sql = SEL_FIND_WORKPLACE_LOCATION_JDBC;
		try (PreparedStatement statement = this.connection().prepareStatement(sql)) {
			statement.setString(1, companyId);
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("WORK_LOCATION_CD"), rs.getString("WORK_LOCATION_NAME"), "");
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CodeName> findReason(String companyId) {
		try (PreparedStatement statement = this.connection().prepareStatement(SELECT_ALL_DIVREASON_JDBC)) {
			statement.setString(1, companyId);
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("REASON_CD"), rs.getString("REASON"), rs.getInt("NO").toString());
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ClosureEmploymentDto findByEmploymentCD(String companyID, String employmentCD) {
		return this.queryProxy()
				.find(new KclmpClosureEmploymentPK(companyID, employmentCD), KclmtClosureEmployment.class)
				.map(x -> new ClosureEmploymentDto(x.kclmpClosureEmploymentPK.companyId,
						x.kclmpClosureEmploymentPK.employmentCD, x.closureId))
				.orElse(null);
	}

	@Override
	public List<DailyRecEditSetDto> getDailyRecEditSet(List<String> listEmployeeId, DateRange dateRange) {
		if (listEmployeeId.isEmpty())
			return Collections.emptyList();
		List<DailyRecEditSetDto> editSets = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			editSets.addAll(this.queryProxy().query(SEL_DAILY_REC_EDIT_SET, KrcdtDailyRecEditSet.class)
					.setParameter("employeeIds", subList).setParameter("ymds", dateRange.toListDate()).getList()
					.stream().map(s -> {
						return new DailyRecEditSetDto(s.krcdtDailyRecEditSetPK.employeeId,
								s.krcdtDailyRecEditSetPK.processingYmd, s.krcdtDailyRecEditSetPK.attendanceItemId,
								s.editState);
					}).collect(Collectors.toList()));
		});
		return editSets;
	}

	@Override
	public List<WorkInfoOfDailyPerformanceDetailDto> find(List<String> listEmployeeId, DateRange dateRange) {
		List<WorkInfoOfDailyPerformanceDetailDto> datas = new ArrayList<>();
		CollectionUtil.split(listEmployeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			datas.addAll(this.queryProxy().query(SEL_DAILY_WORK_INFO, KrcdtDaiPerWorkInfo.class)
					.setParameter("lstDate", dateRange.toListDate()).setParameter("lstEmployee", subList)
					.getList(c -> new WorkInfoOfDailyPerformanceDetailDto(c.krcdtDaiPerWorkInfoPK.employeeId,
							new WorkInformationDto(c.recordWorkWorktimeCode, c.recordWorkWorktypeCode),
							new WorkInformationDto(c.scheduleWorkWorktimeCode, c.recordWorkWorktypeCode),
							EnumAdaptor.valueOf(c.calculationState, CalculationStateDto.class),
							EnumAdaptor.valueOf(c.goStraightAttribute, NotUseAttributeDto.class),
							EnumAdaptor.valueOf(c.backStraightAttribute, NotUseAttributeDto.class),
							c.krcdtDaiPerWorkInfoPK.ymd,
							c.scheduleTimes.isEmpty() ? null
									: c.scheduleTimes.stream()
											.map(s -> new ScheduleTimeSheetDto(s.krcdtWorkScheduleTimePK.workNo,
													new TimeWithDayAttr(s.attendance),
													new TimeWithDayAttr(s.leaveWork)))
											.collect(Collectors.toList()))));
		});
		return datas;
	}

	@Override
	public Optional<ActualLockDto> findAutualLockById(String companyId, int closureId) {
		return this.queryProxy().find(new KrcstActualLockPK(companyId, closureId), KrcstActualLock.class)
				.map(c -> new ActualLockDto(companyId, closureId, c.getDLockState(), c.getMLockState()));
	}

	@SneakyThrows
	@Override
	public List<WorkFixedDto> findWorkFixed(int closureId, int yearMonth) {
		try (val statement = this.connection().prepareStatement(
				"select * from KRCST_WORK_FIXED where CID = ? and CLOSURE_ID = ? and CONFIRM_CLS = 1")) {
			statement.setString(1, AppContexts.user().companyId());
			statement.setInt(2, closureId);

			List<WorkFixedDto> workOp = new NtsResultSet(statement.executeQuery()).getList(rec -> {
				KrcstWorkFixed w = new KrcstWorkFixed();
				w.setKrcstWorkFixedPK(
						new KrcstWorkFixedPK(rec.getString("WKPID"), rec.getInt("CLOSURE_ID"), rec.getString("CID")));
				w.setConfirmPid(rec.getString("CONFIRM_PID"));
				w.setConfirmCls(rec.getInt("CONFIRM_CLS"));
				w.setFixedDate(rec.getDate("FIXED_DATE"));
				w.setProcessYm(rec.getInt("PROCESS_YM"));

				return new WorkFixedDto(closureId, w.getConfirmPid(), w.getKrcstWorkFixedPK().getWkpid(),
						w.getConfirmCls(), w.getFixedDate(), yearMonth, w.getKrcstWorkFixedPK().getCid());
			});

			return workOp;
		}
	}

	@Override
	public OperationOfDailyPerformanceDto findOperationOfDailyPerformance() {
		String companyId = AppContexts.user().companyId();
		OperationOfDailyPerformanceDto dto = new OperationOfDailyPerformanceDto();
		Optional<KrcmtFormatPerformance> format = this.queryProxy().find(new KrcmtFormatPerformancePk(companyId),
				KrcmtFormatPerformance.class);
		Optional<KrcmtDaiPerformEdFun> edFunc = this.queryProxy().find(new KrcmtDaiPerformEdFunPk(companyId),
				KrcmtDaiPerformEdFun.class);
		dto.setComment(edFunc.isPresent() ? edFunc.get().comment : "");
		dto.setSettingUnit(
				EnumAdaptor.valueOf(format.isPresent() ? format.get().settingUnitType : 1, SettingUnitType.class));
		dto.setShowError(edFunc.isPresent() ? edFunc.get().checkErrRefDisp == 1 : false);
		return dto;
	}

	@Override
	public List<AuthorityFormatInitialDisplayDto> findAuthorityFormatInitialDisplay(String companyId) {
		return this.queryProxy().query(SEL_DAILY_PERFORMACE_DISPLAY, KfnmtDailyPerformanceDisplay.class)
				.setParameter("companyId", companyId).getList(k -> new AuthorityFormatInitialDisplayDto(companyId,
						k.kfnmtDailyPerformanceDisplayPK.dailyPerformanceFormatCode));
	}

	@Override
	public List<AuthorityFomatDailyDto> findAuthorityFomatDaily(String companyId, List<String> formatCodes) {
		return this.queryProxy().query(SEL_AUTHOR_DAILY_ITEM, KfnmtAuthorityDailyItem.class)
				.setParameter("companyId", companyId).setParameter("dailyPerformanceFormatCodes", formatCodes)
				.getList(f -> new AuthorityFomatDailyDto(f.kfnmtAuthorityDailyItemPK.companyId,
						f.kfnmtAuthorityDailyItemPK.dailyPerformanceFormatCode,
						f.kfnmtAuthorityDailyItemPK.attendanceItemId, f.kfnmtAuthorityDailyItemPK.sheetNo,
						f.displayOrder, f.columnWidth));
	}

	@Override
	public List<AuthorityFormatSheetDto> findAuthorityFormatSheet(String companyId, List<String> formatCode,
			List<BigDecimal> sheetNo) {
		List<KfnmtAuthorityFormSheet> ent = this.queryProxy()
				.query(SEL_AUTHOR_FORM_SHEET, KfnmtAuthorityFormSheet.class).setParameter("companyId", companyId)
				.setParameter("dailyPerformanceFormatCode", formatCode).setParameter("sheetNo", sheetNo).getList();
		return ent.isEmpty() ? Collections.emptyList()
				: ent.stream()
						.map(f -> new AuthorityFormatSheetDto(f.kfnmtAuthorityFormSheetPK.companyId,
								f.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode,
								f.kfnmtAuthorityFormSheetPK.sheetNo, f.sheetName))
						.collect(Collectors.toList());
	}

	@Override
	public List<DivergenceTimeDto> findDivergenceTime(String companyId, List<Integer> divergenceNo) {
		return this.queryProxy().query(FIND_DVGC_TIME, KrcstDvgcTime.class).setParameter("cid", companyId)
				.setParameter("no", divergenceNo)
				.getList(x -> new DivergenceTimeDto(x.getId().getNo(), companyId, x.getDvgcTimeUseSet().intValue(),
						x.getDvgcReasonInputed().intValue() == 1 ? true : false,
						x.getDvgcReasonSelected().intValue() == 1 ? true : false));

	}

	@Override
	public List<ReasonCodeName> findDivergenceReason(String companyId, int divTimeId) {
		return this.queryProxy().query(SEL_DIVERGENCE_REASON, KmkmtDivergenceReason.class)
				.setParameter("companyId", companyId).setParameter("divTimeId", divTimeId)
				.getList(x -> new ReasonCodeName(x.kmkmtDivergenceReasonPK.divReasonCode, x.divReason));
	}

	@Override
	public List<DailyPerformanceAuthorityDto> findDailyAuthority(String roleId) {
		List<KrcmtDaiPerformanceAut> entities = this.queryProxy()
				.query(GET_DAI_PER_AUTH_WITH_ROLE, KrcmtDaiPerformanceAut.class).setParameter("roleId", roleId)
				.getList();
		List<DailyPerformanceAuthorityDto> results = new ArrayList<>();
		entities.forEach(ent -> {
			BigDecimal avaiBigDecimal = ent.availability;
			boolean availability = false;
			if (avaiBigDecimal.intValue() == 1) {
				availability = true;
			}
			results.add(new DailyPerformanceAuthorityDto(roleId, ent.pk.functionNo, availability));
		});
		return results;
	}

	/**
	 * find authority for monthlyPer kmw003 screen
	 */
	@Override
	@SneakyThrows
	public List<MonthlyPerformanceAuthorityDto> findAuthority(String roleId, BigDecimal availability) {
		List<MonthlyPerformanceAuthorityDto> data = new ArrayList<>();
		try (PreparedStatement statement = this.connection().prepareStatement(
				"SELECT * from KRCMT_DAI_PERFORMANCE_AUT h WHERE h.ROLE_ID = ? AND h.AVAILABILITY = ?")) {
			statement.setString(1, roleId);
			statement.setBigDecimal(2, availability);
			boolean avai = availability.intValue() == 1;
			data.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> {
				return new MonthlyPerformanceAuthorityDto(roleId, rec.getBigDecimal("FUNCTION_NO"), avai);
			}));
	
			return data;
		}
	}

	@Override
	public List<WorkTimeWorkplaceDto> findWorkHours(String companyId, String workplaceId) {
		return this.queryProxy().query(SELECT_WORKTIME_WORKPLACE_BYID, KshmtWorkTimeWorkplace.class)
				.setParameter("companyID", companyId).setParameter("workplaceID", workplaceId)
				.getList(c -> new WorkTimeWorkplaceDto(c.kshmtWorkTimeWorkplacePK.companyID,
						c.kshmtWorkTimeWorkplacePK.workplaceID, c.kshmtWorkTimeWorkplacePK.workTimeID));
	}

	@Override
	public List<CodeName> findWorkTimeZone(String companyId, List<String> shifCode) {
		String sql = FIND_WORK_TIME_ZONE_JDBC;
		if (!shifCode.isEmpty()) {
			sql = sql.concat(" AND WORKTIME_CD IN ( ")
					.concat(shifCode.stream().map(x -> "?").collect(Collectors.joining(",")))
					.concat(") ORDER BY WORKTIME_CD ASC");
		}
		try (PreparedStatement statement = this.connection().prepareStatement(sql)) {
			statement.setString(1, companyId);
			for (int i = 0; i < shifCode.size(); i++) {
				statement.setString(i + 2, shifCode.get(i));
			}
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("WORKTIME_CD"), rs.getString("NAME"), "");
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<WorkTypeChangedDto> findWorkTypeChanged(String employmentCode, String typeCode, String companyId) {
		List<WorkTypeChangedDto> dtos = this.queryProxy()
				.query(GET_ALL_WORK_TYPE_CHANGED, KrcmtWorktypeChangeable.class).setParameter("companyId", companyId)
				.setParameter("employeeCode", employmentCode)
				.getList(x -> new WorkTypeChangedDto(String.valueOf(x.pk.workTypeGroupNo), x.pk.workTypeCode));
		if (!dtos.isEmpty()) {
			Map<String, List<WorkTypeChangedDto>> mapGroupNo = dtos.stream().filter(x -> x.getTypeCode() != "")
					.collect(Collectors.groupingBy(WorkTypeChangedDto::getGroupNo));
			List<WorkTypeChangedDto> temps = new ArrayList<>();
			mapGroupNo.entrySet().forEach(x -> {
				x.getValue().forEach(data -> {
					if (data.getTypeCode().equals(typeCode)) {
						temps.addAll(x.getValue());
					}
				});
			});
			return temps;
		} else {
			return Collections.emptyList();
		}

	}

	@Override
	public List<CodeName> findWorkType(String companyId, Set<String> typeCodes) {
		String sql = SELECT_WORKTYPE_JDBC;
		if (!typeCodes.isEmpty()) {
			sql = sql.concat(" AND CD IN ( ").concat(typeCodes.stream().map(x -> "?").collect(Collectors.joining(",")))
					.concat(")");
		}
		try (PreparedStatement statement = this.connection().prepareStatement(sql)) {
			List<String> xcodes = new ArrayList<>(typeCodes);
			statement.setString(1, companyId);
			for (int i = 0; i < xcodes.size(); i++) {
				statement.setString(i + 2, xcodes.get(i));
			}
			return new NtsResultSet(statement.executeQuery()).getList(rs -> {
				return new CodeName(rs.getString("CD"), rs.getString("NAME"), "");
			});

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateColumnsWidth(Map<Integer, Integer> lstHeader, List<String> formatCodes) {
		List<AuthorityFomatDailyDto> items = this.findAuthorityFomatDaily(AppContexts.user().companyId(), formatCodes);
		List<KfnmtAuthorityDailyItem> entitys = items.stream()
				.map(x -> new KfnmtAuthorityDailyItem(
						new KfnmtAuthorityDailyItemPK(x.getCompanyId(), x.getDailyPerformanceFormatCode(),
								x.getAttendanceItemId(), x.getSheetNo()),
						x.getDisplayOrder(), new BigDecimal(lstHeader.get(x.getAttendanceItemId()))))
				.collect(Collectors.toList());
		this.commandProxy().updateAll(entitys);
	}

	@Override
	public Optional<CodeName> findJobInfoId(String companyId, GeneralDate baseDate, String id) {
		return this.queryProxy().query(SEL_FIND_ID_JOB_INFO, CodeName.class).setParameter("companyId", companyId)
				.setParameter("jobId", id).setParameter("date", baseDate).getSingle();
	}

	@Override
	public Optional<CodeName> findWorkplaceId(String companyId, GeneralDate date, String id) {
		return this.queryProxy().query(SEL_FIND_WORKPLACE, BsymtWorkplaceInfo.class).setParameter("cid", companyId)
				.setParameter("wkpid", id).setParameter("baseDate", date)
				.getSingle(w -> new CodeName(w.getWkpcd(), w.getWkpName(), w.getBsymtWorkplaceInfoPK().getWkpid()));
	}

	@Override
	public List<DailyPerformanceEmployeeDto> getListEmployeeWithSid(List<String> sid) {
		List<BsymtEmployeeDataMngInfo> lstEmployee = new ArrayList<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstEmployee.addAll(this.queryProxy().query(SEL_EMPLOYEE_WITH_SID, BsymtEmployeeDataMngInfo.class)
					.setParameter("sids", subList).getList());
		});

		List<String> ids = lstEmployee.stream().map((employee) -> {
			return employee.bsymtEmployeeDataMngInfoPk.pId.trim();
		}).collect(Collectors.toList());

		List<BpsmtPerson> lstPerson = new ArrayList<>();
		CollectionUtil.split(ids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstPerson.addAll(this.queryProxy().query(SEL_PERSON, BpsmtPerson.class).setParameter("lstPersonId", subList)
					.getList());
		});

		return lstEmployee.stream().map((employee) -> {
			for (BpsmtPerson person : lstPerson) {
				if (person.bpsmtPersonPk.pId.equals(employee.bsymtEmployeeDataMngInfoPk.pId)) {
					return new DailyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId,
							employee.employeeCode, person.personName, "", "", "", false);
				}
			}
			return new DailyPerformanceEmployeeDto(employee.bsymtEmployeeDataMngInfoPk.sId, employee.employeeCode, "",
					"", "", "", false);
		}).collect(Collectors.toList());
	}

	@Override
	public Map<String, List<EnumConstant>> findErAlApplicationByCidAndListErrCd(String companyId,
			List<String> errorCode) {
		List<KrcstErAlApplication> entity = this.queryProxy().query(SEL_FIND_ER_AL_APP, KrcstErAlApplication.class)
				.setParameter("cid", companyId).setParameter("errorCd", errorCode).getList();
		Map<String, List<EnumConstant>> result = new HashMap<>();
		if (!entity.isEmpty()) {
			result = entity.stream()
					.collect(
							Collectors
									.groupingBy(x -> x.krcstErAlApplicationPK.errorCd,
											Collectors.mapping(
													x -> new EnumConstant(x.krcstErAlApplicationPK.appTypeCd,
															EnumAdaptor.valueOf(x.krcstErAlApplicationPK.appTypeCd,
																	ApplicationType.class).nameId,
															""),
													Collectors.toList())));
		}

		return result;
	}

	@Override
	public Optional<IdentityProcessUseSetDto> findIdentityProcessUseSet(String comapnyId) {
		return this.queryProxy().find(new KrcmtIdentityProcessPk(comapnyId), KrcmtIdentityProcess.class)
				.map(x -> new IdentityProcessUseSetDto(x.useDailySelfCk == 1 ? true : false,
						x.useMonthSelfCK == 1 ? true : false,
						x.yourselfConfirmError != null ? x.yourselfConfirmError : null));
	}

	@Override
	public Map<String, Boolean> getConfirmDay(String companyId, List<String> sids, DateRange dates) {
		if (!sids.isEmpty()) {
			Map<String, Boolean> result = new HashMap<>();
			CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				result.putAll(this.queryProxy().query(SELECT_CONFIRM_DAY, KrcdtIdentificationStatus.class)
						.setParameter("companyID", companyId).setParameter("sids", subList)
						.setParameter("processingYmds", dates.toListDate())
						.getList(x -> x.krcdtIdentificationStatusPK.employeeId + "|"
								+ x.krcdtIdentificationStatusPK.processingYmd)
						.stream().collect(Collectors.toMap(y -> y, y -> true)));
			});
			return result;
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public Optional<ApprovalUseSettingDto> findApprovalUseSettingDto(String comapnyId) {
		return this.queryProxy().find(new KrcmtApprovalProcessPk(comapnyId), KrcmtApprovalProcess.class)
				.map(x -> new ApprovalUseSettingDto(x.useDailyBossChk == 1 ? true : false,
						x.useMonthBossChk == 1 ? true : false,
						x.supervisorConfirmError != null ? x.supervisorConfirmError : null));
	}

	@Override
	public Map<String, WorkPlaceHistTemp> getWplByListSidAndPeriod(String companyId, List<String> sids,
			GeneralDate date) {
		// Split query.
		if (sids.isEmpty())
			return Collections.emptyMap();
		List<WorkPlaceHistTemp> resultList = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_LISTSID_WPH, WorkPlaceHistTemp.class)
					.setParameter("listSid", subList).setParameter("baseDate", date).getList());
		});
		if (resultList.isEmpty())
			return Collections.emptyMap();
		Map<String, String> wplName = new HashMap<>();
		CollectionUtil.split(resultList, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			wplName.putAll(this.queryProxy().query(SELECT_BY_LISTSID_NAME, Object[].class)
					.setParameter("companyId", companyId)
					.setParameter("wkpids", subList.stream().map(x -> x.getWorkplaceId()).collect(Collectors.toList()))
					.setParameter("baseDate", date).getList().stream()
					.collect(Collectors.toMap(x -> String.valueOf(x[0]), x -> String.valueOf(x[1]))));
		});

		return resultList.stream().collect(Collectors.toMap(WorkPlaceHistTemp::getEmployeeId, x -> {
			x.setName(wplName.get(x.getWorkplaceId()));
			return x;
		}));
	}

	@Override
	public Map<String, List<AffComHistItemAtScreen>> getAffCompanyHistoryOfEmployee(String cid,
			List<String> employeeIds) {
		if (employeeIds.isEmpty())
			return Collections.emptyMap();
		List<AffComHistItemAtScreen> resultList = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_EMPLOYEE_ID_AFF_COM, BsymtAffCompanyHist.class)
					.setParameter("sIds", subList).setParameter("cid", cid)
					.getList(x -> new AffComHistItemAtScreen(x.bsymtAffCompanyHistPk.sId,
							new DatePeriod(x.startDate, x.endDate))));
		});
		return resultList.stream().collect(Collectors.groupingBy(AffComHistItemAtScreen::getEmployeeId,
				Collectors.mapping(x -> x, Collectors.toList())));
	}

	//SELECT_BY_SID_DATE_AF_COM
	@Override
	public List<String> getAffCompanyHistorySidDate(String cid,
			List<String> employeeIds, DateRange range) {
		if (employeeIds.isEmpty())
			return Collections.emptyList();
		List<String> resultList = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_SID_DATE_AF_COM, String.class)
					.setParameter("sIds", subList).setParameter("cid", cid)
					.setParameter("startDate", range.getStartDate())
					.setParameter("endDate", range.getEndDate())
					.getList());
		});
		return new ArrayList<>(resultList.stream().collect(Collectors.toSet()));
	}
	
	@Override
	public List<EnumConstant> findApplicationCall(String companyId) {
		List<KfnmtApplicationCall> entities = this.queryProxy().query(FIND_APPLICATION_CALL, KfnmtApplicationCall.class)
				.setParameter("companyId", companyId).getList();
		if (!entities.isEmpty()) {
			return entities.stream().map(x -> {
				return new EnumConstant(x.kfnmtApplicationCallPK.applicationType,
						EnumAdaptor.valueOf(x.kfnmtApplicationCallPK.applicationType, ApplicationType.class).nameId,
						"");
			}).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String findWorkConditionLastest(List<String> hists, String employeeId) {
		List<KshmtWorkingCond> entitys = this.queryProxy().query(FIND_WORK_CONDITION, KshmtWorkingCond.class)
				.setParameter("historyId", hists).setParameter("sid", employeeId).getList();
		return entitys.isEmpty() ? "" : entitys.get(0).getKshmtWorkingCondItem().getHistoryId();
	}

	@Override
	public List<DateRange> getWorkConditionFlexDatePeriod(String employeeId, DatePeriod date) {
		List<KshmtWorkingCond> ents = this.queryProxy()
				.query(FIND_PERIOD_ORDER_BY_STR_D_FOR_MULTI, KshmtWorkingCond.class).setParameter("endDate", date.end())
				.setParameter("startDate", date.start()).setParameter("employeeId", employeeId).getList();
		return ents.stream()
				.map(x -> new DateRange(x.getStrD().beforeOrEquals(date.start()) ? date.start() : x.getStrD(),
						x.getEndD().beforeOrEquals(date.end()) ? x.getEndD() : date.end()))
				.collect(Collectors.toList());
	}

	@Override
	public Integer getLimitFexMonth(String companyId) {
		Optional<KrcstFlexShortageLimit> ent = this.queryProxy().query(GET_LIMIT_FLEX_MON, KrcstFlexShortageLimit.class)
				.setParameter("companyId", companyId).getSingle();
		return ent.isPresent() ? ent.get().upperLimitTime : 0;
	}

	@Override
	public List<ErrorFlexMonthDto> getErrorFlexMonth(Integer errorType, Integer yearMonth, String employeeId,
			Integer closureId, Integer closeDay, Integer isLastDay) {
		List<ErrorFlexMonthDto> errorFlex = this.queryProxy().query(GET_MONTH_ERROR, KrcdtEmployeeMonthlyPerError.class)
				.setParameter("errorType", errorType).setParameter("yearMonth", yearMonth)
				.setParameter("employeeId", employeeId).setParameter("closureId", closureId)
				.setParameter("closeDay", closeDay).setParameter("isLastDay", isLastDay)
				.getList(x -> new ErrorFlexMonthDto(x.flex, x.annualHoliday, x.yearlyReserved));
		return errorFlex;
	}

	@Override
	public Map<String, String> getAllEmployment(String companyId, List<String> employeeId, DateRange rangeDate) {
		Map<String, String> empCodes = new HashMap<>();
		CollectionUtil.split(employeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {

			try (PreparedStatement statement = this.connection()
					.prepareStatement("select h.SID, i.EMP_CD from BSYMT_EMPLOYMENT_HIST h"
							+ " inner join BSYMT_EMPLOYMENT_HIS_ITEM i on h.HIST_ID = i.HIST_ID" + " where h.CID = ?"
							+ " and h.START_DATE <= ? and h.END_DATE >= ?" + " and h.SID in ("
							+ subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {

				statement.setString(1, companyId);
				statement.setDate(2, Date.valueOf(rangeDate.getEndDate().toLocalDate()));
				statement.setDate(3, Date.valueOf(rangeDate.getStartDate().toLocalDate()));
				for (int i = 0; i < subList.size(); i++) {
					statement.setString(4 + i, subList.get(i));
				}

				new NtsResultSet(statement.executeQuery()).getList(rec -> {
					empCodes.put(rec.getString("SID"), rec.getString("EMP_CD"));
					return null;
				});

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}

		});
		return empCodes;
	}

	@Override
	public List<OptionalItemDto> findByListNos(String companyId, List<Integer> optionalitemNos) {
		// KRCST_OPTIONAL_ITEM
		// KrcstOptionalItem
		List<OptionalItemDto> dtos = new ArrayList<>();
		String textIn = "";
		for (int i = 0; i < optionalitemNos.size(); i++) {
			int number = optionalitemNos.get(i);
			if (i == (optionalitemNos.size() - 1)) {
				textIn += number;
			} else {
				textIn += number + ",";
			}
		}

		Connection con = this.getEntityManager().unwrap(Connection.class);
		String query = "SELECT s.OPTIONAL_ITEM_NO, s.OPTIONAL_ITEM_NAME, s.OPTIONAL_ITEM_ATR FROM KRCST_OPTIONAL_ITEM as s WHERE s.CID = ? AND s.OPTIONAL_ITEM_NO IN ("
				+ textIn + ")";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, companyId);
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				int itemNo = rs.getInt("OPTIONAL_ITEM_NO");
				String itemName = rs.getString("OPTIONAL_ITEM_NAME");
				int itemAtr = rs.getInt("OPTIONAL_ITEM_ATR");
				dtos.add(new OptionalItemDto(itemNo, itemName, OptionalItemAtr.valueOf(itemAtr)));
			}

			return dtos;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dtos;
	}

	@Override
	public void requestForFlush() {
		// this.getEntityManager().flush();
	}

	private String joinParam(List<String> employeeIDLst) {
		return employeeIDLst.stream().map(x -> "?").collect(Collectors.joining(","));
	}

	@Override
	public List<ClosureDto> getAllClosureDto(String companyId, List<String> employeeIds, DateRange dateRange) {
		Map<Pair<String, DatePeriod>, String> empCodes = new HashMap<>();
		MutableValue<Exception> exception = new MutableValue<>(null);
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			if (exception.optional().isPresent()) {
				return;
			}
			String GET_EMP_ALL_X = new StringBuilder(
					"SELECT h.START_DATE, h.END_DATE, e.SID, e.EMP_CD FROM BSYMT_EMPLOYMENT_HIS_ITEM e")
							.append(" JOIN BSYMT_EMPLOYMENT_HIST h ON e.HIST_ID = h.HIST_ID WHERE")
							.append(" h.START_DATE <= ? AND h.END_DATE >= ? AND h.CID = ? AND h.SID IN (")
							.append(joinParam(subList)).append(")").toString();

			try (PreparedStatement statement = this.connection().prepareStatement(GET_EMP_ALL_X)) {
				statement.setDate(1, Date.valueOf(dateRange.getEndDate().localDate()));
				statement.setDate(2, Date.valueOf(dateRange.getStartDate().localDate()));
				statement.setString(3, companyId);
				for (int i = 0; i < subList.size(); i++) {
					statement.setString(i + 4, subList.get(i));
				}

				new NtsResultSet(statement.executeQuery()).getList(rs -> {
					empCodes.put(
							Pair.of(rs.getString("SID"),
									new DatePeriod(rs.getGeneralDate("START_DATE"), rs.getGeneralDate("END_DATE"))),
							rs.getString("EMP_CD"));
					return null;
				});
			} catch (SQLException e) {
				exception.set(e);
			}
		});
		if (exception.optional().isPresent()) {
			throw new RuntimeException(exception.get());
		}
		// get employment codes
		if (empCodes.isEmpty()) {
			return new ArrayList<>();
		}

		List<ClosureDto> closureDtos = new ArrayList<>();
		CollectionUtil.split(empCodes.values().stream().collect(Collectors.toList()),
				DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
					StringBuilder builderString = new StringBuilder();
					builderString.append("SELECT c.CLOSURE_ID , c.USE_ATR, c.CLOSURE_MONTH, emp.EMPLOYMENT_CD ");
					builderString.append("FROM KCLMT_CLOSURE c JOIN ");
					builderString.append(
							"KCLMT_CLOSURE_EMPLOYMENT emp ON c.CID = emp.CID AND c.CLOSURE_ID = emp.CLOSURE_ID");
					builderString.append("WHERE emp.CID = ? AND c.USE_ATR = 1");
					builderString.append("AND emp.EMPLOYMENT_CD IN ( ").append(joinParam(subList)).append(")");

					try (PreparedStatement statement = this.connection().prepareStatement(builderString.toString())) {
						statement.setString(1, companyId);
						for (int i = 0; i < subList.size(); i++) {
							statement.setString(i + 2, subList.get(i));
						}

						new NtsResultSet(statement.executeQuery()).getList(rs -> {
							closureDtos.add(new ClosureDto(companyId, rs.getInt("CLOSURE_ID"), rs.getInt("USE_ATR"),
									rs.getInt("CLOSURE_MONTH"), "", rs.getString("EMPLOYMENT_CD")));
							return null;
						});
					} catch (SQLException e) {
						exception.set(e);
					}
				});
		List<ClosureDto> result = new ArrayList<>();
		empCodes.forEach((key, value) -> {
			Optional<ClosureDto> optional = closureDtos.stream().filter(item -> item.getEmploymentCode().equals(value))
					.findFirst();
			if (optional.isPresent()) {
				ClosureDto closureDto = new ClosureDto();
				closureDto.setSid(key.getLeft());
				DatePeriod dateTemp = key.getRight();
				if (dateRange.getEndDate().afterOrEquals(dateTemp.end())) {
					closureDto.setDatePeriod(dateTemp.newSpan(dateRange.getStartDate(), dateTemp.end()));
				} else {
					closureDto.setDatePeriod(dateTemp.newSpan(dateTemp.start(), dateRange.getEndDate()));
				}

				closureDto.setEmploymentCode(optional.get().getEmploymentCode());
				closureDto.setClosureId(optional.get().getClosureId());
				closureDto.setClosureMonth(optional.get().getClosureMonth());
				closureDto.setCompanyId(optional.get().getCompanyId());
				closureDto.setUseAtr(optional.get().getUseAtr());
				result.add(closureDto);
			}
		});
		return result;
	}

	@Override
	public List<ConfirmationMonthDto> confirmationMonth(String companyId, Map<String, Integer> sidClosureId) {
		// KrcdtConfirmationMonth
		// KRCDT_CONFIRMATION_MONTH
		List<ConfirmationMonthDto> resultFind = new ArrayList<>();
		String textParam = "";
		int count = sidClosureId.size();
		for (Entry<String, Integer> data : sidClosureId.entrySet()) {
			textParam += "(" + "SID = '" + data.getKey() + "' AND CLOSURE_ID = " + data.getValue() + " )";
			count--;
			if (count > 0) {
				textParam += " OR ";
			}
		}

		Connection con = this.getEntityManager().unwrap(Connection.class);
		String query = "SELECT * FROM KRCDT_CONFIRMATION_MONTH as s WHERE s.CID = ? AND (" + textParam + " )";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, companyId);
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				int closureId = rs.getInt("CLOSURE_ID");
				int processYM = rs.getInt("PROCESS_YM");
				String employeeId = rs.getString("SID");
				int closureDay = rs.getInt("CLOSURE_DAY");
				ConfirmationMonthDto month = new ConfirmationMonthDto(companyId, employeeId, closureId, closureDay,
						processYM);
				resultFind.add(month);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultFind;
	}

	@Override
	public List<AttendenceTimeMonthDto> findAttendenceTimeMonth(List<String> sids, DateRange dateRange) {
		return this.queryProxy().query(FIND_BY_PERIOD_INTO_END, KrcdtMonMerge.class).setParameter("employeeIds", sids)
				.setParameter("startDate", dateRange.getStartDate()).setParameter("endDate", dateRange.getEndDate())
				.getList(c -> new AttendenceTimeMonthDto(c.krcdtMonMergePk.getEmployeeId(),
						new YearMonth(c.krcdtMonMergePk.getYearMonth()),
						ClosureId.valueOf(c.krcdtMonMergePk.getClosureId()),
						new ClosureDate(c.krcdtMonMergePk.getClosureDay(), c.krcdtMonMergePk.getIsLastDay() == 1)));
	}

	@Override
	public boolean isErAl(String companyId, List<String> errorCode) {
	List<Integer> results = new ArrayList<>();
	CollectionUtil.split(errorCode, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
		try (PreparedStatement statement = this.connection().prepareStatement(
				"SELECT COUNT(*) FROM KRCMT_ERAL_SET s WHERE s.CID = ? "
					+ " AND (s.ERAL_ATR = 0 OR s.ERAL_ATR = 1) "				
					+ " AND s.ERROR_ALARM_CD IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {
			
			statement.setString(1, companyId);
			for (int i = 0; i < subList.size(); i++) {
				statement.setString(2 + i, subList.get(i));
			}
			ResultSet result = statement.executeQuery();
			while(result.next()){
				if(result.getInt(1) > 0){
					results.add(result.getInt(1));
				}
            }
			
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		
	});
	
		return !results.isEmpty();
	}

}
