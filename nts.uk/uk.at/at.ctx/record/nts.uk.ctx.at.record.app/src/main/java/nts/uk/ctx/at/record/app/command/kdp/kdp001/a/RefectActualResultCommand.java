package nts.uk.ctx.at.record.app.command.kdp.kdp001.a;

import lombok.Getter;
import nts.uk.ctx.at.record.dom.worklocation.WorkLocationCD;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * 
 * @author sonnlb
 *
 */
public class RefectActualResultCommand {
	@Getter
	private String cardNumberSupport;

	/**
	 * 打刻場所コード 勤務場所コード old
	 */
	@Getter
	private String workLocationCD;

	/**
	 * 就業時間帯コード
	 */
	@Getter
	private String workTimeCode;

	/**
	 * 時間外の申告
	 */
	@Getter
	private OvertimeDeclarationComamnd overtimeDeclaration;

	public RefectActualResult toDomainValue() {
		return new RefectActualResult(cardNumberSupport, new WorkLocationCD(workLocationCD),
				new WorkTimeCode(workTimeCode), overtimeDeclaration.toDomainValue());
	}
}
