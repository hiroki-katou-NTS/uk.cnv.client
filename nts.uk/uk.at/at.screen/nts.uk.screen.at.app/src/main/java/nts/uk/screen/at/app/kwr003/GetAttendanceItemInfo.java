package nts.uk.screen.at.app.kwr003;


import lombok.val;
import nts.uk.ctx.at.function.app.query.outputworkstatustable.GetAttendanceIdByFormNumberQuery;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.TypeOfItem;
import nts.uk.ctx.at.function.dom.attendanceitemname.service.AttendanceItemNameService;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.enums.CommonAttributesOfForms;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItem;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.TypesMasterRelatedDailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service.CompanyMonthlyItemService;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ScreenQuery : 勤怠項目情報を取得する
 *
 * @author chinh.hm
 */
@Stateless
public class GetAttendanceItemInfo {

    @Inject
    private CompanyMonthlyItemService monthlyItemService;
    @Inject
    private CompanyDailyItemService dailyItemService;
    @Inject
    private AttendanceItemNameService attendanceItemNameService;
    @Inject
    private GetAttendanceIdByFormNumberQuery getAttendanceIdByFormNumberQuery;
    @Inject
    private MonthlyAttendanceItemRepository monthlyAttendanceItemRepository;
    @Inject
    private DailyAttendanceItemRepository dailyAttendanceItemRepository;

    public List<AttItemDto> getAttendanceItemInfo(TypeOfItem typeOfItem, int formNumberDisplay) {
        val cid = AppContexts.user().companyId();
        val roleId = Optional.ofNullable(AppContexts.user().roles().forAttendance());
        List<AttItemDto> rs = new ArrayList<AttItemDto>();

        //  （1:日次）
        val listId = getAttendanceIdByFormNumberQuery.getAttendanceId(typeOfItem, formNumberDisplay);
        //  「使用不可の勤怠項目を除く」
        val listAttIdAfterRemove = this.attendanceItemNameService.getAvaiableAttendanceItem(cid, typeOfItem, listId);
        if (typeOfItem == TypeOfItem.Daily) {

            // 会社の月次項目を取得する
            val dailyItemContainName = dailyItemService.getDailyItems(cid, roleId, listAttIdAfterRemove, null);

            // 日次の勤怠項目を取得する Nhận daily Attendance items
            List<DailyAttendanceItem> dailyAttendanceItems = this.dailyAttendanceItemRepository
                    .findByADailyAttendanceItems(listAttIdAfterRemove, cid);

            dailyAttendanceItems
                    .forEach((DailyAttendanceItem item) -> {
                        Optional<AttItemName> attendance = dailyItemContainName.stream()
                                .filter(attd -> attd.getAttendanceItemId() == item.getAttendanceItemId())
                                .findFirst();

                        attendance.ifPresent(attItemName -> {
                            Integer masterType = item.getMasterType().isPresent() ? item.getMasterType().get().value : null;
                            rs.add(new AttItemDto(
                                    item.getAttendanceItemId(),
                                    attItemName.getAttendanceItemName(),
                                    item.getDisplayNumber(),
                                    attItemName.getTypeOfAttendanceItem(),
                                    convertDailyToAttForms(item.getDailyAttendanceAtr().value, masterType),
                                    masterType));
                        });
                    });

            return rs;
        }
        if (typeOfItem == TypeOfItem.Monthly||typeOfItem == TypeOfItem.AnyPeriod) {
            val itemMonthlyContainName = monthlyItemService.getMonthlyItems(cid, roleId, listAttIdAfterRemove, null);
            // 月次の勤怠項目を取得する Nhận Monthly attendance items
            List<MonthlyAttendanceItem> monthlyAttendanceItemList = this.monthlyAttendanceItemRepository.findByAttendanceItemId(cid, listAttIdAfterRemove);
            monthlyAttendanceItemList
                    .forEach(item -> {
                        Optional<AttItemName> attendance = itemMonthlyContainName.stream()
                                .filter(attd -> attd.getAttendanceItemId() == item.getAttendanceItemId())
                                .findFirst();
                        attendance.ifPresent(attItemName -> rs.add(new AttItemDto(
                                item.getAttendanceItemId(),
                                attItemName.getAttendanceItemName(),
                                item.getDisplayNumber(),
                                attItemName.getTypeOfAttendanceItem(),
                                convertMonthlyToAttForms(item.getMonthlyAttendanceAtr().value),
                                null)));
                    });

            return rs;
        }
        return rs;
    }

