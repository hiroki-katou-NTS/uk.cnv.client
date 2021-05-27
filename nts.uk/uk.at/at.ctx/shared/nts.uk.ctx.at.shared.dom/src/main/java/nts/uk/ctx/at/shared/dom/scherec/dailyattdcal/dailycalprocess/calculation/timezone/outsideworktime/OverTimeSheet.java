package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.ot.frame.NotUseAtr;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrame;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.AddSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.BonusPayAutoCalcSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.BonusPayAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ActualWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.UseTimeAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareCalcRange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.service.ActualWorkTimeSheetListService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CheckDateForManageCmpLeaveService;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimezoneNo;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.OneDayTime;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSetAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneOtherSubHolTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowOTTimezone;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkTimezoneSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 残業時間帯
 * @author keisuke_hoshina
 */
@Getter
public class OverTimeSheet {
	//残業枠時間帯
	private List<OverTimeFrameTimeSheetForCalc> frameTimeSheets;

	
	/**
	 * Constrctor
	 * @param frameTimeSheets
	 */
	public OverTimeSheet(List<OverTimeFrameTimeSheetForCalc> frameTimeSheets) {
		super();
		this.frameTimeSheets = frameTimeSheets;
	}
	
	
	/**
	 * 分割後の残業時間枠時間帯を受け取り
	 * @param insertList　補正した時間帯
	 * @param originList　補正する前の時間帯
	 * @return　
	 */
	public static List<OverTimeFrameTimeSheet> correctTimeSpan(List<OverTimeFrameTimeSheet> insertList,List<OverTimeFrameTimeSheet> originList,int nowNumber){
		originList.remove(nowNumber);
		originList.addAll(insertList);
		return originList;
	}
	
	/**
	 * 時間帯毎に残業時間を計算する(補正、制御含む)
	 * 
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param date 年月日
	 * @param workTimeCode 就業時間帯コード：Optional<就業時間帯コード>
	 * @param autoCalcSetting 自動計算設定：残業時間の自動計算設定
	 *  @param statutoryFrameNoList 法定内残業の残業枠NO：List<残業枠NO>
	 * @param overTimeOfDaily 日別勤怠の残業時間
	 */
	public void calculateOvertimeEachTimeZone(TransProcRequire require, String cid, String sid, GeneralDate date,
			String workTypeCode, Optional<String> workTimeCode, AutoCalOvertimeSetting autoCalcSetting,
			List<OverTimeFrameNo> statutoryFrameNoList, OverTimeOfDaily overTimeOfDaily, boolean upperControl,
			List<OvertimeWorkFrame> overtimeFrameList) {
		//時間帯毎に残業時間を計算する
		this.calculateProcess(autoCalcSetting);
		if(upperControl){
			// 事前申請上限制御
			this.advanceAppUpperLimitControl(overTimeOfDaily, autoCalcSetting, statutoryFrameNoList);
		}

		//代休への振替処理
		transferProcSubHol(require, cid, sid, date, workTypeCode, workTimeCode, overtimeFrameList);
	}
	
	/**
	 * 時間帯毎に残業時間を計算する
	 * @param autoCalcSetting 自動計算設定：残業時間の自動計算設定
	 */
	public void calculateProcess(AutoCalOvertimeSetting autoCalcSetting) {
		//残業時間帯の時間枠を取得
		this.frameTimeSheets.forEach(frameTime ->{
			//残業時間帯の計算
			frameTime.getFrameTime().setOverTimeWork(frameTime.correctCalculationTime(Optional.of(false), autoCalcSetting));
		});
		return;
	}
	
	/**
	 * 事前申請上限制御
	 * @param overTimeOfDaily 日別勤怠の残業時間
	 * 	@param autoCalcSet 残業時間の自動計算設定
	 *  @param statutoryFrameNoList 法定内残業の残業枠NO：List<残業枠NO>
	 */
	private void advanceAppUpperLimitControl(OverTimeOfDaily overTimeOfDaily, AutoCalOvertimeSetting autoCalcSet,
			List<OverTimeFrameNo> statutoryFrameNoList) {
		//各時間ごとの制御する時間を計算
		List<TimeDeductByPriorAppOutput> lstTimeDeductOut = calculateTimeToControlEachTime(overTimeOfDaily, autoCalcSet,
				statutoryFrameNoList);

		//残業枠時間帯(WORK)を時系列の逆順に取得
		frameTimeSheets.sort((x, y) -> y.getTimeSheet().getStart().v() -  y.getTimeSheet().getEnd().v());
		
		//loop
		frameTimeSheets.forEach(frameSheet -> {
			// 控除する時間を計算
			Optional<TimeDeductByPriorAppOutput> frameSheetOpt = lstTimeDeductOut.stream()
					.filter(frame -> frame.getOverTimeNo().v() == frameSheet.getOverTimeWorkSheetNo().v()).findFirst();
			
			
			AttendanceTime timeDeductCalc = new AttendanceTime(
					Math.min(frameSheetOpt.map(x -> x.getTimeDeduct().v()).orElse(0),
							frameSheet.getFrameTime().getOverTimeWork().getTime().v()));
			// 時間帯から控除
			frameSheet.getFrameTime().getOverTimeWork().setTime(new AttendanceTime(
					frameSheet.getFrameTime().getOverTimeWork().getTime().v() - timeDeductCalc.v()));
			// 控除する時間を減算
			frameSheetOpt.ifPresent(x -> {
				x.setTimeDeduct(new AttendanceTime(x.getTimeDeduct().v() - timeDeductCalc.v()));
			});
		});
		
		return;
	}
	
	/**
	 * 各時間ごとの制御する時間を計算
	 * @param overTimeOfDaily 日別勤怠の残業時間
	 * @param autoCalcSet 残業時間の自動計算設定
	 * @param statutoryFrameNoList 法定内残業の残業枠NO：List<残業枠NO>
	 * @return
	 */
	private List<TimeDeductByPriorAppOutput> calculateTimeToControlEachTime(OverTimeOfDaily overTimeOfDaily,
			AutoCalOvertimeSetting autoCalcSet, List<OverTimeFrameNo> statutoryFrameNoList) {
		
		//時間帯毎の時間から残業枠毎の時間を集計
		 List<OverTimeFrameTime> lstOverTimeFrame = aggregateTimeForOvertime(overTimeOfDaily);
		 //後で比較する為の時間create
		 List<OverTimeFrameTime> lstOverTimeFrameForAfter = lstOverTimeFrame.stream().map(x -> {
			 return x.clone();
		 }).collect(Collectors.toList());
		
		// 事前残業時間をセットする
		lstOverTimeFrame.forEach(overTime -> {
			val beforeApp = overTimeOfDaily.getOverTimeWorkFrameTime().stream()
					.filter(x -> x.getOverWorkFrameNo().v() == overTime.getOverWorkFrameNo().v()).findFirst()
					.map(x -> x.getBeforeApplicationTime());
			beforeApp.ifPresent(before -> {
				overTime.addBeforeTime(before);
			});
		});
		
		//補正処理を実行する為に、日別勤怠の残業時間のインスタンスを作成
		//事前申請上限制御処理
		List<OverTimeFrameTime> afterUpperControl = new OverTimeFrameTimeList(lstOverTimeFrame)
				.afterUpperControl(autoCalcSet, statutoryFrameNoList);

		//制御する前の時間と比較して制御する時間を計算
		return lstOverTimeFrameForAfter.stream().map(x -> {
			val afterUpperCon = afterUpperControl.stream()
					.filter(aftUp -> aftUp.getOverWorkFrameNo().v() == x.getOverWorkFrameNo().v()).findFirst()
					.map(aftUp -> aftUp.getOverTimeWork().getTime()).orElse(new AttendanceTime(0));
			return new TimeDeductByPriorAppOutput(x.getOverWorkFrameNo(),
					new AttendanceTime(x.getOverTimeWork().getTime().v() - afterUpperCon.v()));
		}).collect(Collectors.toList());
	}
	
