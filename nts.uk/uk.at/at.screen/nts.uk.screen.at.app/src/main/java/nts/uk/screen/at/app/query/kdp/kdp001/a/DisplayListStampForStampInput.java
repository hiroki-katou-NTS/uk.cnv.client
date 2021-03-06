package nts.uk.screen.at.app.query.kdp.kdp001.a;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.EmployeeStampDatasFinder;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto.StampDataOfEmployeesDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 * 
 *         UKDesign.UniversalK.就業.KDP_打刻.KDP001_打刻入力(ポータル).A:打刻入力(ポータル).メニュー別OCD.打刻入力(ポータル)の打刻履歴一覧を表示する
 * 
 */
@Stateless
public class DisplayListStampForStampInput {

	@Inject
	private EmployeeStampDatasFinder finder;

	public List<StampDataOfEmployeesDto> getEmployeeStampData() {
		DatePeriod period = new DatePeriod(GeneralDate.today().addDays(-3), GeneralDate.today());
		String employeeId = AppContexts.user().employeeId();
		
		return finder.getEmployeeStampData(period, employeeId).stream().map(x ->  new StampDataOfEmployeesDto(x))
				.collect(Collectors.toList());

	}
}
