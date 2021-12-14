package nts.uk.file.com.infra.equipment.achievement;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.file.com.app.equipment.achievement.EquipmentUsageSettingsExportRepository;
import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageSettingsAdapter;
import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageSettingsImport;

@Stateless
public class EquipmentUsageSettingsExportRepositoryImpl implements EquipmentUsageSettingsExportRepository {

	@Inject
	private EquipmentUsageSettingsAdapter adapter;
	
	@Override
	public EquipmentUsageSettingsImport findSettings() {
		return this.adapter.findSettings();
	}
}
