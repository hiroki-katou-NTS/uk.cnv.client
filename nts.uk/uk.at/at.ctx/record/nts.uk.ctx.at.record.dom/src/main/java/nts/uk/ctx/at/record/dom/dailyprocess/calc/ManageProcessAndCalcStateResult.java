package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.CreateDailyResultDomainServiceImpl.ProcessState;

/**
 * 計算ステータス、中断ステータス、実績を管理するクラス
 * @author keisuke_hoshina
 *
 */
@AllArgsConstructor
@Getter
public class ManageProcessAndCalcStateResult {
		ProcessState ps;
		List<ManageCalcStateAndResult> lst;
}
