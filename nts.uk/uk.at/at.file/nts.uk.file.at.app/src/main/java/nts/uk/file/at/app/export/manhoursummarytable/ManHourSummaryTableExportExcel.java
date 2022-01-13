package nts.uk.file.at.app.export.manhoursummarytable;

import lombok.val;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.gul.collection.CollectionUtil;
import nts.uk.screen.at.app.kha003.CountTotalLevel;
import nts.uk.screen.at.app.kha003.SummaryItemDetailDto;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourHierarchyFlatData;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ManHourSummaryTableExportExcel extends ExportService<ManHourSummaryTableQuery> {
    @Inject
    private ManHourSummaryTableGenerator manHourSummaryTableGenerator;

    @Override
    protected void handle(ExportServiceContext<ManHourSummaryTableQuery> exportServiceContext) {
        val generatorContext = exportServiceContext.getGeneratorContext();
        val query = exportServiceContext.getQuery();

        CountTotalLevel totalLevelObj = new CountTotalLevel(0);
        countHierarchy(query.getOutputContent().getItemDetails(), totalLevelObj);
        int countTotalLevel = totalLevelObj.getCountTotalLevel();

        ManHourSummaryExportData data = new ManHourSummaryExportData(
                query.getSummaryTableFormat(),
                query.getOutputContent(),
                query.getPeriod(),
                Collections.emptyList(),
                countTotalLevel
        );
        this.manHourSummaryTableGenerator.generate(generatorContext, data);
    }

    private Collector<ManHourHierarchyFlatData, ?, Map<String, Map<String, List<ManHourHierarchyFlatData>>>> groupByLv2AndLv3() {
        return groupingBy(ManHourHierarchyFlatData::getCodeLv2, groupingBy(ManHourHierarchyFlatData::getCodeLv3));
    }

    private void countHierarchy(List<SummaryItemDetailDto> parentList, CountTotalLevel result) {
        int totalLevel = result.getCountTotalLevel();
        if (CollectionUtil.isEmpty(parentList)) return;
        List<SummaryItemDetailDto> childHierarchy = parentList.stream().flatMap(x -> x.getChildHierarchyList().stream()).collect(Collectors.toList());
        totalLevel += 1;
        result.setCountTotalLevel(totalLevel);
        countHierarchy(childHierarchy, result);
    }
}
