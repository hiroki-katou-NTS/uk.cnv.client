package nts.uk.ctx.at.record.app.find.stamp.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto.StampRecordDto;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;

/**
 * 
 * @author phongtq
 *
 */
@Data
@AllArgsConstructor
public class ButtonSettingsDto {
	/** ボタン位置NO */
	private int buttonPositionNo;

	/** ボタンの表示設定 */
	private ButtonDisSetDto buttonDisSet;

	/** ボタン種類 */
	private ButtonTypeDto buttonType;

	/** 使用区分 */
	private int usrArt;

	/** 音声使用方法 */
	private int audioType;

	private int buttonValueType;
	
	private Integer supportWplSet;

	public static ButtonSettingsDto fromDomain(ButtonSettings domain) {
		ButtonTypeDto buttonType = ButtonTypeDto.fromDomain(domain.getButtonType());
		StampTypeDto stampType = buttonType.getStampType();
		return new ButtonSettingsDto(domain.getButtonPositionNo().v(),
				ButtonDisSetDto.fromDomain(domain.getButtonDisSet()), buttonType, domain.getUsrArt().value,
				domain.getAudioType().value, toButtonValueType(stampType), domain.getSupportWplSet().map(c->c.value).orElse(null));

	}

	public static int toButtonValueType(StampTypeDto stampType) {

		if (stampType == null) {
			return ButtonType.RESERVATION_SYSTEM.value;
		}
		int correctTimeStampValue = StampRecordDto.getCorrectTimeStampValue(stampType.getChangeHalfDay(),
				stampType.getGoOutArt(), stampType.getSetPreClockArt(), stampType.getChangeClockArt(),
				stampType.getChangeCalArt());
		switch (EnumAdaptor.valueOf(correctTimeStampValue, ContentsStampType.class)) {
		// 1 2 3 4 10 12 14 16 17 18
		case WORK:
		case WORK_STRAIGHT:
		case WORK_EARLY:
		case WORK_BREAK:
		case GETTING_STARTED:
		case TEMPORARY_WORK:
		case START_SUPPORT:
		case WORK_SUPPORT:
		case START_SUPPORT_EARLY_APPEARANCE:
		case START_SUPPORT_BREAK:

			return ButtonType.GOING_TO_WORK.value;
		// 19 20
		case RESERVATION:
		case CANCEL_RESERVATION:
			return ButtonType.RESERVATION_SYSTEM.value;
		// 5 6 7 11 13 15
		case DEPARTURE:
		case DEPARTURE_BOUNCE:
		case DEPARTURE_OVERTIME:
		case DEPAR:
		case TEMPORARY_LEAVING:
		case END_SUPPORT:
			return ButtonType.WORKING_OUT.value;
		// 8
		case OUT:
			return ButtonType.GO_OUT.value;
		// 9
		case RETURN:
			return ButtonType.RETURN.value;
		default:
			return -1;
		}
	}
}
