package nts.uk.ctx.at.schedule.pubimp.schedule.workschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.pub.schedule.basicschedule.ScWorkScheduleExport_New;
import nts.uk.ctx.at.schedule.pub.schedule.basicschedule.ShortWorkingTimeSheetExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.ActualWorkingTimeOfDailyExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.AttendanceTimeOfDailyAttendanceExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.BreakTimeOfDailyAttdExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.BreakTimeSheetExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.NumberOfDaySuspensionExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.ReasonTimeChangeExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.TimeActualStampExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.TimeLeavingOfDailyAttdExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.TimeLeavingWorkExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.TotalWorkingTimeExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkScheduleBasicInforExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkScheduleConfirmExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkScheduleConfirmExport.SCConfirmedAtrExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkScheduleExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkSchedulePub;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkStampExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkTimeInformationExport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author tutk
 *
 */
@Stateless
public class WorkSchedulePubImpl implements WorkSchedulePub {

	@Inject
	private WorkScheduleRepository workScheduleRepository;

	@Override
	public Optional<WorkScheduleExport> get(String employeeID, GeneralDate ymd) {
		Optional<WorkSchedule> data = workScheduleRepository.get(employeeID, ymd);
		if (data.isPresent()) {
			return Optional.of(convertToWorkSchedule(data.get()));
		}
		return Optional.empty();
	}

	@Override
	public List<WorkScheduleExport> getList(List<String> sids, DatePeriod period) {

		List<WorkSchedule> data = workScheduleRepository.getList(sids, period);
		return data.stream().map(this::convertToWorkSchedule).collect(Collectors.toList());

	}

	private WorkScheduleExport convertToWorkSchedule(WorkSchedule data) {
		
		TimeLeavingOfDailyAttdExport timeLeavingOfDailyAttd = null;
		if (data.getOptTimeLeaving().isPresent()) {
			List<TimeLeavingWorkExport> timeLeavingWorks = data.getOptTimeLeaving().get().getTimeLeavingWorks().stream()
					.map(c -> convertToTimeLeavingWork(c)).collect(Collectors.toList());
			timeLeavingOfDailyAttd = new TimeLeavingOfDailyAttdExport(timeLeavingWorks,
					data.getOptTimeLeaving().get().getWorkTimes() ==null?0:data.getOptTimeLeaving().get().getWorkTimes().v());
		}
		BreakTimeOfDailyAttdExport listBreakTimeOfDaily = new BreakTimeOfDailyAttdExport(
							data.getLstBreakTime().getBreakTimeSheets().stream().map(x -> 
									new BreakTimeSheetExport(x.getBreakFrameNo().v(), 
															x.getStartTime().v(), 
															x.getEndTime().v(), 
															x.getBreakTime().v()))
								.collect(Collectors.toList()));

		WorkScheduleExport workScheduleExport = new WorkScheduleExport(
				data.getEmployeeID(),
				data.getConfirmedATR().value,
				data.getWorkInfo().getRecordInfo().getWorkTypeCode().v(),
				data.getWorkInfo().getRecordInfo().getWorkTimeCode() == null ? null
						: data.getWorkInfo().getRecordInfo().getWorkTimeCode().v(),
				data.getWorkInfo().getGoStraightAtr().value, data.getWorkInfo().getBackStraightAtr().value,
				timeLeavingOfDailyAttd,Optional.of(listBreakTimeOfDaily),
				data.getWorkInfo().getNumberDaySuspension().isPresent()
						? Optional.of(new NumberOfDaySuspensionExport(
								data.getWorkInfo().getNumberDaySuspension().get().getDays().v(),
								data.getWorkInfo().getNumberDaySuspension().get().getClassifiction().value))
						: Optional.empty()
				);
		
		workScheduleExport.setYmd(data.getYmd());
		if (data.getOptAttendanceTime() != null && data.getOptAttendanceTime().isPresent()) {
			ActualWorkingTimeOfDailyExport actualWorkingTimeOfDaily = ActualWorkingTimeOfDailyExport.builder()
					.totalWorkingTime(TotalWorkingTimeExport.builder()
							.actualTime(data.getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime().v())
							.workTimes(data.getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getWorkTimes().v())
							.build())
					.build();
			AttendanceTimeOfDailyAttendanceExport attendanceExport = AttendanceTimeOfDailyAttendanceExport.builder()
					.actualWorkingTimeOfDaily(actualWorkingTimeOfDaily)
					.build();
			workScheduleExport.setOptAttendanceTime(Optional.ofNullable(attendanceExport));
		}
		
		return workScheduleExport;

	}

