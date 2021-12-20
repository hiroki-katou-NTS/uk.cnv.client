package nts.uk.ctx.at.schedule.infra.entity.shift.management.shifttable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRule;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRuleForOrganization;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 組織別のシフト表のルール Entity
 *
 * @author tutk
 */
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_SHIFTTBL_RULE_ORG")
public class KscmtShiftTableRuleForOrg extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KscmtShiftTableRuleForOrgPK pk;

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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "kscmtShiftTableRuleForOrg", orphanRemoval = true)
    public KscmtShiftTableRuleForOrgAvai kscmtShiftTableRuleForOrgAvai;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    public KscmtShiftTableRuleForOrg(KscmtShiftTableRuleForOrgPK pk, int usePublicAtr, int useWorkAvailabilityAtr,
                                     KscmtShiftTableRuleForOrgAvai kscmtShiftTableRuleForOrgAvai) {
        super();
        this.pk = pk;
        this.usePublicAtr = BooleanUtils.toBoolean(usePublicAtr);
        this.useWorkAvailabilityAtr = BooleanUtils.toBoolean(useWorkAvailabilityAtr);
        this.kscmtShiftTableRuleForOrgAvai = kscmtShiftTableRuleForOrgAvai;
    }

    public static KscmtShiftTableRuleForOrg toEntity(String companyId, ShiftTableRuleForOrganization domain) {
        return new KscmtShiftTableRuleForOrg(
                new KscmtShiftTableRuleForOrgPK(
                        companyId,
                        domain.getTargetOrg().getUnit().value,
                        domain.getTargetOrg().getTargetId()
                ),
                domain.getShiftTableRule().getUsePublicAtr().value,
                domain.getShiftTableRule().getUseWorkAvailabilityAtr().value,
                KscmtShiftTableRuleForOrgAvai.toEntity(
                        companyId,
                        domain.getTargetOrg().getUnit().value,
                        domain.getTargetOrg().getTargetId(),
                        domain.getShiftTableRule()
                )
        );
    }

    public ShiftTableRuleForOrganization toDomain() {
        TargetOrgIdenInfor target = TargetOrgIdenInfor.createFromTargetUnit(
                TargetOrganizationUnit.valueOf(this.pk.targetUnit),
                this.pk.targetID
        );
        if (this.kscmtShiftTableRuleForOrgAvai == null) {
            return new ShiftTableRuleForOrganization(
                    target,
                    new ShiftTableRule(
                            NotUseAtr.valueOf(usePublicAtr),
                            NotUseAtr.valueOf(useWorkAvailabilityAtr),
                            Optional.empty(),
                            new ArrayList<>(),
                            Optional.empty()
                    )
            );
        }
        return new ShiftTableRuleForOrganization(
                target,
                kscmtShiftTableRuleForOrgAvai.toDomain(BooleanUtils.toInteger(this.usePublicAtr), BooleanUtils.toInteger(this.useWorkAvailabilityAtr))
        );
    }
}
