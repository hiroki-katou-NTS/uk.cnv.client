package nts.uk.screen.at.app.dailyperformance.correction.dto;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.DPCorrectionStateParam;

/**
 * @author ductm
 *
 */
public class DPCorrectionInitParam {
	// 期間を変更する
	public Boolean changePeriodAtr;
	// 画面モード
	public Integer screenMode;
	// エラー参照を起動する
	public Boolean errorRefStartAtr;
	// 初期表示年月日
	public GeneralDate initDisplayDate;
	// 初期表示社員
	public String employeeID;
	// 対象期間
	public DateRange objectDateRange;
	// 対象社員 (list)
	public List<DailyPerformanceEmployeeDto> lstEmployee;
	// 打刻初期値
	public SPRInfoDto initClock;
	// 日別実績の修正の表示形式
	public Integer displayFormat;
	// 表示期間
	public DateRange displayDateRange;
	// 遷移先の画面
	public String transitionDesScreen;
	public DPCorrectionStateParam dpStateParam;
	public Integer closureId;
	
	public List<String> formatCodes;
}