    private Integer convertDailyToAttForms(Integer typeOfAttendanceItem, Integer masterType) {
        // ・属性：コード　＋　マスタの種類：勤務種類　→　勤務種類
        if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.Code.value)
                && masterType!=null && masterType.equals(TypesMasterRelatedDailyAttendanceItem.WORK_TYPE.value)) {
            return CommonAttributesOfForms.WORK_TYPE.value;
            // ・属性：コード　＋　マスタの種類：就業時間帯　→　就業時間帯
        } else if (typeOfAttendanceItem!=null &&typeOfAttendanceItem.equals(DailyAttendanceAtr.Code.value)
                &&masterType!=null &&  masterType.equals(TypesMasterRelatedDailyAttendanceItem.WORKING_HOURS.value)) {
            return CommonAttributesOfForms.WORKING_HOURS.value;
            // ・属性：コード(上記除く)　→　なし(その他_文字数値)
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.Code.value)) {
            return CommonAttributesOfForms.OTHER_CHARACTER_NUMBER.value;
            // ・マスタを参照する　→　なし(その他_文字数値)
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.ReferToMaster.value)) {
            return CommonAttributesOfForms.OTHER_CHARACTER_NUMBER.value;
            // ・回数　→　回数
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.NumberOfTime.value)) {
            return CommonAttributesOfForms.NUMBER_OF_TIMES.value;
            // ・金額　→　金額
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.AmountOfMoney.value)) {
            return CommonAttributesOfForms.AMOUNT_OF_MONEY.value;
            // ・区分　→　なし(その他_数値)
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.Classification.value)) {
            return CommonAttributesOfForms.OTHER_NUMERICAL_VALUE.value;
            // ・時間　→　時間
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.Time.value)) {
            return CommonAttributesOfForms.TIME.value;
            // ・時刻　→　時刻
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.TimeOfDay.value)) {
            return CommonAttributesOfForms.TIME_OF_DAY.value;
            // ・文字　→　なし(その他_文字)
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.Charater.value)) {
            return CommonAttributesOfForms.OTHER_CHARACTERS.value;
            // ・申請　→　なし(その他_数値)
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(DailyAttendanceAtr.Application.value)) {
            return CommonAttributesOfForms.OTHER_NUMERICAL_VALUE.value;
        }
        return null;
    }

    private Integer convertMonthlyToAttForms(Integer typeOfAttendanceItem) {
        //・時間　→　時間
        if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.TIME.value)) {
            return CommonAttributesOfForms.TIME.value;
        }
        // ・回数　→　回数
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.NUMBER.value)) {
            return CommonAttributesOfForms.NUMBER_OF_TIMES.value;
            // ・日数　→　日数
        } else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.DAYS.value)) {
            return CommonAttributesOfForms.DAYS.value;
        }
        // ・金額　→　金額
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.AMOUNT.value)) {
            return CommonAttributesOfForms.AMOUNT_OF_MONEY.value;
        }
        // ・マスタを参照する　→　なし(その他_文字数値)
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.REFER_TO_MASTER.value)) {
            return CommonAttributesOfForms.OTHER_CHARACTER_NUMBER.value;
        }
        //・コード　→　なし(その他_文字数値)
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.CODE.value)) {
            return CommonAttributesOfForms.OTHER_CHARACTER_NUMBER.value;
        }
        // 区分　→　なし(その他_数値)
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.CLASSIFICATION.value)) {
            return CommonAttributesOfForms.OTHER_NUMERICAL_VALUE.value;
        }
        // ・比率　→　なし(その他_数値)
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.RATIO.value)) {
            return CommonAttributesOfForms.OTHER_NUMERICAL_VALUE.value;
        }
        // ・文字　→　なし(その他_文字)
        else if (typeOfAttendanceItem!=null && typeOfAttendanceItem.equals(MonthlyAttendanceItemAtr.CHARACTER.value)) {
            return CommonAttributesOfForms.OTHER_CHARACTERS.value;
        } else
            return null;
    }

}
