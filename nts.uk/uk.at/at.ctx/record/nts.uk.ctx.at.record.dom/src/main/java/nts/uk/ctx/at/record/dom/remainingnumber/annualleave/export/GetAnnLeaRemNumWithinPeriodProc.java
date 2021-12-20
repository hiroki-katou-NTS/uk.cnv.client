package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnualLeave;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggregatePeriodWork;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AnnualLeaveInfo;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AnnualLeaveLapsedWork;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.DividedDayEachProcess;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.common.days.YearlyDays;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnualLeaveEmpBasicInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.CalcNextAnnualLeaveGrantDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainingHistory;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxHistoryData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.CalcAnnLeaAttendanceRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.CreateInterimAnnualMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.CalYearOffWorkAttendRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.ConfirmLeavePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AttendanceRate;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.OperationStartSetDailyPerform;
import nts.uk.ctx.at.shared.dom.workingcondition.LaborContractTime;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureIdHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantHdTblSet;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LengthServiceTbl;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.export.NextAnnualLeaveGrant;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 処理：期間中の年休残数を取得
 * @author shuichi_ishida
 */
public class GetAnnLeaRemNumWithinPeriodProc {

//	/**
//	 * 期間中の年休残数を取得
//	 * @param require
//	 * @param cacheCarrier
//	 * @param companyId
//	 * @param employeeId 社員ID
//	 * @param aggrPeriod 集計期間
//	 * @param mode 実績のみ参照区分
//	 * @param criteriaDate 基準日
//	 * @param isGetNextMonthData 翌月管理データ取得フラグ
//	 * @param isCalcAttendanceRate 出勤率計算フラグ
//	 * @param isOverWriteOpt 上書きフラグ
//	 * @param forOverWriteListOpt 上書き用の暫定年休管理データ
//	 * @param prevAnnualLeaveOpt 前回の年休の集計結果
//	 * @param noCheckStartDate 集計開始日を締め開始日とする　（締め開始日を確認しない）
//	 * @return 年休の集計結果
//	 */
//	public static Optional<AggrResultOfAnnualLeave> algorithm(RequireM3 require, CacheCarrier cacheCarrier, String companyId,
//			String employeeId, DatePeriod aggrPeriod, InterimRemainMngMode mode, GeneralDate criteriaDate,
//			boolean isGetNextMonthData, boolean isCalcAttendanceRate, Optional<Boolean> isOverWriteOpt,
//			Optional<List<TmpAnnualLeaveMngWork>> forOverWriteListOpt, Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt,
//			Optional<Boolean> noCheckStartDate) {
//
//		return algorithm(require, cacheCarrier, companyId, employeeId, aggrPeriod, mode, criteriaDate,
//				isGetNextMonthData, isCalcAttendanceRate, isOverWriteOpt, forOverWriteListOpt,
//				prevAnnualLeaveOpt,
//				(noCheckStartDate.isPresent() ? noCheckStartDate.get() : false),
//				Optional.empty(), Optional.empty(), Optional.empty(),
//				Optional.empty(), Optional.empty(), Optional.empty());
//	}
//
//	/**
//	 * 期間中の年休残数を取得
//	 * @param companyId 会社ID
//	 * @param employeeId 社員ID
//	 * @param aggrPeriod 集計期間
//	 * @param mode モード
//	 * @param criteriaDate 基準日
//	 * @param isGetNextMonthData 翌月管理データ取得フラグ
//	 * @param isCalcAttendanceRate 出勤率計算フラグ
//	 * @param isOverWriteOpt 上書きフラグ
//	 * @param forOverWriteListOpt 上書き用の暫定年休管理データ
//	 * @param prevAnnualLeaveOpt 前回の年休の集計結果
//	 * @param noCheckStartDate 集計開始日を締め開始日とする　（締め開始日を確認しない）
//	 * @param aggrPastMonthMode 過去月集計モード
//	 * @param yearMonth 年月
//	 * @param companySets 月別集計で必要な会社別設定
//	 * @param employeeSets 月別集計で必要な社員別設定
//	 * @param monthlyCalcDailys 月の計算中の日別実績データ
//	 * @return 年休の集計結果
//	 */
//	public static Optional<AggrResultOfAnnualLeave> algorithm(RequireM3 require, CacheCarrier cacheCarrier,
//			String companyId, String employeeId, DatePeriod aggrPeriod, InterimRemainMngMode mode,
//			GeneralDate criteriaDate, boolean isGetNextMonthData, boolean isCalcAttendanceRate,
//			Optional<Boolean> isOverWriteOpt, Optional<List<TmpAnnualLeaveMngWork>> forOverWriteListOpt,
//			Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt, boolean noCheckStartDate,
//			Optional<MonAggrCompanySettings> companySets, Optional<MonAggrEmployeeSettings> employeeSets,
//			Optional<MonthlyCalculatingDailys> monthlyCalcDailys) {
//
//		return algorithm(require, cacheCarrier, companyId, employeeId, aggrPeriod, mode, criteriaDate,
//				isGetNextMonthData, isCalcAttendanceRate, isOverWriteOpt, forOverWriteListOpt,
//				prevAnnualLeaveOpt,
//				noCheckStartDate,
//				Optional.empty(), Optional.empty(), Optional.empty(),
//				companySets, employeeSets, monthlyCalcDailys);
//	}
//
//	/**
//	 * 期間中の年休残数を取得
//	 * @param companyId 会社ID
//	 * @param employeeId 社員ID
//	 * @param aggrPeriod 集計期間
//	 * @param mode モード
//	 * @param criteriaDate 基準日
//	 * @param isGetNextMonthData 翌月管理データ取得フラグ
//	 * @param isCalcAttendanceRate 出勤率計算フラグ
//	 * @param isOverWriteOpt 上書きフラグ
//	 * @param forOverWriteListOpt 上書き用の暫定年休管理データ
//	 * @param prevAnnualLeaveOpt 前回の年休の集計結果
//	 * @param noCheckStartDate 集計開始日を締め開始日とする　（締め開始日を確認しない）
//	 * @param isOutShortRemainOpt 不足分付与残数データ出力区分
//	 * @param aggrPastMonthModeOpt 過去月集計モード
//	 * @param yearMonthOpt 年月
//	 * @return 年休の集計結果
//	 */
//	public static Optional<AggrResultOfAnnualLeave> algorithm(RequireM3 require, CacheCarrier cacheCarrier, String companyId,
//			String employeeId, DatePeriod aggrPeriod, InterimRemainMngMode mode, GeneralDate criteriaDate, boolean isGetNextMonthData,
//			boolean isCalcAttendanceRate, Optional<Boolean> isOverWriteOpt, Optional<List<TmpAnnualLeaveMngWork>> forOverWriteListOpt,
//			Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt, Optional<Boolean> noCheckStartDate,
//			Optional<Boolean> isOutShortRemainOpt, Optional<Boolean> aggrPastMonthModeOpt, Optional<YearMonth> yearMonthOpt) {
//
//		return algorithm(require, cacheCarrier, companyId, employeeId, aggrPeriod, mode, criteriaDate,
//				isGetNextMonthData, isCalcAttendanceRate, isOverWriteOpt, forOverWriteListOpt,
//				prevAnnualLeaveOpt,
//				(noCheckStartDate.isPresent() ? noCheckStartDate.get() : false),
//				isOutShortRemainOpt, aggrPastMonthModeOpt, yearMonthOpt,
//				Optional.empty(), Optional.empty(), Optional.empty());
//	}

