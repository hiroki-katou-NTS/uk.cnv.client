package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteSet;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
/**
 * 
 * @author tutt
 *
 */
public class OneDayFavoriteSetDto {

	/** 社員ID */
	public String sId;

	/** お気に入りID */
	public String favId;

	/** お気に入り作業名称 */
	public String taskName;

	/** お気に入り内容 */
	public List<TaskBlockDetailContentDto> taskBlockDetailContents;

	public OneDayFavoriteSetDto(OneDayFavoriteSet domain) {
		this.sId = domain.getSId();
		this.favId = domain.getFavId();
		this.taskName = domain.getTaskName().v();
		this.taskBlockDetailContents = domain.getTaskBlockDetailContents().stream()
				.map(m -> new TaskBlockDetailContentDto(m)).collect(Collectors.toList());
	}
}
