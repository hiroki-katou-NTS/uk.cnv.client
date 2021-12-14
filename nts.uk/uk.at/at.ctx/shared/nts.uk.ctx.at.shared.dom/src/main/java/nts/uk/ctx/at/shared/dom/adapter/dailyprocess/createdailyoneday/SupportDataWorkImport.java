package nts.uk.ctx.at.shared.dom.adapter.dailyprocess.createdailyoneday;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@AllArgsConstructor
@Data
public class SupportDataWorkImport {
	private Optional<IntegrationOfDaily> integrationOfDaily;
	private Optional<String> errorMessageId;
}
