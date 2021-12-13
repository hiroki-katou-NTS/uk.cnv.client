/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave;

/**
 * The Interface ManageAnnualSettingSetMemento.
 */
public interface ManageAnnualSettingSetMemento {
    
    /**
     * Sets the company id.
     *
     * @param companyId the new company id
     */
    void setCompanyId(String companyId);
    

    /**
     * Sets the half day manage.
     *
     * @param halfDayManage the new half day manage
     */
    void setHalfDayManage(HalfDayManage halfDayManage);

    /**
     * Sets the remaining number setting.
     *
     * @param remainingNumberSetting the new remaining number setting
     */
    void setRemainingNumberSetting(RemainingNumberSetting remainingNumberSetting);

    
    /**
     * Sets the yearly of day.
     * @return
     */
    void setYearLyOfDays(YearLyOfNumberDays yearLyOfNumberDays);
}
