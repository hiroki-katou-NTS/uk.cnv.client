package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppInfo;

@AllArgsConstructor
@Getter
public class WorkSuppInfoCommand {
	/** 補足時間情報: List<補足情報の時間項目> */
	private List<SuppInfoTimeItemCommand> suppInfoTimeItems;

	/** 補足数値情報: List<補足情報の数値項目> */
	private List<SuppInfoNumItemCommand> suppInfoNumItems;

	/** 補足コメント情報: List<補足情報のコメント項目> */
	private List<SuppInfoCommentItemCommand> suppInfoCommentItems;

	/** 補足選択項目情報: List<補足情報の選択項目> */
	private List<SuppInfoSelectionItemCommand> suppInfoSelectionItems;

	public WorkSuppInfo toDomain() {
		return new WorkSuppInfo(
				this.getSuppInfoTimeItems().stream().map(x-> x.toDomain()).collect(Collectors.toList()),
				this.getSuppInfoNumItems().stream().map(x-> x.toDomain()).collect(Collectors.toList()),
				this.getSuppInfoCommentItems().stream().map(x-> x.toDomain()).collect(Collectors.toList()),
				this.getSuppInfoSelectionItems().stream().map(x-> x.toDomain()).collect(Collectors.toList()));
	}
}
