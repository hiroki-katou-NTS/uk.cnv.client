package nts.uk.ctx.at.function.ac.holidaysremaining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveUsageStatusOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnualLeaveUsageImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.CheckCallRequest;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.HdRemainDetailMerEx;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.HolidayRemainMerEx;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.HolidayRemainMergeAdapter;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.StatusOfHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReserveHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReservedYearHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.RsvLeaUsedCurrentMonImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.CurrentHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.StatusHolidayImported;
import nts.uk.ctx.at.record.dom.monthly.vacation.absenceleave.export.AbsenceleaveCurrentMonthOfEmployee;
import nts.uk.ctx.at.record.dom.monthly.vacation.absenceleave.export.MonthlyAbsenceleaveRemainExport;
import nts.uk.ctx.at.record.dom.monthly.vacation.dayoff.export.DayoffCurrentMonthOfEmployee;
import nts.uk.ctx.at.record.dom.monthly.vacation.dayoff.export.MonthlyDayoffRemainExport;
import nts.uk.ctx.at.record.dom.monthly.vacation.specialholiday.monthremaindata.export.SpecialHolidayRemainDataOutput;
import nts.uk.ctx.at.record.dom.monthly.vacation.specialholiday.monthremaindata.export.SpecialHolidayRemainDataSevice;
import nts.uk.ctx.at.record.pub.monthly.vacation.annualleave.AnnualLeaveUsageExport;
import nts.uk.ctx.at.record.pub.monthly.vacation.annualleave.GetConfirmedAnnualLeave;
import nts.uk.ctx.at.record.pub.monthly.vacation.reserveleave.GetConfirmedReserveLeave;
import nts.uk.ctx.at.record.pub.monthly.vacation.reserveleave.ReserveLeaveUsageExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AggrResultOfAnnualLeaveEachMonth;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveOfThisMonth;
import nts.uk.ctx.at.record.pub.remainnumber.holiday.CheckCallRQ;
import nts.uk.ctx.at.record.pub.remainnumber.holiday.HdRemainDetailMer;
import nts.uk.ctx.at.record.pub.remainnumber.holiday.HdRemainDetailMerPub;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.ReserveLeaveNowExport;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.RsvLeaUsedCurrentMonExport;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.InterimRemainAggregateOutputData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
@Stateless
public class HolidayRemainMergeAdapterImpl implements HolidayRemainMergeAdapter{

	@Inject
	private RemainMergeRepository repoRemainMer;
	@Inject
	private GetConfirmedAnnualLeave a;
	@Inject 
	private GetConfirmedReserveLeave b;
	@Inject
	private MonthlyDayoffRemainExport c;
	@Inject
	private MonthlyAbsenceleaveRemainExport d;
	@Inject
	private SpecialHolidayRemainDataSevice e;
	
	@Inject 
	private HdRemainDetailMerPub hdMerPub;
	
	@Override
	public HolidayRemainMerEx getRemainMer(String employeeId, YearMonthPeriod period) {
		val lstYrMon = ConvertHelper.yearMonthsBetween(period);
		Map<YearMonth, List<RemainMerge>> mapRemainMer = repoRemainMer.findBySidsAndYrMons(employeeId, lstYrMon);
		//255
		List<AnnualLeaveUsageExport> lstAnn = a.getYearHdMonthlyVer2(employeeId, period, mapRemainMer);
		List<AnnualLeaveUsageImported> result255 = new ArrayList<>();
		for (AnnualLeaveUsageExport ann : lstAnn) {
			AnnualLeaveUsageImported HolidayRemainData = new AnnualLeaveUsageImported(ann.getYearMonth(),
					ann.getUsedDays().v(), ann.getUsedTime().map(i -> i.v()).orElse(null), ann.getRemainingDays().v(),
					ann.getRemainingTime().map(i -> i.v()).orElse(null));
			result255.add(HolidayRemainData);
		}
		
		//258
		List<ReserveLeaveUsageExport> lstRsv = b.getYearRsvMonthlyVer2(employeeId, period, mapRemainMer);
		List<ReservedYearHolidayImported> result258 = new ArrayList<>();
		for (ReserveLeaveUsageExport rsv : lstRsv) {
			result258.add(new ReservedYearHolidayImported(rsv.getYearMonth(),
					rsv.getUsedDays().v(), rsv.getRemainingDays().v()));
		}
		
		//259
		List<DayoffCurrentMonthOfEmployee> lstDayCur = c.lstDayoffCurrentMonthOfEmpVer2(employeeId, period, mapRemainMer);
		List<StatusHolidayImported> result259 = new ArrayList<>();
		for (DayoffCurrentMonthOfEmployee day : lstDayCur) {
				StatusHolidayImported statusHoliday = new StatusHolidayImported(day.getYm(), day.getOccurrenceDays(),
						day.getOccurrenceTimes(), day.getUseDays(), day.getUseTimes(), day.getUnUsedDays(),
						day.getUnUsedTimes(), day.getRemainingDays(), day.getRemainingTimes());
				result259.add(statusHoliday);
		}
		
		//260
		List<AbsenceleaveCurrentMonthOfEmployee> lstAbs = d.getDataCurrMonOfEmpVer2(employeeId, period, mapRemainMer);
		List<StatusOfHolidayImported> result260 = new ArrayList<>();
		for (AbsenceleaveCurrentMonthOfEmployee abs : lstAbs) {
			StatusOfHolidayImported sttOfHd = new StatusOfHolidayImported(abs.getYm(), abs.getOccurredDay(),
					abs.getUsedDays(), abs.getUnUsedDays(), abs.getRemainingDays());
			result260.add(sttOfHd);
		}
		
		//263
		List<SpecialHolidayRemainDataOutput> lstSpeHd = e.getSpeHdOfConfMonVer2(employeeId, period, mapRemainMer);
		List<SpecialHolidayImported> result263 = new ArrayList<>();
		for (SpecialHolidayRemainDataOutput speHd : lstSpeHd) {
			SpecialHolidayImported specialHoliday = new SpecialHolidayImported(speHd.getYm(), speHd.getUseDays(),
					speHd.getUseTimes(), speHd.getRemainDays(), speHd.getRemainTimes());
			result263.add(specialHoliday);
		}
		
		return new HolidayRemainMerEx(result255, result258, result259, result260, result263);
	}

