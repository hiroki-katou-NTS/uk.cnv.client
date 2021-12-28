package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteDisplayOrder;

/**
 * AR: 1日お気に入り作業の表示順
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.1日お気に入り作業の表示順
 * 
 * @author tutt
 *
 */
@Getter
public class OneDayFavoriteTaskDisplayOrder extends AggregateRoot {

	/** 社員ID */
	private final String sId;

	/** 表示順 */
	private List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();

	/**
	 * [C-0] 1日お気に入り作業の表示順 (社員ID,表示順)
	 * 
	 * @param employeeId   社員ID
	 * @param displayOrder 表示順
	 */
	public OneDayFavoriteTaskDisplayOrder(String employeeId, List<FavoriteDisplayOrder> displayOrders) {
		super();
		this.sId = employeeId;
		this.displayOrders = displayOrders;
	}

	/** [C-1] 新規追加 */
	/**
	 * 
	 * @param sId 社員ID
	 * @param favId お気に入りID
	 */
	public static OneDayFavoriteTaskDisplayOrder addOneDayFavTaskDisorder(String sId, String favId) {
		return new OneDayFavoriteTaskDisplayOrder(sId, Collections.singletonList(FavoriteDisplayOrder.addFirstDisorder(favId)));
	}

	/**
	 * [1] 新しいお気に入りの表示順を追加する
	 * 
	 */
	public void add(String favId) {
		//if (!this.displayOrders.isEmpty()) {
			for (FavoriteDisplayOrder o : this.displayOrders) {
				// 表示順を後ろにずらす
				o.shiftBackOrder();
			}
		//}

		// 表示順.追加する($新しいお気に入り)
		this.displayOrders.add(FavoriteDisplayOrder.addFirstDisorder(favId));
		
		if (this.displayOrders.size() >= 100) {
			throw new BusinessException("Msg_3256");
		}

		// 表示順：sort $.表示順 ASC
		this.displayOrders.stream().sorted(Comparator.comparingInt(FavoriteDisplayOrder::getOrder))
				.collect(Collectors.toList());
	}

	/**
	 * [2] お気に入りの表示順を削除する
	 * 
	 * @param favoriteId お気に入りID
	 */
	public void delete(String favoriteId) {
		// $削除対象
		// filter $.お気に入りID == お気に入りID
		Optional<FavoriteDisplayOrder> detetedObj = this.displayOrders.stream().filter(f -> f.getFavId().equals(favoriteId)).findAny();

		// filter $.表示順 > $削除対象.表示順
		// $.表示順を前にずらす()
		if(detetedObj.isPresent()) {
			for (FavoriteDisplayOrder o : this.displayOrders) {
				if (o.getOrder() > detetedObj.get().getOrder()) {
					o.shiftFrontOrder();
				}
			}

			// except $削除対象
			this.displayOrders = this.displayOrders.stream().filter(f -> !f.getFavId().equals(favoriteId))
					.collect(Collectors.toList());
		}
	}

	/**
	 * 
	 * @param reorderedId 順番変更対象ID
	 * @param beforeOrder 変更前の表示順
	 * @param afterOrder  変更後の表示順
	 */
	public void changeOrder(String reorderedId, int frontOrder, int backOrder) {
		if (frontOrder == backOrder) {
			return;
		}

		if (frontOrder > backOrder) {
			for (FavoriteDisplayOrder o : this.displayOrders) {
				if (o.getOrder() < frontOrder && o.getOrder() >= backOrder) {
					o.shiftBackOrder();
				}
			}
		}

		if (frontOrder < backOrder) {
			for (FavoriteDisplayOrder o : this.displayOrders) {
				if (o.getOrder() > frontOrder && o.getOrder() <= backOrder) {
					o.shiftFrontOrder();
				}
			}
		}

		for (FavoriteDisplayOrder o : this.displayOrders) {
			if (o.getFavId().equals(reorderedId)) {
				o.changeDisplayOrder(backOrder);
			}
		}

	}

}
