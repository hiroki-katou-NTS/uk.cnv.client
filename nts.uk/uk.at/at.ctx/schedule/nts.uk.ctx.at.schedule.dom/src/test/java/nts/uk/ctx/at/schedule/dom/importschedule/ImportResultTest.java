package nts.uk.ctx.at.schedule.dom.importschedule;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import lombok.val;
import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;

/**
 * Test for ImportResult
 * @author kumiko_otake
 *
 */
public class ImportResultTest {

	@Test
	public void test_getters() {
		val instance = new ImportResult(
						Collections.emptyList()
					,	Collections.emptyList()
					,	Collections.emptyList()
					,	Collections.emptyList()
				);
		NtsAssert.invokeGetters(instance);
	}



	/**
	 * Target	: getOrderOfEmployees
	 */
	@Test
	public void test_getOrderOfEmployees(
				@Injectable List<ImportResultDetail> details
			,	@Injectable List<GeneralDate> unmodifiableDates
			,	@Injectable List<String> unexistEmployees
	) {

		// 社員の並び順
		val orderedEmployeeIds = Arrays.asList(
				new EmployeeId( "EmpId:St#09" )
			,	new EmployeeId( "EmpId:MB#10" )
			,	new EmployeeId( "EmpId:MB#11" )
			,	new EmployeeId( "EmpId:MB#12" )

			,	new EmployeeId( "EmpId:Li#04" )
			,	new EmployeeId( "EmpId:WS#05" )
			,	new EmployeeId( "EmpId:WS#06" )
			,	new EmployeeId( "EmpId:WS#07" )
			,	new EmployeeId( "EmpId:MB#08" )

			,	new EmployeeId( "EmpId:WS#01" )
			,	new EmployeeId( "EmpId:St#02" )
			,	new EmployeeId( "EmpId:WS#03" )
		);


		// 実行
		val result = new ImportResult( details, unmodifiableDates, unexistEmployees, orderedEmployeeIds ).getOrderOfEmployees();


		/* 検証 */
		// 変更不可
		assertThatThrownBy( () -> result.add(new EmployeeId("test")) )
			.isInstanceOf( UnsupportedOperationException.class );

		// 内容
		assertThat( result )
			.isNotSameAs( orderedEmployeeIds )
			.containsExactlyElementsOf( orderedEmployeeIds );

		// 並び順確認
		Collections.sort( orderedEmployeeIds, Comparator.comparing( EmployeeId::v ) );
		assertThat( orderedEmployeeIds ).first().isEqualTo( new EmployeeId( "EmpId:Li#04" ) );
		assertThat( result ).first().isEqualTo( new EmployeeId( "EmpId:St#09" ) );

	}


