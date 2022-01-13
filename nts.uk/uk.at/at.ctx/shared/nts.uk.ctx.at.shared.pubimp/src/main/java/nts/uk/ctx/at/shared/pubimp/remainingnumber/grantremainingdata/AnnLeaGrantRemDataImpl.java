package nts.uk.ctx.at.shared.pubimp.remainingnumber.grantremainingdata;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnLeaGrantRemDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.pub.remainingnumber.grantremainingdata.AnnLeaGrantRemDataPub;
import nts.uk.ctx.at.shared.pub.remainingnumber.grantremainingdata.AnnualLeaveGrantRemainDataExport;
@Stateless
public class AnnLeaGrantRemDataImpl implements AnnLeaGrantRemDataPub{
	@Inject
	private AnnLeaGrantRemDataRepository annLeaGrantRemDataRepository;
	
	@Override
	public List<AnnualLeaveGrantRemainDataExport> getAnnualLeaveGrantRemainingData(String employeeID) {
		List<AnnualLeaveGrantRemainDataExport> result = new ArrayList<>();
		List<AnnualLeaveGrantRemainingData> annualLeaveGrantRemainingDatas = this.annLeaGrantRemDataRepository.findByCheckState(employeeID, LeaveExpirationStatus.AVAILABLE.value);
		if(CollectionUtil.isEmpty(annualLeaveGrantRemainingDatas)){
			return result;
		}
		for(AnnualLeaveGrantRemainingData annualGrant : annualLeaveGrantRemainingDatas){
			Double grantDay = null;
			Double remainDay = null;
			Double remainTime= null;
			Integer grantTime = null;
			if(annualGrant.getDetails() != null && annualGrant.getDetails().getGrantNumber() != null && annualGrant.getDetails().getGrantNumber().getDays() != null){
				grantDay  = annualGrant.getDetails().getGrantNumber().getDays().v();
				grantTime  = annualGrant.getDetails().getGrantNumber().getMinutesOrZero().v();
				remainDay = annualGrant.getDetails().getRemainingNumber().getDays().v();
				remainTime = annualGrant.getDetails().getRemainingNumber().getMinutes().isPresent()?
						annualGrant.getDetails().getRemainingNumber().getMinutes().get().v().doubleValue(): null;
			}
			AnnualLeaveGrantRemainDataExport annualLeaveGrantRemainDataExport = new AnnualLeaveGrantRemainDataExport(

					annualGrant.getGrantDate(),
					grantDay,
					remainDay,
					remainTime,
					grantTime);
			result.add(annualLeaveGrantRemainDataExport);
		}
		return result;
	}

}
