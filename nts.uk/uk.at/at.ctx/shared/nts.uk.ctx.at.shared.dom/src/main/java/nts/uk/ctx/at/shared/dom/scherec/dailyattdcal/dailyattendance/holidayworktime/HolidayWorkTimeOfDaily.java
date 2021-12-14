package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubsTransferProcessMode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.BonusPayAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.AttendanceItemDictionaryForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareCalcRange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareFrameSet;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * 日別実績の休出時間
 * @author keisuke_hoshina
 *
 */
@Getter
@Setter
public class HolidayWorkTimeOfDaily implements Cloneable{
	//休出枠時間帯
	private List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet;
	//休出枠時間
	private List<HolidayWorkFrameTime> holidayWorkFrameTime;
	//休出深夜
	private Finally<HolidayMidnightWork> holidayMidNightWork;
	//休出拘束時間
	private AttendanceTime holidayTimeSpentAtWork;
	
	/**
	 * Constructor 
	 */
	public HolidayWorkTimeOfDaily(List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet,List<HolidayWorkFrameTime> holidayWorkFrameTime,
								   Finally<HolidayMidnightWork> holidayMidNightWork, AttendanceTime holidayTimeSpentAtWork) {
		this.holidayWorkFrameTimeSheet = holidayWorkFrameTimeSheet;
		this.holidayWorkFrameTime = holidayWorkFrameTime;
		this.holidayMidNightWork = holidayMidNightWork;
		this.holidayTimeSpentAtWork = holidayTimeSpentAtWork;
	}
	

	/**
	 * メンバー変数の時間計算を指示するクラス
	 * アルゴリズム：日別実績の休出時間
	 * @param recordReGet 実績
	 * @param holidayWorkTimeSheet 休日出勤時間帯
	 * @param holidayAutoCalcSetting 自動計算設定（休出時間）
	 * @param workType 勤務種類
	 * @param eachWorkTimeSet 就業時間帯別代休時間設定
	 * @param eachCompanyTimeSet 会社別代休時間設定
	 * @param integrationOfDaily 日別実績(Work)
	 * @param beforeApplicationTime 事前深夜時間
	 * @param holidayLateNightAutoCalSetting 自動計算設定（休出深夜時間）
	 * @param declareResult 申告時間帯作成結果
	 * @return 日別実績の休出時間
	 */
	public static HolidayWorkTimeOfDaily calculationTime(
			ManageReGetClass recordReGet,
			HolidayWorkTimeSheet holidayWorkTimeSheet,
			AutoCalSetting holidayAutoCalcSetting,
			WorkType workType,
			Optional<String> workTimeCode,
			IntegrationOfDaily integrationOfDaily,
			AttendanceTime beforeApplicationTime,
			AutoCalSetting holidayLateNightAutoCalSetting,
			DeclareTimezoneResult declareResult) {
		
		//休出枠時間帯の作成
		val holidayWorkFrameTimeSheet = holidayWorkTimeSheet.changeHolidayWorkTimeFrameTimeSheet(
				recordReGet.getPersonDailySetting().getOverTimeSheetReq(),
				workType.getCompanyId(),
				holidayAutoCalcSetting,
				workType,
				workTimeCode,
				integrationOfDaily,
				true);
		//休出時間の計算
		val holidayWorkFrameTime = holidayWorkTimeSheet.collectHolidayWorkTime(
				recordReGet.getPersonDailySetting().getOverTimeSheetReq(),
				workType.getCompanyId(),
				holidayAutoCalcSetting,
				workType,
				workTimeCode,
				integrationOfDaily,
				declareResult,
				true);
		
		//休日出勤深夜時間の計算
		val holidayMidnightWork = Finally.of(calcMidNightTimeIncludeHolidayWorkTime(
				holidayWorkTimeSheet,
				beforeApplicationTime,
				holidayLateNightAutoCalSetting,
				declareResult));
		//使用時間
		val holidayTimeSpentTime = new AttendanceTime(0);
		return new HolidayWorkTimeOfDaily(holidayWorkFrameTimeSheet, holidayWorkFrameTime, holidayMidnightWork, holidayTimeSpentTime);
	}
	
