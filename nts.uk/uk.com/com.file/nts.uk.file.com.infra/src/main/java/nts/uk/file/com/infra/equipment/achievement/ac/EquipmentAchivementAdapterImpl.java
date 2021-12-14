package nts.uk.file.com.infra.equipment.achievement.ac;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageSettingsAdapter;
import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageSettingsImport;
import nts.uk.query.pub.equipment.achievement.EquipmentUsageSettingsPub;

@Stateless
public class EquipmentAchivementAdapterImpl implements EquipmentUsageSettingsAdapter {

	@Inject
	private EquipmentUsageSettingsPub pub;
	
	@Override
	public EquipmentUsageSettingsImport findSettings() {
		return EquipmentUsageSettingsImport.fromExport(this.pub.findSettings());
	}
}
