package nts.uk.screen.at.app.dailymodify.command.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.task.tran.TransactionService;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.app.command.dailyperform.DailyRecordWorkCommand;
import nts.uk.ctx.at.record.app.command.dailyperform.checkdata.RCDailyCorrectionResult;
import nts.uk.ctx.at.record.app.command.dailyperform.month.UpdateMonthDailyParam;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.dom.daily.itemvalue.DailyItemValue;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.pub.monthly.MonthlyAggregateForEmployeesPub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth.AggregatePastMonthResult;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth.AggregatePastMonthsService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.ValidatorDataDailyRes;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.dto.ErrorAfterCalcDaily;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.month.DPMonthValue;
import nts.uk.shr.com.context.AppContexts;

/**
 * 月別実績の集計
 * @author tutk
 *
 */
@Stateless
public class ProcessMonthlyCalc {

	@Inject
	private ValidatorDataDailyRes validatorDataDaily;
	
	@Inject
	private MonthlyAggregateForEmployeesPub monthlyAggregateForEmployeesPub;
	
	@Inject
    protected TransactionService transaction;
	
	@Inject
	private ClosureRepository closureRepository;
	
	@Inject
	private RecordDomRequireService requireService;

	public DailyCalcResult processMonthCalc(List<DailyRecordWorkCommand> commandNew,
			List<DailyRecordWorkCommand> commandOld, List<IntegrationOfDaily> domainDailyNew,
			List<DailyItemValue> dailyItems, UpdateMonthDailyParam month, DPMonthValue monthValue,
			List<EmployeeMonthlyPerError> errorMonthHoliday, DateRange range, int mode, boolean editFlex,Boolean checkUnLock) {
		if(checkUnLock == null) {
			checkUnLock = false;
		}
		String cid = AppContexts.user().companyId();
		
		RCDailyCorrectionResult resultMonth = new RCDailyCorrectionResult(domainDailyNew, new ArrayList<>(), commandNew, commandOld, dailyItems, true);

		if(checkUnLock) {
			// 当月の年月を取得する
			Optional<Closure> closureOpt = closureRepository.findById(cid, month.getClosureId());
			if(closureOpt.get().getClosureMonth().getProcessingYm().v() > month.getYearMonth()) {
				val require = requireService.createRequire();
				// 過去月を集計する
				List<AggregatePastMonthResult> listAggregatePastMonthResult = AggregatePastMonthsService
						.aggregate(require, month.getEmployeeId(), month.getDatePeriod().start(), domainDailyNew);
				return new DailyCalcResult(null, resultMonth, null, new HashMap<>(),listAggregatePastMonthResult);
			}
		}
		List<String> sids = new ArrayList<>();
		sids.add(month.getEmployeeId());
		CacheCarrier carrier = new CacheCarrier();
		carrier.get(IntegrationOfDaily.class.getName(), () -> domainDailyNew);
		//社員の月別実績を集計する
		List<AtomTask> listAtomTask = monthlyAggregateForEmployeesPub.aggregate(carrier, cid, sids, checkUnLock);
		listAtomTask.forEach(x -> transaction.execute(x));
		
		// 集計後エラーチェック
		ErrorAfterCalcDaily errorMonth = checkErrorAfterCalcMonth(resultMonth, month, mode, monthValue, range,
				editFlex ? errorMonthHoliday : new ArrayList<>());
		
		return new DailyCalcResult(null, resultMonth, errorMonth, new HashMap<>(),new ArrayList<>());
	}

	// 集計後エラーチェック
	public ErrorAfterCalcDaily checkErrorAfterCalcMonth(RCDailyCorrectionResult resultIU,
			UpdateMonthDailyParam monthlyParam, int mode, DPMonthValue monthValue, DateRange range,
			List<EmployeeMonthlyPerError> errorMonths) {
		Map<Integer, List<DPItemValue>> resultErrorMonth = new HashMap<>();
		boolean hasError = false;
		DataResultAfterIU dataResultAfterIU = new DataResultAfterIU();
		boolean editFlex = (mode == 0 && monthValue != null && !CollectionUtil.isEmpty(monthValue.getItems()));
		// if (editFlex) {
		// フレックス繰越時間が正しい範囲で入力されているかチェックする
		val flexShortageRCDto = validatorDataDaily.errorCheckFlex(resultIU.getLstMonthDomain(), monthlyParam);
		if ((flexShortageRCDto.isError() || !flexShortageRCDto.getMessageError().isEmpty()) && editFlex) {
			hasError = true;
			if (!resultIU.getLstMonthDomain().isEmpty())
				flexShortageRCDto.createDataCalc(ProcessCommonCalc.convertMonthToItem(
						MonthlyRecordWorkDto.fromOnlyAttTime(resultIU.getLstMonthDomain().get(0)), monthValue));
			flexShortageRCDto.setVersion(monthValue.getVersion());
		}
		dataResultAfterIU.setFlexShortage(flexShortageRCDto);
		// }
		// フレ補填によって年休残数のエラーが発生していないかチェックする
		if (mode == 0) {
			List<EmployeeMonthlyPerError> errorHoliday = errorMonths.stream()
					.filter(x -> x.getAnnualHoliday().isPresent()).collect(Collectors.toList());
			if (!errorHoliday.isEmpty()) {
				resultErrorMonth.putAll(validatorDataDaily.errorMonthNew(errorHoliday));
				hasError = true;
			}
		}
		return new ErrorAfterCalcDaily(hasError, resultErrorMonth, new HashSet<>(), new HashMap<>(),
				dataResultAfterIU.getFlexShortage(), new ArrayList<>());
	}
}
