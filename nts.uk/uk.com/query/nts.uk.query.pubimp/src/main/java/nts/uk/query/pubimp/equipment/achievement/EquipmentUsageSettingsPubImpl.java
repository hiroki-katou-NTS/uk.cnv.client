package nts.uk.query.pubimp.equipment.achievement;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.query.app.equipment.achievement.EquipmentUsageSettingsQuery;
import nts.uk.query.pub.equipment.achievement.EquipmentUsageSettingsPub;
import nts.uk.query.pub.equipment.achievement.export.EquipmentUsageSettingsExport;

@Stateless
public class EquipmentUsageSettingsPubImpl implements EquipmentUsageSettingsPub {
	
	@Inject
	private EquipmentUsageSettingsQuery query;

	@Override
	public EquipmentUsageSettingsExport findSettings() {
		return EquipmentUsageSettingsExport.fromModel(this.query.findSettings());
	}
}
