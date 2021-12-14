package nts.uk.ctx.sys.assist.infra.entity.deletedata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SspmtDataDeletionPatternSettingPK implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *  契約コード
	 */
	@NonNull
	@Column(name = "CONTRACT_CD")
	public String contractCode;
	
	/**
	 * パターンコード
	 */
	@NonNull
	@Column(name = "PATTERN_CD")
	public String patternCode;
	
	/**
	 * パターン区分
	 */
	@Column(name = "PATTERN_ATR")
	public int patternClassification;
}
