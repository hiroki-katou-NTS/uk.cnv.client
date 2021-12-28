package nts.uk.ctx.at.schedule.app.find.budget.premium;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.PerCostRoundSettingDto;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.PersonCostCalculationDto;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.PremiumSettingDto;
import nts.uk.ctx.at.schedule.app.find.budget.premium.dto.*;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceNamePriniumDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceTypePriServiceDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.service.AttendanceTypePrimiumAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.*;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Doan Duy Hung
 * update by chinh.hm
 */

@Stateless
@Transactional
public class PersonCostCalculationFinder {

    @Inject
    private PersonCostCalculationRepository personCostCalculationRepository;

    @Inject
    private PremiumItemRepository premiumItemRepository;

    @Inject
    private AttendanceTypePrimiumAdapter atType;
    @Inject
    private AttendanceNamePriniumAdapter atName;

    @Inject
    private PremiumItemLanguageRepository premiumItemLanguageRepository;


    public List<PersonCostCalDto> findPersonCostCal() {
        List<PersonCostCalDto> rs = new ArrayList<>();
        String companyID = AppContexts.user().companyId();
        val listItem = this.personCostCalculationRepository.getHistAnPerCost(companyID);
        val calculationList = listItem.getHistPersonCostCalculation();
        val listPer = listItem.getPersonCostCalculation();
        List<Integer> listId = new ArrayList<>();
        List<Integer> listAttId = new ArrayList<>();
        for (PersonCostCalculation e : listPer) {
            listId.addAll(e.getPremiumSettings().stream().map(i -> (i.getID().value)).collect(Collectors.toList()));
            e.getPremiumSettings().forEach(m -> listAttId.addAll(m.getAttendanceItems()));
        }
        val listNameAtt = atName.getDailyAttendanceItemName(listAttId).stream().distinct()
                .collect(Collectors.toMap(AttendanceNamePriniumDto::getAttendanceItemId, i -> i));
        List<PremiumItem> listItemInfo = premiumItemRepository.findByCompanyIDAndDisplayNumber(companyID, listId);
        Map<Integer, PremiumItem> mapItemInfo = listItemInfo.stream().collect(Collectors.toMap(i -> i.getDisplayNumber().value, i -> i));
        if (listPer.isEmpty()) return rs;
        for (PersonCostCalculation sub : listPer) {
            val dateHistoryItem = calculationList.getHistoryItems().stream().filter(e -> e.identifier().equals(sub.getHistoryID())).findFirst();
            if (!dateHistoryItem.isPresent()) continue;
            val item = convertToPersonCostDto(sub, dateHistoryItem.get(), mapItemInfo, listNameAtt);
            rs.add(item);
        }

        return rs;
    }

    private PersonCostCalDto convertToPersonCostDto(PersonCostCalculation domain, DateHistoryItem dateHistoryItem,
                                                    Map<Integer, PremiumItem> mapItemInfo, Map<Integer, AttendanceNamePriniumDto> mapName) {
        return new PersonCostCalDto(
                dateHistoryItem.start(),
                dateHistoryItem.end(),
                dateHistoryItem.identifier(),
                domain.getCompanyID(),
                domain.getUnitPrice().isPresent() ? domain.getUnitPrice().get().value : null,
                domain.getHowToSetUnitPrice().value,
                domain.getWorkingHoursUnitPrice().value,
                domain.getRemark().v(),
                new PerCostRoundSettingDto(
                        domain.getRoundingSetting().getRoundingOfPremium().getPriceRounding().value,
                        domain.getRoundingSetting().getAmountRoundingSetting().getUnit().value,
                        domain.getRoundingSetting().getAmountRoundingSetting().getRounding().value),
                domain.getPremiumSettings().stream().map(e -> new PremiumSettingAndNameDto(
                        e.getID().value,
                        mapItemInfo.get(e.getID().value) != null ? mapItemInfo.get(e.getID().value).getName().v() : null,
                        e.getRate().v(),
                        mapItemInfo.get(e.getID().value) != null ? mapItemInfo.get(e.getID().value).getUseAtr().value : null,
                        e.getUnitPrice().value,
                        e.getAttendanceItems().stream().map(mapName::get).collect(Collectors.toList())
                )).collect(Collectors.toList())
        );
    }

