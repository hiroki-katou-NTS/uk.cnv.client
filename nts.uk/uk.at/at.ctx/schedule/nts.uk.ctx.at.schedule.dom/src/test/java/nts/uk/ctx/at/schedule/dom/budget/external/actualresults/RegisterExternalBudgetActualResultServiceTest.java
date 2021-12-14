package nts.uk.ctx.at.schedule.dom.budget.external.actualresults;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudgetCd;
import nts.uk.ctx.at.schedule.dom.budget.external.acceptance.ExtBudgetNumberPerson;
import nts.uk.ctx.at.schedule.dom.budget.external.acceptance.ExtBudgetNumericalVal;
import nts.uk.ctx.at.schedule.dom.budget.external.acceptance.ExtBudgetUnitPrice;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

@RunWith(JMockit.class)
public class RegisterExternalBudgetActualResultServiceTest {

	@Injectable
	private RegisterExternalBudgetActualResultService.Require require;

	/**
	 * if 値.isEmpty
	 */
	@Test
	public void testSignUp_1() {
		TargetOrgIdenInfor targetOrg = Helper.createDummyTargetOrgIdenInfor();
		ExternalBudgetCd itemCode = new ExternalBudgetCd("itemCode");
		GeneralDate ymd = GeneralDate.today();
		Optional<ExternalBudgetValues> extBudgetActualValue = Optional.empty();

		NtsAssert.atomTask(
				() -> RegisterExternalBudgetActualResultService.signUp(require, targetOrg, itemCode, ymd, extBudgetActualValue),
				any -> require.delete(targetOrg, itemCode, ymd));
	}

	/**
	 * if 値 is not Empty if 値 : 外部予算実績金額(PrimitiveValue)
	 */
	@Test
	public void testSignUp_2() {
		TargetOrgIdenInfor targetOrg = Helper.createDummyTargetOrgIdenInfor();
		ExternalBudgetCd itemCode = new ExternalBudgetCd("itemCode");
		GeneralDate ymd = GeneralDate.today();
		Optional<ExternalBudgetValues> extBudgetActualValue = Optional.of(new ExternalBudgetMoneyValue(999));

		NtsAssert.atomTask(
				() -> RegisterExternalBudgetActualResultService.signUp(require, targetOrg, itemCode, ymd, extBudgetActualValue),
				any -> require.delete(targetOrg, itemCode, ymd), any -> require.insert(any.get()));
	}

	/**
	 * if 値 is not Empty if 値 : 外部予算実績単価(PrimitiveValue)
	 */
	@Test
	public void testSignUp_3() {
		TargetOrgIdenInfor targetOrg = Helper.createDummyTargetOrgIdenInfor();
		ExternalBudgetCd itemCode = new ExternalBudgetCd("itemCode");
		GeneralDate ymd = GeneralDate.today();
		Optional<ExternalBudgetValues> extBudgetActualValue = Optional.of(new ExtBudgetUnitPrice(999));

		NtsAssert.atomTask(
				() -> RegisterExternalBudgetActualResultService.signUp(require, targetOrg, itemCode, ymd, extBudgetActualValue),
				any -> require.delete(targetOrg, itemCode, ymd), any -> require.insert(any.get()));
	}

	/**
	 * if 値 is not Empty if 値 : 外部予算実績数値(PrimitiveValue)
	 */
	@Test
	public void testSignUp_4() {
		TargetOrgIdenInfor targetOrg = Helper.createDummyTargetOrgIdenInfor();
		ExternalBudgetCd itemCode = new ExternalBudgetCd("itemCode");
		GeneralDate ymd = GeneralDate.today();
		Optional<ExternalBudgetValues> extBudgetActualValue = Optional.of(new ExtBudgetNumericalVal(999));

		NtsAssert.atomTask(
				() -> RegisterExternalBudgetActualResultService.signUp(require, targetOrg, itemCode, ymd, extBudgetActualValue),
				any -> require.delete(targetOrg, itemCode, ymd), any -> require.insert(any.get()));
	}

	/**
	 * if 値 is not Empty if 値 : 外部予算実績人数(PrimitiveValue)
	 */
	@Test
	public void testSignUp_5() {
		TargetOrgIdenInfor targetOrg = Helper.createDummyTargetOrgIdenInfor();
		ExternalBudgetCd itemCode = new ExternalBudgetCd("itemCode");
		GeneralDate ymd = GeneralDate.today();
		Optional<ExternalBudgetValues> extBudgetActualValue = Optional.of(new ExtBudgetNumberPerson(999));

		NtsAssert.atomTask(
				() -> RegisterExternalBudgetActualResultService.signUp(require, targetOrg, itemCode, ymd, extBudgetActualValue),
				any -> require.delete(targetOrg, itemCode, ymd), any -> require.insert(any.get()));
	}

	/**
	 * if 値 is not Empty if 値 : 外部予算実績時間(PrimitiveValue)
	 */
	@Test
	public void testSignUp_6() {
		TargetOrgIdenInfor targetOrg = Helper.createDummyTargetOrgIdenInfor();
		ExternalBudgetCd itemCode = new ExternalBudgetCd("itemCode");
		GeneralDate ymd = GeneralDate.today();
		Optional<ExternalBudgetValues> extBudgetActualValue = Optional.of(new ExternalBudgetTimeValue(999));

		NtsAssert.atomTask(
				() -> RegisterExternalBudgetActualResultService.signUp(require, targetOrg, itemCode, ymd, extBudgetActualValue),
				any -> require.delete(targetOrg, itemCode, ymd), any -> require.insert(any.get()));
	}



	private static class Helper {
		public static TargetOrgIdenInfor createDummyTargetOrgIdenInfor() {
			return TargetOrgIdenInfor.creatIdentifiWorkplace("workplaceId");
		}
	}

}
