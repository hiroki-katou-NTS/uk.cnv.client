package nts.uk.ctx.at.schedule.infra.entity.shift.management;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.schedule.dom.shift.management.ShiftPallet;
import nts.uk.ctx.at.schedule.dom.shift.management.ShiftPalletDisplayInfor;
import nts.uk.ctx.at.schedule.dom.shift.management.ShiftPalletName;
import nts.uk.ctx.at.schedule.dom.shift.management.ShiftPalletsOrg;
import nts.uk.ctx.at.schedule.dom.shift.management.ShiftRemarks;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 組織別シフトパレット
 * @author phongtq
 *
 */

@Entity
@Table(name = "KSCMT_PALETTE_ORG")
@AllArgsConstructor
@NoArgsConstructor
public class KscmtPaletteOrg extends UkJpaEntity{
	
	@EmbeddedId
	public KscmtPaletteOrgPk pk;
	
	/** 名称 */
	@Column(name = "PAGE_NAME")
	public String pageName;
	
	/** 使用区分 */
	@Column(name = "USE_ATR")
	public int useAtr;
	
	/** 備考 */
	@Column(name = "NOTE")
	public String note;

	@OneToMany(targetEntity = KscmtPaletteOrgCombi.class, mappedBy = "kscmtPaletteOrg", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "KSCMT_PALETTE_ORG_COMBI")
	public List<KscmtPaletteOrgCombi> orgCombis;

	@Override
	protected Object getKey() {
		return pk;
	}

	public static KscmtPaletteOrg fromDomain(ShiftPalletsOrg shiftPalletsOrg ){
		KscmtPaletteOrgPk pk = new KscmtPaletteOrgPk(AppContexts.user().companyId(), shiftPalletsOrg.getTargeOrg().getUnit().value ,shiftPalletsOrg.getTargeOrg().getWorkplaceId().get(),shiftPalletsOrg.getPage());
		
		return new KscmtPaletteOrg(pk,
				shiftPalletsOrg.getShiftPallet().getDisplayInfor().getShiftPalletName().v(),
				shiftPalletsOrg.getShiftPallet().getDisplayInfor().getShiftPalletAtr().value,
				shiftPalletsOrg.getShiftPallet().getDisplayInfor().getRemarks().v(),
				shiftPalletsOrg.getShiftPallet().getCombinations().stream()
				.map(x -> KscmtPaletteOrgCombi.fromDomain(x, pk)).collect(Collectors.toList()));
	}
	//new TargetOrgIdenInfor(EnumAdaptor.valueOf(pk.targetUnit, TargetOrganizationUnit.class)
	public ShiftPalletsOrg toDomain() {
		String workplaceId = null;
		String groupWorkplaceId = null;
		if (pk.targetUnit == 0) {
			workplaceId = pk.targetId;
		} else if (pk.targetUnit == 1)
			groupWorkplaceId = pk.targetId;
		
		return new ShiftPalletsOrg(new TargetOrgIdenInfor(EnumAdaptor.valueOf(pk.targetUnit, TargetOrganizationUnit.class), workplaceId, groupWorkplaceId) ,
				pk.page,
				new ShiftPallet(
						new ShiftPalletDisplayInfor(new ShiftPalletName(pageName), EnumAdaptor.valueOf(useAtr, NotUseAtr.class),
								new ShiftRemarks(note)),
						orgCombis.stream().map(x -> x.toDomain()).collect(Collectors.toList())));
	}
}
