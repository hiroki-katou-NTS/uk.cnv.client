package nts.uk.ctx.sys.assist.infra.entity.storage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.NonNull;

@Embeddable
public class SspmtDataStorageSelectionCategoryPk implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 契約コード
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
	
	/**
	 * カテゴリID
	 */
	@NonNull
	@Column(name = "CATEGORY_ID")
	public String categoryId;
	
	/**
	 * システム種類
	 */
	@Column(name = "SYSTEM_TYPE")
	public int systemType;
}
