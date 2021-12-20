package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting;

import lombok.AllArgsConstructor;
/**
 * @author thanhpv
 * @name 経過月数
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績参照設定.経過月数
 */
@AllArgsConstructor
public enum ElapsedMonths {
	
	CURRENT_MONTH(0, "当月"),

	ONE_MONTH_AGO(1, "1ヶ月前"),
	
	TOW_MONTH_AGO(2, "2ヶ月前"),
	
	THREE_MONTH_AGO(3, "3ヶ月前"),
	
	FOUR_MONTH_AGO(4, "4ヶ月前"),
	
	FIVE_MONTH_AGO(5, "5ヶ月前"),
	
	SIX_MONTH_AGO(6, "6ヶ月前");

	public final int value;
	public final String nameId;
}