	/**
	 * 休出時間に含まれている加給時間帯を計算する
	 * @return　加給時間クラス
	 */
	public List<BonusPayTime> calcBonusPay(AutoCalRaisingSalarySetting bonusPayAutoCalcSet,BonusPayAtr bonusPayAtr,CalAttrOfDailyAttd calcAtrOfDaily){
		List<BonusPayTime> bonusPayList = new ArrayList<>();
//		for(HolidayWorkFrameTimeSheet frameTimeSheet: holidayWorkFrameTimeSheet) {
//			//bonusPayList.addAll(frameTimeSheet.calcBonusPay(ActualWorkTimeSheetAtr.HolidayWork,bonusPayAutoCalcSet,calcAtrOfDaily));
//		}
		return bonusPayList;
	}
	
	/**
	 * 休出時間に含まれている特定日加給時間帯を計算する
	 * @return　加給時間クラス
	 */
	public List<BonusPayTime> calcSpecifiedBonusPay(AutoCalRaisingSalarySetting bonusPayAutoCalcSet,BonusPayAtr bonusPayAtr,CalAttrOfDailyAttd calcAtrOfDaily){
		List<BonusPayTime> bonusPayList = new ArrayList<>();
//		for(HolidayWorkFrameTimeSheet frameTimeSheet: holidayWorkFrameTimeSheet) {
//			//bonusPayList.addAll(frameTimeSheet.calcSpacifiedBonusPay(ActualWorkTimeSheetAtr.HolidayWork,bonusPayAutoCalcSet,calcAtrOfDaily));
//		}
		return bonusPayList;
	}
	
	/**
	 * 休出時間が含んでいる深夜時間の算出
	 * アルゴリズム：休日出勤深夜時間の計算（事前申請制御後）
	 * アルゴリズム：深夜時間の計算
	 * @param holidayWorkTimeSheet 休日出勤時間帯
	 * @param beforeApplicationTime 事前深夜時間
	 * @param holidayLateNightAutoCalSetting 自動計算設定
	 * @param declareResult 申告時間帯作成結果
	 * @return 休出深夜
	 */
	public static HolidayMidnightWork calcMidNightTimeIncludeHolidayWorkTime(
			HolidayWorkTimeSheet holidayWorkTimeSheet,
			AttendanceTime beforeApplicationTime,
			AutoCalSetting holidayLateNightAutoCalSetting,
			DeclareTimezoneResult declareResult) {
		
		EachStatutoryHolidayWorkTime eachTime = new EachStatutoryHolidayWorkTime();
		for(HolidayWorkFrameTimeSheetForCalc  frameTime : holidayWorkTimeSheet.getWorkHolidayTime()) {
			eachTime.addTime(frameTime.getStatutoryAtr().get(), frameTime.calcMidNightTime(holidayLateNightAutoCalSetting));
		}
		List<HolidayWorkMidNightTime> holidayWorkList = new ArrayList<>();
		holidayWorkList.add(new HolidayWorkMidNightTime(eachTime.getStatutory(), StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork));
		holidayWorkList.add(new HolidayWorkMidNightTime(eachTime.getExcess(), StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork));
		holidayWorkList.add(new HolidayWorkMidNightTime(eachTime.getPublicholiday(), StaturoryAtrOfHolidayWork.PublicHolidayWork));
		
		//事前制御の設定を確認
		if(holidayLateNightAutoCalSetting.getUpLimitORtSet()==TimeLimitUpperLimitSetting.LIMITNUMBERAPPLICATION){
			//事前申請制御
			for(HolidayWorkMidNightTime holidayWorkMidNightTime:holidayWorkList) {
				if(holidayWorkMidNightTime.getTime().getTime().greaterThanOrEqualTo(beforeApplicationTime.valueAsMinutes())) {
					TimeDivergenceWithCalculation time = TimeDivergenceWithCalculation.createTimeWithCalculation(beforeApplicationTime, holidayWorkMidNightTime.getTime().getCalcTime());
					holidayWorkMidNightTime.reCreate(time);
				}
			}
		}
		if (declareResult.getCalcRangeOfOneDay().isPresent()){
			// 申告休出深夜時間の計算
			HolidayWorkTimeOfDaily.calcDeclareHolidayWorkMidnightTime(
					holidayWorkList, holidayLateNightAutoCalSetting, declareResult);
		}
		return new HolidayMidnightWork(holidayWorkList);
	}
	
