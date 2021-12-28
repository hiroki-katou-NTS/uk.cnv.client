package nts.uk.ctx.workflow.dom.service.output;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.workflow.dom.service.output.LevelOutput.LevelInforOutput.LevelApproverList.LevelApproverInfo;

@AllArgsConstructor
@Getter
public class ApproverFromGroupOuput {
	
	/**
	 * 承認者リスト
	 */
	private List<LevelApproverInfo> levelApproverInfoLst;
	
	/**
	 * 申請者より下の職位の承認者Flag<Optional>
	 */
	private Optional<Boolean> opLowerOrderFlg;
}
