package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.GetAllEmpWhoBelongWorkplaceGroupService.Require;

@RunWith(JMockit.class)
public class GetAllEmpWhoBelongWorkplaceGroupServiceTest {

	@Injectable
	private Require require;



	/**
	 * Method	: getAllEmp
	 * Pattern	: 職場グループに所属する職場が存在しない
	 */
	@Test
	public void test_getAllEmp_AffiliatedWorkPlaceIsEmpty() {

		new Expectations() {{
			// 職場グループに所属する職場を取得する
			require.getWorkplaceBelongsWorkplaceGroup( (String)any );
		}};


		val result = GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, DatePeriod.years( 1, GeneralDate.today() ), "WorkplaceGroupId");


		assertThat( result ).isEmpty();

	}


	/**
	 * Method	: getAllEmp
	 * Pattern	: 職場に所属している社員が存在しない
	 */
	@Test
	public void test_getAllEmp_AffiliatedEmployeeIsEmpty() {

		val workplaces = Arrays.asList(
				"WorkPlaceId#01"
			,	"WorkPlaceId#02"
			,	"WorkPlaceId#03"
		);

		new Expectations() {{
			// 職場グループに所属する職場を取得する
			require.getWorkplaceBelongsWorkplaceGroup( (String)any );
			result = workplaces;
			// 職場の所属社員を取得する
			require.getEmployeesWhoBelongWorkplace( (String)any, (DatePeriod)any );
		}};


		val result = GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, DatePeriod.years( 1, GeneralDate.today() ), "WorkplaceGroupId");


		assertThat( result ).isEmpty();

	}


	@Test
	public void test_getAllEmp_() {

		val map = new HashMap<String, List<EmployeeInfoData>>() { private static final long serialVersionUID = 1L; {

			// 職場①
			put( "WorkPlaceId#01",
					Arrays.asList(
							Helper.createEmployeeInfoData("WKP#01: Emp",  2)
						,	Helper.createEmployeeInfoData("WKP#01: Emp",  5)
						,	Helper.createEmployeeInfoData("WKP#01: Emp",  6)
					) );
			// 職場③
			put( "WorkPlaceId#03", Collections.emptyList() );
			// 職場⑧
			put( "WorkPlaceId#08",
					Arrays.asList(
							Helper.createEmployeeInfoData("WKP#08: Emp", 42)
					) );
			// 職場⑫
			put( "WorkPlaceId#12",
					Arrays.asList(
							Helper.createEmployeeInfoData("WKP#12: Emp", 11)
						,	Helper.createEmployeeInfoData("WKP#12: Emp", 29)
						,	Helper.createEmployeeInfoData("WKP#12: Emp", 35)
						,	Helper.createEmployeeInfoData("WKP#12: Emp",  6)
					) );

		}};

		val workplaceGroup = "WorkplaceGroupId";
		val workplaces = map.entrySet().stream().map( Map.Entry::getKey ).collect(Collectors.toList());


		new Expectations() {{
			// 職場グループに所属する職場を取得する
			require.getWorkplaceBelongsWorkplaceGroup( (String)any );
			result = workplaces;

			// 職場の所属社員を取得する
			workplaces.forEach( workplace -> {
				require.getEmployeesWhoBelongWorkplace( workplace, (DatePeriod)any );
				result = map.get( workplace );
			} );

		}};


		val result = GetAllEmpWhoBelongWorkplaceGroupService.getAllEmp(require, DatePeriod.years( 1, GeneralDate.today() ), workplaceGroup);


		assertThat( result )
			.extracting(
					e -> e.getEmployeeID()				// 社員名
				,	e -> e.getEmployeeCode().get().v()	// 社員コード
				,	e -> e.getBusinessName().get()		// ビジネスネーム
				,	e -> e.getWorkplaceID()				// 職場ID
				,	e -> e.getWorkplaceGroupID().get()	// 職場グループID
			).containsExactlyInAnyOrderElementsOf(
				map.entrySet().stream()
					.flatMap( entry -> entry.getValue().stream().map( value -> tuple(
							value.getEmpId()			// 社員名
						,	value.getEmpCd()			// 社員コード
						,	value.getEmpName()			// ビジネスネーム
						,	entry.getKey()				// 職場ID
						,	workplaceGroup				// 職場グループID
					) )).collect(Collectors.toList())
			);

	}


	private static class Helper {

		public static EmployeeInfoData createEmployeeInfoData(String prefix, int number) {

			return new EmployeeInfoData(
								prefix+"ID#"+String.format("%2s", number)		// ID
							,	prefix+"Cd#"+String.format("%2s", number)		// コード
							,	prefix+" Name "+String.format("%2s", number)	// 社員名
						);

		}

	}

}

