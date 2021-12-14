package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.i18n.I18NText;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor.Require;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;

@RunWith(JMockit.class)
public class TargetOrgIdenInforTest {

	@Injectable
	private Require require;

	/**
	 * if WORKPLACE
	 */
	@Test
	public void testTargetOrgIdenInfor_1() {
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace("workplaceId");
		assertThat(targetOrgIdenInfor.getUnit()).isEqualTo(TargetOrganizationUnit.WORKPLACE);
		assertThat(targetOrgIdenInfor.getWorkplaceId().isPresent()).isTrue();
		assertThat(targetOrgIdenInfor.getWorkplaceGroupId().isPresent()).isFalse();
	}
	/**
	 * if WORKPLACE_GROUP
	 */
	@Test
	public void testTargetOrgIdenInfor_2() {
		TargetOrgIdenInfor targetOrgIdenInfor =TargetOrgIdenInfor.creatIdentifiWorkplaceGroup("workplaceGroupId");
		assertThat(targetOrgIdenInfor.getUnit()).isEqualTo(TargetOrganizationUnit.WORKPLACE_GROUP);
		assertThat(targetOrgIdenInfor.getWorkplaceId().isPresent()).isFalse();
		assertThat(targetOrgIdenInfor.getWorkplaceGroupId().isPresent()).isTrue();
	}

	@Test
	public void getters() {
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace("workplaceId");
		NtsAssert.invokeGetters(targetOrgIdenInfor);
	}

	@Test
	public void testCreatIdentifiWorkplaceGroup() {
		String workplaceGroupId = "workplaceGroupId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(workplaceGroupId);
		assertThat(targetOrgIdenInfor.getUnit()).isEqualTo(TargetOrganizationUnit.WORKPLACE_GROUP);
		assertThat(targetOrgIdenInfor.getWorkplaceId().isPresent()).isFalse();
		assertThat(targetOrgIdenInfor.getWorkplaceGroupId().get()).isEqualTo(workplaceGroupId);
	}

	@Test
	public void testCreatIdentifiWorkplace() {
		String workplaceId = "workplaceId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(workplaceId);
		assertThat(targetOrgIdenInfor.getUnit()).isEqualTo(TargetOrganizationUnit.WORKPLACE);
		assertThat(targetOrgIdenInfor.getWorkplaceId().get()).isEqualTo(workplaceId);
		assertThat(targetOrgIdenInfor.getWorkplaceGroupId().isPresent()).isFalse();
	}

	/**
	 * WORKPLACE_GROUP
	 * require.職場グループIDを指定して職場グループを取得する( list: @職場グループID ) is empty
	 */
	@Test
	public void testGetDisplayInfor() {
		String workplaceGroupId = "workplaceGroupId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(workplaceGroupId);
		GeneralDate referenceDate = GeneralDate.today();

		new Expectations() {
			{
				require.getSpecifyingWorkplaceGroupId(Arrays.asList(workplaceGroupId));

			}
		};
		NtsAssert.businessException("Msg_37",
				() -> { targetOrgIdenInfor.getDisplayInfor(require, referenceDate); });

	}

	/**
	 * WORKPLACE_GROUP
	 * require.職場グループIDを指定して職場グループを取得する( list: @職場グループID ) is not empty
	 */
	@Test
	public void testGetDisplayInfor_1() {
		String workplaceGroupId = "workplaceGroupId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(workplaceGroupId);
		GeneralDate referenceDate = GeneralDate.today();
		WorkplaceGroupImport workplaceGroupImport = new WorkplaceGroupImport(workplaceGroupId, "workplaceGroupCode", "workplaceGroupName", 1);

		new Expectations() {
			{
				require.getSpecifyingWorkplaceGroupId(Arrays.asList(workplaceGroupId));
				result = Arrays.asList(workplaceGroupImport);

			}
		};
		new MockUp<I18NText>() {
			@Mock
			public String getText(String resourceId, String... params) {
				return "Com_WorkplaceGroup";
			}
		};
		DisplayInfoOrganization displayInfoOrganization = targetOrgIdenInfor.getDisplayInfor(require, referenceDate);
		assertThat(displayInfoOrganization.getDesignation()).isEqualTo("Com_WorkplaceGroup");
		assertThat(displayInfoOrganization.getCode()).isEqualTo(workplaceGroupImport.getWorkplaceGroupCode());
		assertThat(displayInfoOrganization.getName()).isEqualTo(workplaceGroupImport.getWorkplaceGroupName());
		assertThat(displayInfoOrganization.getDisplayName()).isEqualTo(workplaceGroupImport.getWorkplaceGroupName());
		assertThat(displayInfoOrganization.getGenericTerm()).isEqualTo(workplaceGroupImport.getWorkplaceGroupName());
	}

