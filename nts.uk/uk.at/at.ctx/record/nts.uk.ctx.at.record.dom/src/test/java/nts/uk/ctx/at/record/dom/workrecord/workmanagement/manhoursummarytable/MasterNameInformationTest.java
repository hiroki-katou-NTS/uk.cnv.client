package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.function.alarmworkplace.EmployeeInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.shr.com.i18n.TextResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(JMockit.class)
public class MasterNameInformationTest {
    // Used to test the function getDisplayInfo()
    private final List<WorkplaceInfor> workplaceInfos = Helper.MasterNameInfo.workplaceInfos(false, false);
    private final List<WorkplaceInfor> workplaceInfos_NameNull = Helper.MasterNameInfo.workplaceInfos(false, true);
    private final List<WorkplaceInfor> workplaceInfos_CodeNull = Helper.MasterNameInfo.workplaceInfos(true, false);
    private final List<EmployeeInfoImport> empInfos = Helper.MasterNameInfo.empInfos(false, false);
    private final List<EmployeeInfoImport> empInfos_CodeNull = Helper.MasterNameInfo.empInfos(true, false);
    private final List<EmployeeInfoImport> empInfos_NameNull = Helper.MasterNameInfo.empInfos(false, true);
    private final List<TaskImport> tasks = Helper.MasterNameInfo.tasks(false, false);
    private final List<TaskImport> tasks_CodeNull = Helper.MasterNameInfo.tasks(true, false);
    private final List<TaskImport> tasks_NameNull = Helper.MasterNameInfo.tasks(false, true);

    // used to test the function filterWorkDetailData()
    private final List<WorkplaceInfor> affWkps = Helper.MasterNameInfo.affWkps;
    private final List<WorkplaceInfor> wkps = Helper.MasterNameInfo.wkps;
    private final List<EmployeeInfoImport> emps = Helper.MasterNameInfo.emps;
    private final List<TaskImport> tasks1 = Helper.MasterNameInfo.tasks1;
    private final List<TaskImport> tasks2 = Helper.MasterNameInfo.tasks2;
    private final List<TaskImport> tasks3 = Helper.MasterNameInfo.tasks3;
    private final List<TaskImport> tasks4 = Helper.MasterNameInfo.tasks4;
    private final List<TaskImport> tasks5 = Helper.MasterNameInfo.tasks5;

    /**
     * Getter
     */
    @Test
    public void getters() {
        MasterNameInformation target = new MasterNameInformation(workplaceInfos, workplaceInfos, empInfos, tasks, tasks, tasks, tasks, tasks);
        NtsAssert.invokeGetters(target);
    }

