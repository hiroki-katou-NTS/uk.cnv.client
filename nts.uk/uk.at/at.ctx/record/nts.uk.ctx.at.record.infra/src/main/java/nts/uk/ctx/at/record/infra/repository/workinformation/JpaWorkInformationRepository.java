package nts.uk.ctx.at.record.infra.repository.workinformation;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workinformation.ScheduleTimeSheet;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.infra.entity.workinformation.KrcdtDaiPerWorkInfo;

/**
 * 
 * @author lamdt
 *
 */
@Stateless
public class JpaWorkInformationRepository extends JpaRepository implements WorkInformationRepository {

	private static final String DEL_BY_KEY;

	private static final String FIND_BY_LIST_SID;

	private static final String DEL_BY_LIST_KEY;

	private static final String DEL_BY_KEY_ID;

	private static final String FIND_BY_ID = "SELECT a FROM KrcdtDaiPerWorkInfo a "
			+ " WHERE a.krcdtDaiPerWorkInfoPK.employeeId = :employeeId " + " AND a.krcdtDaiPerWorkInfoPK.ymd = :ymd ";

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcdtDaiPerWorkInfo a ");
		builderString.append("WHERE a.krcdtDaiPerWorkInfoPK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDaiPerWorkInfoPK.ymd = :ymd ");
		DEL_BY_KEY = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcdtWorkScheduleTime a ");
		builderString.append("WHERE a.krcdtWorkScheduleTimePK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtWorkScheduleTimePK.ymd = :ymd ");
		DEL_BY_KEY_ID = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDaiPerWorkInfo a ");
		builderString.append("WHERE a.krcdtDaiPerWorkInfoPK.employeeId IN :employeeIds ");
		builderString.append("AND a.krcdtDaiPerWorkInfoPK.ymd IN :ymds ");
		FIND_BY_LIST_SID = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcdtDaiPerWorkInfo a ");
		builderString.append("WHERE a.krcdtDaiPerWorkInfoPK.employeeId IN :employeeIds ");
		builderString.append("AND a.krcdtDaiPerWorkInfoPK.ymd IN :ymds ");
		DEL_BY_LIST_KEY = builderString.toString();
	}

	@Override
	public Optional<WorkInfoOfDailyPerformance> find(String employeeId, GeneralDate ymd) {
		return this.queryProxy().query(FIND_BY_ID, KrcdtDaiPerWorkInfo.class).setParameter("employeeId", employeeId)
				.setParameter("ymd", ymd).getSingle(c -> c.toDomain());
	}

	@Override
	public void delete(String employeeId, GeneralDate ymd) {
		this.getEntityManager().createQuery(DEL_BY_KEY_ID).setParameter("employeeId", employeeId).setParameter("ymd", ymd)
		.executeUpdate();
		
		this.getEntityManager().createQuery(DEL_BY_KEY).setParameter("employeeId", employeeId).setParameter("ymd", ymd)
				.executeUpdate();
		this.getEntityManager().flush();
	}

	@Override
	public List<WorkInfoOfDailyPerformance> findByListEmployeeId(List<String> employeeIds, List<GeneralDate> ymds) {
		return this.queryProxy().query(FIND_BY_LIST_SID, KrcdtDaiPerWorkInfo.class)
				.setParameter("employeeIds", employeeIds).setParameter("ymds", ymds).getList(f -> f.toDomain());
	}

	@Override
	public void deleteByListEmployeeId(List<String> employeeIds, List<GeneralDate> ymds) {
		this.getEntityManager().createQuery(DEL_BY_LIST_KEY).setParameter("employeeIds", employeeIds)
				.setParameter("ymds", ymds).executeUpdate();
	}

	@Override
	public void updateByKey(WorkInfoOfDailyPerformance workInfoOfDailyPerformance) {
		Optional<KrcdtDaiPerWorkInfo> dataOpt = this.queryProxy().query(FIND_BY_ID, KrcdtDaiPerWorkInfo.class).setParameter("employeeId", workInfoOfDailyPerformance.getEmployeeId())
				.setParameter("ymd", workInfoOfDailyPerformance.getYmd()).getSingle();
		KrcdtDaiPerWorkInfo data = dataOpt.isPresent() ? dataOpt.get() : new KrcdtDaiPerWorkInfo();
		if(workInfoOfDailyPerformance != null){
		data.krcdtDaiPerWorkInfoPK.employeeId = workInfoOfDailyPerformance.getEmployeeId();
		data.krcdtDaiPerWorkInfoPK.ymd = workInfoOfDailyPerformance.getYmd();
		data.recordWorkWorktimeCode = workInfoOfDailyPerformance.getRecordWorkInformation().getWorkTimeCode().v();
		data.recordWorkWorktypeCode = workInfoOfDailyPerformance.getRecordWorkInformation().getWorkTypeCode().v();
		data.scheduleWorkWorktimeCode = workInfoOfDailyPerformance.getScheduleWorkInformation().getWorkTimeCode().v();
		data.scheduleWorkWorktypeCode = workInfoOfDailyPerformance.getScheduleWorkInformation().getWorkTypeCode().v();
		data.calculationState = workInfoOfDailyPerformance.getCalculationState().value;
		data.backStraightAttribute = workInfoOfDailyPerformance.getBackStraightAtr().value;
		data.goStraightAttribute = workInfoOfDailyPerformance.getGoStraightAtr().value;
		
		List<ScheduleTimeSheet> scheduleTimeSheets = workInfoOfDailyPerformance.getScheduleTimeSheets();
		data.scheduleTimes.forEach(item -> {
			Optional<ScheduleTimeSheet> scheduleTimeSheet = scheduleTimeSheets.stream().filter(items -> item.krcdtWorkScheduleTimePK.employeeId.equals(workInfoOfDailyPerformance.getEmployeeId())
					&& item.krcdtWorkScheduleTimePK.ymd.equals(workInfoOfDailyPerformance.getYmd())).findFirst();
			item.krcdtWorkScheduleTimePK.employeeId = workInfoOfDailyPerformance.getEmployeeId();
			item.krcdtWorkScheduleTimePK.ymd = workInfoOfDailyPerformance.getYmd();
			item.krcdtWorkScheduleTimePK.workNo = scheduleTimeSheet.get().getWorkNo().v();
			item.attendance = scheduleTimeSheet.get().getAttendance().v();
			item.leaveWork = scheduleTimeSheet.get().getLeaveWork().v();
		});
		this.commandProxy().update(data);
		}
	}

	@Override
	public void insert(WorkInfoOfDailyPerformance workInfoOfDailyPerformance) {
		this.commandProxy().insert(KrcdtDaiPerWorkInfo.toEntity(workInfoOfDailyPerformance));
	}

}