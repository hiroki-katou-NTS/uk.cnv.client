package nts.uk.ctx.at.function.ac.holidaysremaining;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaGrantNumberImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveRemainingAdapter;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveUsageStatusOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnualLeaveUsageImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.NextHolidayGrantDateImported;
import nts.uk.ctx.at.record.pub.monthly.vacation.annualleave.AnnualLeaveUsageExport;
import nts.uk.ctx.at.record.pub.monthly.vacation.annualleave.GetConfirmedAnnualLeave;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AggrResultOfAnnualLeaveEachMonth;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveOfThisMonth;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveRemainNumberPub;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.NextHolidayGrantDate;
import nts.uk.ctx.at.shared.pub.remainingnumber.grantremainingdata.AnnLeaGrantRemDataPub;
import nts.uk.ctx.at.shared.pub.remainingnumber.grantremainingdata.AnnualLeaveGrantRemainDataExport;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;

@Stateless
public class AnnLeaveRemainingFinder implements AnnLeaveRemainingAdapter {

	@Inject
	private AnnLeaveRemainNumberPub annLeaveRemainNumberPub;

	@Inject
	private GetConfirmedAnnualLeave getConfirmedAnnualLeave;

	@Inject
	private AnnLeaGrantRemDataPub annLeaGrantRemDataPub;

	@Override
	public List<AnnLeaveUsageStatusOfThisMonthImported> getAnnLeaveUsageOfThisMonth(String employeeId,
			DatePeriod datePeriod) {
		// RequestList363
		List<AggrResultOfAnnualLeaveEachMonth> aggrResults = annLeaveRemainNumberPub
				.getAnnLeaveRemainAfterThisMonth(employeeId, datePeriod);
		if (aggrResults == null) {
			return new ArrayList<AnnLeaveUsageStatusOfThisMonthImported>();
		}

		return aggrResults.stream().map(item -> {

			return new AnnLeaveUsageStatusOfThisMonthImported(item.getYearMonth(),
					item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getUsedNumberInfo().getUsedNumber().getUsedDays().map(c -> c.v()).orElse(0d),
					item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getUsedNumberInfo().getUsedNumber().getUsedTime().map(c -> c.valueAsMinutes()),
					item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingDays().v(),
					item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getAnnualLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumber().getTotalRemainingTime().isPresent()
									? Optional.of(item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
											.getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo().getRemainingNumber()
											.getTotalRemainingTime().get().v())
									: Optional.empty());
		}).collect(Collectors.toList());
	}

	@Override
	public AnnLeaveOfThisMonthImported getAnnLeaveOfThisMonth(String employeeId) {
		// RequestList265
		AnnLeaveOfThisMonth annLeave = annLeaveRemainNumberPub.getAnnLeaveOfThisMonth(employeeId);
		if (annLeave == null) {
			return null;
		}

		return new AnnLeaveOfThisMonthImported(annLeave.getGrantDate(), annLeave.getGrantDays(),
				annLeave.getFirstMonthRemNumDays(), annLeave.getFirstMonthRemNumMinutes(), annLeave.getUsedDays().v(),
				annLeave.getUsedMinutes(), annLeave.getRemainDays().v(), annLeave.getRemainMinutes());
	}

	@Override
	public NextHolidayGrantDateImported getNextHolidayGrantDate(String companyId, String employeeId) {
		// RequestList369
		NextHolidayGrantDate nextHoloday = annLeaveRemainNumberPub.getNextHolidayGrantDate(companyId, employeeId);
		if (nextHoloday == null) {
			return null;
		}

		return new NextHolidayGrantDateImported(nextHoloday.getGrantDate());
	}

	@Override
	public List<AnnLeaGrantNumberImported> algorithm(String employeeId) {
		List<AnnualLeaveGrantRemainDataExport> listAnnualLeave = annLeaGrantRemDataPub
				.getAnnualLeaveGrantRemainingData(employeeId);
		if (listAnnualLeave == null) {
			return new ArrayList<AnnLeaGrantNumberImported>();
		}
		// requestList281
		return listAnnualLeave.stream().map(item -> {
			return new AnnLeaGrantNumberImported(item.getGrantDate(), item.getGrantNumberDays());
		}).collect(Collectors.toList());
	}

	@Override
	public List<AnnualLeaveUsageImported> algorithm(String employeeId, YearMonthPeriod period) {
		// requestList255
		List<AnnualLeaveUsageExport> algorithm = getConfirmedAnnualLeave.algorithm(employeeId, period);
		if (algorithm == null) {
			return new ArrayList<AnnualLeaveUsageImported>();
		}
		List<AnnualLeaveUsageImported> lstHolidayRemainData = new ArrayList<>();
		algorithm.forEach(item -> {
			AnnualLeaveUsageImported HolidayRemainData = new AnnualLeaveUsageImported(item.getYearMonth(),
					item.getUsedDays().v(), item.getUsedTime().map(i -> i.v()).orElse(null), item.getRemainingDays().v(),
					item.getRemainingTime().map(i -> i.v()).orElse(null));
			lstHolidayRemainData.add(HolidayRemainData);
		});
		return lstHolidayRemainData;
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001
	 * RequestList255 社員の月毎の確定済み年休を取得する - ver2
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 */
	@Override
	public List<AnnualLeaveUsageImported> getYearHdMonthlyVer2(String employeeId, YearMonthPeriod period) {
//		List<AnnualLeaveUsageExport> lstAnnLea = getConfirmedAnnualLeave.getYearHdMonthlyVer2(employeeId, period);
		List<AnnualLeaveUsageImported> lstHdRemainData = new ArrayList<>();
//		lstAnnLea.forEach(item -> {
//			AnnualLeaveUsageImported HolidayRemainData = new AnnualLeaveUsageImported(item.getYearMonth(),
//					item.getUsedDays().v(), item.getUsedTime().map(i -> i.v()).orElse(null), item.getRemainingDays().v(),
//					item.getRemainingTime().map(i -> i.v()).orElse(null));
//			lstHdRemainData.add(HolidayRemainData);
//		});
		return lstHdRemainData;
	}

	@Override
	public List<AnnLeaGrantNumberImported> getAnnLeaGrantNumberImporteds(String employeeId) {
		List<AnnualLeaveGrantRemainDataExport> listAnnualLeave = annLeaGrantRemDataPub
				.getAnnualLeaveGrantRemainingData(employeeId);
		if (listAnnualLeave == null) {
			return new ArrayList<AnnLeaGrantNumberImported>();
		}
		// requestList281
		return listAnnualLeave.stream().map(item -> {
			return new AnnLeaGrantNumberImported(
					item.getGrantDate(),
					item.getGrantNumberDays(),
					item.getRemainDay(),
					item.getRemainTime(),
					item.getGrantTime()
			);
		}).collect(Collectors.toList());
	}
}
