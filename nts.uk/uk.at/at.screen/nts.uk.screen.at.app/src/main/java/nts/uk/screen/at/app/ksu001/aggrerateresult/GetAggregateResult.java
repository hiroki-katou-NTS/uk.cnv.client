package nts.uk.screen.at.app.ksu001.aggrerateresult;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.aggreratedinformation.AggregatedInformationDto;
import nts.uk.screen.at.app.ksu001.aggreratedinformation.ScreenQueryAggregatedInformation;

/**
 *  集計結果を再表示する
 *  UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */
@Stateless
public class GetAggregateResult {

	@Inject
	private ScreenQueryAggregatedInformation screenQueryAggreratedInformation;
	
	public AggregatedInformationDto get(AggregateResultParam param) {
		TargetOrgIdenInfor targetOrgIdenInfor;
		if (param.unit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE,
					Optional.of(param.workplaceId),
					Optional.empty());
		} else {
			targetOrgIdenInfor = new TargetOrgIdenInfor(
					TargetOrganizationUnit.WORKPLACE_GROUP,
					Optional.empty(),
					Optional.of(param.workplaceGroupId));
		}
		// 取得する()
		return screenQueryAggreratedInformation.get(
				param.getSids(),
				new DatePeriod(param.getStartDate(), param.getEndDate()),
				DateInMonth.of(param.getDay()),
				param.getActualData,
				targetOrgIdenInfor,
				Optional.ofNullable(param.getPersonalCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, PersonalCounterCategory.class))),
				Optional.ofNullable(param.getWorkplaceCounterOp()).flatMap(x -> Optional.of(EnumAdaptor.valueOf(x, WorkplaceCounterCategory.class))),
				param.isShiftDisplay()
				);
		
	}
}
