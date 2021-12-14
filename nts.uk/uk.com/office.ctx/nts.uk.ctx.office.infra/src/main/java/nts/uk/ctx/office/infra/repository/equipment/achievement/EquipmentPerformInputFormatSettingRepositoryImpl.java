package nts.uk.ctx.office.infra.repository.equipment.achievement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.office.dom.equipment.achievement.DisplayWidth;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDisplay;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormatSettingRepository;
import nts.uk.ctx.office.infra.entity.equipment.achievement.OfimtEquipmentDayFormat;
import nts.uk.ctx.office.infra.entity.equipment.achievement.OfimtEquipmentDayFormatPK;

@Stateless
public class EquipmentPerformInputFormatSettingRepositoryImpl extends JpaRepository
		implements EquipmentFormatSettingRepository {

	private static final String SELECT_BY_CID = "SELECT t FROM OfimtEquipmentDayFormat t " + "WHERE t.pk.cid = :cid";

	private EquipmentPerformInputFormatSetting toDomain(List<OfimtEquipmentDayFormat> entities) {
		String cid = entities.get(0).getPk().getCid();
		List<ItemDisplay> itemDisplaySettings = entities.stream()
				.map(data -> new ItemDisplay(new DisplayWidth(data.getDisplayWidth()), data.getDisplayOrder(),
						new EquipmentItemNo(String.valueOf(data.getPk().getItemNo()))))
				.collect(Collectors.toList());
		return new EquipmentPerformInputFormatSetting(cid, itemDisplaySettings);
	}

	private List<OfimtEquipmentDayFormat> toEntities(EquipmentPerformInputFormatSetting domain) {
		return domain.getItemDisplaySettings().stream()
				.map(data -> new OfimtEquipmentDayFormat(
						new OfimtEquipmentDayFormatPK(domain.getCid(), Integer.valueOf(data.getItemNo().v())),
						data.getDisplayOrder(), data.getDisplayWidth().v()))
				.collect(Collectors.toList());
	}

	@Override
	public void insert(EquipmentPerformInputFormatSetting domain) {
		this.commandProxy().insertAll(this.toEntities(domain));
	}

	@Override
	public void delete(String cid) {
		List<OfimtEquipmentDayFormat> entities = this.queryProxy()
				.query(SELECT_BY_CID, OfimtEquipmentDayFormat.class)
				.setParameter("cid", cid)
				.getList();
		this.commandProxy().removeAll(entities);
		this.getEntityManager().flush();
	}

	@Override
	public Optional<EquipmentPerformInputFormatSetting> get(String cid) {
		List<OfimtEquipmentDayFormat> entities = this.queryProxy().query(SELECT_BY_CID, OfimtEquipmentDayFormat.class)
				.setParameter("cid", cid).getList();
		if (entities.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(this.toDomain(entities));
	}

	@Override
	public void insertAfterDelete(EquipmentPerformInputFormatSetting domain) {
		this.delete(domain.getCid());
		this.insert(domain);
	}

}
