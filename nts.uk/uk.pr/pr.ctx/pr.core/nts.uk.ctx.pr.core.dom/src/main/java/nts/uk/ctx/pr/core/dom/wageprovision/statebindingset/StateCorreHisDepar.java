package nts.uk.ctx.pr.core.dom.wageprovision.statebindingset;

import java.util.List;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.shr.com.history.YearMonthHistoryItem;
import nts.uk.shr.com.history.strategic.ContinuousResidentHistory;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.arc.time.YearMonth;

/**
* 明細書紐付け履歴（部門）
*/
@Getter
public class StateCorreHisDepar extends AggregateRoot implements ContinuousResidentHistory<YearMonthHistoryItem, YearMonthPeriod,YearMonth> {

    /**
     * 会社ID
     */
    private String cId;

    /**
     * 履歴
     */
    private List<YearMonthHistoryItem> history;
    
    public StateCorreHisDepar(String cid, List<YearMonthHistoryItem> history) {
        this.cId = cid;
        this.history =  history;
    }

    @Override
    public List<YearMonthHistoryItem> items() {
        return this.history;
    }

    @Override
    public void exCorrectToRemove(YearMonthHistoryItem latest) {
        latest.changeSpan(latest.span().newSpanWithMaxEnd());
    }
}
