package nts.uk.ctx.at.function.dom.holidaysremaining;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * @author thanh.tq 帳票に出力する項目
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemOutputForm extends DomainObject {

	// 時間外超過
	private NursingCareLeave nursingcareLeave;

	// 代休
	private ItemsOutputtedAlternate substituteHoliday;

	// 公休
	private ItemsPublicOutput holidays;

	// 子の看護休暇
	private ChildNursingLeave childNursingVacation;

	// 年休
	private YearlyItemsOutput annualHoliday;

	// 振休
	private PauseItem pause;

	// 時間外超過
	private Overtime outOfTime;

	// 積立年休
	private YearlyReserved yearlyReserved;

	// 特別休暇
	private List<Integer> specialHoliday;

	public ItemOutputForm(boolean nursingCareLeave, boolean remainingChargeSubstitute, boolean representSubstitute,
			boolean outputItemSubstitute, boolean outputHolidayForward, boolean monthlyPublic,
			boolean outputItemsHolidays, boolean childNursingVacation, boolean yearlyHoliday, boolean insideHours,
			boolean insideHalfDay, boolean numberRemainingPause, boolean undigestedPause, boolean pauseItem,
			boolean yearlyReserved, List<Integer> listHolidayCds) {
		super();
		this.nursingcareLeave = new NursingCareLeave(nursingCareLeave);
		this.substituteHoliday = new ItemsOutputtedAlternate(remainingChargeSubstitute, representSubstitute,
				outputItemSubstitute);
		this.holidays = new ItemsPublicOutput(outputHolidayForward, monthlyPublic, outputItemsHolidays);
		this.childNursingVacation = new ChildNursingLeave(childNursingVacation);
		this.annualHoliday = new YearlyItemsOutput(yearlyHoliday, insideHours, insideHalfDay);
		this.pause = new PauseItem(numberRemainingPause, undigestedPause, pauseItem);
		this.yearlyReserved = new YearlyReserved(yearlyReserved);
		this.specialHoliday = listHolidayCds;
	}

	public boolean hasOutput() {
		return (nursingcareLeave.isNursingLeave() || substituteHoliday.isRemainingChargeSubstitute()
				|| substituteHoliday.isRepresentSubstitute() || substituteHoliday.isOutputItemSubstitute()
				|| holidays.isOutputHolidayForward() || holidays.isMonthlyPublic() || holidays.isOutputItemsHolidays()
				|| childNursingVacation.isChildNursingLeave() || annualHoliday.isYearlyHoliday()
				|| annualHoliday.isInsideHours() || annualHoliday.isInsideHalfDay() || pause.isNumberRemainingPause()
				|| pause.isUndigestedPause() || pause.isPauseItem() || yearlyReserved.isYearlyReserved()
				|| specialHoliday.size() > 0);
	}

}