	/**
	 * 期間中の年休残数を取得
	 * @param require Require
	 * @param cacheCarrier CacheCarrier
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param aggrPeriod 集計期間
	 * @param mode 実績のみ参照区分
	 * @param criteriaDate 基準日
	 * @param isCalcAttendanceRate 出勤率計算フラグ
	 * @param isOverWriteOpt 上書きフラグ
	 * @param forOverWriteListOpt 上書き用の暫定年休管理データ
	 * @param prevAnnualLeaveOpt 前回の年休の集計結果
	 * @param aggrPastMonthModeOpt 過去月集計モード
	 * @param yearMonthOpt 年月※過去月集計モード  = true の場合は必須
 	 * @param isOverWritePeriod 上書き対象期間
	 * @return 年休の集計結果
	 */
	public static Optional<AggrResultOfAnnualLeave> algorithm(
			RequireM3 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			GeneralDate criteriaDate,
			boolean isCalcAttendanceRate,
			Optional<Boolean> isOverWriteOpt,
			Optional<List<TempAnnualLeaveMngs>> forOverWriteListOpt,
			Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt,
			Optional<Boolean> aggrPastMonthModeOpt,
			Optional<YearMonth> yearMonthOpt,
			Optional<DatePeriod> isOverWritePeriod
			) {

		// 年休の使用区分を取得する
		boolean isManageAnnualLeave = false;

		// ドメインモデル「年休設定」を取得する
		AnnualPaidLeaveSetting annualLeaveSet = require.annualPaidLeaveSetting(companyId);

		if (annualLeaveSet != null) isManageAnnualLeave = annualLeaveSet.isManaged();
		if (!isManageAnnualLeave) return Optional.empty();

		AggrResultOfAnnualLeave aggrResult = new AggrResultOfAnnualLeave();

		// 社員、年休社員基本情報　取得
		Optional<AnnualLeaveEmpBasicInfo> annualLeaveEmpBasicInfoOpt = Optional.empty();

		EmployeeImport employee = require.employee(cacheCarrier,employeeId);
		annualLeaveEmpBasicInfoOpt = require.employeeAnnualLeaveBasicInfo(employeeId);

		if (employee == null) return Optional.empty();
		if (!annualLeaveEmpBasicInfoOpt.isPresent()) return Optional.empty();
		val empBasicInfo = annualLeaveEmpBasicInfoOpt.get();
		val grantTableCode = empBasicInfo.getGrantRule().getGrantTableCode().v();

		// 「休暇の集計期間から入社前、退職後を除く」を実行する
		Optional<DatePeriod> period = ConfirmLeavePeriod.sumPeriod(aggrPeriod, employee);
		if (!period.isPresent()) return Optional.empty();

		// 年休付与テーブル設定、勤続年数テーブル　取得
		Optional<GrantHdTblSet> grantHdTblSetOpt = Optional.empty();
		Optional<List<LengthServiceTbl>> lengthServiceTblsOpt = Optional.empty();

		grantHdTblSetOpt = require.grantHdTblSet(companyId, grantTableCode);
		lengthServiceTblsOpt = Optional.ofNullable(require.lengthServiceTbl(companyId, grantTableCode));



		// 日別実績の運用開始設定　取得
		Optional<OperationStartSetDailyPerform> operationStartSetOpt = Optional.empty();
		operationStartSetOpt = require.dailyOperationStartSet(new CompanyId(companyId));

		// 集計開始日時点の年休情報を作成
		AnnualLeaveInfo annualLeaveInfo
			= createInfoAsOfPeriodStart(require, cacheCarrier, companyId, employeeId,
				prevAnnualLeaveOpt, aggrPastMonthModeOpt, yearMonthOpt, aggrPeriod,
				mode, isCalcAttendanceRate, isOverWriteOpt, forOverWriteListOpt,isOverWritePeriod);

		// 次回年休付与日を計算
		List<NextAnnualLeaveGrant> nextAnnualLeaveGrantList = new ArrayList<>();
		nextAnnualLeaveGrantList = CalcNextAnnualLeaveGrantDate.algorithm(
				require, cacheCarrier,
				companyId, employeeId, Optional.of(period.get()),
				Optional.ofNullable(employee), annualLeaveEmpBasicInfoOpt,
				grantHdTblSetOpt, lengthServiceTblsOpt);
		
		// １日に相当する契約時間を取得する
		Optional<LaborContractTime> laborContractTimeOpt
			= LeaveRemainingNumber.getContractTime(require, companyId, employeeId, criteriaDate);

		// 勤務実績によって次回年休付与を更新
		for (val nextAnnualGrantList : nextAnnualLeaveGrantList){
			if (!grantHdTblSetOpt.isPresent()) continue;
			if (!lengthServiceTblsOpt.isPresent()) continue;

			// 次回年休付与の付与日数を条件によって更新する
			Optional<CalYearOffWorkAttendRate> resultRateOpt = Optional.of(new CalYearOffWorkAttendRate());
			if(mode == InterimRemainMngMode.MONTHLY){
			// 年休出勤率を計算する
				resultRateOpt = CalcAnnLeaAttendanceRate.algorithm(require, cacheCarrier,
					companyId, employeeId,
					nextAnnualGrantList.getGrantDate(),
					Optional.of(nextAnnualGrantList.getTimes().v()),
					Optional.of(annualLeaveSet), Optional.of(employee), annualLeaveEmpBasicInfoOpt,
					grantHdTblSetOpt, lengthServiceTblsOpt, operationStartSetOpt);
			}else{
				resultRateOpt = Optional.of(new CalYearOffWorkAttendRate(100.0,0.0,365.0,0.0));
			}

			if (!resultRateOpt.isPresent())
				continue;
			
			val resultRate = resultRateOpt.get();
			nextAnnualGrantList.setAttendanceRate(Optional.of(
					new AttendanceRate(resultRate.getAttendanceRate())));
			nextAnnualGrantList.setPrescribedDays(Optional.of(
					new YearlyDays(resultRate.getPrescribedDays())));
			nextAnnualGrantList.setWorkingDays(Optional.of(
					new YearlyDays(resultRate.getWorkingDays())));
			nextAnnualGrantList.setDeductedDays(Optional.of(
					new YearlyDays(resultRate.getDeductedDays())));

			// 日数と出勤率から年休付与テーブルを取得する
			val grantConditionOpt = grantHdTblSetOpt.get().getGrantCondition(
					resultRate.getAttendanceRate(),
					resultRate.getPrescribedDays(),
					resultRate.getWorkingDays(),
					resultRate.getDeductedDays());

			if (!grantConditionOpt.isPresent()){
				// 出勤率に応じたテーブルがない場合（出勤率が足りなくて年休が付与されないケース）
				nextAnnualGrantList.setGrantDays(Finally.of(new LeaveGrantDayNumber(0.0)));
				//出勤率が足りない場合は、条件NO=1のテーブルの時間年休上限、半日年休上限の情報を使用する。
				//その為、CalcNextAnnualLeaveGrantDateにて計算した値をそのまま使用する。（ここでは何も更新しない）
				continue;
			}
			
			// 付与日数を計算
			val grantHdTblOpt = require.grantHdTbl(companyId, grantConditionOpt.get().getConditionNo(),
												grantTableCode, nextAnnualGrantList.getTimes().v());

			if (grantHdTblOpt.isPresent()){
				val grantHdTbl = grantHdTblOpt.get();
				nextAnnualGrantList
						.setGrantDays(Finally.of(grantHdTbl.getGrantDays().toLeaveGrantDayNumber()));
				nextAnnualGrantList.setHalfDayAnnualLeaveMaxTimes(
						annualLeaveSet.getLimitedHalfCount(grantHdTbl.getLimitDayYear()));
				nextAnnualGrantList.setTimeAnnualLeaveMaxDays(
						annualLeaveSet.getLimitedTimeHdDays(grantHdTbl.getLimitTimeHd()));
				nextAnnualGrantList.setTimeAnnualLeaveMaxTime(annualLeaveSet
						.getLimitedTimeHdTime(grantHdTbl.getLimitTimeHd(), laborContractTimeOpt));
				
			}
		}


		// 年休集計期間を作成
		List<AggregatePeriodWork> aggregateWork = createAggregatePeriod(
				nextAnnualLeaveGrantList, period.get(), annualLeaveInfo.getGrantRemainingDataList());

		// 暫定年休管理データを取得する
		val tempAnnualLeaveMngs = getTempAnnualLeaveMngs(
				require, employeeId, aggrPeriod, mode,
				isOverWriteOpt, forOverWriteListOpt,isOverWritePeriod);

		for (val aggregatePeriodWork : aggregateWork){

			// 年休の消滅・付与・消化
			aggrResult = annualLeaveInfo.lapsedGrantDigest(
					require, companyId, employeeId, aggregatePeriodWork,
					tempAnnualLeaveMngs, aggrResult, annualLeaveSet);
		}

		// 【渡すパラメータ】 年休情報　←　年休の集計結果．年休情報（期間終了日時点）
		AnnualLeaveInfo annualLeaveInfoEnd = aggrResult.getAsOfPeriodEnd();

		// マイナス分の特別休暇付与残数を1レコードにまとめる
		Optional<AnnualLeaveGrantRemainingData> remainingShortageData
			= annualLeaveInfoEnd.createLeaveGrantRemainingShortageData();

		// 年休不足分として作成した年休付与データを削除する
		aggrResult.deleteShortageRemainData();

		// 年休(期間終了日時点)に残数不足の付与残数データを追加
		if ( remainingShortageData.isPresent() ) {
			annualLeaveInfoEnd.getGrantRemainingDataList().add(remainingShortageData.get());
		}

		// 「年休の集計結果」を返す
		return Optional.of(aggrResult);
	}

