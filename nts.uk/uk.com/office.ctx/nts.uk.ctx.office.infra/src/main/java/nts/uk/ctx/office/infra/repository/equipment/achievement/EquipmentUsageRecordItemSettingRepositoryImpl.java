package nts.uk.ctx.office.infra.repository.equipment.achievement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.office.dom.equipment.achievement.DigitsNumber;
import nts.uk.ctx.office.dom.equipment.achievement.DisplayOfItems;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDescription;
import nts.uk.ctx.office.dom.equipment.achievement.ItemInputControl;
import nts.uk.ctx.office.dom.equipment.achievement.MaximumUsageRecord;
import nts.uk.ctx.office.dom.equipment.achievement.MinimumUsageRecord;
import nts.uk.ctx.office.dom.equipment.achievement.UsageItemName;
import nts.uk.ctx.office.dom.equipment.achievement.UsageRecordUnit;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.ctx.office.infra.entity.equipment.achievement.OfimtEquipmentDayItem;
import nts.uk.ctx.office.infra.entity.equipment.achievement.OfimtEquipmentDayItemPK;

@Stateless
public class EquipmentUsageRecordItemSettingRepositoryImpl extends JpaRepository
		implements EquipmentRecordItemSettingRepository {

	private static final String SELECT_BY_CID = "SELECT t FROM OfimtEquipmentDayItem t " + "WHERE t.pk.cid = :cid";

	private static final String SELECT_BY_CID_AND_ITEM_NOS = "SELECT t FROM OfimtEquipmentDayItem t "
			+ "WHERE t.pk.cid = :cid " + "AND t.pk.itemNo IN :itemNos";

	private EquipmentUsageRecordItemSetting toDomain(OfimtEquipmentDayItem entity) {
		ItemInputControl inputControl = new ItemInputControl(
				EnumAdaptor.valueOf(entity.getItemCls(), ItemClassification.class), entity.getRequire() == 1,
				Optional.ofNullable(entity.getItemLength()).map(Integer::valueOf).map(DigitsNumber::new),
				Optional.ofNullable(entity.getMaxValue()).map(MaximumUsageRecord::new),
				Optional.ofNullable(entity.getMinValue()).map(MinimumUsageRecord::new));
		DisplayOfItems items = new DisplayOfItems(new UsageItemName(entity.getItemName()),
				Optional.ofNullable(entity.getUnit()).map(UsageRecordUnit::new),
				Optional.ofNullable(entity.getMemo()).map(ItemDescription::new));
		return new EquipmentUsageRecordItemSetting(entity.getPk().getCid(),
				new EquipmentItemNo(String.valueOf(entity.getPk().getItemNo())), inputControl, items);
	}

	private OfimtEquipmentDayItem toEntity(EquipmentUsageRecordItemSetting domain) {
		OfimtEquipmentDayItem entity = new OfimtEquipmentDayItem();
		OfimtEquipmentDayItemPK pk = new OfimtEquipmentDayItemPK(domain.getCid(),
				Integer.valueOf(domain.getItemNo().v()));
		entity.setPk(pk);
		entity.setItemCls(domain.getInputcontrol().getItemCls().value);
		entity.setItemLength(domain.getInputcontrol().getDigitsNo().map(DigitsNumber::v).orElse(null));
		entity.setItemName(domain.getItems().getItemName().v());
		entity.setMaxValue(domain.getInputcontrol().getMaximum().map(MaximumUsageRecord::v).orElse(null));
		entity.setMinValue(domain.getInputcontrol().getMinimum().map(MinimumUsageRecord::v).orElse(null));
		entity.setMemo(domain.getItems().getMemo().map(ItemDescription::v).orElse(null));
		entity.setRequire(domain.getInputcontrol().isRequire() ? 1 : 0);
		entity.setUnit(domain.getItems().getUnit().map(UsageRecordUnit::v).orElse(null));
		return entity;
	}

	@Override
	public void insertAll(List<EquipmentUsageRecordItemSetting> domains) {
		this.commandProxy().insertAll(domains.stream().map(this::toEntity).collect(Collectors.toList()));
	}

	@Override
	public void deleteAll(String cid) {
		List<OfimtEquipmentDayItem> entities = this.queryProxy()
				.query(SELECT_BY_CID, OfimtEquipmentDayItem.class).setParameter("cid", cid)
				.getList();
		this.commandProxy().removeAll(entities);
		this.getEntityManager().flush();
	}

	@Override
	public List<EquipmentUsageRecordItemSetting> findByCid(String cid) {
		return this.queryProxy().query(SELECT_BY_CID, OfimtEquipmentDayItem.class).setParameter("cid", cid)
				.getList(this::toDomain);
	}

	@Override
	public Optional<EquipmentUsageRecordItemSetting> findByCidAndItemNo(String cid, String itemNo) {
		return this.queryProxy()
				.find(new OfimtEquipmentDayItemPK(cid, Integer.valueOf(itemNo)), OfimtEquipmentDayItem.class)
				.map(this::toDomain);
	}

	@Override
	public List<EquipmentUsageRecordItemSetting> findByCidAndItemNos(String cid, List<String> itemNos) {
		
		if (CollectionUtil.isEmpty(itemNos)) return Collections.emptyList();
		
		return this.queryProxy().query(SELECT_BY_CID_AND_ITEM_NOS, OfimtEquipmentDayItem.class).setParameter("cid", cid)
				.setParameter("itemNos", itemNos).getList(this::toDomain);
	}

	@Override
	public void insertAllAfterDeleteAll(String cid, List<EquipmentUsageRecordItemSetting> domains) {
		this.deleteAll(cid);
		this.insertAll(domains);
	}

}
