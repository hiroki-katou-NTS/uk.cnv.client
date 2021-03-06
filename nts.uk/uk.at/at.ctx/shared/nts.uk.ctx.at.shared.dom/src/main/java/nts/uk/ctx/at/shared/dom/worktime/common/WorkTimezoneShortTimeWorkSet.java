/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.childcareset.ShortTimeWorkGetRange;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FixedChangeAtr;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeDomainObject;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.ScreenMode;

/**
 * The Class WorkTimezoneShortTimeWorkSet.
 */
// 就業時間帯の短時間勤務設定
@Getter
@NoArgsConstructor
public class WorkTimezoneShortTimeWorkSet extends WorkTimeDomainObject implements Cloneable{

	/** The nurs timezone work use. */
	// 介護時間帯に勤務した場合に勤務として扱う
	private boolean nursTimezoneWorkUse;

	/** The employment time deduct. */
	// 就業時間から控除する
	private boolean employmentTimeDeduct;

	/** The child care work use. */
	// 育児時間帯に勤務した場合に勤務として扱う
	private boolean childCareWorkUse;

	/**
	 * Instantiates a new work timezone short time work set.
	 *
	 * @param nursTimezoneWorkUse
	 *            the nurs timezone work use
	 * @param employmentTimeDeduct
	 *            the employment time deduct
	 * @param childCareWorkUse
	 *            the child care work use
	 */
	public WorkTimezoneShortTimeWorkSet(boolean nursTimezoneWorkUse, boolean employmentTimeDeduct,
			boolean childCareWorkUse) {
		super();
		this.nursTimezoneWorkUse = nursTimezoneWorkUse;
		this.employmentTimeDeduct = employmentTimeDeduct;
		this.childCareWorkUse = childCareWorkUse;
	}

	/**
	 * Instantiates a new work timezone short time work set.
	 *
	 * @param memento
	 *            the memento
	 */
	public WorkTimezoneShortTimeWorkSet(WorkTimezoneShortTimeWorkSetGetMemento memento) {
		this.nursTimezoneWorkUse = memento.getNursTimezoneWorkUse();
		this.employmentTimeDeduct = memento.getEmploymentTimeDeduct();
		this.childCareWorkUse = memento.getChildCareWorkUse();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(WorkTimezoneShortTimeWorkSetSetMemento memento) {
		memento.setNursTimezoneWorkUse(this.nursTimezoneWorkUse);
		memento.setEmploymentTimeDeduct(this.employmentTimeDeduct);
		memento.setChildCareWorkUse(this.childCareWorkUse);
	}

	/**
	 * Correct data.
	 *
	 * @param screenMode
	 *            the screen mode
	 */
	public void correctData(ScreenMode screenMode) {
		if (ScreenMode.SIMPLE.equals(screenMode)) {
			this.nursTimezoneWorkUse = false;
			this.childCareWorkUse = false;
		}
	}
	
	/**
	 * Correct data for fixedChangeAtr.
	 *
	 * @param fixedChangeAtr
	 */
	public void correctDataForFixedChange(FixedChangeAtr fixedChangeAtr) {
		if (fixedChangeAtr != FixedChangeAtr.NOT_CHANGE) {
			this.nursTimezoneWorkUse = true;
			this.childCareWorkUse = true;
		}
	}
	
	/**
	 * Restore data.
	 *
	 * @param clone
	 */
	public void restoreWorkTimezoneShortTimeWorkSet(WorkTimezoneShortTimeWorkSet clone) {
		this.nursTimezoneWorkUse = clone.childCareWorkUse;
		this.employmentTimeDeduct = clone.employmentTimeDeduct;
		this.childCareWorkUse = clone.childCareWorkUse;
	}
	
	@Override
	public WorkTimezoneShortTimeWorkSet clone() {
		WorkTimezoneShortTimeWorkSet cloned = new WorkTimezoneShortTimeWorkSet();
		try {
			cloned.nursTimezoneWorkUse = this.nursTimezoneWorkUse ? true : false ;
			cloned.employmentTimeDeduct = this.employmentTimeDeduct ? true : false ;
			cloned.childCareWorkUse = this.childCareWorkUse ? true : false ;
		}
		catch (Exception e){
			throw new RuntimeException("WorkTimezoneShortTimeWorkSet clone error.");
		}
		return cloned;
	}
	
	/**
	 * 勤務扱いによる取得範囲の判断
	 * @param childCareAtr 育児介護区分
	 * @return 短時間勤務取得範囲
	 */
	public ShortTimeWorkGetRange checkGetRangeByWorkUse(ChildCareAtr childCareAtr){
		
		ShortTimeWorkGetRange result = ShortTimeWorkGetRange.NOT_GET;
		
		// 育児介護区分を確認する
		boolean isWorkUse = false;		// 勤務とするかどうか
		if (childCareAtr == ChildCareAtr.CHILD_CARE){
			// 「育児時間帯に～」を確認する
			isWorkUse = this.childCareWorkUse;
		}
		else if (childCareAtr == ChildCareAtr.CARE){
			// 「介護時間帯に～」を確認する
			isWorkUse = this.nursTimezoneWorkUse;
		}
		
		if (isWorkUse){
			// 勤務とする
			// 結果　←　「出退勤と重複する時間帯を除く」
			result = ShortTimeWorkGetRange.WITHOUT_ATTENDANCE_LEAVE;
		}
		else{
			// 勤務としない
			// 結果　←　「そのまま取得する」
			result = ShortTimeWorkGetRange.NORMAL_GET;
		}
		// 結果を返す
		return result;
	}

	/**
	 * デフォルト設定のインスタンスを生成する
	 * @return 就業時間帯の短時間勤務設定
	 */
	public static WorkTimezoneShortTimeWorkSet generateDefault(){
		WorkTimezoneShortTimeWorkSet domain = new WorkTimezoneShortTimeWorkSet(false, false, false);
		return domain;
	}
}