	//時間帯毎の時間から残業枠毎の時間を集計
	public  List<OverTimeFrameTime> aggregateTimeForOvertime(OverTimeOfDaily overTimeOfDaily) {
		//残業時間帯でループ
		this.frameTimeSheets.forEach(frameTime -> {
			// 残業時間へ加算
			val overTime = overTimeOfDaily.getOverTimeWorkFrameTime().stream()
					.filter(x -> x.getOverWorkFrameNo().v() == frameTime.getOverTimeWorkSheetNo().v()).findFirst();
			overTime.ifPresent(data -> {
				//B.残業時間+=A.残業時間
				data.getOverTimeWork().setTime(new AttendanceTime(data.getOverTimeWork().getTime().v()
						+ frameTime.getFrameTime().getOverTimeWork().getTime().v()));
				data.getOverTimeWork().calcDiverGenceTime();
				//B.振替時間+=B.振替時間
				data.getTransferTime().setTime(new AttendanceTime(data.getTransferTime().getTime().v()
						+ frameTime.getFrameTime().getTransferTime().getTime().v()));
				data.getTransferTime().calcDiverGenceTime();
				
			});
		});
	
		//残業枠時間を返す
		
		return overTimeOfDaily.getOverTimeWorkFrameTime();
	}
	
	/**
	 * 代休への振替処理
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param date 年月日
	 * @param workTimeCode 就業時間帯コード:Optional<就業時間帯コード>
	 * @param overTimeFrame 残業枠：List<残業枠>
	 */
	public void transferProcSubHol(TransProcRequire require, String cid, String sid, GeneralDate date, String workTypeCode, Optional<String> workTimeCode,
			List<OvertimeWorkFrame> overTimeFrame) {
		//○平日かどうか判断
		Optional<WorkType> workTypeOpt = require.findByPK(cid, workTypeCode);
		if(!workTypeOpt.isPresent()) return ;
		WorkStyle workStype = workTypeOpt.get().checkWorkDay();
		if(workStype == WorkStyle.ONE_DAY_REST){
			return;
		}

		// ○当日が代休管理する日かどうかを判断する
		boolean checkDateForMag = require.checkDateForManageCmpLeave(require, cid, sid, date);
		if(!checkDateForMag){
			return;
		}

		// 代休発生設定を取得する
		Optional<SubHolTransferSet> subHolTransSet = GetSubHolOccurrenceSetting.process(require, cid, workTimeCode,
				CompensatoryOccurrenceDivision.FromOverTime);
		if(!subHolTransSet.isPresent()){
			return;
		}
		
		if (subHolTransSet.get().getSubHolTransferSetAtr() == SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL) {
			// △一定時間の振替処理
			this.transProcesCertainPeriod(require, cid, workTimeCode, overTimeFrame);
		} else {
			// △指定時間の振替処理
			this.transferProcessSpecifi(require, cid, workTimeCode, overTimeFrame);
		}
	}
	
	//残業時間を代休へ振り替える(一定時間)
	private void transProcesCertainPeriod(TransProcRequire require, String cid, Optional<String> workTimeCode, List<OvertimeWorkFrame> overTimeFrame) {
		//振替可能時間を計算
		AttendanceTime sumTime = calculateTransferableTime(require, cid, workTimeCode, overTimeFrame, UseTimeAtr.TIME);
		
		//残業時間を代休へ振り替える
		transferOvertimeSubsHol(require, sumTime, UseTimeAtr.TIME, overTimeFrame, TimeSeriesDivision.TIME_SERIES_REVERSE);
		
		//振替可能時間を計算
		AttendanceTime sumCalcTime = calculateTransferableTime(require, cid, workTimeCode, overTimeFrame, UseTimeAtr.CALCTIME);
		
		//残業時間を代休へ振り替える
		transferOvertimeSubsHol(require, sumCalcTime, UseTimeAtr.CALCTIME, overTimeFrame, TimeSeriesDivision.TIME_SERIES_REVERSE);
		
	}
	
	// 振替可能時間を計算
	private AttendanceTime calculateTransferableTime(TransProcRequire require, String cid, Optional<String> workTimeCode,
			List<OvertimeWorkFrame> overTimeFrame, UseTimeAtr atr) {

		//代休発生設定を取得する
		Optional<SubHolTransferSet> subHolidayTrans = GetSubHolOccurrenceSetting.process(require, cid, workTimeCode, CompensatoryOccurrenceDivision.FromOverTime);
		
		//代休振替対象時間を合計する
		AttendanceTime sumTime = totalTimeTransferOfHoliday(overTimeFrame, atr);
		
		//代休振替可能時間を取得
		return subHolidayTrans.map(x -> x.getTransferTime(sumTime)).orElse(new AttendanceTime(0));
	}
	
	// 代休振替対象時間を合計する
	private AttendanceTime totalTimeTransferOfHoliday(List<OvertimeWorkFrame> overTimeFrame, UseTimeAtr atr) {
		// 代休振替対象となる残業枠NO一覧を取得
		val lstFrameUse = overTimeFrame.stream().filter(x -> x.getTransferAtr() == NotUseAtr.USE)
				.collect(Collectors.toList());

		// 代休振替対象となる残業枠NO一覧と一致する時間帯でループする
		val lstFrameSheetDom = this.frameTimeSheets.stream()
				.filter(x -> lstFrameUse.stream()
						.filter(y -> y.getOvertimeWorkFrNo().v().intValue() == x.getOverTimeWorkSheetNo().v())
						.findFirst().isPresent())
				.collect(Collectors.toList());

		// 時間を加算する
		int time = lstFrameSheetDom.stream().collect(
				Collectors.summingInt(x -> atr == UseTimeAtr.TIME ? x.getFrameTime().getOverTimeWork().getTime().v()
						: x.getFrameTime().getOverTimeWork().getCalcTime().v()));

		// 対象時間を返す
		return new AttendanceTime(time);
	}
	
