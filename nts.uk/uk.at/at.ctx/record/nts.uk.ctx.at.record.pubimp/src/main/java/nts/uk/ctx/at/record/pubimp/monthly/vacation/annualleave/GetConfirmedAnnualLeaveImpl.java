package nts.uk.ctx.at.record.pubimp.monthly.vacation.annualleave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.pub.monthly.vacation.annualleave.AnnualLeaveUsageExport;
import nts.uk.ctx.at.record.pub.monthly.vacation.annualleave.GetConfirmedAnnualLeave;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonthRepository;

/**
 * 実装：社員の月毎の確定済み年休を取得する
 * @author shuichu_ishida
 */
@Stateless
public class GetConfirmedAnnualLeaveImpl implements GetConfirmedAnnualLeave {

	/** 年休月別残数データ */
	@Inject
	private AnnLeaRemNumEachMonthRepository annLeaRemNumEachMonthRepo;
	
	/** 社員の月毎の確定済み年休を取得する */
	@Override
	public List<AnnualLeaveUsageExport> algorithm(String employeeId, YearMonthPeriod period) {
		
		// 年月期間を取得
		val yearMonths = ConvertHelper.yearMonthsBetween(period);

		// 「年休月別残数データ」を取得
		List<String> employeeIds = new ArrayList<>();
		employeeIds.add(employeeId);
		val datas = this.annLeaRemNumEachMonthRepo.findBySidsAndYearMonths(employeeIds, yearMonths);

		Map<YearMonth, AnnualLeaveUsageExport> results = new HashMap<>();
		Map<YearMonth, GeneralDate> saveDates = new HashMap<>();
		for (val data : datas){
			
			// 「締め済」でないデータは、除く
			//if (data.getClosureStatus() != ClosureStatus.PROCESSED) continue;
			
			val yearMonth = data.getYearMonth();
			val annualLeave = data.getAnnualLeave();
			val usedNumber = annualLeave.getUsedNumberInfo().getUsedNumber();
			val remNumber = annualLeave.getRemainingNumberInfo().getRemainingNumber();
			
			AnnualLeaveUsedDayNumber usedDays =
					new AnnualLeaveUsedDayNumber(usedNumber.getUsedDays().map(c -> c.v()).orElse(0d));
			UsedMinutes usedTime = null;
			if (usedNumber.getUsedTime().isPresent()){
				usedTime = new UsedMinutes(usedNumber.getUsedTime().get().valueAsMinutes());
			}
			AnnualLeaveRemainingDayNumber remainingDays =
					new AnnualLeaveRemainingDayNumber(remNumber.getTotalRemainingDays().v());
			RemainingMinutes remainingTime = null;
			if (remNumber.getTotalRemainingTime().isPresent()){
				remainingTime = new RemainingMinutes(remNumber.getTotalRemainingTime().get().v());
			}
			
			// 同じ年月が複数ある時、合算する
			if (results.containsKey(yearMonth)){
				val oldResult = results.get(yearMonth);
				val oldDate = saveDates.get(yearMonth);
				
				usedDays = new AnnualLeaveUsedDayNumber(usedDays.v() + oldResult.getUsedDays().v());
				if (oldResult.getUsedTime().isPresent()){
					if (usedTime == null){
						usedTime = new UsedMinutes(oldResult.getUsedTime().get().v());
					}
					else {
						usedTime = new UsedMinutes(usedTime.v() + oldResult.getUsedTime().get().v());
					}
				}
				
				// 年休残数に限り、締め期間．終了日の遅い方を保持する
				if (data.getClosurePeriod().end().before(oldDate)){
					remainingDays = new AnnualLeaveRemainingDayNumber(oldResult.getRemainingDays().v());
					remainingTime = null;
					if (oldResult.getRemainingTime().isPresent()){
						remainingTime = new RemainingMinutes(oldResult.getRemainingTime().get().v());
					}
				}
				else {
					saveDates.put(yearMonth, data.getClosurePeriod().end());
				}
			}
			
			results.put(yearMonth, new AnnualLeaveUsageExport(
					yearMonth,
					usedDays,
					Optional.ofNullable(usedTime),
					remainingDays,
					Optional.ofNullable(remainingTime)));
			
			saveDates.putIfAbsent(yearMonth, data.getClosurePeriod().end());
		}
		
		// 年休月別残数データリストを返す
		val resultList = results.values().stream().collect(Collectors.toList());
		resultList.sort((a, b) -> a.getYearMonth().compareTo(b.getYearMonth()));
		return resultList;
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001
	 * RequestList255 社員の月毎の確定済み年休を取得する - ver2
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 */
	@Override
	public List<AnnualLeaveUsageExport> getYearHdMonthlyVer2(String employeeId, YearMonthPeriod period, 
			Map<YearMonth, List<RemainMerge>> mapRemainMer) {
		// 「年休月別残数データ」を取得
		Map<YearMonth, AnnualLeaveUsageExport> results = new HashMap<>();
		Map<YearMonth, GeneralDate> saveDates = new HashMap<>();
		for (Map.Entry<YearMonth, List<RemainMerge>> entry : mapRemainMer.entrySet()) {
			List<AnnLeaRemNumEachMonth> lstData = entry.getValue().stream()
					.map(c -> c.getAnnLeaRemNumEachMonth())
					.collect(Collectors.toList());
			for(AnnLeaRemNumEachMonth data: lstData){
				// 「締め済」でないデータは、除く
				val yearMonth = data.getYearMonth();
				val annualLeave = data.getAnnualLeave();
				val usedNumber = annualLeave.getUsedNumberInfo().getUsedNumber();
				val remNumber = annualLeave.getRemainingNumberInfo().getRemainingNumber();
				
				AnnualLeaveUsedDayNumber usedDays =
						new AnnualLeaveUsedDayNumber(usedNumber.getUsedDays().map(c -> c.v()).orElse(0d));
				UsedMinutes usedTime = null;
				if (usedNumber.getUsedTime().isPresent()){
					usedTime = new UsedMinutes(usedNumber.getUsedTime().get().valueAsMinutes());
				}
				AnnualLeaveRemainingDayNumber remainingDays =
						new AnnualLeaveRemainingDayNumber(remNumber.getTotalRemainingDays().v());
				RemainingMinutes remainingTime = null;
				if (remNumber.getTotalRemainingTime().isPresent()){
					remainingTime = new RemainingMinutes(remNumber.getTotalRemainingTime().get().v());
				}
				
				// 同じ年月が複数ある時、合算する
				if (results.containsKey(yearMonth)){
					val oldResult = results.get(yearMonth);
					val oldDate = saveDates.get(yearMonth);
					
					usedDays = new AnnualLeaveUsedDayNumber(usedDays.v() + oldResult.getUsedDays().v());
					if (oldResult.getUsedTime().isPresent()){
						if (usedTime == null){
							usedTime = new UsedMinutes(oldResult.getUsedTime().get().v());
						}
						else {
							usedTime = new UsedMinutes(usedTime.v() + oldResult.getUsedTime().get().v());
						}
					}
					
					// 年休残数に限り、締め期間．終了日の遅い方を保持する
					if (data.getClosurePeriod().end().before(oldDate)){
						remainingDays = new AnnualLeaveRemainingDayNumber(oldResult.getRemainingDays().v());
						remainingTime = null;
						if (oldResult.getRemainingTime().isPresent()){
							remainingTime = new RemainingMinutes(oldResult.getRemainingTime().get().v());
						}
					}
					else {
						saveDates.put(yearMonth, data.getClosurePeriod().end());
					}
				}
				
				results.put(yearMonth, new AnnualLeaveUsageExport(
						yearMonth,
						usedDays,
						Optional.ofNullable(usedTime),
						remainingDays,
						Optional.ofNullable(remainingTime)));
				
				saveDates.putIfAbsent(yearMonth, data.getClosurePeriod().end());
			}
		}
		// 年休月別残数データリストを返す
		List<AnnualLeaveUsageExport> resultList = results.values().stream().collect(Collectors.toList());
		resultList.sort((a, b) -> a.getYearMonth().compareTo(b.getYearMonth()));
		return resultList;
	}

	@Override
	public List<AnnualLeaveUsageExport> getYearHdMonthlyVer4(String employeeId, YearMonthPeriod period, Map<YearMonth, List<RemainMerge>> mapRemainMer) {
			// 「年休月別残数データ」を取得
			Map<YearMonth, AnnualLeaveUsageExport> results = new HashMap<>();
			Map<YearMonth, GeneralDate> saveDates = new HashMap<>();
			for (Map.Entry<YearMonth, List<RemainMerge>> entry : mapRemainMer.entrySet()) {
				List<AnnLeaRemNumEachMonth> lstData = entry.getValue().stream()
						.map(c -> c.getAnnLeaRemNumEachMonth())
						.collect(Collectors.toList());
				for(AnnLeaRemNumEachMonth data: lstData){
					// 「締め済」でないデータは、除く
					val yearMonth = data.getYearMonth();
					val annualLeave = data.getAnnualLeave();
					val usedNumber = annualLeave.getUsedNumberInfo().getUsedNumber();
					val remNumber = annualLeave.getRemainingNumberInfo().getRemainingNumber();
					val halfDayAnnualLeave = data.getHalfDayAnnualLeave();
					val maxRemainingTime = data.getMaxRemainingTime();

					/** 月度使用回数 : 半日年休．使用数．回数付与前 */
					UsedTimes numOfuses = null;

					/** 月度残回数: 半日年休．残数．回数付与前*/
					RemainingTimes numOfRemain = null;

					/** 月度残時間: 上限残時間．時間付与前 */
					RemainingMinutes monthlyRemainTime = null;
					if(halfDayAnnualLeave.isPresent()){
						numOfuses = halfDayAnnualLeave.get().getUsedNum()!=null?halfDayAnnualLeave.get().getUsedNum()
								.getTimesBeforeGrant():null;
						numOfRemain = halfDayAnnualLeave.get().getRemainingNum()!=null?
								halfDayAnnualLeave.get().getRemainingNum().getTimesBeforeGrant():null;
					}
					if(maxRemainingTime.isPresent()){
						monthlyRemainTime = new RemainingMinutes(maxRemainingTime.get().getTimeBeforeGrant().v());
					}
					AnnualLeaveUsedDayNumber usedDays =
							new AnnualLeaveUsedDayNumber(usedNumber.getUsedDays().map(c -> c.v()).orElse(0d));
					UsedMinutes usedTime = null;
					if (usedNumber.getUsedTime().isPresent()){
						usedTime = new UsedMinutes(usedNumber.getUsedTime().get().valueAsMinutes());
					}
					AnnualLeaveRemainingDayNumber remainingDays =
							new AnnualLeaveRemainingDayNumber(remNumber.getTotalRemainingDays().v());
					RemainingMinutes remainingTime = null;
					if (remNumber.getTotalRemainingTime().isPresent()){
						remainingTime = new RemainingMinutes(remNumber.getTotalRemainingTime().get().v());
					}

					// 同じ年月が複数ある時、合算する
					if (results.containsKey(yearMonth)){
						val oldResult = results.get(yearMonth);
						val oldDate = saveDates.get(yearMonth);

						usedDays = new AnnualLeaveUsedDayNumber(usedDays.v() + oldResult.getUsedDays().v());
						if (oldResult.getUsedTime().isPresent()){
							if (usedTime == null){
								usedTime = new UsedMinutes(oldResult.getUsedTime().get().v());
							}
							else {
								usedTime = new UsedMinutes(usedTime.v() + oldResult.getUsedTime().get().v());
							}
						}
						if(oldResult.getNumOfuses().isPresent()){
							if(numOfuses == null){
								numOfuses = oldResult.getNumOfuses().get();
							}else {
								numOfuses = new UsedTimes(oldResult.getNumOfuses().get().v() + numOfuses.v());
							}
						}
						if(oldResult.getNumOfRemain().isPresent()){
							if(numOfRemain == null){
								numOfRemain = oldResult.getNumOfRemain().get();
							}else {
								numOfRemain = new RemainingTimes(oldResult.getNumOfRemain().get().v() + numOfRemain.v());
							}
						}
						if(oldResult.getMonthlyRemainTime().isPresent()){
							if(monthlyRemainTime == null){
								monthlyRemainTime = oldResult.getMonthlyRemainTime().get();
							}else {
								monthlyRemainTime = new RemainingMinutes(oldResult.getMonthlyRemainTime().get().v()
										+ monthlyRemainTime.v());
							}
						}
						// 年休残数に限り、締め期間．終了日の遅い方を保持する
						if (data.getClosurePeriod().end().before(oldDate)){
							remainingDays = new AnnualLeaveRemainingDayNumber(oldResult.getRemainingDays().v());
							remainingTime = null;
							if (oldResult.getRemainingTime().isPresent()){
								remainingTime = new RemainingMinutes(oldResult.getRemainingTime().get().v());
							}
						}
						else {
							saveDates.put(yearMonth, data.getClosurePeriod().end());
						}
					}

					results.put(yearMonth, new AnnualLeaveUsageExport(
							yearMonth,
							usedDays,
							Optional.ofNullable(usedTime),
							remainingDays,
							Optional.ofNullable(remainingTime),
							Optional.ofNullable(numOfuses),
							Optional.ofNullable(numOfRemain),
							Optional.ofNullable(monthlyRemainTime)
					));

					saveDates.putIfAbsent(yearMonth, data.getClosurePeriod().end());
				}
			}
			// 年休月別残数データリストを返す
			List<AnnualLeaveUsageExport> resultList = results.values().stream().collect(Collectors.toList());
			resultList.sort((a, b) -> a.getYearMonth().compareTo(b.getYearMonth()));
			return resultList;
		}
	}

