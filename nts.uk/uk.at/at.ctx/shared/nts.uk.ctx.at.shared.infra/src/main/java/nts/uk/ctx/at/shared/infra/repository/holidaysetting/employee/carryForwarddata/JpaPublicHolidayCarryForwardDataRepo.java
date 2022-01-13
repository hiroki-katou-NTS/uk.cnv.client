package nts.uk.ctx.at.shared.infra.repository.holidaysetting.employee.carryForwarddata;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata.KshdtHdpubRem;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata.KshdtHdpubRemPK;

@Stateless
public class JpaPublicHolidayCarryForwardDataRepo extends JpaRepository implements PublicHolidayCarryForwardDataRepository{
	
	private static final String GET_ALL_BY_SID = "SELECT a FROM KshdtHdpubRem a WHERE a.pk.employeeId IN :emplyeeIds";
	
	private static final String REMOVE_BY_SID = "DELETE FROM KshdtHdpubRem a"
			+ " WHERE a.pk.employeeId = :employeeId";
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#get(java.lang.String)
	 */
	@Override
	public Optional<PublicHolidayCarryForwardData> get(String employeeId){
		KshdtHdpubRemPK pk = new KshdtHdpubRemPK(employeeId);
		
		Optional<KshdtHdpubRem> entityOpt = this.queryProxy().find(pk, KshdtHdpubRem.class);
		
		if (entityOpt.isPresent()) {
				return Optional.of(PublicHolidayCarryForwardData.createFromJavaType(
						entityOpt.get().getPk().employeeId,
						entityOpt.get().getCarriedforward(),
						entityOpt.get().getRegisterType()));

		}
		return Optional.empty();
	}
	
	@Override
	public List<PublicHolidayCarryForwardData> getAll(List<String> employeeIds) {
		List<PublicHolidayCarryForwardData> resultList = new ArrayList<>();
		if (CollectionUtil.isEmpty(employeeIds)) return Collections.emptyList();
		resultList.addAll(this.queryProxy().query(GET_ALL_BY_SID,KshdtHdpubRem.class)
				.setParameter("emplyeeIds", employeeIds)
				.getList(entity -> PublicHolidayCarryForwardData.createFromJavaType(
						entity.getPk().employeeId,
						entity.getCarriedforward(),
						entity.getRegisterType())));
		
		return resultList;
	}

	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#persistAndUpdate(nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData)
	 */
	@Override
	public void persistAndUpdate(PublicHolidayCarryForwardData domain){
		KshdtHdpubRemPK pk = new KshdtHdpubRemPK(domain.getEmployeeId());
		
		Optional<KshdtHdpubRem> entityOpt = this.queryProxy().find(pk, KshdtHdpubRem.class);
		
		if (entityOpt.isPresent()) {
			entityOpt.get().fromDomainForUpdate(domain);
			this.commandProxy().update(entityOpt.get());
			this.getEntityManager().flush();
			return;
		}

		KshdtHdpubRem entity = new KshdtHdpubRem();
		entity.fromDomainForInsert(domain);
		this.commandProxy().insert(entity);
		this.getEntityManager().flush();
	}
	
	
	@Override
	public void addAll(List<PublicHolidayCarryForwardData> domains) {
		List<KshdtHdpubRem> entitys = new ArrayList<>();
		
		for (PublicHolidayCarryForwardData domain : domains) {
			KshdtHdpubRem entity = new KshdtHdpubRem();
			entity.fromDomainForInsert(domain);
			entitys.add(entity);
		}
		this.commandProxy().insertAll(entitys);
		this.getEntityManager().flush();
		
	}
	
	
	@Override
	public void updateAll(List<PublicHolidayCarryForwardData> domains) {
		List<KshdtHdpubRem> entitys = new ArrayList<>();
		
		for (PublicHolidayCarryForwardData domain : domains) {
			KshdtHdpubRem entity = new KshdtHdpubRem();
			entity.fromDomainForInsert(domain);
			entitys.add(entity);
		}
		this.commandProxy().updateAll(entitys);
		this.getEntityManager().flush();
		
	}
	
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardDataRepository#remove(nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData)
	 */
	@Override
	public void remove(PublicHolidayCarryForwardData domain) {

		KshdtHdpubRemPK pk = new KshdtHdpubRemPK(domain.getEmployeeId());

		this.commandProxy().remove(KshdtHdpubRem.class, pk);
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
