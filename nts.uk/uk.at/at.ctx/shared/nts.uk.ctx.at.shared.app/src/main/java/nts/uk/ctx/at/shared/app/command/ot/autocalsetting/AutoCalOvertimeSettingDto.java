/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.ot.autocalsetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSettingGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;

/**
 * The Class AutoCalOvertimeSettingDto.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AutoCalOvertimeSettingDto {

	/** The early ot time. */
	private AutoCalSettingDto earlyOtTime;

	/** The early mid ot time. */
	private AutoCalSettingDto earlyMidOtTime;

	/** The normal ot time. */
	private AutoCalSettingDto normalOtTime;

	/** The normal mid ot time. */
	private AutoCalSettingDto normalMidOtTime;

	/** The legal ot time. */
	private AutoCalSettingDto legalOtTime;

	/** The legal mid ot time. */
	private AutoCalSettingDto legalMidOtTime;

	/**
	 * To domain.
	 *
	 * @param companyId
	 *            the company id
	 * @return the auto cal overtime setting
	 */
	public AutoCalOvertimeSetting toDomain() {
		return new AutoCalOvertimeSetting(new DtoGetMemento(this));
	}

	/**
	 * The Class DtoGetMemento.
	 */
	@SuppressWarnings("rawtypes")
	private class DtoGetMemento implements AutoCalOvertimeSettingGetMemento {

		/** The command. */
		private AutoCalOvertimeSettingDto command;

		/**
		 * Instantiates a new dto get memento.
		 *
		 * @param companyId
		 *            the company id
		 * @param command
		 *            the command
		 */
		public DtoGetMemento(AutoCalOvertimeSettingDto command) {
			this.command = command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * AutoCalOvertimeSettingGetMemento#getEarlyOtTime()
		 */
		@Override
		public AutoCalSetting getEarlyOtTime() {
			return new AutoCalSetting(
					TimeLimitUpperLimitSetting.valueOf(command.getEarlyOtTime().getUpLimitOtSet()),
					AutoCalAtrOvertime.valueOf(command.getEarlyOtTime().getCalAtr()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * AutoCalOvertimeSettingGetMemento#getEarlyMidOtTime()
		 */
		@Override
		public AutoCalSetting getEarlyMidOtTime() {
			return new AutoCalSetting(
					TimeLimitUpperLimitSetting
							.valueOf(command.getEarlyMidOtTime().getUpLimitOtSet()),
					AutoCalAtrOvertime.valueOf(command.getEarlyMidOtTime().getCalAtr()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * AutoCalOvertimeSettingGetMemento#getNormalOtTime()
		 */
		@Override
		public AutoCalSetting getNormalOtTime() {
			return new AutoCalSetting(
					TimeLimitUpperLimitSetting.valueOf(command.getNormalOtTime().getUpLimitOtSet()),
					AutoCalAtrOvertime.valueOf(command.getNormalOtTime().getCalAtr()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * AutoCalOvertimeSettingGetMemento#getNormalMidOtTime()
		 */
		@Override
		public AutoCalSetting getNormalMidOtTime() {
			return new AutoCalSetting(
					TimeLimitUpperLimitSetting
							.valueOf(command.getNormalMidOtTime().getUpLimitOtSet()),
					AutoCalAtrOvertime.valueOf(command.getNormalMidOtTime().getCalAtr()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * AutoCalOvertimeSettingGetMemento#getLegalOtTime()
		 */
		@Override
		public AutoCalSetting getLegalOtTime() {
			return new AutoCalSetting(
					TimeLimitUpperLimitSetting.valueOf(command.getLegalOtTime().getUpLimitOtSet()),
					AutoCalAtrOvertime.valueOf(command.getLegalOtTime().getCalAtr()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * AutoCalOvertimeSettingGetMemento#getLegalMidOtTime()
		 */
		@Override
		public AutoCalSetting getLegalMidOtTime() {
			return new AutoCalSetting(
					TimeLimitUpperLimitSetting
							.valueOf(command.getLegalMidOtTime().getUpLimitOtSet()),
					AutoCalAtrOvertime.valueOf(command.getLegalMidOtTime().getCalAtr()));
		}
	}

	public static AutoCalOvertimeSettingDto fromDomain(AutoCalOvertimeSetting domain) {

		return new AutoCalOvertimeSettingDto(
				AutoCalSettingDto.fromDomain(domain.getEarlyOtTime()),
				AutoCalSettingDto.fromDomain(domain.getEarlyMidOtTime()),
				AutoCalSettingDto.fromDomain(domain.getNormalOtTime()),
				AutoCalSettingDto.fromDomain(domain.getNormalMidOtTime()),
				AutoCalSettingDto.fromDomain(domain.getLegalOtTime()),
				AutoCalSettingDto.fromDomain(domain.getLegalMidOtTime()));
	}
}
