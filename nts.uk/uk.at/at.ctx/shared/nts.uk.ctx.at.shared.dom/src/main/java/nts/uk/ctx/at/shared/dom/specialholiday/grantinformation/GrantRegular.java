package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.specialholiday.NextSpecialHolidayGrantParameter;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday.Require;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.SpecialLeaveRestriction;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.LimitCarryoverDays;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantNum;

/**
 * 付与・期限情報
 * @author masaaki_jinno
 *
 */
@Getter
@Setter
public class GrantRegular extends DomainObject {

	/** 付与するタイミングの種類 */
	private TypeTime typeTime;

	/** 	付与基準日 */
	private Optional<GrantDate> grantDate;

	/** 	指定日付与 */
	private Optional<FixGrantDate> fixGrantDate;

	/** 	付与日テーブル参照付与 */
	private Optional<GrantDateTblReferenceGrant> grantPeriodic;

	/** 	期間付与 */
	private Optional<PeriodGrantDate> periodGrantDate;

	@Override
	public void validate() {
		super.validate();
	}
	
	/**
	 * 	inv-1
	 */
	private void invariant(){
		if((this.getTypeTime().equals(TypeTime.REFER_GRANT_DATE_TBL) && this.getGrantPeriodic().isPresent()) ||
				(this.getTypeTime().equals(TypeTime.GRANT_SPECIFY_DATE) && this.getFixGrantDate().isPresent()) ||
				(this.getTypeTime().equals(TypeTime.GRANT_PERIOD) && this.getPeriodGrantDate().isPresent())) {
			return;
		}
		throw new RuntimeException("");
	}

	static public GrantRegular of(
		/** 付与するタイミングの種類 */
		TypeTime typeTime
		/** 付与基準日 */
		, Optional<GrantDate> grantDate
		/** 指定日付与 */
		, Optional<FixGrantDate> fixGrantDate
		/** 付与日テーブル参照付与 */
		, Optional<GrantDateTblReferenceGrant> grantPeriodic
		/** 期間付与 */
		, Optional<PeriodGrantDate> periodGrantDate
	){
		GrantRegular c = new GrantRegular();
		/** 付与するタイミングの種類 */
		c.typeTime=typeTime;
		/** 付与基準日 */
		c.grantDate=grantDate;
		/** 指定日付与 */
		c.fixGrantDate=fixGrantDate;
		/** 付与日テーブル参照付与 */
		c.grantPeriodic=grantPeriodic;
		/** 期間付与 */
		c.periodGrantDate=periodGrantDate;
        c.invariant();
		return c;
	}
	
