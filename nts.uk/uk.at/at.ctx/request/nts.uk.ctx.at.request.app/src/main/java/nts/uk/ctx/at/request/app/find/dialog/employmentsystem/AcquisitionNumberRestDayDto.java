package nts.uk.ctx.at.request.app.find.dialog.employmentsystem;

import java.util.List;

import lombok.Data;

// class 確認残数情報詳細
@Data
public class AcquisitionNumberRestDayDto {
	
	// field 残数詳細
	private List<RemainNumberDetailDto> listRemainNumberDetail;
	
	// field 管理区分
	private Boolean isManagementSection;
	
	// field 紐付け管理
	private List<PegManagementDto> listPegManagement;
	
	// field 予定使用日数
	private double scheduledUsageDay;
	
	// field 予定使用時間
	private double scheduledUsageHour;
	
	// field 予定残数
	private double scheduledRemainingDay;
	
	// field 予定残数時間
	private double scheduledRemainingHour;
	
	// field 予定発生日数
	private double scheduleOccurrencedDay;
	
	// field 予定発生時間
	private double scheduleOccurrencedHour;
	
	// field 使用日数
	private double usageDay;
	
	// field 使用時間
	private double usageHour;
	
	// field 使用期限
	private double expiredDay;
	
	// field 残数
	private double remainingDay;
	
	// field 残数時間
	private double remainingHour;
	
	// field 発生日数
	private double occurrenceDay;
	
	// field 発生時間
	private double occurrenceHour;
	
	// field 繰越日数
	private double carryForwardDay;
	
	// field 繰越時間
	private double carryForwardHour;
}
