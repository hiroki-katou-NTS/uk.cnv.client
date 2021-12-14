package nts.uk.ctx.at.shared.infra.repository.holidaysetting.employee.carryForwarddata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata.KshdtHdpubRem;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata.KshdtHdpubRemPK;

@Stateless
public class JpaPublicHolidayCarryForwardDataRepo extends JpaRepository implements PublicHolidayCarryForwardDataRepository{
	
	private static final String GET_BY_SID = "SELECT a FROM KshdtHdpubRem a WHERE a.pk.employeeId = :employeeId order by a.pk.tagetmonth DESC";

	private static final String REMOVE_BY_SID_YM = "DELETE FROM KshdtHdpubRem a"
			+ " WHERE a.pk.employeeId = :employeeId"
			+" AND a.pk.tagetmonth >= :tagetmonth ";
	
	
	private static final String REMOVE_BY_SID = "DELETE FROM KshdtHdpubRem a"
			+ " WHERE a.pk.employeeId = :employeeId";
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#get(java.lang.String)
	 */
	@Override
	public List<PublicHolidayCarryForwardData> get(String employeeId){
		List<KshdtHdpubRem> entities = this.queryProxy()
				.query(GET_BY_SID, KshdtHdpubRem.class).setParameter("employeeId", employeeId)
				.getList();
		
		List<PublicHolidayCarryForwardData> list = new ArrayList<PublicHolidayCarryForwardData>();
		
		
		for( KshdtHdpubRem x : entities) {
			list.add( PublicHolidayCarryForwardData.createFromJavaType(
						x.getPk().employeeId,
						x.getPk().tagetmonth,
						x.getDeadline(),
						x.getCarriedforward(),
						x.getRegisterType()));

		}
		return list;
	}
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#persistAndUpdate(nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData)
	 */
	@Override
	public void persistAndUpdate(PublicHolidayCarryForwardData domain){
		KshdtHdpubRemPK pk = new KshdtHdpubRemPK(domain.getEmployeeId(),domain.getYearMonth().v());
		
		Optional<KshdtHdpubRem> entityOpt = this.queryProxy().find(pk, KshdtHdpubRem.class);
		
		if (entityOpt.isPresent()) {
			entityOpt.get().fromDomainForUpdate(domain);
			this.commandProxy().update(entityOpt.get());
			this.getEntityManager().flush();
			return;
		}

		KshdtHdpubRem entity = new KshdtHdpubRem();
		entity.fromDomainForUpdate(domain);
		this.commandProxy().insert(entity);
		this.getEntityManager().flush();
	}
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#remove(nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData)
	 */
	@Override
	public void remove(PublicHolidayCarryForwardData domain) {

		KshdtHdpubRemPK pk = new KshdtHdpubRemPK(domain.getEmployeeId(),domain.getYearMonth().v());

		this.commandProxy().remove(KshdtHdpubRem.class, pk);
	}
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#deletePublicHolidayCarryForwardDataAfter(java.lang.String, nts.arc.time.YearMonth)
	 */
	@Override
	public void deleteThisMonthAfter(String employeeId, YearMonth yearMonth){
		this.queryProxy().query(REMOVE_BY_SID_YM, KshdtHdpubRem.class)
		.setParameter("employeeId", employeeId)
		.setParameter("tagetmonth", yearMonth);
	}
	
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#deletePublicHolidayCarryForwardData(java.lang.String)
	 */
	@Override
	public void delete(String employeeId){
		this.queryProxy().query(REMOVE_BY_SID, KshdtHdpubRem.class)
		.setParameter("employeeId", employeeId);
	}

}
