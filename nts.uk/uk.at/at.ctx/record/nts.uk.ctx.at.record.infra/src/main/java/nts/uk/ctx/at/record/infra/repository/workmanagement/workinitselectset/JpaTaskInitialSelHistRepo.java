package nts.uk.ctx.at.record.infra.repository.workmanagement.workinitselectset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskCode;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSel;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHistRepo;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskItem;
import nts.uk.ctx.at.record.infra.entity.workmanagement.workinitselectset.KrcmtTaskInitialSelHist;
import nts.uk.ctx.at.record.infra.entity.workmanagement.workinitselectset.KrcmtTaskInitialSelHistPk;

/**
 * 
 * @author HieuLt
 *
 */
@Stateless
public class JpaTaskInitialSelHistRepo extends JpaRepository implements TaskInitialSelHistRepo {

	private static final String DELETE_BY_SID = " DELETE FROM KrcmtTaskInitialSelHist c WHERE c.pk.sID = :empId";  
														
	private static final String SELECT_BY_SID = " SELECT c FROM KrcmtTaskInitialSelHist c WHERE c.pk.sID = :empId ";
	
	private static final String SELECT_BY_BASE_DATE = SELECT_BY_SID  
										           + " AND c.pk.startDate <= :baseDate "
										           + " AND c.endDate >= :baseDate";
	
	private static final String SELECT_BY_CID = " SELECT c FROM KrcmtTaskInitialSelHist c WHERE c.companyId = :companyId "; 
	
	private static final String SELECT_BY_SID_ORDER_BY_STARTDATE = SELECT_BY_SID + " ORDER BY c.pk.startDate DESC ";

	@Override
	public void insert(TaskInitialSelHist taskInitialSelHist) {
		for(int i = 0; i < taskInitialSelHist.getLstHistory().size(); i++) {
			KrcmtTaskInitialSelHistPk pk = new KrcmtTaskInitialSelHistPk(taskInitialSelHist.getEmpId(), taskInitialSelHist.getLstHistory().get(i).getDatePeriod().start());
			Optional<KrcmtTaskInitialSelHist> entity = this.queryProxy().find(pk, KrcmtTaskInitialSelHist.class);
			if (!entity.isPresent()) {
				this.commandProxy().insert(KrcmtTaskInitialSelHist.toEntity(taskInitialSelHist, i));
			} else {
				TaskInitialSel hist = taskInitialSelHist.getLstHistory().get(i);
				entity.get().endDate = hist.end();
				entity.get().taskCd1 = hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode1().get().v() : "";
				entity.get().taskCd2 = hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode2().get().v() : "";
				entity.get().taskCd3 = hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode3().get().v() : "";
				entity.get().taskCd4 = hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode4().get().v() : "";
				entity.get().taskCd5 = hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode5().get().v() : "";
				this.commandProxy().update(entity.get());
			}
		}		
	}

	@Override
	public void update(TaskInitialSelHist taskInitialSelHist) {
		Boolean isExist = false;
		List<KrcmtTaskInitialSelHist> krcmtTaskInitialSelHists = new ArrayList<KrcmtTaskInitialSelHist>();
		List<KrcmtTaskInitialSelHist> listKrcmtTaskInitialSelHists = new ArrayList<KrcmtTaskInitialSelHist>();
		List<KrcmtTaskInitialSelHist> listKrcmtTaskInitialSelHistsOld = this.queryProxy().query(SELECT_BY_SID_ORDER_BY_STARTDATE ,  KrcmtTaskInitialSelHist.class )
																						.setParameter("empId", taskInitialSelHist.getEmpId())
																						.getList().stream().collect(Collectors.toList());
		
		taskInitialSelHist.getLstHistory().stream().forEach(hist -> {
			KrcmtTaskInitialSelHist entity = new KrcmtTaskInitialSelHist( 
				new KrcmtTaskInitialSelHistPk(taskInitialSelHist.getEmpId(), hist.getDatePeriod().start()),
				hist.end(),	hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode1().get().v() : "",
				hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode2().get().v() : "",
				hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode3().get().v() : "",
				hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode4().get().v() : "",
				hist.getTaskItem().getOtpWorkCode1().isPresent() ? hist.getTaskItem().getOtpWorkCode5().get().v() : "");
			
			listKrcmtTaskInitialSelHists.add(entity);
		});
		
		for(int i = 0; i < listKrcmtTaskInitialSelHistsOld.size(); i++) {
			isExist = false;
			for(int j= 0; j < listKrcmtTaskInitialSelHists.size(); j++) {
				if(listKrcmtTaskInitialSelHistsOld.get(i).pk.startDate == listKrcmtTaskInitialSelHists.get(j).pk.startDate) {
					isExist = true;
				}
			}
			if(!isExist) {
				this.commandProxy().remove(listKrcmtTaskInitialSelHistsOld.get(i));
			}			
		}
		
		if(listKrcmtTaskInitialSelHistsOld.size() > listKrcmtTaskInitialSelHists.size()) {
			this.commandProxy().remove(listKrcmtTaskInitialSelHistsOld.get(0));
		}
		
		listKrcmtTaskInitialSelHists.forEach(taskIntialSelHist -> {
			Optional<KrcmtTaskInitialSelHist> entity = this.queryProxy().find(taskIntialSelHist.pk, KrcmtTaskInitialSelHist.class);
			if (entity.isPresent()) {
				entity.get().endDate = taskIntialSelHist.endDate;
				entity.get().taskCd1 = taskIntialSelHist.taskCd1;
				entity.get().taskCd2 = taskIntialSelHist.taskCd2;
				entity.get().taskCd3 = taskIntialSelHist.taskCd3;
				entity.get().taskCd4 = taskIntialSelHist.taskCd4;
				entity.get().taskCd5 = taskIntialSelHist.taskCd5;
				krcmtTaskInitialSelHists.add(entity.get());
			} else {
//				listKrcmtTaskInitialSelHists.remove(taskIntialSelHist);
				this.commandProxy().insert(taskIntialSelHist);
			}			
			
		});

		
		this.commandProxy().updateAll(krcmtTaskInitialSelHists);

	}

