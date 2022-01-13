package nts.uk.ctx.at.shared.dom.scherec.byperiod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.AnyItemAggregateService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AggregateAnyItem;

/**
 * 期間別の任意項目
 * @author shuichu_ishida
 */
@Getter
public class AnyItemByPeriod implements Cloneable {

	/** 任意項目値 */
	private Map<Integer, AggregateAnyItem> anyItemValues;
	
	/**
	 * コンストラクタ
	 */
	public AnyItemByPeriod(){
		
		this.anyItemValues = new HashMap<>();
	}
	
	/**
	 * ファクトリー
	 * @param anyItemValues 任意項目値
	 * @return 期間別の任意項目
	 */
	public static AnyItemByPeriod of(
			List<AggregateAnyItem> anyItemValues){
		
		AnyItemByPeriod domain = new AnyItemByPeriod();
		for (val anyItemValue : anyItemValues){
			val anyItemNo = anyItemValue.getAnyItemNo();
			domain.anyItemValues.putIfAbsent(anyItemNo, anyItemValue);
		}
		return domain;
	}
	
	@Override
	public AnyItemByPeriod clone() {
		AnyItemByPeriod cloned = new AnyItemByPeriod();
		try {
			for (val value : this.anyItemValues.entrySet()){
				cloned.anyItemValues.putIfAbsent(value.getKey(), value.getValue().clone());
			}
		}
		catch (Exception e){
			throw new RuntimeException("AnyItemByPeriod clone error.");
		}
		return cloned;
	}
	
	/**
	 * 集計処理
	 * @param period 期間
	 * @param calcDailys 月の計算中の日別実績データ
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 */
	public void aggregate(
			DatePeriod period,
			MonthlyCalculatingDailys calcDailys,
			MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets) {
		
		val result = AnyItemAggregateService.aggregateAnyItemAnyPeriod(period, calcDailys, companySets, employeeSets);
		
		result.entrySet().forEach(r -> {

			// 任意項目集計結果を返す
			this.anyItemValues.put(r.getKey(), AggregateAnyItem.of(r.getKey(), 
																	r.getValue().getAnyTime(), 
																	r.getValue().getAnyTimes(), 
																	r.getValue().getAnyAmount()));
		});
	}
}