	/**
	 * 集計開始日時点の年休情報を作成
	 * @param require
	 * @param cacheCarrier
	 * @param companyId　会社ID
	 * @param employeeId 社員ID
//	 * @param noCheckStartDate
	 * @param prevAnnualLeaveOpt　前回の年休の集計結果
	 * @param aggrPastMonthModeOpt 過去月集計モード
	 * @param yearMonthOpt 年月
	 * @param aggrPeriod 集計期間
//	 * @param grantRemainingDatas
	 * @param mode 実績のみ参照区分
//	 * @param isGetNextMonthData
	 * @param isCalcAttendanceRate 出勤率計算フラグ
	 * @param isOverWriteOpt 上書きフラグ
	 * @param forOverWriteListOpt　上書き用の暫定管理データ
	 * @param isOverWritePeriod 上書き対象期間
	 * @return
	 */
	private static AnnualLeaveInfo createInfoAsOfPeriodStart(
			RequireM3 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt,
			Optional<Boolean> aggrPastMonthModeOpt,
			Optional<YearMonth> yearMonthOpt,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			boolean isCalcAttendanceRate,
			Optional<Boolean> isOverWriteOpt,
			Optional<List<TempAnnualLeaveMngs>> forOverWriteListOpt,
			Optional<DatePeriod> isOverWritePeriod){

		AnnualLeaveInfo emptyInfo = new AnnualLeaveInfo();
		emptyInfo.setYmd(aggrPeriod.start());

		// 集計開始日時点の前回の年休の集計結果が存在するかチェック
		// パラメータ「集計開始日」とパラメータ「前回の年休の集計結果．年休情報(期間終了日の翌日開始時点)．年月日」を比較
		AnnualLeaveInfo prevAnnualLeaveInfo
			= prevAnnualLeaveOpt.map(c -> c.getAsOfStartNextDayOfPeriodEnd()).orElse(null);

		// 「開始日」と「年休情報．年月日」を比較
		boolean isSameInfo = false;
		if (prevAnnualLeaveInfo != null){
			if (aggrPeriod.start().equals(prevAnnualLeaveInfo.getYmd())){
				isSameInfo = true;
			}
		}

		if (isSameInfo){ // 集計開始日時点の前回の年休の集計結果が存在するとき

			// パラメータ「前回の年休の集計結果．年休情報(期間終了日の翌日開始時点)」の年休付与残数データ・年休上限データを取得
			// 年休付与残数データ、年休上限データをもとに年休情報を作成
			return createInfoFromRemainingData(
					prevAnnualLeaveInfo.getGrantRemainingDataList(),
					Optional.of(prevAnnualLeaveInfo.getMaxData()), aggrPeriod);

		} else {

			// 過去月集計モードの判断
			boolean aggrPastMonthMode = false;
			if (aggrPastMonthModeOpt.isPresent() && yearMonthOpt.isPresent()) {
				aggrPastMonthMode = aggrPastMonthModeOpt.get() ;
			}

			// 社員の年休情報を取得
			AnnualLeaveInfo annualLeaveInfo = getAnnualLeaveInfo(
					 require,
					 cacheCarrier,
					 companyId,
					 employeeId,
					 prevAnnualLeaveOpt,
					 aggrPastMonthMode,
					 yearMonthOpt,
					 aggrPeriod,
					 mode,
					 isCalcAttendanceRate,
					 isOverWriteOpt,
					 forOverWriteListOpt,
					 isOverWritePeriod);

			// 年休付与残数データ、年休上限データをもとに年休情報を作成
			return createInfoFromRemainingData(
					annualLeaveInfo.getGrantRemainingDataList(),
					Optional.ofNullable(annualLeaveInfo.getMaxData()),
					aggrPeriod);

		}
	}

