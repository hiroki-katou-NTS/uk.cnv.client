package nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.publicHoliday;


import java.util.List;
import java.util.stream.Collectors;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.AggrResultOfPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.shr.com.context.AppContexts;

/**
 * 公休処理
 * @author hayata_maekawa
 *
 */
public class PublicHolidayProcess {

	public static AtomTask process(Require require, CacheCarrier cacheCarrier,
			AggrPeriodEachActualClosure period, String employeeId, List<DailyInterimRemainMngData> interimRemainMngMap){
		
		//ログイン社員の会社ID取得
		String companyId = AppContexts.user().companyId();
		
		//List<暫定管理データ>から暫定公休管理データに絞り込む
		List<TempPublicHolidayManagement> interimPublicData = getRemain(interimRemainMngMap);
		
		//公休残数計算
		AggrResultOfPublicHoliday output = CalculatePublicHoliday.calculateRemain(
				require, cacheCarrier, companyId, period, employeeId,interimPublicData);
		
		//公休残数更新
		return AtomTask.of(RemainPublicHolidayUpdating.updateRemain(require, cacheCarrier, period, employeeId, output))
				//公休暫定データ削除
				.then(DeleteTempPublicHoliday.delete(require, employeeId, period.getPeriod()));
		
	}
	/**
	 * 暫定公休管理データに絞り込み
	 * @param interimRemainMngMap 暫定残数データ
	 * @return
	 */
	private static List<TempPublicHolidayManagement> getRemain(List<DailyInterimRemainMngData> interimRemainMngMap){
		
		return interimRemainMngMap.stream()
				.filter(c -> !c.getRecAbsData().isEmpty() && !c.getPublicHolidayData().isEmpty())
				.map(c -> {
					return c.getPublicHolidayData();
				}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	
	public static interface Require extends RemainPublicHolidayUpdating.Require, CalculatePublicHoliday.Require,
		DeleteTempPublicHoliday.Require{
		
	}
}
