package nts.uk.ctx.at.record.infra.repository.jobmanagement.favoritetask.favoritetaskitem;

import java.util.List;
import java.util.Optional;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItem;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItemRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.favoritetaskitem.KrcdtTaskFavFrameSet;

/**
 * 
 * 
 * @author tutt
 *
 */
public class JpaFavoriteTaskItemRepository extends JpaRepository implements FavoriteTaskItemRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT s FROM KrcdtTaskFavFrameSet s";

	private static final String SELECT_BY_SID = SELECT_ALL_QUERY_STRING + " WHERE s.sId = :sId";
	private static final String SELECT_BY_FAID = SELECT_ALL_QUERY_STRING + " WHERE s.favId = :favId";
	private static final String SELECT_BY_FAVID_AND_SID = SELECT_BY_FAID + " AND s.sId = :sId";

	@Override
	public void insert(FavoriteTaskItem item) {
		this.commandProxy().insert(new KrcdtTaskFavFrameSet(item));
	}

	@Override
	public void update(FavoriteTaskItem item) {
		this.commandProxy().update(new KrcdtTaskFavFrameSet(item));
	}

	@Override
	public void delete(String sId, String favId) {
		Optional<KrcdtTaskFavFrameSet> setEntity = this.queryProxy()
				.query(SELECT_BY_FAVID_AND_SID, KrcdtTaskFavFrameSet.class).setParameter("favId", favId)
				.setParameter("sId", sId).getSingle();

		if (setEntity.isPresent()) {
			this.commandProxy().remove(setEntity.get());
		}
	}

	@Override
	public List<FavoriteTaskItem> getAll(String sId) {
		return this.queryProxy().query(SELECT_BY_SID, KrcdtTaskFavFrameSet.class).setParameter("sId", sId)
				.getList(item -> item.toDomain());
	}

	@Override
	public Optional<FavoriteTaskItem> getByFavoriteId(String favId) {
		return this.queryProxy().query(SELECT_BY_FAID, KrcdtTaskFavFrameSet.class).setParameter("favId", favId)
				.getSingle(c -> c.toDomain());
	}

	@Override
	public List<FavoriteTaskItem> getBySameSetting(String employeeId, List<TaskContent> contents) {
		// TODO Auto-generated method stub
		return null;
	}

}
