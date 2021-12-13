package nts.uk.screen.com.app.find.cas014;

import lombok.val;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

/**
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS014_ロールセットの付与.B: 特別社員の付与設定.メニュー別OCD.ロールセット個人別付与を取得する.ロールセット個人別付与を取得する
 * ScreenQuery: ロールセット個人別付与を取得する
 * @author chinh.hm
 */
@Stateless
public class GetIndividualRollSetGrantScreenQuery {

    @Inject
    private RoleSetGrantedPersonRepository roleSetGrantedPersonRepository;

    public RoleSetGrantedPersonDto getIndividualRollSetGrant(String employeeId){
        Optional<RoleSetGrantedPerson> optionalRoleSetGrantedPerson = this.roleSetGrantedPersonRepository.getByEmployeeId(employeeId);
        val rs = new RoleSetGrantedPersonDto();
        if(optionalRoleSetGrantedPerson.isPresent()){
            val grandPerSon = optionalRoleSetGrantedPerson.get();
           rs.setEmployeeID(grandPerSon.getEmployeeID());
           rs.setRoleSetCd(grandPerSon.getRoleSetCd().v());
           rs.setStartDate(grandPerSon.getValidPeriod().start());
           rs.setEndDate(grandPerSon.getValidPeriod().end());
        }
        return rs;
    }
}
