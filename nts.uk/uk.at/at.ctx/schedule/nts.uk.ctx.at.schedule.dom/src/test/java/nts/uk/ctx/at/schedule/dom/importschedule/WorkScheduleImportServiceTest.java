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
import nts.gul.util.OptionalUtil;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyStartDateService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatus;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
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

		/* 各種データ */
		// 修正可能開始日
		val modifiableStartDate = GeneralDate.ymd( 2021, 6, 16 );
		// 社員情報
		@SuppressWarnings("serial")
		val empCdIdMap = new HashMap<String, String>() {{
			put( "Cd#MB10", "Id#0102" );
			put( "Cd#MB11", "Id#0103" );
			put( "Cd#MB12", "Id#0104" );
			put( "Cd#MB08", "Id#0205" );
		}};
		// 社員の並び順
		val orderedEmployees = Arrays.asList(
				"Cd#WS01"	// 社員ID: なし
			,	"Cd#Li04"	// 社員ID: なし
			,	"Cd#MB08"	// 社員ID: Id#0205
			,	"Cd#St09"	// 社員ID: なし
			,	"Cd#MB10"	// 社員ID: Id#0102
			,	"Cd#MB11"	// 社員ID: Id#0103
			,	"Cd#MB12"	// 社員ID: Id#0104
		);

		/*
		 * 取り込み内容
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
		 * <<編集可能開始日>>
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
			// 社員CD: Cd#WS01 / 社員ID: なし
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 5, 31 ), new ShiftMasterImportCode("Imp#YYY") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 10 ), new ShiftMasterImportCode("Imp#FFF") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 14 ), new ShiftMasterImportCode("Imp#DDD") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 20 ), new ShiftMasterImportCode("Imp#MMM") ) );
			add( new CapturedRawDataOfCell( "Cd#WS01", GeneralDate.ymd( 2021, 6, 22 ), new ShiftMasterImportCode("Imp#GGG") ) );
			// 社員CD: Cd#Li04 / 社員ID: なし
			add( new CapturedRawDataOfCell( "Cd#Li04", GeneralDate.ymd( 2021, 6, 11 ), new ShiftMasterImportCode("Imp#GGG") ) );
			add( new CapturedRawDataOfCell( "Cd#Li04", GeneralDate.ymd( 2021, 6, 13 ), new ShiftMasterImportCode("Imp#KKK") ) );
			add( new CapturedRawDataOfCell( "Cd#Li04", GeneralDate.ymd( 2021, 6, 14 ), new ShiftMasterImportCode("Imp#XXX") ) );
			// 社員CD: Cd#MB08 / 社員ID: Id#0205
			add( new CapturedRawDataOfCell( "Cd#MB08", GeneralDate.ymd( 2021, 6, 21 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB08", GeneralDate.ymd( 2021, 7,  1 ), new ShiftMasterImportCode("Imp#CCC") ) );
			// 社員CD: Cd#St09 / 社員ID: なし
			add( new CapturedRawDataOfCell( "Cd#St09", GeneralDate.ymd( 2021, 6, 13 ), new ShiftMasterImportCode("Imp#TXS") ) );
			// 社員CD: Cd#MB10 / 社員ID: Id#0102
			add( new CapturedRawDataOfCell( "Cd#MB10", GeneralDate.ymd( 2021, 6,  9 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB10", GeneralDate.ymd( 2021, 6, 10 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB10", GeneralDate.ymd( 2021, 6, 11 ), new ShiftMasterImportCode("Imp#AAA") ) );
			// 社員CD: Cd#MB11 / 社員ID: Id#0103
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 10 ), new ShiftMasterImportCode("Imp#BBB") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 15 ), new ShiftMasterImportCode("Imp#CCC") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 16 ), new ShiftMasterImportCode("Imp#AAA") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 20 ), new ShiftMasterImportCode("Imp#DDD") ) );
			add( new CapturedRawDataOfCell( "Cd#MB11", GeneralDate.ymd( 2021, 6, 21 ), new ShiftMasterImportCode("Imp#EEE") ) );
			// 社員CD: Cd#MB12 / 社員ID: Id#0104
			add( new CapturedRawDataOfCell( "Cd#MB12", GeneralDate.ymd( 2021, 6, 15 ), new ShiftMasterImportCode("Imp#CCC") ) );
			add( new CapturedRawDataOfCell( "Cd#MB12", GeneralDate.ymd( 2021, 6, 16 ), new ShiftMasterImportCode("Imp#XYZ") ) );
			add( new CapturedRawDataOfCell( "Cd#MB12", GeneralDate.ymd( 2021, 6, 21 ), new ShiftMasterImportCode("Imp#ABC") ) );
		}};

		new Expectations( ScheModifyStartDateService.class ) {{
			// 修正可能開始日を取得する
			ScheModifyStartDateService.getModifyStartDate(require, anyString);
			result = modifiableStartDate;
			// 社員情報を取得する
			@SuppressWarnings("unchecked") val anyCodes = (List<String>)any;
			require.getEmployeeIds(anyCodes);
			result = empCdIdMap;
		}};


		/* 実行 */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkIfIsImportableData"
				, require
				, new CapturedRawData( rawDataOfCells, orderedEmployees )
		);


		/* 検証 */
		// 取り込み不可日
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

		// 存在しない社員
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrder( "Cd#WS01", "Cd#Li04", "Cd#St09" );

		// 社員の並び順
		assertThat( result.getOrderOfEmployees() )
			.containsExactly(
					new EmployeeId( "Id#0205" )	// 社員CD: Cd#MB08
				,	new EmployeeId( "Id#0102" )	// 社員CD: Cd#MB10
				,	new EmployeeId( "Id#0103" )	// 社員CD: Cd#MB11
				,	new EmployeeId( "Id#0104" )	// 社員CD: Cd#MB12
			);

		// 取り込み結果
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

		/* 各種データ */
		// 参照可能社員
		val referableEmployees = Arrays.asList( "Id#0301", "Id#0302", "Id#0205", "Id#0104" );

		// 取り込み結果リスト
		@SuppressWarnings("serial")
		val importSeeds = new HashMap<ExpectImportResult, Optional<ScheManaStatus>>() {{
			// 社員ID: Id#0301
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 14 ), "Imp#DDD", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 20 ), "Imp#MMM", ImportStatus.UNCHECKED, ImportStatus.EMPLOYEEINFO_IS_INVALID )
					, Optional.of(ScheManaStatus.INVALID_DATA) );
			put( new ExpectImportResult( "Id#0301", GeneralDate.ymd( 2021, 6, 22 ), "Imp#GGG", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			// 社員ID: Id#0302
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 13 ), "Imp#GGG", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 24 ), "Imp#SWK", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0302", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			// 社員ID: Id#0201 / 参照範囲外
			put( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 11 ), "Imp#GGG", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0201", GeneralDate.ymd( 2021, 6, 14 ), "Imp#XXX", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// 社員ID: Id#0205
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 20 ), "Imp#AAA", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 6, 21 ), "Imp#AAA", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0205", GeneralDate.ymd( 2021, 7,  1 ), "Imp#CCC", ImportStatus.UNCHECKED, ImportStatus.EMPLOYEE_IS_NOT_ENROLLED )
					, Optional.of(ScheManaStatus.NOT_ENROLLED) );
			// 社員ID: Id#0101 / 参照範囲外
			put( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 13 ), "Imp#TXS", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0101", GeneralDate.ymd( 2021, 6, 30 ), "Imp#XXX", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// 社員ID: Id#0102 / 参照範囲外
			put( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 11 ), "Imp#AAA", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0102", GeneralDate.ymd( 2021, 6, 24 ), "Imp#HNT", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// 社員ID: Id#0103 / 参照範囲外
			put( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			put( new ExpectImportResult( "Id#0103", GeneralDate.ymd( 2021, 6, 16 ), "Imp#AAA", ImportStatus.UNCHECKED, ImportStatus.OUT_OF_REFERENCE ), Optional.empty() );
			// 社員ID: Id#0104
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 15 ), "Imp#CCC", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_NOTUSE )
					, Optional.of(ScheManaStatus.DO_NOT_MANAGE_SCHEDULE) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 16 ), "Imp#XYZ", ImportStatus.UNCHECKED, ImportStatus.SCHEDULE_IS_NOTUSE )
					, Optional.of(ScheManaStatus.DO_NOT_MANAGE_SCHEDULE) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 21 ), "Imp#ABC", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
			put( new ExpectImportResult( "Id#0104", GeneralDate.ymd( 2021, 6, 24 ), "Imp#TDS", ImportStatus.UNCHECKED )
					, Optional.of(ScheManaStatus.SCHEDULE_MANAGEMENT) );
		}};

		// 取り込み結果
		val interimResult = new ImportResult(
					importSeeds.entrySet().stream().map( e -> e.getKey().getCurrentResult() ).collect(Collectors.toList())
				,	IntStream.of( 9, 10, 15 ).boxed().map( num -> GeneralDate.ymd( 2021, 6, num ) ).collect(Collectors.toList())
				,	Arrays.asList( "Cd#St02", "Cd#WS03", "Cd#WS05" )
				,	Stream.of( "Id#0301", "Id#0302", "Id#0201", "Id#0205", "Id#0101", "Id#0102", "Id#0103", "Id#0104" ).map( str -> new EmployeeId(str) ).collect(Collectors.toList())
			);

		new Expectations( GetEmpCanReferService.class ) {{
			// 参照可能社員の取得
			GetEmpCanReferService.getAll(require, (GeneralDate)any, anyString);
			result = referableEmployees;
		}};

		// 予定管理状態
		importSeeds.entrySet().stream()
			.filter( seed -> seed.getValue().isPresent() )
			.forEach( seed -> {
				val scheMngStatus = Helper.createScheMngStatus( seed.getKey().getEmployeeId(), seed.getKey().getYmd(), seed.getValue().get() );
				new Expectations( ScheManaStatuTempo.class ) {{
					ScheManaStatuTempo.create( require, seed.getKey().getEmployeeId().v(), seed.getKey().getYmd() );
					result = scheMngStatus;
				}};
			} );


		/* 実行 */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkIfEmployeeIsTarget"
				, require, interimResult
		);


		/* 検証 */
		// 不変項目
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnimportableDates() );
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnexistsEmployees() );
		assertThat( result.getOrderOfEmployees() )
			.containsExactlyElementsOf( interimResult.getOrderOfEmployees() );

		// 取り込み結果
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

		/* 各種データ */
		// シフトマスタの状態
		@SuppressWarnings("serial")
		val shiftMasterMap = new HashMap<ShiftMaster, Boolean>() {{
			put( Helper.createDummyShiftMaster( "Imp#DDD" ), true );
			put( Helper.createDummyShiftMaster( "Imp#GGG" ), true );
			put( Helper.createDummyShiftMaster( "Imp#SWK" ), true );
			// put( Helper.createDummyWithImportCode( "Imp#XXX" ), false ); ⇒ 取得失敗
			put( Helper.createDummyShiftMaster( "Imp#AAA" ), false );
			put( Helper.createDummyShiftMaster( "Imp#ABC" ), true );
			put( Helper.createDummyShiftMaster( "Imp#TDS" ), false );
		}};

		// 取り込み結果リスト
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

		// 取り込み結果
		val interimResult = new ImportResult(
					importSeeds.stream().map( ExpectImportResult::getCurrentResult ).collect(Collectors.toList())
				,	IntStream.of( 9, 10, 15 ).boxed().map( num -> GeneralDate.ymd( 2021, 6, num ) ).collect(Collectors.toList())
				,	Arrays.asList( "Cd#St02", "Cd#WS03", "Cd#WS05" )
				,	Stream.of( "Id#0301", "Id#0302", "Id#0201", "Id#0205", "Id#0101", "Id#0102", "Id#0103", "Id#0104" ).map( str -> new EmployeeId(str) ).collect(Collectors.toList())
			);


		// シフトマスタ取得
		val shiftMasters = shiftMasterMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
		new Expectations() {{
			@SuppressWarnings("unchecked") val anyCodes = (List<ShiftMasterImportCode>)any;
			require.getShiftMasters(anyCodes);
			result = shiftMasters;
		}};
		// シフトマスタの状態
		new MockUp<WorkInformation> () {
			@Mock public boolean checkNormalCondition(@SuppressWarnings("unused") WorkInformation.Require require) {
				return shiftMasterMap.get(this.getMockInstance());
			}
		};


		/* 実行 */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkForContentIntegrity"
				, require, interimResult
		);


		/* 検証 */
		// 不変項目
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnimportableDates() );
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnexistsEmployees() );
		assertThat( result.getOrderOfEmployees() )
			.containsExactlyElementsOf( interimResult.getOrderOfEmployees() );

		// 取り込み結果
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

		/* 各種データ */
		// 取り込み結果リスト
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

		// 取り込み結果
		val interimResult = new ImportResult(
					importSeeds.entrySet().stream().map( e -> e.getKey().getCurrentResult() ).collect(Collectors.toList())
				,	IntStream.of( 9, 10, 15 ).boxed().map( num -> GeneralDate.ymd( 2021, 6, num ) ).collect(Collectors.toList())
				,	Arrays.asList( "Cd#St02", "Cd#WS03", "Cd#WS05" )
				,	Stream.of( "Id#0301", "Id#0302", "Id#0201", "Id#0205", "Id#0101", "Id#0102", "Id#0103", "Id#0104" ).map( str -> new EmployeeId(str) ).collect(Collectors.toList())
			);


		// 勤務予定取得
		importSeeds.entrySet().stream()
			.filter( seed -> seed.getValue().isPresent() )
			.forEach( seed -> {
				new Expectations() {{
					// 勤務予定が登録されているか
					require.isWorkScheduleExisted( seed.getKey().getEmployeeId(), seed.getKey().getYmd() );
					result = true;
					// 勤務予定が確定されているか
					require.isWorkScheduleComfirmed( seed.getKey().getEmployeeId(), seed.getKey().getYmd() );
					result = ( seed.getValue().get() == ConfirmedATR.CONFIRMED );
				}};
			} );


		/* 実行 */
		ImportResult result = NtsAssert.Invoke.staticMethod( WorkScheduleImportService.class
			, "checkForExistingWorkSchedule"
				, require, interimResult
		);


		/* 検証 */
		// 不変項目
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnimportableDates() );
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf( interimResult.getUnexistsEmployees() );
		assertThat( result.getOrderOfEmployees() )
			.containsExactlyElementsOf( interimResult.getOrderOfEmployees() );

		// 取り込み結果
		assertThat( result.getResults() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.entrySet().stream()
						.map( e -> e.getKey().getExpectedResult() )
						.collect(Collectors.toList())
			);

	}



	@Test
	public void test_importFrom() {

		/** 各種データ **/
		// 修正可能開始日
		val modifiableStartDate = GeneralDate.ymd( 2021, 6, 8 );
		// 社員情報
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
		// 社員の並び順
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
		// 参照可能社員
		val referableEmployees = Arrays.asList(
				"Id#0301", "Id#0302", "Id#0303"
			,	"Id#0201", "Id#0202"
			,	"Id#0101", "Id#0102", "Id#0103", "Id#0104"
		);
		// シフトマスタと状態
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
			//put( Helper.createDummyShiftMaster( "Imp#310" ), true ); ⇒ 取得失敗
		}};

		// 取り込み内容
		@SuppressWarnings("serial")
		val importSeeds = new ArrayList<ExpectImportFromRawData>() {{

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS01", GeneralDate.ymd( 2021, 6,  5 ), "Imp#629" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS01", GeneralDate.ymd( 2021, 6, 13 ), "Imp#563", ImportStatus.SCHEDULE_IS_NOTUSE, ScheManaStatus.DO_NOT_MANAGE_SCHEDULE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St02", GeneralDate.ymd( 2021, 6,  5 ), "Imp#310" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St02", GeneralDate.ymd( 2021, 6,  6 ), "Imp#610" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St02", GeneralDate.ymd( 2021, 6, 13 ), "Imp#310", ImportStatus.SHIFTMASTER_IS_NOTFOUND, ScheManaStatus.SCHEDULE_MANAGEMENT ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS03", GeneralDate.ymd( 2021, 6,  8 ), "Imp#251", ImportStatus.IMPORTABLE, ScheManaStatus.ON_LEAVE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS03", GeneralDate.ymd( 2021, 6, 11 ), "Imp#629", ImportStatus.SCHEDULE_IS_EXISTS, ScheManaStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS03", GeneralDate.ymd( 2021, 6, 13 ), "Imp#610", ImportStatus.EMPLOYEEINFO_IS_INVALID, ScheManaStatus.INVALID_DATA ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng1", GeneralDate.ymd( 2021, 6,  8 ), "Imp#170" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng1", GeneralDate.ymd( 2021, 6, 11 ), "Imp#934" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng1", GeneralDate.ymd( 2021, 6, 12 ), "Imp#610" ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Li04", GeneralDate.ymd( 2021, 6,  6 ), "Imp#352" ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS05", GeneralDate.ymd( 2021, 6, 12 ), "Imp#629", ImportStatus.SCHEDULE_IS_EXISTS, ScheManaStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS06", GeneralDate.ymd( 2021, 6, 10 ), "Imp#251", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS06", GeneralDate.ymd( 2021, 6, 12 ), "Imp#934", ImportStatus.OUT_OF_REFERENCE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6,  7 ), "Imp#610" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6,  8 ), "Imp#934", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6, 10 ), "Imp#352", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6, 13 ), "Imp#046", ImportStatus.OUT_OF_REFERENCE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#WS07", GeneralDate.ymd( 2021, 6, 14 ), "Imp#251", ImportStatus.OUT_OF_REFERENCE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB08", GeneralDate.ymd( 2021, 6, 12 ), "Imp#627", ImportStatus.OUT_OF_REFERENCE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St09", GeneralDate.ymd( 2021, 6,  7 ), "Imp#046" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St09", GeneralDate.ymd( 2021, 6, 10 ), "Imp#251", ImportStatus.SCHEDULE_IS_EXISTS, ScheManaStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#St09", GeneralDate.ymd( 2021, 6, 13 ), "Imp#310", ImportStatus.EMPLOYEE_IS_NOT_ENROLLED, ScheManaStatus.NOT_ENROLLED ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6,  5 ), "Imp#251" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6,  8 ), "Imp#046", ImportStatus.SCHEDULE_IS_EXISTS, ScheManaStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.UNSETTLED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6, 13 ), "Imp#627", ImportStatus.SCHEDULE_IS_COMFIRMED, ScheManaStatus.SCHEDULE_MANAGEMENT, ConfirmedATR.CONFIRMED ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB10", GeneralDate.ymd( 2021, 6, 14 ), "Imp#251", ImportStatus.IMPORTABLE, ScheManaStatus.SCHEDULE_MANAGEMENT ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB11", GeneralDate.ymd( 2021, 6, 10 ), "Imp#170", ImportStatus.SCHEDULE_IS_NOTUSE, ScheManaStatus.DO_NOT_MANAGE_SCHEDULE ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6,  6 ), "Imp#627" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6,  8 ), "Imp#310", ImportStatus.EMPLOYEEINFO_IS_INVALID, ScheManaStatus.INVALID_DATA ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6,  9 ), "Imp#610", ImportStatus.EMPLOYEEINFO_IS_INVALID, ScheManaStatus.INVALID_DATA ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6, 13 ), "Imp#352", ImportStatus.IMPORTABLE, ScheManaStatus.ON_LEAVE ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#MB12", GeneralDate.ymd( 2021, 6, 15 ), "Imp#934", ImportStatus.SHIFTMASTER_IS_ERROR, ScheManaStatus.SCHEDULE_MANAGEMENT ) );

			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng2", GeneralDate.ymd( 2021, 6,  9 ), "Imp#352" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng2", GeneralDate.ymd( 2021, 6, 10 ), "Imp#563" ) );
			add( new ExpectImportFromRawData( empCdIdMap, "Cd#Mng2", GeneralDate.ymd( 2021, 6, 11 ), "Imp#251" ) );

		}};


		/* 修正可能日・社員情報 */
		new Expectations( ScheModifyStartDateService.class ) {{

			// 修正可能開始日を取得する
			ScheModifyStartDateService.getModifyStartDate(require, anyString);
			result = modifiableStartDate;

			// 社員情報を取得する
			@SuppressWarnings("unchecked") val anyCodes = (List<String>)any;
			require.getEmployeeIds(anyCodes);
			result = empCdIdMap;

		}};

		/* 参照可能社員 */
		new Expectations( GetEmpCanReferService.class ) {{
			// 参照可能社員の取得
			GetEmpCanReferService.getAll(require, (GeneralDate)any, anyString);
			result = referableEmployees;
		}};

		/* 予定管理状態 */
		new MockUp<ScheManaStatuTempo>() {
			@Mock ScheManaStatuTempo create(@SuppressWarnings("unused") ScheManaStatuTempo.Require require, String employeeID, GeneralDate date) {
				val importSeed = importSeeds.stream()
						.filter( seed -> seed.getEmployeeId().orElse(new EmployeeId("")).v().equals(employeeID) && seed.getYmd().equals(date) )
						.findFirst().get();
				return Helper.createScheMngStatus( importSeed.getEmployeeId().get(), importSeed.getYmd(), importSeed.getScheMngStatus().get() );
			}
		};

		/* シフトマスタ */
		val shiftMasters = shiftMasterMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
		new Expectations() {{
			// シフトマスタを取得する
			@SuppressWarnings("unchecked") val anyCodes = (List<ShiftMasterImportCode>)any;
			require.getShiftMasters(anyCodes);
			result = shiftMasters;
		}};
		// シフトマスタの状態をチェックする
		new MockUp<WorkInformation>() {
			@Mock public boolean checkNormalCondition(@SuppressWarnings("unused") WorkInformation.Require require) {
				return shiftMasterMap.get(this.getMockInstance());
			}
		};

		/* 勤務予定 */
		val importSeedsByEmpId = importSeeds.stream()
			.filter( seed -> seed.getEmployeeId().isPresent() && seed.isNeedWorkSchedule() )
			.collect(Collectors.groupingBy( seed -> seed.getEmployeeId().get() ));
		orderedEmployeeIds.stream().forEach( employeeId -> {
			importSeedsByEmpId.getOrDefault( employeeId, Collections.emptyList() ).forEach( seed -> {

				val isScheduleExisted = seed.getConfirmedStatus().isPresent();
				new Expectations() {{
					// 勤務予定が登録されているか
					require.isWorkScheduleExisted(seed.getEmployeeId().get(), seed.getYmd());
					result = isScheduleExisted;
				}};

				if ( isScheduleExisted ) {
					new Expectations() {{
						// 勤務予定が確定されているか
						require.isWorkScheduleComfirmed(seed.getEmployeeId().get(), seed.getYmd());
						result = seed.getConfirmedStatus().map( e -> e == ConfirmedATR.CONFIRMED ).orElse(false);
					}};
				}

			} );
		} );


		/** 実行 **/
		// 実行
		val rawDataOfCells = importSeeds.stream().map( ExpectImportFromRawData::getAsCapturedRaw ).collect(Collectors.toList());
		val result = WorkScheduleImportService.importFrom( require, new CapturedRawData( rawDataOfCells, orderedEmployeeCodes ) );


		/** 検証 **/
		// 取込不可日
		assertThat( result.getUnimportableDates() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.filter( seed -> seed.getYmd().before(modifiableStartDate) )
						.map(ExpectImportFromRawData::getYmd)
						.distinct()
						.collect(Collectors.toList())
				);
		// 存在しない社員
		assertThat( result.getUnexistsEmployees() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.filter( seed -> !seed.getEmployeeId().isPresent() )
						.map(ExpectImportFromRawData::getEmployeeCode)
						.distinct()
						.collect(Collectors.toList())
				);
		// 社員の並び順
		assertThat( result.getOrderOfEmployees() ).containsExactlyElementsOf( orderedEmployeeIds );
		// 取り込み結果
		assertThat( result.getResults() )
			.containsExactlyInAnyOrderElementsOf(
					importSeeds.stream()
						.map(ExpectImportFromRawData::getAsExpectedImportResult)
						.flatMap(OptionalUtil::stream)
						.collect(Collectors.toList())
				);

		// 未チェックの取り込み結果が存在しない
		assertThat( result.getResults() )
			.extracting( ImportResultDetail::getStatus )
			.doesNotContain( ImportStatus.UNCHECKED );

	}



	private static class Helper {

		/**
		 * 社員の予定管理状態を作成する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @param status 予定管理状態
		 * @return
		 */
		public static ScheManaStatuTempo createScheMngStatus(EmployeeId employeeId, GeneralDate date, ScheManaStatus status) {
			return new ScheManaStatuTempo( employeeId.v(), date, status, Optional.empty(), Optional.empty() );
		}

		/**
		 * 取り込みコードを指定してシフトマスタを作成する
		 * @param importCode 取り込みコード
		 * @return シフトマスタ(dummy)
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

		private final Optional<ScheManaStatus> scheMngStatus;
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
				,	ImportStatus expectedStatus, ScheManaStatus scheMngStatus
		) {
			this(empCdIdMap, employeeCode, ymd, importCode, expectedStatus, scheMngStatus, null);
		}
		public ExpectImportFromRawData(Map<String, String> empCdIdMap
				,	String employeeCode, GeneralDate ymd, String importCode
				,	ImportStatus expectedStatus, ScheManaStatus scheMngStatus, ConfirmedATR confirmedStatus
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
