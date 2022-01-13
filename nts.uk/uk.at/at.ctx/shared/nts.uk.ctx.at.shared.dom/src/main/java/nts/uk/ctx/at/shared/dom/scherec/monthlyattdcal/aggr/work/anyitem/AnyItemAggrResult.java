package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.anyitem;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyAmountMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimeMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimesMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AggregateAnyItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrder;

/**
 * 任意項目集計結果
 * @author shuichi_ishida
 */
@Getter
public class AnyItemAggrResult {

	/** 任意項目NO */
	private int optionalItemNo;
	/** 時間 */
	private Optional<AnyTimeMonth> anyTime;
	/** 回数 */
	private Optional<AnyTimesMonth> anyTimes;
	/** 金額 */
	private Optional<AnyAmountMonth> anyAmount;
	
	private AnyItemAggrResult(){
		this.optionalItemNo = 0;
		this.anyTime = Optional.empty();
		this.anyTimes = Optional.empty();
		this.anyAmount = Optional.empty();
	}

	/**
	 * ファクトリー
	 * @param optionalItemNo 任意項目NO
	 * @param anyTime 時間
	 * @param anyTimes 回数
	 * @param anyAmount 金額
	 * @return 任意項目集計結果
	 */
	public static AnyItemAggrResult of(
			int optionalItemNo,
			Optional<AnyTimeMonth> anyTime,
			Optional<AnyTimesMonth> anyTimes,
			Optional<AnyAmountMonth> anyAmount){

		AnyItemAggrResult domain = new AnyItemAggrResult();
		domain.optionalItemNo = optionalItemNo;
		domain.anyTime = anyTime;
		domain.anyTimes = anyTimes;
		domain.anyAmount = anyAmount;
		return domain;
	}

	/**
	 * ファクトリー
	 * @param optionalItemNo 任意項目NO
	 * @param optionalItem 任意項目
	 * @return 任意項目集計結果
	 */
	public static AnyItemAggrResult of(
			int optionalItemNo,
			OptionalItem optionalItem){

		AnyItemAggrResult domain = new AnyItemAggrResult();
		domain.optionalItemNo = optionalItemNo;
		
		// 属性に応じて初期化
		switch (optionalItem.getOptionalItemAtr()){
		case TIME:
			domain.anyTime = Optional.of(new AnyTimeMonth(0));
			break;
		case NUMBER:
			domain.anyTimes = Optional.of(new AnyTimesMonth(0.0));
			break;
		case AMOUNT:
			domain.anyAmount = Optional.of(new AnyAmountMonth(0));
			break;
		}
		
		return domain;
	}
	
	/**
	 * 時間に分を加算する
	 * @param minutes 分
	 */
	public void addTime(int minutes){
		this.anyTime = Optional.of(this.anyTime.map(c -> c.addMinutes(minutes)).orElse(new AnyTimeMonth(minutes)));
	}
	
	/**
	 * 回数に加算する
	 * @param times 回数
	 */
	public void addTimes(double times){
		this.anyTimes = Optional.of(this.anyTimes.map(c -> c.addTimes(times)).orElse(new AnyTimesMonth(times)));
	}
	
	/**
	 * 金額に加算する
	 * @param amount 金額
	 */
	public void addAmount(int amount){
		this.anyAmount = Optional.of(this.anyAmount.map(c -> c.addAmount(amount)).orElse(new AnyAmountMonth(amount)));
	}
	
	/**
	 * 日別実績　縦計処理
	 * @param optionalItemNo 任意項目NO
	 * @param optionalItem 任意項目
	 * @param anyItemTotals 日別実績縦計結果
	 * @return 任意項目集計結果
	 */
	public static AnyItemAggrResult calcFromDailys(Integer optionalItemNo, OptionalItem optionalItem,
			Map<Integer, AggregateAnyItem> anyItemTotals){
		
		AnyItemAggrResult domain = AnyItemAggrResult.of(optionalItemNo, optionalItem);
		
		// 日別実績　縦計処理
		if (anyItemTotals.containsKey(optionalItemNo)){
			val anyItemTotal = anyItemTotals.get(optionalItemNo);
			if (anyItemTotal.getTime().isPresent()){
				domain.addTime(anyItemTotal.getTime().get().v());
			}
			if (anyItemTotal.getTimes().isPresent()){
				domain.addTimes(anyItemTotal.getTimes().get().v().doubleValue());
			}
			if (anyItemTotal.getAmount().isPresent()){
				domain.addAmount(anyItemTotal.getAmount().get().v());
			}
		}
		
		// 任意項目集計結果を返す
		return domain;
	}
	
	/**
	 * 月別実績　計算処理
	 * @param optionalItemNo 任意項目NO
	 * @param optionalItem 任意項目
	 * @param attendanceTime 月別実績の勤怠時間
	 * @param anyItems 月別実績の任意項目リスト
	 * @param companySets 月別集計で必要な会社別設定
	 * @return 任意項目集計結果
	 */
	public static AnyItemAggrResult calcFromMonthly(RequireM1 require, Integer optionalItemNo, OptionalItem optionalItem,
			AttendanceTimeOfMonthly attendanceTime, List<AnyItemOfMonthly> anyItems, MonAggrCompanySettings companySets,
			PerformanceAtr performanceAtr) {
		
		AnyItemAggrResult domain = AnyItemAggrResult.of(optionalItemNo, optionalItem);
		
		// 月別実績　計算処理
		List<Formula> targetFormulas = companySets.getFormulaList().stream()
				.filter(c -> c.getOptionalItemNo().equals(optionalItem.getOptionalItemNo()))
				.collect(Collectors.toList());
		List<FormulaDispOrder> targetFormulaOrders = companySets.getFormulaOrderList().stream()
				.filter(c -> c.getOptionalItemNo().equals(optionalItem.getOptionalItemNo()))
				.collect(Collectors.toList());
		val monthlyConverter = require.createMonthlyConverter();
		MonthlyRecordToAttendanceItemConverter monthlyRecordDto = monthlyConverter.withAttendanceTime(attendanceTime);
		monthlyRecordDto = monthlyRecordDto.withAnyItem(anyItems);
		val calcResult = optionalItem.caluculationFormula(
				companySets.getCompanyId(), targetFormulas, targetFormulaOrders,
				Optional.empty(), Optional.of(monthlyRecordDto), performanceAtr);
		if (calcResult != null){
			if (calcResult.getTime().isPresent()){
				domain.addTime(calcResult.getTime().get().intValue());
			}
			if (calcResult.getCount().isPresent()){
				domain.addTimes(calcResult.getCount().get().doubleValue());
			}
			if (calcResult.getMoney().isPresent()){
				domain.addAmount(calcResult.getMoney().get().intValue());
			}
		}
		
		// 任意項目集計結果を返す
		return domain;
	}
	
	public static interface RequireM1 {
		
		MonthlyRecordToAttendanceItemConverter createMonthlyConverter();
	}
}
