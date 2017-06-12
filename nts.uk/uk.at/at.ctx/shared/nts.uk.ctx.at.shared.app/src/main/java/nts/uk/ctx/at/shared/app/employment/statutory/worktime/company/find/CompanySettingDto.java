/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.employment.statutory.worktime.company.find;

import lombok.Value;
import nts.uk.ctx.at.shared.app.employment.statutory.worktime.shared.DeformationLaborSettingDto;
import nts.uk.ctx.at.shared.app.employment.statutory.worktime.shared.FlexSettingDto;
import nts.uk.ctx.at.shared.app.employment.statutory.worktime.shared.NormalSettingDto;
import nts.uk.ctx.at.shared.dom.employment.statutory.worktime.CompanySetting;

/**
 * The Class CompanySettingDto.
 */
@Value
public class CompanySettingDto {

	/** The flex setting. */
	private FlexSettingDto flexSetting;

	/** The deformation labor setting. */
	private DeformationLaborSettingDto deformationLaborSetting;

	/** The year. */
	private int year;

	/** The normal setting. */
	private NormalSettingDto normalSetting;

	/**
	 * From domain.
	 *
	 * @param domain the domain
	 * @return the company setting dto
	 */
	public static CompanySettingDto fromDomain(CompanySetting domain) {
		FlexSettingDto flexSetting = FlexSettingDto.fromDomain(domain.getFlexSetting());
		DeformationLaborSettingDto deformationLaborSetting = DeformationLaborSettingDto
				.fromDomain(domain.getDeformationLaborSetting());
		NormalSettingDto normalSetting = NormalSettingDto.fromDomain(domain.getNormalSetting());

		return new CompanySettingDto(flexSetting, deformationLaborSetting, domain.getYear().v(), normalSetting);
	}
}
