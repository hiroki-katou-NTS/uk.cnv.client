package nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.shr.com.context.AppContexts;

public class GetRsvLeaRemNumUsageDetail {

	/**
	 * 期間内の積立年休使用明細を取得する
	 * @param employeeId 社員ID
	 * @param period　期間
	 * @param require
	 * @return
	 */
	public static List<TmpResereLeaveMng> getRsvLeaRemNumUsageDetail(
			String employeeId,DatePeriod period, Require require){
		
		val cacheCarrier = new CacheCarrier();
		String companyId = AppContexts.user().companyId();
		
		Optional<GeneralDate> startDateOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier, employeeId);
		
		List<TmpResereLeaveMng> rsvGrantRemainingDatas = new ArrayList<>();
		
		if(!startDateOpt.isPresent()) {
			return rsvGrantRemainingDatas;
		}
		
		if(period.start().before(startDateOpt.get())){
			DatePeriod pastPeriod = new DatePeriod(period.start(),startDateOpt.get().addDays(-1));
			List<DailyInterimRemainMngData> mapRemainData = AggregateMonthlyRecordService
					.mapInterimRemainData(require, cacheCarrier, companyId, employeeId, pastPeriod);
			
			rsvGrantRemainingDatas.addAll(mapRemainData.stream()
					.filter(c -> !c.getRecAbsData().isEmpty() && c.getResereData().isPresent())
					.map(c -> {
						return c.getResereData().get();
					}).collect(Collectors.toList()));
		}
		
		DatePeriod futurePeriod = new DatePeriod(startDateOpt.get(),period.end());
		
		rsvGrantRemainingDatas.addAll(require.tmpResereLeaveMng(employeeId, futurePeriod));
		
		return rsvGrantRemainingDatas;
	}
	
	public static interface Require extends GetClosureStartForEmployee.RequireM1,AggregateMonthlyRecordService.RequireM1{
		
		List<TmpResereLeaveMng> tmpResereLeaveMng(String sid, DatePeriod period);
	}
}
