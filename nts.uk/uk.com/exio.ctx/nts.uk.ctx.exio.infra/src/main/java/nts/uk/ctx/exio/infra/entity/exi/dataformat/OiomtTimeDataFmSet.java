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

import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.dom.exi.dataformat.ItemType;
import nts.uk.ctx.exio.dom.exi.dataformat.TimeDataFormatSet;
import nts.uk.ctx.exio.dom.exi.item.StdAcceptItem;
import nts.uk.ctx.exio.infra.entity.exi.item.OiomtExAcItem;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 時間型データ形式設定
 */

@NoArgsConstructor
@Entity
@Table(name = "OIOMT_EX_AC_FM_TI")
public class OiomtTimeDataFmSet extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@EmbeddedId
	public OiomtTimeDataFmSetPk timeDatFmSetPk;
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
	public int fixedValue;

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
	public int effectiveDigitLength;

	/**
	 * 端数処理
	 */
	@Basic(optional = false)
	@Column(name = "ROUND_PROC")
	public int roundProc;

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
		return timeDatFmSetPk;
	}

	public OiomtTimeDataFmSet(String cid, String conditionCode, int acceptItemNum, int delimiterSet,
			int fixedValue, int hourMinSelect, int effectiveDigitLength, int roundProc, int decimalSelect,
			Integer valueOfFixedValue, Integer startDigit, Integer endDigit, Integer roundProcCls) {
		super();
		this.timeDatFmSetPk = new OiomtTimeDataFmSetPk(cid, conditionCode, acceptItemNum);
		this.delimiterSet = delimiterSet;
		this.fixedValue = fixedValue;
		this.hourMinSelect = hourMinSelect;
		this.effectiveDigitLength = effectiveDigitLength;
		this.roundProc = roundProc;
		this.decimalSelect = decimalSelect;
		this.valueOfFixedValue = valueOfFixedValue;
		this.startDigit = startDigit;
		this.endDigit = endDigit;
		this.roundProcCls = roundProcCls;
	}

	public static OiomtTimeDataFmSet fromDomain(StdAcceptItem item, TimeDataFormatSet domain) {
		return new OiomtTimeDataFmSet(item.getCid(), item.getConditionSetCd().v(),
				item.getAcceptItemNumber(), domain.getDelimiterSet().value, domain.getFixedValue().value,
				domain.getHourMinSelect().value, domain.getEffectiveDigitLength().value, domain.getRoundProc().value,
				domain.getDecimalSelect().value,
				domain.getValueOfFixedValue().isPresent() ? domain.getValueOfFixedValue().get().v() : null,
				domain.getStartDigit().isPresent() ? domain.getStartDigit().get().v() : null,
				domain.getEndDigit().isPresent() ? domain.getEndDigit().get().v() : null,
				domain.getRoundProcCls().isPresent() ? domain.getRoundProcCls().get().value : null);
	}

	public TimeDataFormatSet toDomain() {
		return new TimeDataFormatSet(ItemType.TIME.value, this.delimiterSet, this.fixedValue, this.hourMinSelect,
				this.effectiveDigitLength, this.roundProc, this.decimalSelect, this.valueOfFixedValue, this.startDigit,
				this.endDigit, this.roundProcCls);
	}

}
