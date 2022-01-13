package nts.uk.ctx.at.record.app.find.workrecord.erroralarm;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorAlarmDto {
 	//	ver22 大塚オプション
	private boolean ootsukaOption;
	private List<ErrorAlarmWorkRecordDto> errorAlarmWorkRecordList;
	private List<Integer> applicationList;
}