	/**
	 * 申告休出深夜時間の計算
	 * @param recordList 休出深夜時間
	 * @param holidayLateNightAutoCalSetting 自動計算設定
	 * @param declareResult 申告時間帯作成結果
	 * @return 申告休出深夜時間
	 */
	private static void calcDeclareHolidayWorkMidnightTime(
			List<HolidayWorkMidNightTime> recordList,
			AutoCalSetting holidayLateNightAutoCalSetting,
			DeclareTimezoneResult declareResult) {

		if (!declareResult.getCalcRangeOfOneDay().isPresent()) return;
		CalculationRangeOfOneDay declareCalcRange = declareResult.getCalcRangeOfOneDay().get();
		if (!declareResult.getDeclareCalcRange().isPresent()) return;
		DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
		
		EachStatutoryHolidayWorkTime eachTime = new EachStatutoryHolidayWorkTime();
		// 枠設定を確認する
		if (calcRange.getDeclareSet().getFrameSet() == DeclareFrameSet.WORKTIME_SET){
			// 休日出勤深夜時間の計算（事前申請制御前）
			OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
			if (declareOutsideWork.getHolidayWorkTimeSheet().isPresent()){
				HolidayWorkTimeSheet declareSheet = declareOutsideWork.getHolidayWorkTimeSheet().get();
				for(HolidayWorkFrameTimeSheetForCalc frameTime : declareSheet.getWorkHolidayTime()) {
					AttendanceTime declareTime = frameTime.calcMidNightTime(holidayLateNightAutoCalSetting).getCalcTime();
					if (declareTime.valueAsMinutes() > 0){
						eachTime.addTime(frameTime.getStatutoryAtr().get(), TimeDivergenceWithCalculation.sameTime(declareTime));
						// 編集状態．休出深夜に処理中の法定区分を追加する
						calcRange.getEditState().getHolidayWorkMn().add(frameTime.getStatutoryAtr().get());
					}
				}
			}
		}
		else{
			// 申告休出深夜時間の取得
			{
				// 勤務種類の取得
				if (calcRange.getWorkTypeOpt().isPresent()){
					// 休出かどうかの判断
					if (calcRange.getWorkTypeOpt().get().isHolidayWork()){
						// 申告休出深夜時間　←　事前計算していた深夜時間
						AttendanceTime declareTime = calcRange.getCalcTime().getHolidayWorkMn();
						if (declareTime.valueAsMinutes() > 0){
							// 休出深夜時間を追加する
							OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
							if (declareOutsideWork.getHolidayWorkTimeSheet().isPresent()){
								HolidayWorkTimeSheet declareSheet = declareOutsideWork.getHolidayWorkTimeSheet().get();
								if (declareSheet.getWorkHolidayTime().size() > 0){
									eachTime.addTime(
											declareSheet.getWorkHolidayTime().get(0).getStatutoryAtr().get(), TimeDivergenceWithCalculation.sameTime(declareTime));
									// 編集状態．休出深夜に処理中の法定区分を追加する
									calcRange.getEditState().getHolidayWorkMn().add(
											declareSheet.getWorkHolidayTime().get(0).getStatutoryAtr().get());
								}
							}
						}
					}
				}
			}
		}
		// 申告休出深夜の反映
		{
			for (HolidayWorkMidNightTime record : recordList){
				switch(record.getStatutoryAtr()){
				case WithinPrescribedHolidayWork:
					record.getTime().replaceTimeWithCalc(eachTime.getStatutory().getTime());
					break;
				case ExcessOfStatutoryHolidayWork:
					record.getTime().replaceTimeWithCalc(eachTime.getExcess().getTime());
					break;
				case PublicHolidayWork:
					record.getTime().replaceTimeWithCalc(eachTime.getPublicholiday().getTime());
					break;
				}
			}
		}
	}
	
	/**
	 * 全枠の休出時間の合計の算出
	 * @return　休出時間
	 */
	public AttendanceTime calcTotalFrameTime() {
		int sumHdTime = this.holidayWorkFrameTime.stream().filter(x -> x.getHolidayWorkTime().isPresent())
				.collect(Collectors.summingInt(x -> x.getHolidayWorkTime().get().getTime().v()));
		return new AttendanceTime(sumHdTime);
	}
	
	/**
	 * 全枠の振替休出時間の合計の算出
	 * @return　休出時間
	 */
	public AttendanceTime calcTransTotalFrameTime() {
		int sumHdTranferTime = this.holidayWorkFrameTime.stream().filter(x -> x.getTransferTime().isPresent())
				.collect(Collectors.summingInt(x -> x.getTransferTime().get().getTime().v()));
		return new AttendanceTime(sumHdTranferTime);
	}
	
