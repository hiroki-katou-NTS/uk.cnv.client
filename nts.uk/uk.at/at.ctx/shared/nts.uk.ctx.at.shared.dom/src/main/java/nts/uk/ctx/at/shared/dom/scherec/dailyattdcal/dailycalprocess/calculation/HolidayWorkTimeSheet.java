package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareCalcRange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeSheet.TransProcRequire;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.TimeSeriesDivision;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.service.ActualWorkTimeSheetListService;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.OneDayTime;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSetAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneOtherSubHolTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkHolidayTimeZone;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceDayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSetCheck;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 休日出勤時間帯
 * @author keisuke_hoshina
 */
@Getter
public class HolidayWorkTimeSheet{
	//休出枠時間帯
	private List<HolidayWorkFrameTimeSheetForCalc> workHolidayTime;

	/**
	* Constructor 
 	*/
	public HolidayWorkTimeSheet(List<HolidayWorkFrameTimeSheetForCalc> workHolidayTime) {
		super();
		this.workHolidayTime = workHolidayTime;
	}
		
	/**
	 * 休出枠時間帯をループさせ時間計算をする
	 * アルゴリズム：ループ処理
	 * @param require Require
	 * @param holidayAutoCalcSetting 自動計算設定
	 * @param workType 勤務種類
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @param integrationOfDaily 日別実績(Work)
	 * @param declareResult 申告時間帯作成結果
	 * @param upperControl 事前申請上限制御
	 * @return 休出枠時間(List)
	 */
	public List<HolidayWorkFrameTime> collectHolidayWorkTime(
			OverTimeSheet.TransProcRequire require,
			String cid, 
			AutoCalSetting holidayAutoCalcSetting,
			WorkType workType,
			Optional<String> workTimeCode,
			IntegrationOfDaily integrationOfDaily,
			DeclareTimezoneResult declareResult,
			boolean upperControl){
		
		HolidayWorkTimeOfDaily holidayWorkTime = integrationOfDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getWorkHolidayTime()).orElse(HolidayWorkTimeOfDaily.createDefaultBeforeApp(
					workHolidayTime.stream().map(x -> x.getFrameTime().getHolidayFrameNo().v()).collect(Collectors.toList())));
		if(holidayWorkTime.getHolidayWorkFrameTime().isEmpty()) {
			List<HolidayWorkFrameTime> workFrameTime = workHolidayTime.stream().map(x -> {
				return new HolidayWorkFrameTime(new HolidayWorkFrameNo(x.getFrameTime().getHolidayFrameNo().v()),
						Finally.of(TimeDivergenceWithCalculation.emptyTime()),
						Finally.of(TimeDivergenceWithCalculation.emptyTime()), Finally.of(new AttendanceTime(0)));
			}).collect(Collectors.toList());
			holidayWorkTime.setHolidayWorkFrameTime(workFrameTime);
		}
		
		List<HolidayWorkFrameTime> aftertransTimeList  = new ArrayList<HolidayWorkFrameTime>();
		// 時間帯毎に休出時間を計算する(補正、制御含む)
		calculateHolidayEachTimeZone(require, cid, integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd(),
				workType.getWorkTypeCode().v(), workTimeCode, holidayWorkTime, holidayAutoCalcSetting,
				upperControl);
		//時間帯毎の時間から休出枠毎の時間を集計
		aftertransTimeList.addAll(this.aggregateTimeForHol(holidayWorkTime));

		if (declareResult.getCalcRangeOfOneDay().isPresent()){
			//ループ処理
			CalculationRangeOfOneDay declareCalcRange = declareResult.getCalcRangeOfOneDay().get();
			OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
			if (declareOutsideWork.getHolidayWorkTimeSheet().isPresent()){
				HolidayWorkTimeSheet declareSheet = declareOutsideWork.getHolidayWorkTimeSheet().get();
				//常に「打刻から計算する」で処理する
				CalAttrOfDailyAttd declareCalcSet = CalAttrOfDailyAttd.createAllCalculate();
				List<HolidayWorkFrameTime> declareFrameTimeList = declareSheet.collectHolidayWorkTime(
						require,
						cid, 
						declareCalcSet.getHolidayTimeSetting().getRestTime(),
						workType,
						workTimeCode,
						integrationOfDaily,
						new DeclareTimezoneResult(),
						false);
				//申告休出反映後リストの取得
				HolidayWorkTimeSheet.getListAfterReflectDeclare(aftertransTimeList, declareFrameTimeList, declareResult);
			}
		}
		//マイナスの乖離時間を0にする
		HolidayWorkTimeOfDaily.divergenceMinusValueToZero(aftertransTimeList);

