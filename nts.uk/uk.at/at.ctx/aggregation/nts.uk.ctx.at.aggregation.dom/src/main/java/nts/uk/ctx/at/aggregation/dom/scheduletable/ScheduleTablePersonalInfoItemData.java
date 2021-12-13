package nts.uk.ctx.at.aggregation.dom.scheduletable;

import lombok.Value;

/**
 * スケジュール表の個人情報項目データ
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の個人情報項目データ
 * @author dan_pv
 */
@Value
public class ScheduleTablePersonalInfoItemData {

	/**
	 * コード
	 */
	private final String code;
	
	/**
	 * 名称
	 */
	private final String name;
	
}
