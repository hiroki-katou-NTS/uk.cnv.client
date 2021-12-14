package nts.uk.ctx.office.infra.entity.equipment.data;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * UKDesign.データベース.ER図.オフィス支援.設備管理.設備利用実績データ.OFIDT_EQUIPMENT_DAY_ATD
 * 設備利用実績データ
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "OFIDT_EQUIPMENT_DAY_ATD")
public class OfidtEquipmentDayAtd extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private OfidtEquipmentDayAtdPK pk;
	
	/**
	 * 設備コード
	 */
	@NotNull
	@Column(name = "EQUIPMENT_CODE")
	private String equipmentCode;
	
	/**
	 * 利用日
	 */
	@NotNull
	@Column(name = "USE_DATE")
	private GeneralDate useDate;
	
	/**
	 * 設備分類コード
	 */
	@NotNull
	@Column(name = "EQUIPMENT_CLS_CODE")
	private String equipmentClsCode;
	
	/**
	 * 項目NO1の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_1")
	private String textValue1;
	
	/**
	 * 項目NO2の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_2")
	private String textValue2;

	/**
	 * 項目NO3の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_3")
	private String textValue3;
	
	/**
	 * 項目NO4の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_4")
	private String textValue4;
	
	/**
	 * 項目NO5の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_5")
	private String textValue5;
	
	/**
	 * 項目NO6の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_6")
	private String textValue6;
	
	/**
	 * 項目NO7の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_7")
	private String textValue7;
	
	/**
	 * 項目NO8の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_8")
	private String textValue8;
	
	/**
	 * 項目NO9の値（分類＝文字）
	 */
	@Basic(optional = true)
	@Column(name = "TEXT_VALUE_9")
	private String textValue9;
	
	/**
	 * 項目NO1の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_1")
	private Integer numberValue1;
	
	/**
	 * 項目NO2の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_2")
	private Integer numberValue2;
	
	/**
	 * 項目NO3の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_3")
	private Integer numberValue3;
	
	/**
	 * 項目NO4の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_4")
	private Integer numberValue4;
	
	/**
	 * 項目NO5の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_5")
	private Integer numberValue5;
	
	/**
	 * 項目NO6の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_6")
	private Integer numberValue6;
	
	/**
	 * 項目NO7の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_7")
	private Integer numberValue7;
	
	/**
	 * 項目NO8の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_8")
	private Integer numberValue8;
	
	/**
	 * 項目NO9の値（分類＝数字）
	 */
	@Basic(optional = true)
	@Column(name = "NUMBER_VALUE_9")
	private Integer numberValue9;
	
	/**
	 * 項目NO1の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_1")
	private Integer timeValue1;
	
	/**
	 * 項目NO2の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_2")
	private Integer timeValue2;
	
	/**
	 * 項目NO3の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_3")
	private Integer timeValue3;
	
	/**
	 * 項目NO4の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_4")
	private Integer timeValue4;
	
	/**
	 * 項目NO5の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_5")
	private Integer timeValue5;
	
	/**
	 * 項目NO6の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_6")
	private Integer timeValue6;
	
	/**
	 * 項目NO7の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_7")
	private Integer timeValue7;
	
	/**
	 * 項目NO8の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_8")
	private Integer timeValue8;
	
	/**
	 * 項目NO9の値（分類＝時間）
	 */
	@Basic(optional = true)
	@Column(name = "TIME_VALUE_9")
	private Integer timeValue9;
	
	@Override
	protected Object getKey() {
		return this.pk;
	}
}
