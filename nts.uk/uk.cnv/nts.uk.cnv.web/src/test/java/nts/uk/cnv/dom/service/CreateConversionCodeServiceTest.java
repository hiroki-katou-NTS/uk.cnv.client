package nts.uk.cnv.dom.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import nts.uk.cnv.dom.conversiontable.ConversionTable;
import nts.uk.cnv.dom.conversiontable.ConversionTableTestHelper;
import nts.uk.cnv.dom.databasetype.DatabaseType;

@RunWith(JMockit.class)
public class CreateConversionCodeServiceTest {

	@Injectable
	CreateConversionCodeService.Require require;

	@Test
	public void test_methodCall() {
		CreateConversionCodeService target = new CreateConversionCodeService();

		ConversionInfo info = new ConversionInfo(DatabaseType.sqlserver, "KINJIROU", "dbo", "UK", "dbo", "000000000000");
		List<String> dummyCategories = Arrays.asList("1_COMPANY", "2_PERSON", "3_WEBMENU");
		List<String> dummyTables = Arrays.asList("TABLE_1", "TABLE_2");


		Optional<ConversionTable> dummyConversionTable =
				Optional.of(ConversionTableTestHelper.create_emptyDummy());

		new Expectations() {{
			require.getCategoryPriorities();
			result = dummyCategories;

			require.getCategoryTables((String) any);
			result = dummyTables;

			require.getRecords((String) any, (String) any);
			result = Arrays.asList(1);

			require.getConversionTable(info, (String) any, (String) any, 1, null);
			result = dummyConversionTable;
		}};

		String result = target.create(require, info);

		new Verifications() {{
			require.getCategoryPriorities();
			times = 1;
			require.getCategoryTables(dummyCategories.get(0));
			times = 1;
			require.getRecords(dummyCategories.get(0), (String) any);
			times = 2;
			require.getConversionTable(info, dummyCategories.get(0), (String) any, 1, null);
			times = 2;

			require.getCategoryTables(dummyCategories.get(1));
			times = 1;
			require.getRecords(dummyCategories.get(1), (String) any);
			times = 2;
			require.getConversionTable(info, dummyCategories.get(1), (String) any, 1, null);
			times = 2;

			require.getCategoryTables(dummyCategories.get(2));
			times = 1;
			require.getRecords(dummyCategories.get(2), (String) any);
			times = 2;
			require.getConversionTable(info, dummyCategories.get(2), (String) any, 1, null);
			times = 2;
		}};

		Assert.assertTrue(!result.isEmpty());
	}

	@Test
	public void test_sqlServer_simple() {
		ConversionInfo info = new ConversionInfo(DatabaseType.sqlserver, "KINJIROU", "dbo", "UK", "dbo", "000000000000");
		CreateConversionCodeService target = new CreateConversionCodeService();

		new Expectations() {{
			require.getCategoryPriorities();
			result = Arrays.asList("1_COMPANY");
			require.getCategoryTables("1_COMPANY");
			result = Arrays.asList("TABLE_1");
			require.getRecords("1_COMPANY", "TABLE_1");
			result = Arrays.asList(1);
			result = Optional.of(ConversionTableTestHelper.create_companyDummy(info));
		}};

		String result = target.create(require, info);

		Assert.assertTrue(!result.isEmpty());
	}

}
