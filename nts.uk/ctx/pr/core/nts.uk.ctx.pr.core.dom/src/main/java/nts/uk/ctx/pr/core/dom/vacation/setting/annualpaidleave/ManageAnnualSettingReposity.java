/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.vacation.setting.annualpaidleave;

/**
 * The Interface YearVacationManageSettingReposity.
 */
public interface ManageAnnualSettingReposity {
    
    /**
     * Update.
     *
     * @param setting the setting
     */
    void update(ManageAnnualSetting setting);
    
    /**
     * Find by company id.
     *
     * @param companyId the company id
     * @return the manage annual setting
     */
    ManageAnnualSetting findByCompanyId(String companyId);
}
