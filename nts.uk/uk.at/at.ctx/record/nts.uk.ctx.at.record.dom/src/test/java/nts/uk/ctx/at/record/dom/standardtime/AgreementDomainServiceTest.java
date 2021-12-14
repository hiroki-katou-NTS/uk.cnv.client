package nts.uk.ctx.at.record.dom.standardtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import lombok.val;
import mockit.Injectable;
import mockit.StrictExpectations;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfCompany;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.exceptsetting.AgreementMonthSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.AgreementOneMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;

public class AgreementDomainServiceTest {

	@Injectable
	private AgreementDomainService.RequireM6 require;
	
	private String sid = "sid";
	private GeneralDate ymd = GeneralDate.today();
	private YearMonth ym = ymd.yearMonth();
	private String cid = "cid";
	
	WorkingConditionItem workCondition = new WorkingConditionItem(
			null, null, null, null, null, null, null, null, 
			WorkingSystem.REGULAR_WORK, null, null, null, null, null);
	
	BasicAgreementSetting setting = new BasicAgreementSetting(new AgreementOneMonth(
			OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(50 * 60), 
					new AgreementOneMonthTime(40* 60)), new AgreementOneMonthTime(60* 60)), 
			OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(80* 60), 
					new AgreementOneMonthTime(75* 60)), new AgreementOneMonthTime(100* 60))), 
		null, null, null);
	
	AgreementTimeOfCompany agreement = new AgreementTimeOfCompany(cid, LaborSystemtAtr.GENERAL_LABOR_SYSTEM, setting);
	
	AgreementMonthSetting empSet = new AgreementMonthSetting(sid, ym, 
			OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(90 * 60), 
										new AgreementOneMonthTime(85* 60)));
	
	@Test
	/**　社員の労働条件が存在しない　*/
	public void test1() {
		
		new StrictExpectations() {{
			require.workingConditionItem(sid, ymd);
			result = Optional.empty();
			
			require.agreementMonthSetting(sid, ym);
			result = Optional.empty();
		}};
		
		val result = AgreementDomainService.getBasicSet(require, cid, sid, ymd, ym);

		assertThat(result.getBasicSetting().getOneMonth().getBasic().getErAlTime().getAlarm().valueAsMinutes()).isEqualTo(0);
		assertThat(result.getBasicSetting().getOneMonth().getBasic().getErAlTime().getError().valueAsMinutes()).isEqualTo(0);
		assertThat(result.getBasicSetting().getOneMonth().getBasic().getUpperLimit().valueAsMinutes()).isEqualTo(0);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getAlarm().valueAsMinutes()).isEqualTo(0);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getError().valueAsMinutes()).isEqualTo(0);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getUpperLimit().valueAsMinutes()).isEqualTo(0);
	}
	
	@Test
	/**　社員の36協定時間設定が存在しない　*/
	public void test2() {
		
		new StrictExpectations() {{
			require.workingConditionItem(sid, ymd);
			result = Optional.of(workCondition);
			
			require.agreementUnitSetting(cid);
			result = Optional.empty();
			
			require.agreementTimeOfCompany(cid, LaborSystemtAtr.GENERAL_LABOR_SYSTEM);
			result = Optional.of(agreement);
			
			require.agreementMonthSetting(sid, ym);
			result = Optional.empty();
		}};
		
		val result = AgreementDomainService.getBasicSet(require, cid, sid, ymd, ym);

		assertThat(result.getBasicSetting().getOneMonth().getBasic().getErAlTime().getAlarm().valueAsMinutes()).isEqualTo(40*60);
		assertThat(result.getBasicSetting().getOneMonth().getBasic().getErAlTime().getError().valueAsMinutes()).isEqualTo(50*60);
		assertThat(result.getBasicSetting().getOneMonth().getBasic().getUpperLimit().valueAsMinutes()).isEqualTo(60*60);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getAlarm().valueAsMinutes()).isEqualTo(75*60);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getError().valueAsMinutes()).isEqualTo(80*60);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getUpperLimit().valueAsMinutes()).isEqualTo(100*60);
	}
	
	@Test
	/**　社員の36協定時間設定が存在する　*/
	public void test3() {
		
		new StrictExpectations() {{
			require.workingConditionItem(sid, ymd);
			result = Optional.of(workCondition);
			
			require.agreementUnitSetting(cid);
			result = Optional.empty();
			
			require.agreementTimeOfCompany(cid, LaborSystemtAtr.GENERAL_LABOR_SYSTEM);
			result = Optional.of(agreement);
			
			require.agreementMonthSetting(sid, ym);
			result = Optional.of(empSet);
		}};
		
		val result = AgreementDomainService.getBasicSet(require, cid, sid, ymd, ym);

		assertThat(result.getBasicSetting().getOneMonth().getBasic().getErAlTime().getAlarm().valueAsMinutes()).isEqualTo(40*60);
		assertThat(result.getBasicSetting().getOneMonth().getBasic().getErAlTime().getError().valueAsMinutes()).isEqualTo(50*60);
		assertThat(result.getBasicSetting().getOneMonth().getBasic().getUpperLimit().valueAsMinutes()).isEqualTo(60*60);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getAlarm().valueAsMinutes()).isEqualTo(85*60);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getErAlTime().getError().valueAsMinutes()).isEqualTo(90*60);
		assertThat(result.getBasicSetting().getOneMonth().getSpecConditionLimit().getUpperLimit().valueAsMinutes()).isEqualTo(100*60);
	}
}
