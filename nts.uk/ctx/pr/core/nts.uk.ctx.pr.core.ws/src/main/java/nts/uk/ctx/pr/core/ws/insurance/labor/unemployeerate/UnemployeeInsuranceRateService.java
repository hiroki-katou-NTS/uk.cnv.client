package nts.uk.ctx.pr.core.ws.insurance.labor.unemployeerate;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.YearMonth;
import nts.uk.ctx.core.app.insurance.labor.unemployeerate.HistoryUnemployeeInsuranceRateDto;
import nts.uk.ctx.core.app.insurance.labor.unemployeerate.UnemployeeInsuranceRateDto;
import nts.uk.ctx.pr.core.dom.insurance.MonthRange;
import nts.uk.ctx.pr.core.dom.insurance.RoundingMethod;
import nts.uk.ctx.pr.core.dom.insurance.labor.unemployeerate.CareerGroup;
import nts.uk.ctx.pr.core.dom.insurance.labor.unemployeerate.UnemployeeInsuranceRateItem;
import nts.uk.ctx.pr.core.dom.insurance.labor.unemployeerate.UnemployeeInsuranceRateItemSetting;

@Path("pr/insurance/labor/unemployeerate")
@Produces("application/json")
public class UnemployeeInsuranceRateService extends WebService {
	// get History UnemployeeInsuranceRate
	@POST
	@Path("findallHistory")
	public List<HistoryUnemployeeInsuranceRateDto> findAllHistory() {
		List<HistoryUnemployeeInsuranceRateDto> lstHistoryUnemployeeInsuranceRate = new ArrayList<HistoryUnemployeeInsuranceRateDto>();
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRate006 = new HistoryUnemployeeInsuranceRateDto();
		historyUnemployeeInsuranceRate006.setCompanyCode("companyCode001");
		MonthRange monthRange006 = new MonthRange();
		monthRange006.setStartMonth(new YearMonth(2016 * 100 + 4));
		monthRange006.setEndMonth(new YearMonth(9999 * 100 + 12));
		// historyUnemployeeInsuranceRate006.setMonthRage(monthRange006);
		historyUnemployeeInsuranceRate006.setHistoryId("historyId006");
		historyUnemployeeInsuranceRate006.setStartMonthRage(convertMonth(monthRange006.getStartMonth()));
		historyUnemployeeInsuranceRate006.setEndMonthRage(convertMonth(monthRange006.getEndMonth()));
		historyUnemployeeInsuranceRate006.setInforMonthRage(historyUnemployeeInsuranceRate006.getStartMonthRage()
				+ " ~ " + historyUnemployeeInsuranceRate006.getEndMonthRage());
		lstHistoryUnemployeeInsuranceRate.add(historyUnemployeeInsuranceRate006);
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRate005 = new HistoryUnemployeeInsuranceRateDto();
		historyUnemployeeInsuranceRate005.setCompanyCode("companyCode001");
		MonthRange monthRange005 = new MonthRange();
		monthRange005.setStartMonth(new YearMonth(2015 * 100 + 10));
		monthRange005.setEndMonth(new YearMonth(2016 * 100 + 3));
		// historyUnemployeeInsuranceRate006.setMonthRage(monthRange006);
		historyUnemployeeInsuranceRate005.setHistoryId("historyId005");
		historyUnemployeeInsuranceRate005.setStartMonthRage(convertMonth(monthRange005.getStartMonth()));
		historyUnemployeeInsuranceRate005.setEndMonthRage(convertMonth(monthRange005.getEndMonth()));
		historyUnemployeeInsuranceRate005.setInforMonthRage(historyUnemployeeInsuranceRate005.getStartMonthRage()
				+ " ~ " + historyUnemployeeInsuranceRate005.getEndMonthRage());
		lstHistoryUnemployeeInsuranceRate.add(historyUnemployeeInsuranceRate005);
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRate004 = new HistoryUnemployeeInsuranceRateDto();
		historyUnemployeeInsuranceRate004.setCompanyCode("companyCode001");
		MonthRange monthRange004 = new MonthRange();
		monthRange004.setStartMonth(new YearMonth(2015 * 100 + 4));
		monthRange004.setEndMonth(new YearMonth(2015 * 100 + 9));
		// historyUnemployeeInsuranceRate006.setMonthRage(monthRange006);
		historyUnemployeeInsuranceRate004.setHistoryId("historyId004");
		historyUnemployeeInsuranceRate004.setStartMonthRage(convertMonth(monthRange004.getStartMonth()));
		historyUnemployeeInsuranceRate004.setEndMonthRage(convertMonth(monthRange004.getEndMonth()));
		historyUnemployeeInsuranceRate004.setInforMonthRage(historyUnemployeeInsuranceRate004.getStartMonthRage()
				+ " ~ " + historyUnemployeeInsuranceRate004.getEndMonthRage());
		lstHistoryUnemployeeInsuranceRate.add(historyUnemployeeInsuranceRate004);
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRate003 = new HistoryUnemployeeInsuranceRateDto();
		historyUnemployeeInsuranceRate003.setCompanyCode("companyCode001");
		MonthRange monthRange003 = new MonthRange();
		monthRange003.setStartMonth(new YearMonth(2014 * 100 + 9));
		monthRange003.setEndMonth(new YearMonth(2015 * 100 + 3));
		// historyUnemployeeInsuranceRate006.setMonthRage(monthRange006);
		historyUnemployeeInsuranceRate003.setHistoryId("historyId003");
		historyUnemployeeInsuranceRate003.setStartMonthRage(convertMonth(monthRange003.getStartMonth()));
		historyUnemployeeInsuranceRate003.setEndMonthRage(convertMonth(monthRange003.getEndMonth()));
		historyUnemployeeInsuranceRate003.setInforMonthRage(historyUnemployeeInsuranceRate003.getStartMonthRage()
				+ " ~ " + historyUnemployeeInsuranceRate003.getEndMonthRage());
		lstHistoryUnemployeeInsuranceRate.add(historyUnemployeeInsuranceRate003);
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRate002 = new HistoryUnemployeeInsuranceRateDto();
		historyUnemployeeInsuranceRate002.setCompanyCode("companyCode001");
		MonthRange monthRange002 = new MonthRange();
		monthRange002.setStartMonth(new YearMonth(2014 * 100 + 4));
		monthRange002.setEndMonth(new YearMonth(2014 * 100 + 8));
		// historyUnemployeeInsuranceRate006.setMonthRage(monthRange006);
		historyUnemployeeInsuranceRate002.setHistoryId("historyId002");
		historyUnemployeeInsuranceRate002.setStartMonthRage(convertMonth(monthRange002.getStartMonth()));
		historyUnemployeeInsuranceRate002.setEndMonthRage(convertMonth(monthRange002.getEndMonth()));
		historyUnemployeeInsuranceRate002.setInforMonthRage(historyUnemployeeInsuranceRate002.getStartMonthRage()
				+ " ~ " + historyUnemployeeInsuranceRate002.getEndMonthRage());
		lstHistoryUnemployeeInsuranceRate.add(historyUnemployeeInsuranceRate002);
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRate001 = new HistoryUnemployeeInsuranceRateDto();
		historyUnemployeeInsuranceRate001.setCompanyCode("companyCode001");
		MonthRange monthRange001 = new MonthRange();
		monthRange001.setStartMonth(new YearMonth(2013 * 100 + 4));
		monthRange001.setEndMonth(new YearMonth(2014 * 100 + 3));
		// historyUnemployeeInsuranceRate006.setMonthRage(monthRange006);
		historyUnemployeeInsuranceRate001.setHistoryId("historyId002");
		historyUnemployeeInsuranceRate001.setStartMonthRage(convertMonth(monthRange001.getStartMonth()));
		historyUnemployeeInsuranceRate001.setEndMonthRage(convertMonth(monthRange001.getEndMonth()));
		historyUnemployeeInsuranceRate001.setInforMonthRage(historyUnemployeeInsuranceRate001.getStartMonthRage()
				+ " ~ " + historyUnemployeeInsuranceRate001.getEndMonthRage());
		lstHistoryUnemployeeInsuranceRate.add(historyUnemployeeInsuranceRate001);
		return lstHistoryUnemployeeInsuranceRate;
	}