	/**
	 * 次回特別休暇付与を求める
	 * @param require
	 * @param cacheCarrier
	 * @param parameter
	 * @param specialLeaveRestriction
	 * @return
	 */
	public List<NextSpecialLeaveGrant> getNextSpecialLeaveGrant(
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter,
			SpecialLeaveRestriction specialLeaveRestriction){
		
		List<NextSpecialLeaveGrant> nextSpecialLeaveGrantList
		= new ArrayList<NextSpecialLeaveGrant>();
		
		//期間を補正する
		parameter.setPeriod(this.correctThePeriod(require, cacheCarrier, parameter));
		
		// 取得している「特別休暇．付与情報．付与するタイミングの種類」をチェックする
		if (this.getTypeTime().equals(TypeTime.GRANT_SPECIFY_DATE)){ // 指定日に付与する

			// 定期の次回特別休暇付与を求める
			nextSpecialLeaveGrantList
				= getSpecialLeaveGrantInfo(
					require,
					cacheCarrier,
					parameter,
					specialLeaveRestriction);
			

		} else if (this.getTypeTime().equals(TypeTime.GRANT_PERIOD)){ // 期間で付与する

			// 定期の次回特別休暇付与を求める
			nextSpecialLeaveGrantList
				= getSpecialLeaveGrantInfo(
					require,
					cacheCarrier,
					parameter,
					specialLeaveRestriction);
			

		} else if (this.getTypeTime().equals(TypeTime.REFER_GRANT_DATE_TBL)){ // 付与テーブルを参照して付与する

			// 付与テーブルの付与日一覧を求める
			nextSpecialLeaveGrantList
				= getTableSpecialLeaveGrantInfo(
					require,
					cacheCarrier,
					parameter,
					specialLeaveRestriction
					);
		}
		return nextSpecialLeaveGrantList;
	}
	
	
	/**
	 * 定期の次回特別休暇付与を求める
	 * @param require
	 * @param cacheCarrier
	 * @param parameter
	 * @param specialLeaveRestriction
	 * @return
	 */
	private List<NextSpecialLeaveGrant> getSpecialLeaveGrantInfo(
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter,
			SpecialLeaveRestriction specialLeaveRestriction) {

		//付与基準日を求める
		Optional<GeneralDate> grantDateOpt = getgrantDate(require, cacheCarrier, parameter);
		
		// 付与基準日がOptional.empty()かどうかチェックする
		if ( !grantDateOpt.isPresent()){ 
			// 「List＜次回特別休暇付与＞」を空で作成
			return new ArrayList<>();
		}

		// 「List＜次回特別休暇付与＞」を作成
		List<NextSpecialLeaveGrant> nextSpecialLeaveGrantList = new ArrayList<NextSpecialLeaveGrant>();

		// パラメータ「付与日」←パラメータ「付与基準日」
		GeneralDate grantDate = GeneralDate.localDate(grantDateOpt.get().localDate());

		// ループ
		while (true){

			//　パラメータ「付与日」とパラメータ「期間」を比較する

			// 「期間．終了日」＜「付与日」
			if (parameter.getPeriod().end().before(grantDate)){
				return nextSpecialLeaveGrantList;
			}
			// 「付与日」＜「期間．開始日」
			else if ( grantDate.before(parameter.getPeriod().start()) ){
				//判定処理の後付与日の更新のみ行う
			}
			// 「期間．開始日」≦「付与日」≦「期間．終了日」
			else if ( parameter.getPeriod().contains(grantDate)){

				boolean UseCondition = false;
				
				if(!parameter.getEmployeeId().isPresent()){
					UseCondition = true;
				}else{
				// 利用条件をチェックする
					UseCondition = specialLeaveRestriction.checkUseCondition(
						require, cacheCarrier, parameter.getCompanyId(), 
						parameter.getEmployeeId().get(), parameter.getSpecialHolidayCode().v(), grantDate);
				}
					
				if(UseCondition){
					LeaveGrantDayNumber grantDays = this.getGrantDayOfDesignationOrPeriod();


					grantDays = parameter.getSpecialLeaveBasicInfo().getGrantDay(grantDays);
					
					// 年月日←パラメータ「付与日」
					// 回数←期間中に付与された回数
					NextSpecialLeaveGrant nextSpecialLeaveGrant = new NextSpecialLeaveGrant(grantDate, grantDays,
							new GrantNum(nextSpecialLeaveGrantList.size() + 1),
							this.getDeadline(grantDate, Optional.empty(), Optional.empty(), Optional.empty()));

					// リストに追加
					nextSpecialLeaveGrantList.add(nextSpecialLeaveGrant);
				}
			}
			// パラメータ「付与日」←パラメータ「付与日」＋1年
			grantDate = grantDate.addYears(1);
		}
	}
	
	
	
	/**
	 * 期限日を求める
	 * @param grantDate
	 * @param nextGrantDate
	 * @return
	 */
	private GeneralDate getDeadline(GeneralDate grantDate, Optional<GeneralDate> grantReferenceDate,
			Optional<Integer> elapseNo, Optional<ElapseYear> elapseYear)  {
		switch(this.typeTime) {
		case GRANT_SPECIFY_DATE:/** 指定日付与 */
			if (this.getFixGrantDate().isPresent()){
				return this.getFixGrantDate().get().getDeadLine(grantDate, grantReferenceDate, elapseNo, elapseYear);
			}
			break;
		case GRANT_PERIOD: /**期間で付与する*/
			if(this.getPeriodGrantDate().isPresent()){
				return this.periodGrantDate.get().calcDeadLine(grantDate);
			}
			break;
		case REFER_GRANT_DATE_TBL: /** 付与テーブルを参照して付与する*/
			if(this.getGrantPeriodic().isPresent()){
				return this.getGrantPeriodic().get().calcDeadLine(grantDate, grantReferenceDate, elapseNo, elapseYear);
			}
			break;
		default:
			throw new RuntimeException();
		}
		throw new RuntimeException();
	}
	
