package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday;


import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.PublicHolidayMonthSetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataList;
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
	/**
	 * 期限日
	 */
	private GeneralDate ymd;

	
	public AggregatePublicHolidayWork(YearMonth yearMonth,
			DatePeriod period,
			PublicHolidayMonthSetting publicHolidayMonthSetting,
			GeneralDate ymd){
		
		this.yearMonth = yearMonth;
		this.period = period;
		this.publicHolidayMonthSetting = publicHolidayMonthSetting;
		this.ymd = ymd;
	}
	
	/**
	 * 公休消化情報を作成する
	 * @param PublicHolidayCarryForwardDataList 公休繰越データ一覧
	 * @param tempPublicHolidayManagement List<暫定公休管理データ>
	 * @return PublicHolidayDigestionInformation 公休消化情報
	 */
	public PublicHolidayDigestionInformation createDigestionInformation (
			PublicHolidayCarryForwardDataList carryForwardDataList,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement
			){
			
		//公休消化情報を返す
		return new PublicHolidayDigestionInformation(
				new LeaveGrantDayNumber(this.publicHolidayMonthSetting.getInLegalHoliday().v()),
				carryForwardDataList.getCarryForwardData(),	
				sumUsesOfDataDuringTheperiod(tempPublicHolidayManagement));
	}
		
	
	/**
	 * 公休取得数エラーチェック
	 * @param PublicHolidayCarryForwardDataList 公休繰越データ一覧
	 * @param tempPublicHolidayManagement List<暫定公休管理データ>
	 * @return 公休エラー
	 */
	public Optional<PublicHolidayErrors> errorCheck(
			PublicHolidayCarryForwardDataList carryForwardDataList,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		//翌月繰越数を求める
		LeaveRemainingDayNumber remainingDay = carriedOverToTheNextMonth(carryForwardDataList, tempPublicHolidayManagement);
		
		if(remainingDay.greaterThan(0.0)){
			return Optional.of(new PublicHolidayErrors(true));
		}
		
		return Optional.empty();
	}
	
	/**
	 * 公休繰越情報を作成する
	 * @param PublicHolidayCarryForwardDataList 公休繰越データ一覧
	 * @param tempPublicHolidayManagement List<暫定公休管理データ>
	 * @return 公休繰越情報
	 */
	public PublicHolidayCarryForwardInformation calculateCarriedForwardInformation(
			PublicHolidayCarryForwardDataList carryForwardDataList,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		return new PublicHolidayCarryForwardInformation(
				//翌月繰越数を求める
				carriedOverToTheNextMonth(carryForwardDataList, tempPublicHolidayManagement),
				//集計期間終了時点での未消化を算出
				calculateUnused(carryForwardDataList,tempPublicHolidayManagement));
	}
	


	/**
	 * 公休繰越データを更新
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param tempPublicHolidayManagement List＜暫定公休管理データ＞
	 * @param carryForwardDataList 公休繰越データ一覧
	 * @param require
	 * @return 公休繰越データ一覧
	 */
	public PublicHolidayCarryForwardDataList updatePublicHolidayCarryForwardDataList(
			String companyId,
			String employeeId,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement,
			PublicHolidayCarryForwardDataList carryForwardDataList,
			RequireM1 require
			){
		//集計期間の繰越データ作成
		Optional<PublicHolidayCarryForwardData> CarryForwardData = createCarryForwardDataForAggregationPeriod(
				companyId,
				employeeId,
				tempPublicHolidayManagement,
				carryForwardDataList,
				require);
		
		PublicHolidayCarryForwardDataList AddCarryForwardDataList = new PublicHolidayCarryForwardDataList();
		AddCarryForwardDataList.publicHolidayCarryForwardData.addAll(carryForwardDataList.publicHolidayCarryForwardData);
		if(CarryForwardData.isPresent()){
			AddCarryForwardDataList.publicHolidayCarryForwardData.add(CarryForwardData.get());
		}
		
		//翌月に繰り越す公休繰越データ一覧を取得
		return getCarryForwardDataListCarriedOverToTheNextMonth(AddCarryForwardDataList,tempPublicHolidayManagement);
	}
	

	
	/**
	 * 集計期間終了時点での未消化を算出
	 * @param carryForwardDataList
	 * @param tempPublicHolidayManagement
	 * @return
	 */
	private LeaveRemainingDayNumber calculateUnused(
			PublicHolidayCarryForwardDataList carryForwardDataList,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		//当月残数を取得
		LeaveRemainingDayNumber remainingData = getRemainingDataOfTheMonth(tempPublicHolidayManagement);
		
		PublicHolidayCarryForwardDataList afterOffsetList = new PublicHolidayCarryForwardDataList();
		
		//公休繰越データ一覧の件数でループ
		for(PublicHolidayCarryForwardData CarryForwardData :carryForwardDataList.publicHolidayCarryForwardData){
			afterOffsetList.publicHolidayCarryForwardData.add(CarryForwardData.getCarryForwardDataAfterOffset(remainingData));
			remainingData = CarryForwardData.getAfterOffset(remainingData);
		}
		
		// 期限が切れる一覧を取得,繰越数の合計を取得
		return afterOffsetList.getExpiredList(this.period.end()).getCarryForwardData();
	}
	
	
	/**
	 * 翌月繰越数を求める
	 * @param carryForwardDataList 公休繰越データ一覧
	 * @param tempPublicHolidayManagement List＜暫定公休管理データ＞
	 * @return 翌月繰越数
	 */
	private LeaveRemainingDayNumber carriedOverToTheNextMonth(
			PublicHolidayCarryForwardDataList carryForwardDataList,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		return sumRemainingDataAndcarryForwardData(
				getRemainingDataOfTheMonth(tempPublicHolidayManagement),
				carryForwardDataList.getCarryForwardData());
	}
	
	
	/**
	 * 当月の残数を相殺する
	 * @param remainingData 当月残数
	 * @param carryForwardData 繰越残数
	 * @return 翌月繰越数
	 */
	private LeaveRemainingDayNumber offsetRemainingDataOfTheMonth(
			LeaveRemainingDayNumber remainingData, LeaveRemainingDayNumber carryForwardData){
		if(remainingData.v() < 0.0 && carryForwardData.v() > 0.0){
			return new LeaveRemainingDayNumber(remainingData.v() 
					+ Math.min(Math.abs(remainingData.v()), Math.abs(carryForwardData.v())));
		}
		
		if(remainingData.v() > 0.0 && carryForwardData.v() < 0.0){
			return new LeaveRemainingDayNumber(remainingData.v() 
					+ Math.min(Math.abs(remainingData.v()), Math.abs(carryForwardData.v())));
		}
		return remainingData;
	}
	
	/**
	 *  当月残数と繰越数を合計する
	 * @param remainingData 当月残数
	 * @param carryForwardData 繰越残数
	 * @return 残数
	 */
	private LeaveRemainingDayNumber sumRemainingDataAndcarryForwardData(
			LeaveRemainingDayNumber remainingData, LeaveRemainingDayNumber carryForwardData){
		return new LeaveRemainingDayNumber(remainingData.v() + carryForwardData.v());
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
	
	/**
	 * 翌月に繰り越す公休繰越データ一覧を取得
	 * @param carryForwardDataList 公休繰越データ一覧
	 * @param tempPublicHolidayManagement 暫定データ一覧
	 * @return 公休繰越データ一覧
	 */
	private PublicHolidayCarryForwardDataList getCarryForwardDataListCarriedOverToTheNextMonth(
			PublicHolidayCarryForwardDataList carryForwardDataList,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement){
		
		LeaveRemainingDayNumber remainingData =  getRemainingDataOfTheMonth(tempPublicHolidayManagement);
		PublicHolidayCarryForwardDataList afterOffsetList = new PublicHolidayCarryForwardDataList();
		
		for(PublicHolidayCarryForwardData CarryForwardData :carryForwardDataList.publicHolidayCarryForwardData){
			afterOffsetList.publicHolidayCarryForwardData.add(CarryForwardData.getCarryForwardDataAfterOffset(remainingData));
			remainingData = CarryForwardData.getAfterOffset(remainingData);
		}
		return afterOffsetList.getCarryForwardPublicHolidayCarryForwardDataList(this.period.end());
	}
	
	/**
	 * 集計期間の繰越データ作成
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param tempPublicHolidayManagement List＜暫定公休管理データ＞
	 * @param carryForwardDataList 公休繰越データ一覧
	 * @param require 
	 * @return　Optional<公休繰越データ>
	 */
	private Optional<PublicHolidayCarryForwardData>  createCarryForwardDataForAggregationPeriod(
			String companyId,
			String employeeId,
			List<TempPublicHolidayManagement> tempPublicHolidayManagement,
			PublicHolidayCarryForwardDataList carryForwardDataList,
			RequireM1 require){
		
		//当月残数
		LeaveRemainingDayNumber remainingData = getRemainingDataOfTheMonth(tempPublicHolidayManagement);
		//繰越数
		LeaveRemainingDayNumber carryForwardData = carryForwardDataList.getCarryForwardData();
		
		//当月の残数を相殺する
		LeaveRemainingDayNumber nextMonthCarryForwardData = offsetRemainingDataOfTheMonth(remainingData,carryForwardData);
		
		//公休設定を取得する
		Optional<PublicHolidaySetting> publicHolidaySetting = require.publicHolidaySetting(companyId);
		
		if(!publicHolidaySetting.isPresent()){
			return Optional.empty();
		}
		
		return publicHolidaySetting.get().createPublicHolidayCarryForwardData(
				employeeId,
				this.yearMonth,
				this.ymd,
				nextMonthCarryForwardData,
				GrantRemainRegisterType.MONTH_CLOSE
				);
	}
	
	
	
	public static interface RequireM1{
		//公休設定を取得する（会社ID）
		Optional<PublicHolidaySetting> publicHolidaySetting(String companyId);
	}
}


