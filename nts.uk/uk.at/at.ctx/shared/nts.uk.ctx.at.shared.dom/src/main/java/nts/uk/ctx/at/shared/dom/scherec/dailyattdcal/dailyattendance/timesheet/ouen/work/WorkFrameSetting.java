package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;

import java.util.List;

import nts.arc.layer.dom.objecttype.DomainAggregate;

/** 作業枠設定 仮実装。ドメインが確定したら削除する*/
public class WorkFrameSetting implements DomainAggregate {
	/** 会社ID */
	private String companyId;
	/** 作業枠 */
	private List<WorkFrame> workFrames;

}
