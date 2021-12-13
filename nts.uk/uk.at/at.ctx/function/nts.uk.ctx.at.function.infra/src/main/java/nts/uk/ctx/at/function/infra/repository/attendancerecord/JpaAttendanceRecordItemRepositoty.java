package nts.uk.ctx.at.function.infra.repository.attendancerecord;

import java.rmi.server.UID;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.uk.ctx.at.function.dom.attendancerecord.item.AttendanceRecordRepositoty;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnmtRptWkAtdOutframe;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.KfnmtRptWkAtdOutframePK;
import nts.uk.ctx.at.function.infra.entity.attendancerecord.item.KfnmtRptWkAtdOutatd;

/**
 * The type JpaAttendanceRecordItemRepositoty.
 *
 * @author locph
 */
@Stateless
public class JpaAttendanceRecordItemRepositoty extends JpaAttendanceRecordRepository implements AttendanceRecordRepositoty {

    /**
     * deleteAttendanceRecord
     *
     * @param companyId         the company id
     * @param exportSettingCode the export setting code
     */
    @Override
    public void deleteAttendanceRecord(String layoutId) {
        List<KfnmtRptWkAtdOutatd> items = this.findAllAttendanceRecordItem(layoutId);
        if (items != null && !items.isEmpty()) {
            this.removeAllAttndRecItem(items);
            this.getEntityManager().flush();
        }

    }
    
    /**
     * Duplicate attendance to new layoutId
     * @param layoutId the layout id
     * @param dupliadteId the duplicate id
     */
    @Override
    public void duplicateAttendanceRecord(String layoutId, String duplicateId) {
    	// Duplicate KfnmtRptWkAtdOutframe
    	List<KfnmtRptWkAtdOutframe> frameItems = this.findAllAttendanceRecords(layoutId).stream()
    			.map(x -> {
		        	KfnmtRptWkAtdOutframePK id = new KfnmtRptWkAtdOutframePK(duplicateId,
		    				x.getId().getColumnIndex(),
		    				x.getId().getOutputAtr(),
		    				x.getId().getPosition());
		        	KfnmtRptWkAtdOutframe entity = new KfnmtRptWkAtdOutframe(
		        			id,
		        			x.getExclusVer(),
		        			x.getContractCd(),
		        			x.getCid(),
		        			x.isUseAtr(),
		        			x.getItemName(),
		        			x.getAttribute());
		        	return entity;
		    	}).collect(Collectors.toList());
    	this.commandProxy().insertAll(frameItems);
    	
    	// Duplicate KfnmtRptWkAtdOutatd
    	List<KfnmtRptWkAtdOutatd> atdItems =  this.findAllAttendanceRecordItem(layoutId).stream()
    			.map(x -> new KfnmtRptWkAtdOutatd(
    					(new UID()).toString(),
    					x.getExclusVer(),
    					x.getContractCd(),
    					x.getCid(),
    					duplicateId,
    					x.getColumnIndex(),
    					x.getPosition(),
    					x.getOutputAtr(),
    					x.getTimeItemId(),
    					x.getFormulaType()
    			)).collect(Collectors.toList());
    	this.commandProxy().insertAll(atdItems);
    	
    }
}
