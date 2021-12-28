package nts.uk.ctx.exio.infra.entity.exo.outputitem;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * カテゴリ項目: 主キー情報
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OiomtCtgItemPk implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * カテゴリ項目NO
	 */
	@Basic(optional = false)
	@Column(name = "CTG_ITEM_NO")
	public String ctgItemNo;

	/**
	 * 会社ID
	 */
	@Basic(optional = false)
	@Column(name = "CID")
	public String cid;

	/**
	 * 出力項目コード
	 */
	@Basic(optional = false)
	@Column(name = "OUT_ITEM_CD")
	public String outItemCd;

	/**
	 * 条件設定コード
	 */
	@Basic(optional = false)
	@Column(name = "CND_SET_CD")
	public String condSetCd;
	
	/**
	 * 順序
	 */
	@Basic(optional = false)
	@Column(name = "DISPLAY_ORDER")
	public int displayOrder;

}