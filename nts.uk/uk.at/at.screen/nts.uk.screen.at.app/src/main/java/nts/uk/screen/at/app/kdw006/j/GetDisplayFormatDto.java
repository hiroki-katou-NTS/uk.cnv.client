package nts.uk.screen.at.app.kdw006.j;

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
public class GetDisplayFormatDto {

	/** 実績欄表示項目一覧 */
	private List<RecordColumnDisplayItemDto> recordColumnDisplayItems;

	/** 実績入力ダイアログ表示項目一覧 */
	private List<DisplayAttItemDto> displayAttItems;

	/** 作業内容入力ダイアログ表示項目一覧 */
	private List<DisplayManHrRecordItemDto> displayManHrRecordItems;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class RecordColumnDisplayItemDto {

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
class DisplayAttItemDto {

	// 項目ID: 勤怠項目ID
	private int attendanceItemId;

	// 表示順
	private int order;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
class DisplayManHrRecordItemDto {

	// 項目ID: 勤怠項目ID
	private int attendanceItemId;

	// 表示順
	private int order;
}
