package nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.publicHoliday;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.GetRemainingNumberPublicHolidayService;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.AggrResultOfPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;

/**
 * 公休残数計算
 * @author hayata_maekawa
 *
 */
public class CalculatePublicHoliday {

	/**
	 * 公休残数計算
	 * @param require
	 * @param cacheCarrier
	 * @param companyId 会社ID
	 * @param period　実締め毎集計期間
	 * @param employeeId　社員ID
	 * @param interimPublicDate　暫定公休管理データ
	 * @return 公休の集計結果
	 */
	public static AggrResultOfPublicHoliday calculateRemain(Require require, CacheCarrier cacheCarrier, String companyId,
			AggrPeriodEachActualClosure period, String employeeId, List<TempPublicHolidayManagement> interimPublicDate){
		
		return GetRemainingNumberPublicHolidayService.getPublicHolidayRemNumWithinPeriod(
				companyId, 
				employeeId, 
				Arrays.asList(period.getYearMonth()), 
				period.getPeriod().end(), 
				InterimRemainMngMode.MONTHLY,
				Optional.of(true),
				interimPublicDate,
				Optional.of(CreateAtr.RECORD),
				Optional.of(period.getPeriod()),
				cacheCarrier,
				require);
	}
	
	public static interface Require extends GetRemainingNumberPublicHolidayService.RequireM1{
	}
}
