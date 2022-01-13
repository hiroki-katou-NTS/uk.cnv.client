package nts.uk.screen.at.app.ksu001.extractperiodfeed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInShiftResult;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInWorkInfoResult;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtractPeriodFeedDto {
	public ChangePeriodInShiftResult changePeriodInShiftResult_New;
	
	public ChangePeriodInWorkInfoResult changePeriodInWorkInfoResult_New;
}
