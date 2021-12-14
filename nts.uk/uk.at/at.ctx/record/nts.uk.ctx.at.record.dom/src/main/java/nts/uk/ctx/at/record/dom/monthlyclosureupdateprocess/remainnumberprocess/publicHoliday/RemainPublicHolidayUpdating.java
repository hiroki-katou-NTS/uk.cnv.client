package nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.publicHoliday;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataList;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistory;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.AggrResultOfPublicHoliday;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 公休残数更新
 * @author hayata_maekawa
 *
 */
public class RemainPublicHolidayUpdating {

	/**
	 * 公休繰越データ更新
	 * @param require
	 * @param cacheCarrier
	 * @param period 実締め毎集計期間
	 * @param employeeId 社員ID
	 * @param output 公休の集計結果
	 * @return
	 */
	public static AtomTask updateRemain(Require require, CacheCarrier cacheCarrier,
			AggrPeriodEachActualClosure period, String employeeId,AggrResultOfPublicHoliday output){
		List<AtomTask> atomTask = new ArrayList<>();
		
		//当月以降の公休繰越データを削除
		atomTask.add(deleteAfterCurrentMonth(require, employeeId, period));
		
		//公休繰越履歴データを追加
		atomTask.add(persistHistory(require, employeeId, period));

		return AtomTask.bundle(atomTask)
				//公休繰越データ更新処理
				.then(updateRemainProcess(require, employeeId,  output.publicHolidayCarryForwardData));
		
	}
	
	/**
	 * 当月以降の公休繰越データを削除
	 * @param require
	 * @param employeeId　社員ID
	 * @param period　実締め毎集計期間
	 * @return
	 */
	private static AtomTask deleteAfterCurrentMonth(RequireM1 require, String employeeId, AggrPeriodEachActualClosure period){
				//公休繰越データを削除する
		return AtomTask.of(() -> require.deletePublicHolidayCarryForwardDataAfter(employeeId, period.getYearMonth()))
				//公休繰越履歴データを削除する
				.then(() -> require.deleteCarryForwardDataHistoryAfter(
						employeeId, period.getYearMonth(), period.getClosureId() , period.getClosureDate()));
	}
	
	/**
	 * 公休繰越履歴データを追加
	 * @param require
	 * @param employeeId
	 * @param period
	 * @return
	 */
	private static AtomTask persistHistory(RequireM2 require, String employeeId, AggrPeriodEachActualClosure period){
		List<AtomTask> atomTask = new ArrayList<>();
		
		//繰越データを取得
		PublicHolidayCarryForwardDataList carryForwardDataList = new PublicHolidayCarryForwardDataList(
		require.publicHolidayCarryForwardData(employeeId));

		//公休繰越履歴データに追加
		atomTask.addAll(carryForwardDataList.publicHolidayCarryForwardData.stream()
				.map(data -> {
					PublicHolidayCarryForwardHistory hist = new PublicHolidayCarryForwardHistory(data,period.getYearMonth(),
					period.getClosureId(), period.getClosureDate());
			
			return AtomTask.of(() -> require.persistAndUpdateCarryForwardHistory(hist));
		}).collect(Collectors.toList()));
	
		return AtomTask.bundle(atomTask);
	}
	
	/**
	 * 公休繰越データ更新処理
	 * @param require
	 * @param employeeId　社員ID
	 * @param carryForwardData　公休繰越データ
	 * @return
	 */
	private static AtomTask updateRemainProcess(
			Require require, String employeeId, List<PublicHolidayCarryForwardData> carryForwardData){
		
		List<AtomTask> atomTask = new ArrayList<>();
		//公休繰越データを削除
		atomTask.add(AtomTask.of(() -> require.deletePublicHolidayCarryForwardData(employeeId)));
		//公休繰越データをを追加
		atomTask.addAll(carryForwardData.stream()
				.map(data -> {
					return AtomTask.of(() -> require.persistAndUpdate(data));
				}).collect(Collectors.toList()));
		
		return AtomTask.bundle(atomTask);
		
	}
	
	public static interface Require extends RequireM1,RequireM2{
		
		void deletePublicHolidayCarryForwardData(String employeeId);
		void persistAndUpdate(PublicHolidayCarryForwardData carryForwardData);
		
	}
	
	public static interface RequireM1{
		void deletePublicHolidayCarryForwardDataAfter(String employeeId, YearMonth yearMonth);
		void deleteCarryForwardDataHistoryAfter(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
	}
	
	public static interface RequireM2{
		List<PublicHolidayCarryForwardData> publicHolidayCarryForwardData(String employeeId);
		void persistAndUpdateCarryForwardHistory(PublicHolidayCarryForwardHistory hist);
	}
}
