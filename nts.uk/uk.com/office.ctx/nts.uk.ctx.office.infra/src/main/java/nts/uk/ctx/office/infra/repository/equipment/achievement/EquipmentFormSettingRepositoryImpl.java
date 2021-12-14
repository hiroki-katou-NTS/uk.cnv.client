package nts.uk.ctx.office.infra.repository.equipment.achievement;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormTitle;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormSettingRepository;
import nts.uk.ctx.office.infra.entity.equipment.achievement.OfimtEquipmentDayRpt;

@Stateless
public class EquipmentFormSettingRepositoryImpl extends JpaRepository implements EquipmentFormSettingRepository {

	private EquipmentFormSetting toDomain(OfimtEquipmentDayRpt entity) {
		return new EquipmentFormSetting(entity.getCid(), new EquipmentFormTitle(entity.getReportTitle()));
	}

	private OfimtEquipmentDayRpt toEntity(EquipmentFormSetting domain) {
		return new OfimtEquipmentDayRpt(domain.getCid(), domain.getTitle().v());
	}

	@Override
	public void insert(EquipmentFormSetting domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void delete(String cid) {
		this.commandProxy().remove(OfimtEquipmentDayRpt.class, cid);
		this.getEntityManager().flush();
	}

	@Override
	public Optional<EquipmentFormSetting> findByCid(String cid) {
		return this.queryProxy().find(cid, OfimtEquipmentDayRpt.class).map(this::toDomain);
	}

	@Override
	public void insertAfterDelete(EquipmentFormSetting domain) {
		this.delete(domain.getCid());
		this.insert(domain);
	}

}