	/**
	 * WORKPLACE
	 * require.運用している職場をすべて取得する( list: @職場ID, 基準日 ) is empty
	 */
	@Test
	public void testGetDisplayInfor_2() {
		String workplaceId = "workplaceId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(workplaceId);
		GeneralDate referenceDate = GeneralDate.today();

		new Expectations() {
			{
				require.getWorkplaceInforFromWkpIds(Arrays.asList(workplaceId), referenceDate);

			}
		};
		NtsAssert.businessException("Msg_37",
				() -> { targetOrgIdenInfor.getDisplayInfor(require, referenceDate); });

	}
	/**
	 * WORKPLACE
	 * require.運用している職場をすべて取得する( list: @職場ID, 基準日 ) not empty
	 */
	@Test
	public void testGetDisplayInfor_3() {
		String workplaceId = "workplaceId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(workplaceId);
		GeneralDate referenceDate = GeneralDate.today();
		WorkplaceInfo workplaceInfo = new WorkplaceInfo(workplaceId, Optional.of("workplaceCd"),
				Optional.of("workplaceName"), Optional.of("outsideWkpCd"), Optional.of("wkpGenericName"),
				Optional.of("wkpDisplayName"), Optional.of("tierCd"));

		new Expectations() {
			{
				require.getWorkplaceInforFromWkpIds(Arrays.asList(workplaceId), referenceDate);
				result = Arrays.asList(workplaceInfo);

			}
		};
		new MockUp<I18NText>() {
			@Mock
			public String getText(String resourceId, String... params) {
				return "Com_Workplace";
			}
		};
		DisplayInfoOrganization displayInfoOrganization = targetOrgIdenInfor.getDisplayInfor(require, referenceDate);
		assertThat(displayInfoOrganization.getDesignation()).isEqualTo("Com_Workplace");
		assertThat(displayInfoOrganization.getCode()).isEqualTo(workplaceInfo.getWorkplaceCd().get());
		assertThat(displayInfoOrganization.getName()).isEqualTo(workplaceInfo.getWorkplaceName().get());
		assertThat(displayInfoOrganization.getDisplayName()).isEqualTo(workplaceInfo.getWkpDisplayName().get());
		assertThat(displayInfoOrganization.getGenericTerm()).isEqualTo(workplaceInfo.getWkpGenericName().get());
	}

	/**
	 * WORKPLACE
	 */
	@Test
	public void testGetWorkplaceBelongsOrganization() {
		String workplaceId = "workplaceId";
		TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(workplaceId);

		List<String> listData = targetOrgIdenInfor.getWorkplaceBelongsOrganization(require);
		assertThat(listData)
		.extracting(d->d)
		.containsExactly( "workplaceId");
	}
	/**
	 * WORKPLACE_GROUP
	 * require.職場グループに属する職場を取得する( @会社ID, @職場グループID ) is empty
	 */
	@Test
	public void testGetWorkplaceBelongsOrganization_1() {
		TargetOrganizationUnit unit = TargetOrganizationUnit.WORKPLACE_GROUP;
		String workplaceGroupId = "workplaceGroupId";
		TargetOrgIdenInfor targetOrgIdenInfor = new TargetOrgIdenInfor(unit, Optional.empty(), Optional.of(workplaceGroupId));

		new Expectations() {
			{
				require.getWKPID(workplaceGroupId);

			}
		};
		List<String> listData = targetOrgIdenInfor.getWorkplaceBelongsOrganization(require);
		assertThat(listData.isEmpty()).isTrue();

	}
	/**
	 * WORKPLACE_GROUP
	 * require.職場グループに属する職場を取得する( @会社ID, @職場グループID ) is not empty
	 */
	@Test
	public void testGetWorkplaceBelongsOrganization_2() {
		TargetOrganizationUnit unit = TargetOrganizationUnit.WORKPLACE_GROUP;
		String workplaceGroupId = "workplaceGroupId";
		TargetOrgIdenInfor targetOrgIdenInfor = new TargetOrgIdenInfor(unit, Optional.empty(), Optional.of(workplaceGroupId));
		List<String> listResult = Arrays.asList("wpk1","wpk2");
		new Expectations() {
			{
				require.getWKPID(workplaceGroupId);
				result = listResult;

			}
		};
		List<String> listData = targetOrgIdenInfor.getWorkplaceBelongsOrganization(require);
		assertThat( listData ).containsExactlyElementsOf( listResult );
		assertThat( listData ).containsExactlyInAnyOrderElementsOf( listResult );

	}



}