   //全枠の事前申請時間の合計の算出
	public AttendanceTime calcTotalAppTime() {
		int sumApp = this.holidayWorkFrameTime.stream().filter(x -> x.getBeforeApplicationTime().isPresent())
				.collect(Collectors.summingInt(x -> x.getBeforeApplicationTime().get().v()));
		return new AttendanceTime(sumApp);
	}
	
	/**
	 * 休出時間 実績超過チェック
	 * @return　社員のエラー一覧
	 */
	public List<EmployeeDailyPerError> checkHolidayWorkExcess(String employeeId,
															  GeneralDate targetDate,
															  AttendanceItemDictionaryForCalc attendanceItemDictionary,
															  ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		for(HolidayWorkFrameTime frameTime:this.getHolidayWorkFrameTime()) {
			if(frameTime.isOverLimitDivergenceTime()) {
				//休出時間
				attendanceItemDictionary.findId("休出時間"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
				);
//				//振替時間
//				attendanceItemDictionary.findId("振替時間"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
//						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
//				);
			}
		}
		return returnErrorList;
	}
	
	/**
	 * 休出事前申請のチェック
	 * @return　社員のエラー一覧
	 */
	public List<EmployeeDailyPerError> checkPreHolidayWorkExcess(String employeeId,
			  												  GeneralDate targetDate,
															  AttendanceItemDictionaryForCalc attendanceItemDictionary,
			  												  ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		for(HolidayWorkFrameTime frameTime:this.getHolidayWorkFrameTime()) {
			if(frameTime.isPreOverLimitDivergenceTime()) {
				//休出時間
				attendanceItemDictionary.findId("休出時間"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
				);
//				//振替時間
//				attendanceItemDictionary.findId("振替時間"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
//						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
//				);
			}
		}
		return returnErrorList;
	}
	
	/**
	 *　実績の休出深夜超過
	 * @return
	 */
	public List<EmployeeDailyPerError> checkNightTimeExcess(String employeeId,
														   GeneralDate targetDate,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if(this.getHolidayMidNightWork().isPresent()) {
			returnErrorList.addAll(this.getHolidayMidNightWork().get().getErrorList(employeeId, targetDate, attendanceItemDictionary, errorCode));
		}
		return returnErrorList;
	}
	
	/**
	 * 自身の休出枠時間の乖離時間を計算
	 * @return
	 */
	public HolidayWorkTimeOfDaily calcDiverGenceTime() {
		List<HolidayWorkFrameTime> list = new ArrayList<>();
		for(HolidayWorkFrameTime holidayworkFrameTime:this.holidayWorkFrameTime) {
			holidayworkFrameTime.calcDiverGenceTime();
			list.add(holidayworkFrameTime);
		}
		Finally<HolidayMidnightWork> holidayMidnight = this.holidayMidNightWork.isPresent()?Finally.of(this.holidayMidNightWork.get().calcDiverGenceTime()):this.holidayMidNightWork;
		return new HolidayWorkTimeOfDaily(this.holidayWorkFrameTimeSheet,list,holidayMidnight,this.holidayTimeSpentAtWork);
	}

	/**
	 * マイナスの乖離時間を0にする
	 * @param holidayWorkFrameTimeList 休出枠時間リスト
	 */
	public static void divergenceMinusValueToZero(
			List<HolidayWorkFrameTime> holidayWorkFrameTimeList){
		
		//大塚モードの確認
		if (AppContexts.optionLicense().customize().ootsuka() == false) return;
		
		//マイナスの乖離時間を0にする
		for (val holidayWorkFrameTime : holidayWorkFrameTimeList){
			holidayWorkFrameTime.getHolidayWorkTime().get().divergenceMinusValueToZero();
			holidayWorkFrameTime.getTransferTime().get().divergenceMinusValueToZero();
		}
	}
	
