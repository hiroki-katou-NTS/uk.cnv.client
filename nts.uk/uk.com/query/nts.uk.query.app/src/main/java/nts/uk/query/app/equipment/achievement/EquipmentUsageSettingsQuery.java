package nts.uk.query.app.equipment.achievement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.office.dom.equipment.achievement.DigitsNumber;
import nts.uk.ctx.office.dom.equipment.achievement.DisplayOfItems;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDescription;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDisplay;
import nts.uk.ctx.office.dom.equipment.achievement.ItemInputControl;
import nts.uk.ctx.office.dom.equipment.achievement.MaximumUsageRecord;
import nts.uk.ctx.office.dom.equipment.achievement.MinimumUsageRecord;
import nts.uk.ctx.office.dom.equipment.achievement.UsageRecordUnit;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormatSettingRepository;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.query.model.equipment.achievement.DisplayOfItemsModel;
import nts.uk.query.model.equipment.achievement.EquipmentPerformInputFormatSettingModel;
import nts.uk.query.model.equipment.achievement.EquipmentUsageRecordItemSettingModel;
import nts.uk.query.model.equipment.achievement.EquipmentUsageSettingsModel;
import nts.uk.query.model.equipment.achievement.ItemDisplayModel;
import nts.uk.query.model.equipment.achievement.ItemInputControlModel;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.APP.設備利用実績設定の項目内容を取得する.設備利用実績設定の項目内容を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentUsageSettingsQuery {

	@Inject
	private EquipmentFormatSettingRepository formatSettingRepository;

	@Inject
	private EquipmentRecordItemSettingRepository itemSettingRepository;

	public EquipmentUsageSettingsModel findSettings() {
		String cid = AppContexts.user().companyId();
		// 1. get(ログイン会社ID)
		Optional<EquipmentPerformInputFormatSetting> optFormatSetting = this.formatSettingRepository.get(cid);
		// 2. [設備の実績入力フォーマット設定isPresent]
		if (optFormatSetting.isPresent()) {
			EquipmentPerformInputFormatSetting formatSetting = optFormatSetting.get();
			formatSetting.getItemDisplaySettings().sort(Comparator.comparing(ItemDisplay::getDisplayOrder));
			List<EquipmentUsageRecordItemSetting> itemSettings = formatSetting.getItemDisplaySettings().stream()
					.map(data -> this.itemSettingRepository.findByCidAndItemNo(cid, data.getItemNo().v()).orElse(null))
					.filter(Objects::nonNull).collect(Collectors.toList());
			return new EquipmentUsageSettingsModel(
					itemSettings.stream().map(this::convertItemSetting).collect(Collectors.toList()),
					this.convertFormatSetting(formatSetting));
		}
		return new EquipmentUsageSettingsModel(
				new ArrayList<EquipmentUsageRecordItemSettingModel>(),
				new EquipmentPerformInputFormatSettingModel("", new ArrayList<ItemDisplayModel>()));
	}

	private EquipmentPerformInputFormatSettingModel convertFormatSetting(EquipmentPerformInputFormatSetting domain) {
		List<ItemDisplayModel> itemDisplaySettings = domain.getItemDisplaySettings().stream()
				.map(data -> ItemDisplayModel.builder().displayOrder(data.getDisplayOrder())
						.displayWidth(data.getDisplayWidth().v()).itemNo(data.getItemNo().v()).build())
				.collect(Collectors.toList());
		return new EquipmentPerformInputFormatSettingModel(domain.getCid(), itemDisplaySettings);
	}

	private EquipmentUsageRecordItemSettingModel convertItemSetting(EquipmentUsageRecordItemSetting domain) {
		ItemInputControl icDomain = domain.getInputcontrol();
		DisplayOfItems doiDomain = domain.getItems();
		ItemInputControlModel inputControl = ItemInputControlModel.builder()
				.digitsNo(icDomain.getDigitsNo().map(DigitsNumber::v).orElse(null)).itemCls(icDomain.getItemCls().value)
				.require(icDomain.isRequire()).maximum(icDomain.getMaximum().map(MaximumUsageRecord::v).orElse(null))
				.minimum(icDomain.getMinimum().map(MinimumUsageRecord::v).orElse(null)).build();
		DisplayOfItemsModel items = DisplayOfItemsModel.builder().itemName(doiDomain.getItemName().v())
				.memo(doiDomain.getMemo().map(ItemDescription::v).orElse(null))
				.unit(doiDomain.getUnit().map(UsageRecordUnit::v).orElse(null)).build();
		return EquipmentUsageRecordItemSettingModel.builder().cid(domain.getCid()).inputControl(inputControl)
				.itemNo(domain.getItemNo().v()).items(items).build();
	}
}
