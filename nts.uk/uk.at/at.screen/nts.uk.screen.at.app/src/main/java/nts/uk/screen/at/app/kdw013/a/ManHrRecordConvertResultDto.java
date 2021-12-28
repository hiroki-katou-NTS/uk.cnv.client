package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordConvertResult;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManHrRecordConvertResultDto {
	/** 年月日 */
	private GeneralDate ymd;

	/** 作業リスト */
	private List<ManHrTaskDetailDto> taskList;

	/**
	 * 実績内容 
	 */
	private List<ItemValueDto> manHrContents;

	public static ManHrRecordConvertResultDto fromDomain(ManHrRecordConvertResult domain) {

		return new ManHrRecordConvertResultDto(domain.getYmd(),
				domain.getTaskList().stream().map(t -> ManHrTaskDetailDto.fromDomain(t)).collect(Collectors.toList()),
				domain.getManHrContents().stream().map(t -> ItemValueDto.fromDomain(t)).collect(Collectors.toList()));
	}

}
