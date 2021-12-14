package nts.uk.screen.com.app.find.equipment.achievement.ac;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.query.pub.equipment.achievement.EquipmentUsageSettingsPub;

@Stateless
public class EquipmentUsageSettingsAdapterImpl implements EquipmentUsageSettingsAdapter {

	@Inject
	private EquipmentUsageSettingsPub pub;
	
	@Override
	public EquipmentUsageSettingsImport findSettings() {
		return EquipmentUsageSettingsImport.fromExport(this.pub.findSettings());
	}
}
