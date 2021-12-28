package nts.uk.screen.at.app.ktgwidget.ktg004;

import java.util.List;

import lombok.Value;

@Value
public class GetAttendanceInforParam {

	/**
	 * 勤務状況の詳細設定
	 */
	private List<ItemsSettingDto> itemsSetting;
	
	/**
	 * 現在の締め期間
	 */
	private CurrentClosingPeriod currentClosingPeriod;
}
