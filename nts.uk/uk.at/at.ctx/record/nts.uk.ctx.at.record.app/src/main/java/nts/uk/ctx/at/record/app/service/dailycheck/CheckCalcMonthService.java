package nts.uk.ctx.at.record.app.service.dailycheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.ClosurePeriod;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.GetClosurePeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
//import nts.arc.time.calendar.period.DatePeriod;

/**
 * @author thanhnx 月次集計を実施する必要があるかチェックする
 *
 */
@Stateless
public class CheckCalcMonthService {

	@Inject
	private RecordDomRequireService requireService;

	public Pair<Boolean, List<AggrPeriodEachActualClosure>> isNeedCalcMonth(String companyId, String employeeId,
			List<GeneralDate> lstDayChange) {
		// 集計期間
		List<ClosurePeriod> closurePeriods = GetClosurePeriod.get(requireService.createRequire(), companyId, 
				employeeId, GeneralDate.today(), Optional.empty(), Optional.empty(), Optional.empty());
		List<AggrPeriodEachActualClosure> datePeriod = new ArrayList<>();
		for (ClosurePeriod value : closurePeriods) {
			val valueCheck = isInPeriod(value.getAggrPeriods(), lstDayChange);
			if (valueCheck.getLeft()) {
				datePeriod.addAll(valueCheck.getRight());
			}
		}
		return Pair.of(!datePeriod.isEmpty(), datePeriod);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Pair<Boolean, List<AggrPeriodEachActualClosure>> isInPeriod(List<AggrPeriodEachActualClosure> periods,
			List<GeneralDate> lstDayChange) {
		Set<AggrPeriodEachActualClosure> datePeriods = new HashSet<>();
		for (GeneralDate date : lstDayChange) {
			for (AggrPeriodEachActualClosure datePeriod : periods) {
				// datePeriod.getPeriod()
				if (date.beforeOrEquals(datePeriod.getPeriod().end())
						&& date.afterOrEquals(datePeriod.getPeriod().start())) {
					datePeriods.add(datePeriod);
				}
			}
			;
		}
		;
		return Pair.of(!datePeriods.isEmpty(), new ArrayList(datePeriods));
	}
}