	@POST
	@Path("findHistory/{historyId}")
	public HistoryUnemployeeInsuranceRateDto findHistory(@PathParam("historyId") String historyId) {
		HistoryUnemployeeInsuranceRateDto historyUnemployeeInsuranceRateDto = new HistoryUnemployeeInsuranceRateDto();
		List<HistoryUnemployeeInsuranceRateDto> lstHistoryUnemployeeInsuranceRate = findAllHistory();
		for (HistoryUnemployeeInsuranceRateDto history : lstHistoryUnemployeeInsuranceRate) {
			if (history.getHistoryId().equals(historyId)) {
				historyUnemployeeInsuranceRateDto = history;
			}
		}
		return historyUnemployeeInsuranceRateDto;
	}

	@POST
	@Path("detailHistory/{historyId}")
	public UnemployeeInsuranceRateDto detailHistory(@PathParam("historyId") String historyId) {
		UnemployeeInsuranceRateDto unemployeeInsuranceRate = new UnemployeeInsuranceRateDto();
		unemployeeInsuranceRate.setCompanyCode("companyCode001");
		unemployeeInsuranceRate.setHistoryId(historyId);
		List<UnemployeeInsuranceRateItem> rateItems = new ArrayList<UnemployeeInsuranceRateItem>();
		UnemployeeInsuranceRateItem umInsuranceRateItemAgroforestry = new UnemployeeInsuranceRateItem();
		umInsuranceRateItemAgroforestry.setCareerGroup(CareerGroup.Agroforestry);
		UnemployeeInsuranceRateItemSetting personalSettingAgroforestry = new UnemployeeInsuranceRateItemSetting();
		personalSettingAgroforestry.setRate(55.5);
		personalSettingAgroforestry.setRoundAtr(RoundingMethod.RoundUp);
		umInsuranceRateItemAgroforestry.setPersonalSetting(personalSettingAgroforestry);
		UnemployeeInsuranceRateItemSetting companySettingAgroforestry = new UnemployeeInsuranceRateItemSetting();
		companySettingAgroforestry.setRate(55.59);
		companySettingAgroforestry.setRoundAtr(RoundingMethod.RoundUp);
		umInsuranceRateItemAgroforestry.setCompanySetting(companySettingAgroforestry);
		rateItems.add(umInsuranceRateItemAgroforestry);
		UnemployeeInsuranceRateItem umInsuranceRateItemContruction = new UnemployeeInsuranceRateItem();
		umInsuranceRateItemContruction.setCareerGroup(CareerGroup.Contruction);
		UnemployeeInsuranceRateItemSetting personalSettingContruction = new UnemployeeInsuranceRateItemSetting();
		personalSettingContruction.setRate(55.5);
		personalSettingContruction.setRoundAtr(RoundingMethod.RoundUp);
		umInsuranceRateItemContruction.setPersonalSetting(personalSettingContruction);
		UnemployeeInsuranceRateItemSetting companySettingContruction = new UnemployeeInsuranceRateItemSetting();
		companySettingContruction.setRate(55.59);
		companySettingContruction.setRoundAtr(RoundingMethod.RoundUp);
		umInsuranceRateItemContruction.setCompanySetting(companySettingContruction);
		rateItems.add(umInsuranceRateItemContruction);
		UnemployeeInsuranceRateItem umInsuranceRateItemOther= new UnemployeeInsuranceRateItem();
		umInsuranceRateItemOther.setCareerGroup(CareerGroup.Other);
		UnemployeeInsuranceRateItemSetting personalSettingOther = new UnemployeeInsuranceRateItemSetting();
		personalSettingOther.setRate(55.5);
		personalSettingOther.setRoundAtr(RoundingMethod.RoundUp);
		umInsuranceRateItemOther.setPersonalSetting(personalSettingOther);
		UnemployeeInsuranceRateItemSetting companySettingOther = new UnemployeeInsuranceRateItemSetting();
		companySettingOther.setRate(56.0);
		companySettingOther.setRoundAtr(RoundingMethod.RoundUp);
		umInsuranceRateItemOther.setCompanySetting(companySettingOther);
		rateItems.add(umInsuranceRateItemOther);
		unemployeeInsuranceRate.setRateItems(rateItems);
		return unemployeeInsuranceRate;
	}

	public String convertMonth(YearMonth yearMonth) {
		String convert = "";
		String mounth = "";
		if (yearMonth.month() < 10) {
			mounth = "0" + yearMonth.month();
		} else
			mounth = String.valueOf(yearMonth.month());
		convert = yearMonth.year() + "/" + mounth;
		return convert;
	}
}
