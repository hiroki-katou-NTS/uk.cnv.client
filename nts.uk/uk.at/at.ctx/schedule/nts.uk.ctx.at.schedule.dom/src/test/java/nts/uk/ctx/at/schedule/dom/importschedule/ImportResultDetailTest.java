package nts.uk.ctx.at.schedule.dom.importschedule;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * Test for ImportResultDetail
 * @author kumiko_otake
 */
public class ImportResultDetailTest {

	@Test
	public void test_getters() {
		val instance = ImportResultDetail.createNew(new EmployeeId("EmpId"), GeneralDate.today(), new ShiftMasterImportCode("ImpCd"));
		NtsAssert.invokeGetters(instance);
	}


	/**
	 * Target	: createNew
	 */
	@Test
	public void test_createNew() {

		// 実行
		val result = ImportResultDetail.createNew(
									new EmployeeId("EmpId#1")
								,	GeneralDate.today()
								,	new ShiftMasterImportCode("ImportCode#111")
							);


		// 検証
		assertThat( result.getEmployeeId() ).isEqualTo( new EmployeeId("EmpId#1") );
		assertThat( result.getYmd() ).isEqualTo( GeneralDate.today() );
		assertThat( result.getImportCode() ).isEqualTo( new ShiftMasterImportCode("ImportCode#111") );

		assertThat( result.getStatus() ).isEqualTo( ImportStatus.UNCHECKED );

	}

	/**
	 * Target	: updateStatus
	 */
	@Test
	public void test_updateStatus() {

		/* 新規作成 */
		// 実行
		val instance = ImportResultDetail.createNew(
									new EmployeeId("EmpId#2")
								,	GeneralDate.ymd( 2021, 05, 20 )
								,	new ShiftMasterImportCode("ImportCode#000")
							);

		// 検証
		assertThat( instance.getEmployeeId() ).isEqualTo( new EmployeeId("EmpId#2") );
		assertThat( instance.getYmd() ).isEqualTo( GeneralDate.ymd( 2021, 05, 20 ) );
		assertThat( instance.getImportCode() ).isEqualTo( new ShiftMasterImportCode("ImportCode#000") );

		assertThat( instance.getStatus() ).isEqualTo( ImportStatus.UNCHECKED );



		/* 更新 */
		Stream.of( ImportStatus.values() ).forEach( status -> {

			// 実行
			val result = instance.updateStatus( status );

			// 検証
			assertThat( result.getEmployeeId() ).isEqualTo( instance.getEmployeeId() );
			assertThat( result.getYmd() ).isEqualTo( instance.getYmd() );
			assertThat( result.getImportCode() ).isEqualTo( instance.getImportCode() );

			assertThat( result.getStatus() ).isEqualTo( status );

		} );

	}

}
