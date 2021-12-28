package nts.uk.ctx.at.record.pubimp.dailyprocess.attendancetime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.ProvisionalCalculationService;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.requestlist.PrevisionalForImp;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyAttendanceTimePub;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyAttendanceTimePubExport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyAttendanceTimePubImport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyAttendanceTimePubLateLeaveExport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.importparam.ChildCareTimeZoneImport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.importparam.OutingTimeZoneImport;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class DailyAttendanceTimePubImpl implements DailyAttendanceTimePub{

	@Inject
	private ProvisionalCalculationService provisionalCalculationService;
	/**
	 * RequestList No.23
	 */
	@Override
	public DailyAttendanceTimePubExport calcDailyAttendance(DailyAttendanceTimePubImport imp) {
		val result = calcDailyAttendanceTime(imp);
		
		if(!result.isEmpty()) {
			return isPresentValueForAttendanceTime(result.get(0));
		}
		else {
			return notPresentValueForAttendanceTime();
		}
	}

	@Override
	public IntegrationOfDaily calcOneDayAttendance(DailyAttendanceTimePubImport imp) {
		List<IntegrationOfDaily> result = calcDailyAttendanceTime(imp);
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * RequestList No.13
	 */
	@Override
	public DailyAttendanceTimePubLateLeaveExport calcDailyLateLeave(DailyAttendanceTimePubImport imp) {
		val result = calcDailyAttendanceTime(imp);
		
		if(!result.isEmpty()) {
			return isPresentValueForLateLeave(result.get(0));
		}
		else {
			return new DailyAttendanceTimePubLateLeaveExport(new AttendanceTime(0), new AttendanceTime(0));
		}
	}


	/**
	 * 実績計算
	 * @param imp　import
	 * @return 計算結果
	 */
	private List<IntegrationOfDaily> calcDailyAttendanceTime(DailyAttendanceTimePubImport imp) {

		if(imp.getEmployeeid() == null || imp.getYmd() == null || imp.getTimeZoneMap().isEmpty() || imp.getWorkTypeCode() == null)
			return Collections.emptyList();
		
		//休憩時間帯の作成
		List<BreakTimeSheet> breakTimeSheets = new ArrayList<>();
		List<AttendanceTime> impBreakStart = imp.getBreakStartTime().stream()
				.sorted((f, s) -> f.compareTo(s))
				.collect(Collectors.toList());
		List<AttendanceTime> impBreakEnd = imp.getBreakEndTime().stream()
				.sorted((f, s) -> f.compareTo(s))
				.collect(Collectors.toList());
		
		for(int frameNo = 1 ; frameNo <= impBreakStart.size() ; frameNo++) {
			if(impBreakStart != null && impBreakEnd != null) {
				breakTimeSheets.add(new BreakTimeSheet(new BreakFrameNo(frameNo),
							new TimeWithDayAttr(impBreakStart.get(frameNo - 1).valueAsMinutes()),
							new TimeWithDayAttr(impBreakEnd.get(frameNo - 1).valueAsMinutes()),
							impBreakEnd.get(frameNo - 1).minusMinutes(impBreakStart.get(frameNo - 1).valueAsMinutes())));
			}
		}
		
		//外出時間帯を作成
		List<OutingTimeSheet> outingTimeSheets = new ArrayList<>();
		List<OutingTimeZoneImport> impOutingTimeSheets = imp.getOutingTimeSheets().stream()
				.sorted((f, s) -> f.getTimeZone().getStart().compareTo(s.getTimeZone().getStart()))
				.collect(Collectors.toList());
		
		for(int frameNo = 1; frameNo <= impOutingTimeSheets.size(); frameNo++) {
			outingTimeSheets.add(new OutingTimeSheet(
					new OutingFrameNo(frameNo),
					Optional.of(new WorkStamp(
							new WorkTimeInformation(
									new ReasonTimeChange(TimeChangeMeans.REAL_STAMP, Optional.empty()),
									impOutingTimeSheets.get(frameNo - 1).getTimeZone().getStart()),
							Optional.empty())),
					impOutingTimeSheets.get(frameNo - 1).getGoingOutReason(),
					Optional.of(new WorkStamp(
							new WorkTimeInformation(
									new ReasonTimeChange(TimeChangeMeans.REAL_STAMP, Optional.empty()),
									impOutingTimeSheets.get(frameNo - 1).getTimeZone().getEnd()),
							Optional.empty()))
			));
		}
		
		//短時間勤務時間帯を作成
		List<ShortWorkingTimeSheet> shortWorkingTimeSheets = new ArrayList<>();
		List<ChildCareTimeZoneImport> impShortWorkingTimeSheets = imp.getShortWorkingTimeSheets().stream()
				.sorted((f, s) -> f.getTimeZone().getStart().compareTo(s.getTimeZone().getStart()))
				.collect(Collectors.toList());
		
		for(int frameNo = 1; frameNo <= impShortWorkingTimeSheets.size(); frameNo++) {
			shortWorkingTimeSheets.add(new ShortWorkingTimeSheet(
					new ShortWorkTimFrameNo(frameNo),
					EnumAdaptor.valueOf(impShortWorkingTimeSheets.get(frameNo -1).getGoingOutReason(), ChildCareAtr.class),
					impShortWorkingTimeSheets.get(frameNo - 1).getTimeZone().getStart(),
					impShortWorkingTimeSheets.get(frameNo - 1).getTimeZone().getEnd()));
		}

        return provisionalCalculationService.calculation(Arrays.asList(
                new PrevisionalForImp(
                        imp.getEmployeeid(),
                        imp.getYmd(),
                        imp.getTimeZoneMap(),
                        imp.getWorkTypeCode(),
                        imp.getWorkTimeCode(),
                        breakTimeSheets,
                        outingTimeSheets,
                        shortWorkingTimeSheets
                )
        ));
	}
	
	/**
	 * 日別計算で値が作成できた時のクラス作成 	
	 * @param integrationOfDaily 1日の実績(WORK)
	 * @return RequestList No23 Output class
	 */
	private DailyAttendanceTimePubExport isPresentValueForAttendanceTime(IntegrationOfDaily integrationOfDaily) {
		val overTimeFrames = new HashMap<OverTimeFrameNo,TimeWithCalculation>();
		val holidayWorkFrames = new HashMap<HolidayWorkFrameNo,TimeWithCalculation>();
		val bonusPays = new HashMap<Integer,AttendanceTime>();
		val specBonusPays = new HashMap<Integer,AttendanceTime>();
		TimeWithCalculation flexTime = TimeWithCalculation.sameTime(new AttendanceTime(0));
		TimeWithCalculation excessTimeMidNightTime = TimeWithCalculation.sameTime(new AttendanceTime(0));
		//計算合計外深夜時間
		AttendanceTime timeOutSideMidnight = new AttendanceTime(0);
		//計算残業深夜時間
		AttendanceTime calOvertimeMidnight = new AttendanceTime(0);
		//計算休出深夜時間
		Map<StaturoryAtrOfHolidayWork,AttendanceTime> calHolidayMidnight  = new HashMap<StaturoryAtrOfHolidayWork,AttendanceTime>();
		//遅刻時間1
		AttendanceTime lateTime1 = new AttendanceTime(0);
		//早退時間1
		AttendanceTime earlyLeaveTime1 = new AttendanceTime(0);
		//遅刻時間2
		AttendanceTime lateTime2 = new AttendanceTime(0);
		//早退時間2
		AttendanceTime earlyLeaveTime2 = new AttendanceTime(0);
		//私用外出時間
		AttendanceTime privateOutingTime = new AttendanceTime(0);
		//組合外出時間
		AttendanceTime unionOutingTime = new AttendanceTime(0);
		
		if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily() != null){
				if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()!= null) {
					if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily() != null) {
						if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime() != null) {
							 excessTimeMidNightTime = TimeWithCalculation.convertFromTimeDivergence(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime().getTime());
							 //日別実績の勤怠時間．勤怠時間．勤務時間．総労働時間．所定外時間．所定外深夜時間．時間
							 timeOutSideMidnight = excessTimeMidNightTime.getCalcTime();
						}
						if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
							flexTime = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getNotMinusFlexTime();
							if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getExcessOverTimeWorkMidNightTime().isPresent()) {
								//日別実績の勤怠時間．勤怠時間．勤務時間．総労働時間．所定外時間．残業時間．所定外深夜時間．時間
								calOvertimeMidnight = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getExcessOverTimeWorkMidNightTime().get().getTime().getCalcTime();
							}
						}
						if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()) {
							if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()) {
								if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()) {
									List<HolidayWorkMidNightTime> holidayWorkMidNightTimes = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayMidNightWork().get().getHolidayWorkMidNightTime();
									for(HolidayWorkMidNightTime hw : holidayWorkMidNightTimes) {
										calHolidayMidnight.put(hw.getStatutoryAtr(), hw.getTime().getCalcTime());
									}
									
								}
								
							}
						}
						
					}
					//日別勤怠の勤怠時間．勤務時間．総労働時間．遅刻時間．遅刻時間
					List<LateTimeOfDaily> lateTimeOfDaily = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getLateTimeOfDaily();
					for(LateTimeOfDaily lt : lateTimeOfDaily) {
						if(lt.getWorkNo().v() == 1) {
							lateTime1 = lt.getLateTime().getCalcTime();
						} else if(lt.getWorkNo().v() == 2) {
							lateTime2 = lt.getLateTime().getCalcTime();
						}
					}
					//日別勤怠の勤怠時間．勤務時間．総労働時間．早退時間．早退時間
					List<LeaveEarlyTimeOfDaily> leaveEarlyTimeOfDaily = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getLeaveEarlyTimeOfDaily();
					for(LeaveEarlyTimeOfDaily let : leaveEarlyTimeOfDaily) {
						if(let.getWorkNo().v() == 1) {
							earlyLeaveTime1 = let.getLeaveEarlyTime().getCalcTime();
						}else if(let.getWorkNo().v() == 2) {
							earlyLeaveTime2 = let.getLeaveEarlyTime().getCalcTime();
						}
					}
					
					//日別勤怠の勤怠時間．勤務時間．総労働時間．外出時間
					List<OutingTimeOfDaily> outingTimeOfDailyPer = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getOutingTimeOfDailyPerformance(); 
					for(OutingTimeOfDaily ot : outingTimeOfDailyPer) {
						if(ot.getReason() == GoingOutReason.PRIVATE) {
							privateOutingTime = ot.getRecordTotalTime().getTotalTime().getCalcTime();
						}else if(ot.getReason() == GoingOutReason.UNION) {
							unionOutingTime = ot.getRecordTotalTime().getTotalTime().getCalcTime();
						}
					}
				}
			}
				
			for(int loopNumber = 1 ; loopNumber <=10 ; loopNumber++ ) {
				final int loop = loopNumber;
				//残業
				if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
					val getOver = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime()
									.stream().filter(tc -> tc.getOverWorkFrameNo().v().intValue() == loop).findFirst();
					if(getOver.isPresent()) {
						overTimeFrames.put(new OverTimeFrameNo(loopNumber), TimeWithCalculation.createTimeWithCalculation(getOver.get().getOverTimeWork().getTime().addMinutes(getOver.get().getTransferTime().getTime().valueAsMinutes()),
																														  getOver.get().getOverTimeWork().getCalcTime().addMinutes(getOver.get().getTransferTime().getCalcTime().valueAsMinutes())));
					}
					else {
						overTimeFrames.put(new OverTimeFrameNo(loopNumber),TimeWithCalculation.sameTime(new AttendanceTime(0)));
					}
				}
				//休出
				if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()) {
					val getHol = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().getHolidayWorkFrameTime()
									.stream().filter(tc -> tc.getHolidayFrameNo().v().intValue() == loop).findFirst();
					if(getHol.isPresent()) {
						holidayWorkFrames.put(new HolidayWorkFrameNo(loopNumber), TimeWithCalculation.createTimeWithCalculation(getHol.get().getHolidayWorkTime().get().getTime().addMinutes(getHol.get().getTransferTime().get().getTime().valueAsMinutes()),
																																getHol.get().getHolidayWorkTime().get().getCalcTime().addMinutes(getHol.get().getTransferTime().get().getCalcTime().valueAsMinutes())));
					}
					else {
						holidayWorkFrames.put(new HolidayWorkFrameNo(loopNumber),TimeWithCalculation.sameTime(new AttendanceTime(0)));
					}
				}
				//加給
				if(loopNumber < integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor().getRaisingSalaryTimes().size()) {
					
					bonusPays.put(loopNumber, integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor().getRaisingSalaryTimes().get(loopNumber-1).getBonusPayTime());
				}
				else {
					bonusPays.put(loopNumber, new AttendanceTime(0));
				}
				//特定日加給
				if(loopNumber < integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor().getRaisingSalaryTimes().size()) {
					
					specBonusPays.put(loopNumber, integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor().getRaisingSalaryTimes().get(loopNumber-1).getBonusPayTime());
				}
				else {
					specBonusPays.put(loopNumber, new AttendanceTime(0));
				}

			
			}
		}
		return new DailyAttendanceTimePubExport(overTimeFrames,
				holidayWorkFrames,
				bonusPays,
				specBonusPays,
				flexTime,
				excessTimeMidNightTime,
				timeOutSideMidnight,
				calOvertimeMidnight,
				calHolidayMidnight,
				lateTime1,earlyLeaveTime1,
				lateTime2,earlyLeaveTime2,
				privateOutingTime,unionOutingTime
			);
	}

	/**
	 * 日別計算で値が作成できなかった時のクラス作成
	 * @return　RequestList No23 Output class(all zero)
	 */
	private DailyAttendanceTimePubExport notPresentValueForAttendanceTime() {
		val overTimeFrames = new HashMap<OverTimeFrameNo,TimeWithCalculation>();
		val holidayWorkFrames = new HashMap<HolidayWorkFrameNo,TimeWithCalculation>();
		val bonusPays = new HashMap<Integer,AttendanceTime>();
		val specBonusPays = new HashMap<Integer,AttendanceTime>();
		//計算合計外深夜時間
		AttendanceTime timeOutSideMidnight = new AttendanceTime(0);
		// 計算残業深夜時間
		AttendanceTime calOvertimeMidnight = new AttendanceTime(0);
		// 計算休出深夜時間
		Map<StaturoryAtrOfHolidayWork, AttendanceTime> calHolidayMidnight  = new HashMap<StaturoryAtrOfHolidayWork,AttendanceTime>();
		//遅刻時間1
		AttendanceTime lateTime1 = new AttendanceTime(0);
		//早退時間1
		AttendanceTime earlyLeaveTime1 = new AttendanceTime(0);
		//遅刻時間2
		AttendanceTime lateTime2 = new AttendanceTime(0);
		//早退時間2
		AttendanceTime earlyLeaveTime2 = new AttendanceTime(0);
		//私用外出時間
		AttendanceTime privateOutingTime = new AttendanceTime(0);
		//組合外出時間
		AttendanceTime unionOutingTime = new AttendanceTime(0);
		for(int loopNumber = 1 ; loopNumber <=10 ; loopNumber++ ) {
			//残業
			overTimeFrames.put(new OverTimeFrameNo(loopNumber), TimeWithCalculation.sameTime(new AttendanceTime(0)));
			//休出
			holidayWorkFrames.put(new HolidayWorkFrameNo(loopNumber), TimeWithCalculation.sameTime(new AttendanceTime(0)));
			//加給
			bonusPays.put(loopNumber,new AttendanceTime(0));
			//特定日加給
			specBonusPays.put(loopNumber,new AttendanceTime(0));
		}
		return new DailyAttendanceTimePubExport(overTimeFrames,
												holidayWorkFrames,
												bonusPays,
												specBonusPays,
												TimeWithCalculation.sameTime(new AttendanceTime(0)),
												TimeWithCalculation.sameTime(new AttendanceTime(0)),
												timeOutSideMidnight,
												calOvertimeMidnight,
												calHolidayMidnight,
												lateTime1,earlyLeaveTime1,
												lateTime2,earlyLeaveTime2,
												privateOutingTime,unionOutingTime
											);
	}
	

	/**
	 * 遅刻時間、早退時間計算
	 * @param integrationOfDaily　日別実績(WORK)
	 * @return output class ( No.13)
	 */
	private DailyAttendanceTimePubLateLeaveExport isPresentValueForLateLeave(IntegrationOfDaily integrationOfDaily) {
		int lateTime = 0;
		int leaveEarlyTime = 0;
		if(integrationOfDaily != null
		&& integrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()
		&& integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily() != null
		&& integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime() != null) {
			//遅刻時間
			lateTime = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getLateTimeOfDaily()
						.stream().map(tc -> tc.getLateTime().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(ts -> ts));
			//早退時間
			leaveEarlyTime = integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getLeaveEarlyTimeOfDaily()
								.stream().map(tc -> tc.getLeaveEarlyTime().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(ts -> ts));
		}
		return new DailyAttendanceTimePubLateLeaveExport(new AttendanceTime(lateTime), new AttendanceTime(leaveEarlyTime));
	}
}
