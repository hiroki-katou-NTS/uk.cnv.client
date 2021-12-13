package nts.uk.screen.com.app.find.cmm051;

import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.screen.com.app.find.user.information.employee.data.management.information.EmployeeDataMngInfoDto;
import org.omg.CORBA.Object;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Screen query:UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM051_職場管理者の登録.A:職場管理者の登録.メニュー別OCD.社員情報一覧を取得する
 * @author : chinh.hm
 */

@Stateless
public class GetListEmployeeInformationScreenQuery {
    @Inject
    private WorkplaceManagerRepository workplaceManagerRepository;
    @Inject
    private EmployeeDataMngInfoRepository employeeDataMngInfoRepository;
    @Inject
    private PersonRepository personRepository;

    public EmployeeInformationDto getLisEmployeeInfo(String workplaceId){
        EmployeeInformationDto rs  = new EmployeeInformationDto();
        Comparator<WorkplaceManagerDto> comparator  = new Comparator<WorkplaceManagerDto>() {
            @Override
            public int compare(WorkplaceManagerDto o1, WorkplaceManagerDto o2) {
                return o2.getStartDate().compareTo(o1.getStartDate());
            }
        };
        List<WorkplaceManagerDto> workplaceManagerList = workplaceManagerRepository
                .getWkpManagerListByWkpId(workplaceId)
                .stream().map(WorkplaceManagerDto::new).sorted(comparator).collect(Collectors.toList());

        List<String> sids = workplaceManagerList.stream()
                .map(WorkplaceManagerDto::getEmployeeId).distinct().collect(Collectors.toList());
        List<EmployeeDataMngInfoDto> listEmployee = employeeDataMngInfoRepository.findByListEmployeeId(sids)
                .stream().map(EmployeeDataMngInfoDto::toDto).collect(Collectors.toList());
        List<String> listPersonId = listEmployee.stream().map(EmployeeDataMngInfoDto::getPersonId)
                .collect(Collectors.toList());
        rs.setListEmployee(listEmployee);
        rs.setWorkplaceManagerList(workplaceManagerList);
        if(listPersonId.isEmpty()){
            return rs;
        }
        List<PersonDto> personList = personRepository.getPersonByPersonIds(listPersonId)
                .stream().map(e -> new PersonDto(
                        e.getPersonId(),
                        e.getPersonNameGroup().getBusinessName().v(),
                        e.getGender().value,
                        e.getBirthDate()
                )).collect(Collectors.toList());
        rs.setPersonList(personList);
        return rs;
    }
}
