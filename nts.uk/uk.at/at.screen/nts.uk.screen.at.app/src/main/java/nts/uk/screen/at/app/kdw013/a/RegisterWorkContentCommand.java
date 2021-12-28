package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordConvertResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance.RegisterDeleteTimeZoneAttendanceCommand;

/**
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class RegisterWorkContentCommand {

	/** 対象者 */
	private String employeeId;

	// 工数実績変換結果
	private List<ManHrRecordConvertResultCommand> manHrlst;

	// 日別勤怠(Work)
	private List<IntegrationOfDailyCommand> integrationOfDailys;

	/** 編集状態<Enum.日別勤怠の編集状態> */
	private int editStateSetting;

	// 作業時間帯
	private List<WorkDetailCommand> workDetails;

	/** 確認モード */
	private int mode;

	/** 変更対象日 */
	private List<GeneralDate> changedDates;
	
	// 対象項目リスト
	private List<Integer> itemIds;
	
	private RegisterDeleteTimeZoneAttendanceCommand deleteAttByTimeZones;
	
	
	public List<ManHrRecordConvertResult> getManHrlst(){
		
		return	this.manHrlst.stream().map(mh-> ManHrRecordConvertResultCommand.toDomain(mh)).collect(Collectors.toList());		
	}
	
	public List<IntegrationOfDaily> getIntegrationOfDailys() {
		return this.integrationOfDailys.stream().map(id -> IntegrationOfDailyCommand.toDomain(id))
				.collect(Collectors.toList());

	}

}
