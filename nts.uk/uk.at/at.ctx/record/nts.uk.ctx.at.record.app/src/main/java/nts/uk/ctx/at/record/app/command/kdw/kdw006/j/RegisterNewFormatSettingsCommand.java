package nts.uk.ctx.at.record.app.command.kdw.kdw006.j;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterNewFormatSettingsCommand {

	private int attendanceItemId;
	
	/** 実績欄表示項目一覧 */
	private List<RecordColumnDisplayItemInput> recordColumnDisplayItems;

	/** 実績入力ダイアログ表示項目一覧 */
	private List<DisplayAttItemInput> displayAttItems;

	/** 作業内容入力ダイアログ表示項目一覧 */
	private List<DisplayManHrRecordItemInput> displayManHrRecordItems;
	
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class RecordColumnDisplayItemInput {

	// 表示順
	private int order;

	// 対象項目: 勤怠項目ID
	private int attendanceItemId;

	// 名称: 実績欄表示名称
	private String displayName;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class DisplayAttItemInput {

	// 項目ID: 勤怠項目ID
	private int attendanceItemId;

	// 表示順
	private int order;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class DisplayManHrRecordItemInput {

	// 項目ID: 勤怠項目ID
	private int attendanceItemId;

	// 表示順
	private int order;
}