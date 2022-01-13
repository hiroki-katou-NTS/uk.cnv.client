package nts.uk.ctx.at.record.pubimp.optitem.application;

import nts.uk.ctx.at.record.pub.optitem.application.OptionalItemAppExport;
import nts.uk.ctx.at.record.pub.optitem.application.OptionalItemAppPub;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class OptionalItemAppPubImp implements OptionalItemAppPub {

    @Inject
    private OptionalItemRepository optItemRepo;

    @Override
    public List<OptionalItemAppExport> getOptionalItems(String companyId, List<Integer> optionalItemNos) {
        return this.optItemRepo.findByListNos(companyId, optionalItemNos).stream()
                .map(optItem -> OptionalItemAppExport.builder()
                        .optionalItemName(optItem.getOptionalItemName().v())
                        .optionalItemNo(optItem.getOptionalItemNo().v())
                        .optionalItemUnit(optItem.getUnit().isPresent() ? optItem.getUnit().get().v() : null)
                        .calcResultRange(optItem.getInputControlSetting().getCalcResultRange())
                        .optionalItemAtr(optItem.getOptionalItemAtr())
                        .description(optItem.getDescription().isPresent() ? optItem.getDescription().get().v() : null)
                        .inputCheck(optItem.getInputControlSetting().isInputWithCheckbox())
                        .build())
                .collect(Collectors.toList());
    }
}
