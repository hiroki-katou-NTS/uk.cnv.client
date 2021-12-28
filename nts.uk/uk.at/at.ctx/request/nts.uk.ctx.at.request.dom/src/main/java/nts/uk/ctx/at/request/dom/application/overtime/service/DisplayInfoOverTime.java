package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.CalculationResult;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoBaseDateOutput;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoNoBaseDate;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoWithDateApplication;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;

/**
 * Refactor5
 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.01_初期起動の処理.
 * @author hoangnd
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
// 残業申請の表示情報
public class DisplayInfoOverTime {
	// 基準日に関する情報
	private InfoBaseDateOutput infoBaseDateOutput;
	// 基準日に関係しない情報
	private InfoNoBaseDate infoNoBaseDate;
	// 休出枠
	private List<WorkdayoffFrame> workdayoffFrames = Collections.emptyList();
	// 残業申請区分
	private OvertimeAppAtr overtimeAppAtr;
	// 申請表示情報
	private AppDispInfoStartupOutput appDispInfoStartup;
	// 計算結果
	private Optional<CalculationResult> calculationResultOp = Optional.empty();
	// 申請日に関係する情報
	private Optional<InfoWithDateApplication> infoWithDateApplicationOp = Optional.empty(); 
	// 計算済フラグ
	private CalculatedFlag calculatedFlag;
	// 申請中の勤務情報
	private Optional<WorkInfo> workInfo = Optional.empty();
	// 最新の複数回残業申請
	private Optional<AppOverTime> latestMultipleOvertimeApp = Optional.empty();
	// 計算の休憩時間帯
	private List<BreakTimeSheet> calculatedBreakTimes = new ArrayList<>();
	// 計算の勤務時間
	private List<TimeZone> calculatedWorkTimes = new ArrayList<>();
}
