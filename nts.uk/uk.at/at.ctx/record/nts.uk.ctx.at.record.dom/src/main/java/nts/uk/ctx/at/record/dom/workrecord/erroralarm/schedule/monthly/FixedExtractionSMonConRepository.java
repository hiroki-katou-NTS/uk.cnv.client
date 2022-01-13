package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly;

import java.util.List;

import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.FixedExtractionSDailyCon;


public interface FixedExtractionSMonConRepository {    
    /**
     * Get Schedule fix condition month
     * @param contractCode contract code
     * @param companyId company id
     * @param eralCheckIds list of error alarm check Id
     * @return list of schedule fix condition day
     */
    List<FixedExtractionSMonCon> getScheFixCond(String contractCode, String companyId, String eralCheckIds);
    
    /**
     * Add new a extraction schedule monthly
     * @param contractCode contract code
     * @param companyId company id
     * @param domain FixedExtractionSDailyItems
     */
    void add(String contractCode, String companyId, FixedExtractionSMonCon domain);
    
    /**
     * Update a extraction schedule monthly
     * @param contractCode contract code
     * @param companyId company id
     * @param domain FixedExtractionSDailyItems
     */
    void update(String contractCode, String companyId, FixedExtractionSMonCon domain);
    
    /**
     * Delete a extraction schedule monthly
     * @param contractCode
     * @param companyId
     * @param erAlCheckIds
     */
    void delete(String contractCode, String companyId, String erAlCheckIds);
}
