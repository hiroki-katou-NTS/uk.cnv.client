package nts.uk.screen.com.app.find.cas011.roleset;

import lombok.val;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.portal.dom.adapter.role.DefaultRoleSetDto;
import nts.uk.ctx.sys.portal.dom.adapter.roleset.RoleSetDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS011_ロールセットの登録.ロールセットの登録.メニュー別OCD.ロールセット一覧を取得する
 * ScreenQuery : ロールセット一覧を取得する
 * Get a list of role sets
 *
 * @author chinh.hm
 */
@Stateless
public class GetListOfRoleSetScreenQuery {

    @Inject
    private RoleSetRepository roleSetRepository;

    @Inject
    private DefaultRoleSetRepository defaultRoleSetRepository;

    /**
     * Get List RoleSet and  default RoleSet
     *
     * @return RoleSetAndRoleDefaultDto
     */
    public RoleSetAndRoleDefaultDto getListRoleSet() {
        // call ロールセットをすべて取得する :  List<ロールセット> :
        val rs = new RoleSetAndRoleDefaultDto();
        val cid = AppContexts.user().companyId();
        List<RoleSet> lstRoleSet = this.roleSetRepository.findByCompanyId(cid);
        if (!lstRoleSet.isEmpty()) {
            // get 既定のロールセット
            Optional<DefaultRoleSet> defaultRoleSetOptional = this.defaultRoleSetRepository.findByCompanyId(cid);
            List<RoleSetDto> roleSetDtos = lstRoleSet.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            DefaultRoleSetDto defaultRoleSet = new DefaultRoleSetDto();
            if (defaultRoleSetOptional.isPresent()){
             val   roleSetDefault = defaultRoleSetOptional.get();
                defaultRoleSet.setCompanyId(roleSetDefault.getCompanyId());
                defaultRoleSet.setRoleSetCd(roleSetDefault.getRoleSetCd().v());
            }
            rs.setRoleSetDtos(roleSetDtos);
            rs.setDefaultRoleSet(defaultRoleSet);
        }
        return rs;
    }

    private RoleSetDto convertToDto(RoleSet domain) {
        RoleSetDto roleSetDto = new RoleSetDto();
        roleSetDto.setRoleSetName(domain.getRoleSetName().v());
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
