package nts.uk.screen.com.app.find.cmm051;

import lombok.val;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.screen.com.app.find.user.information.employee.data.management.information.EmployeeDataMngInfoDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <Screen Query> 社員モードで社員情報と職場情報一覧を取得する
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM051_職場管理者の登録.A:職場管理者の登録.メニュー別OCD.社員モードで社員情報と職場情報一覧を取得する
 * @author chinh.hm
 */
@Stateless
public class GetEmployeeInfoAndWorkplaceInfoScreenQuery {
    @Inject
    private EmployeeDataMngInfoRepository employeeDataMngInfoRepository;
    @Inject
    private PersonRepository personRepository;
    @Inject
    private GetList0fWorkplaceInformationScreenQuery workplaceInformationScreenQuery;

    public EmployeeInfoAndWorkplaceInfoDto getListInformation(List<String> sids) {
        List<WorkplaceInforParam> workplaceInfo = new ArrayList<>();
        List<EmployeeDataMngInfoDto> listEmployee = employeeDataMngInfoRepository.findByListEmployeeId(sids)
                .stream().map(EmployeeDataMngInfoDto::toDto).collect(Collectors.toList());
        List<String> listPersonId = listEmployee.stream().map(EmployeeDataMngInfoDto::getPersonId)
                .collect(Collectors.toList());

        List<PersonDto> personList = personRepository.getPersonByPersonIds(listPersonId)
                .stream().map(e -> new PersonDto(
                        e.getPersonId(),
                        e.getPersonNameGroup().getBusinessName().v(),
                        e.getGender().value,
                        e.getBirthDate()
                )).collect(Collectors.toList());
        for (val e : listEmployee) {
            val wpls = workplaceInformationScreenQuery.getListWplInfo(e.getEmployeeId());
            if(wpls.isEmpty()){
                continue;
            }
            workplaceInfo.addAll(wpls);
        }
        return new EmployeeInfoAndWorkplaceInfoDto(workplaceInfo, listEmployee, personList);
    }
}
