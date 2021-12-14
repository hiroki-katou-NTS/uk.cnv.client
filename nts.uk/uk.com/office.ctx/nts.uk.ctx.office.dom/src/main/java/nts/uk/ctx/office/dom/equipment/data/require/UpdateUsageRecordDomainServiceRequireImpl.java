package nts.uk.ctx.office.dom.equipment.data.require;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.ctx.office.dom.equipment.data.domainservice.UpdateUsageRecordDomainService;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;

@AllArgsConstructor
public class UpdateUsageRecordDomainServiceRequireImpl implements UpdateUsageRecordDomainService.Require {
	
	@Inject
	private EquipmentRecordItemSettingRepository equipmentRecordItemSettingRepository;
	
	@Inject
	private EquipmentDataRepository equipmentDataRepository;
	
	@Override
	public List<EquipmentUsageRecordItemSetting> getItemSettings(String cid, List<EquipmentItemNo> itemNos) {
		return this.equipmentRecordItemSettingRepository.findByCidAndItemNos(cid, itemNos.stream()
				.map(EquipmentItemNo::v).collect(Collectors.toList()));
	}

	@Override
	public Optional<EquipmentData> getEquipmentData(String cid, EquipmentCode equipmentCode, GeneralDate useDate,
			String sid, GeneralDateTime inputDate) {
		return this.equipmentDataRepository.findByUsageInfo(cid, equipmentCode.v(), useDate, sid, inputDate);
	}

	@Override
	public void updateEquipmentData(EquipmentData domain) {
		this.equipmentDataRepository.update(domain);
	}
	
}
