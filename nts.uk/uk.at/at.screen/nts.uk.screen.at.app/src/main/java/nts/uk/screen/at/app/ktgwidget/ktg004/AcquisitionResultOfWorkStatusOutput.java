package nts.uk.screen.at.app.ktgwidget.ktg004;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author thanhPV
 * @name 勤務状況の取得結果
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AcquisitionResultOfWorkStatusOutput {

	// 勤務状況の詳細設定
	private List<ItemsSettingDto> itemsSetting;

	// 当月の締め情報
	private CurrentClosingPeriod closingThisMonth;
	
	//表示する年月の締め情報
	private CurrentClosingPeriod closingDisplay;
	
	//name - 名称
	private String name;

    //対象社員の勤怠情報
	private AttendanceInforDto attendanceInfor = new AttendanceInforDto();
    
    //対象社員の残数情報
	private RemainingNumberInforDto remainingNumberInfor = new RemainingNumberInforDto();
	
	//締めＩＤ
    private Integer closureId;
    
    //勤怠担当者である
    private Boolean detailedWorkStatusSettings = false;
    
    //翌月の締め情報
  	private CurrentClosingPeriod nextMonthClosingInformation;
  	
  	// 休暇設定
  	private VacationSetting vacationSetting;
}