	private TimeLeavingWorkExport convertToTimeLeavingWork(TimeLeavingWork domain) {
		return new TimeLeavingWorkExport(domain.getWorkNo().v(),
				!domain.getAttendanceStamp().isPresent() ? null
						: convertToTimeActualStamp(domain.getAttendanceStamp().get()),
				!domain.getLeaveStamp().isPresent() ? null : convertToTimeActualStamp(domain.getLeaveStamp().get()));
	}

	private TimeActualStampExport convertToTimeActualStamp(TimeActualStamp domain) {
		return new TimeActualStampExport(
				domain.getActualStamp().isPresent() ? convertToWorkStamp(domain.getActualStamp().get()) : null,
				domain.getStamp().isPresent() ? convertToWorkStamp(domain.getStamp().get()) : null,
				domain.getNumberOfReflectionStamp());
	}

	private WorkStampExport convertToWorkStamp(WorkStamp domain) {
		return new WorkStampExport( new WorkTimeInformationExport(
				new ReasonTimeChangeExport(domain.getTimeDay().getReasonTimeChange().getTimeChangeMeans().value,
						domain.getTimeDay().getReasonTimeChange().getEngravingMethod().isPresent()
								? domain.getTimeDay().getReasonTimeChange().getEngravingMethod().get().value
								: null),
				domain.getTimeDay().getTimeWithDay().isPresent() ? domain.getTimeDay().getTimeWithDay().get().v()
						: null),
				domain.getLocationCode().isPresent() ? domain.getLocationCode().get().v() : null);
	}

	@Override
	public List<WorkScheduleBasicInforExport> get(List<String> lstSid, DatePeriod ymdPeriod) {
		List<WorkSchedule> getList = workScheduleRepository.getList(lstSid, ymdPeriod);
		List<WorkScheduleBasicInforExport> lstResult = getList.stream()
				.map(x -> new WorkScheduleBasicInforExport(x.getEmployeeID(),
						x.getYmd(),
						x.getWorkInfo().getRecordInfo().getWorkTypeCode().v(),
						x.getWorkInfo().getRecordInfo().getWorkTimeCodeNotNull().isPresent() ? 
								Optional.ofNullable(x.getWorkInfo().getRecordInfo().getWorkTimeCodeNotNull().get().v()): Optional.empty()))
				.collect(Collectors.toList());
		return lstResult;
	}

	@Override
	public Optional<String> getWorkTypeCode(String sid, GeneralDate baseDate) {
		return Optional.ofNullable(workScheduleRepository.get(sid, baseDate).map(i -> i.getWorkInfo().getRecordInfo().getWorkTypeCode().v()).orElse(null));
	}

	@Override
	public Optional<GeneralDate> acquireMaxDateBasicSchedule(List<String> sIds) {
		return this.workScheduleRepository.getMaxDateWorkSche(sIds);
	}
	
