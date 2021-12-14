package nts.uk.ctx.sys.assist.infra.entity.storage;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.assist.dom.storage.DataStorageMng;
import nts.uk.ctx.sys.assist.dom.storage.OperatingCondition;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * データ保存動作管理
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SSPTT_SAVE_MNG")
public class SspttSaveMng extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * データ保存処理ID
	 */
	@Id
	@Basic(optional = false)
	@Column(name = "STORE_PROCESSING_ID")
	public String storeProcessingId;

	/**
	 * 中断するしない
	 */
	@Basic(optional = false)
	@Column(name = "DO_NOT_INTERRUPT")
	public int doNotInterrupt;

	/**
	 * カテゴリカウント
	 */
	@Basic(optional = false)
	@Column(name = "CATEGORY_COUNT")
	public int categoryCount;

	/**
	 * カテゴリトータルカウント
	 */
	@Basic(optional = false)
	@Column(name = "CATEGORY_TOTAL_COUNT")
	public int categoryTotalCount;

	/**
	 * エラー件数
	 */
	@Basic(optional = false)
	@Column(name = "ERROR_COUNT")
	public int errorCount;

	/**
	 * 動作状態
	 */
	@Basic(optional = false)
	@Column(name = "OPERATING_CONDITION")
	public int operatingCondition;

	@Override
	protected Object getKey() {
		return storeProcessingId;
	}

	public DataStorageMng toDomain() {
		return new DataStorageMng(this.storeProcessingId, EnumAdaptor.valueOf(this.doNotInterrupt, NotUseAtr.class),
				this.categoryCount, this.categoryTotalCount, this.errorCount,
				EnumAdaptor.valueOf(this.operatingCondition, OperatingCondition.class));
	}

	public static SspttSaveMng toEntity(DataStorageMng domain) {
		return new SspttSaveMng(domain.getStoreProcessingId(),
				domain.getDoNotInterrupt().value, domain.getCategoryCount(), domain.getCategoryTotalCount(),
				domain.getErrorCount(), domain.getOperatingCondition().value);
	}
}