    /**
     * SummaryItemType = 0 (AFFILIATION_WORKPLACE)
     * code = 001 => equals
     * workplaceCode not null
     * workplaceName not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsAffWkp() {
        val instance = new MasterNameInformation(workplaceInfos, null, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.AFFILIATION_WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("wkpCode01");
        assertThat(result.getName()).isEqualTo("wkpName1");
    }

    /**
     * SummaryItemType = 0 (AFFILIATION_WORKPLACE)
     * code = 001 => equals
     * workplaceName null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsAffWkp_nameIsNull() {
        val instance = new MasterNameInformation(workplaceInfos_NameNull, null, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.AFFILIATION_WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("wkpCode01");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 0 (AFFILIATION_WORKPLACE)
     * code = 001 => equals
     * workplaceCode null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsAffWkp_codeIsNull() {
        val instance = new MasterNameInformation(workplaceInfos_CodeNull, null, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.AFFILIATION_WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 0 (AFFILIATION_WORKPLACE)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsAffWkp() {
        val instance = new MasterNameInformation(workplaceInfos, null, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.AFFILIATION_WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    // WORKPLACE

    /**
     * SummaryItemType = 1 (WORKPLACE)
     * code = 001 => equals
     * workplaceCode not null
     * workplaceName not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsWkp() {
        val instance = new MasterNameInformation(null, workplaceInfos, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("wkpCode01");
        assertThat(result.getName()).isEqualTo("wkpName1");
    }

    /**
     * SummaryItemType = 1 (WORKPLACE)
     * code = 001 => equals
     * workplaceName null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsWkp_nameIsNull() {
        val instance = new MasterNameInformation(null, workplaceInfos_NameNull, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("wkpCode01");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 1 (WORKPLACE)
     * code = 001 => equals
     * workplaceCode null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsWkp_codeIsNull() {
        val instance = new MasterNameInformation(null, workplaceInfos_CodeNull, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 1 (WORKPLACE)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsWkp() {
        val instance = new MasterNameInformation(null, workplaceInfos, null, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.WORKPLACE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //EMPLOYEE

    /**
     * SummaryItemType = 2 (EMPLOYEE)
     * SID = 001 => equals
     * Code not null
     * Name not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsEmployee() {
        val instance = new MasterNameInformation(null, null, empInfos, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.EMPLOYEE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("CD_01");
        assertThat(result.getName()).isEqualTo("Name_1");
    }

    /**
     * SummaryItemType = 2 (EMPLOYEE)
     * SID = 001 => equals
     * code not null
     * Name null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsEmployee_nameIsNull() {
        val instance = new MasterNameInformation(null, null, empInfos_NameNull, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.EMPLOYEE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("CD_01");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 2 (EMPLOYEE)
     * SID = 001 => equals
     * Code null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsEmployee_codeIsNull() {
        val instance = new MasterNameInformation(null, null, empInfos_CodeNull, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.EMPLOYEE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 2 (EMPLOYEE)
     * SID = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsEmployee() {
        val instance = new MasterNameInformation(null, null, empInfos, null, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.EMPLOYEE);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //TASK1

    /**
     * SummaryItemType = 3 (TASK1)
     * Code = 001 => equals
     * Name not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask1() {
        val instance = new MasterNameInformation(null, null, null, tasks, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK1);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("Name_1");
    }

    /**
     * SummaryItemType = 3 (TASK1)
     * code = 001 => equals
     * Name null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask1_nameIsNull() {
        val instance = new MasterNameInformation(null, null, null, tasks_NameNull, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK1);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 3 (TASK1)
     * Code null
     */
    @Test
    public void getDisplayInfo_typeIsTask1_codeIsNull() {
        val instance = new MasterNameInformation(null, null, null, tasks_CodeNull, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK1);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 3 (TASK1)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsTask1() {
        val instance = new MasterNameInformation(null, null, null, tasks, null, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.TASK1);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //TASK2

    /**
     * SummaryItemType = 4 (TASK2)
     * Code = 001 => equals
     * Name not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask2() {
        val instance = new MasterNameInformation(null, null, null, null, tasks, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK2);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("Name_1");
    }

    /**
     * SummaryItemType = 4 (TASK2)
     * code = 001 => equals
     * Name null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask2_nameIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, tasks_NameNull, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK2);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 4 (TASK2)
     * Code null
     */
    @Test
    public void getDisplayInfo_typeIsTask2_codeIsNull() {
        MasterNameInformation instance = new MasterNameInformation(null, null, null, null, tasks_CodeNull, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK2);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 4 (TASK2)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsTask2() {
        val instance = new MasterNameInformation(null, null, null, null, tasks, null, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.TASK2);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //TASK3

    /**
     * SummaryItemType = 5 (TASK3)
     * Code = 001 => equals
     * Name not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask3() {
        val instance = new MasterNameInformation(null, null, null, null, null, tasks, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK3);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("Name_1");
    }

    /**
     * SummaryItemType = 5 (TASK3)
     * code = 001 => equals
     * Name null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask3_nameIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, null, tasks_NameNull, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK3);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 5 (TASK3)
     * Code null
     */
    @Test
    public void getDisplayInfo_typeIsTask3_codeIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, null, tasks_CodeNull, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK3);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 5 (TASK3)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsTask3() {
        val instance = new MasterNameInformation(null, null, null, null, null, tasks, null, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.TASK3);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //TASK4

    /**
     * SummaryItemType = 6 (TASK4)
     * Code = 001 => equals
     * Name not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask4() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, tasks, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK4);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("Name_1");
    }

    /**
     * SummaryItemType = 6 (TASK4)
     * code = 001 => equals
     * Name null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask4_nameIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, tasks_NameNull, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK4);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 6 (TASK4)
     * Code null
     */
    @Test
    public void getDisplayInfo_typeIsTask4_codeIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, tasks_CodeNull, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK4);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 6 (TASK4)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsTask4() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, tasks, null);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.TASK4);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //TASK5

    /**
     * SummaryItemType = 7 (TASK5)
     * Code = 001 => equals
     * Name not null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask5() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, null, tasks);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK5);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("Name_1");
    }

    /**
     * SummaryItemType = 7 (TASK5)
     * code = 001 => equals
     * Name null
     */
    @Test
    public void getDisplayInfo_equalsCode_typeIsTask5_nameIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, null, tasks_NameNull);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK5);

        // Assertion
        assertThat(result.getCode()).isEqualTo("001");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 7 (TASK5)
     * Code null
     */
    @Test
    public void getDisplayInfo_typeIsTask5_codeIsNull() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, null, tasks_CodeNull);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("001", SummaryItemType.TASK5);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    /**
     * SummaryItemType = 7 (TASK5)
     * code = 002 => not equals
     */
    @Test
    public void getDisplayInfo_notEqualsCode_typeIsTask5() {
        val instance = new MasterNameInformation(null, null, null, null, null, null, null, tasks);
        new Expectations(TextResource.class) {{
            TextResource.localize("KHA003_101");
            result = "マスタ未登録";
        }};

        // Execute
        val result = instance.getDisplayInfo("002", SummaryItemType.TASK5);

        // Assertion
        assertThat(result.getCode()).isEqualTo("マスタ未登録");
        assertThat(result.getName()).isEqualTo("マスタ未登録");
    }

    //---------------------- filterWorkDetailData()

    /**
     * Filtering available with all lists
     */
    @Test
    public void filterWorkDetailData_fullData() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, emps, tasks1, tasks2, tasks3, tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * affWorkplaceInfoList_empty
     */
    @Test
    public void filterWorkDetailData_affWorkplaceInfoList_empty() {
        val masterNameInfo = new MasterNameInformation(Collections.emptyList(), wkps, emps, tasks1, tasks2, tasks3, tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * workPlaceInfoList_empty
     */
    @Test
    public void filterWorkDetailData_workPlaceInfoList_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, Collections.emptyList(), emps, tasks1, tasks2, tasks3, tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * employeeInfoList_empty
     */
    @Test
    public void filterWorkDetailData_employeeInfoList_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, Collections.emptyList(), tasks1, tasks2, tasks3, tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * work1List_empty
     */
    @Test
    public void filterWorkDetailData_work1List_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, emps, Collections.emptyList(), tasks2, tasks3, tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * work2List_empty
     */
    @Test
    public void filterWorkDetailData_work2List_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, emps, tasks1, Collections.emptyList(), tasks3, tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * work3List_empty
     */
    @Test
    public void filterWorkDetailData_work3List_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, emps, tasks1, tasks2, Collections.emptyList(), tasks4, tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * work4List_empty
     */
    @Test
    public void filterWorkDetailData_work4List_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, emps, tasks1, tasks2, tasks3, Collections.emptyList(), tasks5);
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * work5List_empty
     */
    @Test
    public void filterWorkDetailData_work5List_empty() {
        val masterNameInfo = new MasterNameInformation(affWkps, wkps, emps, tasks1, tasks2, tasks3, tasks4, Collections.emptyList());
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }

    /**
     * affWorkplaceInfoList empty
     * work1List empty
     * work5List empty
     */
    @Test
    public void filterWorkDetailData_affWorkplaceInfoList_empty_work1List_empty() {
        val masterNameInfo = new MasterNameInformation(Collections.emptyList(), wkps, emps, Collections.emptyList(), tasks2, tasks3, tasks4, Collections.emptyList());
        val workDetails = Helper.workDetailDataList(8);

        // Execute
        val result = masterNameInfo.filterWorkDetailData(workDetails);

        // Assertion
        assertThat(result)
                .extracting(
                        WorkDetailData::getEmployeeId,
                        WorkDetailData::getDate,
                        WorkDetailData::getSupportWorkFrameNo,
                        WorkDetailData::getAffWorkplaceId,
                        WorkDetailData::getWorkplaceId,
                        WorkDetailData::getWorkCode1,
                        WorkDetailData::getWorkCode2,
                        WorkDetailData::getWorkCode3,
                        WorkDetailData::getWorkCode4,
                        WorkDetailData::getWorkCode5,
                        WorkDetailData::getTotalWorkingHours)
                .containsExactly(
                        tuple("001", GeneralDate.fromString("2021/06/01", "yyyy/MM/dd"), 1, "001", "001", "001", "001", "001", "001", "001", 2)
                );
    }
}
