package nts.uk.ctx.at.request.ac.record.optitem;


import nts.uk.ctx.at.record.pub.optitem.application.OptionalItemAppExport;
import nts.uk.ctx.at.record.pub.optitem.application.OptionalItemAppPub;
import nts.uk.ctx.at.request.dom.adapter.OptionalItemAdapter;
import nts.uk.ctx.at.request.dom.adapter.OptionalItemImport;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class OptionalItemAppFinderAc implements OptionalItemAdapter {

    @Inject
    private OptionalItemAppPub optionalItemAppPub;

    @Override
    public List<OptionalItemImport> findOptionalItem(String companyId, List<Integer> optionalItemNos) {
        List<OptionalItemAppExport> optionalItems = optionalItemAppPub.getOptionalItems(AppContexts.user().companyId(), optionalItemNos);
        return optionalItems.stream().map(item -> {
            return new OptionalItemImport(
                    item.getOptionalItemNo(),
                    item.getOptionalItemName(),
                    item.getOptionalItemUnit(),
                    item.getCalcResultRange(),
                    item.getOptionalItemAtr(),
                    item.getDescription(),
                    item.isInputCheck()
            );
        }).collect(Collectors.toList());
    }
}
