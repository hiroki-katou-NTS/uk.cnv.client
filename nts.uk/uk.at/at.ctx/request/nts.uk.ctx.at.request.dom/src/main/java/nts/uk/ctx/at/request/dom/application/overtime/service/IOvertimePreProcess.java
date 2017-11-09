package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.AppCommonSettingOutput;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.overtimeinstruct.OverTimeInstruct;
import nts.uk.ctx.at.request.dom.setting.applicationreason.ApplicationReason;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeRestAppCommonSetting;
import nts.uk.ctx.at.request.dom.setting.company.divergencereason.DivergenceReason;
import nts.uk.ctx.at.request.dom.setting.request.application.apptypediscretesetting.AppTypeDiscreteSetting;
import nts.uk.ctx.at.request.dom.setting.requestofeach.RequestAppDetailSetting;

/**
 * 01_初期データ取得
 * @author Doan Duy Hung
 *
 */
public interface IOvertimePreProcess {
	/**
	 * 01-01_残業通知情報を取得
	 * @param appCommonSettingOutput
	 * @return
	 */
	public OverTimeInstruct getOvertimeInstruct(AppCommonSettingOutput appCommonSettingOutput,String appDate,String employeeID);
	
	/**
	 * 01-03_残業枠を取得
	 * @param overtimeAtr
	 */
	public void getOvertimeHours(int overtimeAtr);
	
	/**
	 * 01-04_加給時間を取得
	 * @param employeeID
	 * @param overtimeRestAppCommonSet
	 * @param appDate
	 */
	public void getBonusTime(String employeeID,Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet, String appDate);
	/**
	 * 01-05_申請定型理由を取得
	 * @param companyID
	 * @param appType
	 * @return
	 */
	public List<ApplicationReason> getApplicationReasonType(String companyID,int appType, Optional<AppTypeDiscreteSetting> appTypeDiscreteSetting);
	
	/**
	 * 01-06_申請理由を取得
	 * @param appTypeDiscreteSetting
	 * @return
	 */
	public boolean displayAppReasonContentFlg(Optional<AppTypeDiscreteSetting> appTypeDiscreteSetting);
	
	/**
	 * 01-07_乖離理由を取得
	 * @param overtimeRestAppCommonSet
	 * @return
	 */
	public boolean displayDivergenceReasonInput(Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet);
	
	/**
	 * 01-08_乖離定型理由を取得
	 * @param companyID
	 * @param appType
	 * @param overtimeRestAppCommonSet
	 * @return
	 */
	public List<DivergenceReason> getDivergenceReasonForm(String companyID,int appType, Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet);
	/**
	 * 01-09_事前申請を取得
	 * @param employeeId
	 * @param overtimeRestAppCommonSet
	 * @param appDate
	 * @return
	 */
	public AppOverTime getPreApplication(String employeeId, Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet,String appDate, int prePostAtr);
	
	/**
	 * 01-13_事前事後区分を取得
	 * @param companyID
	 * @param applicationDto
	 * @param result
	 * @param uiType
	 */
	public DisplayPrePost getDisplayPrePost(String companyID,int uiType,String appDate);
	
	/**
	 * 01-14_勤務時間取得
	 * @param companyID
	 * @param employeeID
	 * @param appDate
	 * @param requestAppDetailSetting
	 */
	public void getWorkingHours(String companyID,String employeeID,String appDate,Optional<RequestAppDetailSetting> requestAppDetailSetting);
	/**
	 *  01-17_休憩時間取得
	 * @param requestAppDetailSetting
	 * @return
	 */
	public boolean getRestTime(Optional<RequestAppDetailSetting> requestAppDetailSetting);
	
	
}
