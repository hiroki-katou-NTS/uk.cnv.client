/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.app.find.systemresource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.com.app.repository.systemresource.SystemResourceData;
import nts.uk.screen.com.app.repository.systemresource.SystemResourceQueryRepository;
import nts.uk.screen.com.app.systemresource.dto.SystemResourceDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class SystemResourceFinder.
 */
@Stateless
public class SystemResourceFinder {
	
	/** The repository. */
	@Inject
	private SystemResourceQueryRepository repository;
	
	
	/**
	 * Find list.
	 *
	 * @param listResourceId the list resource id
	 * @return the list
	 */
	public List<SystemResourceDto> findList(){
		
		String companyId = AppContexts.user().companyId();
		
		List<String> listResourceIds = Arrays.asList("Com_Person", "Com_Employment", "Com_Class", "Com_Jobtitle",
				  "Com_Department", "Com_Workplace", "Com_EquipmentClass", "Com_EquipmentItem", "Com_Office", "Com_Company",
				  "Com_Contract", "Com_User", "Com_Project", "Com_AdHocWork",
				  "Com_BindingTime", "Com_AttendanceDays", "Com_AbsenceDays", "Com_PaidHoliday",
				  "Com_FundedPaidHoliday", "Com_SubstituteWork", "Com_CompensationHoliday", "Com_ExsessHoliday",
				  "Com_PlanedPaidHoliday", "Com_SubstituteHoliday", "Workplace_Group",
				  "Work_Name", "Work_Name1", "Work_Name2", "Work_Name3", "Work_Name4", "Work_Name5");
		
		ArrayList<SystemResourceData> resultFinal = new ArrayList<SystemResourceData>();
		
		String languageId = AppContexts.user().language().basicLanguageId();
		
		// list data in resource cus
		List<SystemResourceData> resultCus = this.repository.findListResourceCus(companyId, languageId);
		
		//list resource default
		List<SystemResourceData> resultDefault = this.repository.findListResource();
		
		listResourceIds.stream().forEach(resourceId -> {
			//find in list resourse cus
			Optional<SystemResourceData> obj = resultCus.stream().filter(o -> o.getResourceId().equals(resourceId)).findFirst();
			
			// add to list result  final
			if (obj.isPresent()) {
				resultFinal.add(obj.get());
			} else {
				resultFinal.add(resultDefault.stream().filter(o -> o.getResourceId().equals(resourceId)).findFirst().get());
			}
		});
		
		List<SystemResourceDto> listDto = resultFinal.stream().map(e -> new SystemResourceDto(e.getResourceId(), e.getResourceContent())).collect(Collectors.toList());
		
		return listDto;
	}
}
