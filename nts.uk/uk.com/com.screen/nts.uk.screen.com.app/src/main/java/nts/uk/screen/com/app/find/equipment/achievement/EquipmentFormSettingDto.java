package nts.uk.screen.com.app.find.equipment.achievement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormTitle;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentFormSettingDto {

	// 会社ID
	private String cid;

	// 帳票タイトル
	private String title;
	
	public static EquipmentFormSettingDto fromDomain(EquipmentFormSetting domain) {
		return new EquipmentFormSettingDto(domain.getCid(), domain.getTitle().v());
	}
	
	public EquipmentFormSetting toDomain() {
		return new EquipmentFormSetting(cid, new EquipmentFormTitle(title));
	}
}
