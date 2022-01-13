package nts.uk.ctx.exio.infra.entity.exi.dataformat;

import java.io.Serializable;

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
import nts.uk.ctx.exio.dom.exi.dataformat.InsTimeDatFmSet;
import nts.uk.ctx.exio.dom.exi.dataformat.ItemType;
import nts.uk.ctx.exio.dom.exi.item.StdAcceptItem;
import nts.uk.ctx.exio.infra.entity.exi.item.OiomtExAcItem;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 時刻型データ形式設定
 */

@NoArgsConstructor
@Entity
@Table(name = "OIOMT_EX_AC_FM_TIME")
public class OiomtExAcFmTime extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public OiomtInsTimeDatFmSetPk insTimeDatFmSetPk;
	/**	契約コード */
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	/**
	 * 区切り文字設定
	 */
	@Basic(optional = false)
	@Column(name = "DELIMITER_SET")
	public int delimiterSet;

	/**
	 * 固定値
	 */
	@Basic(optional = false)
	@Column(name = "FIXED_VALUE")
	public boolean fixedValue;

	/**
	 * 時分/分選択
	 */
	@Basic(optional = false)
	@Column(name = "HOUR_MIN_SELECT")
	public int hourMinSelect;

	/**
	 * 有効桁長
	 */
	@Basic(optional = false)
	@Column(name = "EFFECTIVE_DIGIT_LENGTH")
	public boolean effectiveDigitLength;

	/**
	 * 端数処理
	 */
	@Basic(optional = false)
	@Column(name = "ROUND_PROC")
	public boolean roundProc;

	/**
	 * 進数選択
	 */
	@Basic(optional = false)
	@Column(name = "DECIMAL_SELECT")
	public int decimalSelect;

	/**
	 * 固定値の値
	 */
	@Basic(optional = true)
	@Column(name = "VALUE_OF_FIXED_VALUE")
	public Integer valueOfFixedValue;

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
	 * 端数処理区分
	 */
	@Basic(optional = true)
	@Column(name = "ROUND_PROC_CLS")
	public Integer roundProcCls;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
			@JoinColumn(name = "CONDITION_SET_CD", referencedColumnName = "CONDITION_SET_CD", insertable = false, updatable = false),
			@JoinColumn(name = "ACCEPT_ITEM_NUM", referencedColumnName = "ACCEPT_ITEM_NUMBER", insertable = false, updatable = false) })
	public OiomtExAcItem acceptItem;

	@Override
	protected Object getKey() {
		return insTimeDatFmSetPk;
	}

	public OiomtExAcFmTime(String cid,  String conditionCode, int acceptItemNum, int delimiterSet,
			int fixedValue, int hourMinSelect, int effectiveDigitLength, int roundProc, int decimalSelect,
			Integer valueOfFixedValue, Integer startDigit, Integer endDigit, Integer roundProcCls) {
		super();
		this.insTimeDatFmSetPk = new OiomtInsTimeDatFmSetPk(cid, conditionCode, acceptItemNum);
		this.delimiterSet = delimiterSet;
		this.fixedValue = BooleanUtils.toBoolean(fixedValue);
		this.hourMinSelect = hourMinSelect;
		this.effectiveDigitLength = BooleanUtils.toBoolean(effectiveDigitLength);
		this.roundProc = BooleanUtils.toBoolean(roundProc);
		this.decimalSelect = decimalSelect;
		this.valueOfFixedValue = valueOfFixedValue;
		this.startDigit = startDigit;
		this.endDigit = endDigit;
		this.roundProcCls = roundProcCls;
	}

	public static OiomtExAcFmTime fromDomain(StdAcceptItem item, InsTimeDatFmSet domain) {
		return new OiomtExAcFmTime(item.getCid(), item.getConditionSetCd().v(),
				item.getAcceptItemNumber(), domain.getDelimiterSet().value, domain.getFixedValue().value,
				domain.getHourMinSelect().value, domain.getEffectiveDigitLength().value, domain.getRoundProc().value,
				domain.getDecimalSelect().value,
				domain.getValueOfFixedValue().isPresent() ? domain.getValueOfFixedValue().get().v() : null,
				domain.getStartDigit().isPresent() ? domain.getStartDigit().get().v() : null,
				domain.getEndDigit().isPresent() ? domain.getEndDigit().get().v() : null,
				domain.getRoundProcCls().isPresent() ? domain.getRoundProcCls().get().value : null);
	}

	public InsTimeDatFmSet toDomain() {
		return new InsTimeDatFmSet(ItemType.INS_TIME.value, this.delimiterSet, BooleanUtils.toInteger(this.fixedValue), this.hourMinSelect,
				BooleanUtils.toInteger(this.effectiveDigitLength), BooleanUtils.toInteger(this.roundProc), this.decimalSelect, this.valueOfFixedValue, this.startDigit,
				this.endDigit, this.roundProcCls);
	}

}
