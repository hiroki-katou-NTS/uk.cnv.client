/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command.dto.CompensatoryAcquisitionUseDto;
import nts.uk.ctx.at.shared.app.vacation.setting.compensatoryleave.command.dto.CompensatoryDigestiveTimeUnitDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryDigestiveTimeUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;

// TODO: Auto-generated Javadoc
/**
 * Gets the compensatory digestive time unit.
 *
 * @return the compensatory digestive time unit
 */
@Getter

/**
 * Sets the compensatory digestive time unit.
 *
 * @param compensatoryDigestiveTimeUnit
 *            the new compensatory digestive time unit
 */
@Setter
public class SaveEmploymentCompensatoryCommand {

	// 会社ID
	/** The company id. */
	private String companyId;

	// 雇用区分コード
	/** The employment code. */
	private String employmentCode;

	/** The is managed. */
	private Integer isManaged;

	// 設定
	/** The compensatory acquisition use. */
	private CompensatoryAcquisitionUseDto compensatoryAcquisitionUse;

	// 時間代休の消化単位
	/** The compensatory digestive time unit. */
	private CompensatoryDigestiveTimeUnitDto compensatoryDigestiveTimeUnit;

	/**
	 * To domain.
	 *
	 * @param companyId
	 *            the company id
	 * @return the compensatory leave em setting
	 */
	public CompensatoryLeaveEmSetting toDomain(String companyId) {
		return new CompensatoryLeaveEmSetting(new CompensatoryLeaveEmSettingGetMementoImpl(companyId, this));
	}

	/**
	 * The Class CompensatoryLeaveEmSettingGetMementoImpl.
	 */
	public class CompensatoryLeaveEmSettingGetMementoImpl implements CompensatoryLeaveEmSettingGetMemento {

		/** The company id. */
		private String companyId;

		/** The command. */
		private SaveEmploymentCompensatoryCommand command;

		/**
		 * Instantiates a new compensatory leave em setting get memento impl.
		 *
		 * @param companyId
		 *            the company id
		 * @param command
		 *            the command
		 */
		public CompensatoryLeaveEmSettingGetMementoImpl(String companyId, SaveEmploymentCompensatoryCommand command) {
			this.companyId = companyId;
			this.command = command;
		}

		/**
		 * Gets the company id.
		 *
		 * @return the company id
		 */
		@Override
		public String getCompanyId() {
			return this.companyId;
		}

		/**
		 * Gets the employment code.
		 *
		 * @return the employment code
		 */
		@Override
		public EmploymentCode getEmploymentCode() {
			return new EmploymentCode(this.command.getEmploymentCode());
		}

		@Override
		public ManageDistinct getIsManaged() {
			return ManageDistinct.valueOf(this.command.isManaged);
		}

		@Override
		public CompensatoryAcquisitionUse getCompensatoryAcquisitionUse() {
			return this.command.compensatoryAcquisitionUse.toDomain();
		}

		@Override
		public CompensatoryDigestiveTimeUnit getCompensatoryDigestiveTimeUnit() {
			return this.command.compensatoryDigestiveTimeUnit.toDomain();
		}
	}
}