	//TODO: 確認
	//残業時間を代休へ振り替える
	private void transferOvertimeSubsHol(TransProcRequire require, AttendanceTime sumTime, UseTimeAtr atr, 
			List<OvertimeWorkFrame> overTimeFrame, TimeSeriesDivision timeSeries) {
		
		//○残業時間帯をソート
		val lstFrameUse = overTimeFrame.stream().filter(x -> x.getTransferAtr() == NotUseAtr.USE)
				.collect(Collectors.toList());
		val lstFrameSheetDom = this.frameTimeSheets.stream()
				.filter(x -> lstFrameUse.stream()
						.filter(y -> y.getOvertimeWorkFrNo().v().intValue() == x.getOverTimeWorkSheetNo().v())
						.findFirst().isPresent())
				.sorted((x, y) -> {
					if (timeSeries == TimeSeriesDivision.TIME_SERIES) {
						return x.getTimeSheet().getStart().v() - y.getTimeSheet().getStart().v();
					} else {
						return y.getTimeSheet().getEnd().v() - x.getTimeSheet().getEnd().v();
					}
				}).collect(Collectors.toList());
			
			int transferableTime = sumTime.v();
			int transferTime = 0;
		for (OverTimeFrameTimeSheetForCalc sheetForCalc : lstFrameSheetDom) {
			if(transferableTime == 0) break;
			int timeUseForCalc = 0;
			if (atr == UseTimeAtr.TIME) {
				timeUseForCalc = sheetForCalc.getFrameTime().getOverTimeWork().getTime().v();
				// ○振替可能残時間と残業時間を比較して振替時間を計算
				transferTime = Math.min(timeUseForCalc, transferableTime);
				// ○振替
				sheetForCalc.getFrameTime().getOverTimeWork()
						.setTime(new AttendanceTime(timeUseForCalc - transferTime));
				sheetForCalc.getFrameTime().getTransferTime().setTime(new AttendanceTime(transferTime));
				// ○振替可能残時間から振替時間を減算
				transferableTime -= transferTime;
			} else {
				timeUseForCalc = sheetForCalc.getFrameTime().getOverTimeWork().getCalcTime().v();
				// ○振替可能残時間と残業時間を比較して振替時間を計算
				// ○振替可能残時間と残業時間を比較して振替時間を計算
				transferTime = Math.min(timeUseForCalc, transferableTime);
				// ○振替
				sheetForCalc.getFrameTime().getOverTimeWork()
						.replaceCalcTime(new AttendanceTime(timeUseForCalc - transferTime));
				sheetForCalc.getFrameTime().getTransferTime().replaceCalcTime(new AttendanceTime(transferTime));
				// ○振替可能残時間から振替時間を減算
				transferableTime -= transferTime;
			}

		}
		
	}
	
	//残業時間を代休へ振り替える(指定時間)
	private void transferProcessSpecifi(TransProcRequire require, String cid, Optional<String> workTimeCode, List<OvertimeWorkFrame> overTimeFrame) {
		// 振替可能時間を計算
		AttendanceTime sumTime = calculateTransferableTime(require, cid, workTimeCode, overTimeFrame, UseTimeAtr.TIME);

		// 残業時間を代休へ振り替える
		transferOvertimeSubsHol(require, sumTime, UseTimeAtr.TIME, overTimeFrame,
				TimeSeriesDivision.TIME_SERIES);

		// 振替可能時間を計算
		AttendanceTime sumCalcTime = calculateTransferableTime(require, cid, workTimeCode, overTimeFrame,
				UseTimeAtr.CALCTIME);

		// 残業時間を代休へ振り替える
		transferOvertimeSubsHol(require, sumCalcTime, UseTimeAtr.CALCTIME, overTimeFrame,
				TimeSeriesDivision.TIME_SERIES);

	}
	
	/**
	 * 2021/05/01 update 残業時間の計算
	 * 残業時間枠時間帯をループさせ時間を計算する
	 * アルゴリズム：ループ処理
	 * @param require Require
	 * @param autoCalcSet 残業時間の自動計算設定
	 * @param workType 勤務種類
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @param integrationOfDaily 日別実績(Work)
	 * @param statutoryFrameNoList 法定内残業枠(List)
	 * @param declareResult 申告時間帯作成結果
	 * @param upperControl 事前申請上限制御
	 * @param overtimeFrameList 残業枠リスト
	 * @return 残業枠時間(List)
	 */
	public List<OverTimeFrameTime> collectOverTimeWorkTime(
			OverTimeSheet.TransProcRequire require,
			String cid, 
			AutoCalOvertimeSetting autoCalcSet,
			WorkType workType,
			Optional<String> workTimeCode, 
			IntegrationOfDaily integrationOfDaily,
			List<OverTimeFrameNo> statutoryFrameNoList,
			DeclareTimezoneResult declareResult,
			boolean upperControl,
			List<OvertimeWorkFrame> overtimeFrameList) {

		// 時間帯毎に残業時間を計算する(補正、制御含む)
		Optional<OverTimeOfDaily> overTimeWork = integrationOfDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getOverTimeWork());
		List<OverTimeFrameTime> aftertransTimeList = new ArrayList<OverTimeFrameTime>();
		if (overTimeWork.isPresent()) {
			calculateOvertimeEachTimeZone(require, cid, integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd(),
					workType.getWorkTypeCode().v(), workTimeCode, autoCalcSet, statutoryFrameNoList, overTimeWork.get(),
					upperControl, overtimeFrameList);
			// 時間帯毎の時間から残業枠毎の時間を集計
			aftertransTimeList.addAll(aggregateTimeForOvertime(overTimeWork.get()));
		}
			
		if (declareResult.getCalcRangeOfOneDay().isPresent()){
			//ループ処理
			CalculationRangeOfOneDay declareCalcRange = declareResult.getCalcRangeOfOneDay().get();
			OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
			if (declareOutsideWork.getOverTimeWorkSheet().isPresent()){
				OverTimeSheet declareSheet = declareOutsideWork.getOverTimeWorkSheet().get();
				//常に「打刻から計算する」で処理する
				CalAttrOfDailyAttd declareCalcSet = CalAttrOfDailyAttd.createAllCalculate();
				List<OverTimeFrameTime> declareFrameTimeList = declareSheet.collectOverTimeWorkTime(
						require,
						cid, 
						declareCalcSet.getOvertimeSetting(),
						workType,
						workTimeCode,
						integrationOfDaily,
						statutoryFrameNoList,
						new DeclareTimezoneResult(),
						false,
						overtimeFrameList);
				//申告残業反映後リストの取得
				OverTimeSheet.getListAfterReflectDeclare(aftertransTimeList, declareFrameTimeList, declareResult);
			}
		}
		//マイナスの乖離時間を0にする
		OverTimeOfDaily.divergenceMinusValueToZero(aftertransTimeList);
		
