package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@AllArgsConstructor
@Data
public class SupportDataWork {
	private Optional<IntegrationOfDaily> integrationOfDaily;
	private Optional<String> errorMessageId;
}
