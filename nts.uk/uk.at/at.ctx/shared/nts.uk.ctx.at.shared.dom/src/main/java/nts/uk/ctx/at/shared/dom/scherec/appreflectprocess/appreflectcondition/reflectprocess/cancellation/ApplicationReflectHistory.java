package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.adapter.log.schedulework.CorrectRecordDailyResultImport;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

/**
 * @author thanh_nx
 *
 *         申請反映履歴
 */
@Getter
public class ApplicationReflectHistory implements DomainAggregate {

	// 社員ID
	private String employeeId;

	// 年月日
	private GeneralDate date;

	// 申請ID
	private String applicationId;

	// 予定実績区分
	private ScheduleRecordClassifi classification;

	// 取消区分
	private boolean cancellationCate;

	// 反映前
	private List<AttendanceBeforeApplicationReflect> lstAttBeforeAppReflect;

	// 実行情報
	private AppReflectExecInfo appExecInfo;

	public ApplicationReflectHistory(String employeeId, GeneralDate date, String applicationId,
			ScheduleRecordClassifi classification, boolean cancellationCate,
			List<AttendanceBeforeApplicationReflect> lstAttBeforeAppReflect, AppReflectExecInfo appExecInfo) {
		super();
		this.employeeId = employeeId;
		this.date = date;
		this.applicationId = applicationId;
		this.classification = classification;
		this.cancellationCate = cancellationCate;
		this.lstAttBeforeAppReflect = lstAttBeforeAppReflect;
		this.appExecInfo = appExecInfo;
	}

	/**
	 * 
	 * [1] 同時に値を戻す必要がある反映履歴を取得する
	 */
	public Optional<ApplicationReflectHistory> getReflectHistNeedReturn(Require require,
			ScheduleRecordClassifi classification, Integer itemId, String appId) {
		// $反映履歴
		Optional<ApplicationReflectHistory> cancelHistOtherIdLst = require.getCancelHistOtherId(this.employeeId,
				this.date, appId, this.appExecInfo.getReflectionTime(), classification).stream()
				.findFirst();
		if (!cancelHistOtherIdLst.isPresent())
			return Optional.empty();
		boolean correctHist = existsCorrectHist(require, cancelHistOtherIdLst.get(), itemId);
		
		boolean unreflectHistInHists = existsUnreflectHistInHists(require, cancelHistOtherIdLst.get(), itemId);
		
		if(correctHist || unreflectHistInHists) {
			return Optional.empty();
		}
		
		return cancelHistOtherIdLst;
	}

	// [2] 指定した勤怠項目IDの反映前を取得する
	public Optional<AttendanceBeforeApplicationReflect> getBeforeSpecifiAttReflected(Integer itemId) {
		return this.getLstAttBeforeAppReflect().stream().filter(x -> x.getAttendanceId() == itemId).findFirst();
	}

	// [3] 指定した勤怠項目IDの元に戻す値を判断する
	public Optional<AttendanceBeforeApplicationReflect> determineValueUndoSpecifi(Require require,
			ScheduleRecordClassifi classification, Integer itemId) {
		// $再反映により先に実行した反映前の勤怠
		Optional<AttendanceBeforeApplicationReflect> attBeforeRereflect = this.getAppExecInfo()
				.acquireAttBeforeRereflect(require, this.employeeId, this.date, classification, itemId);

		return attBeforeRereflect.isPresent() ? attBeforeRereflect : this.getBeforeSpecifiAttReflected(itemId);
	}

	//[1] 反映履歴の間に手修正が存在するか確認
	private boolean existsCorrectHist(Require require, ApplicationReflectHistory appHist, int itemId) {
		
		return require.getBySpecifyItemId(this.employeeId, this.date, itemId).stream()
				.anyMatch(x -> x.getCorrectTime().before(this.appExecInfo.getReflectionTime())
						&& x.getCorrectTime().after(appHist.getAppExecInfo().getReflectionTime()));

	}
	
	//	[2] 反映履歴の間に未取消の反映履歴が存在するか確認
	private boolean existsUnreflectHistInHists(Require require, ApplicationReflectHistory appHist, int itemId) {
		List<ApplicationReflectHistory> lstHist = require.getHistWithSidDate(this.employeeId, this.date, this.classification);
		return lstHist.stream().anyMatch(x -> x.getAppExecInfo().getReflectionTime().before(this.getAppExecInfo().getReflectionTime()) 
				&& x.getAppExecInfo().getReflectionTime().after(appHist.getAppExecInfo().getReflectionTime())
				&& x.getLstAttBeforeAppReflect().stream().anyMatch(y -> y.getAttendanceId() == itemId)
				&& !x.isCancellationCate()
				&& x.getClassification() == this.classification
				&& !x.getApplicationId().equals(this.applicationId));
	}
	
	public static interface Require extends AppReflectExecInfo.Require {

		// [R-1] 指定した時刻以前の申請IDが異なる取り消し済み反映履歴を取得する
		// ApplicationReflectHistoryRepo
		public List<ApplicationReflectHistory> getCancelHistOtherId(String sid, GeneralDate date, String appId,
				GeneralDateTime createTime, ScheduleRecordClassifi classification);

		// [R-2] 日別実績の修正履歴を取得する
		// GetRecordDailyPerformanceLogAdapter 日別実績
		// GetWorkScheduleLogAdapter 勤務予定
		public List<CorrectRecordDailyResultImport> getBySpecifyItemId(String sid, GeneralDate targetDate,
				Integer itemId);
	}
}
