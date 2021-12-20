package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday;


import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.PublicHolidayCarryForwardInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.PublicHolidayDigestionInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.PublicHolidayErrors;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;

/**
 * 公休集計期間WORK
 * @author hayata_maekawa
 *
 */
@Getter
@Setter
public class AggregatePublicHolidayWork {

	/**
	 * 年月
	 */
	private YearMonth yearMonth; 
	
	/**
	 * 期間
	 */
	private DatePeriod period;
	
	/**
	 * 公休日数
	 */
	private PublicHolidayMonthSetting publicHolidayMonthSetting;

	
	public AggregatePublicHolidayWork(YearMonth yearMonth,
			DatePeriod period,
			PublicHolidayMonthSetting publicHolidayMonthSetting){
		
		this.yearMonth = yearMonth;
		this.period = period;
		this.publicHolidayMonthSetting = publicHolidayMonthSetting;
	}
	
	/**
	 * 公休消化情報を作成する
	 * @param PublicHolidayCarryForwardData 公休繰越データ
	 * @param tempPublicHolidayManagement List<暫定公休管理データ>
	 * @return PublicHolidayDigestionInformation 公休消化情報
	 */
	public PublicHolidayDigestionInformation createDigestionInformation (
			PublicHolidayCarryForwardData carryForwardData,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement
			){
			
		//公休消化情報を返す
		return new PublicHolidayDigestionInformation(
				new LeaveGrantDayNumber(this.publicHolidayMonthSetting.getInLegalHoliday().v()),
				carryForwardData.getNumberCarriedForward(),	
				sumUsesOfDataDuringTheperiod(tempPublicHolidayManagement));
	}
		
	
	/**
	 * 公休取得数エラーチェック
	 * @param PublicHolidayCarryForwardDataList 公休繰越データ一覧
	 * @param tempPublicHolidayManagement List<暫定公休管理データ>
	 * @return 公休エラー
	 */
	public Optional<PublicHolidayErrors> errorCheck(
			PublicHolidayCarryForwardData carryForwardData,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		//当月残数を取得
		LeaveRemainingDayNumber remainingDay = getRemainingDataOfTheMonth(tempPublicHolidayManagement);
		//当月の残数を相殺する
		LeaveRemainingDayNumber nextCarryForwardDay = carryForwardData.offsetRemainingDataOfTheMonth(remainingDay);
		
		
		if(nextCarryForwardDay.greaterThan(0.0)){
			return Optional.of(new PublicHolidayErrors(true));
		}
		
		return Optional.empty();
	}
	
	/**
	 * 公休繰越情報を作成する
	 * @param companyId
	 * @param employeeId
	 * @param criteriaDate
	 * @param tempPublicHolidayManagement
	 * @param carryForwardData
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	public PublicHolidayCarryForwardInformation calculateCarriedForwardInformation(
			String companyId,
			String employeeId,
			GeneralDate criteriaDate,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement,
			PublicHolidayCarryForwardData carryForwardData,
			CacheCarrier cacheCarrier,
			RequireM1 require){
		
		return new PublicHolidayCarryForwardInformation(
				//繰越データ作成
				createCarryForwardDataForAggregationPeriod(companyId, employeeId,
						criteriaDate,tempPublicHolidayManagement, carryForwardData, cacheCarrier, require).getNumberCarriedForward(),
				//期間終了時点の未消化を求める
				calculateUnused(companyId, employeeId,
						criteriaDate,tempPublicHolidayManagement, carryForwardData, cacheCarrier, require));
	}
	

	
	/**
	 * 当月の繰越データ作成
	 * @param companyId
	 * @param employeeId
	 * @param criteriaDate
	 * @param tempPublicHolidayManagement
	 * @param carryForwardData
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	public PublicHolidayCarryForwardData  createCarryForwardDataForAggregationPeriod(
			String companyId,
			String employeeId,
			GeneralDate criteriaDate,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement,
			PublicHolidayCarryForwardData carryForwardData,
			CacheCarrier cacheCarrier,
			RequireM1 require){
		
		//当月残数
		LeaveRemainingDayNumber remainingData = getRemainingDataOfTheMonth(tempPublicHolidayManagement);
		
		//当月の残数を相殺する
		LeaveRemainingDayNumber nextMonthCarryForwardData = carryForwardData.
				offsetRemainingDataOfTheMonth(remainingData);
		
		//公休設定を取得する
		Optional<PublicHolidaySetting> publicHolidaySetting = require.publicHolidaySetting(companyId);
		
		if(!publicHolidaySetting.isPresent()){
			return new PublicHolidayCarryForwardData(employeeId,
					new LeaveRemainingDayNumber(0.0),
					GrantRemainRegisterType.MONTH_CLOSE);
		}
		
		return publicHolidaySetting.get().createPublicHolidayCarryForwardData(
				employeeId,
				this.period.end(),
				criteriaDate,
				nextMonthCarryForwardData,
				GrantRemainRegisterType.MONTH_CLOSE,
				cacheCarrier,require
				);
	}
	
	/**
	 * 当月残数を取得
	 * @param tempPublicHolidayManagement 暫定データ一覧
	 * @return 当月残数
	 */
	private LeaveRemainingDayNumber getRemainingDataOfTheMonth(
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		return this.publicHolidayMonthSetting.getRemainingDataOfTheMonth(
				new LeaveUsedDayNumber(sumUsesOfDataDuringTheperiod(tempPublicHolidayManagement).v()));
	}
	
	
	/**
	 * 期間終了時点の未消化を求める
	 * @param companyId
	 * @param employeeId
	 * @param criteriaDate
	 * @param tempPublicHolidayManagement
	 * @param carryForwardData
	 * @param cacheCarrier
	 * @param require
	 * @return
	 */
	private LeaveRemainingDayNumber calculateUnused(			
			String companyId,
			String employeeId,
			GeneralDate criteriaDate,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement,
			PublicHolidayCarryForwardData carryForwardData,
			CacheCarrier cacheCarrier,
			RequireM1 require){
		
		//当月残数を取得
		LeaveRemainingDayNumber remainingData = getRemainingDataOfTheMonth(tempPublicHolidayManagement);
		
		
		//公休設定を取得する
		Optional<PublicHolidaySetting> publicHolidaySetting = require.publicHolidaySetting(companyId);
		
		if(!publicHolidaySetting.isPresent()){
			return new LeaveRemainingDayNumber(0.0);
		}
		
		return publicHolidaySetting.get().findUnused(employeeId,
				this.period.end(),
				criteriaDate,
				carryForwardData,
				remainingData,
				cacheCarrier,
				require);
	}
	
	/**
	 * 期間内の使用数の合計を取得
	 * @param tempPublicHolidayManagement 暫定データ一覧
	 * @return 合計使用数
	 */
	private LeaveUsedDayNumber sumUsesOfDataDuringTheperiod(
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		return new LeaveUsedDayNumber(tempPublicHolidayManagement.stream()
				.filter(x -> this.period.contains(x.getYmd()))
				.mapToDouble(x->x.getUseDays().v())
				.sum());		
	}
	
	public static interface RequireM1 extends PublicHolidaySetting.RequireM2{
		//公休設定を取得する（会社ID）
		Optional<PublicHolidaySetting> publicHolidaySetting(String companyId);
	}
}


