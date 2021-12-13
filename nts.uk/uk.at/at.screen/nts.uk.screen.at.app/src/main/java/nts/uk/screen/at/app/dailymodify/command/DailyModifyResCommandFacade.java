package nts.uk.screen.at.app.dailymodify.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.service.TimeOffRemainErrorInfor;
import nts.uk.ctx.at.record.dom.service.TimeOffRemainErrorInputParam;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ErrorType;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyQuery;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.month.LeaveDayErrorDto;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
/** 日別修正CommandFacade */
public class DailyModifyResCommandFacade {

	@Inject
	private TimeOffRemainErrorInfor timeOffRemainErrorInfor;

	@Inject
	private WorkTypeRepository workTypeRepository;

	/** 並列処理用 */
	@Resource
	private ManagedExecutorService executerService;
	
	@Inject
	private RecordDomRequireService requireService;

	public LeaveDayErrorDto mapDomainMonthChange(List<Pair<String, GeneralDate>> employeeChange,
			List<IntegrationOfDaily> domainDailyNew, List<IntegrationOfMonthly> domainMonthNew,
			List<DailyRecordDto> dailyDtoEditAll, DateRange dateRange, List<DPItemValue> lstItemEdits) {
		Set<String> employeeIds = employeeChange.stream().map(x -> x.getLeft()).collect(Collectors.toSet());
		String companyId = AppContexts.user().companyId();
		List<EmployeeMonthlyPerError> monthPer = new ArrayList<>();
		Set<Pair<String, GeneralDate>> detailEmployeeError = new HashSet<>();
		boolean onlyErrorOld = false;
		val cacheCarrier = new CacheCarrier();
		val require = requireService.createRequire();
		for (String emp : employeeIds) {
			// employeeIds.stream().forEach(emp -> {
			List<IntegrationOfDaily> domainDailyEditAll = dailyDtoEditAll.stream()
					.filter(x -> x.getEmployeeId().equals(emp)).map(x -> x.toDomain(x.employeeId(), x.getDate()))
					.collect(Collectors.toList());
			domainDailyEditAll = unionDomain(domainDailyEditAll, domainDailyNew);
			// Acquire closing date corresponding to employee
			List<IntegrationOfDaily> dailyOfEmp = domainDailyEditAll.stream()
					.filter(x -> x.getEmployeeId().equals(emp)).collect(Collectors.toList());

			Optional<GeneralDate> date = GetClosureStartForEmployee.algorithm(
					require, cacheCarrier, emp);
			List<EmployeeMonthlyPerError> lstEmpMonthError = new ArrayList<>();
			if (domainMonthNew != null && !domainMonthNew.isEmpty()) {
				for (IntegrationOfMonthly month : domainMonthNew) {
					TimeOffRemainErrorInputParam param = new TimeOffRemainErrorInputParam(companyId, emp,
							new DatePeriod(date.get(), date.get().addYears(1).addDays(-1)),
							new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), false,
							dailyOfEmp, month.getAttendanceTime());
					// monthPer.addAll(timeOffRemainErrorInfor.getErrorInfor(param));
					lstEmpMonthError.addAll(timeOffRemainErrorInfor.getErrorInfor(param));
				}
				;
			} else {
				Optional<AttendanceTimeOfMonthly> optMonthlyData = (domainMonthNew == null || domainMonthNew.isEmpty())
						? Optional.empty()
						: domainMonthNew.get(0).getAttendanceTime();
				TimeOffRemainErrorInputParam param = new TimeOffRemainErrorInputParam(companyId, emp,
						new DatePeriod(date.get(), date.get().addYears(1).addDays(-1)),
						new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), false,dailyOfEmp, 
						optMonthlyData);
				lstEmpMonthError.addAll(timeOffRemainErrorInfor.getErrorInfor(param));
				// monthPer.addAll(timeOffRemainErrorInfor.getErrorInfor(param));
			}