	@Override
	public void delete(String empId) {
		this.getEntityManager().createQuery(DELETE_BY_SID , KrcmtTaskInitialSelHist.class)
				.setParameter("empId", empId)
				.executeUpdate();
		
	}

	@Override
	public Optional<TaskInitialSelHist> getById(String empId) {
		List<TaskInitialSel> data = this.queryProxy().query(SELECT_BY_SID ,  KrcmtTaskInitialSelHist.class )
				.setParameter("empId", empId)
				.getList().stream().map(c -> new TaskInitialSel(c.pk.sID,
						new DatePeriod(c.pk.startDate, c.endDate) ,
						 new TaskItem(Optional.ofNullable(new TaskCode(c.taskCd1)),
								 Optional.ofNullable(new TaskCode(c.taskCd2)),
								 Optional.ofNullable(new TaskCode(c.taskCd3)),
								 Optional.ofNullable(new TaskCode(c.taskCd4)),
								 Optional.ofNullable(new TaskCode(c.taskCd5)))))
				.collect(Collectors.toList());
					
		return data.isEmpty()? Optional.empty():Optional.of(new TaskInitialSelHist(data.get(0).getEmpID(), data));
	}

	@Override
	public Optional<TaskItem> getByBaseDate(String empId, GeneralDate baseDate) {
		List<TaskInitialSel> data = this.queryProxy().query(SELECT_BY_BASE_DATE ,  KrcmtTaskInitialSelHist.class )
				.setParameter("empId", empId)
				.setParameter("baseDate", baseDate)
				.getList().stream().map(c -> new TaskInitialSel(c.pk.sID,
						new DatePeriod(c.pk.startDate, c.endDate) ,
						 new TaskItem(Optional.ofNullable(new TaskCode(c.taskCd1)),
								 Optional.ofNullable(new TaskCode(c.taskCd2)),
								 Optional.ofNullable(new TaskCode(c.taskCd3)),
								 Optional.ofNullable(new TaskCode(c.taskCd4)),
								 Optional.ofNullable(new TaskCode(c.taskCd5)))))
				.collect(Collectors.toList());
		List<TaskItem>  lst =  data.stream().map(c -> c.getTaskItem()).collect(Collectors.toList());
		if(lst.isEmpty()){
			return Optional.empty();
		}
		return Optional.of(lst.get(0));
	}

	@Override
	public List<TaskInitialSelHist> getByCid(String companyId) {
		Map<String, List<TaskInitialSel>> data = this.queryProxy().query(SELECT_BY_CID ,  KrcmtTaskInitialSelHist.class )
				.setParameter("companyId", companyId)
				.getList().stream().map(c -> new TaskInitialSel(c.pk.sID,
						new DatePeriod(c.pk.startDate, c.endDate) ,
						 new TaskItem(Optional.ofNullable(new TaskCode(c.taskCd1)),
								 Optional.ofNullable(new TaskCode(c.taskCd2)),
								 Optional.ofNullable(new TaskCode(c.taskCd3)),
								 Optional.ofNullable(new TaskCode(c.taskCd4)),
								 Optional.ofNullable(new TaskCode(c.taskCd5)))))
				.collect(Collectors.groupingBy(i->i.getEmpID()));
		List<TaskInitialSelHist> lst = new ArrayList<>();
				data.forEach((k,v) -> lst.add(new TaskInitialSelHist(k, v)));
		return lst;
	}
}
