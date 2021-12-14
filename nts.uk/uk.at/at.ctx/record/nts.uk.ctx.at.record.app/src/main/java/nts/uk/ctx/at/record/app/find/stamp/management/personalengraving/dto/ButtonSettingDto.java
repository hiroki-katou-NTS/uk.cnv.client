package nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto;

import lombok.Data;
import nts.uk.ctx.at.record.app.find.stamp.management.ButtonSettingsDto;
import nts.uk.ctx.at.record.app.find.stamp.management.StampTypeDto;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonDisSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonNameSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;

/**
 * @author anhdt
 *
 */
@Data
public class ButtonSettingDto {
	private Integer btnPositionNo;
	private String btnName;
	private String btnTextColor;
	private String btnBackGroundColor;

	private Integer btnReservationArt;
	private Boolean changeHalfDay;
	private Integer goOutArt;
	private Integer setPreClockArt;
	private Integer changeClockArt;
	private Integer changeCalArt;

	private Integer usrArt;
	private Integer audioType;
	// 0 = reservation system, goingToWork = 1, departure = 2, goOut = 3, turnBack =
	// 4
	private Integer btnDisplayType;
	
	private Integer supportWplset;

	public ButtonSettingDto(ButtonSettings btnSet) {
		this.btnPositionNo = btnSet.getButtonPositionNo().v();

		ButtonDisSet btnDisSet = btnSet.getButtonDisSet();
		ButtonNameSet btnNameSet = btnDisSet.getButtonNameSet();
		this.btnName = btnNameSet.getButtonName().isPresent() ? btnNameSet.getButtonName().get().v() : null;
		this.btnTextColor = btnNameSet.getTextColor().v();
		this.btnBackGroundColor = btnDisSet.getBackGroundColor().v();

		ButtonType btnType = btnSet.getButtonType();
		this.btnReservationArt = btnType.getReservationArt().value;

		btnType.getStampType().ifPresent(x -> {
			this.changeHalfDay = x.isChangeHalfDay();
			this.goOutArt = x.getGoOutArt().isPresent() ? x.getGoOutArt().get().value : null;
			this.setPreClockArt = x.getSetPreClockArt() == null ? null : x.getSetPreClockArt().value;
			this.changeClockArt = x.getChangeClockArt() == null ? null : x.getChangeClockArt().value;
			this.changeCalArt = x.getChangeCalArt() == null ? null : x.getChangeCalArt().value;
		});

		this.supportWplset = btnSet.getSupportWplSet().map(m -> m.value).orElse(null);
		this.usrArt = btnSet.getUsrArt().value;
		this.audioType = btnSet.getAudioType().value;
		this.btnDisplayType = changeHalfDay != null
				? ButtonSettingsDto
						.toButtonValueType(StampTypeDto.fromDomain(btnType.getStampType().map(x -> x).orElse(null)))
				: -1;
	}
}
