package nts.uk.ctx.at.function.dom.holidaysremaining.report;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaGrantNumberImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveUsageStatusOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnualLeaveUsageImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.ChildNursingLeaveCurrentSituationImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.CurrentHolidayRemainImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.NursingLeaveCurrentSituationImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.StatusOfHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReserveHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReservedYearHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.RsvLeaUsedCurrentMonImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.CurrentHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.StatusHolidayImported;

@Setter
@Getter
public class HolidayRemainingInfor {

	// RequestList369
	private Optional<GeneralDate> grantDate;
	// RequestList281
	private List<AnnLeaGrantNumberImported> listAnnLeaGrantNumber;
	// RequestList265
	private AnnLeaveOfThisMonthImported annLeaveOfThisMonth;
	// RequestList255
	private List<AnnualLeaveUsageImported> listAnnualLeaveUsage;
	// RequestList363
	private List<AnnLeaveUsageStatusOfThisMonthImported> listAnnLeaveUsageStatusOfThisMonth;
	// RequestList268
	private ReserveHolidayImported reserveHoliday;
	// RequestList258
	private List<ReservedYearHolidayImported> listReservedYearHoliday;
	// RequestList364
	private List<RsvLeaUsedCurrentMonImported> listRsvLeaUsedCurrentMon;
	// RequestList269
	private List<CurrentHolidayImported> listCurrentHoliday;
	// RequestList259
	private List<StatusHolidayImported> listStatusHoliday;
	// RequestList270
	private List<CurrentHolidayRemainImported> listCurrentHolidayRemain;
	// RequestList260
	private List<StatusOfHolidayImported> listStatusOfHoliday;
	// RequestList273
	private Map<Integer, SpecialVacationImported> mapSpecialVacation;
    private Map<YearMonth, Map<Integer, SpecialVacationImported>> mapSPVaCrurrentMonth;

	// RequestList263
	private Map<Integer, List<SpecialHolidayImported>> mapListSpecialHoliday;
	// RequestList206
	private ChildNursingLeaveCurrentSituationImported childNursingLeave;
	// RequestList207
	private NursingLeaveCurrentSituationImported nursingLeave;
	//
	private CurrentHolidayImported currentHolidayLeft;
	
	private CurrentHolidayRemainImported currentHolidayRemainLeft ; 
	public HolidayRemainingInfor(Optional<GeneralDate> grantDate, List<AnnLeaGrantNumberImported> listAnnLeaGrantNumber,
			AnnLeaveOfThisMonthImported annLeaveOfThisMonth, List<AnnualLeaveUsageImported> listAnnualLeaveUsage,
			List<AnnLeaveUsageStatusOfThisMonthImported> listAnnLeaveUsageStatusOfThisMonth,
			ReserveHolidayImported reserveHoliday, List<ReservedYearHolidayImported> listReservedYearHoliday,
			List<RsvLeaUsedCurrentMonImported> listRsvLeaUsedCurrentMon,
			List<CurrentHolidayImported> listCurrentHoliday, List<StatusHolidayImported> listStatusHoliday,
			List<CurrentHolidayRemainImported> listCurrentHolidayRemain,
			List<StatusOfHolidayImported> listStatusOfHoliday, Map<Integer, SpecialVacationImported> mapSpecialVacation, 
			Map<YearMonth, Map<Integer, SpecialVacationImported>> mapSPVaCrurrentMonth,
			Map<Integer, List<SpecialHolidayImported>> mapListSpecialHoliday,
			ChildNursingLeaveCurrentSituationImported childNursingLeave,
			NursingLeaveCurrentSituationImported nursingLeave,
			CurrentHolidayImported currentHolidayLeft,
			CurrentHolidayRemainImported currentHolidayRemainLeft
			) {
		super();
		this.grantDate = grantDate;
		this.listAnnLeaGrantNumber = listAnnLeaGrantNumber;
		this.annLeaveOfThisMonth = annLeaveOfThisMonth;
		this.listAnnualLeaveUsage = listAnnualLeaveUsage;
		this.listAnnLeaveUsageStatusOfThisMonth = listAnnLeaveUsageStatusOfThisMonth;
		this.reserveHoliday = reserveHoliday;
		this.listReservedYearHoliday = listReservedYearHoliday;
		this.listRsvLeaUsedCurrentMon = listRsvLeaUsedCurrentMon;
		this.listCurrentHoliday = listCurrentHoliday;
		this.listStatusHoliday = listStatusHoliday;
		this.listCurrentHolidayRemain = listCurrentHolidayRemain;
		this.listStatusOfHoliday = listStatusOfHoliday;
		this.mapSpecialVacation = mapSpecialVacation;
		this.mapSPVaCrurrentMonth = mapSPVaCrurrentMonth;
		this.mapListSpecialHoliday = mapListSpecialHoliday;
		this.childNursingLeave = childNursingLeave;
		this.nursingLeave = nursingLeave;
		this.currentHolidayLeft = currentHolidayLeft;
		this.currentHolidayRemainLeft = currentHolidayRemainLeft;
	}
	
	
}
