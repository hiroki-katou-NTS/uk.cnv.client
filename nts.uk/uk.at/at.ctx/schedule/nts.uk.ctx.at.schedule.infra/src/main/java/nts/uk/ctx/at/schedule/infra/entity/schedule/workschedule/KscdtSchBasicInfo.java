package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.UsedDays;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.secondorder.medical.MedicalCareTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.FuriClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.StayingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkTimeNightShift;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 勤務予定の基本情報
 *
 * @author HieuLt
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSCDT_SCH_BASIC_INFO")
public class KscdtSchBasicInfo extends ContractUkJpaEntity {
	@EmbeddedId
	public KscdtSchBasicInfoPK pk;

	@Column(name = "CID")
	public String companyId;
	/** "予定確定区分 ---true:確定済み---false:未確定" **/
	@Column(name = "DECISION_STATUS")
	public boolean confirmedATR;
	/** 雇用コード **/
	@Column(name = "EMP_CD")
	public String empCd;
	/** 職位ID **/
	@Column(name = "JOB_ID")
	public String jobId;
	/** 職場ID **/
	@Column(name = "WKP_ID")
	public String wkpId;
	/** 分類コード **/
	@Column(name = "CLS_CD ")
	public String clsCd;
	/** 勤務種別コード **/
	@Column(name = "BUSTYPE_CD")
	public String busTypeCd;
	/** 看護区分 **/
	@Column(name = "NURSE_LICENSE")
	public String nurseLicense;
	/** 勤務種類コード **/
	@Column(name = "WKTP_CD")
	public String wktpCd;
	/** 就業時間帯コード **/
	@Column(name = "WKTM_CD")
	public String wktmCd;
	/** "直行区分---true:直行する---false:直行しない" **/
	@Column(name = "GO_STRAIGHT_ATR")
	public boolean goStraightAtr;
	/** "直帰区分---true:直帰する---false:直帰しない" **/
	@Column(name = "BACK_STRAIGHT_ATR")
	public boolean backStraightAtr;

	@Column(name = "TREAT_AS_SUBSTITUTE_ATR")
	public Integer treatAsSubstituteAtr;

