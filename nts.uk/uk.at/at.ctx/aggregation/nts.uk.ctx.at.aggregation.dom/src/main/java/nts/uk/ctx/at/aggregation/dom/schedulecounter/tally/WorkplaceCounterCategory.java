package nts.uk.ctx.at.aggregation.dom.schedulecounter.tally;

import nts.arc.enums.EnumAdaptor;

/**
 * 職場計カテゴリ
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.職場計・個人計.職場計カテゴリ
 * @author dan_pv
 *
 */
public enum WorkplaceCounterCategory {

	/** 人件費・時間 */
	LABOR_COSTS_AND_TIME(0, "人件費・時間 "),

	/** 外部予算実績 */
	EXTERNAL_BUDGET(1, "外部予算実績"),

	/** 回数集計 */
	TIMES_COUNTING(2, "回数集計"),

	/** 就業時間帯別の利用人数 */
	WORKTIME_PEOPLE(3, "就業時間帯別の利用人数"),

	/** 時間帯人数 */
	TIMEZONE_PEOPLE(4, "時間帯人数"),

	/** 雇用人数 */
	EMPLOYMENT_PEOPLE(5, "雇用人数"),

	/** 分類人数 */
	CLASSIFICATION_PEOPLE(6, "分類人数"),

	/** 職位人数 */
	POSITION_PEOPLE(7, "職位人数");

	public int value;

	public String nameId;

	private WorkplaceCounterCategory(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	public static WorkplaceCounterCategory of(int value) {
		return EnumAdaptor.valueOf(value, WorkplaceCounterCategory.class);
	}

}
