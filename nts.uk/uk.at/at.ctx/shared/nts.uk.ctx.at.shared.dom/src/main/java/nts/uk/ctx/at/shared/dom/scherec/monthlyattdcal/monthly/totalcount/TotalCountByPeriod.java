package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.totalcount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.MonthlyAggregationErrorInfo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.attdstatus.AttendanceStatusList;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalCount;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimes;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;

/**
 * 期間別の回数集計
 * @author shuichi_ishida
 */
@Getter
public class TotalCountByPeriod implements Cloneable, Serializable {

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 回数集計 */
	private Map<Integer, TotalCount> totalCountList;

	/** エラー情報 */
	private List<MonthlyAggregationErrorInfo> errorInfos;
	
	/*require用*/
	private WorkTypeRepository workTypeRepo;
	
	/**
	 * コンストラクタ
	 */
	public TotalCountByPeriod(){
		
		this.totalCountList = new HashMap<>();
		
		this.errorInfos = new ArrayList<>();
	}

	/**
	 * ファクトリー
	 * @param totalCountList 回数集計
	 * @return 期間別の回数集計
	 */
	public static TotalCountByPeriod of(
			List<TotalCount> totalCountList){
		
		TotalCountByPeriod domain = new TotalCountByPeriod();
		for (val totalCount : totalCountList){
			int totalCountNo = totalCount.getTotalCountNo();
			domain.totalCountList.putIfAbsent(totalCountNo, totalCount);
		}
		return domain;
	}
	
	@Override
	public TotalCountByPeriod clone() {
		TotalCountByPeriod cloned = new TotalCountByPeriod();
		try {
			for (val totalCount : this.totalCountList.entrySet()){
				cloned.totalCountList.put(totalCount.getKey(), totalCount.getValue().clone());
			}
		}
		catch (Exception e){
			throw new RuntimeException("TotalCountbyPeriod clone error.");
		}
		return cloned;
	}
	
	/**
	 * 回数集計する
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param companySets 月別集計で必要な会社別設定
	 * @param monthlyCalcDailys 月の計算中の日別実績データ
	 */
	public void totalize(RequireM1 require, String companyId, String employeeId,
			DatePeriod period, MonAggrCompanySettings companySets, MonthlyCalculatingDailys monthlyCalcDailys){

		val dailyWorks = monthlyCalcDailys.getDailyWorks(employeeId)
				.stream().filter(d -> period.contains(d.getYmd())).collect(Collectors.toList());
		
		val attendanceStates = new AttendanceStatusList(monthlyCalcDailys.getAttendanceTimeOfDailyMap(), 
														monthlyCalcDailys.getTimeLeaveOfDailyMap());
		
		// 回数集計マスタを取得
		val totalTimesList = companySets.getTotalTimesList();
		
		/** ○パラメータ「回数集計」一覧をループ */
		totalTimesList.stream().forEach(tt -> {
			
			totalCountList.put(tt.getTotalCountNo(), tt.aggregateTotalCount(require, dailyWorks, attendanceStates));
		});
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(TotalCountByPeriod target){

		for (val totalCount : this.totalCountList.values()){
			val totalCountNo = totalCount.getTotalCountNo();
			if (target.totalCountList.containsKey(totalCountNo)){
				totalCount.sum(target.totalCountList.get(totalCountNo));
			}
		}
		for (val targetTotalCount : target.getTotalCountList().values()){
			val totalCountNo = targetTotalCount.getTotalCountNo();
			this.totalCountList.putIfAbsent(totalCountNo, targetTotalCount);
		}
	}
	
	public static interface RequireM1 extends TotalTimes.RequireM1 {

	}
}