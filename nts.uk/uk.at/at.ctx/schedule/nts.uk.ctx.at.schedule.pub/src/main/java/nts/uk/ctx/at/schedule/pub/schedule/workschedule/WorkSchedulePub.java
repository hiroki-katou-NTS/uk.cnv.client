package nts.uk.ctx.at.schedule.pub.schedule.workschedule;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.pub.schedule.basicschedule.ScWorkScheduleExport_New;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.Export.勤務予定
 * 勤務予定
 * @author tutk
 *
 */
public interface WorkSchedulePub {
	
	public Optional<WorkScheduleExport> get(String employeeID , GeneralDate ymd);

	/**
	 * 社員ID(List)、期間を設定して勤務予定を取得する
	 */
	public List<WorkScheduleExport> getList(List<String> sids, DatePeriod period);
	
	/**
	 * 取得する
	 * @param employeeIds 社員IDリスト
	 * @param period 期間
	 * @return List<日別勤怠(Work)>
	 */
	public List<IntegrationOfDaily> getListWorkSchedule(List<String> employeeIds, DatePeriod period);
	
	public List<WorkScheduleBasicInforExport> get(List<String> lstSid , DatePeriod ymdPeriod);
	
	
	
	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.Export.日別勤務予定を取得する.社員IDリスト、基準日から勤務予定を取得する
	 * [1] 取得する
	 * 
	 * @param sid      社員ID
	 * @param baseDate 基準日
	 * @return 勤務種類コード
	 */
	public Optional<String> getWorkTypeCode(String sid, GeneralDate baseDate);
	
	/**
	 * 最も未来の勤務予定の年月日を取得する
	 * 
	 * RequestList439
	 * 
	 * 異動者、勤務種別変更者の作成期間の計算
	 * 社員ID（List）を条件に、存在するスケジュール期間の中で最も大きい年月日を取得する
	 * 
	 * @param sIds
	 * @return GeneralDate
	 */
	public Optional<GeneralDate> acquireMaxDateBasicSchedule(List<String> sIds);
	
	/**
     * TEAMD reqlist4 9/10/2020
     * @param employeeId
     * @param baseDate
     * @return
     */
	// Update bug redmine: http://192.168.50.4:3000/issues/116305
    public Optional<ScWorkScheduleExport_New> findByIdNewV2(String employeeId, GeneralDate baseDate);
    
    /**
	 * 
	 * 勤務予定の確定状態を取得する
	 * @param employeeID
	 * @param date
	 * @return
	 */
	public List<WorkScheduleConfirmExport> findConfirmById(List<String> employeeID, DatePeriod date);
}
