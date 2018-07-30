package nts.uk.ctx.exio.infra.entity.exo.category;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.exio.dom.exo.category.ExOutCtg;
import nts.uk.shr.infra.permit.data.JpaEntityOfDescriptionOfAvailabilityPermissionBase;

/**
 * 外部出力カテゴリ
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OIOMT_EX_OUT_CTG")
public class OiomtExOutCtg extends JpaEntityOfDescriptionOfAvailabilityPermissionBase<ExOutCtg>
		implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
    * 会社ID
    */
    @Basic(optional = false)
    @Column(name = "CID")
    public String cid;

	/**
	 * カテゴリID
	 */
	@Basic(optional = false)
	@Column(name = "CATEGORY_ID")
	public int categoryId;

	/**
	 * オフィスヘルパシステム区分
	 */
	@Basic(optional = false)
	@Column(name = "OFFICE_HELPER_SYS_ATR")
	public int officeHelperSysAtr;

	/**
	 * カテゴリ名
	 */
	@Basic(optional = false)
	@Column(name = "CATEGORY_NAME")
	public String categoryName;

	/**
	 * カテゴリ設定
	 */
	@Basic(optional = false)
	@Column(name = "CATEGORY_SET")
	public int categorySet;

	/**
	 * 人事システム区分
	 */
	@Basic(optional = false)
	@Column(name = "PERSON_SYS_ATR")
	public int personSysAtr;

	/**
	 * 勤怠システム区分
	 */
	@Basic(optional = false)
	@Column(name = "ATTENDANCE_SYS_ATR")
	public int attendanceSysAtr;

	/**
	 * 給与システム区分
	 */
	@Basic(optional = false)
	@Column(name = "PAYROLL_SYS_ATR")
	public int payrollSysAtr;

	@Override
	protected Object getKey() {
		return this.functionNo;
	}

	public ExOutCtg toDomain() {
		return new ExOutCtg(this.cid, this.categoryId, this.officeHelperSysAtr, this.categoryName, this.categorySet,
				this.personSysAtr, this.attendanceSysAtr, this.payrollSysAtr, this.functionNo, this.name,
				this.explanation, this.displayOrder, this.defaultValue);
	}

	public static OiomtExOutCtg toEntity(ExOutCtg domain) {
		return new OiomtExOutCtg(domain.getCid(), domain.getCategoryId().v(), domain.getOfficeHelperSysAtr().value,
				domain.getCategoryName().v(), domain.getCategorySet().value, domain.getPersonSysAtr().value,
				domain.getAttendanceSysAtr().value, domain.getPayrollSysAtr().value);
	}

}
