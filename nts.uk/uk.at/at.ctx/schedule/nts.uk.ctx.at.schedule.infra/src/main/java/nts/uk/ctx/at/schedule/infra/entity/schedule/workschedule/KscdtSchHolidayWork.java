package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayMidnightWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 勤務予定の休出時間 * UKDesign.データベース.ER図.就業.勤務予定.勤務予定.勤務予定
 * 
 * @author HieuLt
 */
@Entity
@NoArgsConstructor
@Table(name = "KSCDT_SCH_HOLIDAY_WORK")
@Getter
public class KscdtSchHolidayWork extends ContractUkJpaEntity {

	@EmbeddedId
	public KscdtSchHolidayWorkPK pk;

	/** 会社ID **/
	@Column(name = "CID")
	public String cid;

	/** 休出開始時刻 **/
	@Column(name = "HOLIDAY_WORK_TS_START")
	public int holidayWorkTsStart;

	/** 休出終了時刻 **/
	@Column(name = "HOLIDAY_WORK_TS_END")
	public int holidayWorkTsEnd;

	/** 休出時間 **/
	@Column(name = "HOLIDAY_WORK_TIME")
	public int holidayWorkTime;

	/** 振替時間 **/
	@Column(name = "HOLIDAY_WORK_TIME_TRANS")
	public int holidayWorkTimeTrans;

	/** 事前申請時間 **/
	@Column(name = "HOLIDAY_WORK_TIME_PREAPP")
	public int holidayWorkTimePreApp;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "SID", referencedColumnName = "SID"),
			@PrimaryKeyJoinColumn(name = "YMD", referencedColumnName = "YMD") })
	public KscdtSchTime kscdtSchTime;

	public static KscdtSchHolidayWork toEntity(HolidayWorkFrameTime time, HolidayWorkFrameTimeSheet timeSheet, 
			String sID, GeneralDate yMD, String cID) {
		KscdtSchHolidayWorkPK pk = new KscdtSchHolidayWorkPK(sID, yMD, timeSheet.getHolidayWorkTimeSheetNo().v());
		KscdtSchHolidayWork kscdtSchHolidayWork = new KscdtSchHolidayWork(pk, cID,
				timeSheet.getTimeSheet().getStart().v(), timeSheet.getTimeSheet().getEnd().v(),
				time.getHolidayWorkTime().get().getTime().v(), time.getTransferTime().get().getTime().v(),
				time.getBeforeApplicationTime().get().v());
		return kscdtSchHolidayWork;
	}
	
	public static KscdtSchHolidayWork toEntity2(HolidayWorkFrameTimeSheet timeSheet,  HolidayWorkFrameTime time, 
			String sID, GeneralDate yMD, String cID) {
		KscdtSchHolidayWorkPK pk = new KscdtSchHolidayWorkPK(sID, yMD, timeSheet.getHolidayWorkTimeSheetNo().v());
		KscdtSchHolidayWork kscdtSchHolidayWork = new KscdtSchHolidayWork(pk, cID,
				timeSheet.getTimeSheet().getStart().v(), timeSheet.getTimeSheet().getEnd().v(),
				time.getHolidayWorkTime().get().getTime().v(), time.getTransferTime().get().getTime().v(),
				time.getBeforeApplicationTime().get().v());
		return kscdtSchHolidayWork;
	}
	//勤務予定．勤怠時間．勤務時間．総労働時間．所定外時間．休出時間
	public HolidayWorkTimeOfDaily toDomain(int extBindTimeHw, int extMidNiteHdwTimeLghd, int extMidNiteHdwTimeIlghd, int extMidNiteHdwTimePubhd, List<KscdtSchHolidayWork> holidayWorks) {
		HolidayWorkTimeOfDaily timeOfDaily = null;
		List<HolidayWorkFrameTimeSheet> holidayWorkFrame = new ArrayList<>();
		List<HolidayWorkFrameTime> holidayWorkFrameTime = new ArrayList<>();
		if(!holidayWorks.isEmpty()) {
		holidayWorks.stream().forEach(x -> {
			HolidayWorkFrameTimeSheet frameTimeSheet = new HolidayWorkFrameTimeSheet(
					new HolidayWorkFrameNo(x.getPk().getFrameNo()), new TimeSpanForCalc(
							new TimeWithDayAttr(x.holidayWorkTsStart), new TimeWithDayAttr(x.holidayWorkTsEnd)));
			HolidayWorkFrameTime frameTime = new HolidayWorkFrameTime(new HolidayWorkFrameNo(x.getPk().frameNo),
					Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(x.getHolidayWorkTime()))),
					Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(x.getHolidayWorkTimeTrans()))),
					Finally.of(new AttendanceTime(x.getHolidayWorkTimePreApp())));
			holidayWorkFrame.add(frameTimeSheet);
			holidayWorkFrameTime.add(frameTime);
		});
		}
		HolidayWorkMidNightTime workMidNightTime = new HolidayWorkMidNightTime( new TimeDivergenceWithCalculation(new AttendanceTime(extMidNiteHdwTimeLghd), null, null), null);
		List<HolidayWorkMidNightTime> midNightTimes = new ArrayList<>();
		midNightTimes.add(workMidNightTime);
		HolidayMidnightWork midnightWork = new HolidayMidnightWork(midNightTimes);
		timeOfDaily = new HolidayWorkTimeOfDaily(holidayWorkFrame, holidayWorkFrameTime, Finally.of(midnightWork), new AttendanceTime(extBindTimeHw));
		return timeOfDaily;
	}

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KscdtSchHolidayWork(KscdtSchHolidayWorkPK pk, String cid, int holidayWorkTsStart, int holidayWorkTsEnd,
			int holidayWorkTime, int holidayWorkTimeTrans, int holidayWorkTimePreApp) {
		super();
		this.pk = pk;
		this.cid = cid;
		this.holidayWorkTsStart = holidayWorkTsStart;
		this.holidayWorkTsEnd = holidayWorkTsEnd;
		this.holidayWorkTime = holidayWorkTime;
		this.holidayWorkTimeTrans = holidayWorkTimeTrans;
		this.holidayWorkTimePreApp = holidayWorkTimePreApp;
	}

}
