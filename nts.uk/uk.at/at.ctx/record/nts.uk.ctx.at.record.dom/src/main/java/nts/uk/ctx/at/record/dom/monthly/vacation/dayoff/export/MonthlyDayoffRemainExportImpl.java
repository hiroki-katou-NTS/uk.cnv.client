package nts.uk.ctx.at.record.dom.monthly.vacation.dayoff.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.dayoff.MonthlyDayoffRemainDataRepository;
@Stateless
public class MonthlyDayoffRemainExportImpl implements MonthlyDayoffRemainExport{
	@Inject
	private MonthlyDayoffRemainDataRepository remainDataRepos;
	
	@Override
	public List<DayoffCurrentMonthOfEmployee> lstDayoffCurrentMonthOfEmployee(String employeeId, YearMonth startMonth,
			YearMonth endMonth) {
		List<DayoffCurrentMonthOfEmployee> lstOutput = new ArrayList<DayoffCurrentMonthOfEmployee>();
		//年月期間．開始年月から終了年月まで1か月ずつループ
		for (YearMonth ym = startMonth; ym.lessThanOrEqualTo(endMonth); ym = ym.addMonths(1)) {			
			//ドメインモデル「代休月別残数データ」を取得
			List<MonthlyDayoffRemainData> getDayOffDataBySidYmStatus = remainDataRepos.findByYearMonthOrderByStartYmd(employeeId, ym);
			if(getDayOffDataBySidYmStatus.isEmpty()) {
				continue;
			}
			DayoffCurrentMonthOfEmployee dataOutput = new DayoffCurrentMonthOfEmployee(employeeId, ym, (double)0, 0, (double)0, 0, (double)0, 0, (double)0, 0, (double)0, 0);
			GeneralDate endDateRemainingMax = GeneralDate.ymd(ym.year(), ym.month(), 1);
			GeneralDate endDatecarryMax = GeneralDate.ymd(ym.year(), ym.month(), 1);
			for (MonthlyDayoffRemainData data : getDayOffDataBySidYmStatus) {
				//例えば、残数なら
				//終了日：5/20・・・残数5日
				//終了日：5/31・・・残数2日
				//→終了日が遅い5/31の残数2日を返す
				//残数は締め期間．終了日が遅い方だけ返し、
				if(data.getEndDate().afterOrEquals(endDateRemainingMax)) {
					endDateRemainingMax = data.getEndDate();
					dataOutput.setRemainingDays(data.getRemainingDayTimes().getDay().v());
					dataOutput.setRemainingTimes(data.getRemainingDayTimes().getTime().isPresent() ? data.getRemainingDayTimes().getTime().get().v() : null);
				}
				//繰越数は、締め期間．終了日が早い方だけ返します。
				if(data.getEndDate().before(endDatecarryMax)) {
					endDatecarryMax = data.getEndDate();
					dataOutput.setCarryForWardDays(data.getCarryForWardDayTimes().getDay().v());
					dataOutput.setCarryForWordTimes(data.getCarryForWardDayTimes().getTime().isPresent() ? data.getCarryForWardDayTimes().getTime().get().v() : null);
				}
				dataOutput.setOccurrenceDays(dataOutput.getOccurrenceDays() + data.getOccurrenceDayTimes().getDay().v());
				if(data.getOccurrenceDayTimes().getTime().isPresent()) {
					dataOutput.setOccurrenceTimes(dataOutput.getOccurrenceTimes() + data.getOccurrenceDayTimes().getTime().get().v());	
				}				
				dataOutput.setUseDays(dataOutput.getUseDays() + data.getUseDayTimes().getDay().v());
				if(data.getUseDayTimes().getTime().isPresent()) {
					dataOutput.setUseTimes(dataOutput.getUseTimes() + data.getUseDayTimes().getTime().get().v());	
				}
				
				dataOutput.setUnUsedDays(dataOutput.getUnUsedDays() + data.getUnUsedDayTimes().getDay().v());
				if(data.getUnUsedDayTimes().getTime().isPresent()) {
					dataOutput.setUnUsedTimes(dataOutput.getUnUsedTimes() + data.getUnUsedDayTimes().getTime().get().v());	
				}
				
			}
			lstOutput.add(dataOutput);
		}
		return lstOutput;
	}
	@Override
	public List<DayoffCurrentMonthOfEmployee> lstDayoffCurrentMonthOfEmpVer2(String employeeId, YearMonthPeriod period, 
			Map<YearMonth, List<RemainMerge>> mapRemainMer) {
		List<DayoffCurrentMonthOfEmployee> lstOutput = new ArrayList<DayoffCurrentMonthOfEmployee>();
		//年月期間．開始年月から終了年月まで1か月ずつループ
		for (Map.Entry<YearMonth, List<RemainMerge>> entry : mapRemainMer.entrySet()) {		
			//ドメインモデル「代休月別残数データ」を取得
			List<MonthlyDayoffRemainData> getDayOffDataBySidYmStatus = entry.getValue().stream()
					.map(c -> c.getMonthlyDayoffRemainData())
					.collect(Collectors.toList());
			if(getDayOffDataBySidYmStatus.isEmpty()) {
				continue;
			}
			YearMonth ym = entry.getKey();
			DayoffCurrentMonthOfEmployee dataOutput = new DayoffCurrentMonthOfEmployee(employeeId, ym, (double)0, 0, (double)0, 0, (double)0, 0, (double)0, 0, (double)0, 0);
			GeneralDate endDateRemainingMax = GeneralDate.ymd(ym.year(), ym.month(), 1);
			GeneralDate endDatecarryMax = GeneralDate.ymd(ym.year(), ym.month(), 1);
			for (MonthlyDayoffRemainData data : getDayOffDataBySidYmStatus) {
				//例えば、残数なら
				//終了日：5/20・・・残数5日
				//終了日：5/31・・・残数2日
				//→終了日が遅い5/31の残数2日を返す
				//残数は締め期間．終了日が遅い方だけ返し、
				if(data.getEndDate().afterOrEquals(endDateRemainingMax)) {
					endDateRemainingMax = data.getEndDate();
					dataOutput.setRemainingDays(data.getRemainingDayTimes().getDay().v());
					dataOutput.setRemainingTimes(data.getRemainingDayTimes().getTime().isPresent() ? data.getRemainingDayTimes().getTime().get().v() : null);
				}
				//繰越数は、締め期間．終了日が早い方だけ返します。
				if(data.getEndDate().before(endDatecarryMax)) {
					endDatecarryMax = data.getEndDate();
					dataOutput.setCarryForWardDays(data.getCarryForWardDayTimes().getDay().v());
					dataOutput.setCarryForWordTimes(data.getCarryForWardDayTimes().getTime().isPresent() ? data.getCarryForWardDayTimes().getTime().get().v() : null);
				}
				dataOutput.setOccurrenceDays(dataOutput.getOccurrenceDays() + data.getOccurrenceDayTimes().getDay().v());
				if(data.getOccurrenceDayTimes().getTime().isPresent()) {
					dataOutput.setOccurrenceTimes(dataOutput.getOccurrenceTimes() + data.getOccurrenceDayTimes().getTime().get().v());	
				}				
				dataOutput.setUseDays(dataOutput.getUseDays() + data.getUseDayTimes().getDay().v());
				if(data.getUseDayTimes().getTime().isPresent()) {
					dataOutput.setUseTimes(dataOutput.getUseTimes() + data.getUseDayTimes().getTime().get().v());	
				}
				
				dataOutput.setUnUsedDays(dataOutput.getUnUsedDays() + data.getUnUsedDayTimes().getDay().v());
				if(data.getUnUsedDayTimes().getTime().isPresent()) {
					dataOutput.setUnUsedTimes(dataOutput.getUnUsedTimes() + data.getUnUsedDayTimes().getTime().get().v());	
				}
			}
			lstOutput.add(dataOutput);
		}
		return lstOutput;
	}
}
