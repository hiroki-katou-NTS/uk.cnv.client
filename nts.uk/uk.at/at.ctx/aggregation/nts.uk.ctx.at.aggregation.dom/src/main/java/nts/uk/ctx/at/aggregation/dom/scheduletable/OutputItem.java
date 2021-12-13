package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Value;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.gul.util.OptionalUtil;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 出力項目
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.出力項目
 * @author dan_pv
 *
 */
@Value
public class OutputItem implements DomainValue {

	/**
	 * 	追加列情報の利用区分
	 */
	private final NotUseAtr additionalColumnUseAtr;
	
	/**
	 * シフト背景色の表示区分	
	 */
	private final NotUseAtr shiftBackgroundColorUseAtr;
	
	/**
	 * 	実績表示区分
	 */
	private final NotUseAtr dailyDataDisplayAtr;
	
	/**
	 * 	詳細リスト	
	 */
	private final List<OneRowOutputItem> details;
	
	/**
	 * 作る
	 * @param additionalColumnUseAtr 追加列情報の利用区分
	 * @param shiftBackgroundColorUseAtr シフト背景色の表示区分	
	 * @param dailyDataDisplayAtr 実績表示区分
	 * @param details 詳細リスト
	 * @return
	 */
	public static OutputItem create(
			NotUseAtr additionalColumnUseAtr,
			NotUseAtr shiftBackgroundColorUseAtr,
			NotUseAtr dailyDataDisplayAtr,
			List<OneRowOutputItem> details
			) {
		
		// inv-1
		if ( details.isEmpty() || details.size() > 10 ) {
			throw new BusinessException("Msg_1975");
		}
		
		// inv-2 and inv-4
		List<ScheduleTablePersonalInfoItem> personalInfoList = details.stream()
				.map( row -> additionalColumnUseAtr == NotUseAtr.USE ? 
						Arrays.asList( row.getPersonalInfo(), row.getAdditionalInfo() ) : 
						Arrays.asList( row.getPersonalInfo() ))
				.flatMap( List::stream )
				.flatMap( OptionalUtil::stream )
				.collect( Collectors.toList() );
		
		if ( personalInfoList.isEmpty() ) {
			throw new BusinessException("Msg_2006");
		}	
		
		if ( personalInfoList.size() != new HashSet<>(personalInfoList).size() ) {
			throw new BusinessException("Msg_1972");
		}
		
		// inv-3 and inv-5
		List<ScheduleTableAttendanceItem> attendanceItemList = details.stream()
				.map( row -> row.getAttendanceItem() )
				.flatMap( OptionalUtil::stream ) 
				.collect( Collectors.toList() );
		
		if ( attendanceItemList.isEmpty() ) {
			throw new BusinessException("Msg_2007");
		}
		
		if ( attendanceItemList.size() != new HashSet<>(attendanceItemList).size() ) {
			throw new BusinessException("Msg_1973");
		}
		
		// inv-7
		if ( attendanceItemList.contains(ScheduleTableAttendanceItem.SHIFT) && 
				attendanceItemList.contains(ScheduleTableAttendanceItem.WORK_TIME)) {
			throw new BusinessException("Msg_2209");
		}
		
		return new OutputItem(additionalColumnUseAtr, shiftBackgroundColorUseAtr, dailyDataDisplayAtr, details);
	}
	
	@Override
	public OutputItem clone() {
		
		return new OutputItem(
				this.additionalColumnUseAtr, 
				this.shiftBackgroundColorUseAtr, 
				this.dailyDataDisplayAtr, 
				new ArrayList<>(this.details));
	}
	
	/**
	 * 表示対象の個人情報項目を取得する
	 * @return List<スケジュール表の個人情報項目>
	 */
	public List<ScheduleTablePersonalInfoItem> getDisplayPersonalInfoItems() {

		// $個人情報リスト
		List<ScheduleTablePersonalInfoItem> personalItemList = this.details.stream()
				.map(item -> item.getPersonalInfo())
				.flatMap( OptionalUtil::stream )
				.collect(Collectors.toList());
		
		// $追加列情報リスト
		List<ScheduleTablePersonalInfoItem> additionalItemList = Collections.emptyList();
		if ( this.additionalColumnUseAtr == NotUseAtr.USE) {
			
			additionalItemList = this.details.stream()
									.map(item -> item.getAdditionalInfo())
									.flatMap( OptionalUtil::stream )
									.collect(Collectors.toList());
		}
		
		return Stream.of(personalItemList, additionalItemList)
				.flatMap(x -> x.stream())
				.collect(Collectors.toList());
	}
	
	/**
	 * 表示対象の勤怠項目を取得する
	 * @return List<スケジュール表の勤怠項目>
	 */
	public List<ScheduleTableAttendanceItem> getDisplayAttendanceItems() {
		
		return this.details.stream()
				.map(item -> item.getAttendanceItem())
				.flatMap( OptionalUtil::stream )
				.collect(Collectors.toList());
	}
	
}
