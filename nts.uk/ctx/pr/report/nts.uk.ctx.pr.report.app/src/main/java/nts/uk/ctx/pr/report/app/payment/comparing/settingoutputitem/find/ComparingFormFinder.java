package nts.uk.ctx.pr.report.app.payment.comparing.settingoutputitem.find;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.uk.ctx.pr.report.dom.payment.comparing.settingoutputitem.CategoryAtr;
import nts.uk.ctx.pr.report.dom.payment.comparing.settingoutputitem.ComparingFormDetailRepository;
import nts.uk.ctx.pr.report.dom.payment.comparing.settingoutputitem.ItemMasterAdapter;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ComparingFormFinder {
	@Inject
	private ItemMasterAdapter itemMasterPub;
	@Inject
	private ComparingFormDetailRepository formDetailRepository;

	public ComparingFormDto findComparingForm(String formCode) {

		String companyCode = AppContexts.user().companyCode();
		ComparingFormDto comparingFormDto = new ComparingFormDto();

		List<ComparingItemMasterDto> itemMasterTab_0 = this.itemMasterPub
				.findItemMasterByCatergory(CategoryAtr.PAYMENT.value).stream()
				.map(item -> ComparingItemMasterDto.fromDomain(item.getItemCode().v(), item.getItemName().v(),
						CategoryAtr.PAYMENT.value, CategoryAtr.PAYMENT.name))
				.collect(Collectors.toList());
		List<ComparingItemMasterDto> itemMasterTab_1 = this.itemMasterPub
				.findItemMasterByCatergory(CategoryAtr.DEDUCTION.value).stream()
				.map(item -> ComparingItemMasterDto.fromDomain(item.getItemCode().v(), item.getItemName().v(),
						CategoryAtr.DEDUCTION.value, CategoryAtr.DEDUCTION.name))
				.collect(Collectors.toList());
		List<ComparingItemMasterDto> itemMasterTab_3 = this.itemMasterPub
				.findItemMasterByCatergory(CategoryAtr.ARTICLES.value).stream()
				.map(item -> ComparingItemMasterDto.fromDomain(item.getItemCode().v(), item.getItemName().v(),
						CategoryAtr.ARTICLES.value, CategoryAtr.ARTICLES.name))
				.collect(Collectors.toList());

		if (StringUtils.isBlank(formCode)) {
			comparingFormDto.setLstItemMasterForTab_0(itemMasterTab_0);
			comparingFormDto.setLstItemMasterForTab_1(itemMasterTab_1);
			comparingFormDto.setLstItemMasterForTab_3(itemMasterTab_3);
			comparingFormDto.setLstSelectForTab_0(new ArrayList<>());
			comparingFormDto.setLstSelectForTab_1(new ArrayList<>());
			comparingFormDto.setLstSelectForTab_3(new ArrayList<>());
			return comparingFormDto;
		}
		List<ComparingItemMasterDto> selectForTab_0 = this.formDetailRepository
				.getComparingFormDetailByCategory_Atr(companyCode, formCode, CategoryAtr.PAYMENT.value).stream()
				.map(item -> {
					ComparingItemMasterDto comparingItem = itemMasterTab_0.stream()
							.filter(i -> i.getItemCode().equals(item.getItemCode().v())).findFirst().orElse(null);
					itemMasterTab_0.remove(comparingItem);
					return comparingItem;
				}).collect(Collectors.toList());

		List<ComparingItemMasterDto> selectForTab_1 = this.formDetailRepository
				.getComparingFormDetailByCategory_Atr(companyCode, formCode, CategoryAtr.DEDUCTION.value).stream()
				.map(item -> {
					ComparingItemMasterDto comparingItem = itemMasterTab_1.stream()
							.filter(i -> i.getItemCode().equals(item.getItemCode().v())).findFirst().orElse(null);
					itemMasterTab_1.remove(comparingItem);
					return comparingItem;
				}).collect(Collectors.toList());
		List<ComparingItemMasterDto> selectForTab_3 = this.formDetailRepository
				.getComparingFormDetailByCategory_Atr(companyCode, formCode, CategoryAtr.ARTICLES.value).stream()
				.map(item -> {
					ComparingItemMasterDto comparingItem = itemMasterTab_3.stream()
							.filter(i -> i.getItemCode().equals(item.getItemCode().v())).findFirst().orElse(null);
					itemMasterTab_3.remove(comparingItem);
					return comparingItem;
				}).collect(Collectors.toList());

		comparingFormDto.setLstItemMasterForTab_0(itemMasterTab_0);
		comparingFormDto.setLstItemMasterForTab_1(itemMasterTab_1);
		comparingFormDto.setLstItemMasterForTab_3(itemMasterTab_3);
		comparingFormDto.setLstSelectForTab_0(selectForTab_0);
		comparingFormDto.setLstSelectForTab_1(selectForTab_1);
		comparingFormDto.setLstSelectForTab_3(selectForTab_3);
		return comparingFormDto;
	}

}
