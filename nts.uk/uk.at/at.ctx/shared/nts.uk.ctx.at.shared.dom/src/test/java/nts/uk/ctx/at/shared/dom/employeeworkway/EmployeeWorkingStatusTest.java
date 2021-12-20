package nts.uk.ctx.at.shared.dom.employeeworkway;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.TempAbsenceFrameNo;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus.Require;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workingcondition.ManageAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.SecondSituation;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

@RunWith(JMockit.class)
public class EmployeeWorkingStatusTest {

	@Injectable
	private Require require;

	@Test
	public void getters() {
		val data = EmployeeWorkingStatus.create(require, "empId", GeneralDate.today());
		NtsAssert.invokeGetters(data);
	}

	/**
	 * Pattern	:
	 * 		在籍中か	-> no
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.在籍していない
	 * 		休業枠NO	-> Optional.empty
	 * 		雇用コード	-> Optional.empty
	 * 		所属組織	-> Optional.empty
	 */
	@Test
	public void testCreate_NotEnrolled() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();

		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
		}};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );
		assertThat( result.getOptTempAbsenceFrameNo() ).isEmpty();
		assertThat( result.getOptEmploymentCd() ).isEmpty();
		assertThat( result.getOrganization() ).isEmpty();

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.NOT_ENROLLED );

	}

	/**
	 * Pattern	:
	 * 		在籍中か		-> yes
	 * 		雇用コード		-> empty
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.データ不正
	 * 		休業枠NO	-> Optional.empty
	 * 		雇用コード	-> Optional.empty
	 * 		所属組織	-> Optional.empty
	 */
	@Test
	public void testCreate_EmpCdIsEmpty() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();
		val period = new DatePeriod( date, date );

		// 社員の在籍期間
		val enrolledPeriod = new EmpEnrollPeriodImport(empId, period, SecondSituation.NONE);

		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
			result = Optional.of(enrolledPeriod);

			// require.雇用履歴を取得する( 社員ID, 年月日 )
			require.getEmploymentHistory((String)any, (GeneralDate)any);
		}};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );
		assertThat( result.getOptTempAbsenceFrameNo() ).isEmpty();
		assertThat( result.getOrganization() ).isEmpty();

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.INVALID_DATA );
		assertThat( result.getOptEmploymentCd() ).isEmpty();

	}

	/**
	 * Pattern	:
	 * 		在籍中か		-> yes
	 * 		雇用コード		-> HireTypeCd#01
	 * 		所属組織		-> Unit:職場 / Id:WorkPlaceId#01
	 * 		予定管理区分	-> empty
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.データ不正
	 * 		休業枠NO	-> Optional.empty
	 * 		雇用コード	-> HireTypeCd#01
	 * 		所属組織	-> Unit:職場 / Id:WorkPlaceId#01
	 */
	@Test
	public void testCreate_ScheMnagingAtrIsEmpty() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();
		val period = new DatePeriod( date, date );

		// 社員の在籍期間
		val enrolledPeriod = new EmpEnrollPeriodImport(empId, period, SecondSituation.NONE);
		// 社員の雇用期間
		val hirePeriod = new EmploymentPeriodImported(empId, period, "HireTypeCd#01", Optional.empty());
		// 対象組織識別情報
		val affiliatedOrg = TargetOrgIdenInfor.creatIdentifiWorkplace("WorkPlaceId#01");

		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
			result = Optional.of(enrolledPeriod);

			// require.雇用履歴を取得する( 社員ID, 年月日 )
			require.getEmploymentHistory((String)any, (GeneralDate)any);
			result = Optional.of(hirePeriod);

			// require.労働条件履歴を取得する( 社員ID, 年月日 )
			require.getBySidAndStandardDate((String)any, (GeneralDate)any);
		}};

		new MockUp<GetTargetIdentifiInforService>() {
			// 社員の対象組織識別情報を取得する
			@Mock
			public TargetOrgIdenInfor get(
						@SuppressWarnings("unused") GetTargetIdentifiInforService.Require require
					,	@SuppressWarnings("unused") GeneralDate date
					,	@SuppressWarnings("unused") String empId
			) {
				return affiliatedOrg;
			}
		};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );
		assertThat( result.getOptTempAbsenceFrameNo() ).isEmpty();

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.INVALID_DATA );
		assertThat( result.getOptEmploymentCd() )
			.isNotEmpty()
			.get().isEqualTo( new EmploymentCode(hirePeriod.getEmploymentCd()) );
		assertThat( result.getOrganization() )
			.isNotEmpty()
			.get().isEqualTo( TargetOrgIdenInfor.createFromTargetUnit(affiliatedOrg.getUnit(), affiliatedOrg.getTargetId()) );

	}

	/**
	 * Pattern	:
	 * 		在籍中か		-> yes
	 * 		雇用コード		-> HireTypeCd#02
	 * 		所属組織		-> Unit:組織 / Id:WorkplaceGroupId#11
	 * 		予定管理区分	-> 管理しない
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.予定管理しない
	 * 		休業枠NO	-> Optional.empty
	 * 		雇用コード	-> HireTypeCd#02
	 * 		所属組織	-> Unit:組織 / Id:WorkplaceGroupId#11
	 */
	@Test
	public void testCreate_ScheduleManagingAtrIsNotManaging() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();
		val period = new DatePeriod( date, date );

		// 社員の在籍期間
		val enrolledPeriod = new EmpEnrollPeriodImport(empId, period, SecondSituation.NONE);
		// 社員の雇用期間
		val hirePeriod = new EmploymentPeriodImported(empId, period, "HireTypeCd#02", Optional.empty());
		// 対象組織識別情報
		val affiliatedOrg = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup("WorkplaceGroupId#11");
		// 労働条件項目
		val wrkCndItem = new WorkingConditionItem("wrkCndhistId", ManageAtr.NOTUSE, empId);

		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
			result = Optional.of(enrolledPeriod);

			// require.雇用履歴を取得する( 社員ID, 年月日 )
			require.getEmploymentHistory((String)any, (GeneralDate)any);
			result = Optional.of(hirePeriod);

			// require.労働条件履歴を取得する( 社員ID, 年月日 )
			require.getBySidAndStandardDate((String)any, (GeneralDate)any);
			result = Optional.of(wrkCndItem);
		}};

		new MockUp<GetTargetIdentifiInforService>() {
			// 社員の対象組織識別情報を取得する
			@Mock
			public TargetOrgIdenInfor get(
						@SuppressWarnings("unused") GetTargetIdentifiInforService.Require require
					,	@SuppressWarnings("unused") GeneralDate date
					,	@SuppressWarnings("unused") String empId
			) {
				return affiliatedOrg;
			}
		};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );
		assertThat( result.getOptTempAbsenceFrameNo() ).isEmpty();

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.DO_NOT_MANAGE_SCHEDULE );
		assertThat( result.getOptEmploymentCd() )
			.isNotEmpty()
			.get().isEqualTo( new EmploymentCode(hirePeriod.getEmploymentCd()) );
		assertThat( result.getOrganization() )
			.isNotEmpty()
			.get().isEqualTo( TargetOrgIdenInfor.createFromTargetUnit(affiliatedOrg.getUnit(), affiliatedOrg.getTargetId()) );

	}

	/**
	 * Pattern	:
	 * 		在籍中か		-> yes
	 * 		雇用コード		-> HireTypeCd#03
	 * 		所属組織		-> Unit:職場 / Id:WorkPlaceId#02
	 * 		予定管理区分	-> 管理する
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.休職中
	 * 		休業枠NO	-> Optional.empty
	 * 		雇用コード	-> HireTypeCd#03
	 * 		所属組織	-> Unit:職場 / Id:WorkPlaceId#02
	 */
	@Test
	public void testCreate_SuspensionFromWork() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();
		val period = new DatePeriod( date, date );

		// 社員の在籍期間
		val enrolledPeriod = new EmpEnrollPeriodImport(empId, period, SecondSituation.NONE);
		// 社員の雇用期間
		val hirePeriod = new EmploymentPeriodImported(empId, period, "HireTypeCd#03", Optional.empty());
		// 対象組織識別情報
		val affiliatedOrg = TargetOrgIdenInfor.creatIdentifiWorkplace("WorkPlaceId#02");
		// 労働条件項目
		val wrkCndItem = new WorkingConditionItem("wrkCndhistId", ManageAtr.USE, empId);
		// 社員の休職期間
		val suspensionPeriod = new EmployeeLeaveJobPeriodImport(empId, period);

		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
			result = Optional.of(enrolledPeriod);

			// require.雇用履歴を取得する( 社員ID, 年月日 )
			require.getEmploymentHistory((String)any, (GeneralDate)any);
			result = Optional.of(hirePeriod);

			// require.労働条件履歴を取得する( 社員ID, 年月日 )
			require.getBySidAndStandardDate((String)any, (GeneralDate)any);
			result = Optional.of(wrkCndItem);

			// require.休職期間を取得する( 社員ID, 年月日 )
			require.getByDatePeriod((String)any, (GeneralDate)any);
			result = Optional.of(suspensionPeriod);
		}};

		new MockUp<GetTargetIdentifiInforService>() {
			// 社員の対象組織識別情報を取得する
			@Mock
			public TargetOrgIdenInfor get(
						@SuppressWarnings("unused") GetTargetIdentifiInforService.Require require
					,	@SuppressWarnings("unused") GeneralDate date
					,	@SuppressWarnings("unused") String empId
			) {
				return affiliatedOrg;
			}
		};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );
		assertThat( result.getOptTempAbsenceFrameNo() ).isEmpty();

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.ON_LEAVE );
		assertThat( result.getOptEmploymentCd() )
			.isNotEmpty()
			.get().isEqualTo( new EmploymentCode(hirePeriod.getEmploymentCd()) );
		assertThat( result.getOrganization() )
			.isNotEmpty()
			.get().isEqualTo( TargetOrgIdenInfor.createFromTargetUnit(affiliatedOrg.getUnit(), affiliatedOrg.getTargetId()) );

	}

	/**
	 * Pattern	:
	 * 		在籍中か		-> yes
	 * 		雇用コード		-> HireTypeCd#04
	 * 		所属組織		-> Unit:組織 / Id:WorkplaceGroupId#12
	 * 		予定管理区分	-> 管理する
	 * 		休職中か		-> no
	 * 		休業枠NO		-> 3
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.休業中
	 * 		休業枠NO	-> 3
	 * 		雇用コード	-> HireTypeCd#04
	 * 		所属組織	-> Unit:組織 / Id:WorkplaceGroupId#12
	 */
	@Test
	public void testCreate_TakeDayOff() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();
		val period = new DatePeriod( date, date );

		// 社員の在籍期間
		val enrolledPeriod = new EmpEnrollPeriodImport(empId, period, SecondSituation.NONE);
		// 社員の雇用期間
		val hirePeriod = new EmploymentPeriodImported(empId, period, "HireTypeCd#04", Optional.empty());
		// 労働条件項目
		val wrkCndItem = new WorkingConditionItem("wrkCndhistId", ManageAtr.USE, empId);
		// 対象組織識別情報
		val affiliatedOrg = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup("WorkplaceGroupId#12");
		// 社員の休業期間
		val takeDayOffPeriod = new EmpLeaveWorkPeriodImport(empId, new TempAbsenceFrameNo(BigDecimal.valueOf(3)), period);


		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
			result = Optional.of(enrolledPeriod);

			// require.雇用履歴を取得する( 社員ID, 年月日 )
			require.getEmploymentHistory((String)any, (GeneralDate)any);
			result = Optional.of(hirePeriod);

			// require.労働条件履歴を取得する( 社員ID, 年月日 )
			require.getBySidAndStandardDate((String)any, (GeneralDate)any);
			result = Optional.of(wrkCndItem);

			// require.休職期間を取得する( 社員ID, 年月日 )
			require.getByDatePeriod((String)any, (GeneralDate)any);

			// 休業期間を取得する( 社員ID, 年月日 )
			require.specAndGetHolidayPeriod((String)any, (GeneralDate)any);
			result = Optional.of(takeDayOffPeriod);
		}};

		new MockUp<GetTargetIdentifiInforService>() {
			// 社員の対象組織識別情報を取得する
			@Mock
			public TargetOrgIdenInfor get(
						@SuppressWarnings("unused") GetTargetIdentifiInforService.Require require
					,	@SuppressWarnings("unused") GeneralDate date
					,	@SuppressWarnings("unused") String empId
			) {
				return affiliatedOrg;
			}
		};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.CLOSED );
		assertThat( result.getOptEmploymentCd() )
			.isNotEmpty()
			.get().isEqualTo( new EmploymentCode(hirePeriod.getEmploymentCd()) );
		assertThat( result.getOrganization() )
			.isNotEmpty()
			.get().isEqualTo( TargetOrgIdenInfor.createFromTargetUnit(affiliatedOrg.getUnit(), affiliatedOrg.getTargetId()) );
		assertThat( result.getOptTempAbsenceFrameNo() )
			.isNotEmpty()
			.get().isEqualTo( takeDayOffPeriod.getTempAbsenceFrNo() );

	}

	/**
	 * Pattern	:
	 * 		在籍中か		-> yes
	 * 		雇用コード		-> HireTypeCd#05
	 * 		所属組織		-> Unit:職場 / Id:WorkPlaceId#03
	 * 		予定管理区分	-> 管理する
	 * 		休職中か		-> no
	 * 		休業枠NO		-> empty
	 * Output	:
	 * 		社員の予定管理状態
	 * 		社員ID		-> Input.社員ID
	 * 		年月日		-> Input.年月日
	 * 		状態		-> 就業状態.予定管理する
	 * 		休業枠NO	-> Optional.empty
	 * 		雇用コード	-> HireTypeCd#05
	 * 		所属組織	-> Unit:職場 / Id:WorkPlaceId#03
	 */
	@Test
	public void testCreate_AllClear() {

		// Data
		val empId = "empId";
		val date = GeneralDate.today();
		val period = new DatePeriod( date, date );

		// 社員の在籍期間
		val enrolledPeriod = new EmpEnrollPeriodImport(empId, period, SecondSituation.NONE);
		// 社員の雇用期間
		val hirePeriod = new EmploymentPeriodImported(empId, period, "HireTypeCd#05", Optional.empty());
		// 対象組織識別情報
		val affiliatedOrg = TargetOrgIdenInfor.creatIdentifiWorkplace("WorkPlaceId#03");
		// 労働条件項目
		val wrkCndItem = new WorkingConditionItem("wrkCndhistId", ManageAtr.USE, empId);


		// Mocking
		new Expectations() {{
			// require.在籍期間を取得する( 社員ID, 年月日 )
			require.getAffCompanyHistByEmployee((String)any, (GeneralDate)any);
			result = Optional.of(enrolledPeriod);

			// require.雇用履歴を取得する( 社員ID, 年月日 )
			require.getEmploymentHistory((String)any, (GeneralDate)any);
			result = Optional.of(hirePeriod);

			// require.労働条件履歴を取得する( 社員ID, 年月日 )
			require.getBySidAndStandardDate((String)any, (GeneralDate)any);
			result = Optional.of(wrkCndItem);

			// require.休職期間を取得する( 社員ID, 年月日 )
			require.getByDatePeriod((String)any, (GeneralDate)any);

			// 休業期間を取得する( 社員ID, 年月日 )
			require.specAndGetHolidayPeriod((String)any, (GeneralDate)any);
		}};

		new MockUp<GetTargetIdentifiInforService>() {
			// 社員の対象組織識別情報を取得する
			@Mock
			public TargetOrgIdenInfor get(
						@SuppressWarnings("unused") GetTargetIdentifiInforService.Require require
					,	@SuppressWarnings("unused") GeneralDate date
					,	@SuppressWarnings("unused") String empId
			) {
				return affiliatedOrg;
			}
		};


		// Execute
		val result = EmployeeWorkingStatus.create(require, empId, date);


		// Assertions
		assertThat( result.getEmployeeID() ).isEqualTo( empId );
		assertThat( result.getDate() ).isEqualTo( date );

		assertThat( result.getWorkingStatus() ).isEqualTo( WorkingStatus.SCHEDULE_MANAGEMENT );
		assertThat( result.getOptEmploymentCd() )
			.isNotEmpty()
			.get().isEqualTo( new EmploymentCode(hirePeriod.getEmploymentCd()) );
		assertThat( result.getOrganization() )
			.isNotEmpty()
			.get().isEqualTo( TargetOrgIdenInfor.createFromTargetUnit(affiliatedOrg.getUnit(), affiliatedOrg.getTargetId()) );
		assertThat( result.getOptTempAbsenceFrameNo() ).isEmpty();

	}
}
