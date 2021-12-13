package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting;

import lombok.AllArgsConstructor;
/**
 * @author thanhpv
 * @name 参照範囲
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績参照設定.参照範囲
 */
@AllArgsConstructor
public enum ReferenceRange {
	
	ALL_EMPLOYEES(0, "全社員"),

	SAME_AS_EMPLOYEE_REFERENCE_RANGE_OF_ROLE(1, "ロールの社員参照範囲と同じ");

	public final int value;
	public final String nameId;
}