    /**
     * get List Premium Item  by company ID and by UseAttribute.
     *
     * @return List Premium Item  by company ID
     */
    public List<PremiumItemDto> findPremiumItemByCompanyID() {
        String companyID = AppContexts.user().companyId();
        return this.premiumItemRepository.findByCompanyID(companyID)
                .stream()
                .map(x -> new PremiumItemDto(
                        companyID,
                        x.getDisplayNumber().value,
                        x.getName().v(),
                        x.getUseAtr().value))
                .collect(Collectors.toList());
    }

    public List<PremiumItemDto> findWorkTypeLanguage(String langId) {
        // company id
        String companyId = AppContexts.user().companyId();
        List<PremiumItemLanguage> listPremiumItem = premiumItemLanguageRepository.findByCIdAndLangId(companyId, langId);
        return listPremiumItem.stream().map(PremiumItemDto::fromDomainPremiumItemLanguage).collect(Collectors.toList());
    }

    public PersonCostCalculationSettingDto findByHistoryID(String historyID) {
        String companyID = AppContexts.user().companyId();
        return this.personCostCalculationRepository.findItemByHistoryID(companyID, historyID)
                .map(this::convertToDto).orElse(null);
    }

    /**
     * convert PersonCostCalculation to PersonCostCalculationSettingDto
     *
     * @param personCostCalculation PersonCostCalculation Object
     * @return PersonCostCalculationSettingDto Object
     */

    private PersonCostCalculationSettingDto convertToDto(PersonCostCalculation personCostCalculation) {
        return new PersonCostCalculationSettingDto(
                personCostCalculation.getCompanyID(),
                personCostCalculation.getHistoryID(),
                personCostCalculation.getUnitPrice().isPresent() ? personCostCalculation.getUnitPrice().get().value : null,
                personCostCalculation.getRemark().v(),
                personCostCalculation.getPremiumSettings().stream().map(this::toPremiumSetDto).collect(Collectors.toList()));
    }

    /**
     * convert PremiumSetting to PremiumSetDto
     *
     * @param premiumSetting PremiumSetting Object
     * @return PremiumSetDto Object
     */
    private PremiumSetDto toPremiumSetDto(PremiumSetting premiumSetting) {
        val listItemName = premiumItemRepository.findByCompanyID(premiumSetting.getCompanyID());
        val item = listItemName.stream().anyMatch(j -> j.getDisplayNumber().equals(premiumSetting.getID())) ?
                listItemName.stream().filter(j -> j.getDisplayNumber().equals(premiumSetting.getID())).findFirst().get() : null;
        return new PremiumSetDto(
                premiumSetting.getCompanyID(),
                premiumSetting.getHistoryID(),
                premiumSetting.getID().value,
                premiumSetting.getRate().v(),
                item != null ? item.getName().v() : null,
                item != null ? item.getUseAtr().value : null,
                premiumSetting.getAttendanceItems().stream().map(x ->
                        new ShortAttendanceItemDto(x, atNames(Collections.singletonList(x))
                                .get(0).getAttendanceItemName())).collect(Collectors.toList()),
                premiumSetting.getUnitPrice().value
        );

    }

    /**
     * get attendance at screen use
     *
     * @param screenUseAtr
     * @return
     */
    public List<AttendanceTypePriServiceDto> atTypes(int screenUseAtr) {
        String companyID = AppContexts.user().companyId();
        List<AttendanceTypePriServiceDto> data = atType.getItemByScreenUseAtr(companyID, screenUseAtr);
        return data;
    }

    public List<AttendanceNamePriniumDto> atNames(List<Integer> dailyAttendanceItemIds) {
        if (dailyAttendanceItemIds == null || dailyAttendanceItemIds.isEmpty()){
            return new ArrayList<>();
        }
        List<AttendanceNamePriniumDto> data = atName.getDailyAttendanceItemName(dailyAttendanceItemIds);
        return data;
    }

    /**
     * Get 人件費計算設定の履歴
     */

