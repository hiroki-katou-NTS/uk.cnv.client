package nts.uk.ctx.at.request.app.find.application.applicationlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.request.dom.application.applist.service.param.AppLstApprovalLstDispSet;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppLstApprovalLstDispSetDto {
	
	/**
	 * 開始日表示
	 */
	private String startDateDisp;
	
	/**
	 * 事前事後区分表示
	 */
	private int prePostAtrDisp;
	
	/**
	 * 終了日表示
	 */
	private String endDateDisp;
	
	/**
	 * 所属職場名表示
	 */
	private int workplaceNameDisp;
	
	/**
	 * 申請対象日に対して警告表示
	 */
	private int appDateWarningDisp;

	/**
	 * 承認機能を利用する (申請⑧EA4134) todo
	 */
	private int useApprovalFunction;
	

	public static AppLstApprovalLstDispSetDto fromDomain(AppLstApprovalLstDispSet displaySet) {
		return new AppLstApprovalLstDispSetDto(
				displaySet.getStartDateDisp() == null ? null : displaySet.getStartDateDisp().toString(), 
				displaySet.getPrePostAtrDisp(), 
				displaySet.getEndDateDisp() == null ? null : displaySet.getEndDateDisp().toString(), 
				displaySet.getWorkplaceNameDisp(), 
				displaySet.getAppDateWarningDisp(),
				displaySet.getUseApprovalFunction()
		);
	}
	
}
