package nts.uk.ctx.at.shared.infra.entity.calculation.holiday;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
/**
 * @author phongtq
 * フレックス勤務の設定
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHMT_CALC_C_FLEX")
public class KshmtCalcCFlex  extends ContractUkJpaEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/** 主キー */
	@EmbeddedId
	public KshstFlexSetPK kshstFlexSetPK;
	
	/** 不足計算 */
	@Column(name = "MISS_CALC_HD")
	public int missCalcHd;
	
	/** 割増計算 */
	@Column(name = "PREMIUM_CALC_HD")
	public int premiumCalcHd;
	
	/** 不足計算*/
	@Column(name = "MISS_CALC_SUBHD")
	public int missCalcSubhd;
	
	/** 割増計算 */
	@Column(name = "PREMIUM_CALC_SUBHD")
	public int premiumCalcSubhd;
	
	/** 法定労働控除時間計算 */
	@Column(name="FLEX_DEDUCT_CALC")
	public boolean flexDeductCalc;

	/** 非勤務日計算 */
	@Column(name="FLEX_NONWKING_CALC")
	public int flexNonwkingCalc;
	
	@Override
	protected Object getKey() {
		return kshstFlexSetPK;
	}
}
