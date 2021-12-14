package nts.uk.screen.com.app.find.equipment.classification;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.inject.Inject;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentClassificationScreenQuery {

	@Inject
	private EquipmentClassificationRepository equipmentClassificationRepository;
	
	public List<EquipmentClassificationDto> getAll() {
		return this.equipmentClassificationRepository.getAll(AppContexts.user().contractCode())
				.stream().map(data -> new EquipmentClassificationDto(data.getCode().v(), data.getName().v()))
				.collect(Collectors.toList());
	}
}
