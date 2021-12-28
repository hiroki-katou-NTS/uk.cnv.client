package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormat;

/**
 * @author thanhpv
 *
 */
@Getter
@NoArgsConstructor
public class ManHrInputDisplayFormatDto{
	
	/** 実績欄表示項目一覧*/
	public List<RecordColumnDisplayItemDto> recordColumnDisplayItems;
	
	/** 実績入力ダイアログ表示項目一覧 */
	public List<DisplayAttItemDto> displayAttItems;
	
	/** 作業内容入力ダイアログ表示項目一覧*/
	public List<DisplayManHrRecordItemDto> displayManHrRecordItems;

	public ManHrInputDisplayFormatDto(ManHrInputDisplayFormat domain) {
		super();
		this.recordColumnDisplayItems = domain.getRecordColumnDisplayItems().stream().map(c -> new RecordColumnDisplayItemDto(c)).collect(Collectors.toList());
		this.displayAttItems = domain.getDisplayAttItems().stream().map(c -> new DisplayAttItemDto(c)).collect(Collectors.toList());
		this.displayManHrRecordItems = domain.getDisplayManHrRecordItems().stream().map(c -> new DisplayManHrRecordItemDto(c)).collect(Collectors.toList());
	}

}
