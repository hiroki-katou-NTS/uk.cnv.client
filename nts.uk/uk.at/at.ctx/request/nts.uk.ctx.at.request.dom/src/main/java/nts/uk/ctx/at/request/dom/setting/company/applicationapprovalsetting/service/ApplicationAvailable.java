package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApplicationUseSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApprovalFunctionSet;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;

/**
 * DS: 申請を利用できるか
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.アルゴリズム.勤務種類から促す申請を判断する
 * 
 * @author chungnt
 *
 */

public class ApplicationAvailable {

	/**
	 * 
	 * @param require
	 * @param cid
	 *            会社ID
	 * @param sid
	 *            社員ID
	 * @param date
	 *            申請日
	 * @param appType
	 *            申請種類
	 */
	public static boolean get(Require require, String cid, String sid, GeneralDate date, ApplicationTypeShare appType) {

		// $申請設定 = require.申請設定を取得する(会社ID)
		Optional<ApplicationSetting> applicationSetting = require.findByCompanyId(cid);

		// $基準日 = 申請日
		GeneralDate resultDate = date;

		// if $申請設定.isPresent()
		if (applicationSetting.isPresent()) {
			// $基準日 = $申請設定.基準日として扱う日の取得(申請日)
			resultDate = applicationSetting.get().getBaseDate(Optional.of(date));
		}

		// $申請承認設定 = require.申請承認設定情報の取得を取得する(会社ID,社員ID,$基準日,申請種類)
		ApprovalFunctionSet approvalFunctionSet = require.getApprovalFunctionSet(cid, sid, resultDate, appType);

		// if $申請承認設定.isPresent()
		// return false
		if (approvalFunctionSet == null) {
			return false;
		}

		// $.利用区分 = $申請承認設定.申請利用設定：$.申請種類 == INPUT「申請種類」
		// map $.利用区分
		List<ApplicationUseSetting> applicationUseSettings = approvalFunctionSet.getAppUseSetLst().stream()
				.filter(m -> m.getAppType().value == appType.value).collect(Collectors.toList());

		// return $利用区分 == 利用する ? true ： false
		return applicationUseSettings.isEmpty() ? false : true;
	}

	public static interface Require {
		/**
		 * [R-1] 申請設定を取得する
		 */
		Optional<ApplicationSetting> findByCompanyId(String companyId);

		/**
		 * [R-2] 申請承認設定情報の取得を取得する
		 */
		ApprovalFunctionSet getApprovalFunctionSet(String companyID, String employeeID, GeneralDate date,
				ApplicationTypeShare targetApp);

	}

}
