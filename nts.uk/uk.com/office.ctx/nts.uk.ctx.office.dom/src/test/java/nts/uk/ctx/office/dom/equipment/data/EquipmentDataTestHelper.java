package nts.uk.ctx.office.dom.equipment.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;

public class EquipmentDataTestHelper {
	
	public static final String SID = "sid";
	public static final String CID = "cid";
	public static final String EQUIPMENT_CD = "00001";
	public static final String EQUIPMENT_CLS_CD = "0001";

	public static EquipmentData mockDomain(GeneralDate useDate) {
		List<ResultData> itemDatas = EquipmentDataTestHelper.mockItemDatas();
		return new EquipmentData(GeneralDateTime.now(), useDate, SID, new EquipmentCode(EQUIPMENT_CD),
				new EquipmentClassificationCode(EQUIPMENT_CLS_CD), itemDatas);
	}

	public static Map<EquipmentItemNo, ActualItemUsageValue> mockValueMap() {
		return EquipmentDataTestHelper.mockItemDatas().stream()
				.collect(Collectors.toMap(ResultData::getItemNo, data -> data.getActualValue().orElse(null)));
	}
	
	public static ResultData createItemData(String itemNo, ItemClassification itemCls, String value) {
		return new ResultData(new EquipmentItemNo(itemNo), itemCls, Optional.of(value).map(ActualItemUsageValue::new));
	}
	
	private static List<ResultData> mockItemDatas() {
		return Arrays.asList(
				EquipmentDataTestHelper.createItemData("1", ItemClassification.TEXT, "abc"),
				EquipmentDataTestHelper.createItemData("4", ItemClassification.NUMBER, "123"),
				EquipmentDataTestHelper.createItemData("7", ItemClassification.TIME, "600"));
	}
}
