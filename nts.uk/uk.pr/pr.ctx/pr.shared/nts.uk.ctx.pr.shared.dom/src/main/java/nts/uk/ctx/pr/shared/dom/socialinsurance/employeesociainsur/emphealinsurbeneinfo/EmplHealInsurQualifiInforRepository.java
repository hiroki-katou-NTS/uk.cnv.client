package nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo;

import nts.arc.time.GeneralDate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 社員健康保険資格情報
 */
public interface EmplHealInsurQualifiInforRepository {

    boolean checkEmplHealInsurQualifiInforEndDate(GeneralDate start, GeneralDate end, List<String> empIds);

    boolean checkEmplHealInsurQualifiInforStartDate(GeneralDate start, GeneralDate end, List<String> empIds);

    Optional<HealInsurNumberInfor> getEmplHealInsurQualifiInforByEmpId(String cid, String empId, GeneralDate date);

    Optional<HealInsurNumberInfor> getEmplHealInsurQualifiInforByEmpId(String cid, String empId, GeneralDate date, String historyId);

    Optional<EmplHealInsurQualifiInfor> getEmplHealInsurQualifiInfor(String cid, String empId, GeneralDate date);

    List<EmplHealInsurQualifiInfor> getEmplHealInsurQualifiInfor(String cid, List<String> empIds, GeneralDate date);

    List<EmplHealInsurQualifiInfor> getEmplHealInsurQualifiInfor(String cid, List<String> empIds);

    Optional<EmplHealInsurQualifiInfor> getEmpHealInsQualifiinfoById(String cid, String empId);

    EmplHealInsurQualifiInfor getEmpHealInsQualifiInfoOfEmp(String empId);

    Optional<EmplHealInsurQualifiInfor> getByHistoryId(String historyId);

    Optional<EmplHealInsurQualifiInfor> getByEmpIdAndBaseDate(String empId, GeneralDate baseDate);

    void add(String empId, EmpHealthInsurBenefits item);

    void addAll(Map<String, EmpHealthInsurBenefits> qualifiInfors);

    void update(EmpHealthInsurBenefits benefits);

    void updateAll (List<EmpHealthInsurBenefits> qualifiInfors);

    void remove(String employeeId, String hisId);
}
