/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support;

import java.util.List;
import java.util.Optional;

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
	public static Optional<OuenWorkTimeSheetOfDailyAttendance> getSupportDataJudgedSame(
			Required required, 
			List<OuenWorkTimeSheetOfDailyAttendance> supportDataList,
			OuenWorkTimeSheetOfDailyAttendance targetSupportData,
			boolean isStart) {
		
		// 「応援の同一打刻の判断基準」を取得する		
		JudgmentCriteriaSameStampOfSupport jcSameStampOfSupport = required.getCriteriaSameStampOfSupport();
		
		if(jcSameStampOfSupport == null)
			return Optional.empty();
		
		OuenWorkTimeSheetOfDailyAttendance resultCheck = null;
		
		// 応援データ一覧をループする
		for (OuenWorkTimeSheetOfDailyAttendance supportData : supportDataList) {
			// 開始区分を確認する
			if (isStart && supportData.getTimeSheet().getStart().isPresent() && supportData.getTimeSheet().getStart().get().getTimeWithDay().isPresent()) {
				// 同一と認識すべきの打刻か
				TimeWithDayAttr standardStampStart = supportData.getTimeSheet().getStart().get().getTimeWithDay().get();
				TimeWithDayAttr targetStampStart = targetSupportData.getTimeSheet().getStart().get().getTimeWithDay().get();
				boolean check = jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampStart, targetStampStart);
				if (check) {
					resultCheck = supportData;
					break;
				}
			} 
			if (!isStart && supportData.getTimeSheet().getEnd().isPresent() && supportData.getTimeSheet().getEnd().get().getTimeWithDay().isPresent()) {
				// 同一と認識すべきの打刻か
				TimeWithDayAttr standardStampEnd = supportData.getTimeSheet().getEnd().get().getTimeWithDay().get();
				TimeWithDayAttr targetStampEnd = targetSupportData.getTimeSheet().getEnd().get().getTimeWithDay().get();
				boolean check = jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampEnd, targetStampEnd);
				if (check) {
					resultCheck = supportData;
					break;
				}
			}
		}
		
		if(resultCheck != null){
			// 処理中の応援データを返す
			return Optional.of(resultCheck);
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
