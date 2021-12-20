package nts.uk.screen.at.app.ksu001.processcommon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.EditStateOfDailyAttdDto;

/**
 * @author laitv
 * 勤務予定（勤務情報）dto
 */
@Data
@Builder
@AllArgsConstructor
public class WorkScheduleWorkInforDto {
	
	// 社員ID
	public String employeeId;
	// 年月日
	public GeneralDate date;
	// データがあるか
	public boolean haveData;
	// 実績か
	public boolean achievements;
	// 確定済みか
	public boolean confirmed;
	// 勤務予定が必要か
	public boolean needToWork;
	// 応援か
	public Integer supportCategory; // tu 1-> 5
	
	//Khu vực Optional
	// 勤務種類コード
	public String workTypeCode;
	// 勤務種類名 tên viết tắt
	public String workTypeName;
	// 勤務種類編集状態
	public EditStateOfDailyAttdDto workTypeEditStatus;
	// 就業時間帯コード
	public String workTimeCode;
	// 就業時間帯名
	public String workTimeName;
	// 就業時間帯編集状態
	public EditStateOfDailyAttdDto workTimeEditStatus;
	// 開始時刻
	public Integer startTime;
	// 開始時刻編集状態
	public EditStateOfDailyAttdDto startTimeEditState;
	// 終了時刻
	public Integer endTime;
	// 終了時刻編集状態
	public EditStateOfDailyAttdDto endTimeEditState;
	// 出勤休日区分
	public Integer workHolidayCls;
	// dùng cho màn ksu001
	public boolean workTypeIsNotExit;
	// dùng cho màn ksu001
	public boolean workTimeIsNotExit;
	
	// dùng cho màn ksu002
	public String workTypeNameKsu002;
	
	// dùng cho màn ksu002
	public String workTimeNameKsu002;
	
	// chưa bao gồm điều kiện 対象の日 < A画面パラメータ. 修正可能開始日　の場合 => check dưới UI
	public boolean conditionAbc1; 
	public boolean conditionAbc2;
	
	/**固定勤務
	FIXED(0, "固定勤務"),
	FLEX(1, "フレックス勤務"),
	FLOW(2,"流動勤務"), */
	
	//就業時間帯の勤務形態 
	public Integer workTimeForm;
}
