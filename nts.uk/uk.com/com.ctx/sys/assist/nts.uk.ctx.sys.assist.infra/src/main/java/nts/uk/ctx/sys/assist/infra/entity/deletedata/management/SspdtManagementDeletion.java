package nts.uk.ctx.sys.assist.infra.entity.deletedata.management;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.assist.dom.deletedata.managerment.ManagementDeletion;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Entity
@Table(name = "SSPDT_MANAGEMENT_DELETION")
@NoArgsConstructor
@AllArgsConstructor
public class SspdtManagementDeletion extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
    public SspdtManagementDeletionPK sspdtManagementDeletionPK;
	
	
	/** The interrupted flag. */
	/** 中断するしない */
	@Basic(optional = false)
	@Column(name = "IS_INTERRUPTED_FLG")
	public int isInterruptedFlg;
	
	/** The total category count. */
	/** カテゴリトータルカウント */
	@Basic(optional = false)
	@Column(name = "TOTAL_CATEGORY_COUNT")
	public int totalCategoryCount;
	
	/** The category count. */
	/** カテゴリカウント */
	@Basic(optional = false)
	@Column(name = "CATEGORY_COUNT")
	public int categoryCount;
	
	/** The errorCount. */
	/** エラー件数  */
	@Basic(optional = false)
	@Column(name = "ERROR_COUNT")
	public int errorCount;
	
	/** The operating condition. */
	/**  動作状態 */
	@Basic(optional = false)
	@Column(name = "OPERATING_CONDITION")
	public int operatingCondition;
	

	@Override
	protected Object getKey() {
		return sspdtManagementDeletionPK;
	}

	public ManagementDeletion toDomain() {
		boolean isInterruptedFlg = this.isInterruptedFlg == 1;
		return ManagementDeletion.createFromJavatype(this.sspdtManagementDeletionPK.delId, 
				isInterruptedFlg, this.totalCategoryCount, this.categoryCount, 
				this.errorCount, this.operatingCondition);
	}

	public static SspdtManagementDeletion toEntity(ManagementDeletion managementDeletion) {
		int isInterruptedFlg = managementDeletion.isInterruptedFlg ? 1 : 0;
		
		return new SspdtManagementDeletion(new SspdtManagementDeletionPK(managementDeletion.delId), isInterruptedFlg,
				managementDeletion.totalCategoryCount, managementDeletion.categoryCount, 
				managementDeletion.errorCount, managementDeletion.operatingCondition.value);
	}
}
