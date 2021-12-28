package nts.uk.ctx.at.request.dom.application.applist.service.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * refactor 4
 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.パラメータ.申請一覧承認一覧表示設定
 * @author Doan Duy Hung
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppLstApprovalLstDispSet {

	/**
	 * 開始日表示
	 */
	private GeneralDate startDateDisp;

	/**
	 * 事前事後区分表示
	 */
	private int prePostAtrDisp;

	/**
	 * 終了日表示
	 */
	private GeneralDate endDateDisp;

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
	public AppLstApprovalLstDispSet(GeneralDate startDateDisp,
									int prePostAtrDisp,
									GeneralDate endDateDisp,
									int workplaceNameDisp,
									int appDateWarningDisp) {
		this.startDateDisp = startDateDisp;
		this.prePostAtrDisp = prePostAtrDisp;
		this.endDateDisp = endDateDisp;
		this.workplaceNameDisp = workplaceNameDisp;
		this.appDateWarningDisp = appDateWarningDisp;
	}

}
