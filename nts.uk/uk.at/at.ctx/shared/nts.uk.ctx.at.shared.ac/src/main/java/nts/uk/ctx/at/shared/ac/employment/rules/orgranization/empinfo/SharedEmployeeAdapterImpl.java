
package nts.uk.ctx.at.shared.ac.employment.rules.orgranization.empinfo;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.EmployeeAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.EmployeeInfoWantToBeGet;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.*;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.employment.SyEmploymentPub;
import nts.uk.query.pub.employee.EmployeeInformationPub;
import nts.uk.query.pub.employee.EmployeeInformationQueryDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class SharedEmployeeAdapterImpl implements EmployeeAdapter {

    /**
     * The employment.
     */
    @Inject
    public SyEmploymentPub employment;

    @Inject
    private EmployeeInformationPub employeeInformationPub;

    @Inject
    private SyEmployeePub employeePub;


    /**
     * [1] 社員コードから社員IDを取得する
     *
     * @param companyId 会社ID
     * @param empCodes  List<社員コード>
     * @return Map<社員コード                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                               社員ID>	                                                               ,                               S               t               r               ing>
     */
    @Override
    public Map<String, String> getEmploymentMapCodeName(String companyId, List<String> empCodes) {
        return employment.getEmploymentMapCodeName(companyId, empCodes);
    }

    /**
     * [2] 社員IDリストから社員コードと表示名を取得する
     *
     * @param sIds List<社員ID>
     * @return List<社員コードと表示名Imported                                                                                                                                                                                                                                                                >
     */
    @Override
    public List<EmployeeInfoImport> getByListSid(List<String> sIds) {
        return employeePub.getByListSid(sIds).stream()
                .map(x -> new EmployeeInfoImport(
                        x.getSid(),
                        x.getScd(),
                        x.getBussinessName()
                )).collect(Collectors.toList());
    }

    /**
     * アルゴリズム.<<Public>> 社員の情報を取得する( 社員IDリスト, 基準日, 取得したい社員情報 )
     *
     * @param employeeIds List<社員ID>
     * @param baseDate    年月日
     * @param param       取得したい社員情報
     * @return List<EmployeeInformationImport>
     */
    @Override
    public List<EmployeeInfoImported> getEmployeeInfo(List<String> employeeIds, GeneralDate baseDate, EmployeeInfoWantToBeGet param) {
        EmployeeInformationQueryDto empInfo = EmployeeInformationQueryDto.builder()
                .employeeIds(employeeIds)
                .referenceDate(baseDate)
                .toGetWorkplace(param.isGetWorkplace())
                .toGetDepartment(param.isGetDepartment())
                .toGetPosition(param.isGetJobTitle())
                .toGetEmployment(param.isGetEmployment())
                .toGetClassification(param.isGetClassification())
                .toGetEmploymentCls(false).build();
        return employeeInformationPub.find(empInfo).stream().map(x -> {
                    val workplace = x.getWorkplace();
                    val classification = x.getClassification();
                    val department = x.getDepartment();
                    val position = x.getPosition();
                    val employment = x.getEmployment();
                    Optional<WorkplaceImported> optWorkplace = workplace == null ? Optional.empty() : Optional.of(
                            new WorkplaceImported(
                                    workplace.getWorkplaceId(),
                                    workplace.getWorkplaceCode(),
                                    workplace.getWorkplaceGenericName(),
                                    workplace.getWorkplaceName()
                            )
                    );
                    Optional<DepartmentImported> optDepartment = department == null ? Optional.empty() : Optional.of(
                            new DepartmentImported(
                                    department.getDepartmentId(),
                                    department.getDepartmentCode(),
                                    department.getDepartmentGeneric(),
                                    department.getDepartmentDisplayName()
                            )
                    );
                    Optional<PositionImported> optPosition = position == null ? Optional.empty() : Optional.of(
                            new PositionImported(
                                    position.getPositionId(),
                                    position.getPositionCode(),
                                    position.getPositionName()
                            )
                    );
                    Optional<EmploymentImported> optEmployment = employment == null ? Optional.empty() : Optional.of(
                            new EmploymentImported(
                                    employment.getEmploymentCode(),
                                    employment.getEmploymentName()
                            )
                    );
                    Optional<ClassificationImported> optClassification = classification == null ? Optional.empty() : Optional.of(
                            new ClassificationImported(classification.getClassificationCode(), classification.getClassificationName())
                    );
                    return new EmployeeInfoImported(
                            x.getEmployeeId(),
                            x.getEmployeeCode(),
                            x.getBusinessName(),
                            x.getBusinessNameKana(),
                            optWorkplace,
                            optDepartment,
                            optPosition,
                            optEmployment,
                            optClassification
                    );
                }
        ).collect(Collectors.toList());
    }
}
