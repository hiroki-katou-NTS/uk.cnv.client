/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.MonthlyNumberOfDays;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.AggregatePublicHolidayWork;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery.RequireM7;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureStartEndOutput;
import nts.uk.ctx.at.shared.dom.workrule.closure.GetClosurePeriodBySpecifyingTheYeatMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 
 * @author quytb
 * 公休設定
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.公休管理.設定.公休設定
 * 
 */
@Getter
@Setter
@AllArgsConstructor
public class PublicHolidaySetting extends AggregateRoot {	
	/** 会社ID*/
	private String companyID;
	
	/** 公休を管理する */	
	private int isManagePublicHoliday;
	
	/** 公休管理期間  */
	private PublicHolidayPeriod publicHolidayPeriod;
	
	/** 公休繰越期限 */
	private PublicHolidayCarryOverDeadline publicHolidayCarryOverDeadline;
	
	/** 公休日数がマイナス時に繰越する */
	private int carryOverNumberOfPublicHolidayIsNegative;
	
	
	/**
	 * 集計期間WORKを作成
	 * 
	 * @param employeeId
	 * @param yearMonth
	 * @param criteriaDate
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	public List<AggregatePublicHolidayWork> createAggregatePeriodWork(
			String employeeId,List<YearMonth> yearMonth,GeneralDate criteriaDate, 
			CacheCarrier cacheCarrier, RequireM1 require){
		
		//集計期間を作成する
		List<AggregationPeriod> periodList = createPeriod(employeeId, yearMonth, criteriaDate, cacheCarrier, require);
		
		//集計期間List毎の日数設定を取得
		List<AggregatePublicHolidayWork> aggregatePublicHolidayWork = 
				createAggregatePublicHolidayWork(employeeId, periodList, 
						criteriaDate, cacheCarrier,require);
		
		return aggregatePublicHolidayWork;
	}
	
	
	/**
	 * 繰越期限を計算する
	 * @param int 期首月
	 * @param endDay 当月の最終日
	 * @return GeneralDate 期限日
	 */
	public GeneralDate createDeadline(RequireM2 require, 
			CacheCarrier cacheCarrier,
			String employeeId, 
			GeneralDate endDay,
			GeneralDate criteriaDate){
		
		//繰越期限を確認
		switch(this.publicHolidayCarryOverDeadline){
			//当月
			case CURRENT_MONTH:
				return  endDay;
				
			//無期限
			case INDEFINITE:
				return GeneralDate.ymd(9999, 12, 31);
				
			//年度末
			case YEAR_END:
				return createYearEndDeadline(require, cacheCarrier,employeeId, endDay, criteriaDate);
						
			default:
				throw new RuntimeException();
		}
		
	}
	
