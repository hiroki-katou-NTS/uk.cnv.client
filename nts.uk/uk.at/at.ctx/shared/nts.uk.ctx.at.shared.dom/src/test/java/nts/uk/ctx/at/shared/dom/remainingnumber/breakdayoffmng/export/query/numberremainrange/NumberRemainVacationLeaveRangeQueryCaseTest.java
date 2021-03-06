package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TargetSelectionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.DayOffError;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.OccurrenceDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.OccurrenceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnUsedDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UnUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;

@RunWith(JMockit.class)
public class NumberRemainVacationLeaveRangeQueryCaseTest {

//	private static String CID = "000000000000-0117";
//
//	private static String SID = "292ae91c-508c-4c6e-8fe8-3e72277dec16";
//
//	@Injectable
//	private NumberRemainVacationLeaveRangeQuery.Require require;

	// ????????????4???
	// 2019,11, 4 2019, 11, 5 2019, 11, 14 2019, 11, 15

	@Test
	public void testCase1() {
//		List<InterimDayOffMng> dayOffMng = Arrays.asList(
//				DaikyuFurikyuHelper.createDayOff("d1", 0, 1.0),
//				DaikyuFurikyuHelper.createDayOff("d2", 0, 1.0),
//				DaikyuFurikyuHelper.createDayOff("d3", 0, 1.0),
//				DaikyuFurikyuHelper.createDayOff("d4", 0, 1.0));
//
//		List<InterimRemain> interimMng = Arrays.asList(
//				DaikyuFurikyuHelper.createRemain("d1", GeneralDate.ymd(2019, 11, 4),
//						CreateAtr.SCHEDULE, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d2", GeneralDate.ymd(2019, 11, 5),
//						CreateAtr.RECORD, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d3", GeneralDate.ymd(2019, 11, 14),
//						CreateAtr.RECORD, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d4", GeneralDate.ymd(2019, 11, 15),
//						CreateAtr.RECORD, RemainType.BREAK));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2020, 10, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, new ArrayList<>(), dayOffMng,//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//		
//		new Expectations() {
//			{
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(
//						new EmploymentHistShareImport(SID, "02",
//								new DatePeriod(GeneralDate.ymd(2019, 05, 02), GeneralDate.ymd(2019, 11, 02))),
//						new EmploymentHistShareImport(SID, "00",
//								new DatePeriod(GeneralDate.ymd(2019, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(-4.0),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(4.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0), Arrays.asList(DayOffError.DAYERROR),
//				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//		assertThat(resultActual.getLstSeqVacation()).isEqualTo(new ArrayList<>());

	}

	// ??????????????????4???????????????
	// ????????? 11/2/2019 11/3/2019 11/9/2019 11/10/2019
//	@Test
//	public void testCase2() {
//		List<InterimBreakMng> breakMng = Arrays.asList(
//				new InterimBreakMng("d1", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d2", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d3", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d4", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)));
//
//		List<InterimRemain> interimMng = Arrays.asList(
//				DaikyuFurikyuHelper.createRemain("d1", GeneralDate.ymd(2019, 11, 2),
//						CreateAtr.SCHEDULE, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d2", GeneralDate.ymd(2019, 11, 3),
//						CreateAtr.RECORD, RemainType.BREAK ),
//				DaikyuFurikyuHelper.createRemain("d3", GeneralDate.ymd(2019, 11, 9),
//						CreateAtr.RECORD, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d4", GeneralDate.ymd(2019, 11, 10),
//						CreateAtr.RECORD, RemainType.BREAK));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2020, 10, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, breakMng, new ArrayList<>(),//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//
//		new Expectations() {
//			{
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(
//						new EmploymentHistShareImport(SID, "02",
//								new DatePeriod(GeneralDate.ymd(2019, 05, 02), GeneralDate.ymd(2019, 11, 02))),
//						new EmploymentHistShareImport(SID, "00",
//								new DatePeriod(GeneralDate.ymd(2019, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
////				require.getClosureDataByEmployee(SID, (GeneralDate) any);
////				result = NumberRemainVacationLeaveRangeQueryTest.createClosure();
//
////				require.getFirstMonth(CID);
////				result = new CompanyDto(11);
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(4.0),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(4.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0), Arrays.asList(),
//				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//		assertThat(resultActual.getLstSeqVacation()).isEqualTo(new ArrayList<>());
//
//	}
//
//	// 3 ?????????????????????????????????
//	// ????????? 11/2/2019 11/3/2019 11/9/2019 11/10/2019
//	// ?????? 2019,11, 4 2019, 11, 5 2019, 11, 14 2019, 11, 15
//	@Test
//	public void testCase3() {
//
//		List<InterimDayOffMng> dayOffMng = Arrays.asList(
//				DaikyuFurikyuHelper.createDayOff("d5", 0, 1.0),
//				DaikyuFurikyuHelper.createDayOff("d6", 0, 1.0),
//				DaikyuFurikyuHelper.createDayOff("d7", 0, 1.0),
//				DaikyuFurikyuHelper.createDayOff("d8", 0, 1.0));
//
//		List<InterimBreakMng> breakMng = Arrays.asList(
//				new InterimBreakMng("d1", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d2", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d3", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d4", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)));
//
//		List<InterimRemain> interimMng = Arrays.asList(
//				DaikyuFurikyuHelper.createRemain("d1", GeneralDate.ymd(2019, 11, 2),
//						CreateAtr.SCHEDULE, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d2", GeneralDate.ymd(2019, 11, 3),
//						CreateAtr.RECORD, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d3", GeneralDate.ymd(2019, 11, 9),
//						CreateAtr.RECORD, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d4", GeneralDate.ymd(2019, 11, 10),
//						CreateAtr.RECORD, RemainType.BREAK),
//
//				DaikyuFurikyuHelper.createRemain("d5", GeneralDate.ymd(2019, 11, 4),
//						CreateAtr.SCHEDULE, RemainType.SUBHOLIDAY),
//				DaikyuFurikyuHelper.createRemain("d6", GeneralDate.ymd(2019, 11, 5),
//						CreateAtr.RECORD, RemainType.SUBHOLIDAY),
//				DaikyuFurikyuHelper.createRemain("d7", GeneralDate.ymd(2019, 11, 14),
//						CreateAtr.RECORD, RemainType.SUBHOLIDAY),
//				DaikyuFurikyuHelper.createRemain("d8", GeneralDate.ymd(2019, 11, 15),
//						CreateAtr.RECORD, RemainType.SUBHOLIDAY));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2020, 10, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, breakMng, dayOffMng,//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//
//		new Expectations() {
//			{
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(
//						new EmploymentHistShareImport(SID, "02",
//								new DatePeriod(GeneralDate.ymd(2019, 05, 02), GeneralDate.ymd(2019, 11, 02))),
//						new EmploymentHistShareImport(SID, "00",
//								new DatePeriod(GeneralDate.ymd(2019, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
////				require.getClosureDataByEmployee(SID, (GeneralDate) any);
////				result = NumberRemainVacationLeaveRangeQueryTest.createClosure();
//
////				require.getFirstMonth(CID);
////				result = new CompanyDto(11);
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(0.0),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(4.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(4.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0), Arrays.asList(),
//				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//
//		assertThat(resultActual.getLstSeqVacation())
//				.extracting(x -> x.getOutbreakDay(), x -> x.getDateOfUse(), x -> x.getDayNumberUsed(),
//						x -> x.getTargetSelectionAtr())
//				.containsExactly(
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 2), GeneralDate.ymd(2019, 11, 4),
//								new ReserveLeaveRemainingDayNumber(1.0), TargetSelectionAtr.AUTOMATIC),
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 3), GeneralDate.ymd(2019, 11, 5),
//								new ReserveLeaveRemainingDayNumber(1.0), TargetSelectionAtr.AUTOMATIC),
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 9), GeneralDate.ymd(2019, 11, 14),
//								new ReserveLeaveRemainingDayNumber(1.0), TargetSelectionAtr.AUTOMATIC),
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 10), GeneralDate.ymd(2019, 11, 15),
//								new ReserveLeaveRemainingDayNumber(1.0), TargetSelectionAtr.AUTOMATIC));
//
//	}
//
//	// 4 ?????????????????????????????? ?????????????????????
//	// ????????? 11/2/2019 11/3/2019 (?????? = 1)
//	// ?????? 2019,11, 4 2019, 11, 5 (?????? = 0.5)
//	@Test
//	public void testCase4() {
//
//		List<InterimDayOffMng> dayOffMng = Arrays.asList(
//				DaikyuFurikyuHelper.createDayOff("d5", 0, 0.5),
//				DaikyuFurikyuHelper.createDayOff("d6", 0, 0.5));
//		
//		List<InterimBreakMng> breakMng = Arrays.asList(
//				new InterimBreakMng("d1", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)),
//
//				new InterimBreakMng("d2", new AttendanceTime(480),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//						new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0)));
//
//		List<InterimRemain> interimMng = Arrays.asList(
//				DaikyuFurikyuHelper.createRemain("d1", GeneralDate.ymd(2019, 11, 2),
//						CreateAtr.SCHEDULE, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d2", GeneralDate.ymd(2019, 11, 3),
//						CreateAtr.RECORD, RemainType.BREAK),
//
//				DaikyuFurikyuHelper.createRemain("d5", GeneralDate.ymd(2019, 11, 4),
//						CreateAtr.SCHEDULE, RemainType.SUBHOLIDAY),
//				DaikyuFurikyuHelper.createRemain("d6", GeneralDate.ymd(2019, 11, 5),
//						CreateAtr.RECORD, RemainType.SUBHOLIDAY));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2020, 10, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, breakMng, dayOffMng,//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//
//		new Expectations() {
//			{
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(
//						new EmploymentHistShareImport(SID, "02",
//								new DatePeriod(GeneralDate.ymd(2019, 05, 02), GeneralDate.ymd(2019, 11, 02))),
//						new EmploymentHistShareImport(SID, "00",
//								new DatePeriod(GeneralDate.ymd(2019, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
////				require.getClosureDataByEmployee(SID, (GeneralDate) any);
////				result = NumberRemainVacationLeaveRangeQueryTest.createClosure();
//
////				require.getFirstMonth(CID);
////				result = new CompanyDto(11);
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(1.0),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(1.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(2.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0), Arrays.asList(),
//				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//		assertThat(resultActual.getLstSeqVacation())
//				.extracting(x -> x.getOutbreakDay(), x -> x.getDateOfUse(), x -> x.getDayNumberUsed(),
//						x -> x.getTargetSelectionAtr())
//				.containsExactly(
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 2), GeneralDate.ymd(2019, 11, 4),
//								new ReserveLeaveRemainingDayNumber(0.5), TargetSelectionAtr.AUTOMATIC),
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 2), GeneralDate.ymd(2019, 11, 5),
//								new ReserveLeaveRemainingDayNumber(0.5), TargetSelectionAtr.AUTOMATIC));
//
//	}
//
//	// 5 ?????????????????????????????? ?????????????????????
//	// ????????? 11/2/2019 11/3/2019 (?????? = 0.5)
//	// ?????? 2019,11, 4 2019, 11, 5 (?????? = 1)
//	@Test
//	public void testCase5() {
//
//		List<InterimDayOffMng> dayOffMng = Arrays.asList(
//				DaikyuFurikyuHelper.createDayOff("d5", 0,1.0),
//				DaikyuFurikyuHelper.createDayOff("d6",0, 1.0));
//
//		List<InterimBreakMng> breakMng = Arrays.asList(
//				new InterimBreakMng("d1", new AttendanceTime(0),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(0.5),
//						new AttendanceTime(0), new UnUsedTime(0), new UnUsedDay(0.5)),
//
//				new InterimBreakMng("d2", new AttendanceTime(0),
//						GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(0.5),
//						new AttendanceTime(0), new UnUsedTime(0), new UnUsedDay(0.5)));
//
//		List<InterimRemain> interimMng = Arrays.asList(
//				DaikyuFurikyuHelper.createRemain("d1", GeneralDate.ymd(2019, 11, 2),
//						CreateAtr.SCHEDULE, RemainType.BREAK),
//				DaikyuFurikyuHelper.createRemain("d2", GeneralDate.ymd(2019, 11, 3),
//						CreateAtr.RECORD, RemainType.BREAK),
//
//				DaikyuFurikyuHelper.createRemain("d5", GeneralDate.ymd(2019, 11, 4),
//						CreateAtr.SCHEDULE, RemainType.SUBHOLIDAY),
//				DaikyuFurikyuHelper.createRemain("d6", GeneralDate.ymd(2019, 11, 5),
//						CreateAtr.RECORD, RemainType.SUBHOLIDAY));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2020, 10, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, breakMng, dayOffMng,//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//
//		new Expectations() {
//			{
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(
//						new EmploymentHistShareImport(SID, "02",
//								new DatePeriod(GeneralDate.ymd(2019, 05, 02), GeneralDate.ymd(2019, 11, 02))),
//						new EmploymentHistShareImport(SID, "00",
//								new DatePeriod(GeneralDate.ymd(2019, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
////				require.getClosureDataByEmployee(SID, (GeneralDate) any);
////				result = NumberRemainVacationLeaveRangeQueryTest.createClosure();
//
////				require.getFirstMonth(CID);
////				result = new CompanyDto(11);
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(-1.0),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(2.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(1.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0), Arrays.asList(DayOffError.DAYERROR),
//				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//		assertThat(resultActual.getLstSeqVacation())
//				.extracting(x -> x.getOutbreakDay(), x -> x.getDateOfUse(), x -> x.getDayNumberUsed(),
//						x -> x.getTargetSelectionAtr())
//				.containsExactly(
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 2), GeneralDate.ymd(2019, 11, 4),
//								new ReserveLeaveRemainingDayNumber(1.0), TargetSelectionAtr.AUTOMATIC),
//						Tuple.tuple(GeneralDate.ymd(2019, 11, 3), GeneralDate.ymd(2019, 11, 4),
//								new ReserveLeaveRemainingDayNumber(0.5), TargetSelectionAtr.AUTOMATIC));
//
//	}
//
//	// 6 ???????????????????????????????????????1?????????????????????2019/11/14????????????????????????
//	// ??????????????? 2019/11/14 2019/11/15 (?????? = 0.5)
//	// ???????????? 2019/8/14, ?????????2019/11/14(?????? = 1)
//	@Test
//	public void testCase6() {
//
//		List<InterimDayOffMng> dayOffMng = Arrays.asList(
//				DaikyuFurikyuHelper.createDayOff("d5", 0, 0.5),
//				DaikyuFurikyuHelper.createDayOff("d6", 0, 0.5));
//
//
//		List<InterimRemain> interimMng = Arrays.asList(
//
//				DaikyuFurikyuHelper.createRemain("d5", GeneralDate.ymd(2019, 11, 14),
//						CreateAtr.SCHEDULE, RemainType.SUBHOLIDAY),
//				DaikyuFurikyuHelper.createRemain("d6", GeneralDate.ymd(2019, 11, 15),
//						CreateAtr.RECORD, RemainType.SUBHOLIDAY));
//
//		List<LeaveManagementData> leavFix = Arrays.asList(new LeaveManagementData(
//				"d1", CID, SID, true, GeneralDate.ymd(2019, 8, 14),
//				GeneralDate.ymd(2019, 11, 14), 1.0, 0, 1.0, 0, DigestionAtr.UNUSED.value, 0, 0));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 11, 01), GeneralDate.ymd(2020, 10, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, new ArrayList<>(), dayOffMng,//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//
//		new Expectations() {
//			{
//
//				require.getBySidYmd(CID, SID, (GeneralDate) any, DigestionAtr.UNUSED);
//				result = leavFix;
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(new EmploymentHistShareImport(SID, "00",
//						new DatePeriod(GeneralDate.ymd(2010, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
//				require.findComLeavEmpSet(CID, "00");
//				result = NumberRemainVacationLeaveRangeQueryTest.createComLeav(ManageDistinct.YES, ManageDistinct.NO,
//						"00");
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(-0.5),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(1.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(1.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.5), new RemainingMinutes(0), Arrays.asList(DayOffError.DAYERROR),
//				Finally.of(GeneralDate.ymd(2020, 11, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//		assertThat(resultActual.getLstSeqVacation())
//				.extracting(x -> x.getOutbreakDay(), x -> x.getDateOfUse(), x -> x.getDayNumberUsed(),
//						x -> x.getTargetSelectionAtr())
//				.containsExactly(Tuple.tuple(GeneralDate.ymd(2019, 8, 14), GeneralDate.ymd(2019, 11, 14),
//						new ReserveLeaveRemainingDayNumber(0.5), TargetSelectionAtr.AUTOMATIC));
//
//	}
//
//	// 7 ??????1?????????????????????1??????????????? ????????????0.5??????0.5???????????????????????????????????????
//	// ??????????????? 2019/11/15 (?????? 1)
//	// ????????? 2019/11/10 (?????? 1)
//	// ???????????? 2019/10/14 (?????? 0.5) ????????? 2020/1/14
//	@Test
//	public void testCase7() {
//
//		List<InterimDayOffMng> dayOffMng = Arrays.asList(DaikyuFurikyuHelper.createDayOff("d5",0, 1.0));
//
//		List<InterimBreakMng> breakMng = Arrays.asList(new InterimBreakMng("d1",
//				new AttendanceTime(480), GeneralDate.max().addDays(-1), new OccurrenceTime(0), new OccurrenceDay(1.0),
//				new AttendanceTime(240), new UnUsedTime(0), new UnUsedDay(1.0))
//
//		);
//
//		List<InterimRemain> interimMng = Arrays.asList(DaikyuFurikyuHelper.createRemain("d1",
//				GeneralDate.ymd(2019, 11, 10), CreateAtr.SCHEDULE, RemainType.BREAK),
//
//				DaikyuFurikyuHelper.createRemain("d5", GeneralDate.ymd(2019, 11, 4),
//						CreateAtr.SCHEDULE, RemainType.SUBHOLIDAY));
//
//		List<LeaveManagementData> leavFix = Arrays.asList(new LeaveManagementData(
//				"d9", CID, SID, false, GeneralDate.ymd(2019, 10, 14),
//				GeneralDate.ymd(2020, 1, 14), 0.5, 0, 0.5, 0, DigestionAtr.UNUSED.value, 0, 0));
//
//		BreakDayOffRemainMngRefactParam inputParam = DaikyuFurikyuHelper.inputParamDaikyu(
//				new DatePeriod(GeneralDate.ymd(2019, 4, 01), GeneralDate.ymd(2020, 3, 31)), //???????????????, ???????????????
//				true,//????????? 
//				GeneralDate.ymd(2019, 11, 30), //???????????????
//				true, //??????????????????
//				interimMng, breakMng, dayOffMng,//???????????????????????????
//				Optional.empty(),//???????????????????????????
//				new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));//??????????????????????????????
//
//		new Expectations() {
//			{
//
//				require.getBySidYmd(CID, SID, (GeneralDate) any, DigestionAtr.UNUSED);
//				result = leavFix;
//
//				require.findByEmployeeIdOrderByStartDate(anyString);
//				result = Arrays.asList(
//						new EmploymentHistShareImport(SID, "02",
//								new DatePeriod(GeneralDate.ymd(2019, 05, 02), GeneralDate.ymd(2019, 11, 02))),
//						new EmploymentHistShareImport(SID, "00",
//								new DatePeriod(GeneralDate.ymd(2019, 11, 03), GeneralDate.ymd(9999, 12, 31))));
//
//				require.findEmploymentHistory(CID, SID, (GeneralDate) any);
//				result = Optional.of(new BsEmploymentHistoryImport(SID, "00", "A",
//						new DatePeriod(GeneralDate.min(), GeneralDate.max())));
//
////				require.getClosureDataByEmployee(SID, (GeneralDate) any);
////				result = NumberRemainVacationLeaveRangeQueryTest.createClosure();
//
////				require.getFirstMonth(CID);
////				result = new CompanyDto(11);
//
//			}
//
//		};
//
//		SubstituteHolidayAggrResult resultActual = NumberRemainVacationLeaveRangeQuery
//				.getBreakDayOffMngInPeriod(require, inputParam);
//
//		SubstituteHolidayAggrResult resultExpected = new SubstituteHolidayAggrResult(
//				new VacationDetails(new ArrayList<>()), new ReserveLeaveRemainingDayNumber(0.5),
//				new RemainingMinutes(0), new ReserveLeaveRemainingDayNumber(1.0), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(1.5), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(0.5), new RemainingMinutes(0),
//				new ReserveLeaveRemainingDayNumber(.0), new RemainingMinutes(0), Arrays.asList(DayOffError.PREFETCH_ERROR),
//				Finally.of(GeneralDate.ymd(2020, 04, 01)), new ArrayList<>());
//		NumberRemainVacationLeaveRangeQueryTest.assertData(resultActual, resultExpected);
//		assertThat(resultActual.getLstSeqVacation())
//				.extracting(x -> x.getOutbreakDay(), x -> x.getDateOfUse(), x -> x.getDayNumberUsed(),
//						x -> x.getTargetSelectionAtr())
//				.containsExactly(Tuple.tuple(GeneralDate.ymd(2019, 10, 14), GeneralDate.ymd(2019, 11, 4),
//						new ReserveLeaveRemainingDayNumber(1.0), TargetSelectionAtr.AUTOMATIC));
//
//	}
}