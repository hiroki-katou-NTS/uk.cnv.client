package nts.uk.screen.com.app.find.cas011.roleset;


import lombok.val;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetLinkWebMenuAdapter;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetLinkWebMenuImport;
import nts.uk.ctx.sys.portal.dom.adapter.role.DefaultRoleSetDto;
import nts.uk.ctx.sys.portal.dom.adapter.roleset.RoleSetDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS011_ロールセットの登録.ロールセットの登録.メニュー別OCD.ロールセット詳細情報を取得する
 * ScreenQuery: ロールセット詳細情報を取得する
 * @author chinh.hm
 */
@Stateless
public class GetDetailedRoleSetInformationScreenQuery {

    @Inject
    private RoleSetRepository roleSetRepository;

    @Inject
    private DefaultRoleSetRepository defaultRoleSetRepository;

    @Inject
    private RoleSetLinkWebMenuAdapter roleSetLinkWebMenuAdapter;

    public DetailedRoleSetInformationDto getDetailRoleSet(String roleSetCode) {
        val cid = AppContexts.user().companyId();
        Optional<RoleSet> optionalRoleSet = this.roleSetRepository.findByRoleSetCdAndCompanyId(roleSetCode, cid);
        val rs = new DetailedRoleSetInformationDto();
        if (optionalRoleSet.isPresent()) {
            RoleSet roleSet = optionalRoleSet.get();
            String roleSetCd = roleSet.getRoleSetCd().v();
            List<RoleSetLinkWebMenuImport> linkWebMenuImportList = roleSetLinkWebMenuAdapter.findAllWebMenuByRoleSetCd(roleSetCd);
            Optional<DefaultRoleSet> optionalDefaultRoleSet = defaultRoleSetRepository.findByCompanyId(cid);
            rs.setRoleSetDtos(convertToDto(roleSet));
            DefaultRoleSetDto defaultRoleSet = new DefaultRoleSetDto();
            if (optionalDefaultRoleSet.isPresent()){
                val   roleSetDefault = optionalDefaultRoleSet.get();
                defaultRoleSet.setCompanyId(roleSetDefault.getCompanyId());
                defaultRoleSet.setRoleSetCd(roleSetDefault.getRoleSetCd().v());
            }
            rs.setDefaultRoleSet(defaultRoleSet);
            rs.setLinkWebMenuImportList(linkWebMenuImportList);
        }
        return rs;
    }

    private RoleSetDto convertToDto(RoleSet domain) {
        RoleSetDto roleSetDto = new RoleSetDto();

        roleSetDto.setRoleSetName(domain.getRoleSetName().v());
        //roleSetDto.setApprovalAuthority(domain.getApprovalAuthority().value); //TODO dev delete
        roleSetDto.setCompanyId(domain.getCompanyId());
        roleSetDto.setRoleSetCd(domain.getRoleSetCd().v());

        if (domain.getEmploymentRoleId().isPresent()) {
            roleSetDto.setEmploymentRoleId(domain.getEmploymentRoleId().get());
        }

        if (domain.getPersonInfRoleId().isPresent()) {
            roleSetDto.setPersonInfRoleId(domain.getPersonInfRoleId().get());
        }

        if (domain.getHRRoleId().isPresent()) {
            roleSetDto.setHRRoleId(domain.getHRRoleId().get());
        }

        if (domain.getSalaryRoleId().isPresent()) {
            roleSetDto.setSalaryRoleId(domain.getSalaryRoleId().get());
        }

        if (domain.getMyNumberRoleId().isPresent()) {
            roleSetDto.setMyNumberRoleId(domain.getMyNumberRoleId().get());
        }

        if (domain.getOfficeHelperRoleId().isPresent()) {
            roleSetDto.setOfficeHelperRoleId(domain.getOfficeHelperRoleId().get());
        }
        return roleSetDto;
    }
}
