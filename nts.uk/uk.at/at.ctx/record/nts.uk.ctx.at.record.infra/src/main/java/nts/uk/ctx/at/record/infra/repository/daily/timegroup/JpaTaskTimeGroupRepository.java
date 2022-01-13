package nts.uk.ctx.at.record.infra.repository.daily.timegroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroup;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroupRepository;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeZone;
import nts.uk.ctx.at.record.infra.entity.daily.timegroup.KsrdtTaskTsGroup;
import nts.uk.ctx.at.record.infra.entity.daily.timegroup.KsrdtTaskTsGroupPk;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaTaskTimeGroupRepository extends JpaRepository implements TaskTimeGroupRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT g FROM KsrdtTaskTsGroup g";
	private static final String SELECT_BY_SID = SELECT_ALL_QUERY_STRING + " WHERE g.pk.sId = :sId";
	private static final String SELECT_BY_SID_AND_DATE = SELECT_BY_SID + " AND g.pk.date = :date";
	
	private static final String DELETE_BY_DATE= "DELETE FROM KsrdtTaskTsGroup g WHERE g.pk.sId = :sId AND g.pk.date = :date";

	@Override
	public void insert(TaskTimeGroup tg) {

		for (TaskTimeZone t : tg.getTimezones()) {
			
			t.getSupNos().forEach(sn -> {
				this.commandProxy().insert(new KsrdtTaskTsGroup(tg.getSId(), tg.getDate(), t.getCaltimeSpan(), sn));
			});
			
		}
	}

	@Override
	public void delete(String sId, GeneralDate date) {
		this.getEntityManager().createQuery(DELETE_BY_DATE)
		.setParameter("date", date).setParameter("sId", sId)
		.executeUpdate();
		this.getEntityManager().flush();
	}

	@Override
	public Optional<TaskTimeGroup> get(String sId, GeneralDate date) {
		List<KsrdtTaskTsGroup> entities = this.queryProxy().query(SELECT_BY_SID_AND_DATE, KsrdtTaskTsGroup.class)
				.setParameter("date", date)
				.setParameter("sId", sId)
				.getList();
		
		if (entities.isEmpty()) {
			return Optional.empty();
		}
		
		List<TaskTimeZone> timezones = createTimeZones(entities);
		
		
		
		return Optional.of(new TaskTimeGroup(sId, date, timezones));
	}

	private List<TaskTimeZone> createTimeZones(List<KsrdtTaskTsGroup> entities) {
		List<TaskTimeZone> timezones =  new ArrayList<>();
		
		Map<String,TaskTimeZone> frameMaps =  new HashMap<>();
		
		for (KsrdtTaskTsGroup group : entities) {
			
			String key =  String.valueOf(group.pk.startClock)  +  String.valueOf(group.endClock);
			
			TaskTimeZone value =  frameMaps.get(key);
			
			if (value != null) {
				value.getSupNos().add(new SupportFrameNo(group.pk.subNo));
			}else{
				frameMaps.put(key, 
						new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(group.pk.startClock),new TimeWithDayAttr(group.endClock)), 
										new ArrayList<>(Arrays.asList(new SupportFrameNo(group.pk.subNo))))
						);
			}
		}
		
		frameMaps.entrySet().forEach(entry -> {
			timezones.add(entry.getValue());
		});
		
		return timezones;
	}

	@Override
	public List<TaskTimeGroup> get(String sId, DatePeriod period) {

		List<TaskTimeGroup> timeGroups = new ArrayList<>();

		List<GeneralDate> dates = period.datesBetween();

		for (GeneralDate date : dates) {

			this.get(sId, date).ifPresent(tg -> {
				timeGroups.add(tg);
			});

		}
		return timeGroups;
	}

}
