/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.find.statutory.worktime.companyNew;

import lombok.Data;
import nts.uk.ctx.at.shared.app.command.statutory.worktime.common.WorkingTimeSettingDto;
import nts.uk.ctx.at.shared.dom.statutory.worktime.companyNew.ComRegularLaborTime;

/**
 * The Class CompanyRegularLaborHourDto.
 */
@Data
public class ComRegularLaborTimeDto {
	/** The working time setting new. */
	/** 会社労働時間設定. */
	private WorkingTimeSettingDto workingTimeSetting;

	public static ComRegularLaborTimeDto fromDomain(ComRegularLaborTime comRegularLaborTime) {
		ComRegularLaborTimeDto dto = new ComRegularLaborTimeDto();
		WorkingTimeSettingDto workingTimeSetting = WorkingTimeSettingDto.fromDomain(comRegularLaborTime.getWorkingTimeSet());
		dto.setWorkingTimeSetting(workingTimeSetting);
		return dto;
	}

}