	/**
	 * 集計期間を作成する
	 * @param employeeId
	 * @param yearMonths
	 * @param criteriaDate
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	public List<AggregationPeriod> createPeriod(String employeeId,
			List<YearMonth> yearMonths,
			GeneralDate criteriaDate, 
			CacheCarrier cacheCarrier,
			RequireM1 require){
		
		//INPUT．Require締め日を取得
		Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, this.companyID, criteriaDate);
		
		//締めが取得できたか
		if(closure == null){
			return new ArrayList<>();
		}
		
		//公休管理期間設定を確認
		if(this.publicHolidayPeriod == PublicHolidayPeriod.CLOSURE_PERIOD){
			List<AggregationPeriod> periodList = new ArrayList<>();
			//管理期間 = 締め期間
			//締め開始日と締め日を取得する
			Optional<ClosureStartEndOutput> startDayAndClosureDay = closure.getClosureStartDayAndClosureDay();
			
			if(!startDayAndClosureDay.isPresent()){
				return new ArrayList<>();
			}
			
			//年月の件数ループ
			for(val yearMonth : yearMonths){
				
				//年月＋締め開始日、年月＋締め日の期間を作成
				DatePeriod period = new DatePeriod(
						GeneralDate.ymd(yearMonth,startDayAndClosureDay.get().getStart().v().intValue()),
						GeneralDate.ymd(yearMonth,startDayAndClosureDay.get().getClosure().v().intValue()));
				
				//List<集計期間>に追加
				periodList.add(new AggregationPeriod(yearMonth, period));
				
			}
			return periodList;
		}else{
			List<AggregationPeriod> periodList = new ArrayList<>();
			//管理期間 = 1日から末日
			Optional<ClosureDate> closureDate = closure.getClosureDateOfCurrentMonth();
			if(closureDate.isPresent()){
			//締め日が末日か
				if(closureDate.get().getLastDayOfMonth() == true){
					//年月の月を1か月前にする
					yearMonths = yearMonths.stream().map(c->c.previousMonth()).collect(Collectors.toList());
					
				}
			}
			//年月の件数ループ
			for(val yearMonth : yearMonths){
				//年月＋1日、年月＋年月の末日で期間を作成
				DatePeriod period = DatePeriod.daysFirstToLastIn(yearMonth);
				
				//List<集計期間>に追加
				periodList.add(new AggregationPeriod(yearMonth, period));
			}
			return periodList;
		}
		
		
	}
	

	/**
	 * 繰越データを作成する
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param ymd 期限日
	 * @param numberCarriedForward 繰越数
	 * @param grantRemainRegisterType 登録種別
	 * @return Optional<公休繰越データ>
	 */
	public Optional<PublicHolidayCarryForwardData> createPublicHolidayCarryForwardData(
			String employeeId,
			YearMonth yearMonth,
			GeneralDate ymd,
			LeaveRemainingDayNumber numberCarriedForward,
			GrantRemainRegisterType grantRemainRegisterType
			){
		
		if((iscarryOverNumberOfPublicHoliday() && numberCarriedForward.lessThan(0.0)) || numberCarriedForward.greaterThan(0.0)){
			return Optional.of(new PublicHolidayCarryForwardData(
					employeeId,
					yearMonth,
					ymd,
					numberCarriedForward,
					grantRemainRegisterType));
		}
		return Optional.empty();
	}
	
	
	/**
	 * 年度末の期限日を求める
	 * @param require
	 * @param cacheCarrier
	 * @param employeeId
	 * @param endDay
	 * @param criteriaDate
	 * @return GeneralDate
	 */
	private GeneralDate createYearEndDeadline(RequireM2 require,
			CacheCarrier cacheCarrier,
			String employeeId, 
			GeneralDate endDay,
			GeneralDate criteriaDate){
		
		//暦の年月から年月期間を取得する
		YearMonthPeriod period = require.getYearMonthPeriod(this.companyID, endDay.yearMonth());
		
		//公休管理期間 = １日～末日
		if(this.publicHolidayPeriod == PublicHolidayPeriod.FIRST_DAY_TO_LAST_DAY){
			return period.end().lastGeneralDate();
		}

		//公休管理期間 = 締め期間
		if(this.publicHolidayPeriod == PublicHolidayPeriod.CLOSURE_PERIOD){

			//取得する
			Optional<DatePeriod> datePeriod = GetClosurePeriodBySpecifyingTheYeatMonth.getPeriod(
					require, employeeId, criteriaDate, period.end(), cacheCarrier);
			
			
			if(!datePeriod.isPresent()){
				return period.end().lastGeneralDate();
			}
			
			//取得した年月日を期限日として返す
			return datePeriod.get().end();
		}
		
		throw new RuntimeException();
	}
	
	
	/**
	 * 集計期間WORK作成
	 * @param employeeId
	 * @param periodList
	 * @param yearMonths
	 * @param criteriaDate
	 * @param cacheCarrier
	 * @param require
	 * @return 公休集計期間WORK
	 */
	private List<AggregatePublicHolidayWork> createAggregatePublicHolidayWork(String employeeId, 
			List<AggregationPeriod> periodList, GeneralDate criteriaDate, CacheCarrier cacheCarrier,RequireM2 require){
		
		//公休日数取得
		PublicHolidayManagementUsageUnit publicHolidayManagementUsageUnit =
				require.publicHolidayManagementUsageUnit(this.companyID);
		
		List<PublicHolidayMonthSetting> publicHolidayMonthSettings =
				publicHolidayManagementUsageUnit.GetNumberofPublicHoliday(require, this.companyID, employeeId,periodList , criteriaDate);
		
		
		List<AggregatePublicHolidayWork> aggregatePublicHolidayWork =  new ArrayList<>();
		//公休集計期間WORKを作成
		for(AggregationPeriod period : periodList){
			
			//月間公休日数設定を取得
			PublicHolidayMonthSetting publicHolidayMonthSetting = publicHolidayMonthSettings.stream()
					.filter(x -> x.getPublicHdManagementYear().v() == period.getYearMonth().year()&&
							x.getMonth() == period.getYearMonth().month())
					.findFirst()
					.orElse(
							new PublicHolidayMonthSetting(
									new Year(period.getYearMonth().year()), 
									new Integer(period.getYearMonth().month()), 
									new MonthlyNumberOfDays(0.0)));
			
			aggregatePublicHolidayWork.add(new AggregatePublicHolidayWork(
					period.getYearMonth(),
					period.getPeriod(),
					publicHolidayMonthSetting,
					//期限日作成
					createDeadline(require, cacheCarrier, employeeId ,period.getPeriod().end(), criteriaDate)
					));
		}
		
		
		return aggregatePublicHolidayWork;
	}
	

	//公休日数がマイナス時に繰越するがtrueか
	public boolean iscarryOverNumberOfPublicHoliday(){
		return this.carryOverNumberOfPublicHolidayIsNegative == 1;
	}
	
	
	//締め
	public static interface RequireM1 extends ClosureService.RequireM3, RequireM2{
	}

	public static interface RequireM2 extends ClosureService.RequireM3, RequireM7, PublicHolidayManagementUsageUnit.RequireM1 {
		//暦の年月から年月期間を取得する
		YearMonthPeriod getYearMonthPeriod(String cid, YearMonth yearMonth);
		
		//公休利用単位設定
		PublicHolidayManagementUsageUnit publicHolidayManagementUsageUnit(String companyId);
	}
}
