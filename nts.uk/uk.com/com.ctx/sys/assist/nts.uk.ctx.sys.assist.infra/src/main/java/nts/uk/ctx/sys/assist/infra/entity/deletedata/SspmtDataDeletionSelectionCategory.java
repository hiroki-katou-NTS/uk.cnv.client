package nts.uk.ctx.sys.assist.infra.entity.deletedata;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.assist.dom.deletedata.DataDeletionSelectionCategory;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * データ削除の選択カテゴリ
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SSPMT_SELECTED_DELCATE")
public class SspmtDataDeletionSelectionCategory extends UkJpaEntity implements Serializable,
														   					   DataDeletionSelectionCategory.MementoGetter,
														   					   DataDeletionSelectionCategory.MementoSetter {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SspmtDataDeletionSelectionCategoryPK pk;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "CONTRACT_CD", referencedColumnName = "CONTRACT_CD", insertable = false, updatable = false),
			@JoinColumn(name = "PATTERN_ATR", referencedColumnName = "PATTERN_ATR", insertable = false, updatable = false),
			@JoinColumn(name = "PATTERN_CD", referencedColumnName = "PATTERN_CD", insertable = false, updatable = false) })
	public SspmtDataDeletionPatternSetting patternSetting;

	@Override
	public void setCategoryId(String categoryId) {
		if (pk == null)
			pk = new SspmtDataDeletionSelectionCategoryPK();
		pk.categoryId = categoryId;
	}

	@Override
	public void setPatternCode(String patternCode) {
		if (pk == null)
			pk = new SspmtDataDeletionSelectionCategoryPK();
		pk.patternCode = patternCode;
	}

	@Override
	public void setPatternClassification(int patternClassification) {
		if (pk == null)
			pk = new SspmtDataDeletionSelectionCategoryPK();
		pk.patternClassification = patternClassification;
	}

	@Override
	public void setContractCode(String contractCode) {
		if (pk == null)
			pk = new SspmtDataDeletionSelectionCategoryPK();
		pk.contractCode = contractCode;
	}

	@Override
	public String getCategoryId() {
		if (pk != null)
			return pk.categoryId;
		return null;
	}

	@Override
	public String getPatternCode() {
		if (pk != null)
			return pk.patternCode;
		return null;
	}

	@Override
	public int getPatternClassification() {
		if (pk != null)
			return pk.patternClassification;
		return 0;
	}

	@Override
	public String getContractCode() {
		if (pk != null)
			return pk.contractCode;
		return null;
	}

	@Override
	protected Object getKey() {
		return pk;
	}

	@Override
	public void setSystemType(int systemType) {
		if (pk == null)
			pk = new SspmtDataDeletionSelectionCategoryPK();
		pk.systemType = systemType;
	}

	@Override
	public int getSystemType() {
		if (pk != null)
			return pk.systemType;
		return 0;
	}
}