	@Column(name = "TREAT_AS_SUBSTITUTE_DAYS")
	public Double treatAsSubstituteDays;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "basicInfo", orphanRemoval = true)
	public KscdtSchTime kscdtSchTime;

	@OneToMany(targetEntity = KscdtSchEditState.class, mappedBy = "kscdtSchBasicInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_EDIT_STATE")
	public List<KscdtSchEditState> editStates;

	@OneToMany(targetEntity = KscdtSchAtdLvwTime.class, mappedBy = "kscdtSchBasicInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_ATD_LVW_TIME")
	public List<KscdtSchAtdLvwTime> atdLvwTimes;

	@OneToMany(targetEntity = KscdtSchShortTimeTs.class, mappedBy = "kscdtSchBasicInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_SHORTTIME_TS")
	public List<KscdtSchShortTimeTs> schShortTimeTs;

	@OneToMany(targetEntity = KscdtSchBreakTs.class, mappedBy = "kscdtSchBasicInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_BREAK_TS")
	public List<KscdtSchBreakTs> breakTs;

	@OneToMany(targetEntity = KscdtSchGoingOutTs.class, mappedBy = "kscdtSchBasicInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCDT_SCH_GOING_OUT_TS")
	public List<KscdtSchGoingOutTs> kscdtSchGoingOutTs;

	public static KscdtSchBasicInfo toEntity(WorkSchedule workSchedule, String cID) {

		String sID = workSchedule.getEmployeeID();

		GeneralDate yMD = workSchedule.getYmd();

		// 勤務予定.所属情報
		AffiliationInforOfDailyAttd workInfo = workSchedule.getAffInfo();

		// 勤務予定.勤務情報
		WorkInfoOfDailyAttendance workInfoOfDaily = workSchedule.getWorkInfo();

		// 勤務予定.勤務情報.勤務実績の勤務情報
		WorkInformation workInformation = workInfoOfDaily.getRecordInfo();

		KscdtSchTime kscdtSchTimes = workSchedule != null ? KscdtSchTime.toEntity(workSchedule, cID) : null;

		// 勤務予定.休憩時間帯
		List<KscdtSchEditState> kscdtEditStates = workSchedule.getLstEditState().stream()
				.map(dailyAttd -> KscdtSchEditState.toEntity(dailyAttd, sID, yMD, cID)).collect(Collectors.toList());

		// 勤務予定．出退勤．出退勤
		List<KscdtSchAtdLvwTime> kscdtSchAtdLvwTimes = new ArrayList<>();
		if (workSchedule.getOptTimeLeaving().isPresent()) {
			kscdtSchAtdLvwTimes = workSchedule.getOptTimeLeaving().get().getTimeLeavingWorks().stream()
					.map(timeLeavingWork -> KscdtSchAtdLvwTime.toEntity(timeLeavingWork, sID, yMD, cID))
					.collect(Collectors.toList());
		}

		// fix bug #114419
		// trường hợp chỉ có workNo = 1 thì chỉ insert item28,29,31,34. Nếu có
		// workNo = 2 nữa thì mới insert thêm item 41,44
		if (workSchedule.getOptTimeLeaving().isPresent()) {
			if (workSchedule.getOptTimeLeaving().get().getWorkTimes() != null && workSchedule.getOptTimeLeaving().get().getWorkTimes().v() != 0) {
				if (workSchedule.getOptTimeLeaving().get().getWorkTimes().v() == 1 && workSchedule.getOptTimeLeaving().get().getTimeLeavingWorks().get(0).getWorkNo().v() == 1) {
					kscdtSchAtdLvwTimes = kscdtSchAtdLvwTimes.stream().filter(i -> i.pk.workNo == 1).collect(Collectors.toList());
				}
			}
		}

		// 勤務予定．短時間勤務．時間帯
		List<KscdtSchShortTimeTs> kscdtSchShortTimeTs = new ArrayList<>();
		if (workSchedule.getOptSortTimeWork().isPresent()) {
			kscdtSchShortTimeTs = workSchedule.getOptSortTimeWork().get().getShortWorkingTimeSheets().stream()
					.map(timeSheet -> KscdtSchShortTimeTs.toEntity(timeSheet, sID, yMD, cID))
					.collect(Collectors.toList());
		}
		// 勤務予定．休憩時間帯
		List<KscdtSchBreakTs> kscdtSchBreakTs = new ArrayList<>();
		List<KscdtSchBreakTs> kscdtSchBreakT = workSchedule.getLstBreakTime().getBreakTimeSheets().stream()
				.map(mapper -> KscdtSchBreakTs.toEntity(mapper, sID, yMD, cID)).collect(Collectors.toList());
		kscdtSchBreakTs.addAll(kscdtSchBreakT);

		KscdtSchBasicInfoPK basicInfoPK = new KscdtSchBasicInfoPK(workSchedule.getEmployeeID(), workSchedule.getYmd());
		//#114431
		Double days = null;
		Integer classifiction = null;
		if(workInfoOfDaily.getNumberDaySuspension().isPresent()) {
			days = workInfoOfDaily.getNumberDaySuspension().get().getDays().v();
			classifiction = workInfoOfDaily.getNumberDaySuspension().get().getClassifiction().value;
		}
		List<KscdtSchGoingOutTs> listKscdtSchGoingOutTs = new ArrayList<>();
		if(workSchedule.getOutingTime().isPresent()) {
			List<KscdtSchGoingOutTs> listKscdtSchComeLate = workSchedule.getOutingTime().get().getOutingTimeSheets()
				.stream().map(c-> KscdtSchGoingOutTs.toEntity(sID, yMD, cID, c)).collect(Collectors.toList());
			listKscdtSchGoingOutTs.addAll(listKscdtSchComeLate);
		}


		// null - QA 110800
		KscdtSchBasicInfo basicInfo = new KscdtSchBasicInfo(basicInfoPK,
				cID, workSchedule.getConfirmedATR().value == 1 ? true : false, workInfo.getEmploymentCode().v(),
				workInfo.getJobTitleID(), workInfo.getWplID(), workInfo.getClsCode().v(),
				!workInfo.getBusinessTypeCode().isPresent() ||workInfo.getBusinessTypeCode() ==null ?null:workInfo.getBusinessTypeCode().get().v(), null, workInformation.getWorkTypeCode().v(),
				workInformation.getWorkTimeCode() == null ? null : workInformation.getWorkTimeCode().v(), workInfoOfDaily.getGoStraightAtr().value == 1 ? true : false,
				workInfoOfDaily.getBackStraightAtr().value == 1 ? true : false,
				classifiction,
				days,
				kscdtSchTimes, kscdtEditStates, kscdtSchAtdLvwTimes,
				kscdtSchShortTimeTs, kscdtSchBreakTs,listKscdtSchGoingOutTs);
		return basicInfo;
	}

	public WorkSchedule toDomain(String sID, GeneralDate yMD) {

		// create WorkInfoOfDailyAttendance
		WorkInformation recordInfo = new WorkInformation(wktpCd, wktmCd);
		WorkInfoOfDailyAttendance workInfo = new WorkInfoOfDailyAttendance(
				recordInfo,
				CalculationState.No_Calculated,
				EnumAdaptor.valueOf(goStraightAtr ? 1 : 0, NotUseAttribute.class),
				EnumAdaptor.valueOf(backStraightAtr ? 1 : 0, NotUseAttribute.class),
				EnumAdaptor.valueOf(GeneralDate.today().dayOfWeek() - 1, DayOfWeek.class),
				new ArrayList<>(),Optional.empty());
		if(treatAsSubstituteAtr != null && treatAsSubstituteDays != null) {
			workInfo.setNumberDaySuspension(Optional.of(
					new NumberOfDaySuspension(
						new UsedDays(treatAsSubstituteDays),
						EnumAdaptor.valueOf(treatAsSubstituteAtr, FuriClassifi.class)
					)));
		}
		// create AffiliationInforOfDailyAttd
		AffiliationInforOfDailyAttd affInfo = new AffiliationInforOfDailyAttd(new EmploymentCode(empCd), jobId, wkpId, new ClassificationCode(clsCd),
				Optional.ofNullable(new BusinessTypeCode(busTypeCd)),
				Optional.empty());

		// create List<BreakTimeOfDailyAttd>
		List<BreakTimeSheet> breakTimeSheets = new ArrayList<>();
		breakTs.stream().forEach(x->{
			BreakTimeSheet timeSheet = new BreakTimeSheet(new BreakFrameNo(x.getPk().getFrameNo()), new TimeWithDayAttr(x.getBreakTsStart()), new TimeWithDayAttr(x.getBreakTsEnd()));
			breakTimeSheets.add(timeSheet);

		});
		BreakTimeOfDailyAttd breakTime = new BreakTimeOfDailyAttd(breakTimeSheets);

		// create List<EditStateOfDailyAttd>
		List<EditStateOfDailyAttd> lstEditState = editStates.stream().map(mapper-> new EditStateOfDailyAttd(mapper.getPk().getAtdItemId(),EnumAdaptor.valueOf(mapper.getSditState(), EditStateSetting.class))).collect(Collectors.toList());

		// create Optional<TimeLeavingOfDailyAttd>
		TimeLeavingOfDailyAttd optTimeLeaving = null;
		List<TimeLeavingWork> timeLeavingWorks = new ArrayList<>();
		@SuppressWarnings("unused")
		TimeWithDayAttr timeWithDayAttr = null;
		atdLvwTimes.stream().forEach(mapper-> {
			WorkStamp workStamp = new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET,Optional.empty()), new TimeWithDayAttr(mapper.getAtdClock())), Optional.empty());
			WorkStamp workStamp2 = new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET,Optional.empty()), new TimeWithDayAttr(mapper.getLwkClock())), Optional.empty());
			//#114431
			TimeSpanForCalc timeVacation = null;
			if(mapper.getAtdHourlyHDTSStart() !=null && mapper.getAtdHourlyHDTSEnd() != null) {
				timeVacation = new TimeSpanForCalc(new TimeWithDayAttr(mapper.getAtdHourlyHDTSStart()) , new TimeWithDayAttr(mapper.getAtdHourlyHDTSEnd()));
			}
			TimeSpanForCalc timeVacation2 = null;
			if(mapper.getLvwHourlyHDTSStart() !=null && mapper.getLvwHourlyHDTSEnd() != null) {
				timeVacation2 = new TimeSpanForCalc(new TimeWithDayAttr(mapper.getLvwHourlyHDTSStart()) , new TimeWithDayAttr(mapper.getLvwHourlyHDTSEnd()));
			}
			TimeActualStamp timeActualStamp = new TimeActualStamp(null, workStamp, 0 , null, timeVacation);
			TimeActualStamp timeActualStamp2 = new TimeActualStamp(null, workStamp2, 0, null, timeVacation2);
			TimeLeavingWork timeLeavingWork = new TimeLeavingWork(new WorkNo(mapper.getPk().getWorkNo()), timeActualStamp, timeActualStamp2);
			timeLeavingWorks.add(timeLeavingWork);
		});
		optTimeLeaving = new TimeLeavingOfDailyAttd(timeLeavingWorks, new WorkTimes(0));

		// create Optional<ShortTimeOfDailyAttd> optSortTimeWork
		ShortTimeOfDailyAttd optSortTimeWork = null;
		List<ShortWorkingTimeSheet> shortWorkingTimeSheets = new ArrayList<>();
		schShortTimeTs.stream().forEach(x->{
			ShortWorkingTimeSheet shortWorkingTimeSheet = new ShortWorkingTimeSheet(new ShortWorkTimFrameNo(x.getPk().getFrameNo()), EnumAdaptor.valueOf(x.getPk().getChildCareAtr(), ChildCareAtr.class),
					new TimeWithDayAttr(x.getShortTimeTsStart()), new TimeWithDayAttr(x.getShortTimeTsEnd()));
			shortWorkingTimeSheets.add(shortWorkingTimeSheet);
		});
		WorkSchedule workSch = kscdtSchTime != null ? kscdtSchTime.toDomain(sID, yMD) : null;
		ActualWorkingTimeOfDaily actualWorkingTimeOfDaily = workSch.getOptAttendanceTime().isPresent() ? workSch.getOptAttendanceTime().get().getActualWorkingTimeOfDaily() : null;
		WorkScheduleTimeOfDaily scheduleTimeOfDaily = new WorkScheduleTimeOfDaily(new WorkScheduleTime(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0)), new AttendanceTime(0));
		
		AttendanceTimeOfDailyAttendance attendance = null;
		TaskSchedule taskSchedule = TaskSchedule.createWithEmptyList();
		StayingTimeOfDaily stayingTime = new StayingTimeOfDaily(new AttendanceTimeOfExistMinus(0), new AttendanceTimeOfExistMinus(0), new AttendanceTimeOfExistMinus(0), new AttendanceTime(0), new AttendanceTimeOfExistMinus(0));
		MedicalCareTimeOfDaily medicalCareTime = new MedicalCareTimeOfDaily(WorkTimeNightShift.DAY_SHIFT, new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0));
		
		if(actualWorkingTimeOfDaily != null) {
		attendance = new AttendanceTimeOfDailyAttendance(
				scheduleTimeOfDaily, actualWorkingTimeOfDaily,
				stayingTime, new AttendanceTimeOfExistMinus(0), new AttendanceTimeOfExistMinus(0), medicalCareTime);
		}
		
		if(workSch != null) {
			taskSchedule = workSch.getTaskSchedule();
		}

		optSortTimeWork = new ShortTimeOfDailyAttd(shortWorkingTimeSheets);

		//#114431
		OutingTimeOfDailyAttd outingTime =null;
		if(!kscdtSchGoingOutTs.isEmpty()) {
			List<OutingTimeSheet> outingTimeSheets = kscdtSchGoingOutTs.stream().map(c-> c.toDomain()).collect(Collectors.toList());
			outingTime = new OutingTimeOfDailyAttd(outingTimeSheets);
		}
		
		return new WorkSchedule(
				sID, 
				yMD, 
				EnumAdaptor.valueOf(confirmedATR ? 1 : 0 ,ConfirmedATR.class),
				workInfo, 
				affInfo, 
				breakTime, 
				lstEditState, 
				taskSchedule, 
				Optional.ofNullable(optTimeLeaving), 
				Optional.ofNullable(attendance), 
				Optional.ofNullable(optSortTimeWork), 
				Optional.ofNullable(outingTime));
	}

	@Override
	protected Object getKey() {

		return this.pk;
	}

}