	//PCログインログオフから計算した計算時間を入れる(大塚モードのみ)
	public void setPCLogOnValue(Map<HolidayWorkFrameNo, HolidayWorkFrameTime> map) {
		Map<HolidayWorkFrameNo,HolidayWorkFrameTime> changeList = convertHolMap(this.getHolidayWorkFrameTime());
		
		for(int frameNo = 1 ; frameNo<=10 ; frameNo++) {
			//値更新
			if(changeList.containsKey(new HolidayWorkFrameNo(frameNo))) {
				val getframe = changeList.get(new HolidayWorkFrameNo(frameNo)); 
				if(map.containsKey(new HolidayWorkFrameNo(frameNo))) {
					//残業時間の置き換え
					getframe.getHolidayWorkTime().get().replaceTimeAndCalcDiv(map.get(new HolidayWorkFrameNo(frameNo)).getHolidayWorkTime().get().getCalcTime());
					//振替時間の置き換え
					getframe.getTransferTime().get().replaceTimeAndCalcDiv(map.get(new HolidayWorkFrameNo(frameNo)).getTransferTime().get().getCalcTime());
				}
				else {
					//残業時間の置き換え
					getframe.getHolidayWorkTime().get().replaceTimeAndCalcDiv(new AttendanceTime(0));
					//振替時間の置き換え
					getframe.getTransferTime().get().replaceTimeAndCalcDiv(new AttendanceTime(0));
				}
				changeList.remove(new HolidayWorkFrameNo(frameNo));
				changeList.put(new HolidayWorkFrameNo(frameNo), getframe);
			}
			//リストへ追加
			else {
				if(map.containsKey(new HolidayWorkFrameNo(frameNo))) {
					changeList.put(new HolidayWorkFrameNo(frameNo),
							   	   new HolidayWorkFrameTime(new HolidayWorkFrameNo(frameNo),
							   			   				 Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),map.get(new HolidayWorkFrameNo(frameNo)).getHolidayWorkTime().get().getCalcTime())),
							   			   				 Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),map.get(new HolidayWorkFrameNo(frameNo)).getTransferTime().get().getCalcTime())),
							   			   				 Finally.of(new AttendanceTime(0))));
				}
			}
		}
		
		this.holidayWorkFrameTime = new ArrayList<>(changeList.values());
	}
	
	private Map<HolidayWorkFrameNo,HolidayWorkFrameTime> convertHolMap(List<HolidayWorkFrameTime> holidayWorkFrameTime) {
		Map<HolidayWorkFrameNo,HolidayWorkFrameTime> map= new HashMap<>();
		for(HolidayWorkFrameTime hol : holidayWorkFrameTime) {
			map.put(hol.getHolidayFrameNo(), hol);
		}
		return map;
	}
	
	//事前申請時間の前にデフォルトを作成
	public static HolidayWorkTimeOfDaily createDefaultBeforeApp(List<Integer> lstNo) {
		List<HolidayWorkFrameTime> workFrameTime = lstNo.stream().map(x -> {
			return new HolidayWorkFrameTime(new HolidayWorkFrameNo(x), Finally.of(TimeDivergenceWithCalculation.emptyTime()), Finally.of(TimeDivergenceWithCalculation.emptyTime()),
					Finally.of(new AttendanceTime(0)));
		}).collect(Collectors.toList());
		return new HolidayWorkTimeOfDaily(new ArrayList<>(), 
				workFrameTime, 
				Finally.of(new HolidayMidnightWork(new ArrayList<>())), 
				new AttendanceTime(0));
	}

	@Override
	public HolidayWorkTimeOfDaily clone() {

		// 休出枠時間帯
		List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheetClone = this.holidayWorkFrameTimeSheet.stream().map(x -> {
			return x.clone();
		}).collect(Collectors.toList());

		// 休出枠時間
		List<HolidayWorkFrameTime> holidayWorkFrameTimeClone = this.holidayWorkFrameTime.stream().map(x -> {
			return x.clone();
		}).collect(Collectors.toList());

		// 休出深夜
		Finally<HolidayMidnightWork> holidayMidNightWorkClone = holidayMidNightWork.isPresent()
				? Finally.of(holidayMidNightWork.get().clone())
				: Finally.empty();

		// 休出拘束時間
		AttendanceTime holidayTimeSpentAtWorkClone = new AttendanceTime(this.holidayTimeSpentAtWork.v());

		return new HolidayWorkTimeOfDaily(holidayWorkFrameTimeSheetClone, holidayWorkFrameTimeClone,
				holidayMidNightWorkClone, holidayTimeSpentAtWorkClone);
	}
	
	// 事前申請時間から代休振替を行うか判断する
	public boolean tranferHdWorkCompenCall(SubsTransferProcessMode processMode) {
		AttendanceTime sumHdTime = calcTotalFrameTime();
		AttendanceTime sumHdTranferTime = calcTransTotalFrameTime();
		AttendanceTime sumApp = calcTotalAppTime();
		if ((sumHdTime.valueAsMinutes() + sumHdTranferTime.valueAsMinutes()) <= 0
				&& processMode == SubsTransferProcessMode.DAILY && sumApp.valueAsMinutes() > 0) {
			return true;
		}
		return false;
	}
}
