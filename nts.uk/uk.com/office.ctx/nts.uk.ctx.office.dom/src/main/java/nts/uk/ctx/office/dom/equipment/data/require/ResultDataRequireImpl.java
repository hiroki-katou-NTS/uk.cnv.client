package nts.uk.ctx.office.dom.equipment.data.require;

import java.util.Optional;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.ctx.office.dom.equipment.data.ResultData;

@AllArgsConstructor
public class ResultDataRequireImpl implements ResultData.Require {
	
	@Inject
	private EquipmentRecordItemSettingRepository repository;

	@Override
	public Optional<EquipmentUsageRecordItemSetting> getItemSetting(String cid, String itemNo) {
		return this.repository.findByCidAndItemNo(cid, itemNo);
	}

}
