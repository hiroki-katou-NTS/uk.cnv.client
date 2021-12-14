package nts.uk.file.at.app.export.attendancerecord;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TempAbsenceData {

	// 休職休業枠．名称
	private String tempAbsenceFrameName;
	
	// 休職休業履歴．期間．開始日
	private String periodStart;
	
	// 休職休業履歴．期間．終了日
	private String periodEnd;
}
