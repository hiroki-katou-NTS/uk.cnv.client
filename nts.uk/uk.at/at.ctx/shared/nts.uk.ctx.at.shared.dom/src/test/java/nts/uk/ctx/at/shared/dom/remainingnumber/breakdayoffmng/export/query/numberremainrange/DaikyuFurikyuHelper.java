package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.MngDataStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.UnbalanceCompensation;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.HolidayAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TargetSelectionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUnUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainCarryForward;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.DayOffError;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.AccuVacationBuilder;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.NumberConsecuVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.UnbalanceVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.OccurrenceDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnUsedDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;

public class DaikyuFurikyuHelper {

	public static String CID = "000000000000-0117";

	public static String SID = "292ae91c-508c-4c6e-8fe8-3e72277dec16";

	public static AccumulationAbsenceDetail createDetail(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			GeneralDate deadline, DigestionAtr digestionCate, GeneralDate extinctionDate, boolean unknownDate,
			Optional<GeneralDate> dayoffDate, MngDataStatus dataAtr, String manageId, Integer timeOneDay,
			Integer timeHalfDay, Double occurrentDay, Integer occurrentTime, Double unbalanceDay,
			Integer unbalanceTime) {

		AccumulationAbsenceDetail detail = new AccuVacationBuilder(SID,
				new CompensatoryDayoffDate(unknownDate, dayoffDate), occurrentClass, dataAtr, manageId)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(occurrentDay),
								Optional.ofNullable(occurrentTime == null ? null : new AttendanceTime(occurrentTime))))
						.unbalanceNumber(new NumberConsecuVacation(new ManagementDataRemainUnit(unbalanceDay),
								Optional.ofNullable(unbalanceTime == null ? null : new AttendanceTime(unbalanceTime))))
						.build();
		if (occurrentClass == OccurrenceDigClass.DIGESTION) {
			return detail;
		}

		if (isDaikyu) {
			return new UnbalanceVacation(deadline, digestionCate, Optional.ofNullable(extinctionDate), detail,
					new AttendanceTime(timeOneDay), new AttendanceTime(timeHalfDay));
		} else {
			return new UnbalanceCompensation(detail, deadline, DigestionAtr.UNUSED, Optional.ofNullable(extinctionDate),
					HolidayAtr.PUBLICHOLIDAY);
		}
	}

	public static AccumulationAbsenceDetail createDetail(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, GeneralDate deadline, String manageId, Double occurrentDay,
			Integer occurrentTime, Double unbalanceDay, Integer unbalanceTime) {
		return createDetail(isDaikyu, occurrentClass, deadline, DigestionAtr.UNUSED, GeneralDate.max(),
				!dayoffDate.isPresent(), dayoffDate, MngDataStatus.RECORD, manageId, 480, 240, occurrentDay,
				occurrentTime, unbalanceDay, unbalanceTime);

	}

	public static AccumulationAbsenceDetail createDetailDefault(OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, String manageId) {
		return createDetailDefault(true, occurrentClass, dayoffDate, manageId, 0.0, 0, 0.0, 0);

	}

	public static AccumulationAbsenceDetail createDetailDefault(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, String manageId, Double occurrentDay, Integer occurrentTime,
			Double unbalanceDay, Integer unbalanceTime) {
		AccumulationAbsenceDetail detail = new AccuVacationBuilder(SID,
				new CompensatoryDayoffDate(!dayoffDate.isPresent(), dayoffDate), occurrentClass, MngDataStatus.RECORD,
				manageId)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(occurrentDay),
								Optional.ofNullable(occurrentTime == null ? null : new AttendanceTime(occurrentTime))))
						.unbalanceNumber(new NumberConsecuVacation(new ManagementDataRemainUnit(unbalanceDay),
								Optional.ofNullable(unbalanceTime == null ? null : new AttendanceTime(unbalanceTime))))
						.build();
		if (occurrentClass == OccurrenceDigClass.DIGESTION) {
			return detail;
		}

		if (isDaikyu) {
			return new UnbalanceVacation(GeneralDate.max(), DigestionAtr.UNUSED, Optional.ofNullable(GeneralDate.max()),
					detail, new AttendanceTime(480), new AttendanceTime(240));
		} else {
			return new UnbalanceCompensation(detail, GeneralDate.max(), DigestionAtr.UNUSED,
					Optional.ofNullable(GeneralDate.max()), HolidayAtr.PUBLICHOLIDAY);
		}
	}

	public static AccumulationAbsenceDetail createDetailDefaultUnba(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, String manageId, Double unbalanceDay, Integer unbalanceTime) {

		return createDetailDefault(isDaikyu, occurrentClass, dayoffDate, manageId, 0.0, 0, unbalanceDay, unbalanceTime);
	}

	public static AccumulationAbsenceDetail createDetailDefault(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, String manageId, GeneralDate deadline,
			Double unbalanceDay, Integer unbalanceTime) {
		return createDetailDefault(isDaikyu, occurrentClass, dayoffDate, manageId, DigestionAtr.UNUSED, deadline, unbalanceDay, unbalanceTime);
	}
	public static AccumulationAbsenceDetail createDetailDefault(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, String manageId, DigestionAtr atr, GeneralDate deadline,
			Double unbalanceDay, Integer unbalanceTime) {
		AccumulationAbsenceDetail detail = new AccuVacationBuilder(SID,
				new CompensatoryDayoffDate(!dayoffDate.isPresent(), dayoffDate), occurrentClass, MngDataStatus.RECORD,
				manageId)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(0.0),
								Optional.ofNullable(new AttendanceTime(0))))
						.unbalanceNumber(new NumberConsecuVacation(new ManagementDataRemainUnit(unbalanceDay),
								Optional.ofNullable(new AttendanceTime(unbalanceTime))))
						.build();
		if (occurrentClass == OccurrenceDigClass.DIGESTION) {
			return detail;
		}

		if (isDaikyu) {
			return new UnbalanceVacation(deadline, atr, Optional.ofNullable(GeneralDate.max()), detail,
					new AttendanceTime(480), new AttendanceTime(240));
		} else {
			return new UnbalanceCompensation(detail, deadline, atr, Optional.ofNullable(GeneralDate.max()),
					HolidayAtr.PUBLICHOLIDAY);
		}
	}

	public static AccumulationAbsenceDetail createDetailDefault(boolean isDaikyu, OccurrenceDigClass occurrentClass,
			Optional<GeneralDate> dayoffDate, String manageId, Double occDay, Integer occTime) {
		AccumulationAbsenceDetail detail = new AccuVacationBuilder(SID,
				new CompensatoryDayoffDate(!dayoffDate.isPresent(), dayoffDate), occurrentClass, MngDataStatus.RECORD,
				manageId)
						.numberOccurren(new NumberConsecuVacation(new ManagementDataRemainUnit(occDay),
								Optional.ofNullable(new AttendanceTime(occTime))))
						.unbalanceNumber(new NumberConsecuVacation(new ManagementDataRemainUnit(0.0),
								Optional.ofNullable(new AttendanceTime(0))))
						.build();
		if (occurrentClass == OccurrenceDigClass.DIGESTION) {
			return detail;
		}

		if (isDaikyu) {
			return new UnbalanceVacation(GeneralDate.max(), DigestionAtr.UNUSED, Optional.ofNullable(GeneralDate.max()),
					detail, new AttendanceTime(480), new AttendanceTime(240));
		} else {
			return new UnbalanceCompensation(detail, GeneralDate.max(), DigestionAtr.UNUSED,
					Optional.ofNullable(GeneralDate.max()), HolidayAtr.PUBLICHOLIDAY);
		}
	}

	public static SubstituteHolidayAggrResult createBeforeResult(List<AccumulationAbsenceDetail> lstAccDetail,
			Double remainDay, Integer remainTime, Double dayUse, Integer timeUse, Double occurrenceDay,
			Integer occurrenceTime, Double carryoverDay, Integer carryoverTime, Double unusedDay, Integer unusedTime,
			List<DayOffError> dayOffErrors, GeneralDate nextDay) {
	      return new SubstituteHolidayAggrResult(
	    		new VacationDetails(lstAccDetail),
				new DayOffRemainDayAndTimes(new LeaveRemainingDayNumber(remainDay), Optional.of(new LeaveRemainingTime(remainTime))),//残日数, 残時間
				new DayOffDayTimeUse(new LeaveUsedDayNumber(dayUse), Optional.of(new LeaveUsedTime(timeUse))),
				new DayOffDayAndTimes(new MonthVacationGrantDay(occurrenceDay), Optional.of(new MonthVacationGrantTime(occurrenceTime))),
				new DayOffRemainCarryForward(new LeaveRemainingDayNumber(carryoverDay), Optional.of(new LeaveRemainingTime(carryoverTime))),
				new DayOffDayTimeUnUse(new LeaveRemainingDayNumber(unusedDay), Optional.of(new LeaveRemainingTime(unusedTime))),
				dayOffErrors,
				Finally.of(nextDay), Collections.emptyList());
	}

	public static SubstituteHolidayAggrResult createDefaultResult(List<AccumulationAbsenceDetail> lstAccDetail,
			GeneralDate nextDay) {
		return createDefaultResult(lstAccDetail, 0.0, nextDay);
	}

	public static SubstituteHolidayAggrResult createDefaultResult(List<AccumulationAbsenceDetail> lstAccDetail, Double carryDay,
			GeneralDate nextDay) {
		  return new SubstituteHolidayAggrResult(
		    		new VacationDetails(lstAccDetail),
					new DayOffRemainDayAndTimes(new LeaveRemainingDayNumber(0.0), Optional.of(new LeaveRemainingTime(0))),//残日数, 残時間
					new DayOffDayTimeUse(new LeaveUsedDayNumber(0.0), Optional.of(new LeaveUsedTime(0))),
					new DayOffDayAndTimes(new MonthVacationGrantDay(0.0), Optional.of(new MonthVacationGrantTime(0))),
					new DayOffRemainCarryForward(new LeaveRemainingDayNumber(carryDay), Optional.of(new LeaveRemainingTime(0))),
					new DayOffDayTimeUnUse(new LeaveRemainingDayNumber(0.0), Optional.of(new LeaveRemainingTime(0))),
					new ArrayList<>(),
					Finally.of(nextDay), Collections.emptyList());
	}

	public static BreakDayOffRemainMngRefactParam inputParamDaikyu(DatePeriod dateData, boolean mode,
			GeneralDate screenDisplayDate, boolean replaceChk, List<InterimRemain> interimMng, CreateAtr creatorAtr,
			DatePeriod processDate, List<InterimBreakMng> breakMng, List<InterimDayOffMng> dayOffMng,
			Optional<SubstituteHolidayAggrResult> optBeforeResult, FixedManagementDataMonth fixManaDataMonth) {
		return new BreakDayOffRemainMngRefactParam(CID, SID, dateData, mode, screenDisplayDate, replaceChk, interimMng,
				Optional.ofNullable(creatorAtr), Optional.ofNullable(processDate), breakMng, dayOffMng, optBeforeResult,
				fixManaDataMonth);
	}

	public static BreakDayOffRemainMngRefactParam inputParamDaikyu(DatePeriod dateData, boolean mode,
			GeneralDate screenDisplayDate, boolean replaceChk, List<InterimRemain> interimMng,
			List<InterimBreakMng> breakMng, List<InterimDayOffMng> dayOffMng,
			Optional<SubstituteHolidayAggrResult> optBeforeResult, FixedManagementDataMonth fixManaDataMonth) {
		return new BreakDayOffRemainMngRefactParam(CID, SID, dateData, mode, screenDisplayDate, replaceChk, interimMng,
				Optional.ofNullable(CreateAtr.RECORD), Optional.ofNullable(null), breakMng, dayOffMng, optBeforeResult,
				fixManaDataMonth);
	}

	public static InterimDayOffMng createDayOff(String id, int requireTime, double requireDay) {
		return null;
		/*new InterimDayOffMng(id, new RequiredTime(requireTime), new RequiredDay(requireDay),
				new UnOffsetTime(1020), new UnOffsetDay(1.0));*/
	}

	public static InterimBreakMng createBreak(String id, GeneralDate deadline, int unUseTime, double unUseDay) {

		return null;
		/*new InterimBreakMng(id, new AttendanceTime(480), deadline, new OccurrenceTime(480),
				new OccurrenceDay(1.0), new AttendanceTime(240), new UnUsedTime(unUseTime), new UnUsedDay(unUseDay));*/
	}

	public static InterimRemain createRemain(String id, GeneralDate date, CreateAtr createBy, RemainType type) {
		return null; /*new InterimRemain(id, SID, date, createBy, type);*/
	}

	public static InterimAbsMng createAbsMng(String id, double requireDay) {
		return null; //new InterimAbsMng(id, new RequiredDay(requireDay), new UnOffsetDay(1.0));
	}

	public static InterimRecMng createRecMng(String remainManaID, String sid, GeneralDate ymd, CreateAtr creatorAtr,
			RemainType remainType, GeneralDate deadline, double occDay) {

		return new InterimRecMng(remainManaID, sid, ymd, creatorAtr, remainType, deadline, new OccurrenceDay(1.0),
				new UnUsedDay(1.0));
	}

	public static InterimRecMng createRecUseMng(String remainManaID, String sid, GeneralDate ymd, CreateAtr creatorAtr,
			RemainType remainType, GeneralDate deadline, double unuse) {
		return new InterimRecMng(remainManaID, sid, ymd, creatorAtr, remainType, deadline, new OccurrenceDay(1.0),
				new UnUsedDay(unuse));
	}

	public static AbsRecMngInPeriodRefactParamInput createAbsRecInput(DatePeriod period, GeneralDate dateRefer,
			boolean mode, boolean replaceChk, List<InterimAbsMng> useAbsMng, List<InterimRemain> interimMng,
			List<InterimRecMng> useRecMng) {
		return new AbsRecMngInPeriodRefactParamInput(CID, SID, period, dateRefer, mode, replaceChk, useAbsMng,
				interimMng, useRecMng, Optional.empty(), Optional.empty(), Optional.empty(),
				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));
	}

	public static AbsRecMngInPeriodRefactParamInput createAbsRecInput(DatePeriod period, GeneralDate dateRefer,
			boolean mode, boolean replaceChk, Optional<CompenLeaveAggrResult> optBeforeResult) {
		return new AbsRecMngInPeriodRefactParamInput(CID, SID, period, dateRefer, mode, replaceChk, new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(), optBeforeResult, Optional.empty(), Optional.empty(),
				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));
	}

	public static LeaveComDayOffManagement createLeavComDayOff(GeneralDate occDate, GeneralDate digestDate,
			double usedDays) {
		return new LeaveComDayOffManagement(SID, occDate, digestDate, usedDays, TargetSelectionAtr.MANUAL.value);
	}

	public static PayoutSubofHDManagement createHD(GeneralDate occDate, GeneralDate digestDate,
			double usedDays) {
		return new PayoutSubofHDManagement(SID, occDate, digestDate, usedDays, TargetSelectionAtr.MANUAL.value);
	}
}
