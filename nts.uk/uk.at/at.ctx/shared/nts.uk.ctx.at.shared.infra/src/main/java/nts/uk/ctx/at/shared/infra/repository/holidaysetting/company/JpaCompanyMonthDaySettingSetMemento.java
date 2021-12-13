package nts.uk.ctx.at.shared.infra.repository.holidaysetting.company;

import java.util.List;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingSetMemento;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtHdpubDPerMCom;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.company.KshmtComMonthDaySetPK;


/**
 * The Class JpaCompanyMonthDaySettingSetMemento.
 */
public class JpaCompanyMonthDaySettingSetMemento implements CompanyMonthDaySettingSetMemento{
	
	/** The list kshmt com month day set. */
	private List<KshmtHdpubDPerMCom> listKshmtComMonthDaySet;
	
	/** The company id. */
	private String companyId;

	/** The year. */
	private int year;

	/**
	 * Instantiates a new jpa company month day setting set memento.
	 *
	 * @param entities the entities
	 */
	public JpaCompanyMonthDaySettingSetMemento(List<KshmtHdpubDPerMCom> entities){
		entities.stream().forEach(item -> {
			if (item.getKshmtComMonthDaySetPK() == null) {
				item.setKshmtComMonthDaySetPK(new KshmtComMonthDaySetPK());
			}
		});
		this.listKshmtComMonthDaySet = entities;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingSetMemento#setCompanyId(nts.uk.ctx.bs.employee.dom.common.CompanyId)
	 */
	@Override
	public void setCompanyId(CompanyId companyId) {
		this.companyId = companyId.v();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingSetMemento#setManagementYear(nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public void setManagementYear(Year managementYear) {
		this.year = managementYear.v();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.company.CompanyMonthDaySettingSetMemento#setPublicHolidayMonthSettings(java.util.List)
	 */
	@Override
	public void setPublicHolidayMonthSettings(List<PublicHolidayMonthSetting> publicHolidayMonthSettings) {
		if(this.listKshmtComMonthDaySet.isEmpty()){
			publicHolidayMonthSettings.stream().forEach(item -> {
				KshmtHdpubDPerMCom entity = new KshmtHdpubDPerMCom();
				entity.setKshmtComMonthDaySetPK(new KshmtComMonthDaySetPK());
				entity.getKshmtComMonthDaySetPK().setCid(this.companyId);
				entity.getKshmtComMonthDaySetPK().setManageYear(item.getPublicHdManagementYear().v());
				entity.getKshmtComMonthDaySetPK().setMonth(item.getMonth());
				entity.setInLegalHd(item.getInLegalHoliday().v());
				
				this.listKshmtComMonthDaySet.add(entity);
			});
		} else {
			this.listKshmtComMonthDaySet.stream().forEach(e -> {
				PublicHolidayMonthSetting data = publicHolidayMonthSettings.stream()
						.filter(item -> e.getKshmtComMonthDaySetPK().getMonth() == item.getMonth())
						.findFirst().get();
				e.setKshmtComMonthDaySetPK(new KshmtComMonthDaySetPK(this.companyId, data.getPublicHdManagementYear().v().shortValue(),
						data.getMonth().shortValue()));
				e.setInLegalHd(data.getInLegalHoliday().v());
			});
		}
	}
	
}
