package nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.publicHoliday;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistory;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.AggrResultOfPublicHoliday;

/**
 * 公休残数更新
 * 
 * @author hayata_maekawa
 *
 */
public class RemainPublicHolidayUpdating {

	/**
	 * 公休繰越データ更新
	 * 
	 * @param require
	 * @param cacheCarrier
	 * @param period
	 *            実締め毎集計期間
	 * @param employeeId
	 *            社員ID
	 * @param output
	 *            公休の集計結果
	 * @return
	 */
	public static AtomTask updateRemain(Require require, CacheCarrier cacheCarrier, AggrPeriodEachActualClosure period,
			String employeeId, AggrResultOfPublicHoliday output) {
		List<AtomTask> atomTask = new ArrayList<>();

		// 公休繰越履歴データを追加
		atomTask.add(persistHistory(require, employeeId, period));

		return AtomTask.bundle(atomTask)
				// 公休繰越データ更新処理
				.then(updateRemainProcess(require, employeeId, output.publicHolidayCarryForwardData));

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
		Optional<PublicHolidayCarryForwardData> carryForwardData = require.publicHolidayCarryForwardData(employeeId);

		if(carryForwardData.isPresent()){
		//公休繰越履歴データに追加
		atomTask.add(AtomTask.of(() -> require.persistAndUpdateCarryForwardHistory(
				new PublicHolidayCarryForwardHistory(carryForwardData.get(),period.getYearMonth(),
					period.getClosureId(), period.getClosureDate()))));
		}
	
		return AtomTask.bundle(atomTask);
	}

	/**
	 * 公休繰越データ更新処理
	 * 
	 * @param require
	 * @param employeeId
	 *            社員ID
	 * @param carryForwardData
	 *            公休繰越データ
	 * @return
	 */
	private static AtomTask updateRemainProcess(Require require, String employeeId,
			PublicHolidayCarryForwardData carryForwardData) {

		List<AtomTask> atomTask = new ArrayList<>();
		// 公休繰越データをを追加
		atomTask.add(AtomTask.of(() -> require.persistAndUpdate(carryForwardData)));

		return AtomTask.bundle(atomTask);

	}

	public static interface Require extends RequireM2 {

		void deletePublicHolidayCarryForwardData(String employeeId);

		void persistAndUpdate(PublicHolidayCarryForwardData carryForwardData);

	}


	public static interface RequireM2 {
		Optional<PublicHolidayCarryForwardData> publicHolidayCarryForwardData(String employeeId);

		void persistAndUpdateCarryForwardHistory(PublicHolidayCarryForwardHistory hist);
	}
}
