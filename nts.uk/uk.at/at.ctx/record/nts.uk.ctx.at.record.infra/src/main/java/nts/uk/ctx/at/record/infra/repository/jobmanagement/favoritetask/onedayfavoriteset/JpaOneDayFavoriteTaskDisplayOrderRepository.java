package nts.uk.ctx.at.record.infra.repository.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrderRepository;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset.KrcdtTaskFavDayDispOrder;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaOneDayFavoriteTaskDisplayOrderRepository extends JpaRepository implements OneDayFavoriteTaskDisplayOrderRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT o FROM KrcdtTaskFavDayDispOrder o";
	private static final String SELECT_BY_SID = SELECT_ALL_QUERY_STRING + " WHERE o.sId = :sId";
	private static final String SELECT_BY_FAVID = SELECT_ALL_QUERY_STRING + " WHERE o.favId = :favId";
	
	@Override
	public void insert(OneDayFavoriteTaskDisplayOrder order) {
		for (FavoriteDisplayOrder o : order.getDisplayOrders()) {
			this.commandProxy().insert(new KrcdtTaskFavDayDispOrder(order.getSId(), o));
		}
	}

	@Override
	public void update(OneDayFavoriteTaskDisplayOrder order) {
		for (FavoriteDisplayOrder o : order.getDisplayOrders()) {
			this.commandProxy().update(new KrcdtTaskFavDayDispOrder(order.getSId(), o));
		}
	}

	@Override
	public void delete(String employeeId) {
		List<KrcdtTaskFavDayDispOrder> entities = this.queryProxy()
				.query(SELECT_BY_SID, KrcdtTaskFavDayDispOrder.class).setParameter("sId", employeeId).getList();
		
		for (KrcdtTaskFavDayDispOrder entity: entities) {
			this.commandProxy().remove(entity);
		}
	}

	@Override
	public Optional<OneDayFavoriteTaskDisplayOrder> get(String employeeId) {
		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();

		List<KrcdtTaskFavDayDispOrder> entities = this.queryProxy()
				.query(SELECT_BY_SID, KrcdtTaskFavDayDispOrder.class).setParameter("sId", employeeId).getList();

		for (KrcdtTaskFavDayDispOrder e : entities) {
			displayOrders.add(new FavoriteDisplayOrder(e.favId, e.disporder));
		}

		return Optional.of(new OneDayFavoriteTaskDisplayOrder(employeeId, displayOrders));
	}

	@Override
	public void deleteByFavId(String favId) {
		Optional<KrcdtTaskFavDayDispOrder> entityOpt = this.queryProxy()
				.query(SELECT_BY_FAVID, KrcdtTaskFavDayDispOrder.class).setParameter("favId", favId).getSingle();
		
		if (entityOpt.isPresent()) {
			this.commandProxy().remove(entityOpt.get());
		}
	}

}
