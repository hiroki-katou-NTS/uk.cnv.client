package nts.uk.ctx.at.shared.infra.repository.holidaysetting.employment;

import java.util.List;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment.EmploymentMonthDaySettingSetMemento;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employment.KshmtEmpMonthDaySet;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employment.KshmtEmpMonthDaySetPK;

/**
 * The Class JpaEmploymentMonthDaySettingSetMemento.
 */
public class JpaEmploymentMonthDaySettingSetMemento implements EmploymentMonthDaySettingSetMemento{
	
	/** The list kshmt emp month day set. */
	private List<KshmtEmpMonthDaySet> listKshmtEmpMonthDaySet;
	
	/** The company id. */
	private String companyId;
	
	/** The emp cd. */
	private String empCd;
	
	/** The year. */
	private int year;

	/**
	 * Instantiates a new jpa employment month day setting set memento.
	 *
	 * @param entities the entities
	 */
	public JpaEmploymentMonthDaySettingSetMemento(List<KshmtEmpMonthDaySet> entities){
		entities.stream().forEach(item -> {
			if (item.getKshmtEmpMonthDaySetPK() == null) {
				item.setKshmtEmpMonthDaySetPK(new KshmtEmpMonthDaySetPK());
			}
		});
		this.listKshmtEmpMonthDaySet = entities;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employment.EmploymentMonthDaySettingSetMemento#setCompanyId(nts.uk.ctx.bs.employee.dom.common.CompanyId)
	 */
	@Override
	public void setCompanyId(CompanyId companyId) {
		this.companyId = companyId.v();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employment.EmploymentMonthDaySettingSetMemento#setEmploymentCode(java.lang.String)
	 */
	@Override
	public void setEmploymentCode(String employmentCode) {
		this.empCd = employmentCode;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employment.EmploymentMonthDaySettingSetMemento#setManagementYear(nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year)
	 */
	@Override
	public void setManagementYear(Year managementYear) {
		this.year = managementYear.v();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.employment.EmploymentMonthDaySettingSetMemento#setPublicHolidayMonthSettings(java.util.List)
	 */
	@Override
	public void setPublicHolidayMonthSettings(List<PublicHolidayMonthSetting> publicHolidayMonthSettings) {
		if(this.listKshmtEmpMonthDaySet.isEmpty()){
			publicHolidayMonthSettings.stream().forEach(item -> {
				KshmtEmpMonthDaySet entity = new KshmtEmpMonthDaySet();
				entity.setKshmtEmpMonthDaySetPK(new KshmtEmpMonthDaySetPK());
				entity.getKshmtEmpMonthDaySetPK().setCid(this.companyId);
				entity.getKshmtEmpMonthDaySetPK().setEmpCd(this.empCd);
				entity.getKshmtEmpMonthDaySetPK().setManageYear(this.year);
				entity.getKshmtEmpMonthDaySetPK().setMonth(item.getMonth());
				entity.setInLegalHd(item.getInLegalHoliday().v());
				
				this.listKshmtEmpMonthDaySet.add(entity);
			});
		} else {
			this.listKshmtEmpMonthDaySet.stream().forEach(e -> {
				e.getKshmtEmpMonthDaySetPK().setCid(this.companyId);
				e.getKshmtEmpMonthDaySetPK().setManageYear(this.year);
				e.getKshmtEmpMonthDaySetPK().setMonth(publicHolidayMonthSettings.stream()
														.filter(item -> e.getKshmtEmpMonthDaySetPK().getMonth() == item.getMonth())
																	.findFirst().get().getMonth());
				e.setInLegalHd(publicHolidayMonthSettings.stream()
						.filter(item -> e.getKshmtEmpMonthDaySetPK().getMonth() == item.getMonth())
									.findAny().get().getInLegalHoliday().v());
			});
		}
	}
}
