package nts.uk.ctx.at.record.app.find.manhourrecordusesetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.workrecord.erroralarm.ErrorAlarmWorkRecordDto;

/**
 * 
 * @author huylq
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManHourRecordUseSettingDto {

	//日別実績のエラーアラーム
	ErrorAlarmWorkRecordDto errorAlarmWorkRecord; 
	
	//工数実績参照設定
	ManHourRecordReferenceSettingDto manHourRecordReferenceSetting;
	
	// 使用区分
	int usrAtr;
}
