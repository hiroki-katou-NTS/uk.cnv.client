package nts.uk.ctx.pr.core.dom.wageprovision.companyuniformamount;

import nts.arc.time.YearMonth;
import nts.uk.ctx.pr.core.dom.wageprovision.companyuniformamount.*;
import nts.uk.shr.com.history.YearMonthHistoryItem;
import nts.uk.shr.com.time.calendar.period.YearMonthPeriod;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PayrollUnitPriceSettingService {

    @Inject
    PayrollUnitPriceRepository payrollUnitPriceRepository;

    @Inject
    PayrollUnitPriceHistoryRepository payrollUnitPriceHistoryRepository;

    @Inject
    PayrollUnitPriceSettingRepository payrollUnitPriceSettingRepository;

    public void register(PayrollUnitPrice payrollUnitPrice, PayrollUnitPriceHistory payrollUnitPriceHistory, PayrollUnitPriceSetting payrollUnitPriceSetting){

        YearMonthHistoryItem history = payrollUnitPriceHistory.getHistory().get(0);
        payrollUnitPriceRepository.add(payrollUnitPrice);
        payrollUnitPriceHistoryRepository.add(payrollUnitPriceHistory.getCode().v(), payrollUnitPriceHistory.getCId(), history, payrollUnitPriceSetting);

    }

}
