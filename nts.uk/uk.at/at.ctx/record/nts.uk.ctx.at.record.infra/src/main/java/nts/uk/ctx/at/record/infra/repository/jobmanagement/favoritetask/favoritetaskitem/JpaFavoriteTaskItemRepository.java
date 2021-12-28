package nts.uk.ctx.at.record.infra.repository.jobmanagement.favoritetask.favoritetaskitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

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
@Stateless
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
	public List<FavoriteTaskItem> getBySameSetting(String sId, List<TaskContent> contents) {
		List<FavoriteTaskItem> result = new ArrayList<>();
		String defaultVal = null;

		String taskCode1 = contents.stream().filter(f -> f.getItemId() == 4).findAny()
				.map(x -> x.getTaskCode().v() == "" ? null : x.getTaskCode().v()).orElse(defaultVal);
		String taskCode2 = contents.stream().filter(f -> f.getItemId() == 5).findAny()
				.map(x -> x.getTaskCode().v() == "" ? null : x.getTaskCode().v()).orElse(defaultVal);
		String taskCode3 = contents.stream().filter(f -> f.getItemId() == 6).findAny()
				.map(x -> x.getTaskCode().v() == "" ? null : x.getTaskCode().v()).orElse(defaultVal);
		String taskCode4 = contents.stream().filter(f -> f.getItemId() == 7).findAny()
				.map(x -> x.getTaskCode().v() == "" ? null : x.getTaskCode().v()).orElse(defaultVal);
		String taskCode5 = contents.stream().filter(f -> f.getItemId() == 8).findAny()
				.map(x -> x.getTaskCode().v() == "" ? null : x.getTaskCode().v()).orElse(defaultVal);

		List<KrcdtTaskFavFrameSet> favItems = this.queryProxy().query(SELECT_BY_SID, KrcdtTaskFavFrameSet.class)
				.setParameter("sId", sId).getList();

		for (KrcdtTaskFavFrameSet i : favItems) {
			if (equalsWithNulls(i.taskCd1, taskCode1) && equalsWithNulls(i.taskCd2, taskCode2)
					&& equalsWithNulls(i.taskCd3, taskCode3) && equalsWithNulls(i.taskCd4, taskCode4)
					&& equalsWithNulls(i.taskCd5, taskCode5)) {
				result.add(i.toDomain());
			}
		}

		return result;
	}

	public final boolean equalsWithNulls(Object a, Object b) {
		if (a == b)
			return true;
		if ((a == null) || (b == null))
			return false;
		return a.equals(b);
	}
}
