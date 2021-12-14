package nts.uk.ctx.office.dom.equipment.information;

import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備マスタ.設備情報
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EquipmentInformation extends AggregateRoot {
	
	/**
	 * コード
	 */
	private EquipmentCode equipmentCode;
	
	/**
	 * 名称
	 */
	private EquipmentName equipmentName;
	
	/**
	 * 有効期間
	 */
	private DatePeriod validPeriod;
	
	/**
	 * 備考
	 */
	private Optional<EquipmentRemark> equipmentRemark;
	
	/**
	 * 設備分類コード
	 */
	private EquipmentClassificationCode equipmentClsCode;
	
	// [1] 有効期限内か																							
	public boolean isValid(GeneralDate date) {
		return this.validPeriod.contains(date);
	}
	
	public static EquipmentInformation createFromMemento(MementoGetter mementoGetter) {
		EquipmentInformation domain = new EquipmentInformation();
		domain.getMemento(mementoGetter);
		return domain;
	}
	
	public void getMemento(MementoGetter mementoGetter) {
		this.equipmentCode = new EquipmentCode(mementoGetter.getCode());
		this.equipmentName = new EquipmentName(mementoGetter.getName());
		this.validPeriod = new DatePeriod(mementoGetter.getEffectiveStartDate(), mementoGetter.getEffectiveEndDate());
		this.equipmentRemark = Optional.ofNullable(mementoGetter.getRemark()).map(EquipmentRemark::new);
		this.equipmentClsCode = new EquipmentClassificationCode(mementoGetter.getEquipmentClsCode());
	}
	
	public void setMemento(MementoSetter mementoSetter) {
		mementoSetter.setCode(this.equipmentCode.v());
		mementoSetter.setName(this.equipmentName.v());
		mementoSetter.setEffectiveStartDate(this.validPeriod.start());
		mementoSetter.setEffectiveEndDate(this.validPeriod.end());
		mementoSetter.setRemark(this.equipmentRemark.map(EquipmentRemark::v).orElse(null));
		mementoSetter.setEquipmentClsCode(this.equipmentClsCode.v());
	}
	
	public interface MementoGetter {
		String getCode();
		String getName();
		GeneralDate getEffectiveStartDate();
		GeneralDate getEffectiveEndDate();
		String getRemark();
		String getEquipmentClsCode();
	}
	
	public interface MementoSetter {
		void setCode(String code);
		void setName(String name);
		void setEffectiveStartDate(GeneralDate startDate);
		void setEffectiveEndDate(GeneralDate endDate);
		void setRemark(String remark);
		void setEquipmentClsCode(String equipmentClsCode);
	}
}
