package nts.uk.ctx.sys.assist.infra.entity.storage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class SspmtDataStoragePatternSettingPk implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 解約コード
	 */
	@NonNull
	@Column(name = "CONTRACT_CD")
	public String contractCode;

	/**
	 * パターン区分
	 */
	@Column(name = "PATTERN_ATR")
	public int patternClassification;
	
	/**
	 * パターンコード
	 */
	@NonNull
	@Column(name = "PATTERN_CD")
	public String patternCode;
}