		return aftertransTimeList;
	}
	

	/**
	 * 時間帯毎に休出時間を計算する(補正、制御含む)
	 * 
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param date 年月日
	 * @param workTimeCode 就業時間帯コード：Optional<就業時間帯コード>
	 * 	@param overTimeOfDaily 日別勤怠の休出時間
	 * @param autoCalcSetting 自動計算設定：休出時間の自動計算設定
	 */
	public void calculateHolidayEachTimeZone(TransProcRequire require, String cid, String sid, GeneralDate date,
			String workTypeCode, Optional<String> workTimeCode, HolidayWorkTimeOfDaily holidayWorkTime,
			AutoCalSetting autoCalcSetting, boolean upperControl) {
		
		//時間帯毎に休出時間を計算する
		calculateProcess(autoCalcSetting);
		
		//事前申請上限制御
		if(upperControl) {
			advanceAppUpperLimitControl(holidayWorkTime, autoCalcSetting);
		}
		
		//代休への振替処理
		transferProcSubHol(require, cid, sid, date, workTypeCode, workTimeCode);

	}
	
	//時間帯毎に休出時間を計算する
	public void calculateProcess(AutoCalSetting autoCalcSetting) {
		//休出時間帯の時間枠を取得
		this.workHolidayTime.forEach(frameTime ->{
			//休出時間帯の計算
			frameTime.getFrameTime().setHolidayWorkTime(Finally.of(frameTime.correctCalculationTime(autoCalcSetting)));
		});
		return;
	}
	
	/**
	 * 事前申請上限制御
	 * @param overTimeOfDaily 日別勤怠の休出時間
	 * 	@param autoCalcSet 休出時間の自動計算設定
	 */
	private void advanceAppUpperLimitControl(HolidayWorkTimeOfDaily holidayOfDaily, AutoCalSetting autoCalcSet) {
		
		val holiday = holidayOfDaily.clone();
		//各時間ごとの制御する時間を計算
		List<HolTimeDeductByPriorAppOutput> lstTimeDeductOut = calculateTimeToControlEachTime(holiday, autoCalcSet);
		
		//休出枠時間帯(WORK)を時系列の逆順に取得
		 this.workHolidayTime.sort((x, y) -> y.getTimeSheet().getStart().v() - x.getTimeSheet().getStart().v());
		
		// loop
		 this.workHolidayTime.forEach(frameSheet -> {
			// 控除する時間を計算
			Optional<HolTimeDeductByPriorAppOutput> frameSheetOpt = lstTimeDeductOut.stream()
					.filter(frame -> frame.getHolidayTimeNo().v().intValue() == frameSheet.getFrameTime().getHolidayFrameNo().v().intValue()).findFirst();

			AttendanceTime timeDeductCalc = new AttendanceTime(
					Math.min(frameSheetOpt.map(x -> x.getTimeDeduct().v()).orElse(0),
							(frameSheet.getFrameTime().getHolidayWorkTime().isPresent()
									? frameSheet.getFrameTime().getHolidayWorkTime().get().getTime().v()
									: 0)));
			// 時間帯から控除
			if (frameSheet.getFrameTime().getHolidayWorkTime().isPresent()) {
				frameSheet.getFrameTime().getHolidayWorkTime().get().setTime(new AttendanceTime(
						frameSheet.getFrameTime().getHolidayWorkTime().get().getTime().v() - timeDeductCalc.v()));
			}
			// 控除する時間を減算
			frameSheetOpt.ifPresent(x -> {
				x.setTimeDeduct(new AttendanceTime(x.getTimeDeduct().v() - timeDeductCalc.v()));
			});
		});
		return;
	}
	
	/**
	 * 代休への振替処理
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param date 年月日
	 * @param workTypeCode 勤務種類コード
	 * @param workTimeCode 就業時間帯コード:Optional<就業時間帯コード>
	 */
	public void transferProcSubHol(TransProcRequire require, String cid, String sid, GeneralDate date,
			String workTypeCode, Optional<String> workTimeCode) {

		// 勤務種類を取得
		// ○平日かどうか判断
		Optional<WorkType> workTypeOpt = require.findByPK(cid, workTypeCode);
		if (!workTypeOpt.isPresent())
			return;
		AttendanceDayAttr  workStype = workTypeOpt.get().chechAttendanceDay();
		if(workStype != AttendanceDayAttr.HOLIDAY_WORK){
			return;
		}

		//代休を発生させるをチェック
		if (workTypeOpt.get().getWorkTypeSetAvailable().getGenSubHodiday() == WorkTypeSetCheck.NO_CHECK) {
			return;
		}
		
		// ○当日が代休管理する日かどうかを判断する
		boolean checkDateForMag = require.checkDateForManageCmpLeave(require, cid, sid, date);
		if (!checkDateForMag) {
			return;
		}
		
		// 代休発生設定を取得する
		Optional<SubHolTransferSet> subHolTransSet = GetSubHolOccurrenceSetting.process(require, cid, workTimeCode,
				CompensatoryOccurrenceDivision.WorkDayOffTime);
		if(!subHolTransSet.isPresent()){
			return;
		}
		
		if (subHolTransSet.get().getSubHolTransferSetAtr() == SubHolTransferSetAtr.CERTAIN_TIME_EXC_SUB_HOL) {
			//休出時間を代休へ振り替える(一定時間)
			this.transProcesCertainPeriod(require, cid, workTimeCode);
		} else {
			// 休出時間を代休へ振り替える(指定時間)
			this.transferProcessSpecifi(require, cid, workTimeCode);
		}
	}
	
	//休出時間を代休へ振り替える(一定時間)
	private void transProcesCertainPeriod(TransProcRequire require, String cid, Optional<String> workTimeCode) {
		//振替可能時間を計算
		AttendanceTime sumTime = calculateTransferableTime(require, cid, workTimeCode, UseTimeAtr.TIME);
		
		//残業時間を代休へ振り替える
		transferOvertimeSubsHol(sumTime, UseTimeAtr.TIME, TimeSeriesDivision.TIME_SERIES_REVERSE);
		
		//振替可能時間を計算
		AttendanceTime sumCalcTime = calculateTransferableTime(require, cid, workTimeCode, UseTimeAtr.CALCTIME);
		
		//残業時間を代休へ振り替える
		transferOvertimeSubsHol(sumCalcTime, UseTimeAtr.CALCTIME, TimeSeriesDivision.TIME_SERIES_REVERSE);
		
	}
	
	
	// 振替可能時間を計算
	private AttendanceTime calculateTransferableTime(TransProcRequire require, String cid, Optional<String> workTimeCode, UseTimeAtr atr) {

		//代休発生設定を取得する
		Optional<SubHolTransferSet> subHolidayTrans = GetSubHolOccurrenceSetting.process(require, cid, workTimeCode, CompensatoryOccurrenceDivision.WorkDayOffTime);
		
		//休出時間を合計する
		AttendanceTime sumTime = totalTimeTransferOfHoliday(atr);
		
		//代休振替可能時間を取得
		return subHolidayTrans.map(x -> x.getTransferTime(sumTime)).orElse(new AttendanceTime(0));
	}
	
	// 休出時間を代休へ振り替える
	private AttendanceTime totalTimeTransferOfHoliday(UseTimeAtr atr) {
		int time = this.workHolidayTime.stream().collect(Collectors.summingInt(x -> {
			if (atr == UseTimeAtr.TIME) {
				return x.getFrameTime().getHolidayWorkTime().isPresent()
						? x.getFrameTime().getHolidayWorkTime().get().getTime().v()
						: 0;
			} else {
				return x.getFrameTime().getHolidayWorkTime().isPresent()
						? x.getFrameTime().getHolidayWorkTime().get().getCalcTime().v()
						: 0;
			}
		}));
		return new AttendanceTime(time);
	}
	
	// 休出時間を代休へ振り替える
	private void transferOvertimeSubsHol(AttendanceTime sumTime, UseTimeAtr atr,
			TimeSeriesDivision timeSeries) {
		// 休出時間帯をソート
		val lstFrameSheetDom = this.workHolidayTime.stream().sorted((x, y) -> {
			if (timeSeries == TimeSeriesDivision.TIME_SERIES) {
				return x.getTimeSheet().getStart().v() - y.getTimeSheet().getStart().v();
			} else {
				return y.getTimeSheet().getStart().v() - x.getTimeSheet().getStart().v();
			}
		}).collect(Collectors.toList());

		int transferableTime = sumTime.v();
		int transferTime = 0;
		for (HolidayWorkFrameTimeSheetForCalc sheetForCalc : lstFrameSheetDom) {
			if (transferableTime == 0)
				break;
			int timeUseForCalc = 0;
			if (atr == UseTimeAtr.TIME) {
				timeUseForCalc = sheetForCalc.getFrameTime().getHolidayWorkTime().isPresent()
						? sheetForCalc.getFrameTime().getHolidayWorkTime().get().getTime().v()
						: 0;
				// ○振替可能残時間と残業時間を比較して振替時間を計算
				transferTime = Math.min(timeUseForCalc, transferableTime);
				// ○振替
				if (sheetForCalc.getFrameTime().getHolidayWorkTime().isPresent())
					sheetForCalc.getFrameTime().getHolidayWorkTime().get()
							.replaceTime(new AttendanceTime(timeUseForCalc - transferTime));
				if (sheetForCalc.getFrameTime().getTransferTime().isPresent())
					sheetForCalc.getFrameTime().getTransferTime().get().replaceTime(new AttendanceTime(transferTime));
				// ○振替可能残時間から振替時間を減算
				transferableTime -= transferTime;
			} else {
				timeUseForCalc = sheetForCalc.getFrameTime().getHolidayWorkTime().isPresent()
						?  sheetForCalc.getFrameTime().getHolidayWorkTime().get().getCalcTime().v() : 0;
				// ○振替可能残時間と残業時間を比較して振替時間を計算
				// ○振替可能残時間と残業時間を比較して振替時間を計算
				transferTime = Math.min(timeUseForCalc, transferableTime);
				// ○振替
				if(sheetForCalc.getFrameTime().getHolidayWorkTime().isPresent())
					sheetForCalc.getFrameTime().getHolidayWorkTime().get()
						.replaceCalcTime(new AttendanceTime(timeUseForCalc - transferTime));
				if (sheetForCalc.getFrameTime().getTransferTime().isPresent())
					sheetForCalc.getFrameTime().getTransferTime().get().replaceCalcTime(new AttendanceTime(transferTime));
				// ○振替可能残時間から振替時間を減算
				transferableTime -= transferTime;
			}
		}
	}
	
	// 休出時間を代休へ振り替える(指定時間)
	private void transferProcessSpecifi(TransProcRequire require, String cid, Optional<String> workTimeCode) {
		// 振替可能時間を計算
		AttendanceTime sumTime = calculateTransferableTime(require, cid, workTimeCode, UseTimeAtr.TIME);

		// 休出時間を代休へ振り替える
		transferOvertimeSubsHol(sumTime, UseTimeAtr.TIME, TimeSeriesDivision.TIME_SERIES);

		// 振替可能時間を計算
		AttendanceTime sumCalcTime = calculateTransferableTime(require, cid, workTimeCode,
				UseTimeAtr.CALCTIME);

		// 休出時間を代休へ振り替える
		transferOvertimeSubsHol(sumCalcTime, UseTimeAtr.CALCTIME,
				TimeSeriesDivision.TIME_SERIES);

	}

	/**
	 * 各時間ごとの制御する時間を計算
	 * @param overTimeOfDaily 日別勤怠の休出時間
	 * @param autoCalcSet 休出時間の自動計算設定
	 * @return
	 */
	private List<HolTimeDeductByPriorAppOutput> calculateTimeToControlEachTime(HolidayWorkTimeOfDaily holidayOfDaily,
			AutoCalSetting autoCalcSet) {
		//時間帯毎の時間から休出枠毎の時間を集計
		List<HolidayWorkFrameTime>aggTime = aggregateTimeForHol(holidayOfDaily);
		//後で比較する為の時間create
		List<HolidayWorkFrameTime>aggTimeAfterA = aggTime.stream().map(x -> {
			 return x.clone();
		 }).collect(Collectors.toList());
		
     	//事前休出時間をセットする
		aggTime.forEach(holTime -> {
			val beforeApp = holidayOfDaily.getHolidayWorkFrameTime().stream()
					.filter(x -> x.getHolidayFrameNo().v().intValue() == holTime.getHolidayFrameNo().v().intValue()).findFirst()
					.map(x -> x.getBeforeApplicationTime()).orElse(Finally.of(new AttendanceTime(0)));
			if(holTime.getBeforeApplicationTime().isPresent()){
				holTime.setBeforeApplicationTime(Finally.of(beforeApp.isPresent() ? beforeApp.get() : new AttendanceTime(0)));
			}; 
		});
		
		//補正処理を実行する為に、日別勤怠の休出時間のインスタンスを作成
		//事前申請を上限とする補正処理
		List<HolidayWorkFrameTime> lstB =  new HolidayWorkFrameTimeList(aggTime).afterUpperControl(autoCalcSet);
		
		// 制御する前の時間と比較して制御する時間を計算
		return aggTimeAfterA.stream().map(x -> {
			val valueB = lstB.stream().filter(b -> b.getHolidayFrameNo().v().intValue() == x.getHolidayFrameNo().v().intValue()).findFirst();
			if (!valueB.isPresent())
				return new HolTimeDeductByPriorAppOutput(x.getHolidayFrameNo(), new AttendanceTime(0));

			val finalTime = (x.getHolidayWorkTime().isPresent() ? x.getHolidayWorkTime().get().getTime().v() : 0)
					- (valueB.get().getHolidayWorkTime().isPresent()
							? valueB.get().getHolidayWorkTime().get().getTime().v()
							: 0);
			return new HolTimeDeductByPriorAppOutput(x.getHolidayFrameNo(), new AttendanceTime(finalTime));

		}).collect(Collectors.toList());

	}

	// 時間帯毎の時間から休出枠毎の時間を集計
	public List<HolidayWorkFrameTime> aggregateTimeForHol(HolidayWorkTimeOfDaily holidayOfDaily) {
		val hol = holidayOfDaily.clone();
		//clean time old
		hol.getHolidayWorkFrameTime().forEach(x -> x.cleanTimeAndTransfer());
		// 休出時間帯でループ
		this.workHolidayTime.forEach(frameTime -> {
			// 休出時間へ加算
			val holTime = hol.getHolidayWorkFrameTime().stream()
					.filter(x -> x.getHolidayFrameNo().v().intValue() == frameTime.getFrameTime().getHolidayFrameNo().v().intValue()).findFirst();
			holTime.ifPresent(data -> {
				// B休出時間+=A.休出時間
				if (data.getHolidayWorkTime().isPresent()
						&& frameTime.getFrameTime().getHolidayWorkTime().isPresent()) {
					data.getHolidayWorkTime().get()
							.addMinutesNotReturn(frameTime.getFrameTime().getHolidayWorkTime().get());
				}
				if (data.getTransferTime().isPresent() && frameTime.getFrameTime().getTransferTime().isPresent()) {
					// B.振替時間+=A.振替時間
					data.getTransferTime().get().addMinutesNotReturn(frameTime.getFrameTime().getTransferTime().get());
				}
			});
		});

		// 休出枠時間を返す

		return hol.getHolidayWorkFrameTime();
	}
	
	/**
	 * 代休の振替処理(残業用)
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workType 当日の勤務種類
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @param isManageCmpLeave 代休管理するかどうか
	 */
	public static Optional<SubHolTransferSet> decisionUseSetting(
			OverTimeSheet.TransProcRequire require,
			String employeeId,
			GeneralDate ymd,
			WorkType workType,
			Optional<WorkTimezoneOtherSubHolTimeSet> eachWorkTimeSet,
			Optional<CompensatoryOccurrenceSetting> eachCompanyTimeSet) {
		
		//平日ではない
		if(!workType.getDailyWork().isHolidayWork() || !workType.isGenSubHolidayForHolidayWork()) 
			return Optional.empty();
		// 当日が代休管理する日かどうか判断する
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
	 * 休出枠時間帯(WORK)を全て休出枠時間帯へ変換す
	 * アルゴリズム：休出枠時間帯の作成
	 * @return　休出枠時間帯List
	 */
	public List<HolidayWorkFrameTimeSheet> changeHolidayWorkTimeFrameTimeSheet(OverTimeSheet.TransProcRequire require,
			String cid, 
			AutoCalSetting holidayAutoCalcSetting,
			WorkType workType,
			Optional<String> workTimeCode,
			IntegrationOfDaily integrationOfDaily,
			boolean upperControl){
		//時間帯毎に休出時間を計算する(補正、制御含む)
		HolidayWorkTimeOfDaily holidayWorkTime = integrationOfDaily.getAttendanceTimeOfDailyPerformance()
				.flatMap(x -> x.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getWorkHolidayTime()).orElse(HolidayWorkTimeOfDaily.createDefaultBeforeApp(
					workHolidayTime.stream().map(x -> x.getFrameTime().getHolidayFrameNo().v()).collect(Collectors.toList())));
		// 時間帯毎に休出時間を計算する(補正、制御含む)
		calculateHolidayEachTimeZone(require, cid, integrationOfDaily.getEmployeeId(), integrationOfDaily.getYmd(),
				workType.getWorkTypeCode().v(), workTimeCode, holidayWorkTime, holidayAutoCalcSetting,
				upperControl);

		
		return this.workHolidayTime.stream()
											.map(tc -> {
												val mapData = tc.changeNotWorkFrameTimeSheet();
												//B.計算休出時間←A.枠時間.休出時間.計算時間
												if(tc.getFrameTime().getHolidayWorkTime().isPresent())
													mapData.setHdTimeCalc(tc.getFrameTime().getHolidayWorkTime().get().getCalcTime());
												//B.計算振替時間←A.枠時間.振替時間.計算時間
												if(tc.getFrameTime().getTransferTime().isPresent())
													mapData.setTranferTimeCalc(tc.getFrameTime().getTransferTime().get().getCalcTime());
												//休出枠時間帯を作成
												return mapData;
											})
											.sorted((first,second) -> first.getHolidayWorkTimeSheetNo().v().compareTo(second.getHolidayWorkTimeSheetNo().v()))
											.collect(Collectors.toList());
	}
	
	/**
	 * 控除時間を取得
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @return 控除時間
	 */
	public AttendanceTime getDeductionTime(
			ConditionAtr conditionAtr, DeductionAtr dedAtr, TimeSheetRoundingAtr roundAtr, NotUseAtr canOffset) {
		
		return ActualWorkTimeSheetListService.calcDeductionTime(conditionAtr, dedAtr, roundAtr,
				this.workHolidayTime.stream().map(tc -> (ActualWorkingTimeSheet)tc).collect(Collectors.toList()), canOffset);
	}
	
	/**
	 * 控除回数を計算
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @return 控除時間
	 */
	public int calcDeductionCount(ConditionAtr conditionAtr, DeductionAtr dedAtr) {
		
		return ActualWorkTimeSheetListService.calcDeductionCount(conditionAtr, dedAtr,
				this.workHolidayTime.stream().map(tc -> (ActualWorkingTimeSheet)tc).collect(Collectors.toList()));
	}
	
	/**
	 * 休出時間帯に入っている加給時間の計算
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 加給時間(List)
	 */
	public List<BonusPayTime> calcBonusPayTimeInHolidayWorkTime(List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily) {
		
		List<BonusPayTime> bonusPayList = new ArrayList<>();
		for(HolidayWorkFrameTimeSheetForCalc timeFrame : workHolidayTime) {
			bonusPayList.addAll(timeFrame.calcBonusPay(ActualWorkTimeSheetAtr.HolidayWork, bpTimeItemSets, calcAtrOfDaily));
		}
		//同じNo同士はここで加算し、Listのサイズを減らす
		return sumBonusPayTime(bonusPayList);
	}
	
	/**
	 * 休出時間帯に入っている特定加給時間の計算
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 特定加給時間(List)
	 */
	public List<BonusPayTime> calcSpecBonusPayTimeInHolidayWorkTime(List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily) {
		
		List<BonusPayTime> bonusPayList = new ArrayList<>();
		for(HolidayWorkFrameTimeSheetForCalc timeFrame : workHolidayTime) {
			bonusPayList.addAll(timeFrame.calcSpacifiedBonusPay(ActualWorkTimeSheetAtr.HolidayWork, bpTimeItemSets, calcAtrOfDaily));
		}
		//同じNo同士はここで加算し、Listのサイズを減らす
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

	
	public AttendanceTime desictionUseUppserTime(AutoCalSetting autoCalcSet, HolidayWorkFrameTime loopHolidayTimeFrame,AttendanceTime attendanceTime) {
		switch(autoCalcSet.getUpLimitORtSet()) {
		//上限なし
		case NOUPPERLIMIT:
			return attendanceTime;
		//指示時間を上限とする
		case INDICATEDYIMEUPPERLIMIT:
		//事前申請を上限とする
		case LIMITNUMBERAPPLICATION:
			return loopHolidayTimeFrame.getBeforeApplicationTime().get();
		default:
			throw new RuntimeException("uknown AutoCalcAtr Over Time When Ot After Upper Control");
		}
	}
	
	/**
	 * 代休の振替処理(休出用)
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param ymd 年月日
	 * @param workType 当日の勤務種類
	 * @param afterCalcUpperTimeList 休出枠時間(List)
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @return 休出枠時間(List)
	 */
	public static List<HolidayWorkFrameTime> transProcess(
			OverTimeSheet.TransProcRequire require,
			String employeeId,
			GeneralDate ymd,
			WorkType workType,
			List<HolidayWorkFrameTime> afterCalcUpperTimeList,
			Optional<WorkTimezoneOtherSubHolTimeSet> eachWorkTimeSet,
			Optional<CompensatoryOccurrenceSetting> eachCompanyTimeSet) {
		
		val useSettingAtr = decisionUseSetting(
				require, employeeId, ymd, workType, eachWorkTimeSet, eachCompanyTimeSet);
		
		if(!useSettingAtr.isPresent())
			return afterCalcUpperTimeList;
		//代休振替設定判定
		switch(useSettingAtr.get().getSubHolTransferSetAtr()) {
			//一定時間を超えたら代休とする
			case CERTAIN_TIME_EXC_SUB_HOL:
				return periodOfTimeTransfer(useSettingAtr.get().getCertainTime(),afterCalcUpperTimeList);
			//指定した時間を代休とする
			case SPECIFIED_TIME_SUB_HOL:
				return transAllTime(useSettingAtr.get().getDesignatedTime().getOneDayTime(),
									useSettingAtr.get().getDesignatedTime().getHalfDayTime(),
									afterCalcUpperTimeList);
			default:
				throw new RuntimeException("unknown daikyuSet:");
		}
	}
	
	/**
	 * 代休振替設定の取得
	 * @param eachWorkTimeSet 就業時間帯代休設定
	 * @param eachCompanyTimeSet　会社別代休設定
	 * @return　代休振替設定
	 */
	private static Optional<SubHolTransferSet> getTransSet(Optional<WorkTimezoneOtherSubHolTimeSet> eachWorkTimeSet,
										  Optional<CompensatoryOccurrenceSetting> eachCompanyTimeSet) {
		//就業時間帯の振替設定参照
		if(eachWorkTimeSet.isPresent() && eachWorkTimeSet.get().getSubHolTimeSet().isUseDivision()) {
			return Optional.of(eachWorkTimeSet.get().getSubHolTimeSet());
		}
		//会社共通の振替設定参照
		else {
			if(eachCompanyTimeSet.isPresent()) {
				if(eachCompanyTimeSet.get().getTransferSetting().isUseDivision()) {
					return Optional.of(eachCompanyTimeSet.get().getTransferSetting());
				}
				else {
					return Optional.empty();
				}
			}
		}
		return Optional.empty();
	}


	/**
	 * 一定時間の振替処理
	 * @param 一定時間
	 */
	public static List<HolidayWorkFrameTime> periodOfTimeTransfer(OneDayTime periodTime,List<HolidayWorkFrameTime> afterCalcUpperTimeList) {
		/*振替可能時間の計算*/
		AttendanceTime transAbleTime = calcTransferTimeOfPeriodTime(new AttendanceTime(periodTime.v()),afterCalcUpperTimeList,UseTimeAtr.TIME);
		AttendanceTime transAbleCalcTime = calcTransferTimeOfPeriodTime(new AttendanceTime(periodTime.v()),afterCalcUpperTimeList,UseTimeAtr.CALCTIME);
		/*振り替える*/
		val afterTransOverTime = trans(transAbleTime ,afterCalcUpperTimeList ,UseTimeAtr.TIME);
		val afterTransOverCalcTime = trans(transAbleCalcTime ,afterTransOverTime ,UseTimeAtr.CALCTIME);
		return afterTransOverCalcTime;
	}
	
	/**
	 * 代休の振替可能時間の計算
	 * @param periodTime 一定時間
	 * @param afterCalcUpperTimeList 残業時間枠リスト
	 * @param useTimeAtr 使用時間区分
	 * @return 振替可能時間
	 */
	private static AttendanceTime calcTransferTimeOfPeriodTime(AttendanceTime periodTime,List<HolidayWorkFrameTime> afterCalcUpperTimeList, UseTimeAtr useTimeAtr) {
		int totalFrameTime =  useTimeAtr.isTime()
								?afterCalcUpperTimeList.stream().map(tc -> tc.getHolidayWorkTime().get().getTime().v()).collect(Collectors.summingInt(tc -> tc))
								:afterCalcUpperTimeList.stream().map(tc -> tc.getHolidayWorkTime().get().getCalcTime().v()).collect(Collectors.summingInt(tc -> tc));
		if(new AttendanceTime(totalFrameTime).greaterThanOrEqualTo(periodTime)) {
			return new AttendanceTime(totalFrameTime).minusMinutes(periodTime.valueAsMinutes());
		}
		else {
			return new AttendanceTime(0);
		}
	}

	
	public static List<HolidayWorkFrameTime> trans(AttendanceTime restTransAbleTime, List<HolidayWorkFrameTime> afterCalcUpperTimeList,UseTimeAtr useTimeAtr) {
		List<HolidayWorkFrameTime> returnList = new ArrayList<>();
		//振替時間
		AttendanceTime transAbleTime = restTransAbleTime;
		//振替残時間
		AttendanceTime transRestAbleTime = restTransAbleTime;
		for(HolidayWorkFrameTime holidayWorkFrameTime : afterCalcUpperTimeList) {

			transAbleTime = calcTransferTime(useTimeAtr, holidayWorkFrameTime, transRestAbleTime);
			//振替
			val overTime = useTimeAtr.isTime()?holidayWorkFrameTime.getHolidayWorkTime().get().getTime().minusMinutes(transAbleTime.valueAsMinutes())
											  :holidayWorkFrameTime.getHolidayWorkTime().get().getCalcTime().minusMinutes(transAbleTime.valueAsMinutes());
			//振替可能時間から減算
			transRestAbleTime = transRestAbleTime.minusMinutes(transAbleTime.valueAsMinutes());
			returnList.add(calcTransTimeInFrame(useTimeAtr, holidayWorkFrameTime, overTime, transAbleTime));
		}
		
		return returnList;
	}
	
	/**
	 * 振替可能時間算出(振替処理前)
	 * @param useTimeAtr
	 * @param overTimeFrameTime
	 * @param transAbleTime
	 * @param transRestAbleTime
	 * @return
	 */
	private static AttendanceTime calcTransferTime(UseTimeAtr useTimeAtr, HolidayWorkFrameTime holidayWorkFrameTime,AttendanceTime transRestAbleTime) {
		if(useTimeAtr.isTime()) {
			return holidayWorkFrameTime.getHolidayWorkTime().get().getTime().greaterThanOrEqualTo(transRestAbleTime)
																		  ?transRestAbleTime
																		  :holidayWorkFrameTime.getHolidayWorkTime().get().getTime();
		}
		else {
			return holidayWorkFrameTime.getHolidayWorkTime().get().getCalcTime().greaterThanOrEqualTo(transRestAbleTime)
					  													  ?transRestAbleTime
					  													  :holidayWorkFrameTime.getHolidayWorkTime().get().getCalcTime();
		}
	}
	
	
	/**
	 * 振替残時間(振替後)の算出
	 * @param useTimeAtr 使用する時間区分
	 * @param overTimeFrameTime　残業時間枠
	 * @param overTime　残業時間
	 * @param transAbleTime　振替時間
	 * @return 振替処理後の残業時間枠
	 */
	private static HolidayWorkFrameTime calcTransTimeInFrame(UseTimeAtr useTimeAtr, HolidayWorkFrameTime holidayWorkTimeFrame,AttendanceTime holidayWorkTime, AttendanceTime transAbleTime){
		if(useTimeAtr.isTime()) {
			val changeOverTimeFrame = holidayWorkTimeFrame.changeOverTime(TimeDivergenceWithCalculation.createTimeWithCalculation(holidayWorkTime , 
																																  holidayWorkTimeFrame.getHolidayWorkTime().get().getCalcTime()));
			return changeOverTimeFrame.changeTransTime(TimeDivergenceWithCalculation.createTimeWithCalculation(transAbleTime,
																											   holidayWorkTimeFrame.getTransferTime().get().getCalcTime()));
		}
		else {
			val changeOverTimeFrame = holidayWorkTimeFrame.changeOverTime(TimeDivergenceWithCalculation.createTimeWithCalculation(holidayWorkTimeFrame.getHolidayWorkTime().get().getTime(),
																																  holidayWorkTime));
			
			return changeOverTimeFrame.changeTransTime(TimeDivergenceWithCalculation.createTimeWithCalculation(holidayWorkTimeFrame.getTransferTime().get().getTime(),
																											   transAbleTime));
		}
	}
	
	/**
	 * 指定時間の振替処理
	 * @param prioritySet 優先設定
	 */
	public static List<HolidayWorkFrameTime> transAllTime(OneDayTime oneDay,OneDayTime halfDay,List<HolidayWorkFrameTime> afterCalcUpperTimeList) {
		AttendanceTime transAbleTime = calsTransAllTime(oneDay,halfDay,afterCalcUpperTimeList,UseTimeAtr.TIME);
		AttendanceTime transAbleCalcTime = calsTransAllTime(oneDay,halfDay,afterCalcUpperTimeList,UseTimeAtr.CALCTIME);
		/*振り替える*/
		val afterTransOverTime = trans(transAbleTime ,afterCalcUpperTimeList ,UseTimeAtr.TIME);
		val afterTransOverCalcTime = trans(transAbleCalcTime ,afterTransOverTime ,UseTimeAtr.CALCTIME);
		return afterTransOverCalcTime;
	}
	
	/**
	 * 指定合計時間の計算
	 * @param 指定時間クラス 
	 */
	private static AttendanceTime calsTransAllTime(OneDayTime oneDay,OneDayTime halfDay,List<HolidayWorkFrameTime> afterCalcUpperTimeList,UseTimeAtr useTimeAtr) {
		int totalFrameTime = useTimeAtr.isTime()
										?afterCalcUpperTimeList.stream().map(tc -> tc.getHolidayWorkTime().get().getTime().v()).collect(Collectors.summingInt(tc -> tc))
										:afterCalcUpperTimeList.stream().map(tc -> tc.getHolidayWorkTime().get().getCalcTime().v()).collect(Collectors.summingInt(tc -> tc));
		if(totalFrameTime >= oneDay.valueAsMinutes()) {
			return  new AttendanceTime(oneDay.v());
		}
		else {
			if(totalFrameTime >= halfDay.valueAsMinutes()) {
				return new AttendanceTime(halfDay.v());
			}
			else {
				return new AttendanceTime(0);
			}
		}
	}
	
	/**
	 * 流動勤務(休日出勤)
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param deductTimeSheet 控除時間帯
	 * @param holidayStartEnd 休出開始終了
	 * @param oneDayOfRange 1日の計算範囲
	 * @return 休日出勤時間帯
	 */
	public static HolidayWorkTimeSheet createAsFlow(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductTimeSheet,
			TimeSpanForDailyCalc holidayStartEnd,
			TimeSpanForDailyCalc oneDayOfRange) {
		
		// 出退勤時間帯と1日の計算範囲の重複部分を計算範囲とする
		TimeSpanForDailyCalc calcRange = holidayStartEnd.getDuplicatedWith(oneDayOfRange).orElse(holidayStartEnd);
		// 指定時間帯に含まれる控除時間帯リストを取得
		List<TimeSheetOfDeductionItem> itemsWithinCalc = deductTimeSheet.getDupliRangeTimeSheet(
				calcRange, DeductionAtr.Deduction);
		
		List<HolidayWorkFrameTimeSheetForCalc> holidayWorkFrameTimeSheets = new ArrayList<>();
		
		// 休出枠の設定を取得
		for(FlowWorkHolidayTimeZone processingTimezone : integrationOfWorkTime.getFlowWorkSetting().get().getOffdayWorkTimezoneLstWorkTimezone()) {
			// 休出時間帯の開始時刻を計算
			TimeWithDayAttr holidayStart = calcRange.getStart();
			if(holidayWorkFrameTimeSheets.size() != 0) {
				//枠Noの降順の1件目（前回作成した枠を取得する為）
				holidayStart = holidayWorkFrameTimeSheets.stream()
						.sorted((f,s) -> s.getHolidayWorkTimeSheetNo().compareTo(f.getHolidayWorkTimeSheetNo()))
						.map(frame -> frame.getTimeSheet().getEnd())
						.findFirst().get();
			}
			// 控除時間から休出時間帯を作成
			holidayWorkFrameTimeSheets.add(HolidayWorkFrameTimeSheetForCalc.createAsFlow(
					todayWorkType,
					integrationOfWorkTime.getFlowWorkSetting().get(),
					deductTimeSheet,
					itemsWithinCalc,
					new TimeSpanForDailyCalc(holidayStart, calcRange.getEnd()),
					personDailySetting.getBonusPaySetting(),
					integrationOfDaily.getSpecDateAttr(),
					companyCommonSetting.getMidNightTimeSheet(),
					processingTimezone));
			// 退勤時刻を含む休出枠時間帯が作成されているか判断する
			if (holidayWorkFrameTimeSheets.stream()
					.filter(o -> o.getTimeSheet().contains(calcRange.getEnd()))
					.findFirst()
					.isPresent()) {
				break;
			}
		}
		return new HolidayWorkTimeSheet(holidayWorkFrameTimeSheets);
	}
	
	/**
	 * 申告休出反映後リストの取得
	 * @param recordList 休出枠時間（実績用）
	 * @param declareList 休出枠時間（申告用）
	 * @param declareResult 申告時間帯作成結果
	 */
	private static void getListAfterReflectDeclare(
			List<HolidayWorkFrameTime> recordList,
			List<HolidayWorkFrameTime> declareList,
			DeclareTimezoneResult declareResult){

		// 申告Listから反映時間=0:00のデータを削除する
		declareList.removeIf(c ->
		(c.getHolidayWorkTime().get().getCalcTime().valueAsMinutes() + c.getTransferTime().get().getCalcTime().valueAsMinutes() == 0));
		// 休出枠時間を確認する
		for (HolidayWorkFrameTime record : recordList){
			// 処理中の休出枠NOが申告用Listに存在するか確認
			Optional<HolidayWorkFrameTime> declare = declareList.stream()
					.filter(c -> c.getHolidayFrameNo().v().intValue() == record.getHolidayFrameNo().v().intValue()).findFirst();
			if (declare.isPresent()){
				// 処理中の休出枠時間に申告用の計算時間を反映
				record.getHolidayWorkTime().get().replaceTimeWithCalc(declare.get().getHolidayWorkTime().get().getCalcTime());
				record.getTransferTime().get().replaceTimeWithCalc(declare.get().getTransferTime().get().getCalcTime());
				// 編集状態．休出に処理中の休出枠NOを追加する
				if (declareResult.getDeclareCalcRange().isPresent()){
					DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
					calcRange.getEditState().getHolidayWork().add(record.getHolidayFrameNo());
				}
			}
		}
		// 申告用Listを確認する
		for (HolidayWorkFrameTime declare : declareList){
			// 処理中の休出枠NOが申告用Listに存在するか確認
			Optional<HolidayWorkFrameTime> record = recordList.stream()
					.filter(c -> c.getHolidayFrameNo().v().intValue() == declare.getHolidayFrameNo().v().intValue()).findFirst();
			if (!record.isPresent()){
				// 処理中の休出枠時間を反映後Listに追加
				recordList.add(new HolidayWorkFrameTime(
						declare.getHolidayFrameNo(),
						Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(
								declare.getHolidayWorkTime().get().getCalcTime(),
								new AttendanceTime(0))),
						Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(
								declare.getTransferTime().get().getCalcTime(),
								new AttendanceTime(0))),
						Finally.of(new AttendanceTime(0))));
				// 編集状態．休出に処理中の休出枠NOを追加する
				if (declareResult.getDeclareCalcRange().isPresent()){
					DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
					calcRange.getEditState().getHolidayWork().add(declare.getHolidayFrameNo());
				}
			}
		}
	}
}
