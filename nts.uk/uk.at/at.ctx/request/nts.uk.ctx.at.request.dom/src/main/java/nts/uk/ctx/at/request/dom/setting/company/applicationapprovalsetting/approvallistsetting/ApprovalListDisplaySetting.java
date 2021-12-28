package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * refactor 4
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.承認一覧設定.承認一覧表示設定
 * @author Doan Duy Hung
 *
 */
@Getter
@NoArgsConstructor
public class ApprovalListDisplaySetting {
	
	/**
	 * 会社ID
	 */
	private String companyID;
	
	/**
	 * 申請理由
	 */
	private DisplayAtr appReasonDisAtr;
	
	/**
	 * 事前申請の超過メッセージ
	 */
	private DisplayAtr advanceExcessMessDisAtr;
	
	/**
	 * 実績超過メッセージ
	 */
	private DisplayAtr actualExcessMessDisAtr;
	
	/**
	 * 申請対象日に対して警告表示
	 */
	private WeekNumberDays warningDateDisAtr;
	
	/**
	 * 所属職場名表示
	 */
	private NotUseAtr displayWorkPlaceName;

	/**
	 * 承認機能を利用する
	 */
	private NotUseAtr useApprovalFunction;
	
	public ApprovalListDisplaySetting(String companyID, DisplayAtr appReasonDisAtr, DisplayAtr advanceExcessMessDisAtr,
			DisplayAtr actualExcessMessDisAtr, WeekNumberDays warningDateDisAtr, NotUseAtr displayWorkPlaceName, NotUseAtr useApprovalFunction) {
		this.companyID = companyID;
		this.appReasonDisAtr = appReasonDisAtr;
		this.advanceExcessMessDisAtr = advanceExcessMessDisAtr;
		this.actualExcessMessDisAtr = actualExcessMessDisAtr;
		this.warningDateDisAtr = warningDateDisAtr;
		this.displayWorkPlaceName = displayWorkPlaceName;
		this.useApprovalFunction = useApprovalFunction;
	}

	public static ApprovalListDisplaySetting create(String companyID, int appReasonDispAtr, int preExcessAtr, int atdExcessAtr, int warningDays, int dispWorkplace, int useApprovalFunction) {
		return new ApprovalListDisplaySetting(
				companyID,
				EnumAdaptor.valueOf(appReasonDispAtr, DisplayAtr.class),
				EnumAdaptor.valueOf(preExcessAtr, DisplayAtr.class),
				EnumAdaptor.valueOf(atdExcessAtr, DisplayAtr.class),
				new WeekNumberDays(warningDays),
				EnumAdaptor.valueOf(dispWorkplace, NotUseAtr.class),
				EnumAdaptor.valueOf(useApprovalFunction, NotUseAtr.class)
		);
	}

}
