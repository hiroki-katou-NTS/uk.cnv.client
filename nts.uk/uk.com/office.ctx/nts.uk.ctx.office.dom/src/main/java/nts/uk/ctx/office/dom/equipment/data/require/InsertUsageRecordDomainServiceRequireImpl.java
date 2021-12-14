package nts.uk.ctx.office.dom.equipment.data.require;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.ctx.office.dom.equipment.data.domainservice.InsertUsageRecordDomainService;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;

@AllArgsConstructor
public class InsertUsageRecordDomainServiceRequireImpl implements InsertUsageRecordDomainService.Require {

	@Inject
	private EquipmentInformationRepository equipmentInformationRepository;
	
	@Inject
	private EquipmentRecordItemSettingRepository equipmentRecordItemSettingRepository;
	
	@Inject
	private EquipmentDataRepository equipmentDataRepository;
	
	@Override
	public Optional<EquipmentInformation> getEquipmentInfo(String cid, EquipmentCode equipmentCode) {
		return this.equipmentInformationRepository.findByPk(cid, equipmentCode.v());
	}

	@Override
	public List<EquipmentUsageRecordItemSetting> getItemSettings(String cid, List<EquipmentItemNo> itemNos) {
		return this.equipmentRecordItemSettingRepository.findByCidAndItemNos(cid, itemNos.stream()
				.map(EquipmentItemNo::v).collect(Collectors.toList()));
	}

	@Override
	public void insertEquipmentData(EquipmentData domain) {
		this.equipmentDataRepository.insert(domain);
	}

	@Override
	public Optional<EquipmentUsageRecordItemSetting> getItemSetting(String cid, String itemNo) {
		return this.equipmentRecordItemSettingRepository.findByCidAndItemNo(cid, itemNo);
	}
	
}
