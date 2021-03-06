package nts.uk.ctx.at.request.app.find.application.holidayshipment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.dto.HolidayShipmentDto;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.HolidayShipmentScreenAFinder;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;

@Stateless
public class HolidayShipmentScreenCFinder {

	@Inject
	private HolidayShipmentScreenAFinder aFinder;

	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;

//	@Inject
//	private WithDrawalReqSetRepository withDrawRepo;

//	@Inject
//	private ApplicationReasonRepository appResonRepo;

	public HolidayShipmentDto startPage(String sid, GeneralDate baseDate, int uiType) {
//		String companyID = AppContexts.user().companyId();
//		String employeeID = sid != null ? sid : AppContexts.user().employeeId();
//		ApplicationType appType = ApplicationType.COMPLEMENT_LEAVE_APPLICATION;
//		AppCommonSettingOutput appCommonSettingOutput = aFinder.getAppCommonSet(companyID, employeeID, baseDate);
//		// アルゴリズム「起動前共通処理（新規）」を実行する
//		HolidayShipmentDto output = aFinder.commonProcessBeforeStart(appType, companyID, employeeID, baseDate,
//				appCommonSettingOutput);
//		// アルゴリズム「事前事後区分の判断」を実行する
//		output.setPreOrPostType(
//				otherCommonAlgorithm.judgmentPrePostAtr(appType, baseDate, uiType == 0 ? true : false).value);
//
//		// アルゴリズム「振休振出申請設定の取得」を実行する
//		Optional<WithDrawalReqSet> withDrawalReqSetOpt = withDrawRepo.getWithDrawalReqSet();
//		if (withDrawalReqSetOpt.isPresent()) {
//			output.setDrawalReqSet(WithDrawalReqSetDto.fromDomain(withDrawalReqSetOpt.get()));
//		}
//
//		// アルゴリズム「振休振出申請定型理由の取得」を実行する
////		output.setAppReasonComboItems(appResonRepo.getReasonByAppType(companyID, appType.value).stream()
////				.map(x -> ApplicationReasonDto.convertToDto(x)).collect(Collectors.toList()));
//
//		return output;
		return null;
	}

}