    public HistAndPersonCostLastDto getHistPersonCost() {
        val cid = AppContexts.user().companyId();
        val listItem = this.personCostCalculationRepository.getHistPersonCostCalculation(cid);
        if (!listItem.isPresent()) {
            throw new BusinessException("Msg_2027");
        }
        val listItemName = premiumItemRepository.findByCompanyID(cid);
        val lisHist = listItem.get().items().stream().map(e -> new HistPersonCostCalculationDto(
                e.start(),
                e.end(),
                cid,
                e.identifier()
        )).collect(Collectors.toList());
        PersonCostCalculationDto personCostCalLast = null;
        if (lisHist != null) {
            val hist = lisHist.get(0);
            val domainOpt = this.personCostCalculationRepository.getPersonCost(cid, hist.getHistoryID());
            if (domainOpt.isPresent()) {
                val domain = domainOpt.get().getPersonCostCalculation();
                personCostCalLast = new PersonCostCalculationDto(
                        domainOpt.get().getStartDate(),
                        domainOpt.get().getEndDate(),
                        domain.getHistoryID(),
                        domain.getCompanyID(),
                        domain.getUnitPrice().isPresent() ? domain.getUnitPrice().get().value : null,
                        domain.getHowToSetUnitPrice().value,
                        domain.getWorkingHoursUnitPrice().value,
                        domain.getRemark().v(),
                        new PerCostRoundSettingDto(
                                domain.getRoundingSetting().getRoundingOfPremium().getPriceRounding().value,
                                (domain.getRoundingSetting().getAmountRoundingSetting().getUnit().value),
                                domain.getRoundingSetting().getAmountRoundingSetting().getRounding().value),
                        domain.getPremiumSettings().stream().map(e -> new PremiumSettingDto(
                                e.getID().value,
                                listItemName.stream().anyMatch(j -> j.getDisplayNumber().equals(e.getID())) ?
                                        listItemName.stream().filter(j -> j.getDisplayNumber().equals(e.getID())).findFirst().get().getName().v() : null,
                                listItemName.stream().anyMatch(j -> j.getDisplayNumber().equals(e.getID())) ?
                                        listItemName.stream().filter(j -> j.getDisplayNumber().equals(e.getID())).findFirst().get().getUseAtr().value : null,
                                e.getRate().v(),
                                e.getUnitPrice().value,
                                e.getAttendanceItems(),
                                atNames(e.getAttendanceItems())
                        )).collect(Collectors.toList()));
            }
        }
        return new HistAndPersonCostLastDto(
                lisHist,
                personCostCalLast
        );
    }


    public PersonCostCalculationDto getHistPersonCostByHistId(String histId) {
        val cid = AppContexts.user().companyId();
        val listItemName = premiumItemRepository.findByCompanyID(cid);
        PersonCostCalculationDto personCostCalLast = null;
        val domainOpt = this.personCostCalculationRepository.getPersonCost(cid, histId);
        if (domainOpt.isPresent()) {
            val domain = domainOpt.get().getPersonCostCalculation();
            personCostCalLast = new PersonCostCalculationDto(
                    domainOpt.get().getStartDate(),
                    domainOpt.get().getEndDate(),
                    domain.getHistoryID(),
                    domain.getCompanyID(),
                    domain.getUnitPrice().isPresent() ? domain.getUnitPrice().get().value : null,
                    domain.getHowToSetUnitPrice().value,
                    domain.getWorkingHoursUnitPrice().value,
                    domain.getRemark().v(),
                    new PerCostRoundSettingDto(
                            domain.getRoundingSetting().getRoundingOfPremium().getPriceRounding().value,
                            (domain.getRoundingSetting().getAmountRoundingSetting().getUnit().value),
                            domain.getRoundingSetting().getAmountRoundingSetting().getRounding().value),

                    domain.getPremiumSettings().stream().map(e -> new PremiumSettingDto(
                            e.getID().value,
                            listItemName.stream().anyMatch(j -> j.getDisplayNumber().equals(e.getID())) ?
                                    listItemName.stream().filter(j -> j.getDisplayNumber().equals(e.getID())).findFirst().get().getName().v() : null,
                            listItemName.stream().anyMatch(j -> j.getDisplayNumber().equals(e.getID())) ?
                                    listItemName.stream().filter(j -> j.getDisplayNumber().equals(e.getID())).findFirst().get().getUseAtr().value : null,

                            e.getRate().v(),
                            e.getUnitPrice().value,
                            e.getAttendanceItems(),
                            atNames(e.getAttendanceItems())
                    )).collect(Collectors.toList()));
        }

        return personCostCalLast;
    }
}
