package nts.uk.ctx.at.record.dom.workrecord.operationsetting;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

@Getter
public class OperationOfDailyPerformance extends AggregateRoot{
	
	private CompanyId companyId;
	
	private FunctionalRestriction functionalRestriction;
	
	private DisplayRestriction displayRestriction;
	
	private SettingUnit settingUnit;
	
	private RegisterTotaltimeCheer comment;

}
