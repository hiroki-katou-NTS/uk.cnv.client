package nts.uk.ctx.pereg.ac.info.setting.event.no19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.dom.event.DomainEventSubscriber;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultipleDomainEvent;
import nts.uk.ctx.pereg.dom.person.info.category.IsAbolition;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCategoryRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.item.PerInfoItemDefRepositoty;
import nts.uk.ctx.pereg.dom.person.info.item.PersonInfoItemDefinition;
import nts.uk.shr.com.context.AppContexts;

/**
 * Event:複数回勤務項目の廃止区分の切り替え WorkManagementMultipleDomainEvent
 * 
 * @author lanlt
 *
 */
@Stateless
public class WorkTimesMultipleEventSubcriber implements DomainEventSubscriber<WorkManagementMultipleDomainEvent>{
	@Inject
	private PerInfoCategoryRepositoty ctgRepo;

	@Inject
	private PerInfoItemDefRepositoty itemRepo;

	private static final String ctgCodeCS00020 = "CS00020";
	private static final String ctgCodeCS00070 = "CS00070";

	private static final List<String> itemCdLstOfCS00020 = Arrays.asList(new String[] { "IS00135", "IS00136", "IS00137",
			"IS00144", "IS00145", "IS00146", "IS00162", "IS00163", "IS00164", "IS00171", "IS00172", "IS00173",
			"IS00180", "IS00181", "IS00182", "IS00153", "IS00154", "IS00155"});
	
	private static final List<String> itemCdLstOfCS00070 = Arrays.asList(new String[] {"IS00198", "IS00199", "IS00200",
			"IS00207", "IS00208", "IS00209", "IS00216", "IS00217", "IS00218", "IS00225", "IS00226", "IS00227",
			"IS00234", "IS00235", "IS00236", "IS00243", "IS00244", "IS00245", "IS00189", "IS00190", "IS00191" });

	@Override
	public Class<WorkManagementMultipleDomainEvent> subscribedToEventType() {
		return WorkManagementMultipleDomainEvent.class;
	}

	@Override
	public void handle(WorkManagementMultipleDomainEvent domainEvent) {
//		List<PersonInfoItemDefinition> itemUpdateLst = new ArrayList<>();
//		itemUpdateLst.addAll(setAbolition( getItemLst() , domainEvent.isUseAtr()));
//		if (itemUpdateLst.size() > 0)
//			this.itemRepo.updateAbolitionItem(itemUpdateLst);
	}
	
	private List<PersonInfoItemDefinition> getItemLst() {
		String companyLogin = AppContexts.user().companyId();
		Optional<PersonInfoCategory> CS00020 = ctgRepo.getPerInfoCategoryByCtgCD(ctgCodeCS00020, companyLogin);
		Optional<PersonInfoCategory> CS00070 = ctgRepo.getPerInfoCategoryByCtgCD(ctgCodeCS00070, companyLogin);
		List<PersonInfoItemDefinition> itemLst = new ArrayList<>();
		if (CS00020.isPresent()) {
			itemLst.addAll(this.itemRepo.getAllItemId(Arrays.asList(new String[] {(CS00020.get().getPersonInfoCategoryId())}), itemCdLstOfCS00020));
		}
		if (CS00070.isPresent()) {
			itemLst.addAll(this.itemRepo.getAllItemId(Arrays.asList(new String[] {(CS00070.get().getPersonInfoCategoryId())}), itemCdLstOfCS00070));
		}
		return itemLst;
	}
	
	private  List<PersonInfoItemDefinition> setAbolition(List<PersonInfoItemDefinition> itemLst, boolean params ){
		List<PersonInfoItemDefinition> itemUpdateLst = new ArrayList<>();
		if (itemLst.size() > 0) {
				itemUpdateLst.addAll(itemLst.stream().map(c -> {
					c.setIsAbolition(params == true? IsAbolition.NOT_ABOLITION: IsAbolition.ABOLITION);
					return c;
				}).collect(Collectors.toList()));
		}
		return itemUpdateLst;
	}


}
