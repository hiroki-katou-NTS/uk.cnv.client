package nts.uk.screen.com.app.find.equipment.information;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentInformationScreenQuery {

	@Inject
	private EquipmentInformationRepository equipmentInformationRepository;
	
	@Inject
	private EquipmentClassificationRepository equipmentClassificationRepository;
	
	/**
	 * UKDesign.UniversalK.オフィス.OEM_設備マスタ.OEM002_設備の登録.A：設備の登録.メニュー別OCD.設備一覧を取得する
	 */
	public EquipmentInformationStartupDto getStartupList() {
		// 1. get*(ログイン会社ID)
		List<EquipmentInformation> equipmentInformations = this.equipmentInformationRepository
				.findByCid(AppContexts.user().companyId());
		List<EquipmentClassification> equipmentClassifications = Collections.emptyList();
		// 2. 設備情報. isPresent
		if (!equipmentInformations.isEmpty()) {
			List<String> clsCodes = equipmentInformations.stream()
					.map(data -> data.getEquipmentClsCode().v()).distinct().collect(Collectors.toList());
			// get(契約コード、List<設備分類コード>)
			equipmentClassifications = this.equipmentClassificationRepository
					.getFromClsCodeList(AppContexts.user().contractCode(), clsCodes);
		}
		equipmentInformations.sort(Comparator.comparing(data -> ((EquipmentInformation) data).getEquipmentClsCode().v())
				.thenComparing(data -> ((EquipmentInformation) data).getEquipmentCode().v()));
		return EquipmentInformationStartupDto.fromDomains(equipmentInformations, equipmentClassifications);
	}
	
	/**
	 * UKDesign.UniversalK.オフィス.OEM_設備マスタ.OEM002_設備の登録.A：設備の登録.メニュー別OCD.設備情報を表示する
	 */
	public EquipmentInformationDto getEquipmentInfo(String code) {
		return this.equipmentInformationRepository.findByPk(AppContexts.user().companyId(), code)
				.map(EquipmentInformationDto::fromDomain)
				.orElse(null);
	}
}
