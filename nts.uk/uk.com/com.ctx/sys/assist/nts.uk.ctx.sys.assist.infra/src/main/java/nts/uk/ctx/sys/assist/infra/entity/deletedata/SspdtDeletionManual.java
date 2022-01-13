package nts.uk.ctx.sys.assist.infra.entity.deletedata;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.sys.assist.dom.deletedata.ManualSetDeletion;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "SSPDT_DELETION_MANUAL")
@NoArgsConstructor
@AllArgsConstructor
public class SspdtDeletionManual extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
    public SspdtManualSetDeletionPK sspdtManualSetDeletionPK;
	
	/** The company Id. */
	/** 会社ID */
	@Basic(optional = false)
	@Column(name = "CID")
	public String companyID;
	
	
	/** The deletion name. */
	/** 削除名称 */
	@Basic(optional = false)
	@Column(name = "DEL_NAME")
	public String delName;
	
	/** The The saving before deletion flag. */
	/** 削除前に保存 */
	@Basic(optional = false)
	@Column(name = "IS_SAVE_BEFORE_DELETE_FLG")
	public boolean isSaveBeforeDeleteFlg;
	
	/** The existing compress pass flag. */
	/** パスワード有無 */
	@Basic(optional = false)
	@Column(name = "IS_EXIST_COMPRESS_PASS_FLG")
	public boolean isExistCompressPassFlg;
	
	/** The password encrypt for compress file. */
	/** 手動保存の圧縮パスワード */
	@Basic(optional = true)
	@Column(name = "PASSWORD_FOR_COMPRESS_FILE")
	public String passwordCompressFileEncrypt;
	
	/** The having employee specified flag. */
	/** 社員指定の有無 */
	@Basic(optional = false)
	@Column(name = "HAVE_EMPLOYEE_SPECIFIED_FLG")
	public boolean haveEmployeeSpecifiedFlg;
	
	/** The employee Id. */
	/** 実行者 */
	@Basic(optional = false)
	@Column(name = "SID")
	public String sId;
	
	/** The supplement explanation. */
	/**  補足説明 */
	@Basic(optional = true)
	@Column(name = "SUPPLEMENT_EXPLANATION")
	public String supplementExplanation;
	
	/** The reference date.*/
	/** 基準日 */
	@Basic(optional = true)
	@Column(name = "REFERENCE_DATE")
	public GeneralDate referenceDate;
	
	/** The execution date time. */
	/** 実行日時 */
	@Basic(optional = false)
	@Column(name = "EXECUTION_DATE_TIME")
	public GeneralDateTime executionDateTime;
	
	/** The start date of daily. */
	/** 日次削除開始日 */
	@Basic(optional = true)
	@Column(name = "START_DATE_OF_DAILY")
	public GeneralDate startDateOfDaily;
	
	/** The end date of daily. */
	/** 日次削除終了日 */
	@Basic(optional = true)
	@Column(name = "END_DATE_OF_DAILY")
	public GeneralDate endDateOfDaily;
	
	
	/** The start month of monthly. */
	/** 月次削除開始月  */
	@Basic(optional = true)
	@Column(name = "START_MONTH_OF_MONTHLY")
	public Integer startMonthOfMonthly;
	
	/** The end month of monthly. */
	/** 月次削除終了月  */
	@Basic(optional = true)
	@Column(name = "END_MONTH_OF_MONTHLY")
	public Integer endMonthOfMonthly;
	
	
	/** The start year of monthly. */
	/** 年次開始年  */
	@Basic(optional = true)
	@Column(name = "START_YEAR_OF_MONTHLY")
	public Integer startYearOfMonthly;
	
	
	/** The end year of monthly. */
	/** 年次終了年  */
	@Basic(optional = true)
	@Column(name = "END_YEAR_OF_MONTHLY")
	public Integer endYearOfMonthly;
	
	/**
	 * 実行区分
	 */
	@Basic(optional = false)
	@Column(name = "EXECUTE_ATR")
	public int executeClassification;
	
	/**
	 * 削除パターン
	 */
	@Basic(optional = false)
	@Column(name = "DEL_PATTERN")
	public String delPattern;
	
	/**
	 * 対象カテゴリ
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "manualSetDeletion", orphanRemoval = true, fetch = FetchType.LAZY)
	private List<SspdtCategoryDeletion> categoriesDeletion; 

	@Override
	protected Object getKey() {
		return sspdtManualSetDeletionPK;
	}

	public ManualSetDeletion toDomain() {
		boolean isSaveBeforeDeleteFlg = this.isSaveBeforeDeleteFlg;
		boolean isExistCompressPassFlg = this.isExistCompressPassFlg;
		boolean haveEmployeeSpecifiedFlg = this.haveEmployeeSpecifiedFlg;
		return ManualSetDeletion.createFromJavatype(this.sspdtManualSetDeletionPK.delId, this.companyID,
				this.delName, isSaveBeforeDeleteFlg, isExistCompressPassFlg, this.passwordCompressFileEncrypt,
				haveEmployeeSpecifiedFlg, this.sId, this.supplementExplanation, this.referenceDate,
				this.executionDateTime, this.startDateOfDaily, this.endDateOfDaily,
				this.startMonthOfMonthly, this.endMonthOfMonthly, this.startYearOfMonthly, this.endYearOfMonthly,
				this.executeClassification, this.delPattern,
				this.categoriesDeletion.stream().map(SspdtCategoryDeletion::toDomain).collect(Collectors.toList()));
	}

	public static SspdtDeletionManual toEntity(ManualSetDeletion manualSetting) {
		Optional<Integer> startMonthly = ManualSetDeletion.convertYearMonthToInt(manualSetting.getStartMonthOfMonthly());
		Optional<Integer> endMonthly = ManualSetDeletion.convertYearMonthToInt(manualSetting.getEndMonthOfMonthly());
		
		return new SspdtDeletionManual(new SspdtManualSetDeletionPK(manualSetting.getDelId()),
				manualSetting.getCompanyId(), manualSetting.getDelName().v(), 
				manualSetting.isSaveBeforeDeleteFlg(),
				manualSetting.isExistCompressPassFlg(), 
				manualSetting.getPasswordCompressFileEncrypt().isPresent() ? manualSetting.getPasswordCompressFileEncrypt().get().v() : null, 
						manualSetting.isHaveEmployeeSpecifiedFlg(), manualSetting.getSId(), 
				manualSetting.getSupplementExplanation().isPresent() ? manualSetting.getSupplementExplanation().get().v() : null, 
				manualSetting.getReferenceDate().isPresent() ? manualSetting.getReferenceDate().get() : null, 
				manualSetting.getExecutionDateTime(), 
				manualSetting.getStartDateOfDaily().isPresent() ? manualSetting.getStartDateOfDaily().get() : null, 
				manualSetting.getEndDateOfDaily().isPresent() ? manualSetting.getEndDateOfDaily().get() : null,
				startMonthly.isPresent() ? startMonthly.get() : null, 
				endMonthly.isPresent() ? endMonthly.get() : null, 
				manualSetting.getStartYearOfMonthly().isPresent() ? manualSetting.getStartYearOfMonthly().get() : null, 
				manualSetting.getEndYearOfMonthly().isPresent() ? manualSetting.getEndYearOfMonthly().get() : null,
				manualSetting.getExecuteClassification().value,
				manualSetting.getDelPattern().v(),
				manualSetting.getCategories().stream().map(SspdtCategoryDeletion::toEntity).collect(Collectors.toList()));
	}
}