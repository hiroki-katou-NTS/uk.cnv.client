package nts.uk.ctx.at.function.infra.entity.holidaysremaining;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 出力する特別休暇: 主キー情報
 */

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KfnmtRptHdRemainHdspPk implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 出力レイアウトID : 休暇残数管理表の出力項目設定.会社ID
	 */
	@Basic(optional = false)
	@Column(name = "LAYOUT_ID")
	public String layoutId;

	/**
	 * コード
	 */
	@Basic(optional = false)
	@Column(name = "SPECIAL_CD")
	public int specialCd;

}