	/**
	 * 休暇残数を計算する締め開始日を取得する
	 * @param employeeId 社員ID
	 * @return 締め開始日
	 */
	static public Optional<GeneralDate> getClosureStartDate(
			RequireM3 require, CacheCarrier cacheCarrier, String employeeId) {

		GeneralDate closureStart = null;	// 締め開始日
		Optional<GeneralDate> closureStartOpt = Optional.empty();

		// 最新の締め終了日翌日を取得する
		Optional<ClosureStatusManagement> sttMng = require.latestClosureStatusManagement(employeeId);
		if (sttMng.isPresent()){
			// 受け取った「年月日」を返す
			closureStart = sttMng.get().getPeriod().end();
//			if (closureStart.before(GeneralDate.max())){
//				closureStart = closureStart.addDays(1);
//			}
			closureStartOpt = Optional.of(closureStart);
		}
		else {
			// 社員に対応する締め開始日を取得する
			// 受け取った「締め開始日」を返す
			closureStartOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier, employeeId);
		}

		return closureStartOpt;
	}

	/**
	 * 社員の年休情報を取得
	 * @param require
	 * @param cacheCarrier
	 * @param companyId　会社ID
	 * @param employeeId 社員ID
	 * @param prevAnnualLeaveOpt　前回の年休の集計結果
	 * @param aggrPastMonthMode 過去月集計モード
	 * @param yearMonthOpt 年月
	 * @param aggrPeriod 集計期間
	 * @param mode 実績のみ参照区分
	 * @param isCalcAttendanceRate 出勤率計算フラグ
	 * @param isOverWriteOpt 上書きフラグ
	 * @param forOverWriteListOpt　上書き用の暫定管理データ
	 * @param isOverWritePeriod 上書き対象期間
	 * @return　年休情報
	 */
	private static AnnualLeaveInfo getAnnualLeaveInfo(
			RequireM3 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt,
			Boolean aggrPastMonthMode,
			Optional<YearMonth> yearMonthOpt,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			boolean isCalcAttendanceRate,
			Optional<Boolean> isOverWriteOpt,
			Optional<List<TempAnnualLeaveMngs>> forOverWriteListOpt,
			Optional<DatePeriod> isOverWritePeriod
			){

			// 休暇残数を計算する締め開始日を取得する
			GeneralDate closureStart = aggrPeriod.start();
			Optional<GeneralDate> closureStartDateOpt
				= getClosureStartDate(require, cacheCarrier, employeeId);
			if ( closureStartDateOpt.isPresent() ) {
				closureStart = closureStartDateOpt.get();
			}

			// 年休付与残数データリスト
			List<AnnualLeaveGrantRemainingData> grantRemainingDatas
				= new ArrayList<AnnualLeaveGrantRemainingData>();

			// 年休上限データを取得
			Optional<AnnualLeaveMaxData>  annLeaMaxDataOpt = Optional.empty();

			// 取得した締め開始日とパラメータ「集計開始日」を比較

			 // 締め開始日=パラメータ「集計開始日」

			if ( closureStart.equals(aggrPeriod.start())
					 // 締め開始日>パラメータ「集計開始日」 && パラメータ．過去月集計モードがfalse
					|| ( closureStart.after(aggrPeriod.start()) && !aggrPastMonthMode )
				){

				// ドメインモデル「年休付与残数データ」を取得
	//			【条件】
	//			社員ID=パラメータ「社員ID」
	//					付与日<=締め開始日
	//					期限日>=締め開始日
	//					期限切れ状態=使用可能
				GeneralDate closureStartTmp = closureStart; // コンパイルエラー回避
				grantRemainingDatas = require.annualLeaveGrantRemainingData(employeeId).stream()
						.filter(c->c.getGrantDate().beforeOrEquals(closureStartTmp)
								&& c.getDeadline().afterOrEquals(closureStartTmp)
								&& c.getExpirationStatus().IsAVAILABLE())
						.collect(Collectors.toList());

				// 年休上限データを取得
				annLeaMaxDataOpt = require.annualLeaveMaxData(employeeId);

			}
			// 締め開始日>パラメータ「集計開始日」 && パラメータ．過去月集計モードがtrue
			else if (closureStart.after(aggrPeriod.start())
					&& aggrPastMonthMode ){

					// ドメインモデル「年休付与残数履歴データ」を取得
					List<AnnualLeaveRemainingHistory> remainHistList
						= require.annualLeaveRemainingHistory(
							employeeId, yearMonthOpt.get());
					if (remainHistList.size() > 0) {
						// 付与日 ASC、期限切れ状態＝「使用可能」　を採用
						remainHistList.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
						for (AnnualLeaveRemainingHistory remainHist : remainHistList) {
							if (remainHist.getExpirationStatus() != LeaveExpirationStatus.AVAILABLE) continue;

							// 取得したドメインを年休付与残数データに変換
							AnnualLeaveGrantRemainingData remainData
								= AnnualLeaveGrantRemainingData.createFromHistory(remainHist);
							grantRemainingDatas.add(remainData);
						}
					}

					// 社員の締めを取得
					Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, employeeId, aggrPeriod.end());

					// 年月指定して年休上限データを取得
					Optional<AnnualLeaveMaxHistoryData> annualLeaveMaxHistoryData
						= require.AnnualLeaveMaxHistoryData(
								employeeId, yearMonthOpt.get(), closure.getClosureId(), closure.getClosureDateOfCurrentMonth().get());

					// 「AnnualLeaveMaxData」クラスへキャスト
					if ( annualLeaveMaxHistoryData.isPresent() ) {
						annLeaMaxDataOpt = Optional.of((AnnualLeaveMaxData)annualLeaveMaxHistoryData.get());
					}

			} else if (closureStart.before(aggrPeriod.start())){ // 締め開始日<パラメータ「集計開始日」

					// 開始日までの年休残数を計算　（締め開始日～集計開始日前日）
					val aggrResultOpt = algorithm(
							require, cacheCarrier, companyId, employeeId,
							new DatePeriod(closureStart, aggrPeriod.start().addDays(-1)),
							mode, aggrPeriod.start().addDays(-1),
							isCalcAttendanceRate, isOverWriteOpt, forOverWriteListOpt,
							Optional.empty(), Optional.of(aggrPastMonthMode), yearMonthOpt,isOverWritePeriod);

					if (aggrResultOpt.isPresent()) {
						val aggrResult = aggrResultOpt.get();

						// 年休情報（期間終了日の翌日開始時点）を取得
						val asOfPeriodEnd = aggrResult.getAsOfStartNextDayOfPeriodEnd();

						// 集計結果から、年休付与残数データを作成する
						// 集計結果から、上限データを作成する
						// 年休付与残数データ、年休上限データを返す

						return createInfoFromRemainingData(
								asOfPeriodEnd.getGrantRemainingDataList(),
								Optional.of(asOfPeriodEnd.getMaxData()), aggrPeriod);
					}
			}

			AnnualLeaveInfo annualLeaveInfo = new AnnualLeaveInfo();
			annualLeaveInfo.setGrantRemainingDataList(grantRemainingDatas);
			if ( annLeaMaxDataOpt.isPresent() ) {
				annualLeaveInfo.setMaxData(annLeaMaxDataOpt.get());
			}
			return annualLeaveInfo;
	}

	/**
	 * 年休付与残数データから年休情報を作成
	 * @param grantRemainingDataList 付与残数データリスト
	 * @param maxDataOpt 上限データ
	 * @return 年休情報
	 */
	private static AnnualLeaveInfo createInfoFromRemainingData(List<AnnualLeaveGrantRemainingData> grantRemainingDataList,
			Optional<AnnualLeaveMaxData> maxDataOpt, DatePeriod aggrPeriod){

		AnnualLeaveInfo returnInfo = new AnnualLeaveInfo();
		returnInfo.setYmd(aggrPeriod.start());

		String employeeId = "";

		// 年休情報．年休付与情報　←　パラメータ「付与残数データ」
		List<AnnualLeaveGrantRemainingData> targetDatas = new ArrayList<>();
		for (val grantRemainingData : grantRemainingDataList){
			if (grantRemainingData.getExpirationStatus() == LeaveExpirationStatus.EXPIRED) continue;
			targetDatas.add(grantRemainingData);
			employeeId = grantRemainingData.getEmployeeId();
		}
		targetDatas.sort((a, b) -> a.getGrantDate().compareTo(b.getGrantDate()));
		returnInfo.setGrantRemainingDataList(targetDatas);

		// 年休情報．上限データ　←　パラメータ「上限データ」
		if (!maxDataOpt.isPresent()) {
			Integer intNull = null;
			AnnualLeaveMaxData emptyMaxData = AnnualLeaveMaxData.createFromJavaType(
					employeeId, intNull, intNull, intNull, intNull);
			returnInfo.setMaxData(emptyMaxData);
		}
		else {
			returnInfo.setMaxData(maxDataOpt.get());
		}

		// 年休情報残数を更新
		returnInfo.updateRemainingNumber(GrantBeforeAfterAtr.BEFORE_GRANT, aggrPeriod.end());

		// 年休情報を返す
		return returnInfo;
	}

	/**
	 * 年休集計期間を作成
	 * @param nextAnnualLeaveGrantList 次回年休付与リスト
	 * @return 年休集計期間WORKリスト
	 */
	private static List<AggregatePeriodWork> createAggregatePeriod(List<NextAnnualLeaveGrant> nextAnnualLeaveGrantList,
			DatePeriod aggrPeriod, List<AnnualLeaveGrantRemainingData> grantRemainingDatas){

		List<AggregatePeriodWork> aggregatePeriodWorks = new ArrayList<>();

		// 処理単位分割日リスト
		Map<GeneralDate, DividedDayEachProcess> dividedDayMap = new HashMap<>();

		// 期間終了日翌日
		GeneralDate nextDayOfPeriodEnd = aggrPeriod.end();
		if (nextDayOfPeriodEnd.before(GeneralDate.max())) nextDayOfPeriodEnd = nextDayOfPeriodEnd.addDays(1);

		// 「年休付与残数データ」を取得　（期限日　昇順、付与日　昇順）
		List<AnnualLeaveGrantRemainingData> remainingDatas = new ArrayList<>();
		remainingDatas.addAll(grantRemainingDatas);
		Collections.sort(remainingDatas, new Comparator<AnnualLeaveGrantRemainingData>() {
			@Override
			public int compare(AnnualLeaveGrantRemainingData o1, AnnualLeaveGrantRemainingData o2) {
				int compDeadline = o1.getDeadline().compareTo(o2.getDeadline());
				if (compDeadline != 0) return compDeadline;
				return o1.getGrantDate().compareTo(o2.getGrantDate());
			}
		});

		// 取得した「年休付与残数データ」をすべて「処理単位分割日リスト」に追加
		for (val remainingData : remainingDatas){
			val deadline = remainingData.getDeadline();
			if (!aggrPeriod.contains(deadline)) continue;

			val nextDayOfDeadline = deadline.addDays(1);
			dividedDayMap.putIfAbsent(nextDayOfDeadline, new DividedDayEachProcess(nextDayOfDeadline));
			dividedDayMap.get(nextDayOfDeadline).getLapsedWork().setLapsedAtr(true);
		}

		// 「次回年休付与リスト」をすべて「処理単位分割日リスト」に追加
		for (val nextAnnualLeaveGrant : nextAnnualLeaveGrantList){
			val grantDate = nextAnnualLeaveGrant.getGrantDate();
			if (grantDate.before(aggrPeriod.start().addDays(1))) continue;
			if (grantDate.after(nextDayOfPeriodEnd)) continue;

			dividedDayMap.putIfAbsent(grantDate, new DividedDayEachProcess(grantDate));
			dividedDayMap.get(grantDate).getGrantWork().setGrantAtr(true);
			dividedDayMap.get(grantDate).getGrantWork().setAnnualLeaveGrant(Optional.of(nextAnnualLeaveGrant));
		}

		// 期間終了日翌日の「処理単位分割日」を取得・追加　→　フラグ設定
		dividedDayMap.putIfAbsent(nextDayOfPeriodEnd, new DividedDayEachProcess(nextDayOfPeriodEnd));
		dividedDayMap.get(nextDayOfPeriodEnd).getEndDay().setNextPeriodEndAtr(true);

		// 「処理単位分割日」をソート
		List<DividedDayEachProcess> dividedDayList = new ArrayList<>();
		dividedDayList.addAll(dividedDayMap.values());
		dividedDayList.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));

		// 「年休集計期間WORK」を作成
		AggregatePeriodWork startWork = new AggregatePeriodWork();
		
		if ( 0 < dividedDayList.size() ){
			val startWorkEnd = dividedDayList.get(0).getYmd().addDays(-1);
			startWork.setPeriod(new DatePeriod(aggrPeriod.start(), startWorkEnd));
			// 消滅　←最初の「処理単位分割日．消滅情報WORK」
			startWork.setLapsedAtr(dividedDayList.get(0).getLapsedWork());
		}
		aggregatePeriodWorks.add(startWork);

		// 付与後フラグ
		/**付与前か付与後か */
		GrantBeforeAfterAtr grantPeriodAtr = GrantBeforeAfterAtr.BEFORE_GRANT;

		for (int index = 0; index < dividedDayList.size(); index++){
			val nowDividedDay = dividedDayList.get(index);
			Optional<DividedDayEachProcess> nextDividedDay = Optional.empty();
			if (index + 1 < dividedDayList.size()) nextDividedDay = Optional.of(dividedDayList.get(index + 1));

			// 付与フラグをチェック
			if (nowDividedDay.getGrantWork().isGrantAtr()) {
				grantPeriodAtr = GrantBeforeAfterAtr.AFTER_GRANT;
			}

			// 年休集計期間WORKを作成し、Listに追加
			
			// 期間．終了日←次の「処理単位分割日．年月日」の前日
			// 　※次の処理単位分割日がない場合、パラメータ「終了日」の翌日
			GeneralDate workPeriodEnd = nextDayOfPeriodEnd;
			if (nextDividedDay.isPresent()) workPeriodEnd = nextDividedDay.get().getYmd().addDays(-1);
			
			// 消滅←次の「処理単位分割日.消滅情報WORK」
			//　　※次の処理単位分割日がない場合、「期間の開始日に消滅するかどうか」をfalseで消滅情報WORKを作成
			AnnualLeaveLapsedWork lapsedWork = new AnnualLeaveLapsedWork(false);
			if ( nextDividedDay.isPresent() ){
				lapsedWork = nextDividedDay.get().getLapsedWork();
			}

			AggregatePeriodWork nowWork = new AggregatePeriodWork(new DatePeriod(nowDividedDay.getYmd(), workPeriodEnd),
					lapsedWork,
					nowDividedDay.getGrantWork(),
					nowDividedDay.getEndDay(),
					grantPeriodAtr);

			aggregatePeriodWorks.add(nowWork);
		}

		// 処理期間内で何回目の付与なのかを保持。（一回目の付与を判断したい）
		AtomicInteger grantNumber = new AtomicInteger(1);
		for( AggregatePeriodWork nowWork : aggregatePeriodWorks){
			if ( nowWork.getGrantWork().isGrantAtr() ) // 付与のとき
			{
				nowWork.getGrantWork().setGrantNumber(grantNumber.get());
				grantNumber.incrementAndGet();
			}
		}

		for(AggregatePeriodWork work : aggregatePeriodWorks) {
			if(work.getPeriod().contains(aggrPeriod.end()))
				work.getEndWork().setPeriodEndAtr(true);
			if(work.getPeriod().contains(aggrPeriod.end().addDays(1)))
				work.getEndWork().setNextPeriodEndAtr(true);
		}

		return aggregatePeriodWorks;
	}

	/**
	 * 暫定年休管理データを取得する
	 * @return 暫定年休管理データWORKリスト
	 */
	private static List<TempAnnualLeaveMngs> getTempAnnualLeaveMngs(
			RequireM2 require,
			String employeeId,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			Optional<Boolean> isOverWriteOpt,
			Optional<List<TempAnnualLeaveMngs>> forOverWriteListOpt,
			Optional<DatePeriod> isOverWritePeriod){

		List<TempAnnualLeaveMngs> results = new ArrayList<>();

		// 「モード」をチェック
		if (mode == InterimRemainMngMode.MONTHLY){
			// 月次モード
		}
		if (mode == InterimRemainMngMode.OTHER){
			// その他モード

			// 「暫定年休管理データ」を取得する
			val interimRemains = require.tmpAnnualHolidayMng(employeeId, aggrPeriod);
			for (val master : interimRemains){
				results.add(master);
			}

			// 年休フレックス補填分を暫定年休データに反映する
			{
				// 「月別実績の勤怠時間」を取得
				val attendanceTimes = require.attendanceTimeOfMonthly(employeeId, aggrPeriod);
				for (val attendanceTime : attendanceTimes){

					// 月別実績の勤怠時間からフレックス補填の暫定年休管理データを作成する
					val compensFlexWorkOpt = CreateInterimAnnualMngData.ofCompensFlexToWork(
							attendanceTime, attendanceTime.getDatePeriod().end());

					// 「暫定年休管理データ」を返す
					if (compensFlexWorkOpt.isPresent()) results.add(compensFlexWorkOpt.get());
				}
			}
		}

		// 「上書きフラグ」をチェック
		if (isOverWriteOpt.isPresent()){
			if (isOverWriteOpt.get()){
				if(isOverWritePeriod.isPresent()){

					//上書き対象期間内の暫定年休管理データを削除
					results.removeIf(x -> isOverWritePeriod.get().contains(x.getYmd()));

					// 上書き用データがある時、追加する
					if (forOverWriteListOpt.isPresent()){
						val overWrites = forOverWriteListOpt.get();
						for (val overWrite : overWrites){

							// 上書き用データを追加
							results.add(overWrite);
						}
					}
				}
			}
		}

		results.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));
		return results;
	}


	public static interface RequireM2 {

//		List<InterimRemain> interimRemains(String employeeId, DatePeriod dateData, RemainType remainType);

		List<TempAnnualLeaveMngs> tmpAnnualHolidayMng(String sid, DatePeriod dateData);

		List<AttendanceTimeOfMonthly> attendanceTimeOfMonthly(String employeeId, DatePeriod period);
	}

	public static interface RequireM3 extends RequireM2,
												GetClosureStartForEmployee.RequireM1,
												CalcNextAnnualLeaveGrantDate.RequireM2,
												CalcAnnLeaAttendanceRate.RequireM1,
												LeaveRemainingNumber.RequireM3,
//												GetClosureIdHistory.RequireM2{
												GetClosureIdHistory.RequireM2,
												nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService.RequireM3{

		List<AnnualLeaveRemainingHistory> annualLeaveRemainingHistory(String sid, YearMonth ym);

		/** 締め状態管理 */
		Optional<ClosureStatusManagement> latestClosureStatusManagement(String employeeId);

		/** 年休上限履歴データを取得する */
		Optional<AnnualLeaveMaxHistoryData> AnnualLeaveMaxHistoryData(
				String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);

		Optional<AnnualLeaveMaxData> annualLeaveMaxData(String employeeId);

		List<AnnualLeaveGrantRemainingData> annualLeaveGrantRemainingData(String employeeId);

		Optional<OperationStartSetDailyPerform> dailyOperationStartSet(CompanyId companyId);
	}

}
