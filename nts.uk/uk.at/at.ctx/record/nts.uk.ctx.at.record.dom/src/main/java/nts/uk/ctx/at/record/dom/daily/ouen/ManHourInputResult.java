package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * @author thanhpv
 * @name 工数入力結果
 */
@AllArgsConstructor
@Getter
public class ManHourInputResult {

	private AtomTask atomTask;
	
	private Optional<IntegrationOfDaily> integrationOfDaily;
}