	/**
	 * Target	: getImportableDates
	 */
	@Test
	public void test_getImportableDates() {

		// 実行
		val result = new ImportResult(
				Arrays.asList(
						ImportResultHelper.createDetail( "EmpId#1", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#1", ImportStatus.OUT_OF_REFERENCE )
					,	ImportResultHelper.createDetail( "EmpId#1", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.SCHEDULE_IS_COMFIRMED )
					,	ImportResultHelper.createDetail( "EmpId#1", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#1", ImportStatus.SCHEDULE_IS_EXISTS )

					,	ImportResultHelper.createDetail( "EmpId#2", GeneralDate.ymd( 2021, 5, 13 ), "ImpCd#5", ImportStatus.EMPLOYEEINFO_IS_INVALID )
					,	ImportResultHelper.createDetail( "EmpId#2", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#5", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "EmpId#2", GeneralDate.ymd( 2021, 5, 27 ), "ImpCd#9", ImportStatus.SHIFTMASTER_IS_ERROR )
					,	ImportResultHelper.createDetail( "EmpId#2", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#3", ImportStatus.IMPORTABLE )

					,	ImportResultHelper.createDetail( "EmpId#3", GeneralDate.ymd( 2021, 5, 14 ), "ImpCd#3", ImportStatus.SCHEDULE_IS_EXISTS )
					,	ImportResultHelper.createDetail( "EmpId#3", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#3", ImportStatus.SCHEDULE_IS_COMFIRMED )
					,	ImportResultHelper.createDetail( "EmpId#3", GeneralDate.ymd( 2021, 5, 17 ), "ImpCd#7", ImportStatus.SHIFTMASTER_IS_NOTFOUND )
					,	ImportResultHelper.createDetail( "EmpId#3", GeneralDate.ymd( 2021, 5, 27 ), "ImpCd#2", ImportStatus.SCHEDULE_IS_NOTUSE )
					,	ImportResultHelper.createDetail( "EmpId#3", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#3", ImportStatus.SCHEDULE_IS_NOTUSE )
				)
			,	Arrays.asList( GeneralDate.ymd( 2021, 5, 31 ), GeneralDate.ymd( 2021, 5, 27 ) )
			,	Collections.emptyList()
			,	Collections.emptyList()
		).getImportableDates();


		// 検証
		assertThat( result ).containsExactly(
							GeneralDate.ymd( 2021, 5, 13 )
						,	GeneralDate.ymd( 2021, 5, 14 )
						,	GeneralDate.ymd( 2021, 5, 15 )
						,	GeneralDate.ymd( 2021, 5, 17 )
						,	GeneralDate.ymd( 2021, 5, 22 )
						,	GeneralDate.ymd( 2021, 5, 27 )
						,	GeneralDate.ymd( 2021, 5, 31 )
					);

	}


	/**
	 * Target	: getImportableEmployees
	 */
	@Test
	public void test_getImportableEmployees() {

		// 実行
		val result = new ImportResult(
				Arrays.asList(
						ImportResultHelper.createDetail( "Id#0101", GeneralDate.ymd( 2021, 6, 16 ), "Imp#AAA", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0104", GeneralDate.ymd( 2021, 6, 16 ), "Imp#XYZ", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0100", GeneralDate.ymd( 2021, 6, 18 ), "Imp#QvQ", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0103", GeneralDate.ymd( 2021, 6, 20 ), "Imp#DDD", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0205", GeneralDate.ymd( 2021, 6, 21 ), "Imp#AAA", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0302", GeneralDate.ymd( 2021, 6, 21 ), "Imp#EEE", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0101", GeneralDate.ymd( 2021, 6, 21 ), "Imp#ABC", ImportStatus.UNCHECKED )
					,	ImportResultHelper.createDetail( "Id#0205", GeneralDate.ymd( 2021, 7,  1 ), "Imp#CCC", ImportStatus.UNCHECKED )
				)
			,	Collections.emptyList()
			,	Collections.emptyList()
			,	Stream.of(
						"Id#0301", "Id#0302", "Id#0303"
					,	"Id#0201", "Id#0202", "Id#0203", "Id#0204", "Id#0205"
					,	"Id#0101", "Id#0102", "Id#0103", "Id#0104"
				).map(EmployeeId::new).collect(Collectors.toList())
		).getImportableEmployees();


		// 検証
		/* ※通常は発生しない条件を含む※
		 * - 『社員の並び順』に存在するが『取込内容』には存在しないID⇒含まれない
		 * - 『社員の並び順』に存在しないID⇒先頭
		 */
		assertThat( result ).containsExactly(
							new EmployeeId("Id#0100")
						,	new EmployeeId("Id#0302")
						,	new EmployeeId("Id#0205")
						,	new EmployeeId("Id#0101")
						,	new EmployeeId("Id#0103")
						,	new EmployeeId("Id#0104")
					);

	}


	/**
	 * Target	: existsUncheckedResults
	 */
	@Test
	public void test_existsUncheckedResults(
			@Injectable List<GeneralDate> unimportableDates
		,	@Injectable List<String> unexistsEmployees
		,	@Injectable List<EmployeeId> orderOfEmployees
	) {

		/* 取り込み結果 */
		// チェック済み
		val checked = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#08", GeneralDate.ymd( 2021, 5, 13 ), "ImpCd#5", ImportStatus.IMPORTABLE )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.SCHEDULE_IS_COMFIRMED )
		);
		// 未チェック
		val unchecked = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#1", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#03", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5,  1 ), "ImpCd#1", ImportStatus.UNCHECKED )
		);


		/* 取り込み結果: なし */
		{
			// 作成
			val instance = new ImportResult(
						Collections.emptyList()
					,	unimportableDates, unexistsEmployees, orderOfEmployees
				);
			// 実行＆検証
			assertThat( instance.existsUncheckedResults() ).isFalse();
		}

		/* 取り込み結果: チェック済みのみ(未チェックなし) */
		{
			// 作成
			val instance = new ImportResult(
					checked
				,	unimportableDates, unexistsEmployees, orderOfEmployees
			);
			// 実行＆検証
			assertThat( instance.existsUncheckedResults() ).isFalse();
		}

		/* 取り込み結果: 未チェック＋チェック済み */
		{
			// 作成
			val instance = new ImportResult(
					Stream.of( checked, unchecked ).flatMap(Collection::stream).collect(Collectors.toList())
				,	unimportableDates, unexistsEmployees, orderOfEmployees
			);
			// 実行＆検証
			assertThat( instance.existsUncheckedResults() ).isTrue();
		}

	}


	/**
	 * Target	: getUncheckedResults
	 */
	@Test
	public void test_getUncheckedResults() {

		/* 取り込み結果 */
		// チェック済み
		val checked = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#08", GeneralDate.ymd( 2021, 5, 13 ), "ImpCd#5", ImportStatus.IMPORTABLE )
			,	ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#5", ImportStatus.SCHEDULE_IS_NOTUSE )
			,	ImportResultHelper.createDetail( "EmpId#01", GeneralDate.ymd( 2021, 5, 27 ), "ImpCd#9", ImportStatus.OUT_OF_REFERENCE )
			,	ImportResultHelper.createDetail( "EmpId#10", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#3", ImportStatus.SHIFTMASTER_IS_ERROR )
			,	ImportResultHelper.createDetail( "EmpId#08", GeneralDate.ymd( 2021, 5, 14 ), "ImpCd#3", ImportStatus.SCHEDULE_IS_COMFIRMED )
			,	ImportResultHelper.createDetail( "EmpId#01", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#3", ImportStatus.EMPLOYEE_IS_NOT_ENROLLED )
			,	ImportResultHelper.createDetail( "EmpId#07", GeneralDate.ymd( 2021, 5, 17 ), "ImpCd#7", ImportStatus.EMPLOYEEINFO_IS_INVALID )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5, 27 ), "ImpCd#2", ImportStatus.SCHEDULE_IS_EXISTS )
			,	ImportResultHelper.createDetail( "EmpId#03", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#3", ImportStatus.SHIFTMASTER_IS_NOTFOUND )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.SCHEDULE_IS_COMFIRMED )
		);
		// 未チェック
		val unchecked = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#1", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#03", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#1", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5,  1 ), "ImpCd#1", ImportStatus.UNCHECKED )
		);
		// 未チェック＋チェック済み
		val details = Stream.of( checked, unchecked ).flatMap(Collection::stream).collect(Collectors.toList());
		Collections.shuffle(details, ThreadLocalRandom.current());	// ランダムに並び替え


		/* 作成 */
		val instance = new ImportResult(
					details
				,	Arrays.asList( GeneralDate.ymd( 2021, 5, 31 ), GeneralDate.ymd( 2021, 5, 27 ) )
				,	Arrays.asList( "EmpId#2" )
				,	Collections.emptyList()
			);
		assertThat( instance.getResults() )
			.hasSize( checked.size() + unchecked.size() )
			.containsAll( checked )
			.containsAll( unchecked );


		/* 実行＆検証 */
		assertThat( instance.getUncheckedResults() )
			.containsExactlyInAnyOrderElementsOf( unchecked );

	}


	/**
	 * Target	: updateUncheckedResults
	 */
	@Test
	public void test_updateUncheckedResults() {

		/* 取り込み結果(作成時) */
		// チェック済み
		val checked = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#08", GeneralDate.ymd( 2021, 5, 13 ), "ImpCd#5", ImportStatus.IMPORTABLE )
			,	ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#5", ImportStatus.SCHEDULE_IS_NOTUSE )
			,	ImportResultHelper.createDetail( "EmpId#01", GeneralDate.ymd( 2021, 5, 27 ), "ImpCd#9", ImportStatus.OUT_OF_REFERENCE )
			,	ImportResultHelper.createDetail( "EmpId#10", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#3", ImportStatus.SHIFTMASTER_IS_ERROR )
			,	ImportResultHelper.createDetail( "EmpId#08", GeneralDate.ymd( 2021, 5, 14 ), "ImpCd#3", ImportStatus.SCHEDULE_IS_COMFIRMED )
			,	ImportResultHelper.createDetail( "EmpId#01", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#3", ImportStatus.EMPLOYEE_IS_NOT_ENROLLED )
			,	ImportResultHelper.createDetail( "EmpId#07", GeneralDate.ymd( 2021, 5, 17 ), "ImpCd#7", ImportStatus.EMPLOYEEINFO_IS_INVALID )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5, 27 ), "ImpCd#2", ImportStatus.SCHEDULE_IS_EXISTS )
			,	ImportResultHelper.createDetail( "EmpId#03", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#3", ImportStatus.SHIFTMASTER_IS_NOTFOUND )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.SCHEDULE_IS_COMFIRMED )
		);
		// 未チェック
		val unchecked = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 15 ), "ImpCd#1", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#03", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#0", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#05", GeneralDate.ymd( 2021, 5, 31 ), "ImpCd#1", ImportStatus.UNCHECKED )
			,	ImportResultHelper.createDetail( "EmpId#02", GeneralDate.ymd( 2021, 5,  1 ), "ImpCd#1", ImportStatus.UNCHECKED )
		);
		// チェック済み＋未チェック
		val creating = Stream.of( checked, unchecked ).flatMap(Collection::stream).collect(Collectors.toList());
		Collections.shuffle(creating, ThreadLocalRandom.current());	// ランダムに並び替え


		/* 作成 */
		val instance = new ImportResult(
					creating
				,	Arrays.asList( GeneralDate.ymd( 2021, 5, 31 ), GeneralDate.ymd( 2021, 5, 27 ) )
				,	Arrays.asList( "EmpId#2" )
				,	Collections.emptyList()
			);

		assertThat( instance.getResults() )
			.hasSize( checked.size() + unchecked.size() )
			.containsAll( checked )
			.containsAll( unchecked );



		/* 入れ替え */
		// 入れ替え対象
		val swaping = Arrays.asList(
				ImportResultHelper.createDetail( "EmpId#08", GeneralDate.ymd( 2021, 5, 22 ), "ImpCd#7", ImportStatus.SHIFTMASTER_IS_ERROR )
			,	ImportResultHelper.createDetail( "EmpId#07", GeneralDate.ymd( 2021, 5, 17 ), "ImpCd#4", ImportStatus.SCHEDULE_IS_NOTUSE )
			,	ImportResultHelper.createDetail( "EmpId#04", GeneralDate.ymd( 2021, 5,  9 ), "ImpCd#9", ImportStatus.SCHEDULE_IS_NOTUSE )
			,	ImportResultHelper.createDetail( "EmpId#01", GeneralDate.ymd( 2021, 5,  4 ), "ImpCd#9", ImportStatus.IMPORTABLE )
		);

		// 実行
		val result = instance.updateUncheckedResults( swaping );

		// 検証
		assertThat( result.getResults() )
			.hasSize( checked.size() + swaping.size() )
			.containsAll( checked )
			.containsAll( swaping );

	}

}
