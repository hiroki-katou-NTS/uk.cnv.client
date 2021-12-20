package nts.uk.ctx.at.function.app.find.holidaysremaining;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import nts.uk.ctx.at.function.dom.holidaysremaining.VariousVacationControl;
import nts.uk.ctx.at.shared.app.find.specialholiday.SpecialHolidayDto;

@Value
public class VariousVacationControlDto {
	private boolean annualHolidaySetting;
	private boolean yearlyReservedSetting;
	private boolean substituteHolidaySetting;
	private boolean pauseItemHolidaySetting;
	private boolean childNursingSetting;
	private boolean nursingCareSetting;
	private boolean com60HourVacationSetting ;
	private boolean publicHolidaySetting ;
	boolean halfDayYearlySetting;
	boolean hourlyLeaveSetting;
	boolean pauseItemHolidaySettingCompany;

	private List<SpecialHolidayDto> listSpecialHoliday;

	public static VariousVacationControlDto fromDomain(VariousVacationControl domain) {
		return new VariousVacationControlDto(
				domain.isAnnualHolidaySetting(),
				domain.isYearlyReservedSetting(),
				domain.isSubstituteHolidaySetting(),
				domain.isPauseItemHolidaySetting(),
				domain.isChildNursingSetting(),
				domain.isNursingCareSetting(),
				domain.isCom60HourVacationSetting(),
				domain.isPublicHolidaySetting(),
				domain.isHalfDayYearlySetting(),
				domain.isHourlyLeaveSetting(),
				domain.isPauseItemHolidaySettingCompany(),
				domain.getListSpecialHoliday().stream()
						.map(c -> SpecialHolidayDto.fromDomain(c)).collect(Collectors.toList()));

	}
}
