package nts.uk.ctx.at.schedule.dom.importschedule;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.Getter;
import lombok.Value;
import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.OptionalUtil;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyStartDateService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.employeeworkway.WorkingStatus;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetEmpCanReferService;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ColorCodeChar6;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterName;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * Test for WorkScheduleImportService
 * @author kumiko_otake
 *
 */
@RunWith(JMockit.class)
public class WorkScheduleImportServiceTest {

	@Injectable WorkScheduleImportService.Require require;

	/**
	 * Target	: checkIfIsImportableData
	 */
	@Test
	public void test_checkIfIsImportableData() {

		/* ??????????????? */
		// ?????????????????????
		val modifiableStartDate = GeneralDate.ymd( 2021, 6, 16 );
		// ????????????
		@SuppressWarnings("serial")
		val empCdIdMap = new HashMap<String, String>() {{
			put( "Cd#MB10", "Id#0102" );
			put( "Cd#MB11", "Id#0103" );
			put( "Cd#MB12", "Id#0104" );
			put( "Cd#MB08", "Id#0205" );
		}};
		// ??????????????????
		val orderedEmployees = Arrays.asList(
				"Cd#WS01"	// ??????ID: ??????
			,	"Cd#Li04"	// ??????ID: ??????
			,	"Cd#MB08"	// ??????ID: Id#0205
			,	"Cd#St09"	// ??????ID: ??????
			,	"Cd#MB10"	// ??????ID: Id#0102
			,	"Cd#MB11"	// ??????ID: Id#0103
			,	"Cd#MB12"	// ??????ID: Id#0104
		);

		/*
		 * ??????????????????
		 * ====================
		 * 05/31 -> Cd#WS01
		 * ---
		 * 06/09 -> Cd#MB10
		 * 06/10 -> Cd#WS01, Cd#MB10, Cd#MB11
		 * 06/11 -> Cd#Li04, Cd#MB10
		 * ---
		 * 06/13 -> Cd#Li04, Cd#St09
		 * 06/14 -> Cd#WS01, Cd#Li04
		 * 06/15 -> Cd#MB11, Cd#MB12
		 * <<?????????????????????>>
		 * 06/16 -> Cd#MB11, Cd#MB12
		 * ---
		 * 06/20 -> Cd#WS01, Cd#MB11
		 * 06/21 -> Cd#MB08, Cd#MB11, Cd#MB12
		 * 06/22 -> Cd#WS01
		 * ---
		 * 07/01 -> Cd#MB08
		 * ====================
		 */
		@SuppressWarnings("serial")
		val rawDataOfCells = new ArrayList<CapturedRawDataOfCell>() {{
			// ??????CD: Cd#WS01 / ??????ID: ??????
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 5, 31 ), new ShiftMasterImportCode("Imp#YYY") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 10 ), new ShiftMasterImportCode("Imp#FFF") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 14 ), new ShiftMasterImportCode("Imp#DDD") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 20 ), new ShiftMasterImportCode("Imp#MMM") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 22 ), new ShiftMasterImportCode("Imp#GGG") ) );
			// ??????CD: Cd#Li04 / ??????ID: ??????
			add( new CapturedRawDataOfCell( "Cd#Li04", GeneralDate.ymd( 2021, 6, 11 ), new ShiftMasterImportCode("Imp#GGG") ) );
			add( new CapturedRawDataOfCell( "Cd#Li04", GeneralDate.ymd( 2021, 6, 13 ), new ShiftMasterImportCode("Imp#KKK") ) );
			add( new CapturedRawDataOfCell( "Cd#Li04", GeneralDate.ymd( 2021, 6, 14 ), new ShiftMasterImportCode("Imp#XXX") ) );
			// ??????CD: Cd#MB08 / ??????ID: Id#0205
			add( new CapturedRawDataOfCell( "Cd#MB08", GeneralDate.ymd( 2021, 6, 21 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB08", GeneralDate.ymd( 2021, 7,  1 ), new ShiftMasterImportCode("Imp#CCC") ) );
			// ??????CD: Cd#St09 / ??????ID: ??????
			add( new CapturedRawDataOfCell( "Cd#St09", GeneralDate.ymd( 2021, 6, 13 ), new ShiftMasterImportCode("Imp#TXS") ) );
			// ??????CD: Cd#MB10 / ??????ID: Id#0102
			add( new CapturedRawDataOfCell( "Cd#MB10", GeneralDate.ymd( 2021, 6,  9 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB10", GeneralDate.ymd( 2021, 6, 10 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB10", GeneralDate.ymd( 2021, 6, 11 ), new ShiftMasterImportCode("Imp#AAA") ) );
			// ??????CD: Cd#MB11 / ??????ID: Id#0103
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 10 ), new ShiftMasterImportCode("Imp#BBB") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 15 ), new ShiftMasterImportCode("Imp#CCC") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 16 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 20 ), new ShiftMasterImportCode("Imp#DDD") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 21 ), new ShiftMasterImportCode("Imp#EEE") ) );
			// ??????CD: Cd#MB12 / ??????ID: Id#0104
			add( new CapturedRawDataOfCell( "Cd#MB12", GeneralDate.ymd( 2021, 6, 15 ), new ShiftMasterImportCode("Imp#CCC") ) );
			add( new CapturedRawDataOfCell( "Cd#MB12", GeneralDate.ymd( 2021, 6, 16 ), new ShiftMasterImportCode("Imp#XYZ") ) );
			add( new CapturedRawDataOfCell( "Cd#MB12", GeneralDate.ymd( 2021, 6, 21 ), new ShiftMasterImportCode("Imp#ABC") ) );
		}};

		new Expectations( ScheModifyStartDateService.class ) {{
			// ????????????????????????????????????
			ScheModifyStartDateService.getModifyStartDate(require, anyString);
			result = modifiableStartDate;
			// ???????????????????????????
			@SuppressWarnings("unchecked") val anyCodes = (List<String>)any;
			require.getEmployeeIds(anyCodes);
			result = empCdIdMap;
		}};


		/* ?????? */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkIfIsImportableData"
				, require
				, new CapturedRawData( rawDataOfCells, orderedEmployees )
		);


		/* ?????? */
		// ?????????????????????
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrder(
						GeneralDate.ymd( 2021, 5, 31 )
					,	GeneralDate.ymd( 2021, 6,  9 )
					,	GeneralDate.ymd( 2021, 6, 10 )
					,	GeneralDate.ymd( 2021, 6, 11 )
					,	GeneralDate.ymd( 2021, 6, 13 )
					,	GeneralDate.ymd( 2021, 6, 14 )
					,	GeneralDate.ymd( 2021, 6, 15 )
			);

		// ?????????????????????
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrder( "Cd#WS01", "Cd#Li04", "Cd#St09" );

		// ??????????????????
		assertThat( result.getOrderOfEmployees() )
			.containsExactly(
					new EmployeeId( "Id#0205" )	// ??????CD: Cd#MB08
				,	new EmployeeId( "Id#0102" )	// ??????CD: Cd#MB10
				,	new EmployeeId( "Id#0103" )	// ??????CD: Cd#MB11
				,	new EmployeeId( "Id#0104" )	// ??????CD: Cd#MB12
			);

		// ??????????????????
		assertThat( result.getResults() )
			.containsExactlyInAnyOrder(
					ImportResultHelper.createDetail( "Id#0103", GeneralDate.ymd( 2021, 6, 16 ), "Imp#AAA", ImportStatus.UNCHECKED )
				,	ImportResultHelper.createDetail( "Id#0104", GeneralDate.ymd( 2021, 6, 16 ), "Imp#XYZ", ImportStatus.UNCHECKED )
				,	ImportResultHelper.createDetail( "Id#0103", GeneralDate.ymd( 2021, 6, 20 ), "Imp#DDD", ImportStatus.UNCHECKED )
				,	ImportResultHelper.createDetail( "Id#0205", GeneralDate.ymd( 2021, 6, 21 ), "Imp#AAA", ImportStatus.UNCHECKED )
				,	ImportResultHelper.createDetail( "Id#0103", GeneralDate.ymd( 2021, 6, 21 ), "Imp#EEE", ImportStatus.UNCHECKED )
				,	ImportResultHelper.createDetail( "Id#0104", GeneralDate.ymd( 2021, 6, 21 ), "Imp#ABC", ImportStatus.UNCHECKED )
				,	ImportResultHelper.createDetail( "Id#0205", GeneralDate.ymd( 2021, 7,  1 ), "Imp#CCC", ImportStatus.UNCHECKED )
			);

	}


	/**
	 * Target	: checkIfEmployeeIsTarget
	 */
	@Test
	public void test_checkIfEmployeeIsTarget() {

		/* ??????????????? */
		// ??????????????????
		val referableEmployees = Arrays.asList( "Id#0301", "Id#0302", "Id#0205", "Id#0104" );

		// ???????????????????????????
		@SuppressWarnings("serial")
		val importSeeds = new HashMap<ExpectImportResult, Optional<WorkingStatus>>() {{
			// ??????ID: Id#0301
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 14 ), "Imp#DDD", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 20 ), "Imp#MMM", ImportStatus.UNCHECKED, ImportStatus.EMPLOYEEINFO_IS_INVALID )
					, Optional.of(WorkingStatus.INVALID_DATA) );
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 22 ), "Imp#GGG", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			// ??????ID: Id#0302
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 13 ), "Imp#GGG", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 24 ), "Imp#SWK", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			// ??????ID: Id#0201 / ???????????????
			put( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 11 ), "Imp#GGG", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 14 ), "Imp#XXX", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// ??????ID: Id#0205
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 20 ), "Imp#AAA", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 21 ), "Imp#AAA", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 7,  1 ), "Imp#CCC", ImportStatus.UNCHECKED, ImportStatus.EMPLOYEE_IS_NOT_ENROLLED )
					, Optional.of(WorkingStatus.NOT_ENROLLED) );
			// ??????ID: Id#0101 / ???????????????
			put( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 13 ), "Imp#TXS", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// ??????ID: Id#0102 / ???????????????
			put( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 11 ), "Imp#AAA", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 24 ), "Imp#HNT", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// ??????ID: Id#0103 / ???????????????
			put( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 16 ), "Imp#AAA", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// ??????ID: Id#0104
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_NOTUSE )
					, Optional.of(WorkingStatus.DO_NOT_MANAGE_SCHEDULE) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 16 ), "Imp#XYZ", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_NOTUSE )
					, Optional.of(WorkingStatus.DO_NOT_MANAGE_SCHEDULE) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 21 ), "Imp#ABC", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 24 ), "Imp#TDS", ImportStatus.UNCHECKED )
					, Optional.of(WorkingStatus.SCHEDULE_MANAGEMENT) );
		}};

		// ??????????????????
		val interimResult = new ImportResult(
					importSeeds.entrySet().stream().map( e -> e.getKey().getCurrentResult() ).collect(Collectors.toList())
				,	IntStream.of( 9, 10, 15 ).boxed().map( num -> GeneralDate.ymd( 2021, 6, num ) ).collect(Collectors.toList())
				,	Arrays.asList( "Cd#St02", "Cd#WS03", "Cd#WS05" )
				,	Stream.of( "Id#0301", "Id#0302", "Id#0201", "Id#0205", "Id#0101", "Id#0102", "Id#0103", "Id#0104" ).map( str -> new EmployeeId(str) ).collect(Collectors.toList())
			);

		new Expectations( GetEmpCanReferService.class ) {{
			// ???????????????????????????
			GetEmpCanReferService.getAll(require, (String)any, (GeneralDate)any, (DatePeriod)any);
			result = referableEmployees;
		}};

		// ????????????
		importSeeds.entrySet().stream()
			.filter( seed -> seed.getValue().isPresent() )
			.forEach( seed -> {
				val status = Helper.createEmployeeWorkingStatus( seed.getKey().getEmployeeId(), seed.getKey().getYmd(), seed.getValue().get() );
				new Expectations( EmployeeWorkingStatus.class ) {{
					EmployeeWorkingStatus.create( require, seed.getKey().getEmployeeId().v(), seed.getKey().getYmd() );
					result = status;
				}};
			} );


		/* ?????? */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkIfEmployeeIsTarget"
				, require, interimResult
		);


		/* ?????? */
		// ????????????
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnimportableDates() );
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnexistsEmployees() );
		assertThat( result.getOrderOfEmployees() )
			.containsExactlyElementsOf( interimResult.getOrderOfEmployees() );

		// ??????????????????
		assertThat( result ).isNotEqualTo( interimResult );
		assertThat( result.getResults() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.entrySet().stream()
						.map( e -> e.getKey().getExpectedResult() )
						.collect(Collectors.toList())
			);

	}


	/**
	 * Target	: checkForContentIntegrity
	 */
	@Test
	public void test_checkForContentIntegrity() {

		/* ??????????????? */
		// ???????????????????????????
		@SuppressWarnings("serial")
		val shiftMasterMap = new HashMap<ShiftMaster, Boolean>() {{
			put( Helper.createDummyShiftMaster( "Imp#DDD" ), true );
			put( Helper.createDummyShiftMaster( "Imp#GGG" ), true );
			put( Helper.createDummyShiftMaster( "Imp#SWK" ), true );
			// put( Helper.createDummyShiftMaster( "Imp#XXX" ), false ); ??? ????????????
			put( Helper.createDummyShiftMaster( "Imp#AAA" ), false );
			put( Helper.createDummyShiftMaster( "Imp#ABC" ), true );
			put( Helper.createDummyShiftMaster( "Imp#TDS" ), false );
		}};

		// ???????????????????????????
		@SuppressWarnings("serial")
		val importSeeds = new ArrayList<ExpectImportResult>() {{

			add( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 11 ), "Imp#GGG", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 14 ), "Imp#XXX", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 13 ), "Imp#TXS", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 11 ), "Imp#AAA", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 24 ), "Imp#HNT", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 16 ), "Imp#AAA", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 20 ), "Imp#MMM", ImportStatus.EMPLOYEEINFO_IS_INVALID ) );
			add( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 7,  1 ), "Imp#CCC", ImportStatus.EMPLOYEE_IS_NOT_ENROLLED ) );
			add( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.SCHEDULE_IS_NOTUSE ) );
			add( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 16 ), "Imp#XYZ", ImportStatus.SCHEDULE_IS_NOTUSE ) );

			add( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 14 ), "Imp#DDD", ImportStatus.UNCHECKED ) );
			add( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 22 ), "Imp#GGG", ImportStatus.UNCHECKED ) );
			add( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 13 ), "Imp#GGG", ImportStatus.UNCHECKED ) );
			add( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 24 ), "Imp#SWK", ImportStatus.UNCHECKED ) );
			add( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.UNCHECKED, ImportStatus.SHIFTMASTER_IS_NOTFOUND ) );
			add( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 20 ), "Imp#AAA", ImportStatus.UNCHECKED, ImportStatus.SHIFTMASTER_IS_ERROR ) );
			add( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 21 ), "Imp#AAA", ImportStatus.UNCHECKED, ImportStatus.SHIFTMASTER_IS_ERROR ) );
			add( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 21 ), "Imp#ABC", ImportStatus.UNCHECKED ) );
			add( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 24 ), "Imp#TDS", ImportStatus.UNCHECKED, ImportStatus.SHIFTMASTER_IS_ERROR ) );

		}};

		// ??????????????????
		val interimResult = new ImportResult(
					importSeeds.stream().map( ExpectImportResult::getCurrentResult ).collect(Collectors.toList())
				,	IntStream.of( 9, 10, 15 ).boxed().map( num -> GeneralDate.ymd( 2021, 6, num ) ).collect(Collectors.toList())
				,	Arrays.asList( "Cd#St02", "Cd#WS03", "Cd#WS05" )
				,	Stream.of( "Id#0301", "Id#0302", "Id#0201", "Id#0205", "Id#0101", "Id#0102", "Id#0103", "Id#0104" ).map( str -> new EmployeeId(str) ).collect(Collectors.toList())
			);


		// ????????????????????????
		val shiftMasters = shiftMasterMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
		new Expectations() {{
			@SuppressWarnings("unchecked") val anyCodes = (List<ShiftMasterImportCode>)any;
			require.getShiftMasters(anyCodes);
			result = shiftMasters;
		}};
		// ???????????????????????????
		new MockUp<WorkInformation> () {
			@Mock public boolean checkNormalCondition(@SuppressWarnings("unused") WorkInformation.Require require) {
				return shiftMasterMap.get(this.getMockInstance());
			}
		};


		/* ?????? */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkForContentIntegrity"
				, require, interimResult
		);


		/* ?????? */
		// ????????????
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnimportableDates() );
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnexistsEmployees() );
		assertThat( result.getOrderOfEmployees() )
			.containsExactlyElementsOf( interimResult.getOrderOfEmployees() );

		// ??????????????????
		assertThat( result ).isNotEqualTo( interimResult );
		assertThat( result.getResults() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.map( ExpectImportResult::getExpectedResult )
						.collect(Collectors.toList())
			);

	}


	/**
	 * Target	: checkForExistingWorkSchedule
	 */
	@Test
	public void test_checkForExistingWorkSchedule() {

		/* ??????????????? */
		// ???????????????????????????
		@SuppressWarnings("serial")
		val importSeeds = new HashMap<ExpectImportResult, Optional<ConfirmedATR>>() {{

			put( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 11 ), "Imp#GGG", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 14 ), "Imp#XXX", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 13 ), "Imp#TXS", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 11 ), "Imp#AAA", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 24 ), "Imp#HNT", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 16 ), "Imp#AAA", ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 20 ), "Imp#MMM", ImportStatus.EMPLOYEEINFO_IS_INVALID ), Optional.empty() );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 7,  1 ), "Imp#CCC", ImportStatus.EMPLOYEE_IS_NOT_ENROLLED ), Optional.empty() );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.SCHEDULE_IS_NOTUSE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 16 ), "Imp#XYZ", ImportStatus.SCHEDULE_IS_NOTUSE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.SHIFTMASTER_IS_NOTFOUND ), Optional.empty() );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 20 ), "Imp#AAA", ImportStatus.SHIFTMASTER_IS_ERROR ), Optional.empty() );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 21 ), "Imp#AAA", ImportStatus.SHIFTMASTER_IS_ERROR ), Optional.empty() );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 24 ), "Imp#TDS", ImportStatus.SHIFTMASTER_IS_ERROR ), Optional.empty() );

			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 14 ), "Imp#DDD", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_EXISTS )
					, Optional.of(ConfirmedATR.UNSETTLED) );
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 22 ), "Imp#GGG", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_COMFIRMED )
					, Optional.of(ConfirmedATR.CONFIRMED) );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 13 ), "Imp#GGG", ImportStatus.UNCHECKED, ImportStatus.IMPORTABLE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 24 ), "Imp#SWK", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_EXISTS )
					, Optional.of(ConfirmedATR.UNSETTLED) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 21 ), "Imp#ABC", ImportStatus.UNCHECKED, ImportStatus.IMPORTABLE ), Optional.empty() );

		}};

		// ??????????????????
		val interimResult = new ImportResult(
					importSeeds.entrySet().stream().map( e -> e.getKey().getCurrentResult() ).collect(Collectors.toList())
				,	IntStream.of( 9, 10, 15 ).boxed().map( num -> GeneralDate.ymd( 2021, 6, num ) ).collect(Collectors.toList())
				,	Arrays.asList( "Cd#St02", "Cd#WS03", "Cd#WS05" )
				,	Stream.of( "Id#0301", "Id#0302", "Id#0201", "Id#0205", "Id#0101", "Id#0102", "Id#0103", "Id#0104" ).map( str -> new EmployeeId(str) ).collect(Collectors.toList())
			);


		// ??????????????????
		importSeeds.entrySet().stream()
			.filter( seed -> seed.getValue().isPresent() )
			.forEach( seed -> {
				new Expectations() {{
					// ???????????????????????????????????????
					require.isWorkScheduleExisted( seed.getKey().getEmployeeId(), seed.getKey().getYmd() );
					result = true;
					// ???????????????????????????????????????
					require.isWorkScheduleComfirmed( seed.getKey().getEmployeeId(), seed.getKey().getYmd() );
					result = ( seed.getValue().get() == ConfirmedATR.CONFIRMED );
				}};
			} );


		/* ?????? */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkForExistingWorkSchedule"
				, require, interimResult
		);


		/* ?????? */
		// ????????????
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnimportableDates() );
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnexistsEmployees() );
		assertThat( result.getOrderOfEmployees() )
			.containsExactlyElementsOf( interimResult.getOrderOfEmployees() );

		// ??????????????????
		assertThat( result ).isNotEqualTo( interimResult );
		assertThat( result.getResults() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.entrySet().stream()
						.map( e -> e.getKey().getExpectedResult() )
						.collect(Collectors.toList())
			);


	}


	/**
	 * Target	:
	 * 	- checkIfEmployeeIsTarget
	 * 	- checkForContentIntegrity
	 * 	- checkForExistingWorkSchedule
	 * Pattern	: ?????????????????????????????????????????????
	 */
	@Test
	public void test_unexistsUncheckedResults(@Injectable ImportResult interimResult) {

		/* ???????????????????????????????????? */
		new Expectations() {{
			interimResult.existsUncheckedResults();
			result = false;
		}};


		/* ??????????????????????????????????????????: checkIfEmployeeIsTarget */
		{
			// ??????
			ImportResult result = NtsAssert.Invoke.staticMethod(
					WorkScheduleImportService.class, "checkIfEmployeeIsTarget"
						, require, interimResult
			);
			// ??????
			assertThat( result ).isEqualTo( interimResult );
		}

		/* ???????????????????????????????????????????????????: checkForContentIntegrity */
		{
			// ??????
			ImportResult result = NtsAssert.Invoke.staticMethod(
					WorkScheduleImportService.class, "checkForContentIntegrity"
						, require, interimResult
			);
			// ??????
			assertThat( result ).isEqualTo( interimResult );
		}

		/* ????????????????????????????????????????????????: checkForExistingWorkSchedule */
		{
			// ??????
			ImportResult result = NtsAssert.Invoke.staticMethod(
					WorkScheduleImportService.class, "checkForExistingWorkSchedule"
						, require, interimResult
			);
			// ??????
			assertThat( result ).isEqualTo( interimResult );
		}

	}


	/**
	 * Target	: importFrom
	 */
	@Test
	public void test_importFrom() {

		/** ??????????????? **/
		// ?????????????????????
		val modifiableStartDate = GeneralDate.ymd( 2021, 6, 8 );
		// ????????????
		@SuppressWarnings("serial")
		val empCdIdMap = new HashMap<String, String>() {{
			put( "Cd#WS01", "Id#0301" );
			put( "Cd#WS03", "Id#0303" );
			put( "Cd#WS05", "Id#0202" );
			put( "Cd#WS06", "Id#0203" );
			put( "Cd#WS07", "Id#0204" );
			put( "Cd#St02", "Id#0302" );
			put( "Cd#St09", "Id#0101" );
			put( "Cd#MB08", "Id#0205" );
			put( "Cd#MB10", "Id#0102" );
			put( "Cd#MB11", "Id#0103" );
			put( "Cd#MB12", "Id#0104" );
			put( "Cd#Li04", "Id#0201" );
		}};
		// ??????????????????
		val orderedEmployeeCodes = Arrays.asList(
				"Cd#WS01", "Cd#St02", "Cd#WS03", "Cd#Mng1"
			,	"Cd#Li04", "Cd#WS05", "Cd#WS06", "Cd#WS07", "Cd#MB08"
			,	"Cd#St09", "Cd#MB10", "Cd#MB11", "Cd#MB12", "Cd#Mng2"
		);
		val orderedEmployeeIds = Stream.of(
				"Id#0301", "Id#0302", "Id#0303"
			,	"Id#0201", "Id#0202", "Id#0203", "Id#0204", "Id#0205"
			,	"Id#0101", "Id#0102", "Id#0103", "Id#0104"
		).map(EmployeeId::new).collect(Collectors.toList());
		// ??????????????????
		val referableEmployees = Arrays.asList(
				"Id#0301", "Id#0302", "Id#0303"
			,	"Id#0201", "Id#0202"
			,	"Id#0101", "Id#0102", "Id#0103", "Id#0104"
		);
		// ???????????????????????????
		@SuppressWarnings("serial")
		val shiftMasterMap = new HashMap<ShiftMaster, Boolean>() {{
			put( Helper.createDummyShiftMaster( "Imp#046" ), true );
			put( Helper.createDummyShiftMaster( "Imp#352" ), true );
			put( Helper.createDummyShiftMaster( "Imp#934" ), false );
			put( Helper.createDummyShiftMaster( "Imp#627" ), true );
			put( Helper.createDummyShiftMaster( "Imp#251" ), true );
			put( Helper.createDummyShiftMaster( "Imp#170" ), true );
			put( Helper.createDummyShiftMaster( "Imp#563" ), true );
			put( Helper.createDummyShiftMaster( "Imp#610" ), true );
			put( Helper.createDummyShiftMaster( "Imp#629" ), true );
			//put( Helper.createDummyShiftMaster( "Imp#310" ), true ); ??? ????????????
		}};

		// ??????????????????
		@SuppressWarnings("serial")
		val importSeeds = new ArrayList<ExpectImportFromRawData>() {{

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS01", GeneralDate.ymd( 2021, 6,  5 ), "Imp#629" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS01", GeneralDate.ymd( 2021, 6, 13 ), "Imp#563", ImportStatus.SCHEDULE_IS_NOTUSE, WorkingStatus.DO_NOT_MANAGE_SCHEDULE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St02", GeneralDate.ymd( 2021, 6,  5 ), "Imp#310" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St02", GeneralDate.ymd( 2021, 6,  6 ), "Imp#610" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St02", GeneralDate.ymd( 2021, 6, 13 ), "Imp#310", ImportStatus.SHIFTMASTER_IS_NOTFOUND, WorkingStatus.SCHEDULE_MANAGEMENT ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS03", GeneralDate.ymd( 2021, 6,  8 ), "Imp#251", ImportStatus.IMPORTABLE, WorkingStatus.ON_LEAVE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS03", GeneralDate.ymd( 2021, 6, 11 ), "Imp#629", ImportStatus.SCHEDULE_IS_EXISTS, WorkingStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS03", GeneralDate.ymd( 2021, 6, 13 ), "Imp#610", ImportStatus.EMPLOYEEINFO_IS_INVALID, WorkingStatus.INVALID_DATA ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng1", GeneralDate.ymd( 2021, 6,  8 ), "Imp#170" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng1", GeneralDate.ymd( 2021, 6, 11 ), "Imp#934" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng1", GeneralDate.ymd( 2021, 6, 12 ), "Imp#610" ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Li04", GeneralDate.ymd( 2021, 6,  6 ), "Imp#352" ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS05", GeneralDate.ymd( 2021, 6, 12 ), "Imp#629", ImportStatus.SCHEDULE_IS_EXISTS, WorkingStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS06", GeneralDate.ymd( 2021, 6, 10 ), "Imp#251", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS06", GeneralDate.ymd( 2021, 6, 12 ), "Imp#934", ImportStatus.OUT_OF_REFERENCE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6,  7 ), "Imp#610" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6,  8 ), "Imp#934", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6, 10 ), "Imp#352", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6, 13 ), "Imp#046", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6, 14 ), "Imp#251", ImportStatus.OUT_OF_REFERENCE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB08", GeneralDate.ymd( 2021, 6, 12 ), "Imp#627", ImportStatus.OUT_OF_REFERENCE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St09", GeneralDate.ymd( 2021, 6,  7 ), "Imp#046" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St09", GeneralDate.ymd( 2021, 6, 10 ), "Imp#251", ImportStatus.SCHEDULE_IS_EXISTS, WorkingStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St09", GeneralDate.ymd( 2021, 6, 13 ), "Imp#310", ImportStatus.EMPLOYEE_IS_NOT_ENROLLED, WorkingStatus.NOT_ENROLLED ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6,  5 ), "Imp#251" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6,  8 ), "Imp#046", ImportStatus.SCHEDULE_IS_EXISTS, WorkingStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6, 13 ), "Imp#627", ImportStatus.SCHEDULE_IS_COMFIRMED, WorkingStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.CONFIRMED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6, 14 ), "Imp#251", ImportStatus.IMPORTABLE, WorkingStatus.SCHEDULE_MANAGEMENT ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB11", GeneralDate.ymd( 2021, 6, 10 ), "Imp#170", ImportStatus.SCHEDULE_IS_NOTUSE, WorkingStatus.DO_NOT_MANAGE_SCHEDULE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6,  6 ), "Imp#627" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6,  8 ), "Imp#310", ImportStatus.EMPLOYEEINFO_IS_INVALID, WorkingStatus.INVALID_DATA ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6,  9 ), "Imp#610", ImportStatus.EMPLOYEEINFO_IS_INVALID, WorkingStatus.INVALID_DATA ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6, 13 ), "Imp#352", ImportStatus.IMPORTABLE, WorkingStatus.ON_LEAVE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6, 15 ), "Imp#934", ImportStatus.SHIFTMASTER_IS_ERROR, WorkingStatus.SCHEDULE_MANAGEMENT ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng2", GeneralDate.ymd( 2021, 6,  9 ), "Imp#352" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng2", GeneralDate.ymd( 2021, 6, 10 ), "Imp#563" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng2", GeneralDate.ymd( 2021, 6, 11 ), "Imp#251" ) );

		}};


		/* ?????????????????????????????? */
		new Expectations( ScheModifyStartDateService.class ) {{

			// ????????????????????????????????????
			ScheModifyStartDateService.getModifyStartDate(require, anyString);
			result = modifiableStartDate;

			// ???????????????????????????
			@SuppressWarnings("unchecked") val anyCodes = (List<String>)any;
			require.getEmployeeIds(anyCodes);
			result = empCdIdMap;

		}};

		/* ?????????????????? */
		new Expectations( GetEmpCanReferService.class ) {{
			// ???????????????????????????
			GetEmpCanReferService.getAll(require, (String)any, (GeneralDate)any, (DatePeriod)any);
			result = referableEmployees;
		}};

		/* ???????????? */
		new MockUp<EmployeeWorkingStatus>() {
			@Mock EmployeeWorkingStatus create(@SuppressWarnings("unused") EmployeeWorkingStatus.Require require, String employeeID, GeneralDate date) {
				val importSeed = importSeeds.stream()
						.filter( seed -> seed.getEmployeeId().orElse(new EmployeeId("")).v().equals(employeeID) && seed.getYmd().equals(date) )
						.findFirst().get();
				return Helper.createEmployeeWorkingStatus( importSeed.getEmployeeId().get(), importSeed.getYmd(), importSeed.getScheMngStatus().get() );
			}
		};

		/* ?????????????????? */
		val shiftMasters = shiftMasterMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
		new Expectations() {{
			// ?????????????????????????????????
			@SuppressWarnings("unchecked") val anyCodes = (List<ShiftMasterImportCode>)any;
			require.getShiftMasters(anyCodes);
			result = shiftMasters;
		}};
		// ????????????????????????????????????????????????
		new MockUp<WorkInformation>() {
			@Mock public boolean checkNormalCondition(@SuppressWarnings("unused") WorkInformation.Require require) {
				return shiftMasterMap.get(this.getMockInstance());
			}
		};

		/* ???????????? */
		val importSeedsByEmpId = importSeeds.stream()
			.filter( seed -> seed.getEmployeeId().isPresent() && seed.isNeedWorkSchedule() )
			.collect(Collectors.groupingBy( seed -> seed.getEmployeeId().get() ));
		orderedEmployeeIds.stream().forEach( employeeId -> {
			importSeedsByEmpId.getOrDefault( employeeId, Collections.emptyList() ).forEach( seed -> {

				val isScheduleExisted = seed.getConfirmedStatus().isPresent();
				new Expectations() {{
					// ???????????????????????????????????????
					require.isWorkScheduleExisted(seed.getEmployeeId().get(), seed.getYmd());
					result = isScheduleExisted;
				}};

				if ( isScheduleExisted ) {
					new Expectations() {{
						// ???????????????????????????????????????
						require.isWorkScheduleComfirmed(seed.getEmployeeId().get(), seed.getYmd());
						result = seed.getConfirmedStatus().map( e -> e == ConfirmedATR.CONFIRMED ).orElse(false);
					}};
				}

			} );
		} );


		/** ?????? **/
		// ??????
		val rawDataOfCells = importSeeds.stream().map( ExpectImportFromRawData::getAsCapturedRaw ).collect(Collectors.toList());
		val result = WorkScheduleImportService.importFrom( require, new CapturedRawData( rawDataOfCells, orderedEmployeeCodes ) );


		/** ?????? **/
		// ???????????????
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.filter( seed -> seed.getYmd().before(modifiableStartDate) )
						.map(ExpectImportFromRawData::getYmd)
						.distinct()
						.collect(Collectors.toList())
				);
		// ?????????????????????
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.filter( seed -> !seed.getEmployeeId().isPresent() )
						.map(ExpectImportFromRawData::getEmployeeCode)
						.distinct()
						.collect(Collectors.toList())
				);
		// ??????????????????
		assertThat( result.getOrderOfEmployees() ).containsExactlyElementsOf( orderedEmployeeIds );
		// ??????????????????
		assertThat( result.getResults() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.map(ExpectImportFromRawData::getAsExpectedImportResult)
						.flatMap(OptionalUtil::stream)
						.collect(Collectors.toList())
				);

		// ??????????????????????????????????????????????????????
		assertThat( result.getResults() )
			.extracting( ImportResultDetail::getStatus )
			.doesNotContain( ImportStatus.UNCHECKED );

	}



	private static class Helper {

		/**
		 * ????????????????????????????????????
		 * @param employeeId ??????ID
		 * @param date ?????????
		 * @param status ??????????????????
		 * @return
		 */
		public static EmployeeWorkingStatus createEmployeeWorkingStatus(EmployeeId employeeId, GeneralDate date, WorkingStatus status) {
			return new EmployeeWorkingStatus( employeeId.v(), date, status, Optional.empty(), Optional.empty(), Optional.empty() );
		}

		/**
		 * ?????????????????????????????????????????????????????????????????????
		 * @param importCode ?????????????????????
		 * @return ??????????????????(dummy)
		 */
		public static ShiftMaster createDummyShiftMaster(String importCode) {
			return ShiftMaster.create(
						"companyId", new ShiftMasterCode("code")
					,	new ShiftMasterDisInfor(new ShiftMasterName("name"), new ColorCodeChar6("ffffff"), new ColorCodeChar6("000000"), Optional.empty())
					,	new WorkTypeCode("workTypeCode"), Optional.of(new WorkTimeCode("workTimeCode"))
					,	Optional.of(new ShiftMasterImportCode(importCode))
				);
		}

	}

	@Getter
	private class ExpectImportResult {

		private final EmployeeId employeeId;
		private final GeneralDate ymd;
		private final ShiftMasterImportCode importCode;

		private final ImportResultDetail currentResult;
		private final ImportResultDetail expectedResult;

		public ExpectImportResult(String employeeId, GeneralDate ymd, String importCode, ImportStatus currentStatus, ImportStatus expectedStatus) {
			this.employeeId = new EmployeeId(employeeId);
			this.ymd = ymd;
			this.importCode = new ShiftMasterImportCode(importCode);

			this.currentResult = ImportResultHelper.createDetail( employeeId, ymd, importCode, currentStatus );
			this.expectedResult = ImportResultHelper.createDetail( employeeId, ymd, importCode, expectedStatus );
		}

		public ExpectImportResult(String employeeId, GeneralDate ymd, String importCode, ImportStatus currentStatus) {
			this( employeeId, ymd, importCode, currentStatus, currentStatus );
		}

	}


	@Value
	private class ExpectImportFromRawData {

		private final String employeeCode;
		private final GeneralDate ymd;
		private final ShiftMasterImportCode importCode;

		private final Optional<ImportStatus> expectedStatus;

		private final Optional<WorkingStatus> scheMngStatus;
		private final Optional<ConfirmedATR> confirmedStatus;

		private final Optional<EmployeeId> employeeId;
		private final boolean needWorkSchedule;

		private final CapturedRawDataOfCell asCapturedRaw;
		private final Optional<ImportResultDetail> asExpectedImportResult;


		public ExpectImportFromRawData(Map<String, String> empCdIdMap, String employeeCode, GeneralDate ymd, String importCode) {
			this(empCdIdMap, employeeCode, ymd, importCode, null, null, null);
		}
		public ExpectImportFromRawData(Map<String, String> empCdIdMap, String employeeCode, GeneralDate ymd, String importCode, ImportStatus expectedStatus) {
			this(empCdIdMap, employeeCode, ymd, importCode, expectedStatus, null, null);
		}
		public ExpectImportFromRawData(Map<String, String> empCdIdMap
				,	String employeeCode, GeneralDate ymd, String importCode
				,	ImportStatus expectedStatus, WorkingStatus scheMngStatus
		) {
			this(empCdIdMap, employeeCode, ymd, importCode, expectedStatus, scheMngStatus, null);
		}
		public ExpectImportFromRawData(Map<String, String> empCdIdMap
				,	String employeeCode, GeneralDate ymd, String importCode
				,	ImportStatus expectedStatus, WorkingStatus scheMngStatus, ConfirmedATR confirmedStatus
		) {
			this.employeeCode = employeeCode;
			this.ymd = ymd;
			this.importCode = new ShiftMasterImportCode(importCode);
			this.expectedStatus = Optional.ofNullable(expectedStatus);
			this.scheMngStatus = Optional.ofNullable(scheMngStatus);
			this.confirmedStatus = Optional.ofNullable(confirmedStatus);

			this.employeeId = empCdIdMap.entrySet().stream()
					.filter( entry -> entry.getKey().equals(employeeCode) )
					.map(Map.Entry::getValue)
					.findFirst().map(EmployeeId::new);

			this.needWorkSchedule = this.confirmedStatus.isPresent()
					|| this.expectedStatus.orElse(ImportStatus.UNCHECKED) == ImportStatus.IMPORTABLE;

			this.asCapturedRaw = new CapturedRawDataOfCell( this.employeeCode, this.ymd, this.importCode );

			this.asExpectedImportResult = this.expectedStatus
					.map( status -> ImportResultHelper.createDetail( this.employeeId.get().v(), this.ymd, this.importCode.v(), status ) );

		}

	}

}
