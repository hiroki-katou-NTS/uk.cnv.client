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
		Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, employeeId, criteriaDate);
		
		//締めが取得できたか
		if(closure == null){
			return new ArrayList<>();
		}
		
		//公休管理期間設定を確認
		if(this.publicHolidayPeriod == PublicHolidayPeriod.CLOSURE_PERIOD){
			List<AggregationPeriod> periodList = new ArrayList<>();
			//管理期間 = 締め期間

			
			//当月の締め日を取得する
			Optional<ClosureDate> closureDate = closure.getClosureDateOfCurrentMonth();
			
			if(!closureDate.isPresent()){
				return new ArrayList<>();
			}
			
			//年月の件数ループ
			for(val yearMonth : yearMonths){
				
				//締め開始日と締め日を取得する
				Optional<ClosureStartEndOutput> startDayAndClosureDay = closure.getClosureStartDayAndClosureDay(yearMonth);
				
				if(!startDayAndClosureDay.isPresent()){
					return new ArrayList<>();
				}
				
				
				//年月＋締め開始日、年月＋締め日の期間を作成
				DatePeriod period = new DatePeriod(
						GeneralDate.ymd(closureDate.get().getLastDayOfMonth() ? yearMonth:yearMonth.previousMonth(),
								startDayAndClosureDay.get().getStart().v().intValue()),
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
				if(closureDate.get().getLastDayOfMonth() == false){
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
	 * [5] 繰越データを作成する
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param ymd 期限日
	 * @param numberCarriedForward 繰越数
	 * @param grantRemainRegisterType 登録種別
	 * @return 公休繰越データ
	 */
	public PublicHolidayCarryForwardData createPublicHolidayCarryForwardData(
			String employeeId,
			GeneralDate endDay,
			GeneralDate criteriaDate,
			LeaveRemainingDayNumber numberCarriedForward,
			GrantRemainRegisterType grantRemainRegisterType,
			CacheCarrier cacheCarrier,
			RequireM2 require
			){
		
		if(this.isCarryOver(employeeId, endDay, criteriaDate, numberCarriedForward, cacheCarrier, require)){
			return new PublicHolidayCarryForwardData(
					employeeId,
					numberCarriedForward,
					grantRemainRegisterType);
		}
		return new PublicHolidayCarryForwardData(employeeId,
				new LeaveRemainingDayNumber(0.0),
				grantRemainRegisterType);
	}
	
	
	/**
	 * [6] 未消化数を求める
	 * @param employeeId
	 * @param endDay
	 * @param criteriaDate
	 * @param carryForwardData
	 * @param numberCarriedForward
	 * @param grantRemainRegisterType
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	public LeaveRemainingDayNumber findUnused(
			String employeeId,
			GeneralDate endDay,
			GeneralDate criteriaDate,
			PublicHolidayCarryForwardData carryForwardData,
			LeaveRemainingDayNumber numberCarriedForward,
			CacheCarrier cacheCarrier,
			RequireM2 require
			){
		
		LeaveRemainingDayNumber offsetsNumber = carryForwardData.offsetRemainingDataOfTheMonth(numberCarriedForward);
		if(this.isCarryOver(employeeId, endDay, criteriaDate, offsetsNumber, cacheCarrier, require)){
			return new LeaveRemainingDayNumber(0.0);
		}
		
		if(offsetsNumber.v() < 0){
			return new LeaveRemainingDayNumber(0.0);
		}
		
		return offsetsNumber;
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
		Optional<YearMonthPeriod> period = require.getYearMonthPeriodByCalendarYearmonth(this.companyID, endDay.yearMonth());
		
		if(!period.isPresent()){
			throw new RuntimeException();
		}
		
		//公休管理期間 = １日～末日
		if(this.publicHolidayPeriod == PublicHolidayPeriod.FIRST_DAY_TO_LAST_DAY){
			return period.get().end().lastGeneralDate();
		}

		//公休管理期間 = 締め期間
		if(this.publicHolidayPeriod == PublicHolidayPeriod.CLOSURE_PERIOD){

			//取得する
			Optional<DatePeriod> datePeriod = GetClosurePeriodBySpecifyingTheYeatMonth.getPeriod(
					require, employeeId, criteriaDate, period.get().end(), cacheCarrier);
			
			
			if(!datePeriod.isPresent()){
				return period.get().end().lastGeneralDate();
			}
			
			//取得した期間.終了日を期限日として返す
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
		
		List<AggregatePublicHolidayWork> aggregatePublicHolidayWork =  new ArrayList<>();
		
		//公休日数取得
		Optional<PublicHolidayManagementUsageUnit> publicHolidayManagementUsageUnit =
				require.publicHolidayManagementUsageUnit(this.companyID);
		
		if(!publicHolidayManagementUsageUnit.isPresent()){
			return aggregatePublicHolidayWork;
		}
		
		List<PublicHolidayMonthSetting> publicHolidayMonthSettings =
				publicHolidayManagementUsageUnit.get().GetNumberofPublicHoliday(
						require, this.companyID, employeeId,periodList , criteriaDate);
		
		
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
					publicHolidayMonthSetting
					));
		}
		
		
		return aggregatePublicHolidayWork;
	}
	
	
	/**
	 * [pvt-3] 繰越するか判断
	 * @param employeeId
	 * @param endDay
	 * @param criteriaDate
	 * @param numberCarriedForward
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	private boolean isCarryOver(String employeeId,
			GeneralDate endDay,
			GeneralDate criteriaDate,
			LeaveRemainingDayNumber numberCarriedForward,
			CacheCarrier cacheCarrier,
			RequireM2 require){
		
		if((iscarryOverNumberOfPublicHoliday() && numberCarriedForward.lessThan(0.0)) || numberCarriedForward.greaterThan(0.0)){
			if(createDeadline(require, cacheCarrier, employeeId, endDay,criteriaDate).after(endDay) ){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	//公休管理するか
	public boolean isManagePublicHoliday(){
		return this.isManagePublicHoliday == 1;
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
		Optional<YearMonthPeriod> getYearMonthPeriodByCalendarYearmonth(String cid, YearMonth yearMonth);
		
		//公休利用単位設定
		Optional<PublicHolidayManagementUsageUnit> publicHolidayManagementUsageUnit(String companyId);
	}
}
