package nts.uk.ctx.at.record.dom.workrecord.erroralarm.daily.algorithm;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.WorkRecordExtractingCondition;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.MonthlyAttendanceItemNameDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

@Getter
@AllArgsConstructor
public class PrepareData {
	
	// List<勤務種類＞
	private List<WorkType> listWorkType;
	
	// List＜日別実績＞
	private List<IntegrationOfDaily> listIntegrationDai;
	
	// List＜勤務実績のエラーアラームチェック＞
	private List<ErrorAlarmCondition> listErrorAlarmCon;
	
	// List<勤務実績の抽出条件＞
	private List<WorkRecordExtractingCondition> workRecordCond;
	
	// List＜日別実績のエラーアラーム＞
	private List<ErrorAlarmWorkRecord> listError;
	
	// List＜勤務実績の固定抽出条件＞
	// 本人確認状況　（List）
	// 管理者未確認　（List）
	// List＜労働条件＞
	//　List＜打刻カード＞
	private DataFixExtracCon dataforDailyFix;
	
	// List＜就業時間帯の設定＞
	private List<WorkTimeSetting> listWorktime;
	//List<勤怠項目>
	private List<MonthlyAttendanceItemNameDto> lstItemDay;

	private List<ErrorAlarmCondition> errorMessageAlarmList;
}