			// 勤務種類が変更されているかチェックする
//			val itemEdit28s = lstItemEdits.stream().filter(it -> it.getEmployeeId().equals(emp) && it.getItemId() == 28)
//					.map(it -> it.getValue()).collect(Collectors.toList());
//			val lstWTClassification = new HashSet<>();
//			if (!itemEdit28s.isEmpty()) {
//				List<WorkType> lstWType = workTypeRepository.getPossibleWorkType(companyId, itemEdit28s);
//				for (WorkType wt : lstWType) {
//					// lstWType.stream().forEach(wt -> {
//					val wtTemp = checkInGroupWorkPer(wt);
//					if (wtTemp != null) {
//						lstWTClassification.add(convertError(wtTemp));
//						onlyErrorOld = false;
//						val itemRow = lstItemEdits.stream()
//								.filter(it -> it.getEmployeeId().equals(emp) && it.getItemId() == 28
//										&& it.getValue().equals(wt.getWorkTypeCode().v()))
//								.map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toSet());
//						detailEmployeeError.addAll(itemRow);
//					}
//					// });
//				}
//
//			}
//			
			
			// boolean hasErrorInDB = !lstEmpMonthError.stream().filter(x ->
			// x.getErrorType()).collect(Collectors.toList()).isEmpty();
//            lstEmpMonthError = lstWTClassification.isEmpty() ? lstEmpMonthError
//                    : lstEmpMonthError.stream()
//                            .filter(lstErrorTemp -> lstWTClassification.contains(lstErrorTemp.getErrorType())
//                                    || lstErrorTemp.getErrorType() == ErrorType.CHILDCARE_HOLIDAY
//                                    || lstErrorTemp.getErrorType() == ErrorType.CARE_HOLIDAY)
//                            .collect(Collectors.toList());
			

