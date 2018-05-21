package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.basicinfo.CalcNextAnnualLeaveGrantDate;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnLeaGrantRemDataRepository;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.maxdata.AnnLeaMaxDataRepository;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggregatePeriodWork;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AnnualLeaveInfo;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnualLeave;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.export.NextAnnualLeaveGrant;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * 実装：期間中の年休残数を取得
 * @author shuichu_ishida
 */
@Stateless
public class GetAnnLeaRemNumWithinPeriodImpl implements GetAnnLeaRemNumWithinPeriod {

	/** 年休設定 */
	@Inject
	private AnnualPaidLeaveSettingRepository annualPaidLeaveSet;
	/** 年休付与残数データ */
	@Inject
	private AnnLeaGrantRemDataRepository annLeaGrantRemDataRepo;
	/** 年休上限データ */
	@Inject
	private AnnLeaMaxDataRepository annLeaMaxDataRepo;
	/** 社員に対応する締め開始日を取得する */
	@Inject
	private GetClosureStartForEmployee getClosureStartForEmployee;
	/** 次回年休付与日を計算 */
	@Inject
	private CalcNextAnnualLeaveGrantDate calcNextAnnualLeaveGrantDate;
	/** 暫定年休管理データを作成する */
	@Inject
	private CreateTempAnnualLeaveManagement createTempAnnualLeaveMng;
	/** 期間中の年休残数を取得 */
	@Inject
	private GetAnnLeaRemNumWithinPeriod getAnnLeaRemNumWithinPeriod;
	
	/** 会社ID */
	private String companyId;
	/** 社員ID */
	private String employeeId;
	/** 集計期間 */
	private DatePeriod aggrPeriod;
	/** モード */
	private TempAnnualLeaveMngMode mode;
	/** 翌月管理データ取得フラグ */
	private boolean isGetNextMonthData;
	/** 出勤率計算フラグ */
	private boolean isCalcAttendanceRate;
	/** 上書きフラグ */
	private Optional<Boolean> isOverWriteOpt;
	/** 上書き用の暫定年休管理データ */
	private Optional<List<TempAnnualLeaveManagement>> forOverWriteListOpt;
	/** 前回の年休の集計結果 */
	private Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt;
	/** 年休集計期間WORKリスト */
	private List<AggregatePeriodWork> aggregatePeriodWorks;
	/** 年休付与残数データリスト */
	private List<AnnualLeaveGrantRemainingData> grantRemainingDatas;

	/** 期間中の年休残数を取得 */
	@Override
	public Optional<AggrResultOfAnnualLeave> algorithm(
			String companyId, String employeeId, DatePeriod aggrPeriod, TempAnnualLeaveMngMode mode,
			GeneralDate criteriaDate, boolean isGetNextMonthData, boolean isCalcAttendanceRate,
			Optional<Boolean> isOverWriteOpt, Optional<List<TempAnnualLeaveManagement>> forOverWriteListOpt,
			Optional<AggrResultOfAnnualLeave> prevAnnualLeaveOpt) {
		
		this.companyId = companyId;
		this.employeeId = employeeId;
		this.aggrPeriod = aggrPeriod;
		this.mode = mode;
		this.isGetNextMonthData = isGetNextMonthData;
		this.isCalcAttendanceRate = isCalcAttendanceRate;
		this.isOverWriteOpt = isOverWriteOpt;
		this.forOverWriteListOpt = forOverWriteListOpt;
		this.prevAnnualLeaveOpt = prevAnnualLeaveOpt;
		
		// 年休の使用区分を取得する
		boolean isManageAnnualLeave = false;
		val annualLeaveSet = this.annualPaidLeaveSet.findByCompanyId(companyId);
		if (annualLeaveSet != null) isManageAnnualLeave = annualLeaveSet.isManaged();
		if (!isManageAnnualLeave) return Optional.empty();

		AggrResultOfAnnualLeave aggrResult = new AggrResultOfAnnualLeave();
		
		// 年休付与残数データ　取得
		this.grantRemainingDatas = this.annLeaGrantRemDataRepo.findNotExp(employeeId);
		
		// 集計開始日時点の年休情報を作成
		AnnualLeaveInfo annualLeaveInfo = this.createInfoAsOfPeriodStart();
		
		// 次回年休付与日を計算
		GeneralDate calcEnd = aggrPeriod.end();
		if (calcEnd.before(GeneralDate.max())) calcEnd = calcEnd.addDays(1);
		val calcPeriod = new DatePeriod(aggrPeriod.start(), calcEnd);
		val nextAnnualLeaveGrantList = this.calcNextAnnualLeaveGrantDate.algorithm(
				companyId, employeeId, Optional.of(calcPeriod));
		
		// 年休集計期間を作成
		this.createAggregatePeriod(nextAnnualLeaveGrantList);
		
		// 暫定年休管理データを作成する
		val tempAnnualLeaveMngs = this.createTempAnnualLeaveMng.algorithm(companyId, employeeId, aggrPeriod, mode);
		
		for (val aggregatePeriodWork : this.aggregatePeriodWorks){

			// 年休の消滅・付与・消化
			aggrResult = annualLeaveInfo.lapsedGrantDigest(companyId, employeeId, aggregatePeriodWork,
					tempAnnualLeaveMngs, isGetNextMonthData, isCalcAttendanceRate, aggrResult, annualLeaveSet);
		}
		
		// 「年休の集計結果」を返す
		return Optional.of(aggrResult);
	}
	
