package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * @author thanhpv
 * @name 工数実績参照設定
 * @part UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績参照設定.工数実績参照設定
 */
@Getter
@AllArgsConstructor
public class ManHourRecordReferenceSetting extends AggregateRoot {
	
	/** 経過月数 */
	private ElapsedMonths elapsedMonths;
	
	/** 参照範囲 */
	private ReferenceRange referenceRange;
	
//■Public
	/**
	 * @name [1] 作業修正可能開始日付を取得する
	 * @Decription 	説明:指定社員の締め開始日から作業修正可能期間の開始日付を求める
	 * @input require
	 * @input 社員ID
	 * @output 修正可能開始日付 GeneralDate
	 */
	public GeneralDate getWorkCorrectionStartDate(Require require, String employeeId) {
		//$期間 = require.締め期間を取得する(社員ID, 年月日#今日())
		DatePeriod period = require.getPeriod(employeeId, GeneralDate.today());
		switch (this.elapsedMonths) {
			case ONE_MONTH_AGO:
				return period.start().addMonths(-1);
			case TOW_MONTH_AGO:
				return period.start().addMonths(-2);
			case THREE_MONTH_AGO:
				return period.start().addMonths(-3);
			case FOUR_MONTH_AGO:
				return period.start().addMonths(-4);
			case FIVE_MONTH_AGO:
				return period.start().addMonths(-5);
			case SIX_MONTH_AGO:
				return period.start().addMonths(-6);
			default:
				return period.start();
		}
	}
	
	/**
	 * @name [2] 参照可能範囲を取得する
	 * @Decription 	説明:工数実績にて参照可能な職場と社員を取得する	
	 * @input require
	 * @input cId	会社ID
	 * @input userId	ユーザID
	 * @input employeeId	社員ID
	 * @input baseDate	年月日
	 * @output 参照可能範囲	Map<社員ID,職場ID>
	 */
	public Map<String, String> getWorkCorrectionStartDate(Require require, String companyId, String userId, String employeeId, GeneralDate baseDate) {
		//if @参照範囲 == ロールの社員参照範囲と同じ																				
		if(this.referenceRange == ReferenceRange.SAME_AS_EMPLOYEE_REFERENCE_RANGE_OF_ROLE)	
			//return require.参照可能社員を取得する(ユーザID,社員ID,基準日)															
			return require.getWorkPlace(userId, employeeId, baseDate);
		//return $参照可能範囲 = require.全社員を取得する(会社ID,基準日)
		return require.getByCID(companyId, baseDate);
	}
	
//■Require
	public static interface Require {
		//[R-1] 作業変更可能期間設定を取得する
		//作業変更可能期間設定Repository.get(会社ID)	
		ManHourRecordReferenceSetting get();
		
		//[R-2] 締め期間を取得する
		//アルゴリズム.社員に対応する締め期間を取得する(社員ID,基準日)
		DatePeriod getPeriod(String employeeId, GeneralDate date);
		
		//[R-3] 参照可能社員を取得する							
		//参照可能社員の所属職場を取得するAdapter.取得する(ユーザID,社員ID,基準日)		
		Map<String, String> getWorkPlace(String userID, String employeeID, GeneralDate date);
		
		//[R-4] 全社員を取得する							
		//全ての職場の所属社員を取得するAdapter.取得する(会社ID,基準日)		
		Map<String, String> getByCID(String companyId, GeneralDate baseDate);
	}
}
