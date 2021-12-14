package nts.uk.ctx.at.function.infra.entity.holidaysremaining;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 出力する特別休暇: 主キー情報
 */

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KfnmtSpecialHolidayPk implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会社ID
	 */
	@Basic(optional = false)
	@Column(name = "CID")
	public String cid;

	/**
	 * コード
	 */
	@Basic(optional = false)
	@Column(name = "CD")
	public String cd;

	/**
	 * コード
	 */
	@Basic(optional = false)
	@Column(name = "SPECIAL_CD")
	public int specialCd;

}
