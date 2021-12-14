package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 計算ステータス、中断ステータス、実績を管理するクラス
 * @author keisuke_hoshina
 *
 */
@AllArgsConstructor
@Getter
public class ManageProcessAndCalcStateResult {
		//中断ステータス
		ProcessState ps;
		//計算ステータス&実績
		List<ManageCalcStateAndResult> lst;
		
		@Setter
		Map<Pair<String, GeneralDate>, AtomTask> atomTasks;
		
		public ManageProcessAndCalcStateResult (ProcessState ps, List<ManageCalcStateAndResult> lst) {
			
			this(ps, lst, new HashMap<>());
		}
		
		protected List<IntegrationOfDaily> getIntegrationList() {
			return this.lst.stream().map(tg -> tg.integrationOfDaily).collect(Collectors.toList());
		}
}
