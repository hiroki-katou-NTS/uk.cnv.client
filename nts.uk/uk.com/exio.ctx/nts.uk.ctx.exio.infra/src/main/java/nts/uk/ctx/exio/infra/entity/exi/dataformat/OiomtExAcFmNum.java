package nts.uk.ctx.exio.infra.entity.exi.dataformat;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.dom.exi.dataformat.ItemType;
import nts.uk.ctx.exio.dom.exi.dataformat.NumDataFormatSet;
import nts.uk.ctx.exio.dom.exi.item.StdAcceptItem;
import nts.uk.ctx.exio.infra.entity.exi.item.OiomtExAcItem;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 数値型データ形式設定
 */

@NoArgsConstructor
@Entity
@Table(name = "OIOMT_EX_AC_FM_NUM")
public class OiomtExAcFmNum extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public OiomtNumDataFormatSetPk numDataFormatSetPk;
	/**	契約コード */
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	/**
	 * 固定値
	 */
	@Basic(optional = false)
	@Column(name = "FIXED_VALUE")
	public boolean fixedValue;

	/**
	 * 小数区分
	 */
	@Basic(optional = false)
	@Column(name = "DECIMAL_DIVISION")
	public boolean decimalDivision;

	/**
	 * 有効桁長
	 */
	@Basic(optional = false)
	@Column(name = "EFFECTIVE_DIGIT_LENGTH")
	public boolean effectiveDigitLength;

	/**
	 * コード変換コード
	 */
	@Basic(optional = true)
	@Column(name = "CD_CONVERT_CD")
	public String cdConvertCd;

	/**
	 * 固定値の値
	 */
	@Basic(optional = true)
	@Column(name = "VALUE_OF_FIXED_VALUE")
	public BigDecimal valueOfFixedValue;

	/**
	 * 少数桁数
	 */
	@Basic(optional = true)
	@Column(name = "DECIMAL_DIGIT_NUM")
	public Integer decimalDigitNum;

	/**
	 * 有効桁数開始桁
	 */
	@Basic(optional = true)
	@Column(name = "START_DIGIT")
	public Integer startDigit;

	/**
	 * 有効桁数終了桁
	 */
	@Basic(optional = true)
	@Column(name = "END_DIGIT")
	public Integer endDigit;

	/**
	 * 小数点区分
	 */
	@Basic(optional = true)
	@Column(name = "DECIMAL_POINT_CLS")
	public boolean decimalPointCls;

	/**
	 * 小数端数
	 */
	@Basic(optional = true)
	@Column(name = "DECIMAL_FRACTION")
	public Integer decimalFraction;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
			@JoinColumn(name = "CONDITION_SET_CD", referencedColumnName = "CONDITION_SET_CD", insertable = false, updatable = false),
			@JoinColumn(name = "ACCEPT_ITEM_NUM", referencedColumnName = "ACCEPT_ITEM_NUMBER", insertable = false, updatable = false) })
	public OiomtExAcItem acceptItem;

	@Override
	protected Object getKey() {
		return numDataFormatSetPk;
	}

	public OiomtExAcFmNum(String cid, String conditionCode, int acceptItemNum, int fixedValue,
			int decimalDivision, int effectiveDigitLength, String cdConvertCd, BigDecimal valueOfFixedValue,
			Integer decimalDigitNum, Integer startDigit, Integer endDigit, Integer decimalPointCls,
			Integer decimalFraction) {
		super();
		this.numDataFormatSetPk = new OiomtNumDataFormatSetPk(cid, conditionCode, acceptItemNum);
		this.fixedValue = BooleanUtils.toBoolean(fixedValue);
		this.decimalDivision = BooleanUtils.toBoolean(decimalDivision);
		this.effectiveDigitLength = BooleanUtils.toBoolean(effectiveDigitLength);
		this.cdConvertCd = cdConvertCd;
		this.valueOfFixedValue = valueOfFixedValue;
		this.decimalDigitNum = decimalDigitNum;
		this.startDigit = startDigit;
		this.endDigit = endDigit;
		this.decimalPointCls = BooleanUtils.toBoolean(decimalPointCls);
		this.decimalFraction = decimalFraction;
	}

	public static OiomtExAcFmNum fromDomain(StdAcceptItem item, NumDataFormatSet domain) {
		return new OiomtExAcFmNum(item.getCid(), item.getConditionSetCd().v(),
				item.getAcceptItemNumber(), domain.getFixedValue().value, domain.getDecimalDivision().value,
				domain.getEffectiveDigitLength().value,
				domain.getCdConvertCd().isPresent() ? domain.getCdConvertCd().get().v() : null,
				domain.getValueOfFixedValue().isPresent() ? domain.getValueOfFixedValue().get().v() : null,
				domain.getDecimalDigitNum().isPresent() ? domain.getDecimalDigitNum().get().v() : null,
				domain.getStartDigit().isPresent() ? domain.getStartDigit().get().v() : null,
				domain.getEndDigit().isPresent() ? domain.getEndDigit().get().v() : null,
				domain.getDecimalPointCls().isPresent() ? new Integer(domain.getDecimalPointCls().get().value) : null,
				domain.getDecimalFraction().isPresent() ? new Integer(domain.getDecimalFraction().get().value) : null);
	}

	public NumDataFormatSet toDomain() {
		return new NumDataFormatSet(ItemType.NUMERIC.value, BooleanUtils.toInteger(this.fixedValue), BooleanUtils.toInteger(this.decimalDivision),
				BooleanUtils.toInteger(this.effectiveDigitLength), this.cdConvertCd, this.valueOfFixedValue, this.decimalDigitNum,
				this.startDigit, this.endDigit, BooleanUtils.toInteger(this.decimalPointCls), this.decimalFraction);
	}

}
