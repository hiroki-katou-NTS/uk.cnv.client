package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskScheduleDetail;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeGoOutTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.interval.IntervalTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.RaiseSalaryTimeOfDailyPerfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.temporarytime.TemporaryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AbsenceOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AnnualOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.OverSalaryOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SpecialHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SubstituteHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TimeDigestOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TransferHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.YearlyReservedOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.IntervalExemptionTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.WithinOutingTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.AttendanceClock;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * ??????????????????????????? UKDesign.??????????????????.ER???.??????.????????????.????????????.????????????
 * 
 * @author HieuLt
 *
 */
@Entity
@NoArgsConstructor
@Table(name = "KSCDT_SCH_TIME")
@Getter
public class KscdtSchTime extends ContractUkJpaEntity {

	@EmbeddedId
	public KscdtSchTimePK pk;
	/** ??????ID **/
	@Column(name = "CID")
	public String cid;

	/** ???????????? **/
	@Column(name = "COUNT")
	public int count;

	/** ??????????????? **/
	@Column(name = "TOTAL_TIME")
	public int totalTime;

	/** ???????????? **/
	@Column(name = "TOTAL_TIME_ACT")
	public int totalTimeAct;

	/** ????????? ???????????? **/
	@Column(name = "PRS_WORK_TIME")
	public int prsWorkTime;
	
	/** ????????? ???????????? **/
	@Column(name = "PRS_WORK_TIME_ACT")
	public int prsWorkTimeAct;

	/** ????????? ???????????? **/
	@Column(name = "PRS_PRIME_TIME")
	public int prsPrimeTime;

	/** ????????? ???????????? **/
	@Column(name = "PRS_MIDNITE_TIME")
	public int prsMidniteTime;

	/** ????????? ?????????????????? **/
	@Column(name = "EXT_BIND_TIME_OTW")
	public int extBindTimeOtw;

	/** ????????? ?????????????????? **/
	@Column(name = "EXT_BIND_TIME_HDW")
	public int extBindTimeHw;

	/** ????????? ???????????? ????????????????????? **/
	@Column(name = "EXT_VARWK_OTW_TIME_LEGAL")
	public int extVarwkOtwTimeLegal;

	/** ????????? ????????????????????? **/
	@Column(name = "EXT_FLEX_TIME")
	public int extFlexTime;

	/** ????????? ????????????????????? ?????????????????? **/
	@Column(name = "EXT_FLEX_TIME_PREAPP")
	public int extFlexTimePreApp;

	/** ??????????????? ???????????? **/
	@Column(name = "EXT_MIDNITE_OTW_TIME")
	public int extMidNiteOtwTime;

	/** ??????????????? ???????????? ??????????????? **/
	@Column(name = "EXT_MIDNITE_HDW_TIME_LGHD")
	public int extMidNiteHdwTimeLghd;

	/** ??????????????? ???????????? ??????????????? **/
	@Column(name = "EXT_MIDNITE_HDW_TIME_ILGHD")
	public int extMidNiteHdwTimeIlghd;

	/** ??????????????? ???????????? ?????? **/
	@Column(name = "EXT_MIDNITE_HDW_TIME_PUBHD")
	public int extMidNiteHdwTimePubhd;

	/** ??????????????? ???????????? **/
	@Column(name = "EXT_MIDNITE_TOTAL")
	public int extMidNiteTotal;

	/** ??????????????? ???????????? ?????????????????? **/
	@Column(name = "EXT_MIDNITE_TOTAL_PREAPP")
	public int extMidNiteTotalPreApp;
	
	/** ?????????????????????????????? **/
	@Column(name = "INTERVAL_ATD_CLOCK")
	public int intervalAtdClock;

	/** ???????????????????????? **/
	@Column(name = "INTERVAL_TIME")
	public int intervalTime;

	/** ?????????????????? **/
	@Column(name = "BRK_TOTAL_TIME")
	public int brkTotalTime;

	/** ?????????????????? ?????? **/
	@Column(name = "USE_DAYLY_HD_PAID")
	public int hdPaidTime;

	/** ?????????????????????????????? ?????? **/
	@Column(name = "USE_HOURLY_HD_PAID")
	public int hdPaidHourlyTime;

	/** ?????????????????? ?????? **/
	@Column(name = "USE_DAYLY_HD_COM")
	public int hdComTime;

	/** ?????????????????????????????? ?????? **/
	@Column(name = "USE_HOURLY_HD_COM")
	public int hdComHourlyTime;

	/** ?????????????????? ???????????? **/
	@Column(name = "USE_DAYLY_HD_60H")
	public int hd60hTime;

	/** ?????????????????????????????? ????????????**/
	@Column(name = "USE_HOURLY_HD_60H")
	public int hd60hHourlyTime;