			monthPer.addAll(lstEmpMonthError);
			// });
		}

        if (!monthPer.isEmpty()) {
            employeeChange.forEach(data -> {
                val itemRow = lstItemEdits.stream()
                        .filter(it -> it.getEmployeeId().equals(data.getKey()) && it.getDate().equals(data.getValue()))
                        .map(x -> Pair.of(x.getEmployeeId(), x.getDate())).collect(Collectors.toSet());
                Set<String> listEmpError = monthPer.stream().map(x->x.getEmployeeID()).collect(Collectors.toSet());
                itemRow.forEach(i ->{
                	if(listEmpError.contains(i.getKey())) {
                		detailEmployeeError.add(i);
                	}
                });
		});
        }
		return new LeaveDayErrorDto(onlyErrorOld, monthPer, detailEmployeeError);
	}

	private WorkTypeClassification checkInGroupWorkPer(WorkType wt) {
		if (wt.getDailyWork() == null)
			return null;

		WorkTypeUnit unit = wt.getDailyWork().getWorkTypeUnit();
		if (unit == WorkTypeUnit.OneDay) {
			val oneDay = wt.getDailyWork().getOneDay();
			if (oneDay == WorkTypeClassification.AnnualHoliday || oneDay == WorkTypeClassification.SpecialHoliday
					|| oneDay == WorkTypeClassification.SubstituteHoliday || oneDay == WorkTypeClassification.Pause || oneDay == WorkTypeClassification.YearlyReserved)
				return oneDay;
			// AnnualHoliday , SpecialHoliday, SubstituteHoliday, Pause
		} else {
			val morDay = wt.getDailyWork().getMorning();
			val aftDay = wt.getDailyWork().getAfternoon();
			if (morDay == WorkTypeClassification.AnnualHoliday || morDay == WorkTypeClassification.SpecialHoliday
					|| morDay == WorkTypeClassification.SubstituteHoliday || morDay == WorkTypeClassification.Pause || morDay == WorkTypeClassification.YearlyReserved)
				return morDay;

			if (aftDay == WorkTypeClassification.AnnualHoliday || aftDay == WorkTypeClassification.SpecialHoliday
					|| aftDay == WorkTypeClassification.SubstituteHoliday || aftDay == WorkTypeClassification.Pause || aftDay == WorkTypeClassification.YearlyReserved)
				return aftDay;
		}
		return null;
	}

	private ErrorType convertError(WorkTypeClassification wtc) {
		switch (wtc) {
		case AnnualHoliday:
			return ErrorType.YEARLY_HOLIDAY;

		case SpecialHoliday:
			return ErrorType.SPECIAL_REMAIN_HOLIDAY_NUMBER;

		case SubstituteHoliday:
			return ErrorType.REMAINING_ALTERNATION_NUMBER;

		case Pause:
			return ErrorType.REMAIN_LEFT;
			
		case YearlyReserved:
		    return ErrorType.NUMBER_OF_MISSED_PIT;

		default:
			return ErrorType.YEARLY_HOLIDAY;
		}
	}

	private List<IntegrationOfDaily> unionDomain(List<IntegrationOfDaily> parent, List<IntegrationOfDaily> child) {
		val date = child.stream().collect(Collectors.toMap(x -> x.getYmd(), x -> "", (x, y) -> x));
		val resultFilter = parent.stream().filter(x -> !date.containsKey(x.getYmd()))
				.collect(Collectors.toList());
		resultFilter.addAll(child);
		return resultFilter;
	}

	public void createStampSourceInfo(DailyRecordDto dtoEdit, List<DailyModifyQuery> querys) {
		val sidLogin = AppContexts.user().employeeId();
		boolean editBySelf = sidLogin.equals(dtoEdit.getEmployeeId());
		Integer stampSource = editBySelf ? TimeChangeMeans.HAND_CORRECTION_PERSON.value
				: TimeChangeMeans.HAND_CORRECTION_OTHERS.value;
		List<ItemValue> itemValueTempDay = querys.stream().filter(
				x -> x.getEmployeeId().equals(dtoEdit.getEmployeeId()) && x.getBaseDate().equals(dtoEdit.getDate()))
				.flatMap(x -> x.getItemValues().stream()).collect(Collectors.toList());
		List<ItemValue> itemValue = itemValueTempDay.stream()
				.filter(x -> DPText.ITEM_INSERT_STAMP_SOURCE.contains(x.itemId())).collect(Collectors.toList());
		itemValue.stream().forEach(x -> {
			try {
				switch (x.itemId()) {
				case 75:
				case 79:
				case 73:
					dtoEdit.getAttendanceLeavingGate().get().getAttendanceLeavingGateTime()
							.get(Math.abs(75 - x.itemId()) / 4).getStart().setStampSourceInfo(stampSource);
					break;
				case 77:
				case 81:
				case 85:
					dtoEdit.getAttendanceLeavingGate().get().getAttendanceLeavingGateTime()
							.get(Math.abs(77 - x.itemId()) / 4).getEnd()
							.setStampSourceInfo(TimeChangeMeans.HAND_CORRECTION_OTHERS.value);
					break;
				case 31:
				case 41:
					if (x.itemId() == 31 && dtoEdit.getTimeLeaving().get().getWorkAndLeave().get(0).getWorking()
							.getTime().getStampSourceInfo() != TimeChangeMeans.SPR_COOPERATION.value) {
						dtoEdit.getTimeLeaving().get().getWorkAndLeave().get(Math.abs(31 - x.itemId()) / 10)
								.getWorking().getTime().setStampSourceInfo(stampSource);
					} else if (x.itemId() == 41) {
						dtoEdit.getTimeLeaving().get().getWorkAndLeave().get(1).getWorking().getTime()
								.setStampSourceInfo(stampSource);
					}
					break;
				case 34:
				case 44:
					if (x.itemId() == 34
							&& dtoEdit.getTimeLeaving().get().getWorkAndLeave().get(Math.abs(34 - x.itemId()) / 10)
									.getLeave().getTime().getStampSourceInfo() != TimeChangeMeans.SPR_COOPERATION.value) {
						dtoEdit.getTimeLeaving().get().getWorkAndLeave().get(0).getLeave().getTime()
								.setStampSourceInfo(stampSource);
					} else if (x.itemId() == 44) {
						dtoEdit.getTimeLeaving().get().getWorkAndLeave().get(1).getLeave().getTime()
								.setStampSourceInfo(stampSource);
					}
					break;
				case 51:
				case 59:
				case 67:
					dtoEdit.getTemporaryTime().get().getWorkLeaveTime().get(Math.abs(51 - x.itemId()) / 8)
							.getWorking().getTime().setStampSourceInfo(stampSource);
					break;
				case 53:
				case 61:
				case 69:
					dtoEdit.getTemporaryTime().get().getWorkLeaveTime().get(Math.abs(53 - x.itemId()) / 8).getLeave()
							.getTime().setStampSourceInfo(stampSource);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				System.out.print("Lỗi map createStampSourceInfo itemId: " + x.itemId());
			}
		});
	}
}
