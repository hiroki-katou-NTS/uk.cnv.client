package nts.uk.ctx.at.function.ac.holidayover60h;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.holidayover60h.*;
import nts.uk.ctx.at.record.pub.remainingnumber.holidayover60h.AggrResultOfHolidayOver60hExport;
import nts.uk.ctx.at.record.pub.remainingnumber.holidayover60h.GetHolidayOver60hRemNumWithinPeriodPub;
import nts.uk.ctx.at.record.pub.remainingnumber.holidayover60h.HolidayOver60hInfoExport;
import nts.uk.ctx.at.record.pub.remainingnumber.holidayover60h.HolidayOver60hRemainingNumberExport;
import nts.uk.ctx.at.record.pub.remainnumber.holidayover60h.export.HolidayOver60hGrantRemainingExport;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.holidayover60h.interim.TmpHolidayOver60hMng;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class GetHolidayOver60hPeriodAdapterImp implements GetHolidayOver60hPeriodAdapter {
    @Inject
    private GetHolidayOver60hRemNumWithinPeriodPub publisher;

    /**
     * [RQ677]期間中の60H超休残数を取得する
     *
     * @param companyId          会社ID
     * @param employeeId         社員ID
     * @param aggrPeriod         集計期間
     * @param mode               実績のみ参照区分
     * @param criteriaDate       基準日
     * @param isOverWriteOpt     上書きフラグ
     * @param forOverWriteList   上書き用の暫定60H超休管理データ
     * @param prevHolidayOver60h 前回の60H超休の集計結果
     * @return 60H超休の集計結果
     */
    @Override
    public AggrResultOfHolidayOver60hImport algorithm(String companyId, String employeeId, DatePeriod aggrPeriod, InterimRemainMngMode mode, GeneralDate criteriaDate, Optional<Boolean> isOverWriteOpt, Optional<List<TmpHolidayOver60hMng>> forOverWriteList, Optional<AggrResultOfHolidayOver60hImport> prevHolidayOver60h) {
        AggrResultOfHolidayOver60hExport result = publisher.algorithm(
                companyId,
                employeeId,
                aggrPeriod,
                mode,
                criteriaDate,
                isOverWriteOpt,
                forOverWriteList,
                prevHolidayOver60h.map(i -> importToExport(i))
        );
        return exportToImport(result);
    }

    private AggrResultOfHolidayOver60hImport exportToImport(AggrResultOfHolidayOver60hExport export) {
        return new AggrResultOfHolidayOver60hImport(
                export.getUsedTimes(),
                export.getHolidayOver60hErrors(),
                holidayOver60hInfoToImport(export.getAsOfPeriodEnd()),
                holidayOver60hInfoToImport(export.getAsOfStartNextDayOfPeriodEnd()),
                holidayOver60hInfoToImport(export.getLapsed())
        );
    }

    private AggrResultOfHolidayOver60hExport importToExport(AggrResultOfHolidayOver60hImport importData) {
        return new AggrResultOfHolidayOver60hExport(
                importData.getUsedTimes(),
                importData.getHolidayOver60hErrors(),
                holidayOver60hInfoToExport(importData.getAsOfPeriodEnd()),
                holidayOver60hInfoToExport(importData.getAsOfStartNextDayOfPeriodEnd()),
                holidayOver60hInfoToExport(importData.getLapsed())
        );
    }

    private HolidayOver60hInfoImport holidayOver60hInfoToImport(HolidayOver60hInfoExport export) {
        return new HolidayOver60hInfoImport(
                export.getYmd(),
                new HolidayOver60hRemainingNumberImport(
                        export.getRemainingNumber().getUsedTimeWithMinus(),
                        export.getRemainingNumber().getRemainingTimeWithMinus(),
                        export.getRemainingNumber().getUsedTimeNoMinus(),
                        export.getRemainingNumber().getRemainingTimeNoMinus(),
                        export.getRemainingNumber().getCarryForwardTimes(),
                        export.getRemainingNumber().getHolidayOver60hUndigestNumber()
                ),
                export.getGrantRemainingExportList().stream().map(i -> {
                    HolidayOver60hGrantRemainingImport tmp = new HolidayOver60hGrantRemainingImport();
                    tmp.setLeaveID(i.getLeaveID());
                    tmp.setEmployeeId(i.getEmployeeId());
                    tmp.setGrantDate(i.getGrantDate());
                    tmp.setDeadline(i.getDeadline());
                    tmp.setExpirationStatus(i.getExpirationStatus());
                    tmp.setRegisterType(i.getRegisterType());
                    tmp.setDetails(i.getDetails());
                    return tmp;
                }).collect(Collectors.toList())
        );
    }

    private HolidayOver60hInfoExport holidayOver60hInfoToExport(HolidayOver60hInfoImport importData) {
        return new HolidayOver60hInfoExport(
                importData.getYmd(),
                new HolidayOver60hRemainingNumberExport(
                        importData.getRemainingNumber().getUsedTimeWithMinus(),
                        importData.getRemainingNumber().getRemainingTimeWithMinus(),
                        importData.getRemainingNumber().getUsedTimeNoMinus(),
                        importData.getRemainingNumber().getRemainingTimeNoMinus(),
                        importData.getRemainingNumber().getCarryForwardTimes(),
                        importData.getRemainingNumber().getHolidayOver60hUndigestNumber()
                ),
                importData.getGrantRemainingList().stream().map(i -> {
                    HolidayOver60hGrantRemainingExport tmp = new HolidayOver60hGrantRemainingExport();
                    tmp.setLeaveID(i.getLeaveID());
                    tmp.setEmployeeId(i.getEmployeeId());
                    tmp.setGrantDate(i.getGrantDate());
                    tmp.setDeadline(i.getDeadline());
                    tmp.setExpirationStatus(i.getExpirationStatus());
                    tmp.setRegisterType(i.getRegisterType());
                    tmp.setDetails(i.getDetails());
                    return tmp;
                }).collect(Collectors.toList())
        );
    }
}