package nts.uk.screen.at.app.kha003.a;

import lombok.val;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItem;
import nts.uk.screen.at.app.kha003.ManHourSummaryTableFormatDto;
import nts.uk.screen.at.app.kha003.SummaryItemDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ＜＜ScreenQuery＞＞ 工数集計レイアウト取得する
 */
@Stateless
public class ManHourSummaryLayoutScreenQuery {
    @Inject
    private ManHourSummaryTableFormatRepository manHourRepo;

    public ManHourSummaryTableFormatDto get(String code) {
        val manHourOpt = this.manHourRepo.get(AppContexts.user().companyId(), code);
        return manHourOpt.map(opt -> new ManHourSummaryTableFormatDto(
                opt.getCode().v(),
                opt.getName().v(),
                opt.getDetailFormatSetting().getTotalUnit().value,
                opt.getDetailFormatSetting().getDisplayFormat().value,
                opt.getDetailFormatSetting().getDisplayVerticalHorizontalTotal().value,
                getSummaryItemList(opt.getDetailFormatSetting().getSummaryItemList())
        )).orElse(null);
    }

    private List<SummaryItemDto> getSummaryItemList(List<SummaryItem> lstItem) {
        return lstItem.stream().map(item -> new SummaryItemDto(item.getHierarchicalOrder(),
                item.getSummaryItemType().value,
                item.getSummaryItemType().nameId
        )).sorted(Comparator.comparingInt(SummaryItemDto::getHierarchicalOrder)).collect(Collectors.toList());
    }
}
