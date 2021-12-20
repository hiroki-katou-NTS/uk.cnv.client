package nts.uk.ctx.at.request.dom.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;

public interface ApplicationRepository {
	
	public List<Application> findByListID(String companyID, List<String> listAppID);
	
	/**
	 * getApplicationBySIDs
	 * @param employeeID
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Application> getApplicationBySIDs(List<String> employeeID,GeneralDate startDate, GeneralDate endDate);
	
	/**
	 * refactor 4
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.申請.アルゴリズム.承認一覧の申請を取得.承認一覧の申請を取得
	 * @author hoatt
	 * get List Application
	 * Phuc vu CMM045
	 * @param companyId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Application> getListAppModeApprCMM045(String companyID, DatePeriod period, List<String> lstAppId,
			boolean unapprovalStatus, boolean approvalStatus, boolean denialStatus, boolean agentApprovalStatus, 
			boolean remandStatus, boolean cancelStatus, List<Integer> lstType, List<PrePostAtr> prePostAtrLst, 
			List<String> employeeIDLst, List<StampRequestMode> stampRequestModeLst, List<OvertimeAppAtr> overtimeAppAtrLst);
	
	public List<Application> getByListRefStatus(String companyID, String employeeID ,GeneralDate startDate, GeneralDate endDate , List<Integer> listReflecInfor  );
	
	public List<Application> getListLateOrLeaveEarly(String companyID, String employeeID, GeneralDate startDate, GeneralDate endDate);
	/**
	 * 検索
	 * @param sid
	 * @param dateData　申請日　期間
	 * @param reflect　反映情報.実績反映状態
	 * @param appType　申請種類
	 * @return
	 */
	public List<Application> getByPeriodReflectType(String sid, DatePeriod dateData, List<Integer> reflect, List<Integer> appType);
	
	/**
	 * 検索
	 * @param sid
	 * @param dateData　申請日　
	 * @param reflect　反映情報.実績反映状態
	 * @param appType　申請種類
	 * @return
	 */
	public List<Application> getByPeriodReflectType(String sid, GeneralDate baseDate, List<Integer> reflect, List<Integer> appType);
	
	/**
	 * getListAppByType
	 * sort：申請日（ASC）、入力日（DESC）
	 * @param companyId
	 * @param sID: 申請者＝パラメータ．社員ID
	 * @param appDate: パラメータ．期間．開始日＜＝　申請日　＜＝パラメータ．期間．終了日
	 * @param prePostAtr: 事前事後区分＝事後
	 * @param appType: 休日出勤申請
	 * @param lstRef: 実績反映状態＝未承認 or 反映待ち
	 * @return
	 */
	public List<Application> getListAppByType(String companyId, String sID, GeneralDate startDate, GeneralDate endDate, 
			int prePostAtr, int appType, List<Integer> lstRef);
	
	/**
	 * 反映の申請
	 * @param sid 社員ID
	 * @param dateData　期間
	 * @param recordStatus　実績反映状態
	 * @param scheStatus　実績反映状態
	 * @param appType 申請種類
	 * @return
	 */
	public List<Application> getAppForReflect(String sid, DatePeriod dateData, List<Integer> recordStatus,
			List<Integer> scheStatus, List<Integer> appType);
	/**
	 * 検索
	 * @param sid
	 * @param dateData　申請日 リスト
	 * @param reflect　反映情報.実績反映状態
	 * @param appType　申請種類
	 * @return
	 */
	public List<Application> getByListDateReflectType(String sid, List<GeneralDate> dateData, List<Integer> reflect, List<Integer> appType);
	/**
	 * 
	 * @param sid
	 * @param period
	 * @param reflect
	 * @param appType
	 * @return
	 */
	public List<Application> getByListDateReflectType(String sid, DatePeriod period, List<Integer> reflect, List<Integer> appType);
	/**
	 * 
	 * @param companyId
	 * @param configName
	 * @param subName
	 * @return
	 */
	public Map<String, Integer> getParamCMMS45(String companyId, String configName, List<String> subName);
	
	// refactor 4
	
	public Optional<Application> findByID(String companyID, String appID);
	
	public void insert(Application application);
	
	public void update(Application application);
	
	public void remove(String appID);
	
	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.アルゴリズム.申請IDを使用して申請一覧を取得する.申請IDを使用して申請一覧を取得する
	 * @param appID
	 * @return
	 */
	public Optional<Application> findByID(String appID);
	
	public List<Application> getByAppTypeList(List<String> employeeLst, GeneralDate startDate, GeneralDate endDate, List<ApplicationType> appTypeLst, 
			List<PrePostAtr> prePostAtrLst, List<StampRequestMode> stampRequestModeLst, List<OvertimeAppAtr> overtimeAppAtrLst);

	public List<Application> getAppForKAF008(String sID, GeneralDate startDate, GeneralDate endDate);

	/**
	* 申請を取得	(反映状態="反映済み",対象日=ループ中の申請日)
	 * @param sid
	 * @param appDate
	 * @return
	 */
	public List<Application> getAppReflected(String sid, GeneralDate appDate);
	
	//申請を取得する
	// 期間に一致する申請を取得する
	public List<Application> getApplication(PrePostAtr prePostAtr, GeneralDateTime inputDate, GeneralDate appDate,
			ApplicationType appType, String employeeID);
	
	public List<Application> getApprSttByEmpPeriod(String employeeID, DatePeriod period);
	
	public Optional<String> getNewestPreAppIDByEmpDate(String employeeID, GeneralDate date, ApplicationType appType);
	
	public List<Application> findByIDLst(List<String> appIDLst);

	// 期間に一致する申請を取得する
	public List<Application> getAllApplication(List<String> sID, DatePeriod period);

	/**
	 * 申請情報を取得する(社員IDリスト, 期間, 反映状態リスト)
	 * 
	 * @param sids            社員IDリスト
	 * @param datePeriod      期間
	 * @param listReflecInfor 反映状態リスト
	 * @return Map<String, List<Application_New>> Map<社員ID、List<申請>>
	 */
	public Map<String, List<Application>> getMapListApplicationNew(List<String> sids, DatePeriod datePeriod,
			List<Integer> listReflecInfor);
	
	// 申請データを取得する
	/**
	 * 
	 * @param employeeID 申請者
	 * @param appType 申請種類
	 * @param appDate 申請日
	 * @param prePostAtr 事前事後区分
	 * @return
	 */
	public List<Application> getAllApplicationByAppTypeAndPrePostAtr(String employeeID, int appType, GeneralDate appDate, int prePostAtr);
		
}
