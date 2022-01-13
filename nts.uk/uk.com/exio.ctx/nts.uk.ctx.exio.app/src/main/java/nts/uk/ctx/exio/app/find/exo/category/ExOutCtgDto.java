package nts.uk.ctx.exio.app.find.exo.category;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.exio.dom.exo.category.ExOutCtg;

/**
 * 外部出力カテゴリ
 */
@AllArgsConstructor
@Value
public class ExOutCtgDto {

	/**
	 * カテゴリID
	 */
	private int categoryId;

	/**
	 * オフィスヘルパシステム区分
	 */
	private int officeHelperSysAtr;

	/**
	 * カテゴリ名
	 */
	private String categoryName;

	/**
	 * カテゴリ設定
	 */
	private int categorySet;

	/**
	 * 人事システム区分
	 */
	private int personSysAtr;

	/**
	 * 勤怠システム区分
	 */
	private int attendanceSysAtr;

	/**
	 * 給与システム区分
	 */
	private int payrollSysAtr;

	/**
	*
	*/
	private int functionNo;

	/**
	*
	*/
	private String functionName;

	/**
	*
	*/
	private String explanation;

	/**
	*
	*/
	private int displayOrder;

	/**
	*
	*/
	private boolean defaultValue;


    private int outingPeriodClassific;


	private int classificationToUse;


	public static ExOutCtgDto fromDomain(ExOutCtg domain) {
		return new ExOutCtgDto(
		        domain.getCategoryId().v(),
                domain.getOfficeHelperSysAtr().value,
				domain.getCategoryName().v(),
                domain.getCategorySet().value,
                domain.getPersonSysAtr().value,
				domain.getAttendanceSysAtr().value,
                domain.getPayrollSysAtr().value,
                domain.getFunctionNo(),
				domain.getName(),
                domain.getExplanation(),
                domain.getDisplayOrder(),
                domain.getDefaultValue(),
                domain.getOutingPeriodClassific().value,
                domain.getClassificationToUse().value);
	}

}
