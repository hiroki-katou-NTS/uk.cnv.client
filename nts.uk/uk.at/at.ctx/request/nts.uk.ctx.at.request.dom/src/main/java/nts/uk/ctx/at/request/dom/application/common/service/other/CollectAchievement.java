package nts.uk.ctx.at.request.dom.application.common.service.other;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;

/**
 * 13.実績を取得する
 * @author Doan Duy Hung
 *
 */
public interface CollectAchievement {
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム.13.実績を取得する(getAchievement).実績の取得
	 * @param companyID 会社ID
	 * @param applicantID 申請者の社員ID
	 * @param appDate 申請日
	 * @return
	 */
	public ActualContentDisplay getAchievement(String companyID, String applicantID, GeneralDate appDate);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム.実績内容の取得.実績内容の取得
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param dateLst 申請対象日リスト<Optional>
	 * @param appType 申請種類
	 * @return
	 */
	public List<ActualContentDisplay> getAchievementContents(String companyID, String employeeID, List<GeneralDate> dateLst, ApplicationType appType);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム.01-09_事前申請を取得.事前内容の取得.事前内容の取得
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param dateLst 申請対象日リスト<Optional>
	 * @param appType 申請種類
	 * @return
	 */
	public List<PreAppContentDisplay> getPreAppContents(String companyID, String employeeID, List<GeneralDate> dateLst, ApplicationType appType, Optional<OvertimeAppAtr> opOvertimeAppAtr);
	
}