		return aftertransTimeList;
	}

	/**
	 * 控除時間を取得
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @return 控除時間
	 */
	public AttendanceTime getDeductionTime(
			ConditionAtr conditionAtr, DeductionAtr dedAtr, TimeSheetRoundingAtr roundAtr) {
		
		return ActualWorkTimeSheetListService.calcDeductionTime(conditionAtr, dedAtr, roundAtr,
				this.frameTimeSheets.stream().map(tc -> (ActualWorkingTimeSheet)tc).collect(Collectors.toList()));
	}

	/**
	 * 控除回数の計算
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @return 控除回数
	 */
	public int calcDeductionCount(ConditionAtr conditionAtr, DeductionAtr dedAtr) {
		
		return ActualWorkTimeSheetListService.calcDeductionCount(conditionAtr, dedAtr,
				this.frameTimeSheets.stream().map(tc -> (ActualWorkingTimeSheet)tc).collect(Collectors.toList()));
	}
	
	/**
	 * 残業枠時間帯(WORK)を全て残業枠時間帯へ変換する
	 * アルゴリズム：残業枠時間帯の作成
	 * @return 残業枠時間帯List
	 */
	public List<OverTimeFrameTimeSheet> changeOverTimeFrameTimeSheet(OverTimeSheet.TransProcRequire require,
			String cid, 
			AutoCalOvertimeSetting autoCalcSet,
			WorkType workType,
			Optional<String> workTimeCode, 
			IntegrationOfDaily integrationOfDaily,
			List<OverTimeFrameNo> statutoryFrameNoList,
			boolean upperControl,
			List<OvertimeWorkFrame> overtimeFrameList){
		
		Optional<OverTimeOfDaily> overTimeWork = integrationOfDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getOverTimeWork());
		if (overTimeWork.isPresent()) {
			//時間帯毎に残業時間を計算する(補正、制御含む)
			calculateOvertimeEachTimeZone(require, cid, integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd(),
					workType.getWorkTypeCode().v(), workTimeCode, autoCalcSet, statutoryFrameNoList, overTimeWork.get(),
					upperControl, overtimeFrameList);
		}
	
		return this.frameTimeSheets.stream().map(tc -> {
			val mapData = tc.changeNotWorkFrameTimeSheet();
			//B.計算残業時間←A.枠時間.残業時間.時間
			mapData.setOverTimeCalc(tc.getFrameTime().getOverTimeWork().getTime());
			//B.計算振替残業時間←A.枠時間.振替時間.時間
			mapData.setTranferTimeCalc(tc.getFrameTime().getTransferTime().getTime());
			//残業枠時間帯の作成
			return mapData;
		}).sorted((first, second) -> first.getFrameNo().v().compareTo(second.getFrameNo().v()))
				.collect(Collectors.toList());
	}
	
	/**
	 * 残業時間帯に入っている加給時間の計算
	 * アルゴリズム：加給時間の計算
	 * @param raisingAutoCalcSet 加給の自動計算設定
	 * @param bonusPayAutoCalcSet 加給自動計算設定
	 * @param bonusPayAtr 加給区分
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 加給時間(List)
	 */
	public List<BonusPayTime> calcBonusPayTimeInOverWorkTime(
			AutoCalRaisingSalarySetting raisingAutoCalcSet,
			BonusPayAutoCalcSet bonusPayAutoCalcSet,
			BonusPayAtr bonusPayAtr,
			CalAttrOfDailyAttd calcAtrOfDaily) {
		
		List<BonusPayTime> bonusPayList = new ArrayList<>();
		ActualWorkTimeSheetAtr sheetAtr;
		for(OverTimeFrameTimeSheetForCalc timeFrame : frameTimeSheets) {
			sheetAtr = ActualWorkTimeSheetAtr.OverTimeWork;
			if(timeFrame.getWithinStatutryAtr().isStatutory()) {
				sheetAtr = ActualWorkTimeSheetAtr.StatutoryOverTimeWork;
				if(timeFrame.isGoEarly()) {
					sheetAtr = ActualWorkTimeSheetAtr.EarlyWork;
				}
			}
			bonusPayList.addAll(timeFrame.calcBonusPay(sheetAtr,raisingAutoCalcSet,bonusPayAutoCalcSet,calcAtrOfDaily,bonusPayAtr));
		}
		return sumBonusPayTime(bonusPayList);
	}
	
	/**
	 * 残業時間帯に入っている特定加給時間の計算
	 * アルゴリズム：加給時間の計算
	 * @param raisingAutoCalcSet 加給の自動計算設定
	 * @param bonusPayAutoCalcSet 加給自動計算設定
	 * @param bonusPayAtr 加給区分
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 特定加給時間(List)
	 */
	public List<BonusPayTime> calcSpecBonusPayTimeInOverWorkTime(
			AutoCalRaisingSalarySetting raisingAutoCalcSet,
			BonusPayAutoCalcSet bonusPayAutoCalcSet,
			BonusPayAtr bonusPayAtr,
			CalAttrOfDailyAttd calcAtrOfDaily) {
		
		List<BonusPayTime> bonusPayList = new ArrayList<>();
		ActualWorkTimeSheetAtr sheetAtr;
		for(OverTimeFrameTimeSheetForCalc timeFrame : frameTimeSheets) {
			sheetAtr = ActualWorkTimeSheetAtr.OverTimeWork;
			if(timeFrame.getWithinStatutryAtr().isStatutory()) {
				sheetAtr = ActualWorkTimeSheetAtr.StatutoryOverTimeWork;
				if(timeFrame.isGoEarly()) {
					sheetAtr = ActualWorkTimeSheetAtr.EarlyWork;
				}
			}
			bonusPayList.addAll(timeFrame.calcSpacifiedBonusPay(sheetAtr,raisingAutoCalcSet,bonusPayAutoCalcSet,calcAtrOfDaily,bonusPayAtr));
		}
		return sumBonusPayTime(bonusPayList);
	}
	
	/**
	 * 同じ加給時間Ｎｏを持つものを１つにまとめる
	 * @param bonusPayTime　加給時間
	 * @return　Noでユニークにした加給時間List
	 */
	private List<BonusPayTime> sumBonusPayTime(List<BonusPayTime> bonusPayTime){
		List<BonusPayTime> returnList = new ArrayList<>();
		List<BonusPayTime> refineList = new ArrayList<>();
		for(int bonusPayNo = 1 ; bonusPayNo <= 10 ; bonusPayNo++) {
			refineList = getByBonusPayNo(bonusPayTime, bonusPayNo);
			if(refineList.size()>0) {
				returnList.add(new BonusPayTime(bonusPayNo,
												new AttendanceTime(refineList.stream().map(tc -> tc.getBonusPayTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))),
												TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(refineList.stream().map(tc -> tc.getWithinBonusPay().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))),
																							  new AttendanceTime(refineList.stream().map(tc -> tc.getWithinBonusPay().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)))),
												TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(refineList.stream().map(tc -> tc.getExcessBonusPayTime().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))),
																							  new AttendanceTime(refineList.stream().map(tc -> tc.getExcessBonusPayTime().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))))
												));
			}
		}
		return returnList;
	}
	
	/**
	 * 受け取った加給時間Ｎｏを持つ加給時間を取得
	 * @param bonusPayTime 加給時間
	 * @param bonusPayNo　加給時間Ｎｏ
	 * @return　加給時間リスト
	 */
	private List<BonusPayTime> getByBonusPayNo(List<BonusPayTime> bonusPayTime,int bonusPayNo){
		return bonusPayTime.stream().filter(tc -> tc.getBonusPayTimeItemNo() == bonusPayNo).collect(Collectors.toList());
	}
	
	/**
	 * 深夜時間計算
	 * @return 計算時間get
	 */
	public TimeDivergenceWithCalculation calcMidNightTime(AutoCalOvertimeSetting autoCalcSet) {
		TimeDivergenceWithCalculation calcTime = TimeDivergenceWithCalculation.defaultValue();
		for(OverTimeFrameTimeSheetForCalc timeSheet:frameTimeSheets) {
			val calcSet = getCalcSetByAtr(autoCalcSet, timeSheet.getWithinStatutryAtr(),timeSheet.isGoEarly());
			calcTime = calcTime.addMinutes(timeSheet.calcMidNightTime(calcSet));
		}
		return calcTime;
	}
	
	/**
	 * 法定内区分、早出区分に従って計算区分の取得
	 * @param autoCalcSet 自動計算設定
	 * @param statutoryAtr　法定内区分
	 * @param goEarly　早出区分
	 * @return　自動計算設定
	 */
	private AutoCalSetting getCalcSetByAtr(AutoCalOvertimeSetting autoCalcSet,StatutoryAtr statutoryAtr, boolean goEarly) {
		//法内である
		if(statutoryAtr.isStatutory() ) {
			return autoCalcSet.getLegalMidOtTime();
		}
		else {
			//早出である
			if(goEarly) {
				return autoCalcSet.getEarlyMidOtTime();
			}
			else {
				return autoCalcSet.getNormalMidOtTime();
			}
		}
	}
	
	/**
	 * 代休の振替処理(残業用)
	 * アルゴリズム：振替処理
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workType 当日の勤務種類
	 * @param afterCalcUpperTimeList 残業時間枠リスト
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @param overtimeFrameList 残業枠リスト
	 */
	public List<OverTimeFrameTime> transProcess(
			OverTimeSheet.TransProcRequire require,
			String employeeId,
			GeneralDate ymd,
			WorkType workType,
			List<OverTimeFrameTime> afterCalcUpperTimeList,
			Optional<WorkTimezoneOtherSubHolTimeSet> eachWorkTimeSet,
			Optional<CompensatoryOccurrenceSetting> eachCompanyTimeSet,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		val useSettingAtr = decisionUseSetting(
				require, employeeId, ymd, workType, eachWorkTimeSet, eachCompanyTimeSet);
		
		if(!useSettingAtr.isPresent())
			return afterCalcUpperTimeList;
		//代休振替設定判定
		switch(useSettingAtr.get().getSubHolTransferSetAtr()) {
			//一定時間を超えたら代休とする
			case CERTAIN_TIME_EXC_SUB_HOL:
				return periodOfTimeTransfer(useSettingAtr.get().getCertainTime(),afterCalcUpperTimeList,
						overtimeFrameList);
			//指定した時間を代休とする
			case SPECIFIED_TIME_SUB_HOL:
				return transAllTime(useSettingAtr.get().getDesignatedTime().getOneDayTime(),
								    useSettingAtr.get().getDesignatedTime().getHalfDayTime(),
									afterCalcUpperTimeList,
									overtimeFrameList);
			default:
				throw new RuntimeException("unknown daikyuSet:");
		}
	}

	/**
	 * 代休の振替処理(残業用)
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workType 当日の勤務種類
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @return 代休振替設定
	 */
	public Optional<SubHolTransferSet> decisionUseSetting(
			OverTimeSheet.TransProcRequire require,
			String employeeId,
			GeneralDate ymd,
			WorkType workType,
			Optional<WorkTimezoneOtherSubHolTimeSet> eachWorkTimeSet,
			Optional<CompensatoryOccurrenceSetting> eachCompanyTimeSet) {
		//平日ではない
		if(!workType.isWeekDayAttendance()) 
			return Optional.empty();
		// 当日が代休管理する日かどうかを判断する
		boolean isManageCmpLeave = require.checkDateForManageCmpLeave(
				require, AppContexts.user().companyId(), employeeId, ymd);
		if (!isManageCmpLeave) return Optional.empty();
		
		val transSet = getTransSet(eachWorkTimeSet,eachCompanyTimeSet);
		//就業時間帯の代休設定取得できない
		if(!transSet.isPresent()||!transSet.get().isUseDivision()) {
			return Optional.empty();
		}
		else {
			//代休振替設定判定
			return transSet;
		}
	}
	
	/**
	 * 代休振替設定の取得
	 * @param eachWorkTimeSet 就業時間帯代休設定
	 * @param eachCompanyTimeSet　会社別代休設定
	 * @return　代休振替設定
	 */
	private Optional<SubHolTransferSet> getTransSet(Optional<WorkTimezoneOtherSubHolTimeSet> eachWorkTimeSet,
										  Optional<CompensatoryOccurrenceSetting> eachCompanyTimeSet) {
		if(eachWorkTimeSet.isPresent() && eachWorkTimeSet.get().getSubHolTimeSet().isUseDivision()) {
			return Optional.of(eachWorkTimeSet.get().getSubHolTimeSet());
		}
		else {
			if(eachCompanyTimeSet.isPresent())
				return Optional.of(eachCompanyTimeSet.get().getTransferSetting());
		}
		return Optional.empty();
	}


	/**
	 * 一定時間の振替処理
	 * @param periodTime 一定時間
	 * @param afterCalcUpperTimeList 残業時間枠リスト
	 * @param overtimeFrameList 残業枠リスト
	 */
	public List<OverTimeFrameTime> periodOfTimeTransfer(
			OneDayTime periodTime,
			List<OverTimeFrameTime> afterCalcUpperTimeList,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		/*振替可能時間の計算*/
		AttendanceTime transAbleTime = calcTransferTimeOfPeriodTime(
				new AttendanceTime(periodTime.v()), afterCalcUpperTimeList, UseTimeAtr.TIME, overtimeFrameList);
		AttendanceTime transAbleCalcTime = calcTransferTimeOfPeriodTime(
				new AttendanceTime(periodTime.v()), afterCalcUpperTimeList, UseTimeAtr.CALCTIME, overtimeFrameList);
		/*振り替える*/
		val afterTransOverTime = trans(transAbleTime, afterCalcUpperTimeList, UseTimeAtr.TIME, overtimeFrameList);
		val afterTransOverCalcTime = trans(transAbleCalcTime, afterTransOverTime, UseTimeAtr.CALCTIME, overtimeFrameList);
		return afterTransOverCalcTime;
	}
	
	/**
	 * 代休の振替可能時間の計算
	 * @param periodTime 一定時間
	 * @param afterCalcUpperTimeList 残業時間枠リスト
	 * @param useTimeAtr 使用時間区分
	 * @param overtimeFrameList 残業枠リスト
	 * @return 振替可能時間
	 */
	private AttendanceTime calcTransferTimeOfPeriodTime(
			AttendanceTime periodTime,
			List<OverTimeFrameTime> afterCalcUpperTimeList,
			UseTimeAtr useTimeAtr,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		int totalFrameTime = this.calcTotalFrameTime(afterCalcUpperTimeList, useTimeAtr, overtimeFrameList);
		if(totalFrameTime > periodTime.valueAsMinutes()) {
			return new AttendanceTime(totalFrameTime).minusMinutes(periodTime.valueAsMinutes());
		}
		else {
			return new AttendanceTime(0);
		}
	}

	/**
	 * 振替対象時間を合計する
	 * @param overtimeFrameTimeList 残業枠時間リスト
	 * @param useTimeAtr 時間区分
	 * @param overtimeFrameList 残業枠リスト
	 * @return 振替対象合計時間
	 */
	private int calcTotalFrameTime(
			List<OverTimeFrameTime> overtimeFrameTimeList,
			UseTimeAtr useTimeAtr,
			List<OvertimeWorkFrame> overtimeFrameList){
		
		int totalFrameTime = 0;
		for (OverTimeFrameTime frameTime: overtimeFrameTimeList){
			// 代休振替対象＝「しない」の時、振替対象から外す
			if(this.isTransTarget(overtimeFrameList, frameTime.getOverWorkFrameNo()) == false) continue;
			// 振替対象枠の時間を合計する
			if (useTimeAtr.isTime()){
				totalFrameTime += frameTime.getOverTimeWork().getTime().valueAsMinutes();
			}
			else{
				totalFrameTime += frameTime.getOverTimeWork().getCalcTime().valueAsMinutes();
			}
		}
		return totalFrameTime;
	}
	
	/**
	 * 振替処理
	 * @param restTransAbleTime 残振替可能時間
	 * @param afterCalcUpperTimeList 残業枠時間リスト
	 * @param useTimeAtr 時間区分
	 * @param overtimeFrameList 残業枠リスト
	 * @return 残業枠時間リスト
	 */
	public List<OverTimeFrameTime> trans(
			AttendanceTime restTransAbleTime,
			List<OverTimeFrameTime> afterCalcUpperTimeList,
			UseTimeAtr useTimeAtr,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		List<OverTimeFrameTime> returnList = new ArrayList<>();
		//振替時間
		AttendanceTime transAbleTime = restTransAbleTime;
		//振替残時間
		AttendanceTime transRestAbleTime = restTransAbleTime;
		for(OverTimeFrameTime overTimeFrameTime : afterCalcUpperTimeList) {
			
			// 代休振替対象＝「しない」の時、振り替えないようにする
			if(this.isTransTarget(overtimeFrameList, overTimeFrameTime.getOverWorkFrameNo()) == false){
				returnList.add(overTimeFrameTime);
				continue;
			}
			
			transAbleTime = calcTransferTime(useTimeAtr, overTimeFrameTime, transRestAbleTime);
			//振替
			val overTime = useTimeAtr.isTime()?overTimeFrameTime.getOverTimeWork().getTime().minusMinutes(transAbleTime.valueAsMinutes())
											  :overTimeFrameTime.getOverTimeWork().getCalcTime().minusMinutes(transAbleTime.valueAsMinutes());
			//振替可能時間から減算
			transRestAbleTime = transRestAbleTime.minusMinutes(transAbleTime.valueAsMinutes());
			returnList.add(calcTransTimeInFrame(useTimeAtr, overTimeFrameTime, overTime, transAbleTime));
		}
		
		return returnList;
	}

	/**
	 * 代休振替対象かどうか
	 * @param overtimeFrameList 残業枠リスト
	 * @param frameNo 残業枠No
	 * @return true:代休振替対象,false:代休振替対象でない
	 */
	private boolean isTransTarget(
			List<OvertimeWorkFrame> overtimeFrameList,
			OverTimeFrameNo frameNo){
		
		Optional<OvertimeWorkFrame> frame = overtimeFrameList.stream()
				.filter(c -> c.getOvertimeWorkFrNo().v().intValue() == frameNo.v())
				.findFirst();
		if (!frame.isPresent()) return false;
		if (frame.get().getTransferAtr() == NotUseAtr.NOT_USE) return false;
		return true;
	}
	
	/**
	 * 振替可能時間算出(振替処理前)
	 * @param useTimeAtr
	 * @param overTimeFrameTime
	 * @param transAbleTime
	 * @param transRestAbleTime
	 * @return
	 */
	private AttendanceTime calcTransferTime(UseTimeAtr useTimeAtr, OverTimeFrameTime overTimeFrameTime, AttendanceTime transRestAbleTime) {
		if(useTimeAtr.isTime()) {
			return overTimeFrameTime.getOverTimeWork().getTime().greaterThanOrEqualTo(transRestAbleTime)
																		  ?transRestAbleTime
																		  :overTimeFrameTime.getOverTimeWork().getTime();
		}
		else {
			return overTimeFrameTime.getOverTimeWork().getCalcTime().greaterThanOrEqualTo(transRestAbleTime)
					  													  ?transRestAbleTime
					  													  :overTimeFrameTime.getOverTimeWork().getCalcTime();
		}
	}
	
	/**
	 * 振替残時間(振替後)の算出
	 * @param useTimeAtr 使用する時間区分
	 * @param overTimeFrameTime　残業時間枠
	 * @param overTime　振替処理後の残業時間
	 * @param transAbleTime　振替時間
	 * @return 振替処理後の残業時間枠
	 */
	private OverTimeFrameTime calcTransTimeInFrame(UseTimeAtr useTimeAtr, OverTimeFrameTime overTimeFrameTime,AttendanceTime overTime, AttendanceTime transAbleTime){
		if(useTimeAtr.isTime()) {
			val changeOverTimeFrame = overTimeFrameTime.changeOverTime(TimeDivergenceWithCalculation.createTimeWithCalculation(overTime,
																															   overTimeFrameTime.getOverTimeWork().getCalcTime()));
			return changeOverTimeFrame.changeTransTime(TimeDivergenceWithCalculation.createTimeWithCalculation(transAbleTime, overTimeFrameTime.getTransferTime().getCalcTime()));
		}
		else {
			val changeOverTimeFrame = overTimeFrameTime.changeOverTime(TimeDivergenceWithCalculation.createTimeWithCalculation(overTimeFrameTime.getOverTimeWork().getTime(),
																															   overTime));
			return changeOverTimeFrame.changeTransTime(TimeDivergenceWithCalculation.createTimeWithCalculation(overTimeFrameTime.getTransferTime().getTime(), transAbleTime));
		}
	}
	
	/**
	 * 指定時間の振替処理
	 * @param oneDay 指定時間1日
	 * @param halfDay 指定時間半日
	 * @param afterCalcUpperTimeList 残業時間枠リスト
	 * @param overtimeFrameList 残業枠リスト
	 * @return 残業枠時間リスト
	 */
	public List<OverTimeFrameTime> transAllTime(
			OneDayTime oneDay,
			OneDayTime halfDay,
			List<OverTimeFrameTime> afterCalcUpperTimeList,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		AttendanceTime transAbleTime = calsTransAllTime(
				oneDay, halfDay, afterCalcUpperTimeList, UseTimeAtr.TIME, overtimeFrameList);
		AttendanceTime transAbleCalcTime = calsTransAllTime(
				oneDay, halfDay, afterCalcUpperTimeList, UseTimeAtr.CALCTIME, overtimeFrameList);
		/*振り替える*/
		val afterTransOverTime = trans(transAbleTime ,afterCalcUpperTimeList ,UseTimeAtr.TIME,overtimeFrameList);
		val afterTransOverCalcTime = trans(transAbleCalcTime ,afterTransOverTime ,UseTimeAtr.CALCTIME,overtimeFrameList);
		return afterTransOverCalcTime;
	}
	
	/**
	 * 指定合計時間の計算
	 * @param oneDay 1日の時間
	 * @param halfDay 半日の時間
	 * @param afterCalcUpperTimeList 残業時間枠リスト
	 * @param useTimeAtr 時間区分
	 * @param overtimeFrameList 残業枠リスト
	 * @return 振替可能時間 
	 */
	private AttendanceTime calsTransAllTime(
			OneDayTime oneDay,
			OneDayTime halfDay,
			List<OverTimeFrameTime> afterCalcUpperTimeList,
			UseTimeAtr useTimeAtr,
			List<OvertimeWorkFrame> overtimeFrameList) {
		
		int totalFrameTime = this.calcTotalFrameTime(afterCalcUpperTimeList, useTimeAtr, overtimeFrameList);
		if(totalFrameTime >= oneDay.valueAsMinutes()) {
			return  new AttendanceTime(oneDay.valueAsMinutes());
		}
		else {
			if(totalFrameTime >= halfDay.valueAsMinutes()) {
				return new AttendanceTime(halfDay.valueAsMinutes());
			}
			else {
				return new AttendanceTime(0);
			}
		}
	}
	
	/**
	 * 変形法定内残業時間の計算
	 * @return　変形法定内残業時間
	 */
	public AttendanceTime calcIrregularTime() {
		val irregularTimeSheetList = this.frameTimeSheets.stream().filter(tc -> tc.getWithinStatutryAtr().isDeformationCriterion()).collect(Collectors.toList());
		return new AttendanceTime(irregularTimeSheetList.stream().map(tc -> tc.overTimeCalculationByAdjustTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
	}
	
	/**
	 * 流動勤務(平日・就外)
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param deductTimeSheet 控除時間帯
	 * @param createdWithinWorkTimeSheet 就業時間内時間帯
	 * @return 残業時間帯
	 */
	public static Optional<OverTimeSheet> createAsFlow(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			DeductionTimeSheet deductTimeSheet,
			WithinWorkTimeSheet createdWithinWorkTimeSheet) {
		
		// 計算範囲の取得
		Optional<TimeSpanForDailyCalc> calcRange = getStartEnd(integrationOfDaily, createdWithinWorkTimeSheet);
		if(!calcRange.isPresent()) return Optional.empty();
		// 指定時間帯に含まれる控除時間帯リストを取得
		List<TimeSheetOfDeductionItem> itemsWithinCalc = deductTimeSheet.getDupliRangeTimeSheet(
				calcRange.get(), DeductionAtr.Deduction);
		
		List<OverTimeFrameTimeSheetForCalc> overTimeFrameTimeSheets = new ArrayList<>();
		
		// 残業枠の設定を取得
		for(FlowOTTimezone processingFlowOTTimezone : integrationOfWorkTime.getFlowWorkSetting().get().getHalfDayWorkTimezoneLstOTTimezone()) {
			// 残業時間帯の開始時刻を計算
			TimeWithDayAttr overTimeStartTime = calcRange.get().getStart();
			if(overTimeFrameTimeSheets.size() != 0) {
				// 枠Noの降順の1件目（前回作成した枠を取得する為）
				overTimeStartTime = overTimeFrameTimeSheets.stream()
						.sorted((f,s) -> s.getOverTimeWorkSheetNo().compareTo(f.getOverTimeWorkSheetNo()))
						.map(frame -> frame.getTimeSheet().getEnd())
						.findFirst().get();
			}
			// 控除時間から残業時間帯を作成
			overTimeFrameTimeSheets.add(OverTimeFrameTimeSheetForCalc.createAsFlow(
					integrationOfWorkTime.getFlowWorkSetting().get(),
					processingFlowOTTimezone,
					deductTimeSheet,
					itemsWithinCalc,
					overTimeStartTime,
					calcRange.get().getEnd(),
					personDailySetting.getBonusPaySetting(),
					integrationOfDaily.getSpecDateAttr(),
					companyCommonSetting.getMidNightTimeSheet()));
			// 退勤時刻を含む残業枠時間帯が作成されているか判断する
			if(overTimeFrameTimeSheets.stream()
					.filter(o -> o.getTimeSheet().contains(calcRange.get().getEnd()))
					.findFirst()
					.isPresent()) {
				break;
			}
		}
		
		//時間休暇溢れ分の割り当て
		List<OverTimeFrameTimeSheetForCalc> afterAllocateVacation = OverTimeSheet.allocateTimeVacationToOverTime(
				integrationOfWorkTime.getFlowWorkSetting().get(),
				personDailySetting.getAddSetting(),
				createdWithinWorkTimeSheet.getTimeVacationAdditionRemainingTime().get(),
				calcRange.get().getStart(),
				integrationOfDaily.getCalAttr().getOvertimeSetting(),
				overTimeFrameTimeSheets);
		
		//変形基準内残業を分割
		List<OverTimeFrameTimeSheetForCalc> afterVariableWork = OverTimeFrameTimeSheetForCalc.dicisionCalcVariableWork(
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				predetermineTimeSetForCalc,
				afterAllocateVacation);
		
		//法定内残業分割処理
		List<OverTimeFrameTimeSheetForCalc> afterCalcStatutoryOverTimeWork = OverTimeFrameTimeSheetForCalc.diciaionCalcStatutory(
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				predetermineTimeSetForCalc,
				afterVariableWork,
				createdWithinWorkTimeSheet);
		
		return Optional.of(new OverTimeSheet(afterCalcStatutoryOverTimeWork));
	}
	
	/**
	 * 時間休暇溢れ分を割り当て
	 * @param flowWorkSetting 流動勤務設定
	 * @param addSetting 加算設定 
	 * @param timeVacationAdditionRemainingTime 休暇使用合計残時間未割当
	 * @param overTimeStartTime 残業開始時刻
	 * @param autoCalcSet 残業時間の自動計算設定
	 * @param overTimeframeTimeSheets 残業枠時間帯(WORK)
	 */
	private static List<OverTimeFrameTimeSheetForCalc> allocateTimeVacationToOverTime(
			FlowWorkSetting flowWorkSetting,
			AddSetting addSetting,
			AttendanceTime timeVacationAdditionRemainingTime,
			TimeWithDayAttr overTimeStartTime,
			AutoCalOvertimeSetting autoCalcSet,
			List<OverTimeFrameTimeSheetForCalc> overTimeframeTimeSheets) {
		
		//input.休暇使用合計残時間未割当のチェック
		if(timeVacationAdditionRemainingTime.lessThanOrEqualTo(AttendanceTime.ZERO))
			return overTimeframeTimeSheets;
		
		//割増計算方法をチェック
		if(addSetting.getCalculationByActualTimeAtr(PremiumAtr.Premium).isCalclationByActualTime())
			return overTimeframeTimeSheets;
		
		if(!overTimeframeTimeSheets.isEmpty()){
			overTimeframeTimeSheets.sort((f,s) -> s.getOverTimeWorkSheetNo().compareTo(f.getOverTimeWorkSheetNo()));
			//勤務した残業時間帯に割り当てる
			timeVacationAdditionRemainingTime = overTimeframeTimeSheets.get(overTimeframeTimeSheets.size()-1).allocateToOverTimeFrame(
					timeVacationAdditionRemainingTime,
					flowWorkSetting.getHalfDayWorkTimezone().getWorkTimeZone(),
					autoCalcSet);
		}
		
		if(timeVacationAdditionRemainingTime.lessThanOrEqualTo(AttendanceTime.ZERO))
			return overTimeframeTimeSheets;
		
		//勤務した残業時間帯以降に割り当てる
		overTimeframeTimeSheets.addAll(
				OverTimeSheet.createFrameWorkSheetAsNonWorkingHours(
					new EmTimezoneNo(overTimeframeTimeSheets.size()),
					timeVacationAdditionRemainingTime,
					overTimeStartTime,
					flowWorkSetting.getHalfDayWorkTimezone().getWorkTimeZone()));
		
		return overTimeframeTimeSheets;
	}
	
	/**
	 * 以降に割当（勤務していない残業枠時間帯を作成して時間休暇溢れ時間を持たせる）
	 * @param overTimeWorkSheetNo 就業時間帯NO
	 * @param timeVacationAdditionRemainingTime 休暇使用合計残時間未割当
	 * @param overTimeStartTime 残業開始時刻
	 * @param flowWorkTimezoneSetting 流動勤務設定
	 * @return 時間休暇溢れ時間を持たせた残業枠時間帯
	 */
	private static List<OverTimeFrameTimeSheetForCalc> createFrameWorkSheetAsNonWorkingHours(
			EmTimezoneNo overTimeWorkSheetNo,
			AttendanceTime timeVacationAdditionRemainingTime,
			TimeWithDayAttr overTimeStartTime,
			FlowWorkTimezoneSetting flowWorkTimezoneSetting){
		
		//残業枠を作成する
		List<OverTimeFrameTimeSheetForCalc> overTimeFrames = flowWorkTimezoneSetting.getLstOTTimezone().stream()
				.filter(timezone -> overTimeWorkSheetNo.lessThan(new EmTimezoneNo(timezone.getWorktimeNo())))
				.map(timezone -> OverTimeFrameTimeSheetForCalc.createEmpty(
						new TimeSpanForDailyCalc(overTimeStartTime, overTimeStartTime),
						timezone))
				.sorted((f,s) -> f.getOverTimeWorkSheetNo().compareTo(s.getOverTimeWorkSheetNo()))
				.collect(Collectors.toList());
		
		//残業枠へ割り当てる
		for(int i=0; i<overTimeFrames.size(); i++) {
			timeVacationAdditionRemainingTime = overTimeFrames.get(i).allocateOverflowTimeVacation(
					i == overTimeFrames.size()-1,
					timeVacationAdditionRemainingTime,
					flowWorkTimezoneSetting);
			
			if(timeVacationAdditionRemainingTime.lessThanOrEqualTo(AttendanceTime.ZERO)) break;
		}
		return overTimeFrames;
	}
	
	/**
	 * 残業開始終了時刻を取得する
	 * @param integrationOfDaily 日別実績(Work)
	 * @param withinWorkTimeSheet 就業時間内時間帯
	 * @return 残業開始終了時刻
	 */
	private static Optional<TimeSpanForDailyCalc> getStartEnd(IntegrationOfDaily integrationOfDaily, WithinWorkTimeSheet withinWorkTimeSheet) {
		//残業開始時刻
		Optional<TimeWithDayAttr> start = withinWorkTimeSheet.getStartEndToWithinWorkTimeFrame().map(span -> span.getEnd());
		if (!start.isPresent()) {
			return Optional.empty();
		}
		//残業終了時刻
		Optional<TimeWithDayAttr> end = integrationOfDaily.getAttendanceLeave().flatMap(attd -> attd.getLastLeaveTime());
		if (!end.isPresent()) {
			return Optional.empty();
		}
		if(start.get().greaterThan(end.get())) {
			return Optional.empty();
		}
		return Optional.of(new TimeSpanForDailyCalc(
				new TimeWithDayAttr(start.get().valueAsMinutes()),
				new TimeWithDayAttr(end.get().valueAsMinutes())));
	}
	
	/**
	 * 申告残業反映後リストの取得
	 * @param recordList 残業枠時間（実績用）
	 * @param declareList 残業枠時間（申告用）
	 * @param declareResult 申告時間帯作成結果
	 */
	private static void getListAfterReflectDeclare(
			List<OverTimeFrameTime> recordList,
			List<OverTimeFrameTime> declareList,
			DeclareTimezoneResult declareResult){

		// 申告Listから反映時間=0:00のデータを削除する
		declareList.removeIf(c ->
		(c.getOverTimeWork().getCalcTime().valueAsMinutes() + c.getTransferTime().getCalcTime().valueAsMinutes() == 0));
		// 残業枠時間を確認する
		for (OverTimeFrameTime record : recordList){
			// 処理中の残業枠NOが申告用Listに存在するか確認
			Optional<OverTimeFrameTime> declare = declareList.stream()
					.filter(c -> c.getOverWorkFrameNo().v() == record.getOverWorkFrameNo().v()).findFirst();
			if (declare.isPresent()){
				// 処理中の残業枠時間に申告用の計算時間を反映
				record.getOverTimeWork().replaceTimeWithCalc(declare.get().getOverTimeWork().getCalcTime());
				record.getTransferTime().replaceTimeWithCalc(declare.get().getTransferTime().getCalcTime());
				// 編集状態．残業に処理中の残業枠NOを追加する
				if (declareResult.getDeclareCalcRange().isPresent()){
					DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
					calcRange.getEditState().getOvertime().add(record.getOverWorkFrameNo());
				}
			}
		}
		// 申告用Listを確認する
		for (OverTimeFrameTime declare : declareList){
			// 処理中の残業枠NOが申告用Listに存在するか確認
			Optional<OverTimeFrameTime> record = recordList.stream()
					.filter(c -> c.getOverWorkFrameNo().v() == declare.getOverWorkFrameNo().v()).findFirst();
			if (!record.isPresent()){
				// 処理中の残業枠時間を反映後Listに追加
				recordList.add(new OverTimeFrameTime(
						declare.getOverWorkFrameNo(),
						TimeDivergenceWithCalculation.createTimeWithCalculation(
								declare.getOverTimeWork().getCalcTime(),
								new AttendanceTime(0)),
						TimeDivergenceWithCalculation.createTimeWithCalculation(
								declare.getTransferTime().getCalcTime(),
								new AttendanceTime(0)),
						new AttendanceTime(0),
						new AttendanceTime(0)));
				// 編集状態．残業に処理中の残業枠NOを追加する
				if (declareResult.getDeclareCalcRange().isPresent()){
					DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
					calcRange.getEditState().getOvertime().add(declare.getOverWorkFrameNo());
				}
			}
		}
	}
	
	/**
	 * 残業時間帯に含まない時間帯の取得
	 * @param timeSpan 計算用時間帯
	 * @return 計算用時間帯List
	 */
	public List<TimeSpanForCalc> getTimeSheetNotDupOverTime(TimeSpanForCalc timeSpan){
		
		List<TimeSpanForCalc> results = new ArrayList<>(Arrays.asList(timeSpan));
		
		// 確認中Listに保存
		List<TimeSpanForCalc> checking = new ArrayList<>(Arrays.asList(timeSpan));
		// 残業枠時間帯を確認する
		for (OverTimeFrameTimeSheetForCalc frame : this.frameTimeSheets){
			// 確認中Listを確認する
			for (TimeSpanForCalc check : checking){
				// 結果Listをクリア
				results = new ArrayList<>();
				// 基準時間帯の比較対象と重複していない部分の取得
				results.addAll(check.getNotDuplicationWith(frame.getTimeSheet().getTimeSpan()));
				// 確認中List ←　結果List
				checking = new ArrayList<>(results);
			}
		}
		// 結果を返す
		return results;
	}
	
	/**
	 * 振替処理Require
	 * @author shuichi_ishida
	 */
	public static interface TransProcRequire extends CheckDateForManageCmpLeaveService.Require, GetSubHolOccurrenceSetting.Require{
		
		/** 代休を管理する年月日かどうかを判断する */
		boolean checkDateForManageCmpLeave(
				CheckDateForManageCmpLeaveService.Require require,
				String companyId, String employeeId, GeneralDate ymd);
		
		//WorkTypeRepository.findByPK
		Optional<WorkType> findByPK(String companyId, String workTypeCd);
	}
}
