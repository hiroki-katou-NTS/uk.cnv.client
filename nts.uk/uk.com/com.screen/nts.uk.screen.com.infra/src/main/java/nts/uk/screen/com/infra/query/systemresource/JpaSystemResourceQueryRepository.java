/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.infra.query.systemresource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.screen.com.app.repository.systemresource.SystemResourceData;
import nts.uk.screen.com.app.repository.systemresource.SystemResourceQueryRepository;
import nts.uk.shr.infra.i18n.resource.data.CisctI18NResource;
import nts.uk.shr.infra.i18n.resource.data.CismtI18NResourceCus;
import nts.uk.shr.infra.i18n.resource.data.CismtI18NResourceCusPK;


/**
 * The Class JpaSystemResourceQueryRepository.
 */
@Stateless
public class JpaSystemResourceQueryRepository extends JpaRepository implements SystemResourceQueryRepository {
	
	/** The Constant QUERY_STRING. */
	private static final String QUERY_STRING_CUS = "SELECT rs From CismtI18NResourceCus rs "
			                                   + "WHERE rs.pk.companyId = :companyid AND rs.pk.languageId = :languageId AND rs.pk.resourceId IN :resourceIdList";
	
	/** The Constant QUERY_STRING. */
	private static final String QUERY_STRING = "SELECT rs From CisctI18NResource rs "
			                                   + "WHERE rs.pk.resourceId IN :resourceIdList";

	/* (non-Javadoc)
	 * @see nts.uk.screen.com.app.repository.systemresource.SystemResourceQueryRepository#findListResource()
	 */
	@Override
	public List<SystemResourceData> findListResourceCus(String companyId, String languageId) {
		
		List<String> list = Arrays.asList("Com_Person", "Com_Employment", "Com_Class", "Com_Jobtitle",
										  "Com_Department", "Com_Workplace", "Com_EquipmentClass", "Com_EquipmentItem", "Com_Office", "Com_Company",
										  "Com_Contract", "Com_User", "Com_Project", "Com_AdHocWork",
										  "Com_BindingTime", "Com_AttendanceDays", "Com_AbsenceDays", "Com_PaidHoliday",
										  "Com_FundedPaidHoliday", "Com_SubstituteWork", "Com_CompensationHoliday", "Com_ExsessHoliday",
										  "Com_PlanedPaidHoliday","Com_SubstituteHoliday", "Workplace_Group",
										  "Work_Name", "Work_Name1", "Work_Name2", "Work_Name3", "Work_Name4", "Work_Name5");
		
		List<CismtI18NResourceCus> listResource = this.queryProxy().query(QUERY_STRING_CUS, CismtI18NResourceCus.class)
										.setParameter("languageId", languageId)
										.setParameter("companyid", companyId)
										.setParameter("resourceIdList", list).getList();
		
		List<SystemResourceData> result =  listResource.stream().map(e -> this.toDataCus(e)).collect(Collectors.toList());
		
		return result;
	}
	

	/* (non-Javadoc)
	 * @see nts.uk.screen.com.app.repository.systemresource.SystemResourceQueryRepository#findListResource(java.lang.String)
	 */
	@Override
	public List<SystemResourceData> findListResource() {
		List<String> list = Arrays.asList("Com_Person", "Com_Employment", "Com_Class", "Com_Jobtitle",
				  "Com_Department", "Com_Workplace", "Com_EquipmentClass", "Com_EquipmentItem", "Com_Office", "Com_Company",
				  "Com_Contract", "Com_User", "Com_Project", "Com_AdHocWork",
				  "Com_BindingTime", "Com_AttendanceDays", "Com_AbsenceDays", "Com_PaidHoliday",
				  "Com_FundedPaidHoliday", "Com_SubstituteWork", "Com_CompensationHoliday", "Com_ExsessHoliday",
				  "Com_PlanedPaidHoliday","Com_SubstituteHoliday","Workplace_Group",
				  "Work_Name", "Work_Name1", "Work_Name2", "Work_Name3", "Work_Name4", "Work_Name5");

		List<CisctI18NResource> listResource = this.queryProxy().query(QUERY_STRING, CisctI18NResource.class)
						.setParameter("resourceIdList", list).getList();
		
		List<SystemResourceData> result =  listResource.stream().map(e -> this.toData(e)).collect(Collectors.toList());
		
		return result;
	} 

	/* (non-Javadoc)
	 * @see nts.uk.screen.com.app.repository.systemresource.SystemResourceQueryRepository#update(nts.uk.screen.com.app.systemresource.dto.SystemResourceDto)
	 */
	@Override
	public void update(CismtI18NResourceCus entity){
		this.commandProxy().update(entity);
	}

	/* (non-Javadoc)
	 * @see nts.uk.screen.com.app.repository.systemresource.SystemResourceQueryRepository#findByResourceId(java.lang.String)
	 */
	@Override
	public Optional<CismtI18NResourceCus> findByResourceId(String companyId, String resourceId) {
		return this.queryProxy().find(CismtI18NResourceCusPK.createForAllPrograms(companyId,"ja" , "C" , resourceId), CismtI18NResourceCus.class);
	}
	
	/**
	 * To data.
	 *
	 * @param entity the entity
	 * @return the system resource data
	 */
	private SystemResourceData toData(CisctI18NResource entity){
		SystemResourceData data = SystemResourceData.builder()
			.resourceContent(entity.content)
			.resourceId(entity.pk.resourceId).build();
		
		return data;
	}
	
	/**
	 * To data cus.
	 *
	 * @param entity the entity
	 * @return the system resource data
	 */
	private SystemResourceData toDataCus(CismtI18NResourceCus entity){
		SystemResourceData data = SystemResourceData.builder()
			.resourceContent(entity.content)
			.resourceId(entity.pk.resourceId).build();
		
		return data;
	}

}
