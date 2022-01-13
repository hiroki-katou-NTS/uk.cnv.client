package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;

/**
 * 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.1日お気に入り作業セット
 * AR: 1日お気に入りセット
 * 
 * @author tutt
 */
@Getter
public class OneDayFavoriteSet extends AggregateRoot {

	/** 社員ID */
	private final String sId;

	/** お気に入りID */
	private final String favId;

	/** お気に入り作業名称 */
	@Setter
	private FavoriteTaskName taskName;

	/** お気に入り内容 */
	private final List<TaskBlockDetailContent> taskBlockDetailContents;

	/**
	 * [C-0] 1日お気に入り作業セット (社員ID,お気に入りID,名称,お気に入り内容)
	 * @param sId
	 * @param favId
	 * @param taskName
	 * @param taskBlockDetailContents
	 */
	public OneDayFavoriteSet(String sId, String favId, FavoriteTaskName taskName,
			List<TaskBlockDetailContent> taskBlockDetailContents) {
		super();
		this.sId = sId;
		this.favId = favId;
		this.taskName = taskName;
		this.taskBlockDetailContents = taskBlockDetailContents;
	}

	/**
	 * [C-1] 新規追加
	 * @param sId
	 * @param taskName
	 * @param taskBlockDetailContents
	 */
	public static OneDayFavoriteSet addOneDayFavSet(String sId, FavoriteTaskName taskName,
			List<TaskBlockDetailContent> taskBlockDetailContents) {
		return new OneDayFavoriteSet(sId, IdentifierUtil.randomUniqueId(), taskName, taskBlockDetailContents);
	}
	
}