	/** ?????????????????? ???????????? **/
	@Column(name = "USE_DAYLY_HD_SP")
	public int hdspTime;

	/** ?????????????????????????????? ????????????**/
	@Column(name = "USE_HOURLY_HD_SP")
	public int hdspHourlyTime;

	/** ?????????????????? ???????????? **/
	@Column(name = "USE_DAYLY_HD_STK")
	public int hdstkTime;

	/** ?????????????????? ???????????? **/
	@Column(name = "HOURLY_HD_USETIME")
	public int hdHourlyTime;

	/** ?????????????????? ???????????? **/
	@Column(name = "HOURLY_HD_SHORTAGETIME")
	public int hdHourlyShortageTime;

	/** ???????????? **/
	@Column(name = "ABSENCE_TIME")
	public int absenceTime;

	/** ?????????????????? **/
	@Column(name = "VACATION_ADD_TIME")
	public int vacationAddTime;

	/** ?????????????????? **/
	@Column(name = "STAGGERED_WH_TIME")
	public int staggeredWhTime;
	
	//ver 5
	/**????????? ?????????????????? **/
	@Column(name = "PRS_WORK_TIME_AMOUNT")
	public int prsWorkTimeAmount;
	
	/** ?????? ??????????????????**/
	@Column(name = "PREMIUM_WORK_TIME_TOTAL")
	public int premiumWorkTimeTotal;
	
	/** ?????? ???????????? **/
	@Column(name = "PREMIUM_AMOUNT_TOTAL")
	public int premiumAmountTotal;
	//end ver 5
	
