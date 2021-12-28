package nts.uk.ctx.exio.dom.exo.category;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.exio.dom.exo.execlog.RoleType;
import nts.uk.shr.com.permit.DescriptionOfAvailabilityPermissionBase;

/**
 * 外部出力カテゴリ
 */
@Getter
public class ExOutCtg extends DescriptionOfAvailabilityPermissionBase {

	/**
	 * オフィスヘルパシステム区分
	 */
	private SystemUsability officeHelperSysAtr;
	/**
	 * カテゴリID
	 */
	private CategoryCd categoryId;
	/**
	 * カテゴリ名
	 */
	private CategoryName categoryName;

	/**
	 * カテゴリ設定
	 */
	private CategorySetting categorySet;

	/**
	 * 人事システム区分
	 */
	private SystemUsability personSysAtr;

	/**
	 * 勤怠システム区分
	 */
	private SystemUsability attendanceSysAtr;
	/**
	 * 外部出期間区分: 外部出期間区分
	 */
	private OutingPeriodClassific outingPeriodClassific;
	/**
	 * 給与システム区分
	 */
	private SystemUsability payrollSysAtr;

	/**
	 * 締め使う区分
	 */
	private ClassificationToUse classificationToUse;

	public ExOutCtg(
			int categoryId,
			int officeHelperSysAtr,
			String categoryName,
			int categorySet,
			int personSysAtr,
			int attendanceSysAtr,
			int payrollSysAtr,
			int functionNo,
			String functionName,
			String explanation,
			int displayOrder,
			boolean defaultValue,
			int outingPeriodClassific,
			int classificationToUse) {
		super(functionNo, functionName, explanation, displayOrder, defaultValue);
		this.categoryId = new CategoryCd(categoryId);
		this.officeHelperSysAtr = EnumAdaptor.valueOf(officeHelperSysAtr, SystemUsability.class);
		this.categoryName = new CategoryName(categoryName);
		this.categorySet = EnumAdaptor.valueOf(categorySet, CategorySetting.class);
		this.personSysAtr = EnumAdaptor.valueOf(personSysAtr, SystemUsability.class);
		this.attendanceSysAtr = EnumAdaptor.valueOf(attendanceSysAtr, SystemUsability.class);
		this.payrollSysAtr = EnumAdaptor.valueOf(payrollSysAtr, SystemUsability.class);
		this.outingPeriodClassific = EnumAdaptor.valueOf(outingPeriodClassific,OutingPeriodClassific.class);
		this.classificationToUse = EnumAdaptor.valueOf(classificationToUse,ClassificationToUse.class);
	}

	public SystemUsability getSysUsability(int roleType) {
		switch (roleType){
			case 3:  // 就業
				return attendanceSysAtr;
			case 4:  // 給与
				return payrollSysAtr;
			case 5:  // 人事
				return personSysAtr;
			case 6:  // OH
				return officeHelperSysAtr;
			default:
				return SystemUsability.UNVAILABLE;
		}
	}
}
