package nts.uk.ctx.at.record.infra.repository.jobmanagement.favoritetask.favoritetaskitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.favoritetaskitem.KrcdtTaskFavFrameSetDisporder;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaFavoriteTaskDisplayOrderRepository extends JpaRepository implements FavoriteTaskDisplayOrderRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT o FROM KrcdtTaskFavFrameSetDisporder o";
	private static final String SELECT_BY_SID = SELECT_ALL_QUERY_STRING + " WHERE o.sId = :sId";
	private static final String SELECT_BY_FAVID = SELECT_ALL_QUERY_STRING + " WHERE o.favId = :favId";

	@Override
	public void insert(FavoriteTaskDisplayOrder order) {
		for (FavoriteDisplayOrder o : order.getDisplayOrders()) {
			this.commandProxy().insert(new KrcdtTaskFavFrameSetDisporder(order.getEmployeeId(), o));
		}
	}

	@Override
	public void update(FavoriteTaskDisplayOrder order) {
		for (FavoriteDisplayOrder o : order.getDisplayOrders()) {
			this.commandProxy().update(new KrcdtTaskFavFrameSetDisporder(order.getEmployeeId(), o));
		}
	}

	@Override
	public void delete(String sId) {
		List<KrcdtTaskFavFrameSetDisporder> entities = this.queryProxy()
				.query(SELECT_BY_SID, KrcdtTaskFavFrameSetDisporder.class).setParameter("sId", sId).getList();
		
		for (KrcdtTaskFavFrameSetDisporder entity: entities) {
			this.commandProxy().remove(entity);
		}
	}

	@Override
	public Optional<FavoriteTaskDisplayOrder> get(String sId) {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();

		List<KrcdtTaskFavFrameSetDisporder> entities = this.queryProxy()
				.query(SELECT_BY_SID, KrcdtTaskFavFrameSetDisporder.class).setParameter("sId", sId).getList();

		for (KrcdtTaskFavFrameSetDisporder e : entities) {
			displayOrders.add(new FavoriteDisplayOrder(e.favId, e.disporder));
		}

		return Optional.of(new FavoriteTaskDisplayOrder(sId, displayOrders));
	}

	@Override
	public void deleteByFavId(String favoriteId) {
		Optional<KrcdtTaskFavFrameSetDisporder> entityOpt = this.queryProxy()
				.query(SELECT_BY_FAVID, KrcdtTaskFavFrameSetDisporder.class).setParameter("favId", favoriteId).getSingle();
		
		if (entityOpt.isPresent()) {
			this.commandProxy().remove(entityOpt.get());
		}
		
	}

}
