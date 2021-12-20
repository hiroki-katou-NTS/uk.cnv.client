/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.annualpaidleave;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualNumberDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.HalfDayManage;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.ManageAnnualSettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.MaxDayReference;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RemainingNumberSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RetentionYear;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.RoundProcessingClassification;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.YearLyOfNumberDays;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.annualpaidleave.KshmtHdpaidSetMng;

/**
 * The Class JpaManageAnnualSettingGetMemento.
 */
public class JpaManageAnnualSettingGetMemento implements ManageAnnualSettingGetMemento {

    /** The entity. */
    private KshmtHdpaidSetMng entity;

    /**
     * Instantiates a new jpa manage annual setting get memento.
     *
     * @param entity the entity
     */
    public JpaManageAnnualSettingGetMemento(KshmtHdpaidSetMng entity) {
        this.entity = entity;
    }

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * ManageAnnualSettingGetMemento#getCompanyId()
     */
    @Override
    public String getCompanyId() {
        return this.entity.getCid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * ManageAnnualSettingGetMemento#getMaxGrantDay()
     */

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * ManageAnnualSettingGetMemento#getHalfDayManage()
     */
    @Override
    public HalfDayManage getHalfDayManage() {
        HalfDayManage halfDay = HalfDayManage.builder()
                .manageType(ManageDistinct.valueOf(this.entity.getHalfManageAtr()))
                .reference(MaxDayReference.valueOf(this.entity.getHalfMaxReference()))
                .maxNumberUniformCompany(new AnnualNumberDay(this.entity.getHalfMaxUniformComp()))
                .roundProcesCla(RoundProcessingClassification.valueOf(this.entity.getHalfRoundProc()))
                .build();
        return halfDay;
    }

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * ManageAnnualSettingGetMemento#getRemainingNumberSetting()
     */
    @Override
    public RemainingNumberSetting getRemainingNumberSetting() {
        RemainingNumberSetting remain = RemainingNumberSetting.builder()
                .retentionYear(new RetentionYear(this.entity.getRetentionYear()))
                .build();
        return remain;
    }

    /*
     * (non-Javadoc)
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * ManageAnnualSettingGetMemento#getYearLyOfDays()
     */
	@Override
	public YearLyOfNumberDays getYearLyOfDays() {
		return new YearLyOfNumberDays(entity.getScheduleWorkingDays());
	}
}
