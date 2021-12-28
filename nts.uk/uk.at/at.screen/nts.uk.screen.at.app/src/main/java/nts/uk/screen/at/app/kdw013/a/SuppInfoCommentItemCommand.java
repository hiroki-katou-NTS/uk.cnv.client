package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoCommentItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppComment;

@AllArgsConstructor
@Getter
public class SuppInfoCommentItemCommand {
	/** 補足情報NO: 作業補足情報NO */
	private Integer suppInfoNo;

	/** 補足コメント: 作業補足コメント */
	private String workSuppComment;

	public SuppInfoCommentItem toDomain() {

		return new SuppInfoCommentItem(new SuppInfoNo(this.getSuppInfoNo()),
				this.getWorkSuppComment()==null?null: new WorkSuppComment(this.getWorkSuppComment()));
	}
}
