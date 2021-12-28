package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoCommentItem;

@AllArgsConstructor
@Getter
public class SuppInfoCommentItemDto {
	/** 補足情報NO: 作業補足情報NO */
	private Integer suppInfoNo;

	/** 補足コメント: 作業補足コメント */
	private String workSuppComment;

	public static SuppInfoCommentItemDto fromDomain(SuppInfoCommentItem domain) {
		
		return new SuppInfoCommentItemDto(domain.getSuppInfoNo().v(), domain.getWorkSuppComment()== null ? null :  domain.getWorkSuppComment().v());
	}
}