	/**
	 * 付与基準日を求める
	 * @param require
	 * @param cacheCarrier
	 * @param parameter
	 * @return
	 */
	private Optional<GeneralDate> getgrantDate(			
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter){
		
		if (typeTime.equals(TypeTime.GRANT_SPECIFY_DATE)){ // 付与するタイミングの種類 = 指定日で付与する
			if(this.fixGrantDate.isPresent()){
				return this.fixGrantDate.get().getgrantDate(require, cacheCarrier, parameter, this.getGrantDate());
			}
			
		}else if(typeTime.equals(TypeTime.GRANT_PERIOD)){//付与するタイミングの種類 = 期間で付与する
			if(this.getPeriodGrantDate().isPresent()){
				return this.getPeriodGrantDate().get().getPeriodSpecialLeaveGrantInfo(require, cacheCarrier, parameter);
			}
			
		}else  if (typeTime.equals(TypeTime.REFER_GRANT_DATE_TBL)){//付与するタイミングの種類 = 付与テーブルを参照して付与する
			if(this.getGrantDate().isPresent()){
				return  this.getGrantDate().get().getSpecialLeaveGrantDate(
						require, cacheCarrier, parameter);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * 指定日付与か期間付与の付与日数を求める
	 * @return
	 */
	private LeaveGrantDayNumber getGrantDayOfDesignationOrPeriod(){
		
		//[SystemError]	
		this.invariant();
		if(this.typeTime.equals(TypeTime.REFER_GRANT_DATE_TBL)){
			throw new RuntimeException("");
		}
		
		if(this.typeTime.equals(TypeTime.GRANT_SPECIFY_DATE)){
			return new LeaveGrantDayNumber(this.getFixGrantDate().get().getGrantDays().getGrantDays().v().doubleValue());
		}
		
		if(typeTime.equals(TypeTime.GRANT_PERIOD)){
			return new LeaveGrantDayNumber(this.getPeriodGrantDate().get().getGrantDays().getGrantDays().v().doubleValue());
		}	
		
		throw new RuntimeException("");
	}
	
	/**
	 * テーブルに基づいた次回特別休暇付与を求める
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param int spLeaveCD 特別休暇コード
	 * @param callFrom 呼び出し元（指定日付与または期間付与）
	 * @param period 期間
	 * @param grantDate 付与日
	 * @param
	 * @param specialHoliday 特別休暇
	 * @return 次回特休付与リスト
	 */
	private List<NextSpecialLeaveGrant> getTableSpecialLeaveGrantInfo(
			Require require,
			CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter,
			SpecialLeaveRestriction specialLeaveRestriction) {

		
		if(!this.getGrantDate().isPresent() || !this.getGrantPeriodic().isPresent()){
			return new ArrayList<>();
		}
		// 付与基準日を求める
		Optional<GeneralDate> grantDate = this.getGrantDate().get().getSpecialLeaveGrantDate(
				require, cacheCarrier, parameter);
		
		if ( !grantDate.isPresent() ){
			return new ArrayList<>();
		}

		// 次回特別休暇付与を求める
		return this.getGrantPeriodic().get().askGrantdaysFromtable(
					require,
					cacheCarrier,
					parameter,
					grantDate.get(),
					specialLeaveRestriction
					);
	}


	
	/**
	 * 期間を補正する
	 * @param require
	 * @param cacheCarrier
	 * @param parameter
	 * @return
	 */
	public DatePeriod correctThePeriod(Require require, CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter){
		

		if(this.decideShifiPerriod(require, cacheCarrier, parameter)){
			return this.createCorrectedPeriod(parameter.getPeriod(), 1);
		}else{
			return this.createCorrectedPeriod(parameter.getPeriod(), 0);
		}
		
		
	}
	
	/**
	 * 期間をずらすか判断をする
	 * @param require
	 * @param employeeId
	 * @param period
	 * @return
	 */
	private boolean decideShifiPerriod(Require require, CacheCarrier cacheCarrier,
			NextSpecialHolidayGrantParameter parameter){
		
		if (!this.getTypeTime().equals(TypeTime.GRANT_PERIOD)){ // 期間で付与する以外
			return true;
		}

		Optional<GeneralDate> empEnrollPeriod = parameter.getEntryDate(require, cacheCarrier);
		
		if(!empEnrollPeriod.isPresent()){
			return true;
		}
		
		if ( empEnrollPeriod.get().equals(parameter.getPeriod().start()) ) {
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 補正後期間を作成する
	 * @param period
	 * @param addStart
	 * @return
	 */
	private DatePeriod createCorrectedPeriod(DatePeriod period, int addStart){
		//期間終了日が日付最大値以下判断
		int addEnd = 0;
		if (period.end().before(GeneralDate.max())){
			addEnd = 1;
		}
		
		return new DatePeriod(period.start().addDays(addStart), period.end().addDays(addEnd));
	}
	
	/**
	 * 蓄積上限日数を取得する
	 * @return
	 */
	public Optional<LimitCarryoverDays> getLimitCarryoverDays(){
		if(this.getTypeTime().equals(TypeTime.GRANT_SPECIFY_DATE)){
			if(this.fixGrantDate.isPresent()){
				return this.fixGrantDate.get().getLimitCarryoverDays();
			}
		}else if(this.getTypeTime().equals(TypeTime.REFER_GRANT_DATE_TBL)){
			if(grantPeriodic.isPresent()){
				return this.grantPeriodic.get().getLimitCarryoverDays();
			}
		}
		return Optional.empty();
	}
}
