package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppInfo;

@AllArgsConstructor
@Getter
public class WorkSuppInfoDto {

	/** 補足時間情報: List<補足情報の時間項目> */
	private List<SuppInfoTimeItemDto> suppInfoTimeItems;

	/** 補足数値情報: List<補足情報の数値項目> */
	private List<SuppInfoNumItemDto> suppInfoNumItems;

	/** 補足コメント情報: List<補足情報のコメント項目> */
	private List<SuppInfoCommentItemDto> suppInfoCommentItems;

	/** 補足選択項目情報: List<補足情報の選択項目> */
	private List<SuppInfoSelectionItemDto> suppInfoSelectionItems;

	public static WorkSuppInfoDto from(WorkSuppInfo domain) {
		
		List<SuppInfoTimeItemDto> suppInfoTimeItemDtos = domain.getSuppInfoTimeItems().stream().map(x-> SuppInfoTimeItemDto.fromDomain(x)).collect(Collectors.toList());

		List<SuppInfoNumItemDto> suppInfoNumItemDtos = domain.getSuppInfoNumItems().stream().map(x-> SuppInfoNumItemDto.fromDomain(x)).collect(Collectors.toList());
		
		List<SuppInfoCommentItemDto> suppInfoCommentItemDtos = domain.getSuppInfoCommentItems().stream().map(x-> SuppInfoCommentItemDto.fromDomain(x)).collect(Collectors.toList());
		
		List<SuppInfoSelectionItemDto> suppInfoSelectionItemDtos = domain.getSuppInfoSelectionItems().stream().map(x-> SuppInfoSelectionItemDto.fromDomain(x)).collect(Collectors.toList());
		
		return new WorkSuppInfoDto(suppInfoTimeItemDtos, suppInfoNumItemDtos, suppInfoCommentItemDtos, suppInfoSelectionItemDtos);
	}

}