	/**
	 * 集計開始日時点の年休情報を作成
	 * @return 年休情報
	 */
	private AnnualLeaveInfo createInfoAsOfPeriodStart(){
	
		AnnualLeaveInfo emptyInfo = new AnnualLeaveInfo();
		emptyInfo.setYmd(this.aggrPeriod.start());
		
		// 「前回の年休情報」を確認　（前回の年休の集計結果．年休情報（期間終了日の翌日開始時点））
		AnnualLeaveInfo prevAnnualLeaveInfo = null;
		if (this.prevAnnualLeaveOpt.isPresent()){
			prevAnnualLeaveInfo = this.prevAnnualLeaveOpt.get().getAsOfStartNextDayOfPeriodEnd();
		}
		
		// 「開始日」と「年休情報．年月日」を比較
		boolean isSameInfo = false;
		if (prevAnnualLeaveInfo != null){
			if (this.aggrPeriod.start() == prevAnnualLeaveInfo.getYmd()){
				isSameInfo = true;
			}
		}
		if (isSameInfo){
			
			// 「前回の年休情報」を取得　→　取得内容をもとに年休情報を作成
			return this.createInfoFromRemainingData(
					prevAnnualLeaveInfo.getGrantRemainingNumberList(), prevAnnualLeaveInfo.getMaxData());
		}
		
		//　社員に対応する締め開始日を取得する
		val closureStartOpt = this.getClosureStartForEmployee.algorithm(this.employeeId);
		boolean isAfterClosureStart = false;
		if (closureStartOpt.isPresent()){
			if (closureStartOpt.get().before(this.aggrPeriod.start())) isAfterClosureStart = true;
		}
		
		if (isAfterClosureStart){
			// 締め開始日<集計開始日　の時
			
			// 開始日までの年休残数を計算　（締め開始日～集計開始日前日）
			val aggrResultOpt = this.getAnnLeaRemNumWithinPeriod.algorithm(
					this.companyId, this.employeeId,
					new DatePeriod(closureStartOpt.get(), this.aggrPeriod.start().addDays(-1)),
					this.mode,
					this.aggrPeriod.start().addDays(-1),
					this.isGetNextMonthData,
					this.isCalcAttendanceRate,
					this.isOverWriteOpt,
					this.forOverWriteListOpt,
					Optional.empty());
			if (!aggrResultOpt.isPresent()) return emptyInfo;
			val aggrResult = aggrResultOpt.get();
			
			// 年休情報（期間終了日の翌日開始時点）を取得
			val asOfPeriodEnd = aggrResult.getAsOfPeriodEnd();
			
			// 取得内容をもとに年休情報を作成
			return this.createInfoFromRemainingData(asOfPeriodEnd.getGrantRemainingNumberList(),
					asOfPeriodEnd.getMaxData());
		}

		// 締め開始日>=集計開始日　or 締め開始日がnull　の時
		
		// 「年休上限データ」を取得
		val annLeaMaxDataOpt = this.annLeaMaxDataRepo.get(this.employeeId);
		if (!annLeaMaxDataOpt.isPresent()) return emptyInfo;
		val annLeaMaxData = annLeaMaxDataOpt.get();

		// 取得内容をもとに年休情報を作成
		return this.createInfoFromRemainingData(this.grantRemainingDatas, annLeaMaxData);
	}
	
	/**
	 * 年休付与残数データから年休情報を作成
	 * @param grantRemainingDataList 付与残数データリスト
	 * @param maxData 上限データ
	 * @return 年休情報
	 */
	private AnnualLeaveInfo createInfoFromRemainingData(
			List<AnnualLeaveGrantRemainingData> grantRemainingDataList,
			AnnualLeaveMaxData maxData){
		
		AnnualLeaveInfo returnInfo = new AnnualLeaveInfo();
		returnInfo.setYmd(this.aggrPeriod.start());

		// 年休情報．年休付与情報　←　パラメータ「付与残数データ」
		returnInfo.setGrantRemainingNumberList(grantRemainingDataList);
		
		// 年休情報残数を更新
		returnInfo.updateRemainingNumber();
		
		return returnInfo;
	}
	
