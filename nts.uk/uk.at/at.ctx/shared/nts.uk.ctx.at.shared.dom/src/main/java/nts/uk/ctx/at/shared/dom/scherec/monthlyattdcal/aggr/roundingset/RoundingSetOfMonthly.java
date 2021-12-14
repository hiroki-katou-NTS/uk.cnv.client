package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.roundingset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;

/**
 * 月別実績の丸め設定
 * @author shuichu_ishida
 */
@Getter
public class RoundingSetOfMonthly extends AggregateRoot {

	/** 会社ID */
	private String companyId;
	/** 項目丸め設定 */
	private Map<Integer, ItemRoundingSetOfMonthly> itemRoundingSet;
	
	/**
	 * コンストラクタ
	 * @param companyId 会社ID
	 */
	public RoundingSetOfMonthly(String companyId){
		
		super();
		this.companyId = companyId;
		this.itemRoundingSet = new HashMap<>();
	}

	/**
	 * ファクトリー
	 * @param companyId 会社ID
	 * @param itemRoundingSets 項目丸め設定
	 * @return 月別実績の丸め設定
	 */
	public static RoundingSetOfMonthly of(
			String companyId,
			List<ItemRoundingSetOfMonthly> itemRoundingSets){

		RoundingSetOfMonthly domain = new RoundingSetOfMonthly(companyId);
		for (val itemRoundingSet : itemRoundingSets){
			val attendanceItemId = itemRoundingSet.getAttendanceItemId();
			domain.itemRoundingSet.putIfAbsent(attendanceItemId, itemRoundingSet);
		}
		return domain;
	}
	
	/** 月別実績の時間項目を丸める */
	public AttendanceTimeOfMonthly round(Require require, AttendanceTimeOfMonthly monthlyAttendanceTime) {
			
		val converter = require.createMonthlyConverter();
		converter.withAttendanceTime(monthlyAttendanceTime);

		/** 丸め設定を取得する */
		this.itemRoundingSet.entrySet().forEach(v -> {
			/** 月別実績の項目の値を取得 */
			List<ItemValue> values = getMonthlyItemValue(converter, v);

			/** 丸め処理を行う */
			values = round(v.getValue(), values);
			
			converter.merge(values);
		});
		
		/** 月別実績の勤怠時間を返す */
		return converter.toAttendanceTime().get();
	}

	private List<ItemValue> round(ItemRoundingSetOfMonthly roundSet, List<ItemValue> values) {
		
		return values.stream().map(c -> {
			if (c.value() != null) {
				/** 丸め処理を行う */
				c.value(roundSet.getRoundingSet().round(c.value()));
			}
			
			return c;
		}).collect(Collectors.toList());
	}

	/** 月別実績の項目の値を取得 */
	private List<ItemValue> getMonthlyItemValue(MonthlyRecordToAttendanceItemConverter converter,
			Entry<Integer, ItemRoundingSetOfMonthly> v) {
		
		if (v.getKey() == AttendanceItemOfMonthly.FLEX_TIME.value) 
			
			return converter.convert(Arrays.asList(v.getKey(), AttendanceItemOfMonthly.FLEX_SHORTAGE_TIME.value, 
														AttendanceItemOfMonthly.FLEX_EXCESS_TIME.value));
		else if (v.getKey() >= AttendanceItemOfMonthly.HOLIDAY_WORK_TIME_01.value 
				&& v.getKey() <= AttendanceItemOfMonthly.HOLIDAY_WORK_TIME_10.value) 
			return converter.convert(Arrays.asList(v.getKey(), v.getKey() + 33));
		
		else if (v.getKey() >= AttendanceItemOfMonthly.OVER_TIME_01.value 
				&& v.getKey() <= AttendanceItemOfMonthly.OVER_TIME_10.value) 
			return converter.convert(Arrays.asList(v.getKey(), v.getKey() + 1329));
		return converter.convert(Arrays.asList(v.getKey()));
	}
	
	public static interface Require {
		
		public MonthlyRecordToAttendanceItemConverter createMonthlyConverter();
	}


	/**
	 * 項目丸め
	 * @param attendanceItemId 勤怠項目ID
	 * @param attendanceTimeMonth 勤怠月間時間　（丸め前）
	 * @return 勤怠月間時間　（丸め後）
	 */
	public AttendanceTimeMonth itemRound(int attendanceItemId, AttendanceTimeMonth attendanceTimeMonth){

		int minutes = attendanceTimeMonth.v();
		if (!this.itemRoundingSet.containsKey(attendanceItemId)) return attendanceTimeMonth;
		val targetRoundingSet = this.itemRoundingSet.get(attendanceItemId);
		return new AttendanceTimeMonth(targetRoundingSet.getRoundingSet().round(minutes));
	}
}
