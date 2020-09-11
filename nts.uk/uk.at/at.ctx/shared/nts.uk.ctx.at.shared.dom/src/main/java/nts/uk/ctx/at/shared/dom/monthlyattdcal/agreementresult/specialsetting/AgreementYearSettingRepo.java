package nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.specialsetting;

import nts.arc.time.YearMonth;

import java.util.List;
import java.util.Optional;

public interface AgreementYearSettingRepo {

    /**
     * [1] Insert(３６協定年月設定)
     */
    void insert(AgreementYearSetting domain);

    /**
     * [2] Update(３６協定年月設定)
     */
    void update(AgreementYearSetting domain);

    /**
     * [3] Delete(３６協定年月設定)
     */
    void delete(AgreementYearSetting domain);

    /**
     * [4] get
     * 指定社員の全ての３６協定年月設定を取得する
     */
    List<AgreementYearSetting> getByEmployeeId(String employeeId);

    /**
     * [4] get
     * 指定社員の全ての３６協定年月設定を取得する
     */
    Optional<AgreementYearSetting> getByEmployeeIdAndYm(String employeeId, YearMonth yearMonth);
}