	@Override
	public HdRemainDetailMerEx getRemainDetailMer(String employeeId, YearMonth currentMonth, GeneralDate baseDate,
			DatePeriod period, CheckCallRequest checkCall) {
		CheckCallRQ check = new CheckCallRQ(checkCall.isCall265(), checkCall.isCall268(), checkCall.isCall269(), checkCall.isCall363(), checkCall.isCall364(), checkCall.isCall369());
		HdRemainDetailMer data = hdMerPub.getHdRemainDetailMer(employeeId, currentMonth, baseDate, period, check);
		//265
		AnnLeaveOfThisMonth annLeave = data.getResult265();
		AnnLeaveOfThisMonthImported result265 = annLeave == null ? null : new AnnLeaveOfThisMonthImported(annLeave.getGrantDate(), annLeave.getGrantDays(),
				annLeave.getFirstMonthRemNumDays(), annLeave.getFirstMonthRemNumMinutes(), annLeave.getUsedDays().v(),
				annLeave.getUsedMinutes(), annLeave.getRemainDays().v(), annLeave.getRemainMinutes());
		//268
		ReserveLeaveNowExport reserveLeave = data.getResult268();
		ReserveHolidayImported result268 = reserveLeave == null ? null : new ReserveHolidayImported(reserveLeave.getStartMonthRemain().v(), 
				reserveLeave.getGrantNumber().v(), reserveLeave.getUsedNumber().v(), reserveLeave.getRemainNumber().v(),
				reserveLeave.getUndigestNumber().v());
		//269
		List<InterimRemainAggregateOutputData> lst269 = data.getResult269();
		List<CurrentHolidayImported> result269 = lst269.stream()
				.map(c -> new CurrentHolidayImported(c.getYm(), c.getMonthStartRemain(), c.getMonthOccurrence(),
						c.getMonthUse(), c.getMonthExtinction(), c.getMonthEndRemain()))
				.collect(Collectors.toList());
		//363
		List<AggrResultOfAnnualLeaveEachMonth> lst363 = data.getResult363();
		List<AnnLeaveUsageStatusOfThisMonthImported> result363 = lst363.stream().map(c ->
			new AnnLeaveUsageStatusOfThisMonthImported(c.getYearMonth(),
					c.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getUsedNumberInfo().getUsedNumber().getUsedDays().map(x -> x.v()).orElse(0d),
					c.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getUsedNumberInfo().getUsedNumber().getUsedTime().map(x -> x.valueAsMinutes()),
					c.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingDays().v(),
					c.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingTime().isPresent()
									? Optional.of(c.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
											.getRemainingNumber().getAnnualLeaveWithMinus()
											.getRemainingNumberInfo().getRemainingNumber()
											.getTotalRemainingTime().get().v())
									: Optional.empty())
		).collect(Collectors.toList());
		//364
		List<RsvLeaUsedCurrentMonExport> lst364 = data.getResult364();
		List<RsvLeaUsedCurrentMonImported> result364 = lst364.stream().map(c -> new RsvLeaUsedCurrentMonImported(
				c.getYearMonth(), c.getUsedNumber().v(), c.getRemainNumber().v())).collect(Collectors.toList());
		//369
		Optional<GeneralDate> result369 = data.getResult369() == null ? Optional.empty() : 
					Optional.of(data.getResult369().getGrantDate());
		
		return new HdRemainDetailMerEx(result265, result268, result269, result363, result364, result369);
	}

}
