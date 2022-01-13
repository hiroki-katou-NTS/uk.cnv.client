package nts.uk.ctx.at.record.app.find.optitem;

import java.math.BigDecimal;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.ControlOfAttendanceItemsRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.ControlOfMonthlyItems;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.ControlOfMonthlyItemsRepository;
import nts.uk.ctx.at.shared.dom.scherec.service.DailyItemList;
import nts.uk.ctx.at.shared.dom.scherec.service.MonthlyItemList;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author anhnm
 *
 */
@Stateless
public class OptionalItemService {

    @Inject
    private ControlOfMonthlyItemsRepository monthlyControlRepository;

    @Inject
    private ControlOfAttendanceItemsRepository dailyControlRepository;

    public ControlUnitDto getItemControl(int performanceAtr, int itemID) {
        String companyID = AppContexts.user().companyId();

        int attandanceItemID = 0;
        if (performanceAtr == 0) {
            Optional<MonthlyItemList> attandanceItem = MonthlyItemList.getOption(itemID);
            if (attandanceItem.isPresent()) {
                attandanceItemID = attandanceItem.get().itemId;
            } else {
                return null;
            }

            Optional<ControlOfMonthlyItems> controlMonthlyOpt = monthlyControlRepository
                    .getControlOfMonthlyItem(companyID, attandanceItemID);
            return controlMonthlyOpt.isPresent() ? ControlUnitDto.fromMonthlyDomain(controlMonthlyOpt.get()) : null;
        } else {
            Optional<DailyItemList> attandanceItem = DailyItemList.getOption(itemID);
            if (attandanceItem.isPresent()) {
                attandanceItemID = attandanceItem.get().itemId;
            } else {
                return null;
            }

            Optional<ControlOfAttendanceItems> controlDailyOptional = dailyControlRepository
                    .getControlOfAttendanceItem(companyID, attandanceItemID);
            return controlDailyOptional.isPresent() ? ControlUnitDto.fromDailyDomain(controlDailyOptional.get()) : null;
        }
    }

    public void updateItemControl(int performanceAtr, int itemID, BigDecimal roundingUnit) {
        String companyID = AppContexts.user().companyId();

        int attandanceItemID = 0;

        Optional<MonthlyItemList> monthlyAttandanceItem = MonthlyItemList.getOption(itemID);
        if (monthlyAttandanceItem.isPresent()) {
            attandanceItemID = monthlyAttandanceItem.get().itemId;

            Optional<ControlOfMonthlyItems> controlMonthlyOpt = monthlyControlRepository
                    .getControlOfMonthlyItem(companyID, attandanceItemID);
            if (controlMonthlyOpt.isPresent()) {
                monthlyControlRepository.updateControlOfMonthlyItem(controlMonthlyOpt.get());
            } else {
                ControlOfMonthlyItems controlMonth = new ControlOfMonthlyItems(companyID, attandanceItemID, null);
                monthlyControlRepository.addControlOfMonthlyItem(controlMonth);
            }
        } 
        Optional<DailyItemList> dailyAttandanceItem = DailyItemList.getOption(itemID);
        if (dailyAttandanceItem.isPresent()) {
            attandanceItemID = dailyAttandanceItem.get().itemId;

            Optional<ControlOfAttendanceItems> controlDailyOptional = dailyControlRepository
                    .getControlOfAttendanceItem(companyID, attandanceItemID);
            if (controlDailyOptional.isPresent()) {
                dailyControlRepository.updateControlOfAttendanceItem(controlDailyOptional.get());
            } else {
                ControlOfAttendanceItems controlDaily = new ControlOfAttendanceItems(companyID, attandanceItemID, null);
                dailyControlRepository.insertControlOfAttendanceItem(controlDaily);
            }
        } 
    }
}