	//ver 4
	/** ?????????????????? ??????**/
	@Column(name = "USE_DAILY_HD_SUB")
	public int useDailyHDSub;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "SID", referencedColumnName = "SID", insertable = false, updatable = false),
			@JoinColumn(name = "YMD", referencedColumnName = "YMD", insertable = false, updatable = false) })
	public KscdtSchBasicInfo basicInfo;

	@OneToMany(targetEntity = KscdtSchOvertimeWork.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_OVERTIME_WORK")
	public List<KscdtSchOvertimeWork> overtimeWorks;

	@OneToMany(targetEntity = KscdtSchHolidayWork.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_HOLIDAY_WORK")
	public List<KscdtSchHolidayWork> holidayWorks;

	@OneToMany(targetEntity = KscdtSchBonusPay.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_BONUSPAY")
	public List<KscdtSchBonusPay> bonusPays;

	@OneToMany(targetEntity = KscdtSchPremium.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_PREMIUM")
	public List<KscdtSchPremium> premiums;

	@OneToMany(targetEntity = KscdtSchShortTime.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_SHORTTIME")
	public List<KscdtSchShortTime> shortTimes;

	@OneToMany(targetEntity = KscdtSchComeLate.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_COME_LATE")
	public List<KscdtSchComeLate> kscdtSchComeLate;

	@OneToMany(targetEntity = KscdtSchGoingOut.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_GOING_OUT")
	public List<KscdtSchGoingOut> kscdtSchGoingOut;

	@OneToMany(targetEntity = KscdtSchLeaveEarly.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_LEAVE_EARLY")
	public List<KscdtSchLeaveEarly> kscdtSchLeaveEarly;

	@OneToMany(targetEntity = KscdtSchTask.class, mappedBy = "kscdtSchTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_TASK")
	public List<KscdtSchTask> kscdtSchTask;

	/**
	 * 
	 * @param workingTime ??????????????????????????????????????????
	 * @param sID
	 * @param yMD
	 * @param cID
	 * @return
	 */
	public static KscdtSchTime toEntity(WorkSchedule workSchedule, String cID) {
		GeneralDate yMD = workSchedule.getYmd();
		String sID = workSchedule.getEmployeeID();
		KscdtSchTimePK pk = new KscdtSchTimePK(sID, yMD);
		TaskSchedule task = workSchedule.getTaskSchedule();
		ActualWorkingTimeOfDaily timeOfDailys = workSchedule.getOptAttendanceTime().get().getActualWorkingTimeOfDaily();
		// ????????????????????????????????????????????????????????????
		TotalWorkingTime workingTime = timeOfDailys.getTotalWorkingTime();

		WithinStatutoryTimeOfDaily timeOfDaily = workingTime.getWithinStatutoryTimeOfDaily();

		// ??????????????????????????????????????????????????????????????????????????????
		ExcessOfStatutoryTimeOfDaily statutoryTimeOfDaily = workingTime.getExcessOfStatutoryTimeOfDaily();

		// ?????????????????????????????????????????????????????????????????????????????????????????????
		Optional<OverTimeOfDaily> overTimeOfDaily = statutoryTimeOfDaily.getOverTimeWork();

		// ??????????????????????????????????????????????????????????????????????????????????????????????????????
		ExcessOfStatutoryMidNightTime excessOfStatutoryMidNightTime = statutoryTimeOfDaily
				.getExcessOfStatutoryMidNightTime();

		// ????????????????????????????????????????????????????????????.????????????
		HolidayOfDaily holidayOfDaily = workingTime.getHolidayOfDaily();
		
		// create KscdtSchOvertimeWork
		// ??????????????????????????????????????????.???????????????.???????????????.???????????????
		List<KscdtSchOvertimeWork> kscdtSchOvertimeWork = overTimeOfDaily.get().getOverTimeWorkFrameTime().stream()
				.map(mapper -> KscdtSchOvertimeWork.toEntity(mapper, cID, sID, yMD)).collect(Collectors.toList());

		// create KscdtSchHolidayWork
		// ?????????????????????????????????????????????????????????????????????????????????????????????
		HolidayWorkTimeOfDaily workTimeOfDaily = statutoryTimeOfDaily.getWorkHolidayTime().get();
		List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet = workTimeOfDaily.getHolidayWorkFrameTimeSheet();
		List<HolidayWorkFrameTime> holidayWorkFrameTime = workTimeOfDaily.getHolidayWorkFrameTime();
		List<KscdtSchHolidayWork> kscdtSchHolidayWork = new ArrayList<>();

		if (holidayWorkFrameTimeSheet.size() != holidayWorkFrameTime.size()) {
			if (holidayWorkFrameTime.size() > 0 && holidayWorkFrameTimeSheet.size() > 0) {
				for (HolidayWorkFrameTime x : holidayWorkFrameTime) {
					KscdtSchHolidayWork work = holidayWorkFrameTimeSheet.stream()
							.map(y -> KscdtSchHolidayWork.toEntity(x, y, sID, yMD, cID)).findFirst().get();
					kscdtSchHolidayWork.add(work);
				}
			}
		} else {
			if (holidayWorkFrameTime.size() > 0 && holidayWorkFrameTimeSheet.size() > 0) {
				for (HolidayWorkFrameTimeSheet x : holidayWorkFrameTimeSheet) {
					KscdtSchHolidayWork work = holidayWorkFrameTime.stream()
							.map(y -> KscdtSchHolidayWork.toEntity2(x, y, sID, yMD, cID)).findFirst().get();
					kscdtSchHolidayWork.add(work);
				}
			}
		}
		// create KscdtSchBonusPay
		// ??????????????????????????????????????????????????????????????????????????????????????????
		List<BonusPayTime> dailyPerfor = workingTime.getRaiseSalaryTimeOfDailyPerfor().getRaisingSalaryTimes();
		List<KscdtSchBonusPay> kscdtSchBonusPay = dailyPerfor.stream()
				.map(mapper -> KscdtSchBonusPay.toEntity(sID, yMD, mapper)).collect(Collectors.toList());

		// create KscdtSchPremium
		// ????????????????????????????????????????????????????????????????????????
		List<PremiumTime> premiumTimes = timeOfDailys.getPremiumTimeOfDailyPerformance().getPremiumTimes();
		List<KscdtSchPremium> kscdtSchPremium = premiumTimes.stream()
				.map(mapper -> KscdtSchPremium.toEntity(mapper, sID, yMD)).collect(Collectors.toList());

		// ????????????????????????????????????????????????????????????????????????????????????
		ShortWorkTimeOfDaily result = workingTime.getShotrTimeOfDaily();
		List<KscdtSchShortTime> kscdtSchShortTime = new ArrayList<>();
		kscdtSchShortTime.add(KscdtSchShortTime.toEntity(result, sID, yMD));

		// QA 110844
		Integer timeLghd = 0;
		Integer timeIlghd = 0;
		Integer timePubhd = 0;
		if (statutoryTimeOfDaily.getWorkHolidayTime().isPresent()) {
			if (statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()) {
				if (!statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
						.getHolidayWorkMidNightTime().isEmpty()) {
					if (statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
							.getHolidayWorkMidNightTime().get(0).getStatutoryAtr() != null) {
						String check = statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
								.getHolidayWorkMidNightTime().get(0).getStatutoryAtr().name();

						if (check.equals(StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork.name())
								&& statutoryTimeOfDaily.getWorkHolidayTime().isPresent()
								&& statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()
								&& !statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
										.getHolidayWorkMidNightTime().isEmpty()) {
							timeLghd = statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
									.getHolidayWorkMidNightTime().get(0).getTime().getTime().v();
						}

						if (check.equals(StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork.name())
								&& statutoryTimeOfDaily.getWorkHolidayTime().isPresent()
								&& statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()
								&& !statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
										.getHolidayWorkMidNightTime().isEmpty()) {
							timeIlghd = statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
									.getHolidayWorkMidNightTime().get(0).getTime().getTime().v();
						}

						if (check.equals(StaturoryAtrOfHolidayWork.PublicHolidayWork.name())
								&& statutoryTimeOfDaily.getWorkHolidayTime().isPresent()
								&& statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()
								&& !statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
										.getHolidayWorkMidNightTime().isEmpty()) {
							timePubhd = statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayMidNightWork().get()
									.getHolidayWorkMidNightTime().get(0).getTime().getTime().v();
						}
					}
				}
			}
		}
		// #114431
		List<KscdtSchComeLate> listKscdtSchComeLate = timeOfDailys.getTotalWorkingTime().getLateTimeOfDaily().stream()
				.map(c -> KscdtSchComeLate.toEntity(sID, yMD, cID, c.getWorkNo().v(), c.getTimePaidUseTime()))
				.collect(Collectors.toList());

		List<KscdtSchGoingOut> listKscdtSchGoingOut = timeOfDailys.getTotalWorkingTime()
				.getOutingTimeOfDailyPerformance().stream()
				.map(c -> KscdtSchGoingOut.toEntity(sID, yMD, cID, c.getReason().value, c.getTimeVacationUseOfDaily()))
				.collect(Collectors.toList());

		List<KscdtSchLeaveEarly> listKscdtSchLeaveEarly = timeOfDailys.getTotalWorkingTime().getLeaveEarlyTimeOfDaily()
				.stream()
				.map(c -> KscdtSchLeaveEarly.toEntity(sID, yMD, cID, c.getWorkNo().v(), c.getTimePaidUseTime()))
				.collect(Collectors.toList());
		AtomicInteger index = new AtomicInteger(1);
		List<KscdtSchTask> lstKscdtSchTask = task.getDetails().stream()
				.map(c -> KscdtSchTask.toEntity(sID, yMD, cID, c, index.getAndIncrement())).collect(Collectors.toList());

		KscdtSchTime kscdtSchTime = new KscdtSchTime(pk, cID, // cid
				workingTime.getWorkTimes() == null ? 0 : workingTime.getWorkTimes().v(), // count
				workingTime.getWorkTimes() == null ? 0 : workingTime.getTotalTime().v(), // totalTime
				workingTime.getWorkTimes() == null ? 0 : workingTime.getActualTime().v(), // totalTimeAct
				timeOfDaily.getWorkTime() == null ? 0 : timeOfDaily.getWorkTime().v(), // prsWorkTime
				timeOfDaily.getActualWorkTime() == null ? 0 : timeOfDaily.getActualWorkTime().v(), // prsWorkTimeAct
				timeOfDaily.getWithinPrescribedPremiumTime() == null ? 0
						: timeOfDaily.getWithinPrescribedPremiumTime().v(), // prsPrimeTime
				timeOfDaily.getWithinPrescribedPremiumTime() == null ? 0
						: timeOfDaily.getWithinStatutoryMidNightTime().getTime().getTime().v(), // prsMidniteTime
				overTimeOfDaily.isPresent() ? overTimeOfDaily.get().getOverTimeWorkSpentAtWork().v() : 0, // extBindTimeOtw
				statutoryTimeOfDaily.getWorkHolidayTime().isPresent()
						? statutoryTimeOfDaily.getWorkHolidayTime().get().getHolidayTimeSpentAtWork().v()
						: 0, // extBindTimeHw
				!overTimeOfDaily.isPresent() ? 0 : overTimeOfDaily.get().getIrregularWithinPrescribedOverTimeWork().v(), // extVarwkOtwTimeLegal
				!overTimeOfDaily.isPresent() ? 0 : overTimeOfDaily.get().getFlexTime().getFlexTime().getTime().v(), // extFlexTime
				!overTimeOfDaily.isPresent() ? 0 : overTimeOfDaily.get().getFlexTime().getBeforeApplicationTime().v(), // extFlexTimePreApp
				!overTimeOfDaily.isPresent() ? 0
						: overTimeOfDaily.get().getExcessOverTimeWorkMidNightTime().get().getTime().getTime().v(), // EXT_MIDNITE_OTW_TIME
				timeLghd, // extMidNiteHdwTimeLghd
				timeIlghd, // extMidNiteHdwTimeIlghd
				timePubhd, // EXT_MIDNITE_HDW_TIME_PUBHD // extMidNiteHdwTimePubhd
				excessOfStatutoryMidNightTime.getTime().getTime().v(), // EXT_MIDNITE_TOTAL // extMidNiteTotal
				excessOfStatutoryMidNightTime.getBeforeApplicationTime().v(), // EXT_MIDNITE_TOTAL_PREAPP - 31
																				// //extMidNiteTotalPreApp
				0, // ??ang QA 110822 // intervalAtdClock
				0, // ??ang QA 110822 // intervalTime
				workingTime.getBreakTimeOfDaily() != null && workingTime.getBreakTimeOfDaily().getToRecordTotalTime() != null 
				&& workingTime.getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime() != null ?
						workingTime.getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime().v() : 0, // 34
																													// //
																													// brkTotalTime

				//workingTime.getBreakTimeOfDaily() == null ? 0 : workingTime.getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime().v(), // 34 // brkTotalTime
				holidayOfDaily.getAnnual() == null ? 0 : holidayOfDaily.getAnnual().getUseTime().v(), // 35 USE_DAYLY_HD_PAID // hdPaidTime
				holidayOfDaily.getAnnual() == null ? 0 : holidayOfDaily.getAnnual().getDigestionUseTime().v(), // 36 USE_HOURLY_HD_PAID // hdPaidHourlyTime
				holidayOfDaily.getSubstitute() == null ? 0 : holidayOfDaily.getSubstitute().getUseTime().v(), // hdComTime
				holidayOfDaily.getSubstitute() == null ? 0 : holidayOfDaily.getSubstitute().getDigestionUseTime().v(), // 38 USE_HOURLY_HD_COM // hdComHourlyTime
				holidayOfDaily.getOverSalary() == null ? 0 : holidayOfDaily.getOverSalary().getUseTime().v(), // hd60hTime
				holidayOfDaily.getOverSalary() == null ? 0 : holidayOfDaily.getOverSalary().getDigestionUseTime().v(), // 40 USE_HOURLY_HD_60H // hd60hHourlyTime
				holidayOfDaily.getSpecialHoliday() == null ? 0 : holidayOfDaily.getSpecialHoliday().getUseTime().v(), // hdspTime
				holidayOfDaily.getSpecialHoliday() == null ? 0 : holidayOfDaily.getSpecialHoliday().getDigestionUseTime().v(), // 42 USE_HOURLY_HD_SP // hdspHourlyTime
				holidayOfDaily.getYearlyReserved() == null ? 0 : holidayOfDaily.getYearlyReserved().getUseTime().v(), // hdstkTime
				holidayOfDaily.getTimeDigest() == null ? 0 : holidayOfDaily.getTimeDigest().getUseTime().v(), // hdHourlyTime
				holidayOfDaily.getTimeDigest() == null ? 0 : holidayOfDaily.getTimeDigest().getLeakageTime().v(), // hdHourlyShortageTime
				holidayOfDaily.getAbsence() == null ? 0 : holidayOfDaily.getAbsence().getUseTime().v(), // absenceTime
				workingTime.getVacationAddTime() == null ? 0 : workingTime.getVacationAddTime().v(), // vacationAddTime
				timeOfDailys.getTimeDifferenceWorkingHours() == null ? 0
						: timeOfDailys.getTimeDifferenceWorkingHours().v(), // staggeredWhTime
				kscdtSchOvertimeWork, kscdtSchHolidayWork, kscdtSchBonusPay, kscdtSchPremium, kscdtSchShortTime,
				listKscdtSchComeLate,listKscdtSchGoingOut,listKscdtSchLeaveEarly,lstKscdtSchTask,
				//ver5
				timeOfDaily.getWithinWorkTimeAmount() == null ? 0 : timeOfDaily.getWithinWorkTimeAmount().v(), // prsWorkTimeAmount TODO :X??c nh???n l???i
				timeOfDailys.getPremiumTimeOfDailyPerformance().getTotalWorkingTime() == null?0:timeOfDailys.getPremiumTimeOfDailyPerformance().getTotalWorkingTime().v(),//premiumWorkTimeTotal
				timeOfDailys.getPremiumTimeOfDailyPerformance().getTotalAmount() == null?0:timeOfDailys.getPremiumTimeOfDailyPerformance().getTotalAmount().v(),//premiumAmountTotal
				//ver4
				holidayOfDaily.getTransferHoliday().getUseTime() == null ? 0 :holidayOfDaily.getTransferHoliday().getUseTime().v()//useDailyHDSub 
				);
		return kscdtSchTime;

	}

	public WorkSchedule toDomain(String sID, GeneralDate yMD) {
		// ??????????????????
		AttendanceTime constraintDiffTime = new AttendanceTime(0);
		// ????????????
		ConstraintTime constraintTime = new ConstraintTime(new AttendanceTime(0), new AttendanceTime(0));
		// ??????????????????
		AttendanceTime timeDiff = new AttendanceTime(staggeredWhTime);
		// ???????????????
		KscdtSchOvertimeWork kscdtSchOvertimeWork = new KscdtSchOvertimeWork();
		KscdtSchHolidayWork kscdtSchHolidayWork = new KscdtSchHolidayWork();

		ExcessOfStatutoryMidNightTime nightTime = new ExcessOfStatutoryMidNightTime(
				new TimeDivergenceWithCalculation(new AttendanceTime(this.extMidNiteTotal), new AttendanceTime(0),
						new AttendanceTimeOfExistMinus(0)),
				new AttendanceTime(this.extMidNiteTotalPreApp));

		ExcessOverTimeWorkMidNightTime midNightTimes = new ExcessOverTimeWorkMidNightTime(
				new TimeDivergenceWithCalculation(new AttendanceTime(extMidNiteTotalPreApp), new AttendanceTime(0),
						new AttendanceTimeOfExistMinus(0)));
		OverTimeOfDaily overTimeOfDaily = new OverTimeOfDaily(new ArrayList<>(), new ArrayList<>(),
				Finally.of(midNightTimes), new AttendanceTime(extVarwkOtwTimeLegal),
				new FlexTime(
						TimeDivergenceWithCalculationMinusExist.sameTime(new AttendanceTimeOfExistMinus(extFlexTime)),
						new AttendanceTime(extFlexTimePreApp)),
				new AttendanceTime(extBindTimeOtw));

		ExcessOfStatutoryTimeOfDaily excessOfStatutoryTimeOfDaily = new ExcessOfStatutoryTimeOfDaily(nightTime,
				Optional.ofNullable(kscdtSchOvertimeWork.toDomain(overTimeOfDaily, overtimeWorks)),
				Optional.ofNullable(kscdtSchHolidayWork.toDomain(this.extBindTimeHw, this.extMidNiteHdwTimeLghd,
						this.extMidNiteHdwTimeIlghd, this.extMidNiteHdwTimePubhd, holidayWorks)));
		// raiseSalaryTimeOfDailyPerfor
		KscdtSchBonusPay kscdtSchBonusPay = new KscdtSchBonusPay();
		RaiseSalaryTimeOfDailyPerfor raiseSalaryTimeOfDailyPerfor = new RaiseSalaryTimeOfDailyPerfor(
				kscdtSchBonusPay.toDomain(bonusPays), new ArrayList<>());

		KscdtSchShortTime kscdtSchShortTime = new KscdtSchShortTime();
		ShortWorkTimeOfDaily shotrTime = kscdtSchShortTime.toDomain(sID, yMD, shortTimes);
		// WithinStatutoryMidNightTime
		WithinStatutoryMidNightTime midNightTime = new WithinStatutoryMidNightTime(new TimeDivergenceWithCalculation(
				new AttendanceTime(prsMidniteTime), new AttendanceTime(0), new AttendanceTimeOfExistMinus(0)));
		WithinStatutoryTimeOfDaily withinStatutoryTimeOfDaily = new WithinStatutoryTimeOfDaily(
				new AttendanceTime(this.prsWorkTime), new AttendanceTime(this.prsWorkTimeAct),
				new AttendanceTime(this.prsPrimeTime), midNightTime);
		//ver5
		withinStatutoryTimeOfDaily.setWithinWorkTimeAmount(new AttendanceAmountDaily(this.prsWorkTimeAmount));

		SubstituteHolidayOfDaily substitute = new SubstituteHolidayOfDaily(new AttendanceTime(hdComTime),
				new AttendanceTime(hdComHourlyTime));
		OverSalaryOfDaily overSalary = new OverSalaryOfDaily(new AttendanceTime(hd60hTime),
				new AttendanceTime(hd60hHourlyTime));
		SpecialHolidayOfDaily specialHoliday = new SpecialHolidayOfDaily(new AttendanceTime(hdspTime),
				new AttendanceTime(hdspHourlyTime));
		YearlyReservedOfDaily yearlyReserved = new YearlyReservedOfDaily(new AttendanceTime(hdstkTime));
		TimeDigestOfDaily timeDigest = new TimeDigestOfDaily(new AttendanceTime(hdHourlyTime),
				new AttendanceTime(hdHourlyShortageTime));
		AnnualOfDaily annual = new AnnualOfDaily(new AttendanceTime(hdPaidTime), new AttendanceTime(hdPaidHourlyTime));
		HolidayOfDaily holidayOfDaily = new HolidayOfDaily(new AbsenceOfDaily(new AttendanceTime(absenceTime)),
				timeDigest, yearlyReserved, substitute, overSalary, specialHoliday, annual, new TransferHolidayOfDaily(new AttendanceTime(this.useDailyHDSub))); //ver4

		DeductionTotalTime toRecordTotalTime = DeductionTotalTime
				.of(TimeWithCalculation.sameTime(new AttendanceTime(this.brkTotalTime)), TimeWithCalculation.sameTime(AttendanceTime.ZERO), TimeWithCalculation.sameTime(AttendanceTime.ZERO));
		BreakTimeOfDaily breakTimeOfDaily = new BreakTimeOfDaily(toRecordTotalTime, null, new BreakTimeGoOutTimes(0), new AttendanceTime(0), new ArrayList<>());

		TemporaryTimeOfDaily temporaryTime = new TemporaryTimeOfDaily(new ArrayList<>());
		IntervalTimeOfDaily intervalTime = IntervalTimeOfDaily.of(new AttendanceClock(0), new AttendanceTime(0));

		// #114431
		List<LateTimeOfDaily> lateTimeOfDaily = new ArrayList<>();
		for (KscdtSchComeLate scl : kscdtSchComeLate) {
			LateTimeOfDaily temp = new LateTimeOfDaily(TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value
																											// default
					TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
					new WorkNo(scl.pk.workNo), scl.toDomain(), IntervalExemptionTime.defaultValue());// value default
			lateTimeOfDaily.add(temp);
		}

		List<OutingTimeOfDaily> lateOutingTimeOfDaily = new ArrayList<>();
		for (KscdtSchGoingOut sgo : kscdtSchGoingOut) {
			OutingTimeOfDaily temp = new OutingTimeOfDaily(new BreakTimeGoOutTimes(0), // value default
					GoingOutReason.valueOf(sgo.pk.reasonAtr), sgo.toDomain(),
					OutingTotalTime.of(TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
							WithinOutingTotalTime.of(TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
									TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
									TimeWithCalculation.sameTime(AttendanceTime.ZERO)), // value default
							TimeWithCalculation.sameTime(AttendanceTime.ZERO)), // value default
					OutingTotalTime.of(TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
							WithinOutingTotalTime.of(TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
									TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
									TimeWithCalculation.sameTime(AttendanceTime.ZERO)), // value default
							TimeWithCalculation.sameTime(AttendanceTime.ZERO)), // value default
					new ArrayList<>());// value default
			lateOutingTimeOfDaily.add(temp);
		}

		List<LeaveEarlyTimeOfDaily> leaveEarlyTimeOfDaily = new ArrayList<>();
		for (KscdtSchLeaveEarly scl : kscdtSchLeaveEarly) {
			LeaveEarlyTimeOfDaily temp = new LeaveEarlyTimeOfDaily(TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value
																														// default
					TimeWithCalculation.sameTime(AttendanceTime.ZERO), // value default
					new WorkNo(scl.pk.workNo), scl.toDomain(), IntervalExemptionTime.defaultValue());// value default
			leaveEarlyTimeOfDaily.add(temp);
		}

		TotalWorkingTime totalWorkingTime = new TotalWorkingTime(new AttendanceTime(this.totalTime),
				new AttendanceTime(0), new AttendanceTime(this.totalTimeAct), withinStatutoryTimeOfDaily,
				excessOfStatutoryTimeOfDaily, lateTimeOfDaily, leaveEarlyTimeOfDaily, breakTimeOfDaily,
				lateOutingTimeOfDaily, raiseSalaryTimeOfDailyPerfor, new WorkTimes(this.count), temporaryTime,
				shotrTime, holidayOfDaily, new AttendanceTime(vacationAddTime), intervalTime);

		// ????????????
		DivergenceTimeOfDaily divTime = new DivergenceTimeOfDaily(new ArrayList<>());

		// ????????????
		KscdtSchPremium kscdtSchPremium = new KscdtSchPremium();
		PremiumTimeOfDailyPerformance premiumTime = new PremiumTimeOfDailyPerformance(
				kscdtSchPremium.toDomain(premiums),
				new AttendanceAmountDaily(this.premiumAmountTotal), //ver5
				new AttendanceTime(this.premiumWorkTimeTotal)
				); //ver5

		ActualWorkingTimeOfDaily workingTimeOfDaily = new ActualWorkingTimeOfDaily(constraintDiffTime, constraintTime,
				timeDiff, totalWorkingTime, divTime, premiumTime);

		// Create Task
		List<TaskScheduleDetail> details = kscdtSchTask.stream()
				.map(task -> new TaskScheduleDetail(new TaskCode(task.taskCode), new TimeSpanForCalc(new TimeWithDayAttr(task.startClock), new TimeWithDayAttr(task.endClock))))
				.collect(Collectors.toList());
		TaskSchedule taskSchedule = new TaskSchedule(details);

		AttendanceTimeOfDailyAttendance optAttendanceTime = new AttendanceTimeOfDailyAttendance(null,
				workingTimeOfDaily, null, null, null);

		WorkSchedule workSchedule = new WorkSchedule(sID, yMD, null, null, null, null, null, taskSchedule,
				Optional.ofNullable(null), Optional.ofNullable(optAttendanceTime), Optional.ofNullable(null),
				Optional.ofNullable(null));
		return workSchedule;
	}

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KscdtSchTime(KscdtSchTimePK pk, String cid, int count, int totalTime, int totalTimeAct, int prsWorkTime,
			int prsWorkTimeAct, int prsPrimeTime, int prsMidniteTime, int extBindTimeOtw, int extBindTimeHw,
			int extVarwkOtwTimeLegal, int extFlexTime, int extFlexTimePreApp, int extMidNiteOtwTime,
			int extMidNiteHdwTimeLghd, int extMidNiteHdwTimeIlghd, int extMidNiteHdwTimePubhd, int extMidNiteTotal,
			int extMidNiteTotalPreApp, int intervalAtdClock, int intervalTime, int brkTotalTime, int hdPaidTime,
			int hdPaidHourlyTime, int hdComTime, int hdComHourlyTime, int hd60hTime, int hd60hHourlyTime, int hdspTime,
			int hdspHourlyTime, int hdstkTime, int hdHourlyTime, int hdHourlyShortageTime, int absenceTime,
			int vacationAddTime, int staggeredWhTime, List<KscdtSchOvertimeWork> overtimeWorks,
			List<KscdtSchHolidayWork> holidayWorks, List<KscdtSchBonusPay> bonusPays, List<KscdtSchPremium> premiums,
			List<KscdtSchShortTime> shortTimes,List<KscdtSchComeLate> kscdtSchComeLate,List<KscdtSchGoingOut> kscdtSchGoingOut,List<KscdtSchLeaveEarly> kscdtSchLeaveEarly,
			List<KscdtSchTask> kscdtSchTask,
			int prsWorkTimeAmount,int premiumWorkTimeTotal,int premiumAmountTotal,int useDailyHDSub
			) {
		super();
		this.pk = pk;
		this.cid = cid;
		this.count = count;
		this.totalTime = totalTime;
		this.totalTimeAct = totalTimeAct;
		this.prsWorkTime = prsWorkTime;
		this.prsWorkTimeAct = prsWorkTimeAct;
		this.prsPrimeTime = prsPrimeTime;
		this.prsMidniteTime = prsMidniteTime;
		this.extBindTimeOtw = extBindTimeOtw;
		this.extBindTimeHw = extBindTimeHw;
		this.extVarwkOtwTimeLegal = extVarwkOtwTimeLegal;
		this.extFlexTime = extFlexTime;
		this.extFlexTimePreApp = extFlexTimePreApp;
		this.extMidNiteOtwTime = extMidNiteOtwTime;
		this.extMidNiteHdwTimeLghd = extMidNiteHdwTimeLghd;
		this.extMidNiteHdwTimeIlghd = extMidNiteHdwTimeIlghd;
		this.extMidNiteHdwTimePubhd = extMidNiteHdwTimePubhd;
		this.extMidNiteTotal = extMidNiteTotal;
		this.extMidNiteTotalPreApp = extMidNiteTotalPreApp;
		this.intervalAtdClock = intervalAtdClock;
		this.intervalTime = intervalTime;
		this.brkTotalTime = brkTotalTime;
		this.hdPaidTime = hdPaidTime;
		this.hdPaidHourlyTime = hdPaidHourlyTime;
		this.hdComTime = hdComTime;
		this.hdComHourlyTime = hdComHourlyTime;
		this.hd60hTime = hd60hTime;
		this.hd60hHourlyTime = hd60hHourlyTime;
		this.hdspTime = hdspTime;
		this.hdspHourlyTime = hdspHourlyTime;
		this.hdstkTime = hdstkTime;
		this.hdHourlyTime = hdHourlyTime;
		this.hdHourlyShortageTime = hdHourlyShortageTime;
		this.absenceTime = absenceTime;
		this.vacationAddTime = vacationAddTime;
		this.staggeredWhTime = staggeredWhTime;
		this.overtimeWorks = overtimeWorks;
		this.holidayWorks = holidayWorks;
		this.bonusPays = bonusPays;
		this.premiums = premiums;
		this.shortTimes = shortTimes;
		this.kscdtSchComeLate = kscdtSchComeLate;
		this.kscdtSchGoingOut = kscdtSchGoingOut;
		this.kscdtSchLeaveEarly = kscdtSchLeaveEarly;
		this.kscdtSchTask = kscdtSchTask;
		//ver5
		this.prsWorkTimeAmount = prsWorkTimeAmount;
		this.premiumWorkTimeTotal = premiumWorkTimeTotal;
		this.premiumAmountTotal = premiumAmountTotal;
		//ver4
		this.useDailyHDSub = useDailyHDSub;
	}
}
