package nts.uk.ctx.at.record.infra.repository.log;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.log.ErrMessageContent;
import nts.uk.ctx.at.record.dom.workrecord.log.ErrMessageInfo;
import nts.uk.ctx.at.record.dom.workrecord.log.ErrMessageInfoRepository;
import nts.uk.ctx.at.record.dom.workrecord.log.ErrMessageResource;
import nts.uk.ctx.at.record.dom.workrecord.log.enums.ExecutionContent;
import nts.uk.ctx.at.record.infra.entity.log.KrcdtErrMessageInfo;
@Stateless
public class JpaErrMessageInfoRepository extends JpaRepository implements ErrMessageInfoRepository {

	private final String SELECT_FROM_ERR_MESSAGE = "SELECT c FROM KrcdtErrMessageInfo c ";
	private final String SELECT_ERR_MESSAGE_BYID = SELECT_FROM_ERR_MESSAGE
			+ " WHERE c.krcdtErrMessageInfoPK.empCalAndSumExecLogID = :empCalAndSumExecLogID "
			+ " AND c.krcdtErrMessageInfoPK.executionContent = :executionContent ";
	
	private ErrMessageInfo toDomain(KrcdtErrMessageInfo entity) {
		return new ErrMessageInfo(
				entity.krcdtErrMessageInfoPK.employeeID,
				entity.krcdtErrMessageInfoPK.empCalAndSumExecLogID,
				new ErrMessageResource(entity.krcdtErrMessageInfoPK.resourceID),
				EnumAdaptor.valueOf( entity.krcdtErrMessageInfoPK.executionContent,ExecutionContent.class),
				entity.krcdtErrMessageInfoPK.disposalDay,
				new ErrMessageContent(entity.messageError)
				);
	}
	
	@Override
	public List<ErrMessageInfo> getAllErrMessageInfoByEmpID(String empCalAndSumExecLogID,int executionContent) {
		List<ErrMessageInfo> data = this.queryProxy().query(SELECT_ERR_MESSAGE_BYID , KrcdtErrMessageInfo.class)
				.setParameter("empCalAndSumExecLogID", empCalAndSumExecLogID)
				.setParameter("executionContent", executionContent)
				.getList(c -> toDomain(c));
		return data;
	}

}
