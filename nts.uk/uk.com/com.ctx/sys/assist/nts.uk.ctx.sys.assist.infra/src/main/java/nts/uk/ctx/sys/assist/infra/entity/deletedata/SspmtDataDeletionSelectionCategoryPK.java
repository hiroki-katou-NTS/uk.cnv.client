package nts.uk.ctx.sys.assist.infra.entity.deletedata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.NonNull;

@Embeddable
public class SspmtDataDeletionSelectionCategoryPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * カテゴリID
	 */
	@NonNull
	@Column(name = "CATEGORY_ID")
	public String categoryId;
	
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
	
	/**
	 * 契約コード
	 */
	@NonNull
	@Column(name = "CONTRACT_CD")
	public String contractCode;
	
	/**
	 * システム種類
	 */
	@Column(name = "SYSTEM_TYPE")
	public int systemType;
}
