package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting;

import java.util.Optional;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.clock.ClockHourMinute;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.BeforeAddCheckMethod;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.service.checkpostappaccept.PostAppAcceptLimit;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.service.checkpreappaccept.PreAppAcceptLimit;
import nts.uk.shr.com.time.AttendanceClock;

/**
 * refactor 4
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.申請設定.申請種類別設定.受付制限設定
 * @author Doan Duy Hung
 *
 */
@Getter
public class ReceptionRestrictionSetting {
	
	/**
	 * 残業申請事前の受付制限
	 */
	private Optional<OTAppBeforeAccepRestric> otAppBeforeAccepRestric;
	
	/**
	 * 事後の受付制限
	 */
	private AfterhandRestriction afterhandRestriction;
	
	/**
	 * 事前の受付制限
	 */
	private Optional<BeforehandRestriction> beforehandRestriction;
	
	/**
	 * 申請種類
	 */
	private ApplicationType appType;
	
	public ReceptionRestrictionSetting(OTAppBeforeAccepRestric otAppBeforeAccepRestric,
			AfterhandRestriction afterhandRestriction, BeforehandRestriction beforehandRestriction, ApplicationType appType) {
		this.otAppBeforeAccepRestric = Optional.ofNullable(otAppBeforeAccepRestric);
		this.afterhandRestriction = afterhandRestriction;
		this.beforehandRestriction = Optional.ofNullable(beforehandRestriction);
		this.appType = appType;
	}
	
	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.申請設定.申請種類別設定.アルゴリズム.事前申請がいつから受付可能か確認する.事前申請がいつから受付可能か確認する
	 * @param opOvertimeAppAtr Optional＜残業申請区分＞
	 * @return
	 */
	public PreAppAcceptLimit checkWhenPreAppCanBeAccepted(Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		// 「＠申請種類」をチェックする
		if(appType != ApplicationType.OVER_TIME_APPLICATION) {
			// 事前申請の受付制限．受付制限利用する = @事前の受付制限．利用する
			PreAppAcceptLimit preAppAcceptLimit = new PreAppAcceptLimit(beforehandRestriction.get().isToUse());
			// @事前の受付制限．利用する = true の場合
			if(beforehandRestriction.get().isToUse()) {
				// 事前申請の受付制限．受付可能年月日 = システム日付 + @事前の受付制限．日数
				preAppAcceptLimit.setOpAcceptableDate(Optional.of(GeneralDate.today().addDays(beforehandRestriction.get().getDateBeforehandRestrictions().value)));
			}
			// OUTPUTにセットして返す
			return preAppAcceptLimit;
		}
		// 「＠残業申請事前の受付制限．チェック方法」をチェックする
		if(otAppBeforeAccepRestric.get().getMethodCheck() == BeforeAddCheckMethod.CHECK_IN_DAY) {
			// 事前申請の受付制限．受付制限利用する = @残業申請事前の受付制限．利用する
			PreAppAcceptLimit preAppAcceptLimit = new PreAppAcceptLimit(otAppBeforeAccepRestric.get().isToUse());
			// @残業申請事前の受付制限．利用する = true の場合
			if(otAppBeforeAccepRestric.get().isToUse()) {
				// 事前申請の受付制限．受付可能年月日 = システム日付 + @残業申請事前の受付制限．日数
				preAppAcceptLimit.setOpAcceptableDate(Optional.of(GeneralDate.today().addDays(otAppBeforeAccepRestric.get().getDateBeforehandRestrictions().value)));
			}
			// OUTPUTにセットして返す
			return preAppAcceptLimit;
		}
		// 事前申請の受付制限．受付制限利用する = @残業申請事前の受付制限．利用する
		PreAppAcceptLimit preAppAcceptLimit = new PreAppAcceptLimit(otAppBeforeAccepRestric.get().isToUse());
		// @残業申請事前の受付制限．利用する = true の場合
		if(otAppBeforeAccepRestric.get().isToUse()) {
			// 事前申請の受付制限．受付可能年月日 = システム日付
			preAppAcceptLimit.setOpAcceptableDate(Optional.of(GeneralDate.today()));
			// INPUT．「残業申請区分」をチェックする
			switch (opOvertimeAppAtr.get()) {
				case EARLY_OVERTIME:
					// 事前申請の受付制限．受付可能時刻 = @残業申請事前の受付制限．時刻（早出残業）
					preAppAcceptLimit.setOpAvailableTime(otAppBeforeAccepRestric.get().getOpEarlyOvertime());
					break;
				case NORMAL_OVERTIME:
					// 事前申請の受付制限．受付可能時刻 = @残業申請事前の受付制限．時刻（通常残業）
					preAppAcceptLimit.setOpAvailableTime(otAppBeforeAccepRestric.get().getOpNormalOvertime());
					break;
				case EARLY_NORMAL_OVERTIME:
					// 事前申請の受付制限．受付可能時刻 = @残業申請事前の受付制限．時刻（早出残業・通常残業）
					preAppAcceptLimit.setOpAvailableTime(otAppBeforeAccepRestric.get().getOpEarlyNormalOvertime());
					break;
				default:
					// 事前申請の受付制限．受付可能時刻 = @残業申請事前の受付制限．時刻（複数回残業）
					preAppAcceptLimit.setOpAvailableTime(otAppBeforeAccepRestric.get().getOpMultipleOvertime());
					break;
			}
		}
		// OUTPUTにセットして返す
		return preAppAcceptLimit;
	}
	
	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.申請設定.申請種類別設定.アルゴリズム.事後申請がいつから受付可能か確認する.事後申請がいつから受付可能か確認する
	 * @return
	 */
	public PostAppAcceptLimit checkWhenPostAppCanBeAccepted() {
		// 「＠事後の受付制限．未来日許可しない」をチェックする
		if(afterhandRestriction.isAllowFutureDay()) {
			// 事後申請の受付制限．受付制限利用する = true
			PostAppAcceptLimit postAppAcceptLimit = new PostAppAcceptLimit(true);
			// 事後申請の受付制限．受付可能年月日 = システム日付
			postAppAcceptLimit.setOpAcceptableDate(Optional.of(GeneralDate.today()));
			// OUTPUTにセットして返す
			return postAppAcceptLimit;
		}
		// 事後申請の受付制限．受付制限利用する = false
		return new PostAppAcceptLimit(false);
	}
	
	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.申請設定.申請種類別設定.アルゴリズム.対象日が申請可能かを判定する.対象日が申請可能かを判定する
	 * @param appType 対象申請
	 * @param date 対象日
	 * @param overtimeAppAtr 残業区分
	 * @param advanceReceptionDate 事前受付日
	 * @param advanceReceptionHours 事前受付時分
	 * @return
	 */
	public boolean applyPossibleCheck(ApplicationType appType, GeneralDate date, OvertimeAppAtr overtimeAppAtr, 
			GeneralDate advanceReceptionDate, AttendanceClock advanceReceptionHours) {
		// Input「対象申請」をチェックする
		if(appType != ApplicationType.OVER_TIME_APPLICATION) {
			// INPUT．対象日とINPUT．事前受付日を比較する
			if(date.before(advanceReceptionDate)) {
				return true;
			} else {
				return false;
			}
		}
		// INPUT．事前受付時分をチェックする
		if(advanceReceptionHours==null) {
			// INPUT．対象日とINPUT．事前受付日を比較する
			if(date.before(advanceReceptionDate)) {
				return true;
			} else {
				return false;
			}
		}
		// INPUT．対象日とシステム日付を比較
		if(date.after(GeneralDate.today())) {
			return false;
		}
		// システム日時とINPUT．INPUT．事前受付時分を比較する
		if(ClockHourMinute.now().v() > advanceReceptionHours.v()) {
			return true;
		}
		return false;
	}
	
}
