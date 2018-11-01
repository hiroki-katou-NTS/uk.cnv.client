package nts.uk.ctx.pr.core.app.find.wageprovision.statementlayout;

import nts.uk.ctx.pr.core.dom.wageprovision.statementlayout.StatementLayout;
import nts.uk.ctx.pr.core.dom.wageprovision.statementlayout.StatementLayoutHistRepository;
import nts.uk.ctx.pr.core.dom.wageprovision.statementlayout.StatementLayoutRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.YearMonthHistoryItem;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class StatementLayoutFinder {
    @Inject
    private StatementLayoutRepository statementLayoutRepo;
    @Inject
    private StatementLayoutHistRepository statementLayoutHistRepo;

    public StatementLayoutAndHistDto getStatementLayoutAndLastHist(String code) {
        String cid = AppContexts.user().companyId();

        Optional<StatementLayout> statementLayout = statementLayoutRepo.getStatementLayoutById(cid, code);
        List<YearMonthHistoryItem> yearMonthHistoryItem = statementLayoutHistRepo.getLatestHistByCidAndCode(cid, code);

        if(statementLayout.isPresent()) {
            return StatementLayoutAndHistDto.fromDomain(statementLayout.get(), yearMonthHistoryItem);
        } else {
            return null;
        }
    }

    public StatementLayoutAndHistDto getStatementLayoutAndHistById(String code, String histId) {
        String cid = AppContexts.user().companyId();

        Optional<StatementLayout> statementLayout = statementLayoutRepo.getStatementLayoutById(cid, code);
        List<YearMonthHistoryItem> yearMonthHistoryItem = statementLayoutHistRepo.getStatementLayoutHistById(histId);

        if(statementLayout.isPresent()) {
            return StatementLayoutAndHistDto.fromDomain(statementLayout.get(), yearMonthHistoryItem);
        } else {
            return null;
        }
    }
}
