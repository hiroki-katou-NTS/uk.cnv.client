package nts.uk.ctx.at.schedule.pubimp.budget.premium;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.pub.budget.premium.PersonCostSettingExport;
import nts.uk.ctx.at.schedule.pub.budget.premium.PremiumItemDto;
import nts.uk.ctx.at.schedule.pub.budget.premium.PremiumItemPub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculationRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemRepository;
import nts.arc.time.calendar.period.DatePeriod;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PremiumItemPubImpl implements PremiumItemPub {

	@Inject
	private PremiumItemRepository premiumItemRepository;
	@Inject
	private PersonCostCalculationRepository personCostCalculationRepository;

	@Override
	public List<PremiumItemDto> findByCompanyID(String companyID) {
		return premiumItemRepository.findByCompanyID(companyID).stream()
				.map(x -> new PremiumItemDto(
						x.getCompanyID(),
						x.getDisplayNumber().value,
						x.getName().v(),
						x.getUseAtr().value))
				.collect(Collectors.toList());
	}

	@Override
	public List<PremiumItemDto> findByCompanyIDAndDisplayNumber(String companyID, List<Integer> displayNumbers) {
		return premiumItemRepository.findByCompanyIDAndDisplayNumber(companyID, displayNumbers).stream()
				.map(x -> new PremiumItemDto(
						x.getCompanyID(),
						x.getDisplayNumber().value,
						x.getName().v(),
						x.getUseAtr().value))
				.collect(Collectors.toList());
	}

	@Override
	public List<PersonCostSettingExport> getPersonCostSetting(String companyID, GeneralDate date) {
		List<PersonCostSettingExport> result = new ArrayList<>();
		// ドメインモデル「割増時間項目」を取得する
		List<PremiumItem> preiumItems = this.premiumItemRepository.findAllIsUse(companyID);
		if(CollectionUtil.isEmpty(preiumItems)){
			return result;
		}
//		List<PersonCostCalculation> personCosts = this.personCostCalculationRepository.findByCompanyIDAndDisplayNumber(companyID, date);
//		if(personCosts != null){
			// 取得したドメインモデル「割増時間項目」のデータ分を処理をループする
//			personCosts.stream().forEach(pc -> {
//				pc.getPremiumSettings().stream().filter(ps -> preiumItems.contains(ps.getDisplayNumber())).forEach(ps -> {
//					result.add(new PersonCostSettingExport(ps.getRate().v(), ps.getDisplayNumber(), ps.getAttendanceItems(),
//							new DatePeriod(pc.getStartDate(), pc.getEndDate())));
//				});
//			});
//		}
		return result;
	}

	@Override
	public List<PersonCostSettingExport> getPersonCostSetting(String companyID, DatePeriod period) {
		List<PersonCostSettingExport> result = new ArrayList<>();
		// ドメインモデル「割増時間項目」を取得する
		List<PremiumItem> preiumItems = this.premiumItemRepository.findAllIsUse(companyID);
		if(CollectionUtil.isEmpty(preiumItems)){
			return result;
		}
		
//		List<PersonCostCalculation> personCosts = this.personCostCalculationRepository.findByCompanyIDAndDisplayNumberNotFull(companyID, period, itemNos);
//		if(personCosts != null){
			// 取得したドメインモデル「割増時間項目」のデータ分を処理をループする
//			personCosts.stream().forEach(pc -> {
//				pc.getPremiumSettings().stream().forEach(ps -> {
//					result.add(new PersonCostSettingExport(ps.getRate().v(), ps.getDisplayNumber(), ps.getAttendanceItems(),
//							new DatePeriod(pc.getStartDate(), pc.getEndDate())));
//				});
//			});
//		}
		return result;
	}
}