	/**
	 * 年休集計期間を作成
	 * @param nextAnnualLeaveGrantList 次回年休付与リスト
	 * @return 年休集計期間WORKリスト
	 */
	private void createAggregatePeriod(List<NextAnnualLeaveGrant> nextAnnualLeaveGrantList){
		
		this.aggregatePeriodWorks = new ArrayList<>();
		
		// 処理単位分割日リスト
		Map<GeneralDate, DividedDayEachProcess> dividedDayMap = new HashMap<>();
		
		// 期間終了日翌日
		GeneralDate nextDayOfPeriodEnd = this.aggrPeriod.end();
		if (nextDayOfPeriodEnd.before(GeneralDate.max())) nextDayOfPeriodEnd = nextDayOfPeriodEnd.addDays(1);
		
		// 「年休付与残数データ」を取得　（期限日　昇順、付与日　昇順）
		List<AnnualLeaveGrantRemainingData> remainingDatas = new ArrayList<>();
		remainingDatas.addAll(this.grantRemainingDatas);
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
			if (!this.aggrPeriod.contains(deadline)) continue;
			
			val nextDayOfDeadline = deadline.addDays(1);
			dividedDayMap.putIfAbsent(nextDayOfDeadline, new DividedDayEachProcess(nextDayOfDeadline));
			dividedDayMap.get(nextDayOfDeadline).setLapsedAtr(true);
		}
		
		// 「次回年休付与リスト」をすべて「処理単位分割日リスト」に追加
		for (val nextAnnualLeaveGrant : nextAnnualLeaveGrantList){
			val grantDate = nextAnnualLeaveGrant.getGrantDate();
			if (grantDate.beforeOrEquals(this.aggrPeriod.start().addDays(1))) continue;
			if (grantDate.after(nextDayOfPeriodEnd)) continue;
			
			dividedDayMap.putIfAbsent(grantDate, new DividedDayEachProcess(grantDate));
			dividedDayMap.get(grantDate).setGrantAtr(true);
			dividedDayMap.get(grantDate).setNextAnnualLeaveGrant(Optional.of(nextAnnualLeaveGrant));
		}
		
		// 期間終了日翌日の「処理単位分割日」を取得・追加　→　フラグ設定
		dividedDayMap.putIfAbsent(nextDayOfPeriodEnd, new DividedDayEachProcess(nextDayOfPeriodEnd));
		dividedDayMap.get(nextDayOfPeriodEnd).setNextDayAfterPeriodEnd(true);
		
		// 「処理単位分割日」をソート
		List<DividedDayEachProcess> dividedDayList = new ArrayList<>();
		dividedDayList.addAll(dividedDayMap.values());
		dividedDayList.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));
		
		// 「年休集計期間WORK」を作成
		AggregatePeriodWork startWork = new AggregatePeriodWork();
		val startWorkEnd = dividedDayList.get(0).getYmd().addDays(-1);
		startWork.setPeriod(new DatePeriod(this.aggrPeriod.start(), startWorkEnd));
		this.aggregatePeriodWorks.add(startWork);
		
		// 付与後フラグ
		boolean isAfterGrant = false;
		
		for (int index = 0; index < dividedDayList.size(); index++){
			val nowDividedDay = dividedDayList.get(index);
			DividedDayEachProcess nextDividedDay = null;
			if (index + 1 < dividedDayList.size()) nextDividedDay = dividedDayList.get(index + 1);
			
			// 付与フラグをチェック
			if (nowDividedDay.isGrantAtr()) isAfterGrant = true;
			
			// 年休集計期間WORKを作成し、Listに追加
			GeneralDate workPeriodEnd = nextDayOfPeriodEnd;
			if (nextDividedDay != null) workPeriodEnd = nextDividedDay.getYmd();
			AggregatePeriodWork nowWork = AggregatePeriodWork.of(
					new DatePeriod(nowDividedDay.getYmd(), workPeriodEnd),
					nowDividedDay.isNextDayAfterPeriodEnd(),
					nowDividedDay.isGrantAtr(),
					isAfterGrant,
					nowDividedDay.isLapsedAtr(),
					nowDividedDay.getNextAnnualLeaveGrant());
			this.aggregatePeriodWorks.add(nowWork);
		}
	}
}
