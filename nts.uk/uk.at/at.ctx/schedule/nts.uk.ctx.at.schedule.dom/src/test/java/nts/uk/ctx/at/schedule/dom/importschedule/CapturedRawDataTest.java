package nts.uk.ctx.at.schedule.dom.importschedule;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import lombok.val;
import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * Test for CapturedRawData
 * @author kumiko_otake
 *
 */
public class CapturedRawDataTest {

	@Test
	public void test_getters() {
		val instance = new CapturedRawData(Collections.emptyList(), Collections.emptyList());
		NtsAssert.invokeGetters(instance);
	}



	/**
	 * Target	: getEmployeeCodes
	 */
	@Test
	public void test_getEmployeeCodes(@Injectable List<CapturedRawDataOfCell> results) {

		// 社員の並び順
		val orderedEmployeeCds = Arrays.asList(
					"EmpCd=WS-01"
				,	"EmpCd=St-02"
				,	"EmpCd=WS-03"

				,	"EmpCd=Li-04"
				,	"EmpCd=WS-05"
				,	"EmpCd=WS-06"
				,	"EmpCd=WS-07"
				,	"EmpCd=MB-08"

				,	"EmpCd=St-09"
				,	"EmpCd=MB-10"
				,	"EmpCd=MB-11"
				,	"EmpCd=MB-12"
		);


		// 実行
		val result = new CapturedRawData( results, orderedEmployeeCds ).getEmployeeCodes();


		/* 検証 */
		// 変更不可
		assertThatThrownBy( () -> result.add("test") )
			.isInstanceOf( UnsupportedOperationException.class );

		// 内容
		assertThat( result )
			.isNotSameAs( orderedEmployeeCds )
			.containsExactlyElementsOf( orderedEmployeeCds );

		// 並び順確認
		Collections.sort( orderedEmployeeCds );
		assertThat( orderedEmployeeCds ).first().isEqualTo( "EmpCd=Li-04" );
		assertThat( result ).first().isEqualTo( "EmpCd=WS-01" );

	}


	/**
	 * Target	: getYmdList
	 */
	@Test
	public void test_getYmdList() {

		// 実行
		val result = new CapturedRawData(
			Arrays.asList(
					new CapturedRawDataOfCell( "EmpCd#1", GeneralDate.ymd( 2021, 5, 15 ), new ShiftMasterImportCode("ImpCd#1") )
				,	new CapturedRawDataOfCell( "EmpCd#1", GeneralDate.ymd( 2021, 5, 22 ), new ShiftMasterImportCode("ImpCd#0") )
				,	new CapturedRawDataOfCell( "EmpCd#1", GeneralDate.ymd( 2021, 5, 31 ), new ShiftMasterImportCode("ImpCd#1") )

				,	new CapturedRawDataOfCell( "EmpCd#2", GeneralDate.ymd( 2021, 5, 13 ), new ShiftMasterImportCode("ImpCd#5") )
				,	new CapturedRawDataOfCell( "EmpCd#2", GeneralDate.ymd( 2021, 5, 15 ), new ShiftMasterImportCode("ImpCd#5") )
				,	new CapturedRawDataOfCell( "EmpCd#2", GeneralDate.ymd( 2021, 5, 27 ), new ShiftMasterImportCode("ImpCd#9") )
				,	new CapturedRawDataOfCell( "EmpCd#2", GeneralDate.ymd( 2021, 5, 31 ), new ShiftMasterImportCode("ImpCd#3") )

				,	new CapturedRawDataOfCell( "EmpCd#3", GeneralDate.ymd( 2021, 5, 14 ), new ShiftMasterImportCode("ImpCd#3") )
				,	new CapturedRawDataOfCell( "EmpCd#3", GeneralDate.ymd( 2021, 5, 15 ), new ShiftMasterImportCode("ImpCd#3") )
				,	new CapturedRawDataOfCell( "EmpCd#3", GeneralDate.ymd( 2021, 5, 17 ), new ShiftMasterImportCode("ImpCd#7") )
				,	new CapturedRawDataOfCell( "EmpCd#3", GeneralDate.ymd( 2021, 5, 27 ), new ShiftMasterImportCode("ImpCd#2") )
				,	new CapturedRawDataOfCell( "EmpCd#3", GeneralDate.ymd( 2021, 5, 31 ), new ShiftMasterImportCode("ImpCd#3") )
			)
			,	Arrays.asList( "EmpCd#3", "EmpCd#1", "EmpCd#0", "EmpCd#2" )
		).getYmdList();


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

}
