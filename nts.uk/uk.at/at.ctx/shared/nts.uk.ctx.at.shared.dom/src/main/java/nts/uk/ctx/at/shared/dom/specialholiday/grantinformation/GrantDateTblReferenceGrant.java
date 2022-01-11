package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.specialholiday.NextSpecialHolidayGrantParameter;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday.Require;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.SpecialLeaveRestriction;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.LimitCarryoverDays;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantNum;

/**
 * 付与テーブル参照付与
 * @author hayata_maekawa
 *
 */

@AllArgsConstructor
@Getter
@Setter
public class GrantDateTblReferenceGrant {
	/** 期限 */
	private GrantDeadline grantDeadline; 

	
	/**
	 * 次回特別休暇付与を求める
	 * @param require
	 * @param cacheCarrier
	 * @param parameter
	 * @param grantBaseDate
	 * @param specialLeaveRestriction
	 * @return
	 */
	public List<NextSpecialLeaveGrant> getNextSpecialLeaveGrant(
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter,
			GeneralDate grantBaseDate,
			SpecialLeaveRestriction specialLeaveRestriction) {

		Optional<ElapseYear> elapseYearOpt = require.elapseYear(parameter.getCompanyId(), parameter.getSpecialHolidayCode().v());

		if ( !elapseYearOpt.isPresent() ){ // 取得できなかった場合
			return new ArrayList<>();
		}
		
		Optional<GrantDateTbl> grantDateTbl = parameter.getSpecialLeaveBasicInfo().getGrantDateTbl(parameter.getCompanyId(), require);
		
		if(!grantDateTbl.isPresent()){// 取得できなかった場合
			return new ArrayList<>();
		}

		// List＜次回特別休暇付与＞
		List<NextSpecialLeaveGrant> lstOutput = new ArrayList<>();
		//付与回数
		MutableValue<Integer> grantNO = new MutableValue<>(1);
		// 付与日
		GeneralDate grantDate = grantBaseDate;

		// 「期間．終了日」＞=「付与日」の間 ループ
		while (parameter.getPeriod().end().afterOrEquals(grantDate)) {

			//付与日を取得する
			Optional<GeneralDate> grantDateOp = elapseYearOpt.get().getGrantDate(grantBaseDate, grantNO.get());
			
			if(!grantDateOp.isPresent()){
				break;
			}
			
			grantDate = grantDateOp.get();
			
			// 付与日とパラメータ「期間」を比較する

			// 「期間．終了日」＜「付与日」
			if ( parameter.getPeriod().end().before(grantDate)) {
				break;
			}
			
			// 「付与日」＜「期間．開始日」
			else if (  grantDate.before(parameter.getPeriod().start())) {
				
			}
			else if(parameter.getPeriod().contains(grantDate)){

				boolean checkUser = false;
				
				if(!parameter.getEmployeeId().isPresent()){
					checkUser = true;
				}else{
					// 利用条件をチェックする
					checkUser = specialLeaveRestriction.canUseCondition(require, cacheCarrier,
							parameter.getCompanyId(), parameter.getEmployeeId().get(), parameter.getSpecialHolidayCode().v(),
							grantDate);
				}
				if(checkUser) {
					
					//付与日数を取得する
					Optional<GrantedDays> grantDays = grantDateTbl.get().getGrantDays(grantNO.get());
					
					if(!grantDays.isPresent()){
						break;
					}
					
					//　パラメータ「付与日数一覧」を追加する
	
					//　【追加する項目】
					//　・年月日←パラメータ「付与日」
					//　・回数←期間中に付与された回数
					//　・付与日数　←　
					//　【処理中の経過年数が存在する場合】
					//　処理中の「経過年数テーブル．付与回数」に対応する「付与日数」
					//　【処理中の経過年数が存在しない場合】
					//　「テーブル以降付与日数.付与日数」
					NextSpecialLeaveGrant outPut = new NextSpecialLeaveGrant(grantDate,
							new LeaveGrantDayNumber(grantDays.get().v().doubleValue()), 
							new GrantNum(lstOutput.size() + 1),
							this.calcDeadLine(grantDate, Optional.of(grantBaseDate), Optional.of(grantNO.get()),elapseYearOpt));
					
					lstOutput.add(outPut);
				}
			}
			
			grantNO.set(grantNO.get() + 1);
		}

		// ・List＜次回特別休暇付与＞←「List＜次回特別休暇付与＞」

		return lstOutput;
	}
	
	/**
	 * 期限日を求める
	 * @param grantDate
	 * @param grantReferenceDate
	 * @param elapseNo
	 * @param elapseYear
	 * @return
	 */
	public GeneralDate calcDeadLine(GeneralDate grantDate, Optional<GeneralDate> grantReferenceDate,
			Optional<Integer> elapseNo, Optional<ElapseYear> elapseYear) {
		return this.grantDeadline.getDeadLine(grantDate, grantReferenceDate, elapseNo, elapseYear);
	}
	
	/**
	 * 蓄積上限日数を取得する
	 * @return
	 */
	public Optional<LimitCarryoverDays> getLimitCarryoverDays(){
		return this.grantDeadline.getLimitCarryoverDays();
	}
	
}
