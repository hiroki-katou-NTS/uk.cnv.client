package nts.uk.ctx.at.record.ac.specificdatesetting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.specificdatesetting.RecSpecificDateItemImport;
import nts.uk.ctx.at.record.dom.adapter.specificdatesetting.RecSpecificDateSettingAdapter;
import nts.uk.ctx.at.schedule.pub.shift.businesscalendar.specificdate.SpecificDateItemExport;
import nts.uk.ctx.at.schedule.pub.shift.businesscalendar.specificdate.WpSpecificDateSettingExport;
import nts.uk.ctx.at.schedule.pub.shift.businesscalendar.specificdate.WpSpecificDateSettingPub;
import nts.uk.ctx.at.shared.dom.adapter.specificdatesetting.RecSpecificDateSettingImport;

@Stateless
public class RecSpecificDateSettingAdapterImpl implements RecSpecificDateSettingAdapter{
	
	@Inject
	private WpSpecificDateSettingPub wpSpecificDateSettingPub;
	

	@Override
	public RecSpecificDateSettingImport specificDateSettingService(String companyID, String workPlaceID,
			GeneralDate date) {
		WpSpecificDateSettingExport wpSpecificDateSettingExport = wpSpecificDateSettingPub.workplaceSpecificDateSettingService(companyID, workPlaceID, date);
		return new RecSpecificDateSettingImport(wpSpecificDateSettingExport.getDate(), wpSpecificDateSettingExport.getNumberList());
	}

	@Override
	public RecSpecificDateSettingImport specificDateSettingServiceByListWpl(String companyID, List<String> workPlaceID,
			GeneralDate date) {
		for (String wPId : workPlaceID) {
			return this.specificDateSettingService(companyID, wPId, date);
		}
		return null;
	}

	@Override
	public List<RecSpecificDateSettingImport> getList(String companyID, List<String> workPlaceID,
			DatePeriod datePeriod) {
		List<RecSpecificDateSettingImport> data = new ArrayList<>();
		for(GeneralDate date : datePeriod.datesBetween() ) {
			RecSpecificDateSettingImport recSpecificDateSettingImport = specificDateSettingServiceByListWpl(companyID, workPlaceID, date);
			if(recSpecificDateSettingImport!=null) {
				data.add(recSpecificDateSettingImport);
			}
		}
		return data;
	}

	@Override
	public List<RecSpecificDateItemImport> getSpecifiDateItem(String companyId, List<Integer> specifiDateNos) {
		List<SpecificDateItemExport> wpSpecificDateSettingExport = wpSpecificDateSettingPub.getSpecifiDateByListCode(companyId, specifiDateNos);
		return wpSpecificDateSettingExport.stream().map(x -> new RecSpecificDateItemImport(companyId, x.getUseAtr(), x.getSpecificDateItemNo(), x.getSpecificName()))
				.collect(Collectors.toList());
	}

	@Override
	public RecSpecificDateSettingImport findSpecDateSetByWkpLst(String companyID, List<String> workPlaceIds,
			GeneralDate date) {
		WpSpecificDateSettingExport wpSpecificDateSettingExport = wpSpecificDateSettingPub.findSpecDateSetByWkpLst(companyID, workPlaceIds, date);
		return new RecSpecificDateSettingImport(wpSpecificDateSettingExport.getDate(), wpSpecificDateSettingExport.getNumberList());
	}

}
