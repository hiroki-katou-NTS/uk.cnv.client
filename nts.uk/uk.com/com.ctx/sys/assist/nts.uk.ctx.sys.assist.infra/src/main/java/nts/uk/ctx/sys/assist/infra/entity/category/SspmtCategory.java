package nts.uk.ctx.sys.assist.infra.entity.category;


import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.assist.dom.category.Category;
import nts.uk.ctx.sys.assist.dom.category.CategoryId;
import nts.uk.ctx.sys.assist.dom.category.CategoryName;
import nts.uk.ctx.sys.assist.dom.category.RecoverFormCompanyOther;
import nts.uk.ctx.sys.assist.dom.category.RecoveryStorageRange;
import nts.uk.ctx.sys.assist.dom.category.StorageRangeSaved;
import nts.uk.ctx.sys.assist.dom.category.StoredProcedureSpecified;
import nts.uk.ctx.sys.assist.dom.category.SystemUsability;
import nts.uk.ctx.sys.assist.dom.category.TimeStore;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
* カテゴリ
*/
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SSPMT_CATEGORY")
public class SspmtCategory extends UkJpaEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
	public SspmtCategoryPk categoryPk;
    /**
    * オフィスヘルパーの使用可否
    */
    @Basic(optional = false)
    @Column(name = "SCHELPER_SYSTEM")
    public int schelperSystem;
    
    /**
    * カテゴリ名
    */
    @Basic(optional = false)
    @Column(name = "CATEGORY_NAME")
    public String categoryName;
    
    /**
    * システム使用可否
    */
    @Basic(optional = false)
    @Column(name = "POSSIBILITY_SYSTEM")
    public int possibilitySystem;
    
    /**
    * 保存方法指定可能
    */
    @Basic(optional = false)
    @Column(name = "STORED_PROCEDURE_SPECIFIED")
    public int storedProcedureSpecified;
    
    /**
    * 保存期間区分
    */
    @Basic(optional = false)
    @Column(name = "TIME_STORE")
    public int timeStore;
    
    /**
    * 別会社区分
    */
    @Basic(optional = false)
    @Column(name = "OTHER_COMPANY_CLS")
    public int otherCompanyCls;
    
    /**
    * 勤怠システムの使用可否
    */
    @Basic(optional = false)
    @Column(name = "ATTENDANCE_SYSTEM")
    public int attendanceSystem;
    
    /**
    * 復旧保存範囲
    */
    @Basic(optional = false)
    @Column(name = "RECOVERY_STORAGE_RANGE")
    public int recoveryStorageRange;
    
    /**
    * 給与システムの使用可否
    */
    @Basic(optional = false)
    @Column(name = "PAYMENT_AVAILABILITY")
    public int paymentAvailability;
    
    /**
    * 保存時保存範囲
    */
    @Basic(optional = false)
    @Column(name = "STORAGE_RANGE_SAVED")
    public int storageRangeSaved;

    public Category toDomain() {
        return new Category(EnumAdaptor.valueOf(this.schelperSystem, SystemUsability.class), new CategoryId(this.categoryPk.categoryId), new CategoryName(this.categoryName), EnumAdaptor.valueOf(this.possibilitySystem, SystemUsability.class), 
        		EnumAdaptor.valueOf(this.storedProcedureSpecified, StoredProcedureSpecified.class), EnumAdaptor.valueOf(this.timeStore, TimeStore.class), EnumAdaptor.valueOf(this.otherCompanyCls, RecoverFormCompanyOther.class), EnumAdaptor.valueOf(this.attendanceSystem, SystemUsability.class), 
        		EnumAdaptor.valueOf(this.recoveryStorageRange, RecoveryStorageRange.class), EnumAdaptor.valueOf(this.paymentAvailability, SystemUsability.class), EnumAdaptor.valueOf(this.storageRangeSaved, StorageRangeSaved.class));
    }
    public static SspmtCategory toEntity(Category domain) {
        return new SspmtCategory(
        		new SspmtCategoryPk(domain.getCategoryId().v()),
        		domain.getSchelperSystem().value, domain.getCategoryName().v(), 
        		domain.getPossibilitySystem().value, domain.getStoredProcedureSpecified().value, domain.getTimeStore().value, domain.getOtherCompanyCls().value, domain.getAttendanceSystem().value, domain.getRecoveryStorageRange().value, domain.getPaymentAvailability().value, domain.getStorageRangeSaved().value);
    }
	@Override
	protected Object getKey() {
		return categoryPk;
	}

}