	@Override
    public Optional<ScWorkScheduleExport_New> findByIdNewV2(String employeeId, GeneralDate baseDate) {
        ScWorkScheduleExport_New result = new ScWorkScheduleExport_New();
//      get 勤務予定
        Optional<WorkSchedule> workSchedule =  workScheduleRepository.get(employeeId, baseDate);
        workSchedule.ifPresent(x -> {
            result.setEmployeeId(x.getEmployeeID());
            result.setDate(x.getYmd());
            result.setWorkTypeCode(x.getWorkInfo().getRecordInfo().getWorkTypeCode().v());
            result.setWorkTimeCode(Optional.ofNullable(x.getWorkInfo().getRecordInfo().getWorkTimeCodeNotNull().map(y -> y.v()).orElse(null)));
            x.getOptTimeLeaving().ifPresent(a -> {
                
                if (!CollectionUtil.isEmpty(a.getTimeLeavingWorks())) {
                    a.getTimeLeavingWorks()
                        .stream()
                        .forEach(b -> {
                            Optional<TimeWithDayAttr> start = b.getAttendanceStamp().map(g -> g.getStamp().map(h -> h.getTimeDay().getTimeWithDay()).orElse(Optional.empty())).orElse(Optional.empty());
                            
                            Optional<TimeWithDayAttr> end = b.getLeaveStamp().map(g -> g.getStamp().map(h -> h.getTimeDay().getTimeWithDay()).orElse(Optional.empty())).orElse(Optional.empty());
                            if (b.getWorkNo().v() == 1) {
                                result.setScheduleStartClock1(start);
                                result.setScheduleEndClock1(end);
                            } else if (b.getWorkNo().v() == 2) {
                                result.setScheduleStartClock2(start);
                                result.setScheduleEndClock2(end);
                            }
                        });
                }
            });
            
            
            
            result.setChildTime(0);
            x.getOptSortTimeWork().ifPresent(y -> {
                List<ShortWorkingTimeSheet> shortWorkingTimeSheet = y.getShortWorkingTimeSheets();
                List<ShortWorkingTimeSheetExport> listExport = shortWorkingTimeSheet.stream().map(a ->{
                    return new ShortWorkingTimeSheetExport(
                            a.getShortWorkTimeFrameNo().v(),
                            a.getChildCareAttr().value,
                            a.getStartTime().v(),
                            a.getEndTime().v());
                }).collect(Collectors.toList());
                result.setListShortWorkingTimeSheetExport(listExport);                          
            });     
            
            List<BreakTimeSheet> breakTimeSheets = x.getLstBreakTime().getBreakTimeSheets();
            List<BreakTimeSheetExport> breakTimeSheetExports = breakTimeSheets.stream().map(y -> {
                return new BreakTimeSheetExport(
                        y.getBreakFrameNo().v(), 
                        y.getStartTime() != null ? y.getStartTime().v() : 0, 
                        y.getEndTime() != null ? y.getEndTime().v() : 0, 
                        y.getBreakTime() != null ? y.getBreakTime().v() : 0);
            }).collect(Collectors.toList());
            result.setListBreakTimeSheetExports(breakTimeSheetExports);
        });
        

        
        return workSchedule.isPresent() ? Optional.of(result) : Optional.empty();
    }

	@Override
	public List<WorkScheduleConfirmExport> findConfirmById(List<String> employeeID, DatePeriod date) {
		List<WorkSchedule> workSchedules = workScheduleRepository.getList(employeeID, date);
		Map<Pair<String, GeneralDate>, WorkSchedule> mapData = workSchedules.stream()
				.collect(Collectors.toMap(x -> Pair.of(x.getEmployeeID(), x.getYmd()), x -> x));

		List<WorkScheduleConfirmExport> result = new ArrayList<>();
		employeeID.stream().forEach(x -> {
			date.datesBetween().forEach(dateB -> {
				WorkSchedule data = mapData.get(Pair.of(x, dateB));
				result.add(data == null ? new WorkScheduleConfirmExport(x, dateB, SCConfirmedAtrExport.UNSETTLED)
						: new WorkScheduleConfirmExport(x, dateB,
								EnumAdaptor.valueOf(data.getConfirmedATR().value, SCConfirmedAtrExport.class)));
			});
		});
		return result;
	}
}
