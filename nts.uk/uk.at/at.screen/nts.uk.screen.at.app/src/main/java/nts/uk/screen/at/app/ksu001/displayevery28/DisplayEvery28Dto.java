package nts.uk.screen.at.app.ksu001.displayevery28;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInShiftResult;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodInWorkInfoResult;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DisplayEvery28Dto {
	
	// ・個人計集計結果　←集計内容によって情報が異なる
	public ChangePeriodInShiftResult changePeriodInShiftResult_New;
	
	// ・職場計集計結果　←集計内容によって情報が異なる
	public ChangePeriodInWorkInfoResult changePeriodInWorkInfoResult_New;
	
	
}
