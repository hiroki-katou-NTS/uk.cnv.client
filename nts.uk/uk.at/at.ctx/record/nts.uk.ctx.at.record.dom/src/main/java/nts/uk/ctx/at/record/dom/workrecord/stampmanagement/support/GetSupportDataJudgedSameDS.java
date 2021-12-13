/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect.StartAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * DomainService: 同一と判断された応援データを取得する
 * path: UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.打刻管理.応援.同一と判断された応援データを取得する
 * @author laitv
 */
public class GetSupportDataJudgedSameDS {

	/**
	 * [1] 同一と判断された応援データを取得する
	 * @param required Required
	 * @param supportDataList   応援データ一覧
	 * @param targetSupportData 対象応援データ	
	 * @param isStart 開始区分
	 */
	public static Optional<OuenWorkTimeSheetOfDailyAttendance> getSupportDataJudgedSame(Required required, 
			List<OuenWorkTimeSheetOfDailyAttendance> supportDataList, TimeWithDayAttr targetSupportTime, StartAtr isStart) {
		
		// 「応援の同一打刻の判断基準」を取得する		
		JudgmentCriteriaSameStampOfSupport jcSameStampOfSupport = required.getCriteriaSameStampOfSupport();
		
		if(jcSameStampOfSupport == null)
			return Optional.empty();
		
		// 応援データ一覧をループする
		for (OuenWorkTimeSheetOfDailyAttendance supportData : supportDataList) {
			
			val standardStampStart = supportData.getTimeSheet().getStart().flatMap(c -> c.getTimeWithDay()).orElse(null);
			
			// 開始区分を確認する
			if (isStart == StartAtr.START_OF_SUPPORT && standardStampStart != null) {
				// 同一と認識すべきの打刻か
				if (jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampStart, targetSupportTime)) {
					
					// 処理中の応援データを返す
					return Optional.ofNullable(supportData);
				}
			} 
			
			val standardStampEnd = supportData.getTimeSheet().getEnd().flatMap(c -> c.getTimeWithDay()).orElse(null);
			
			if (isStart == StartAtr.END_OF_SUPPORT && standardStampEnd != null) {
				// 同一と認識すべきの打刻か
				if (jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampEnd, targetSupportTime)) {

					// 処理中の応援データを返す
					return Optional.ofNullable(supportData);
				}
			}
		}
		
		// Emptyを返す
		return Optional.empty();
	}
	
	public static interface Required {
		/**
		 * [R-1] 応援の同一打刻の判断基準を取得する	
		 * @param companyId
		 * get từ JudCriteriaSameStampOfSupportRepo ra nhá.
		 */
		JudgmentCriteriaSameStampOfSupport getCriteriaSameStampOfSupport();
	}
}
