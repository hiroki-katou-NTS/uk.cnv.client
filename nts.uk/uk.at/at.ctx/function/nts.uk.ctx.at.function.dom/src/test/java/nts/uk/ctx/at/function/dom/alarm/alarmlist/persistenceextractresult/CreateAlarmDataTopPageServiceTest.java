package nts.uk.ctx.at.function.dom.alarm.alarmlist.persistenceextractresult;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.alarm.AffAtWorkplaceExport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.DeleteInfoAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmImport;
import nts.uk.shr.com.context.AppContexts;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(JMockit.class)
public class CreateAlarmDataTopPageServiceTest {
    @Injectable
    private CreateAlarmDataTopPageService.Require require;

    /**
     * WkplIdListNotError Empty
     * wkplIdListErrors NotEmpty
     */
    @Test
    public void testCreateAlarmTopPage_WorkplaceIdListNotError_Empty() {
        DeleteInfoAlarmImport deleteInfoAlarmImport = new DeleteInfoAlarmImport(
                1,
                Arrays.asList("del001", "del002"),
                0,
                Optional.of("patternCode1"));
        List<TopPageAlarmImport> alarmListInfo = DumData.alarmListInfos;
        List<AffAtWorkplaceExport> affAtWorkplaceExports = DumData.affAtWorkplaceExports01;
        List<String> employeeIds = Arrays.asList("sya001", "sya002", "sya003", "sya004", "sya005", "del001", "del002");

        String companyId = "companyId";

        new Expectations(AppContexts.class) {
            {
                AppContexts.user().companyId();
                result = companyId;

                require.getWorkplaceId(employeeIds, GeneralDate.today());
                result = affAtWorkplaceExports;

//                require.getAdminReceiveAlarmMailByWorkplaceIds(Collections.singletonList("S001"));
//                result = Collections.singletonMap("S001", Arrays.asList("sya001", "sya002", "sya003","sya004", "sya005"));

                require.getAdminReceiveAlarmMailByWorkplaceIds(Arrays.asList("S001", "S002"));
                Map<String, List<String>> infoMap = new HashMap<>();
                infoMap.put("S001",Arrays.asList("sya001", "sya002", "sya003"));
                infoMap.put("S002", Arrays.asList("sya004", "sya005"));
                result = infoMap;
            }
        };

        NtsAssert.atomTask(() ->
                        CreateAlarmDataTopPageService.create(require, Optional.of(deleteInfoAlarmImport), alarmListInfo),
                any -> require.create(companyId, any.get(), any.get())
        );
    }

    /**
     * WkplIdListNotError NotEmpty
     * wkplIdListErrors NotEmpty
     */
    @Test
    public void testCreateAlarmTopPage_WorkplaceIdListNotError_NotEmpty() {
        DeleteInfoAlarmImport deleteInfoAlarmImport = DumData.deleteInfoAlarmImport2;
        List<TopPageAlarmImport> alarmListInfo = DumData.alarmListInfos2;
        List<AffAtWorkplaceExport> affAtWorkplaceExports = DumData.affAtWorkplaceExports;
        List<String> employeeIds = DumData.employeeIds;

        String companyId = "companyId";

        new Expectations(AppContexts.class) {
            {
                AppContexts.user().companyId();
                result = companyId;

                require.getWorkplaceId(employeeIds, GeneralDate.today());
                result = affAtWorkplaceExports;

                require.getAdminReceiveAlarmMailByWorkplaceIds(Collections.singletonList("S002"));
                result = Collections.singletonMap("S002", Arrays.asList("sya004", "sya005"));

                require.getAdminReceiveAlarmMailByWorkplaceIds(Collections.singletonList("S001"));
                result = Collections.singletonMap("S001", Arrays.asList("sya001", "sya002", "sya003"));
            }
        };

        NtsAssert.atomTask(() -> CreateAlarmDataTopPageService.create(require, Optional.of(deleteInfoAlarmImport), alarmListInfo),
                any -> require.create(companyId, any.get(), any.get())
        );
    }

    /**
     * wkplIdListErrors Empty
     */
    @Test
    public void testCreateAlarmTopPage_wkplIdListErrors_Empty() {
        DeleteInfoAlarmImport deleteInfoAlarmImport = DumData.deleteInfoAlarmImport;
        List<TopPageAlarmImport> alarmListInfo = new ArrayList<>();
        List<AffAtWorkplaceExport> affAtWorkplaceExports = DumData.affAtWorkplaceExports;
        List<String> employeeIds = Arrays.asList("sya001", "sya002", "sya003", "sya004", "sya005");

        String companyId = "companyId";

        new Expectations(AppContexts.class) {
            {
                AppContexts.user().companyId();
                result = companyId;

                require.getWorkplaceId(employeeIds, GeneralDate.today());
                result = affAtWorkplaceExports;
            }
        };

        NtsAssert.atomTask(() -> CreateAlarmDataTopPageService.create(require, Optional.of(deleteInfoAlarmImport), alarmListInfo),
                any -> require.create(companyId, any.get(), any.get())
        );
    }

    /**
     * wkplIdListErrors NotEmpty
     */
    @Test
    public void testCreateAlarmTopPage_wkplIdListErrors_NotEmpty() {
        DeleteInfoAlarmImport deleteInfoAlarmImport = DumData.deleteInfoAlarmImport2;
        List<TopPageAlarmImport> alarmListInfo = DumData.alarmListInfos2;
        List<AffAtWorkplaceExport> affAtWorkplaceExports = DumData.affAtWorkplaceExports;
        List<String> employeeIds = DumData.employeeIds;

        String companyId = "companyId";

        new Expectations(AppContexts.class) {
            {
                AppContexts.user().companyId();
                result = companyId;

                require.getWorkplaceId(employeeIds, GeneralDate.today());
                result = affAtWorkplaceExports;

                require.getAdminReceiveAlarmMailByWorkplaceIds(Collections.singletonList("S001"));
                result = Collections.singletonMap("S001", Arrays.asList("sya001", "sya002", "sya003"));
            }
        };

        NtsAssert.atomTask(() -> CreateAlarmDataTopPageService.create(require, Optional.of(deleteInfoAlarmImport), alarmListInfo),
                any -> require.create(companyId, any.get(), any.get())
        );
    }

    /**
     * alarmListInfo_Empty
     */
    @Test
    public void testCreateAlarmTopPage_alarmListInfo_Empty() {
        List<TopPageAlarmImport> alarmListInfo = Collections.emptyList();
        DeleteInfoAlarmImport deleteInfoAlarmImport = DumData.deleteInfoAlarmImport2;
        List<AffAtWorkplaceExport> affAtWorkplaceExports = DumData.affAtWorkplaceExports2;
        List<String> employeeIds = Arrays.asList("sya004", "sya005");

        String companyId = "companyId";

        new Expectations(AppContexts.class) {
            {
                AppContexts.user().companyId();
                result = companyId;

                require.getWorkplaceId(employeeIds, GeneralDate.today());
                result = affAtWorkplaceExports;

                require.getAdminReceiveAlarmMailByWorkplaceIds(Collections.singletonList("S002"));
                result = Collections.singletonMap("S002", Arrays.asList("sya004", "sya005"));
            }
        };

        NtsAssert.atomTask(() -> CreateAlarmDataTopPageService.create(require, Optional.of(deleteInfoAlarmImport), alarmListInfo),
                any -> require.create(companyId, any.get(), any.get())
        );
    }
}
