package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;

/**
 * お気に入りの表示順
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class FavoriteDisplayOrder implements DomainValue {

	/** お気に入りID */
	private final String favId;

	/** 表示順 */
	private int order;

	/**
	 * [C-1] 1個目の表示順を追加する
	 * 
	 * @param favId
	 * @return お気に入りの表示順
	 */
	public static FavoriteDisplayOrder addFirstDisorder(String favId) {
		return new FavoriteDisplayOrder(favId, 1);
	}

	/**
	 * [1] 表示順を後ろにずらす
	 */
	public void shiftBackOrder() {
		this.order++;
	}
	
	/**
	 * [2] 表示順を前にずらす
	 */
	public void shiftFrontOrder() {
		this.order--;
	}
	
	/**
	 * 	[3] 表示順を入れ替える
	 * @param order
	 */
	public void changeDisplayOrder(int order) {
		this.order = order;
	}

}
