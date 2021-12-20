package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.kdw013.a.ConfirmerDto;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class ChangeDateDto {
	
	// List<作業グループ>
	private List<WorkGroupDto> workGroupDtos;

	// 修正可能開始日付
	private GeneralDate workCorrectionStartDate;

	// List＜確認者>
	private List<ConfirmerDto> lstComfirmerDto;

	// List<作業実績詳細>
	private List<WorkRecordDetailDto> lstWorkRecordDetailDto;
	
}
