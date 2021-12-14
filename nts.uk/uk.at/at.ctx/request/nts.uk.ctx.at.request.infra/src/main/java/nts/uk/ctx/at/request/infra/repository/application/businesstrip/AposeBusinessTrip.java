package nts.uk.ctx.at.request.infra.repository.application.businesstrip;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Worksheet;

import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfo;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripPrintContent;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripWorkHour;
import nts.uk.ctx.at.request.dom.application.common.service.print.PrintContentOfApp;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class AposeBusinessTrip {

    @Inject
    private WorkTypeRepository workTypeRepository;

    public void printBusinessTrip(Worksheet worksheet, PrintContentOfApp printContentOfApp) {
        Cells cells = worksheet.getCells();

        String departureTime = Strings.EMPTY;
        String returnTime = Strings.EMPTY;
        String cid = AppContexts.user().companyId();

        Optional<BusinessTripPrintContent> businessTrip = printContentOfApp.getOpBusinessTripPrintContent();
        if( businessTrip.isPresent()) {
            if (businessTrip.get().getDepartureTime().isPresent()) {
                departureTime = new TimeWithDayAttr(businessTrip.get().getDepartureTime().get()).getFullText();
            }
            if (businessTrip.get().getReturnTime().isPresent()) {
                returnTime = new TimeWithDayAttr(businessTrip.get().getReturnTime().get()).getFullText();
            }
            if (!businessTrip.get().getInfos().isEmpty()) {
                List<String> wkTypeCds = businessTrip.get().getInfos()
                        .stream().filter(i -> i.getWorkInformation().getWorkTypeCode() != null)
                        .map(i -> i.getWorkInformation().getWorkTypeCode().v())
                        .collect(Collectors.toList());
                List<WorkType> lstWorkType = workTypeRepository.findNotDeprecatedByListCode(cid, wkTypeCds);
                List<BusinessTripInfo> lstInfo = businessTrip.get().getInfos();
                if (lstInfo.size() > 15) {
                    int startRow = 10;
                    List<BusinessTripInfo> listECell = lstInfo.subList(0, 16);
                    this.setColumnVal(cells, "D", "E", "F", listECell, lstWorkType);
                    List<BusinessTripInfo> listICell = lstInfo.subList(16, lstInfo.size());
                    this.setColumnVal(cells, "H", "I", "J", listICell, lstWorkType);
                } else {
                    this.setColumnVal(cells, "D", "E", "F", lstInfo, lstWorkType);
                    int totalDateRows = businessTrip.get().getInfos().size();
                    cells.hideRows(9 + totalDateRows, 16 - totalDateRows);
                }
            }
        }

        Cell cellD1 = cells.get("B8");
        cellD1.setValue(I18NText.getText("KAF008_20"));

        Cell cellD2_1 = cells.get("D8");
        cellD2_1.setValue(I18NText.getText("KAF008_21"));

        Cell cellD2_2 = cells.get("E8");
        cellD2_2.setValue(departureTime);

        Cell cellD3_1 = cells.get("G8");
        cellD3_1.setValue(I18NText.getText("KAF008_22"));

        Cell cellD3_2 = cells.get("H8");
        cellD3_2.setValue(returnTime);

        Cell cellD10 = cells.get("B9");
        cellD10.setValue(I18NText.getText("KAF008_23"));

        Cell cellD10_H1 = cells.get("D9");
        cellD10_H1.setValue(I18NText.getText("KAF008_24"));

        Cell cellD10_H2 = cells.get("F9");
        cellD10_H2.setValue(I18NText.getText("KAF008_23"));

        Cell cellD10_H3 = cells.get("H9");
        cellD10_H3.setValue(I18NText.getText("KAF008_24"));

        Cell cellD10_H4 = cells.get("J9");
        cellD10_H4.setValue(I18NText.getText("KAF008_23"));

    }

    private void setColumnVal(Cells cells, String dateRow, String wkTypeCodeRow, String timeRow, List<BusinessTripInfo> lstInfo, List<WorkType> lstWorkType) {
        int startRow = 10;
        for (BusinessTripInfo eachinfo : lstInfo) {
            Cell currentDateRow = cells.get(dateRow + startRow);
            Cell currentWkTypeNameRow = cells.get(wkTypeCodeRow + startRow);
            Cell currentTimeRow = cells.get(timeRow + startRow);

            String wkTypeName = Strings.EMPTY;

            GeneralDate currentDate = eachinfo.getDate();
            currentDateRow.setValue(currentDate.toString("M/d") + this.getDayOfWeek(currentDate.dayOfWeekEnum()));

            if (eachinfo.getWorkInformation().getWorkTypeCode() != null) {
                Optional<WorkType> workTypeInfo = lstWorkType.stream().filter(i -> i.getWorkTypeCode().v().equals(eachinfo.getWorkInformation().getWorkTypeCode().v())).findFirst();
                if (workTypeInfo.isPresent()) {
                    wkTypeName = workTypeInfo.get().getName().v();
                    currentWkTypeNameRow.setValue(wkTypeName);
                }
            }
            if (!eachinfo.getWorkingHours().isEmpty()) {
                String start = "";
                String end = "";
                Optional<BusinessTripWorkHour> workNo1 = eachinfo.getWorkingHours().stream().filter(i -> i.getWorkNo().v().equals(1)).findFirst();
                if (workNo1.isPresent()) {
                    start = workNo1.get().getStartDate().isPresent() ? workNo1.get().getStartDate().get().getFullText() : "";
                    end = workNo1.get().getEndDate().isPresent() ? workNo1.get().getEndDate().get().getFullText() : "";
                    if (!StringUtils.isEmpty(start) || !StringUtils.isEmpty(end)) {
                        if (StringUtils.isEmpty(start)) start = "　　　　";
                        if (StringUtils.isEmpty(end)) end = "　　　　";
                        currentTimeRow.setValue(
                                start + 
                                I18NText.getText("KAF008_69") + 
                                end);
                    }
                }
            }
            startRow++;
        }
    }

    private String getDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "(月)";
            case TUESDAY:
                return "(火)";
            case WEDNESDAY:
                return "(水)";
            case THURSDAY:
                return "(木)";
            case FRIDAY:
                return "(金)";
            case SATURDAY:
                return "(土)";
            case SUNDAY:
                return "(日)";
        }
        return null;
    }

}
