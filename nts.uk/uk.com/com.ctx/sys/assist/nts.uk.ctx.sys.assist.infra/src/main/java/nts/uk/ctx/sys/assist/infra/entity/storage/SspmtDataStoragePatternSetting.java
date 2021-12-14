package nts.uk.ctx.sys.assist.infra.entity.storage;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.assist.dom.storage.DataStoragePatternSetting;
import nts.uk.ctx.sys.assist.dom.storage.DataStorageSelectionCategory;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * データ保存のパターン設定
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SSPMT_DATASTO_PATTERN_SET")
public class SspmtDataStoragePatternSetting extends UkJpaEntity
		implements Serializable, DataStoragePatternSetting.MementoGetter, DataStoragePatternSetting.MementoSetter {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public SspmtDataStoragePatternSettingPk pk;

	// column 排他バージョン
	@Version
	@Column(name = "EXCLUS_VER")
	private long version;

	/**
	 * パターン名
	 */
	@Basic(optional = false)
	@Column(name = "PATTERN_NAME")
	public String patternName;

	/**
	 * 調査用保存の識別
	 */
	@Basic(optional = false)
	@Column(name = "IDEN_SURVEY_ARCH")
	public int idenSurveyArch;

	/**
	 * パスワード有無
	 */
	@Basic(optional = false)
	@Column(name = "WITHOUT_PASSWORD")
	public int withoutPassword;

	/**
	 * 日次参照年
	 */
	@Basic(optional = true)
	@Column(name = "DAILY_REFER_YEAR")
	public Integer dailyReferYear;

	/**
	 * 日次参照月
	 */
	@Basic(optional = true)
	@Column(name = "DAILY_REFER_MONTH")
	public Integer dailyReferMonth;

	/**
	 * 月次参照年
	 */
	@Basic(optional = true)
	@Column(name = "MONTH_REFER_YEAR")
	public Integer monthlyReferYear;

	/**
	 * 月次参照月
	 */
	@Basic(optional = true)
	@Column(name = "MONTH_REFER_MONTH")
	public Integer monthlyReferMonth;

	/**
	 * 年次参照月
	 */
	@Basic(optional = true)
	@Column(name = "ANNUAL_REFER_YEAR")
	public Integer annualReferYear;

	/**
	 * パターン圧縮パスワード
	 */
	@Basic(optional = true)
	@Column(name = "PATTERN_COMPRE_PASS")
	public String patternCompressionPwd;

	/**
	 * パターン補足説明
	 */
	@Basic(optional = true)
	@Column(name = "PATTERN_SUPLE_EXPLAN")
	public String patternSuppleExplanation;

	/**
	 * データ保存の選択カテゴリ
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "patternSetting", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SSPMT_DATASTO_SELECT_CATE")
	public List<SspmtDataStorageSelectionCategory> categories;

	@Override
	protected Object getKey() {
		return pk;
	}

	@Override
	public void setPatternCode(String patternCode) {
		if (pk == null)
			pk = new SspmtDataStoragePatternSettingPk();
		this.pk.patternCode = patternCode;
	}

	@Override
	public void setPatternClassification(int patternClassification) {
		if (pk == null)
			pk = new SspmtDataStoragePatternSettingPk();
		this.pk.patternClassification = patternClassification;
	}

	@Override
	public void setContractCode(String contractCode) {
		if (pk == null)
			pk = new SspmtDataStoragePatternSettingPk();
		this.pk.contractCode = contractCode;
	}

	@Override
	public void setCategories(List<DataStorageSelectionCategory> categories) {
		this.categories = categories.stream().map(domain -> {
			SspmtDataStorageSelectionCategory entity = new SspmtDataStorageSelectionCategory();
			domain.setMemento(entity);
			return entity;
		}).collect(Collectors.toList());
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
	public List<DataStorageSelectionCategory> getCategories() {
		return this.categories.stream().map(DataStorageSelectionCategory::createFromMemento)
				.collect(Collectors.toList());
	}
}