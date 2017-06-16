/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command.dto.CompensatoryAcquisitionUseDto;
import nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command.dto.CompensatoryDigestiveTimeUnitDto;
import nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command.dto.CompensatoryOccurrenceSettingDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryDigestiveTimeUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
@Getter
@Setter
public class SaveCompensatoryLeaveCommand {

	/** The company id. */
	private String companyId;

	/** The is managed. */
	private Integer isManaged;

	/** The normal vacation setting. */
	private CompensatoryAcquisitionUseDto compensatoryAcquisitionUse;

	/** The occurrence vacation setting. */
	private CompensatoryDigestiveTimeUnitDto compensatoryDigestiveTimeUnit;
	
	/** The compensatory occurrence setting. */
	private List<CompensatoryOccurrenceSettingDto> compensatoryOccurrenceSetting; 

	public CompensatoryLeaveComSetting toDomain(String companyId) {
		return new CompensatoryLeaveComSetting(new CompensatoryLeaveComGetMementoImpl(companyId, this));
	}

	public class CompensatoryLeaveComGetMementoImpl implements CompensatoryLeaveComGetMemento {

		/** The company id. */
		private String companyId;

		/** The command. */
		private SaveCompensatoryLeaveCommand command;

		/**
		 * @param companyId
		 * @param command
		 */
		public CompensatoryLeaveComGetMementoImpl(String companyId, SaveCompensatoryLeaveCommand command) {
			this.companyId = companyId;
			this.command = command;
		}

		@Override
		public String getCompanyId() {
			return this.companyId;
		}

		@Override
		public ManageDistinct getIsManaged() {
			return ManageDistinct.valueOf(this.command.isManaged);
		}

		@Override
		public CompensatoryAcquisitionUse getCompensatoryAcquisitionUse() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompensatoryDigestiveTimeUnit getCompensatoryDigestiveTimeUnit() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<CompensatoryOccurrenceSetting> getCompensatoryOccurrenceSetting() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
