package nts.uk.ctx.sys.assist.infra.entity.deletedata;

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
import nts.uk.ctx.sys.assist.dom.deletedata.DataDeletionPatternSetting;
import nts.uk.ctx.sys.assist.dom.deletedata.DataDeletionSelectionCategory;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * データ削除のパターン設定
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SSPMT_DELETE_PATTERN_SET")
public class SspmtDataDeletionPatternSetting extends UkJpaEntity implements Serializable,
																			DataDeletionPatternSetting.MementoGetter,
																			DataDeletionPatternSetting.MementoSetter {

	private static final long serialVersionUID = 1L;

	// column 排他バージョン
	@Version
	@Column(name = "EXCLUS_VER")
	private long version;

	@EmbeddedId
	private SspmtDataDeletionPatternSettingPK pk;

	/**
	 * パスワード有無
	 */
	@Basic(optional = false)
	@Column(name = "WITHOUT_PASSWORD")
	private int withoutPassword;

	/**
	 * 削除パターンパスワード
	 */
	@Basic(optional = true)
	@Column(name = "PATTERN_COMPRE_PASS")
	private String patternCompressionPwd;

	/**
	 * 月次参照年
	 */
	@Basic(optional = true)
	@Column(name = "MONTH_REFER_YEAR")
	private Integer monthlyReferYear;

	/**
	 * 日次参照年
	 */
	@Basic(optional = true)
	@Column(name = "DAILY_REFER_YEAR")
	private Integer dailyReferYear;

	/**
	 * 年次参照年
	 */
	@Basic(optional = true)
	@Column(name = "ANNUAL_REFER_YEAR")
	private Integer annualReferYear;

	/**
	 * 日次参照月
	 */
	@Basic(optional = true)
	@Column(name = "DAILY_REFER_MONTH")
	private Integer dailyReferMonth;

	/**
	 * 月次参照月
	 */
	@Basic(optional = true)
	@Column(name = "MONTH_REFER_MONTH")
	private Integer monthlyReferMonth;

	/**
	 * パターン名
	 */
	@Basic(optional = false)
	@Column(name = "PATTERN_NAME")
	private String patternName;
	
	/**
	 * 削除パターン補足説明
	 */
	@Basic(optional = true)
	@Column(name = "PATTERN_SUPLE_EXPLAN")
	private String patternSuppleExplanation;
	
	/**
	 * データ削除の選択カテゴリ
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "patternSetting", orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "SSPMT_SELECTED_DELCATE")
	public List<SspmtDataDeletionSelectionCategory> categories;

	@Override
	protected Object getKey() {
		return pk;
	}

	@Override
	public void setPatternCode(String patternCode) {
		if (pk == null)
			pk = new SspmtDataDeletionPatternSettingPK();
		pk.patternCode = patternCode;
	}

	@Override
	public void setPatternClassification(int patternClassification) {
		if (pk == null)
			pk = new SspmtDataDeletionPatternSettingPK();
		pk.patternClassification = patternClassification;
	}

	@Override
	public void setContractCode(String contractCode) {
		if (pk == null)
			pk = new SspmtDataDeletionPatternSettingPK();
		pk.contractCode = contractCode;
	}

	@Override
	public void setCategories(List<DataDeletionSelectionCategory> categories) {
		this.categories = categories.stream()
									.map(domain -> {
										SspmtDataDeletionSelectionCategory entity = new SspmtDataDeletionSelectionCategory();
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
	public List<DataDeletionSelectionCategory> getCategories() {
		return this.categories.stream()
						.map(DataDeletionSelectionCategory::createFromMemento)
						.collect(Collectors.toList());
	}
}
