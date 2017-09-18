package nts.uk.ctx.at.request.dom.application.stamp;

import nts.uk.ctx.at.request.dom.application.stamp.output.AppStampSetOutput;

/**
 * 
 * @author Doan Duy Hung
 *
 */
public interface AppStampCommonDomainService {
	
	// 打刻申請設定の取得
	public AppStampSetOutput appStampSet(String companyID); 
	
	// 申請理由の生成と検査
	public void appReasonCheck(String titleReason, String detailReason, AppStamp appStamp);
	
	public void validateReason(AppStamp appStamp);
}
