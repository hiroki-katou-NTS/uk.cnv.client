package nts.uk.ctx.at.schedule.infra.entity.shift.management.shifttable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRule;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRuleForCompany;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 会社のシフト表のルール entity
 * @author tutk
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_SHIFTTBL_RULE_CMP")
public class KscmtShiftTableRuleForCompany extends ContractUkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 会社ID
	 */
	@Id
	@Column(name = "CID")
	public String companyId;
	
	/**
	 * 公開運用区分
	 */
	@Column(name = "USE_PUBLICATION_ATR")
	public boolean usePublicAtr;
	
	/**
	 * 勤務希望運用区分
	 */
	@Column(name = "USE_AVAILABILITY_ATR")
	public boolean useWorkAvailabilityAtr;
	
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "kscmtShiftTableRuleForCompany", orphanRemoval = true)
	public KscmtShiftTableRuleForCompanyAvai kscmtShiftTableRuleForCompanyAvai;

	@Override
	protected Object getKey() {
		return this.companyId;
	}

	public KscmtShiftTableRuleForCompany(String companyId, int usePublicAtr, int useWorkAvailabilityAtr,
			KscmtShiftTableRuleForCompanyAvai kscmtShiftTableRuleForCompanyAvai) {
		super();
		this.companyId = companyId;
		this.usePublicAtr = BooleanUtils.toBoolean(usePublicAtr);
		this.useWorkAvailabilityAtr = BooleanUtils.toBoolean(useWorkAvailabilityAtr);
		this.kscmtShiftTableRuleForCompanyAvai = kscmtShiftTableRuleForCompanyAvai;
	}

	public static KscmtShiftTableRuleForCompany toEntity(String companyId,ShiftTableRuleForCompany domain ) {
		return new KscmtShiftTableRuleForCompany(
				companyId,
				domain.getShiftTableRule().getUsePublicAtr().value,
				domain.getShiftTableRule().getUseWorkAvailabilityAtr().value,
				KscmtShiftTableRuleForCompanyAvai.toEntity(companyId, domain.getShiftTableRule())
		);
	}
	
	public ShiftTableRuleForCompany toDomain() {
		if (this.kscmtShiftTableRuleForCompanyAvai == null) {
			return new ShiftTableRuleForCompany(
					new ShiftTableRule(
							NotUseAtr.valueOf(usePublicAtr),
							NotUseAtr.valueOf(useWorkAvailabilityAtr),
							Optional.empty(),
							new ArrayList<>(),
							Optional.empty()
					)
			);
		}
		return new ShiftTableRuleForCompany(kscmtShiftTableRuleForCompanyAvai.toDomain(BooleanUtils.toInteger(this.usePublicAtr), BooleanUtils.toInteger(this.useWorkAvailabilityAtr)));
	}
}
