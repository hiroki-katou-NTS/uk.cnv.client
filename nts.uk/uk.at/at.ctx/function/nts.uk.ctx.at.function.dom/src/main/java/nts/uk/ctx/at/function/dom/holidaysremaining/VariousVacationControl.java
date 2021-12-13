package nts.uk.ctx.at.function.dom.holidaysremaining;

import java.util.List;

import lombok.Data;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;

@Data
public class VariousVacationControl {

    private boolean annualHolidaySetting;
    private boolean yearlyReservedSetting;
    private boolean substituteHolidaySetting;
    private boolean pauseItemHolidaySetting;
    private boolean childNursingSetting;
    private boolean nursingCareSetting;
    boolean com60HourVacationSetting ;
    boolean publicHolidaySetting ;
    boolean halfDayYearlySetting;
    boolean hourlyLeaveSetting;
    boolean pauseItemHolidaySettingCompany;
    private List<SpecialHoliday> listSpecialHoliday;

    public VariousVacationControl(boolean annualHolidaySetting, boolean yearlyReservedSetting,
                                  boolean substituteHolidaySetting, boolean pauseItemHolidaySetting,
                                  boolean childNursingSetting, boolean nursingCareSetting,
                                  boolean com60HourVacationSetting, boolean publicHolidaySetting,
                                  boolean halfDayYearlySetting, boolean hourlyLeaveSetting,boolean pauseItemHolidaySettingCompany,
                                  List<SpecialHoliday> listSpecialHoliday) {
        super();
        this.annualHolidaySetting = annualHolidaySetting;
        this.yearlyReservedSetting = yearlyReservedSetting;
        this.substituteHolidaySetting = substituteHolidaySetting;
        this.pauseItemHolidaySetting = pauseItemHolidaySetting;
        this.childNursingSetting = childNursingSetting;
        this.nursingCareSetting = nursingCareSetting;
        this.listSpecialHoliday = listSpecialHoliday;
        this.com60HourVacationSetting = com60HourVacationSetting;
        this.publicHolidaySetting = publicHolidaySetting;
        this.halfDayYearlySetting = halfDayYearlySetting;
        this.hourlyLeaveSetting = hourlyLeaveSetting;
        this.pauseItemHolidaySettingCompany = pauseItemHolidaySettingCompany;
    }
}
