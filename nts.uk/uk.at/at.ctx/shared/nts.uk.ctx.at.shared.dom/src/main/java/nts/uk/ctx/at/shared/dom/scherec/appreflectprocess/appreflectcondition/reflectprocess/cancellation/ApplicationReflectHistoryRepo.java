package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

public interface ApplicationReflectHistoryRepo {

	public List<ApplicationReflectHistory> findAppReflectHistAfterMaxTime(String sid, GeneralDate baseDate,
			ScheduleRecordClassifi classification, boolean flgRemove, GeneralDateTime reflectionTime);

	public void insertAppReflectHist(String cid, ApplicationReflectHistory hist);

	public void updateAppReflectHist(String sid, String appId, GeneralDate baseDate,
			ScheduleRecordClassifi classification, boolean flagRemove);

	/**
	 * [条件] 社員ID＝input.申請.申請者 申請ID＝input.申請.申請ID 年月日＝input.対象日 予定実績区分＝input.予定実績区分
	 * 取消フラグ＝false
	 * 
	 * 反映時刻（DESC）
	 */
	public List<ApplicationReflectHistory> findAppReflectHist(String sid, String appId, GeneralDate baseDate,
			ScheduleRecordClassifi classification, boolean flgRemove);

	/**
	 * [条件] 社員ID＝input.申請.申請者 年月日＝input.対象日 予定実績区分＝input.予定実績区分 取消フラグ＝false
	 * 反映時刻＜=取得した[最新の申請反映履歴].反映時刻 ]
	 * 
	 * 反映時刻（DESC）
	 */
	public List<ApplicationReflectHistory> findAppReflectHistDateCond(String sid, GeneralDate baseDate,
			ScheduleRecordClassifi classification, boolean flgRemove, GeneralDateTime reflectionTime);
	
	/**
	 * [1] 指定した時刻以前の申請IDが異なる取り消し済み履歴を取得する
	 */
	public List<ApplicationReflectHistory> getCancelHistOtherId(String sid,  GeneralDate date, String appId, GeneralDateTime createTime,
			ScheduleRecordClassifi classification);
	
	//[2] 申請反映履歴を取得する
	public List<ApplicationReflectHistory> getHistWithSidDate(String sid, GeneralDate date,
			ScheduleRecordClassifi classification);
}
