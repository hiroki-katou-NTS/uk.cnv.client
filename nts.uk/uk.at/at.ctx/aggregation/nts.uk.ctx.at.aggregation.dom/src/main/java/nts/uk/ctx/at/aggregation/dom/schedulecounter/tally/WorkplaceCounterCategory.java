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
	LABOR_COSTS_AND_TIME(0, "Enum_WorkplaceCounterCategory_LaborCostsAndTime"),

	/** 外部予算実績 */
	EXTERNAL_BUDGET(1, "Enum_WorkplaceCounterCategory_ExternalBudget"),

	/** 回数集計 */
	TIMES_COUNTING(2, "Enum_WorkplaceCounterCategory_TimesCounting"),

	/** 就業時間帯別の利用人数 */
	WORKTIME_PEOPLE(3, "Enum_WorkplaceCounterCategory_WorktimePeople"),

	/** 時間帯人数 */
	TIMEZONE_PEOPLE(4, "Enum_WorkplaceCounterCategory_TimezonePeople"),

	/** 雇用人数 */
	EMPLOYMENT_PEOPLE(5, "Enum_WorkplaceCounterCategory_EmploymentPeople"),

	/** 分類人数 */
	CLASSIFICATION_PEOPLE(6, "Enum_WorkplaceCounterCategory_ClassificationPeople"),

	/** 職位人数 */
	POSITION_PEOPLE(7, "Enum_WorkplaceCounterCategory_PositionPeople");

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
