package nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EmployeeStampInfo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampDataOfEmployees;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampInfoDisp;

/**
 * @author anhdt
 *
 */
@Data
public class StampDataOfEmployeesDto {
	private String employeeId;
	private String date;
	private List<StampRecordDto> stampRecords = new ArrayList<>();
	
	public StampDataOfEmployeesDto (EmployeeStampInfo domain) {
		this.employeeId = domain.getEmployeeId();
		this.date = domain.getDate().toString();
		
		
		List<StampInfoDisp> stampRecords = domain.getListStampInfoDisp();
		for(StampInfoDisp stampRecord : stampRecords) {
			this.stampRecords.add(new StampRecordDto(stampRecord));
		}
		
	}
	
	public StampDataOfEmployeesDto(StampDataOfEmployees domain) {
        this.employeeId = domain.getEmployeeId();
        this.date = domain.getDate().toString();
        
        Map<String, Stamp> cardNumberStamp = domain.getListStamp()
                 .stream()
                 .filter(distinctByKey(s -> s.retriveKey()))
                 .collect(Collectors.toMap(Stamp::retriveKey, s -> s));
        
//        List<StampRecord> stampRecords = domain.getListStampRecord();
//        stampRecords.sort((d1, d2) -> d2.getStampDateTime().compareTo(d1.getStampDateTime()));
//        for(StampRecord stampRecord : stampRecords) {
//            Stamp stamp = cardNumberStamp.get(stampRecord.retriveKey());
//            this.stampRecords.add(new StampRecordDto(stampRecord, stamp));
//        }
	}

	public static <T> Predicate<T> distinctByKey(
		    Function<? super T, ?> keyExtractor) {
		   
		    Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
		    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
		}
}
