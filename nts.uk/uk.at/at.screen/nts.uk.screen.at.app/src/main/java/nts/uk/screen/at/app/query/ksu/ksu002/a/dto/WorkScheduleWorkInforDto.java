package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author chungnt 勤務予定（勤務情報）dto
 *
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
	public Achievement achievements;
	
	// 確定済みか
	public boolean confirmed;
	
	// 勤務予定が必要か
	public boolean needToWork;
	
	// 応援か
	public Integer supportCategory; // tu 1-> 5

	// Khu vực Optional
	// 勤務種類コード
	public String workTypeCode;
	
	// 勤務種類名
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
	
	public DateInfoDuringThePeriodDto dateInfoDuringThePeriod;
	
	//就業時間帯の勤務形態 
	public Integer workTimeForm;
	
	@Builder
	public static class Achievement {
		
		public String employeeId;
		
		// 年月日
		public GeneralDate date;

		// Khu vực Optional
		// 勤務種類コード
		public String workTypeCode;
		
		// 勤務種類名
		public String workTypeName;
		
		// 就業時間帯コード
		public String workTimeCode;
		
		// 就業時間帯名
		public String workTimeName;
		
		// 開始時刻
		public Integer startTime;
		
		// 終了時刻
		public Integer endTime;	
		
	}

}
