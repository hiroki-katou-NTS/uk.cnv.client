package nts.uk.ctx.at.schedule.dom.importschedule;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.employeeworkway.WorkingStatus;

/**
 * Test for ImportStatus
 * @author kumiko_otake
 *
 */
public class ImportStatusTest {

	@Test
	public void test_getters() {
		NtsAssert.invokeGetters(ImportStatus.SCHEDULE_IS_EXISTS);
	}

	/**
	 * Target	: getMessageId
	 */
	@Test
	public void test_getMessageId() {

		/** メッセージIDなし **/
		{
			// 期待値
			@SuppressWarnings("serial")
			val expected = new ArrayList<ImportStatus>() {{
				add( ImportStatus.UNCHECKED );
				add( ImportStatus.IMPORTABLE );
			}};

			// 検証
			expected.stream().forEach( item -> {

				assertThat( item.getMessageId() ).isEmpty();

			} );
		}

		/* メッセージIDあり */
		{
			// 期待値
			@SuppressWarnings("serial")
			val expected = new HashMap<ImportStatus, String>() {{
				put( ImportStatus.OUT_OF_REFERENCE,			"#Msg_2176" );
				put( ImportStatus.EMPLOYEEINFO_IS_INVALID,	"#Msg_2177" );
				put( ImportStatus.EMPLOYEE_IS_NOT_ENROLLED,	"#Msg_2178" );
				put( ImportStatus.SCHEDULE_IS_NOTUSE,		"#Msg_2179" );
				put( ImportStatus.SHIFTMASTER_IS_NOTFOUND,	"#Msg_2180" );
				put( ImportStatus.SHIFTMASTER_IS_ERROR,		"#Msg_2181" );
				put( ImportStatus.SCHEDULE_IS_COMFIRMED,	"#Msg_2135" );
				put( ImportStatus.SCHEDULE_IS_EXISTS,		"#Msg_2136" );
			}};

			// 検証
			expected.entrySet().stream().forEach( item -> {

				assertThat( item.getKey().getMessageId() )
					.isPresent()
					.get().isEqualTo( item.getValue() );

			} );
		}

	}


	/**
	 * Target	: isUnimportable
	 */
	@Test
	public void test_isUnimportable() {

		// 期待値
		@SuppressWarnings("serial")
		val expected = new HashMap<ImportStatus, Boolean>() {{
			// true: 取り込み不可である
			put( ImportStatus.UNCHECKED,				true );
			put( ImportStatus.OUT_OF_REFERENCE,			true );
			put( ImportStatus.EMPLOYEEINFO_IS_INVALID,	true );
			put( ImportStatus.EMPLOYEE_IS_NOT_ENROLLED,	true );
			put( ImportStatus.SCHEDULE_IS_NOTUSE,		true );
			put( ImportStatus.SHIFTMASTER_IS_NOTFOUND,	true );
			put( ImportStatus.SHIFTMASTER_IS_ERROR,		true );
			put( ImportStatus.SCHEDULE_IS_COMFIRMED,	true );
			// false: 取り込み不可ではない
			put( ImportStatus.IMPORTABLE,				false );
			put( ImportStatus.SCHEDULE_IS_EXISTS,		false );
		}};

		// 検証
		Stream.of( ImportStatus.values() ).forEach( status -> {

			assertThat( status.isUnimportable() ).isEqualTo( expected.get(status) );

		} );


	}


	/**
	 * Target	: from( WorkingStatus )
	 */
	@Test
	public void test_from_WorkingStatus() {

		// 期待値※未チェック以外
		@SuppressWarnings("serial")
		val expected = new HashMap<WorkingStatus, ImportStatus>() {{
			put( WorkingStatus.NOT_ENROLLED,			ImportStatus.EMPLOYEE_IS_NOT_ENROLLED );
			put( WorkingStatus.INVALID_DATA,			ImportStatus.EMPLOYEEINFO_IS_INVALID );
			put( WorkingStatus.DO_NOT_MANAGE_SCHEDULE,	ImportStatus.SCHEDULE_IS_NOTUSE );
		}};

		// 検証
		Stream.of( WorkingStatus.values() ).forEach( status -> {

			assertThat( ImportStatus.from( status ) )
				.isEqualTo( expected.getOrDefault( status, ImportStatus.UNCHECKED ) );

		} );

	}

}
