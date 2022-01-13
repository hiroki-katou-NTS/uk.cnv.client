package nts.uk.ctx.at.record.infra.repository.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteSet;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskSetRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskBlockDetailContent;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskContentForEachSupportFrame;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset.KrcdtTaskFavDaySet;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset.KrcdtTaskFavDaySetItem;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset.KrcdtTaskFavDaySetItemPk;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset.KrcdtTaskFavDaySetTs;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaOneDayFavoriteTaskSetRepository extends JpaRepository implements OneDayFavoriteTaskSetRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT s FROM KrcdtTaskFavDaySet s";

	private static final String SELECT_BY_SID = SELECT_ALL_QUERY_STRING + " WHERE s.sId = :sId";
	private static final String SELECT_BY_FAID = SELECT_ALL_QUERY_STRING + " WHERE s.favId = :favId";
	private static final String SELECT_BY_FAVID_AND_SID = SELECT_BY_FAID + " AND s.sId = :sId";
	
	private static final String DELETE_SET_TS = "SELECT ts FROM KrcdtTaskFavDaySetTs ts WHERE ts.pk.favId = :favId";
	private static final String DELETE_SET_ITEM = "SELECT i FROM KrcdtTaskFavDaySetItem i WHERE i.pk.favId = :favId";
	

	@Override
	public void insert(OneDayFavoriteSet set) {
		this.commandProxy().insert(new KrcdtTaskFavDaySet(set));

//		Map<Integer, List<TaskContent>> contentMap = new HashMap<>();
//
//		for (TaskBlockDetailContent content : set.getTaskBlockDetailContents()) {
//
//			for (TaskContentForEachSupportFrame frame : content.getTaskContents()) {
//				List<TaskContent> contents = new ArrayList<>();
//				
//				if (frame.getTaskContent().getItemId() >= 3 && frame.getTaskContent().getItemId() <= 8) {
//					if (!contentMap.containsKey(frame.getFrameNo().v())) {
//						contents.add(frame.getTaskContent());
//						contentMap.put(frame.getFrameNo().v(), contents);
//					} else {
//						contentMap.get(frame.getFrameNo().v()).add(frame.getTaskContent());
//					}
//				}
//			}
//		}
//		
//		for (Map.Entry<Integer, List<TaskContent>> entry : contentMap.entrySet()) {
//
//			List<TaskContent> taskContents = entry.getValue();
//			
//			if (!taskContents.isEmpty()) {
//				Integer taskPeriod = taskContents.stream().filter(m -> m.getItemId() == 3).findAny().map(m -> Integer.parseInt(m.getTaskCode().v())).orElse(null);
//				
//				String taskCd1 = taskContents.stream().filter(m -> m.getItemId() == 4).findAny().map(m -> m.getTaskCode().v()).orElse(null);
//				String taskCd2 = taskContents.stream().filter(m -> m.getItemId() == 5).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
//				String taskCd3 = taskContents.stream().filter(m -> m.getItemId() == 6).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
//				String taskCd4 = taskContents.stream().filter(m -> m.getItemId() == 7).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
//				String taskCd5 = taskContents.stream().filter(m -> m.getItemId() == 8).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
//
//				set.getTaskBlockDetailContents().stream()
//						.filter(x -> x.getTaskContents().stream()
//								.filter(tc -> tc.getFrameNo().v().equals(entry.getKey())).findFirst().isPresent())
//						.findFirst()
//						.ifPresent(x -> {
//								this.commandProxy()
//								.insert(new KrcdtTaskFavDaySetItem(
//												new KrcdtTaskFavDaySetItemPk(set.getFavId(), entry.getKey(),
//												x.getStartTime().v()), taskCd1,
//												taskCd2, taskCd3, taskCd4, taskCd5));
//					
//				});
//			}
//		
//		}
		
		for (TaskBlockDetailContent content : set.getTaskBlockDetailContents()) {
			
			for (TaskContentForEachSupportFrame frame : content.getTaskContents()) {
			
				List<TaskContent> taskContents = frame.getTaskContent();
				if (!taskContents.isEmpty()) {
					
					String taskCd1 = taskContents.stream().filter(m -> m.getItemId() == 4).findAny().map(m -> m.getTaskCode().v()).orElse(null);
					String taskCd2 = taskContents.stream().filter(m -> m.getItemId() == 5).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
					String taskCd3 = taskContents.stream().filter(m -> m.getItemId() == 6).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
					String taskCd4 = taskContents.stream().filter(m -> m.getItemId() == 7).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
					String taskCd5 = taskContents.stream().filter(m -> m.getItemId() == 8).findAny().map(m -> m.getTaskCode().v() == "" ? null : m.getTaskCode().v()).orElse(null);
					
					this.commandProxy()
					.insert(new KrcdtTaskFavDaySetItem(
							new KrcdtTaskFavDaySetItemPk(set.getFavId(), frame.getFrameNo().v(),
							content.getStartTime().v()), taskCd1,
							taskCd2, taskCd3, taskCd4, taskCd5, frame.getAttendanceTime().isPresent() ? frame.getAttendanceTime().get().v() : null));
					
				}
			
			}
		}
		
		

		for (TaskBlockDetailContent content : set.getTaskBlockDetailContents()) {
			this.commandProxy().insert(
					new KrcdtTaskFavDaySetTs(set.getFavId(), content.getStartTime().v(), content.getEndTime().v()));
		}

	}

	@Override
	public void update(OneDayFavoriteSet set) {
		this.queryProxy().find(set.getFavId(), KrcdtTaskFavDaySet.class).ifPresent(entity -> {
			entity.favName = set.getTaskName().v();
			this.commandProxy().update(entity);
		});
	}

	@Override
	public void delete(String sId, String favId) {
		Optional<KrcdtTaskFavDaySet> setEntity = this.queryProxy()
				.query(SELECT_BY_FAVID_AND_SID, KrcdtTaskFavDaySet.class).setParameter("favId", favId)
				.setParameter("sId", sId).getSingle();

		if (setEntity.isPresent()) {
			this.commandProxy().remove(setEntity.get());
		}
		
		List<KrcdtTaskFavDaySetTs> taskFavDaySetTsList = this.queryProxy().query(DELETE_SET_TS, KrcdtTaskFavDaySetTs.class)
				.setParameter("favId", favId)
				.getList();
		
		if (!taskFavDaySetTsList.isEmpty()) {
			for (KrcdtTaskFavDaySetTs ts : taskFavDaySetTsList) {
				this.commandProxy().remove(ts);
			}
		}
		
		List<KrcdtTaskFavDaySetItem> taskFavDaySetItemList = this.queryProxy().query(DELETE_SET_ITEM, KrcdtTaskFavDaySetItem.class)
				.setParameter("favId", favId)
				.getList();
		
		if (!taskFavDaySetItemList.isEmpty()) {
			for (KrcdtTaskFavDaySetItem item : taskFavDaySetItemList) {
				this.commandProxy().remove(item);
			}
		}
		
	}

	@Override
	public List<OneDayFavoriteSet> getAll(String sId) {
		List<OneDayFavoriteSet> result = new ArrayList<>();
		List<KrcdtTaskFavDaySet> entities = this.queryProxy().query(SELECT_BY_SID, KrcdtTaskFavDaySet.class)
				.setParameter("sId", sId).getList();

		for (KrcdtTaskFavDaySet s : entities) {
			List<TaskBlockDetailContent> taskBlockDetails = new ArrayList<>();

			toDomain(taskBlockDetails, s);

			OneDayFavoriteSet set = new OneDayFavoriteSet(s.sId, s.favId, new FavoriteTaskName(s.favName),
					taskBlockDetails);
			result.add(set);
		}
		return result;
	}

	@Override
	public Optional<OneDayFavoriteSet> getByFavoriteId(String favId) {

		Optional<KrcdtTaskFavDaySet> optEntity = this.queryProxy().query(SELECT_BY_FAID, KrcdtTaskFavDaySet.class)
				.setParameter("favId", favId).getSingle();

		List<TaskBlockDetailContent> taskBlockDetails = new ArrayList<>();

		if (!optEntity.isPresent()) {
			return Optional.empty();

		}

		KrcdtTaskFavDaySet entity = optEntity.get();

		toDomain(taskBlockDetails, entity);

		return Optional.of(new OneDayFavoriteSet(entity.sId, entity.favId, new FavoriteTaskName(entity.favName),
				taskBlockDetails));
	}

	private void toDomain(List<TaskBlockDetailContent> taskBlockDetails, KrcdtTaskFavDaySet entity) {
		for (KrcdtTaskFavDaySetTs ts : entity.krcdttaskFavDaySetTsList) {

			List<TaskContentForEachSupportFrame> taskContents = new ArrayList<>();

			for (KrcdtTaskFavDaySetItem i : ts.krcdtTaskFavDaySetItemList) {

				List<TaskContent> taskContentList = new ArrayList<>();
				taskContentList.add(new TaskContent(4, new WorkCode(i.taskCd1)));
				taskContentList.add(new TaskContent(5, new WorkCode(i.taskCd2)));
				taskContentList.add(new TaskContent(6, new WorkCode(i.taskCd3)));
				taskContentList.add(new TaskContent(7, new WorkCode(i.taskCd4)));
				taskContentList.add(new TaskContent(8, new WorkCode(i.taskCd5)));
				
				TaskContentForEachSupportFrame frame = new TaskContentForEachSupportFrame(
						new SupportFrameNo(i.pk.supTaskNo), taskContentList, Optional.of(new AttendanceTime(i.taskTime)));

				taskContents.add(frame);
			}

			TaskBlockDetailContent block = new TaskBlockDetailContent(new TimeWithDayAttr(ts.pk.startClock),
					new TimeWithDayAttr(ts.endClock), taskContents);

			taskBlockDetails.add(block);
		}
	}
}
