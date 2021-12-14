package nts.uk.ctx.at.record.infra.repository.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.query.TypedQueryWrapper;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.infra.entity.daily.ouen.KrcdtDayOuenTimeSheet;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkinputRemarks;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class OuenWorkTimeSheetOfDailyRepoImpl extends JpaRepository implements OuenWorkTimeSheetOfDailyRepo {

	@Override
	public OuenWorkTimeSheetOfDaily find(String empId, GeneralDate ymd) {
		
		List<KrcdtDayOuenTimeSheet> entitis = queryProxy()
				.query("SELECT o FROM KrcdtDayOuenTimeSheet o WHERE o.pk.sid = :sid AND o.pk.ymd = :ymd", KrcdtDayOuenTimeSheet.class)
				.setParameter("sid", empId).setParameter("ymd", ymd).getList();
		
		if(entitis.isEmpty())
			return null;
		
		OuenWorkTimeSheetOfDaily rs = toDomain(entitis);
		
		return rs;
	}
	
	@Override
	public List<OuenWorkTimeSheetOfDaily> find(String sid, DatePeriod period) {
		
		List<KrcdtDayOuenTimeSheet> entitis = this.queryProxy().query("SELECT s FROM KrcdtDayOuenTimeSheet s WHERE s.pk.sid = :sid"
				+ " AND s.pk.ymd >= :start AND s.pk.ymd <= :end", KrcdtDayOuenTimeSheet.class)
				.setParameter("sid", sid)
				.setParameter("start", period.start())
				.setParameter("end", period.end())
				.getList();
		
		if(entitis.isEmpty())
			return new ArrayList<>();
		List<OuenWorkTimeSheetOfDaily> rs = new ArrayList<>();
		
		period.datesBetween().forEach(ymd -> {
			List<KrcdtDayOuenTimeSheet> entitisBySidAndDate = entitis.stream().filter(i -> i.pk.sid.equals(sid) && i.pk.ymd.equals(ymd)).collect(Collectors.toList());
			if(!entitisBySidAndDate.isEmpty()){
				rs.add(toDomain(entitisBySidAndDate));
				
			}
		});
		return rs;
	}
	
	@Override
	public boolean findPK(String empId, GeneralDate ymd, int ouenNo ) {
		
		Optional<KrcdtDayOuenTimeSheet> entitis = queryProxy()
				.query("SELECT o FROM KrcdtDayOuenTimeSheet o WHERE o.pk.sid = :sid AND o.pk.ymd = :ymd AND o.pk.ouenNo = :ouenNo", KrcdtDayOuenTimeSheet.class)
				.setParameter("sid", empId)
				.setParameter("ymd", ymd)
				.setParameter("ouenNo", ouenNo)
				.getSingle();
		
		if(entitis.isPresent())
			return true;
		
		return false;
	}
	
	public Optional<KrcdtDayOuenTimeSheet> getEntity(String empId, GeneralDate ymd, int ouenNo ) {
		Optional<KrcdtDayOuenTimeSheet> entitis = queryProxy()
				.query("SELECT o FROM KrcdtDayOuenTimeSheet o WHERE o.pk.sid = :sid AND o.pk.ymd = :ymd AND o.pk.ouenNo = :ouenNo", KrcdtDayOuenTimeSheet.class)
				.setParameter("sid", empId)
				.setParameter("ymd", ymd)
				.setParameter("ouenNo", ouenNo)
				.getSingle();
		
		return entitis;
	}

	@Override
	public void update(List<OuenWorkTimeSheetOfDaily> domain) {
		List<KrcdtDayOuenTimeSheet> lstEntity = new ArrayList<>();
		domain.stream().map(c -> KrcdtDayOuenTimeSheet.convert(c)).forEach(e -> {
			lstEntity.addAll(e);
		});
		
		lstEntity.forEach(i -> {
			Optional<KrcdtDayOuenTimeSheet> entityOld = getEntity(i.pk.sid, i.pk.ymd, i.pk.ouenNo);
			if(!entityOld.isPresent()){
				commandProxy().insert(i);
			} else{
				updateData(entityOld.get(), i);
				commandProxy().update(entityOld.get());
			}
		});
	}

	@Override
	public void insert(List<OuenWorkTimeSheetOfDaily> domain) {
		if(domain.isEmpty()) return;
		List<KrcdtDayOuenTimeSheet> lstEntity = new ArrayList<>();
		domain.stream().map(c -> KrcdtDayOuenTimeSheet.convert(c)).forEach(e -> {
			lstEntity.addAll(e);
		});
		lstEntity.forEach(i -> {
		OuenWorkTimeSheetOfDaily lstDomainOld = this.find(domain.get(0).getEmpId(), domain.get(0).getYmd());
		if(lstDomainOld != null) {
			List<OuenWorkTimeSheetOfDailyAttendance> dataOld = lstDomainOld.getOuenTimeSheet().stream().filter(x -> {
				return x.getWorkNo() != i.pk.ouenNo;
			}).collect(Collectors.toList());
			
			if(!dataOld.isEmpty()) {
				for (OuenWorkTimeSheetOfDailyAttendance atd : dataOld) {
					this.removePK(i.pk.sid, i.pk.ymd, atd.getWorkNo());
				}
			}
		}
		});
		this.update(domain);
	}

	private void updateData(KrcdtDayOuenTimeSheet entityOld, KrcdtDayOuenTimeSheet dataUpdate) {
		entityOld.startTime = dataUpdate.startTime;
		entityOld.endTime = dataUpdate.endTime;
		entityOld.workCd1 = StringUtil.isNullOrEmpty(dataUpdate.workCd1, true) ? null : dataUpdate.workCd1;
		entityOld.workCd2 = dataUpdate.workCd2;
		entityOld.workCd3 = dataUpdate.workCd3;
		entityOld.workCd4 = dataUpdate.workCd4;
		entityOld.workCd5 = dataUpdate.workCd5;
		entityOld.workLocationCode = dataUpdate.workLocationCode;
		entityOld.workplaceId      = dataUpdate.workplaceId;
	}

	@Override
	public void delete(List<OuenWorkTimeSheetOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTimeSheet.convert(c)).forEach(e -> {
			commandProxy().remove(e);
		});
	}
	
	public OuenWorkTimeSheetOfDaily toDomain(List<KrcdtDayOuenTimeSheet> es) {
		List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet = es.stream().map(ots -> OuenWorkTimeSheetOfDailyAttendance.create(
				ots.pk.ouenNo, 
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(new WorkplaceId(ots.workplaceId), new WorkLocationCD(ots.workLocationCode)), 
						Optional.of(WorkGroup.create(ots.workCd1, ots.workCd2, ots.workCd3, ots.workCd4, ots.workCd5)),
						StringUtil.isNullOrEmpty(ots.workRemarks, true) ? Optional.empty() : Optional.of(new WorkinputRemarks(ots.workRemarks))), 
				TimeSheetOfAttendanceEachOuenSheet.create(
						new WorkNo(ots.workNo), 
						Optional.of(new WorkTimeInformation(
									new ReasonTimeChange(
											ots.startTimeChangeWay == null ? TimeChangeMeans.REAL_STAMP : EnumAdaptor.valueOf(ots.startTimeChangeWay, TimeChangeMeans.class), 
											ots.startStampMethod == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(ots.startStampMethod, EngravingMethod.class))), 
									ots.startTime == null ? null : new TimeWithDayAttr(ots.startTime))), 
						Optional.of(new WorkTimeInformation(
									new ReasonTimeChange(
											ots.endTimeChangeWay == null ? TimeChangeMeans.REAL_STAMP : EnumAdaptor.valueOf(ots.endTimeChangeWay, TimeChangeMeans.class), 
											ots.endStampMethod == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(ots.endStampMethod, EngravingMethod.class))), 
									ots.endTime == null ? null : new TimeWithDayAttr(ots.endTime)))))).collect(Collectors.toList());
		
		return OuenWorkTimeSheetOfDaily.create(es.get(0).pk.sid, es.get(0).pk.ymd, ouenTimeSheet);
	}

	@Override
	public void remove(String sid, GeneralDate ymd) {
		String delete = "delete from KrcdtDayOuenTimeSheet o " + " where o.pk.sid = :sid "
				+ " and o.pk.ymd = :ymd ";
		this.getEntityManager().createQuery(delete).setParameter("sid", sid)
												   .setParameter("ymd", ymd)
												   .executeUpdate();
	}
	
	@Override
	public void removePK(String sid, GeneralDate ymd, int ouenNo) {
		String delete = "delete from KrcdtDayOuenTimeSheet o " + " where o.pk.sid = :sid "
				+ " and o.pk.ymd = :ymd "
				+ " and o.pk.ouenNo = :ouenNo ";
		this.getEntityManager().createQuery(delete).setParameter("sid", sid)
												   .setParameter("ymd", ymd)
												   .setParameter("ouenNo", ouenNo)
												   .executeUpdate();
	}

	@Override
	public List<OuenWorkTimeSheetOfDaily> find(Map<String, List<GeneralDate>> param) {
		List<KrcdtDayOuenTimeSheet> supports = new ArrayList<>();
		String query = new StringBuilder("SELECT sp FROM KrcdtDayOuenTimeSheet sp")
								.append(" WHERE sp.pk.sid IN :sid")
								.append(" AND sp.pk.ymd IN :date")
								.toString();
		TypedQueryWrapper<KrcdtDayOuenTimeSheet> tpQuery = queryProxy().query(query, KrcdtDayOuenTimeSheet.class);
		CollectionUtil.split(param, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, p -> {
			supports.addAll(tpQuery.setParameter("sid", p.keySet())
					.setParameter("date", p.values().stream().flatMap(List::stream).collect(Collectors.toSet()))
					.getList().stream()
					.collect(Collectors.toList()));
		});
		
		List<OuenWorkTimeSheetOfDaily> domains = new ArrayList<>();
		param.forEach((sid, dates) -> {
			dates.forEach(date -> {
				List<KrcdtDayOuenTimeSheet> supportsByEmp = supports.stream().filter(c -> c.pk.sid.equals(sid) && c.pk.ymd.equals(date)).collect(Collectors.toList());
				if (!supportsByEmp.isEmpty()) {
					domains.add(toDomain(supportsByEmp));
				}
			});
		});

		return domains;
	}

	@Override
	public List<OuenWorkTimeSheetOfDaily> find(List<String> sid, DatePeriod ymd) {
		List<KrcdtDayOuenTimeSheet> entitis = new ArrayList<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, p -> {
			entitis.addAll(this.queryProxy().query("SELECT s FROM KrcdtDayOuenTimeSheet s WHERE s.pk.sid IN :sid"
				+ " AND s.pk.ymd >= :start AND s.pk.ymd <= :end", KrcdtDayOuenTimeSheet.class)
				.setParameter("sid", p)
				.setParameter("start", ymd.start())
				.setParameter("end", ymd.end())
				.getList()
			);
		});
		if(entitis.isEmpty())
			return new ArrayList<>();
		List<OuenWorkTimeSheetOfDaily> rs = new ArrayList<>();
		sid.forEach(id -> {
			ymd.datesBetween().forEach(item -> {
				List<KrcdtDayOuenTimeSheet> entitisBySidAndDate = entitis.stream().filter(i -> i.pk.sid.equals(id) && i.pk.ymd.equals(item)).collect(Collectors.toList());
				if(!entitisBySidAndDate.isEmpty()){
					rs.add(toDomain(entitisBySidAndDate));
				}
			});
		});
		return rs;
	}

}
