package nts.uk.ctx.at.record.app.find.dailyperform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.OuenWorkTimeSheetOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.affiliationInfor.AffiliationInforOfDailyPerforFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.affiliationInfor.dto.AffiliationInforOfDailyPerforDto;
import nts.uk.ctx.at.record.app.find.dailyperform.attendanceleavinggate.AttendanceLeavingGateOfDailyFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.attendanceleavinggate.dto.AttendanceLeavingGateOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.calculationattribute.CalcAttrOfDailyPerformanceFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.calculationattribute.dto.CalcAttrOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.AttendanceTimeDailyPerformDto;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.erroralarm.EmployeeDailyPerErrorFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.erroralarm.dto.EmployeeDailyPerErrorDto;
import nts.uk.ctx.at.record.app.find.dailyperform.goout.OutingTimeOfDailyPerformanceFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.goout.dto.OutingTimeOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.optionalitem.OptionalItemOfDailyPerformFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.optionalitem.dto.OptionalItemOfDailyPerformDto;
import nts.uk.ctx.at.record.app.find.dailyperform.pclogoninfor.PCLogOnInforOfDailyPerformFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.pclogoninfor.dto.PCLogOnInforOfDailyPerformDto;
import nts.uk.ctx.at.record.app.find.dailyperform.remark.RemarksOfDailyFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.remark.dto.RemarksOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.resttime.BreakTimeDailyFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.resttime.dto.BreakTimeDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.shorttimework.ShortTimeOfDailyFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.shorttimework.dto.ShortTimeOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.snapshot.SnapshotDto;
import nts.uk.ctx.at.record.app.find.dailyperform.snapshot.SnapshotFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.specificdatetttr.SpecificDateAttrOfDailyPerforFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.specificdatetttr.dto.SpecificDateAttrOfDailyPerforDto;
import nts.uk.ctx.at.record.app.find.dailyperform.supporttime.SupportTimeFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.temporarytime.TemporaryTimeOfDailyPerformanceFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.temporarytime.dto.TemporaryTimeOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.WorkInformationOfDailyFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.dto.WorkInformationOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.AttendanceTimeByWorkOfDailyFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.OuenWorkTimeFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.TimeLeavingOfDailyPerformanceFinder;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.AttendanceTimeByWorkOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.OuenWorkTimeOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.TimeLeavingOfDailyPerformanceDto;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.shared.app.util.attendanceitem.FinderFacade;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DailyRecordWorkFinder extends FinderFacade {

	@Inject
	private AttendanceTimeOfDailyPerformFinder attendanceTimeFinder;

	@Inject
	private AffiliationInforOfDailyPerforFinder affiliInfoFinder;

	@Inject
	private AttendanceLeavingGateOfDailyFinder attendanceLeavingGateFinder;

	@Inject
	private OutingTimeOfDailyPerformanceFinder outingTimeFinder;

	@Inject
	private OptionalItemOfDailyPerformFinder optionalItemFinder;

	@Inject
	private PCLogOnInforOfDailyPerformFinder pcLogOnInfoFinder;

	@Inject
	private BreakTimeDailyFinder breakItemFinder;

	@Inject
	private SpecificDateAttrOfDailyPerforFinder specificDateAttrFinder;

	@Inject
	private TemporaryTimeOfDailyPerformanceFinder temporaryTimeFinder;

	@Inject
	private WorkInformationOfDailyFinder workInfoFinder;

	@Inject
	private TimeLeavingOfDailyPerformanceFinder timeLeavingFinder;

	@Inject
	private CalcAttrOfDailyPerformanceFinder calcAttrFinder;

	@Inject
	private ShortTimeOfDailyFinder shortWorkFinder;

	@Inject
	private EditStateOfDailyPerformanceFinder editStateFinder;
	
	@Inject
	private EmployeeDailyPerErrorFinder errorFinder;
	 
	@Inject
	private AttendanceTimeByWorkOfDailyFinder attendanceTimeByWorkFinder;

	@Inject
	private RemarksOfDailyFinder remarkFinder;
	
	@Inject
	private OuenWorkTimeFinder ouenWorkTimeFinder;

	@Inject
	private SnapshotFinder snapshotFinder;

	@Inject
	private WorkInformationRepository workInfoRepo;
	
	@Inject
	private  SupportTimeFinder supportTimeFinder;
	
	@Override
	public FinderFacade getFinder(String layout) {
		switch (layout) {
		case DAILY_WORK_INFO_CODE:
			return this.workInfoFinder;
		case DAILY_CALCULATION_ATTR_CODE:
			return this.calcAttrFinder;
		case DAILY_AFFILIATION_INFO_CODE:
			return this.affiliInfoFinder;
		case DAILY_OUTING_TIME_CODE:
			return this.outingTimeFinder;
		case DAILY_BREAK_TIME_CODE:
			return this.breakItemFinder;
		case DAILY_ATTENDANCE_TIME_CODE:
			return this.attendanceTimeFinder;
		case DAILY_ATTENDANCE_TIME_BY_WORK_CODE:
			return this.attendanceTimeByWorkFinder;
		case DAILY_ATTENDACE_LEAVE_CODE:
			return this.timeLeavingFinder;
		case DAILY_SHORT_TIME_CODE:
			return this.shortWorkFinder;
		case DAILY_SPECIFIC_DATE_ATTR_CODE:
			return this.specificDateAttrFinder;
		case DAILY_ATTENDANCE_LEAVE_GATE_CODE:
			return this.attendanceLeavingGateFinder;
		case DAILY_OPTIONAL_ITEM_CODE:
			return this.optionalItemFinder;
		case DAILY_EDIT_STATE_CODE:
			return this.editStateFinder;
		case DAILY_TEMPORARY_TIME_CODE:
			return this.temporaryTimeFinder;
		case DAILY_PC_LOG_INFO_CODE:
			return this.pcLogOnInfoFinder;
		case DAILY_REMARKS_CODE:
			return this.remarkFinder;
		case DAILY_SUPPORT_TIME_CODE:
			return this.ouenWorkTimeFinder;
		case DAILY_SNAPSHOT_CODE:
			return this.snapshotFinder;
		case DAILY_SUPPORT_TIMESHEET_CODE:
			return this.supportTimeFinder;	
		default:
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DailyRecordDto find(String employeeId, GeneralDate baseDate) {
		Optional<WorkInfoOfDailyPerformance> workInfo = this.workInfoRepo.find(employeeId, baseDate);
		if (!workInfo.isPresent())
			return null;
		
		return DailyRecordDto.builder().employeeId(employeeId).workingDate(baseDate)
				.withWorkInfo(WorkInformationOfDailyDto.getDto(workInfo.get()))
				.withCalcAttr(calcAttrFinder.find(employeeId, baseDate))
				.withAffiliationInfo(affiliInfoFinder.find(employeeId, baseDate))
				.withErrors(errorFinder.finds(employeeId, baseDate))
				.outingTime(outingTimeFinder.find(employeeId, baseDate))
				.breakTime(breakItemFinder.find(employeeId, baseDate))
				.attendanceTime(attendanceTimeFinder.find(employeeId, baseDate))
				.attendanceTimeByWork(attendanceTimeByWorkFinder.find(employeeId, baseDate))
				.timeLeaving(timeLeavingFinder.find(employeeId, baseDate))
				.shortWorkTime(shortWorkFinder.find(employeeId, baseDate))
				.specificDateAttr(specificDateAttrFinder.find(employeeId, baseDate))
				.attendanceLeavingGate(attendanceLeavingGateFinder.find(employeeId, baseDate))
				.optionalItems(optionalItemFinder.find(employeeId, baseDate))
				.addEditStates(editStateFinder.finds(employeeId, baseDate))
				.temporaryTime(temporaryTimeFinder.find(employeeId, baseDate))
				.pcLogInfo(pcLogOnInfoFinder.find(employeeId, baseDate))
				.remarks(remarkFinder.find(employeeId, baseDate))
				.withOuenWorkTime(ouenWorkTimeFinder.find(employeeId, baseDate))
				.withSnapshot(snapshotFinder.find(employeeId, baseDate))
				.withOuenSheet(supportTimeFinder.find(employeeId, baseDate))
				.complete();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConvertibleAttendanceItem> List<T> find(List<String> employeeId, DatePeriod baseDate) {
		long startTime = System.currentTimeMillis();
		Map<String, Map<GeneralDate, WorkInformationOfDailyDto>> workInfos = toMap(
				workInfoFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, CalcAttrOfDailyPerformanceDto>> calcAttrs = toMap(
				calcAttrFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, AffiliationInforOfDailyPerforDto>> affiliInfo = toMap(
				affiliInfoFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, SnapshotDto>> snapshots = toMap(
				snapshotFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, List<EmployeeDailyPerErrorDto>>> errors = toMapList(
				errorFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, OutingTimeOfDailyPerformanceDto>> outings = toMap(
				outingTimeFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, BreakTimeDailyDto>> breaks = toMap(
				breakItemFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, AttendanceTimeDailyPerformDto>> attendTime = toMap(
				attendanceTimeFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, AttendanceTimeByWorkOfDailyDto>> attendTimeByWork = toMap(
				attendanceTimeByWorkFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, TimeLeavingOfDailyPerformanceDto>> leaving = toMap(
				timeLeavingFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, ShortTimeOfDailyDto>> shortWork = toMap(
				shortWorkFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, SpecificDateAttrOfDailyPerforDto>> specificDateAttr = toMap(
				specificDateAttrFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, AttendanceLeavingGateOfDailyDto>> attendLeavingGate = toMap(
				attendanceLeavingGateFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, OptionalItemOfDailyPerformDto>> optionalItems = toMap(
				optionalItemFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, List<EditStateOfDailyPerformanceDto>>> editStates = toMapList(
				editStateFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, TemporaryTimeOfDailyPerformanceDto>> temporaryTime = toMap(
				temporaryTimeFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, PCLogOnInforOfDailyPerformDto>> pcLogInfo = toMap(
				pcLogOnInfoFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, RemarksOfDailyDto>> remarks = toMap(
				remarkFinder.find(employeeId, baseDate));
		Map<String, Map<GeneralDate, OuenWorkTimeSheetOfDailyDto>> supportTimes = toMap(
				supportTimeFinder.find(employeeId, baseDate));
		
		Map<String, Map<GeneralDate, OuenWorkTimeOfDailyDto>> ouenWorkTime = toMap(
				ouenWorkTimeFinder.find(employeeId, baseDate));
        System.out.print("thoi gian lay data DB: " +(System.currentTimeMillis() - startTime));
		return (List<T>) employeeId.stream().map(em -> {
			List<DailyRecordDto> dtoByDates = new ArrayList<>();
			GeneralDate start = baseDate.start();
			while (start.beforeOrEquals(baseDate.end())) {
				WorkInformationOfDailyDto workInfo = getValue(workInfos.get(em), start);
				if (workInfo != null && workInfo.isHaveData()) {
					DailyRecordDto current = DailyRecordDto.builder().employeeId(em).workingDate(start)
							.withWorkInfo(workInfo).withCalcAttr(getValue(calcAttrs.get(em), start))
							.withAffiliationInfo(getValue(affiliInfo.get(em), start))
							.withSnapshot(getValue(snapshots.get(em), start))
							.withErrors(getListValue(errors.get(em), start))
							.outingTime(getValue(outings.get(em), start))
							.breakTime(getValue(breaks.get(em), start))
							.attendanceTime(getValue(attendTime.get(em), start))
							.attendanceTimeByWork(getValue(attendTimeByWork.get(em), start))
							.timeLeaving(getValue(leaving.get(em), start))
							.shortWorkTime(getValue(shortWork.get(em), start))
							.specificDateAttr(getValue(specificDateAttr.get(em), start))
							.attendanceLeavingGate(getValue(attendLeavingGate.get(em), start))
							.optionalItems(getValue(optionalItems.get(em), start))
							.addEditStates(getListValue(editStates.get(em), start))
							.temporaryTime(getValue(temporaryTime.get(em), start))
							.pcLogInfo(getValue(pcLogInfo.get(em), start))
							.remarks(getValue(remarks.get(em), start))
							.withOuenSheet(getValue(supportTimes.get(em), start))
							.withOuenWorkTime(getValue(ouenWorkTime.get(em), start))
							.complete();
					dtoByDates.add(current);
				}
				start = start.addDays(1);
			}
			return dtoByDates;
		}).flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ConvertibleAttendanceItem> List<T> find(Map<String, List<GeneralDate>> param) {
		long startTime = System.currentTimeMillis();
		Map<String, Map<GeneralDate, WorkInformationOfDailyDto>> workInfos = toMap(workInfoFinder.find(param));
		Map<String, Map<GeneralDate, CalcAttrOfDailyPerformanceDto>> calcAttrs = toMap(calcAttrFinder.find(param));
		Map<String, Map<GeneralDate, AffiliationInforOfDailyPerforDto>> affiliInfo = toMap(affiliInfoFinder.find(param));
		Map<String, Map<GeneralDate, SnapshotDto>> snapshots = toMap(snapshotFinder.find(param));
		Map<String, Map<GeneralDate, List<EmployeeDailyPerErrorDto>>> errors = toMapList(errorFinder.find(param));
		Map<String, Map<GeneralDate, OutingTimeOfDailyPerformanceDto>> outings = toMap(outingTimeFinder.find(param));
		Map<String, Map<GeneralDate, BreakTimeDailyDto>> breaks = toMap(breakItemFinder.find(param));
		Map<String, Map<GeneralDate, AttendanceTimeDailyPerformDto>> attendTime = toMap(attendanceTimeFinder.find(param));
		Map<String, Map<GeneralDate, AttendanceTimeByWorkOfDailyDto>> attendTimeByWork = toMap(attendanceTimeByWorkFinder.find(param));
		Map<String, Map<GeneralDate, TimeLeavingOfDailyPerformanceDto>> leaving = toMap(timeLeavingFinder.find(param));
		Map<String, Map<GeneralDate, ShortTimeOfDailyDto>> shortWork = toMap(shortWorkFinder.find(param));
		Map<String, Map<GeneralDate, SpecificDateAttrOfDailyPerforDto>> specificDateAttr = toMap(specificDateAttrFinder.find(param));
		Map<String, Map<GeneralDate, AttendanceLeavingGateOfDailyDto>> attendLeavingGate = toMap(attendanceLeavingGateFinder.find(param));
		Map<String, Map<GeneralDate, OptionalItemOfDailyPerformDto>> optionalItems = toMap(optionalItemFinder.find(param));
		Map<String, Map<GeneralDate, List<EditStateOfDailyPerformanceDto>>> editStates = toMapList(editStateFinder.find(param));
		Map<String, Map<GeneralDate, TemporaryTimeOfDailyPerformanceDto>> temporaryTime = toMap(temporaryTimeFinder.find(param));
		Map<String, Map<GeneralDate, PCLogOnInforOfDailyPerformDto>> pcLogInfo = toMap(pcLogOnInfoFinder.find(param));
		Map<String, Map<GeneralDate, RemarksOfDailyDto>> remarks = toMap(remarkFinder.find(param));
		Map<String, Map<GeneralDate, OuenWorkTimeSheetOfDailyDto>> supportTimes = toMap(supportTimeFinder.find(param));
		Map<String, Map<GeneralDate, OuenWorkTimeOfDailyDto>> ouenWorkTime = toMap(ouenWorkTimeFinder.find(param));

		System.out.print("thoi gian lay data DB: " +(System.currentTimeMillis() - startTime));

		return (List<T>) param.entrySet().stream().map(p -> {
			return (List<DailyRecordDto>) p.getValue().stream().map(d -> {
				return DailyRecordDto.builder().employeeId(p.getKey()).workingDate(d)
						.withWorkInfo(getValue(workInfos.get(p.getKey()), d))
						.withCalcAttr(getValue(calcAttrs.get(p.getKey()), d))
						.withAffiliationInfo(getValue(affiliInfo.get(p.getKey()), d))
						.withSnapshot(getValue(snapshots.get(p.getKey()), d))
						.withErrors(getListValue(errors.get(p.getKey()), d))
						.outingTime(getValue(outings.get(p.getKey()), d))
						.breakTime(getValue(breaks.get(p.getKey()), d))
						.attendanceTime(getValue(attendTime.get(p.getKey()), d))
						.attendanceTimeByWork(getValue(attendTimeByWork.get(p.getKey()), d))
						.timeLeaving(getValue(leaving.get(p.getKey()), d))
						.shortWorkTime(getValue(shortWork.get(p.getKey()), d))
						.specificDateAttr(getValue(specificDateAttr.get(p.getKey()), d))
						.attendanceLeavingGate(getValue(attendLeavingGate.get(p.getKey()), d))
						.optionalItems(getValue(optionalItems.get(p.getKey()), d))
						.addEditStates(getListValue(editStates.get(p.getKey()), d))
						.temporaryTime(getValue(temporaryTime.get(p.getKey()), d))
						.pcLogInfo(getValue(pcLogInfo.get(p.getKey()), d))
						.remarks(getValue(remarks.get(p.getKey()), d))
						.withOuenSheet(getValue(supportTimes.get(p.getKey()), d))
						.withOuenWorkTime(getValue(ouenWorkTime.get(p.getKey()), d))
						.complete();
			}).collect(Collectors.toList());
		}).flatMap(List::stream).filter(x -> x.getWorkInfo() != null).collect(Collectors.toList());
	}

	private <T extends ConvertibleAttendanceItem> T getValue(Map<GeneralDate, T> data, GeneralDate date) {
		return data == null ? null : data.get(date);
	}

	private <T extends ConvertibleAttendanceItem> List<T> getListValue(Map<GeneralDate, List<T>> data,
			GeneralDate date) {
		return data == null ? new ArrayList<>() : data.get(date) == null ? new ArrayList<>() : data.get(date);
	}

	private <T extends ConvertibleAttendanceItem> Map<String, Map<GeneralDate, T>> toMap(List<T> dtos) {
		return dtos.stream()
				.collect(Collectors.groupingBy(c -> c.employeeId(), Collectors.toMap(x -> x.workingDate(), x -> x)));
	}

	private <T extends ConvertibleAttendanceItem> Map<String, Map<GeneralDate, List<T>>> toMapList(List<T> dtos) {
		return dtos.stream()
				.collect(Collectors.groupingBy(c -> c.employeeId(), Collectors.groupingBy(x -> x.workingDate())));
	}
}
