package nts.uk.ctx.at.request.app.find.application.optitem;

import nts.arc.error.BundledBusinessException;
import nts.arc.error.BusinessException;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.request.app.command.application.optionalitem.OptionalItemApplicationCommand;
import nts.uk.ctx.at.request.app.find.application.optitem.optitemdto.*;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.optionalitemappsetting.OptItemSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetFinder;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemApplicationTypeCode;
import nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem.ControlOfAttendanceItemsDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.ControlOfAttendanceItemsRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.service.DailyItemList;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class OptionalItemApplicationQuery {

//    private static final Integer OPTIONAL_ITEM_NO_CONVERT_CONST = 640;

    @Inject
    private ControlOfAttendanceItemsRepository controlOfAttendanceItemsRepository;


    @Inject
    private OptionalItemRepository optionalItemRepo;


    @Inject
    private OptionalItemApplicationRepository repository;

    @Inject
    private OptionalItemRepository optionalItemRepository;

    @Inject
    private OptionalItemAppSetFinder optionalItemAppSetFinder;


    /**
     * ドメインモデル「任意項目」を取得する
     */
    public List<ControlOfAttendanceItemsDto> findControlOfAttendance(List<Integer> optionalItemNos) {
        String cid = AppContexts.user().companyId();
        List<Integer> daiLyList = optionalItemNos.stream().map(no -> DailyItemList.getOption(no).map(i -> i.itemId).orElse(0)).collect(Collectors.toList());
        List<ControlOfAttendanceItems> controlOfAttendanceItems = controlOfAttendanceItemsRepository.getByItemDailyList(cid, daiLyList);
        return controlOfAttendanceItems.stream().map(item -> ControlOfAttendanceItemsDto.fromDomain(item)).collect(Collectors.toList());
    }

    public OptionalItemApplicationDetail getDetail(String applicationId) {
        String cid = AppContexts.user().companyId();
        OptionalItemApplicationDetail detail = new OptionalItemApplicationDetail();
        Optional<OptionalItemApplication> byAppId = this.repository.getByAppId(cid, applicationId);
        OptionalItemApplication domain = byAppId.get();
        String settingCode = domain.getCode().v();
        OptionalItemAppSetDto setting = optionalItemAppSetFinder.findByCode(new OptionalItemApplicationTypeCode(settingCode).v());
        List<Integer> optionalItemNos = setting.getSettingItems().stream().map(i -> i.getNo()).collect(Collectors.toList());
        List<OptionalItem> optionalItems = optionalItemRepo.findByListNos(cid, optionalItemNos);
//        List<ControlOfAttendanceItems> controlOfAttendanceItems = controlOfAttendanceItemsRepository.getByItemDailyList(cid,
//                optionalItemNos.stream().map(no -> DailyItemList.getOption(no).map(i -> i.itemId).orElse(0)).collect(Collectors.toList())
//        );
//        detail.setControlOfAttendanceItems(controlOfAttendanceItems.stream().map(ControlOfAttendanceItemsDto::fromDomain).collect(Collectors.toList()));
        detail.setApplication(OptionalItemApplicationDto.fromDomain(domain));
        detail.getApplication().setName(setting.getName());
        detail.getApplication().setNote(setting.getNote());
        detail.setOptionalItems(optionalItems.stream().map(item ->
                {
                    CalcResultRangeDto calcResultRangeDto = new CalcResultRangeDto();
                    item.getInputControlSetting().getCalcResultRange().saveToMemento(calcResultRangeDto);
                    if (item.getInputControlSetting().getDailyInputUnit().isPresent()) {
                        calcResultRangeDto.setTimeInputUnit(item.getInputControlSetting().getDailyInputUnit().get().getTimeItemInputUnit().map(i -> i.value).orElse(null));
                        calcResultRangeDto.setNumberInputUnit(item.getInputControlSetting().getDailyInputUnit().get().getNumberItemInputUnit().map(i -> i.value).orElse(null));
                        calcResultRangeDto.setAmountInputUnit(item.getInputControlSetting().getDailyInputUnit().get().getAmountItemInputUnit().map(i -> i.value).orElse(null));
                    }
                    OptionalItemDto optionalItemDto = new OptionalItemDto();
                    optionalItemDto.setOptionalItemNo(item.getOptionalItemNo().v());
                    optionalItemDto.setOptionalItemName(item.getOptionalItemName().v());
                    optionalItemDto.setUnit(item.getUnit().map(PrimitiveValueBase::v).orElse(null));
                    optionalItemDto.setInputCheck(item.getInputControlSetting().isInputWithCheckbox());
                    optionalItemDto.setCalcResultRange(calcResultRangeDto);
                    optionalItemDto.setOptionalItemAtr(item.getOptionalItemAtr().value);
                    optionalItemDto.setDescription(item.getDescription().map(PrimitiveValueBase::v).orElse(null));
                    optionalItemDto.setDispOrder(setting.getSettingItems().stream().filter(i -> i.getNo() == item.getOptionalItemNo().v()).findFirst().map(OptItemSetDto::getDispOrder).orElse(1));
                    return optionalItemDto;
                }
        ).collect(Collectors.toList()));
        return detail;
    }

    public void checkBeforeUpdate(OptionalItemApplicationCommand params) {
        String cid = AppContexts.user().companyId();
        /*登録時チェック処理（全申請共通）*/
        List<AnyItemValueDto> optionalItems = params.getOptionalItems();
        boolean register = false;
        List<Integer> optionalItemNos = optionalItems.stream().map(anyItemNo -> anyItemNo.getItemNo()).collect(Collectors.toList());
        Map<Integer, OptionalItem> optionalItemMap = optionalItemRepository.findByListNos(cid, optionalItemNos).stream().collect(Collectors.toMap(optionalItem -> optionalItem.getOptionalItemNo().v(), item -> item));
        List<Integer> daiLyList = optionalItemNos.stream().map(no -> DailyItemList.getOption(no).map(i -> i.itemId).orElse(0)).collect(Collectors.toList());
//        Map<Integer, ControlOfAttendanceItems> controlOfAttendanceItemsMap = controlOfAttendanceItemsRepository.getByItemDailyList(cid, daiLyList).stream().collect(Collectors.toMap(item -> item.getItemDailyID(), item -> item));
        BundledBusinessException exceptions = BundledBusinessException.newInstance();
        for (Iterator<AnyItemValueDto> iterator = optionalItems.iterator(); iterator.hasNext(); ) {
            AnyItemValueDto inputOptionalItem = iterator.next();
            /* Kiểm tra giá trị nằm trong giới hạn, vượt ra ngoài khoảng giới hạn thì thông báo lỗi Msg_1692 */
//            ControlOfAttendanceItems controlOfAttendanceItems = controlOfAttendanceItemsMap.get(DailyItemList.getOption(inputOptionalItem.getItemNo()).map(i -> i.itemId).orElse(0));
            OptionalItem optionalItem = optionalItemMap.get(inputOptionalItem.getItemNo());
            CalcResultRange range = optionalItem.getInputControlSetting().getCalcResultRange();
            String itemName = optionalItemMap.get(inputOptionalItem.getItemNo()) != null ? optionalItemMap.get(inputOptionalItem.getItemNo()).getOptionalItemName().v() : "";
            String itemNo = optionalItemMap.get(inputOptionalItem.getItemNo()) != null ? optionalItemMap.get(inputOptionalItem.getItemNo()).getOptionalItemNo().v().toString() : "";
            if (inputOptionalItem.getAmount() != null) {
                Integer amountLower = null;
                Integer amountUpper = null;
                Integer amount = inputOptionalItem.getAmount();
                if (range.getAmountRange().isPresent()
                        && range.getAmountRange().get().getDailyAmountRange().isPresent()
                        && range.getAmountRange().get().getDailyAmountRange().get().getLowerLimit().isPresent()) {
                    amountLower = range.getAmountRange().get().getDailyAmountRange().get().getLowerLimit().get().v();
                }
                if (range.getAmountRange().isPresent()
                        && range.getAmountRange().get().getDailyAmountRange().isPresent()
                        && range.getAmountRange().get().getDailyAmountRange().get().getUpperLimit().isPresent()) {
                    amountUpper = range.getAmountRange().get().getDailyAmountRange().get().getUpperLimit().get().v();
                }
                if ((range.getLowerLimit().isSET() && amountLower != null && amountLower.compareTo(amount) > 0)
                        || (range.getUpperLimit().isSET() && amountUpper != null && amountUpper.compareTo(amount) < 0)) {
                    exceptions.addMessage(new BusinessException("Msg_1692", itemName, itemNo));
                }
                if (optionalItem.getInputControlSetting().getDailyInputUnit().isPresent()) {
                    optionalItem.getInputControlSetting().getDailyInputUnit().get().getAmountItemInputUnit().ifPresent(unit -> {
                        switch (unit) {
//                            case ONE:
//                                if (amount % 1 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
//                                break;
                            case TEN:
                                if (amount % 10 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case ONE_HUNDRED:
                                if (amount % 100 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case ONE_THOUSAND:
                                if (amount % 1000 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case TEN_THOUSAND:
                                if (amount % 10000 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            default:
                                break;
                        }
                    });
                }
                register = true;
            }
            if (inputOptionalItem.getTimes() != null) {
                register = true;
                if (!optionalItem.getInputControlSetting().isInputWithCheckbox()) {
                    BigDecimal numberLower = null;
                    BigDecimal numberUpper = null;
                    BigDecimal times = inputOptionalItem.getTimes();
                    if (range.getNumberRange().isPresent()
                            && range.getNumberRange().get().getDailyTimesRange().isPresent()
                            && range.getNumberRange().get().getDailyTimesRange().get().getLowerLimit().isPresent()) {
                        numberLower = range.getNumberRange().get().getDailyTimesRange().get().getLowerLimit().get().v();
                    }
                    if (range.getNumberRange().isPresent()
                            && range.getNumberRange().get().getDailyTimesRange().isPresent()
                            && range.getNumberRange().get().getDailyTimesRange().get().getUpperLimit().isPresent()) {
                        numberUpper = range.getNumberRange().get().getDailyTimesRange().get().getUpperLimit().get().v();
                    }
                    if ((range.getLowerLimit().isSET() && numberLower != null && numberLower.compareTo(times) > 0)
                            || (range.getUpperLimit().isSET() && numberUpper != null && numberUpper.compareTo(times) < 0)) {
                        exceptions.addMessage(new BusinessException("Msg_1692", itemName, itemNo));
                    }
                    if (optionalItem.getInputControlSetting().getDailyInputUnit().isPresent()) {
                        optionalItem.getInputControlSetting().getDailyInputUnit().get().getNumberItemInputUnit().ifPresent(unit -> {
                            switch (unit) {
                                case ONE_HUNDREDTH:
                                    if (times.remainder(new BigDecimal("0.01")).compareTo(BigDecimal.ZERO) != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                    break;
                                case ONE_TENTH:
                                    if (times.remainder(new BigDecimal("0.1")).compareTo(BigDecimal.ZERO) != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                    break;
                                case ONE_HALF:
                                    if (times.remainder(new BigDecimal("0.5")).compareTo(BigDecimal.ZERO) != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                    break;
//                            case ONE:
//                                if (times.doubleValue() % 1 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
//                                break;
                                default:
                                    break;
                            }
                        });
                    }
                }
            }
            if (inputOptionalItem.getTime() != null) {
                Integer timeLower = null;
                Integer timeUpper = null;
                Integer time = inputOptionalItem.getTime();
                if (range.getTimeRange().isPresent()
                        && range.getTimeRange().get().getDailyTimeRange().isPresent()
                        && range.getTimeRange().get().getDailyTimeRange().get().getLowerLimit().isPresent()) {
                    timeLower = range.getTimeRange().get().getDailyTimeRange().get().getLowerLimit().get().v();
                }
                if (range.getTimeRange().isPresent()
                        && range.getTimeRange().get().getDailyTimeRange().isPresent()
                        && range.getTimeRange().get().getDailyTimeRange().get().getUpperLimit().isPresent()) {
                    timeUpper = range.getTimeRange().get().getDailyTimeRange().get().getUpperLimit().get().v();
                }
                if ((range.getLowerLimit().isSET() && timeLower != null && timeLower.compareTo(time) > 0)
                        || (range.getUpperLimit().isSET() && timeUpper != null && timeUpper.compareTo(time) < 0)) {
                    exceptions.addMessage(new BusinessException("Msg_1692", itemName, itemNo));
                }
                if (optionalItem.getInputControlSetting().getDailyInputUnit().isPresent()) {
                    optionalItem.getInputControlSetting().getDailyInputUnit().get().getTimeItemInputUnit().ifPresent(unit -> {
                        switch (unit) {
//                            case ONE_MINUTE:
//                                if (time % 1 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
//                                break;
                            case FIVE_MINUTES:
                                if (time % 5 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case TEN_MINUTES:
                                if (time % 10 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case FIFTEEN_MINUTES:
                                if (time % 15 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case THIRTY_MINUTES:
                                if (time % 30 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            case SIXTY_MINUTES:
                                if (time % 60 != 0) exceptions.addMessage(new BusinessException("Msg_1693", itemName, itemNo));
                                break;
                            default:
                                break;
                        }
                    });
                }
                register = true;
            }
        }
        /*Không có dữ liệu thì hiển thị lỗi*/
        if (!register) {
            throw new BusinessException("Msg_1691");
        }
        if (!exceptions.getMessageId().isEmpty()) {
            exceptions.throwExceptions();
        }
    }
}
