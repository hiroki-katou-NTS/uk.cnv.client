package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.gul.util.OptionalUtil;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 出力項目
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.スケジュール表.出力項目
 * @author dan_pv
 *
 */
@Value
public class OutputItem implements DomainValue {

	/**
	 * 	追加列情報の利用区分
	 */
	private NotUseAtr additionalColumnUseAtr;
	
	/**
	 * シフト背景色の表示区分	
	 */
	private NotUseAtr shiftBackgroundColorUseAtr;
	
	/**
	 * 	実績表示区分
	 */
	private NotUseAtr dailyDataDisplayAtr;
	
	/**
	 * 	詳細リスト	
	 */
	private List<OneRowOutputItem> details;
	
	/**
	 * 作る
	 * @param additionalColumnUseAtr
	 * @param shiftBackgroundColorUseAtr
	 * @param dailyDataDisplayAtr
	 * @param details
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
	
}
