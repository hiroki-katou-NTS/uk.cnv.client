package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.DayoffTranferInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.WorkTypeRemainInfor;

/**
 * @author thanh_nx
 *
 *         勤務種類別残数情報と代休振替情報
 */
@AllArgsConstructor
@Getter
public class RemainNumberWtSubsLeavTransInfo {

	/** 勤務種類別 */
	private Optional<WorkTypeRemainInfor> workTypeRemain;
	
	/** 代休振替 */
	private Optional<DayoffTranferInfor> dayOffTranfer;
}
