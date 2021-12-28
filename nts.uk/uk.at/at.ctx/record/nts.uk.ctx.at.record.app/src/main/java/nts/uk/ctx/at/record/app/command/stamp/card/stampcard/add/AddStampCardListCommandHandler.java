package nts.uk.ctx.at.record.app.command.stamp.card.stampcard.add;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregAddListCommandHandler;
@Stateless
public class AddStampCardListCommandHandler extends CommandHandlerWithResult<List<AddStampCardCommand>, List<MyCustomizeException>>
implements PeregAddListCommandHandler<AddStampCardCommand>{
	@Inject
	private StampCardRepository stampCardRepo;
	@Override
	public String targetCategoryCd() {
		return "CS00069";
	}

	@Override
	public Class<?> commandClass() {
		return AddStampCardCommand.class;
	}

	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<AddStampCardCommand>> context) {
		List<AddStampCardCommand> cmd = context.getCommand();
		String contractCode = AppContexts.user().contractCode();
		List<StampCard> insertLst = new ArrayList<>();
		List<String> sids= new ArrayList<>();
		List<MyCustomizeException> result = new ArrayList<>();
		Iterator<AddStampCardCommand> itr = cmd.iterator();
		while (itr.hasNext()) {
			AddStampCardCommand c = itr.next();
			// create new domain and add
			if (c.getStampNumber() != null) {
				Optional<StampCard> stampCardOpt = this.stampCardRepo.getByCardNoAndContractCode(c.getStampNumber(),
						contractCode);
				if (stampCardOpt.isPresent()) {
					sids.add(c.getEmployeeId());
					itr.remove();
				} else {
					String stampCardId = IdentifierUtil.randomUniqueId();
					StampCard stampCard = new StampCard(new ContractCode(AppContexts.user().contractCode()),
							new StampNumber(c.getStampNumber()), c.getEmployeeId(), GeneralDate.today(), stampCardId);
					insertLst.add(stampCard);
				}
			} else {
				String stampCardId = IdentifierUtil.randomUniqueId();
				StampCard stampCard = new StampCard(new ContractCode(AppContexts.user().contractCode()),
						new StampNumber(c.getStampNumber()), c.getEmployeeId(), GeneralDate.today(), stampCardId);
				insertLst.add(stampCard);
			}
		}

		if(!sids.isEmpty()) {
			result.add(new MyCustomizeException("Msg_1106", sids));
		}
		// remove duplicate key
		List<StampCard> stampCardList = stampCardRepo.getLstStampCardByContractCode(contractCode);
		List<String> errosSid = new ArrayList<String>();
		Map<String, StampCard> insertMap = new HashMap<String, StampCard>();
		
		for (StampCard stampCard : insertLst) {
			if (stampCardList.stream().anyMatch(x -> x.getStampNumber().v().equals(stampCard.getStampNumber().v()))
					|| insertMap.containsKey(stampCard.getStampNumber().v())
					
					) {
				errosSid.add(stampCard.getEmployeeId());
			} else {
				
				insertMap.put(stampCard.getStampNumber().v(), stampCard);
			}
		}
		if (!CollectionUtil.isEmpty(errosSid)) result.add(new MyCustomizeException("Msg_1106", errosSid));
		insertLst =
				insertMap
					.entrySet()
					.stream()
					.map(Map.Entry::getValue)
					.collect(Collectors.toList());
						
		if(!insertLst.isEmpty()) {
			stampCardRepo.addAll(insertLst);
		}
		
		return result;
	}

}
