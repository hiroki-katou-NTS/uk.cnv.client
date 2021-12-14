package nts.uk.screen.at.app.dailymodify.command.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.command.dailyperform.checkdata.RCDailyCorrectionResult;
import nts.uk.screen.at.app.dailyperformance.correction.checkdata.dto.ErrorAfterCalcDaily;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ResultReturnDCUpdateData;

@AllArgsConstructor
@Data
public class DailyCalcResult {
	
	private DataResultAfterIU dataResultAfterIU;
	
	private RCDailyCorrectionResult resultUI;
	
	private ErrorAfterCalcDaily errorAfterCheck;
	
	private Map<Pair<String, GeneralDate>, ResultReturnDCUpdateData> lstResultDaiRowError = new HashMap<>();
}
