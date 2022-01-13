package nts.uk.file.at.infra.schedule.personalschedulebyindividual;

import lombok.Data;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PersonalScheduleByIndividualFormat {
    // item c2_1
    private String coln1C21;
    private String coln2C21;
    private String coln3C21;
    private String coln4C21;
    private String coln5C21;
    private String coln6C21;
    private String coln7C21;
    // item c2_22
    private String coln1C22;
    private String coln2C22;
    private String coln3C22;
    private String coln4C22;
    private String coln5C22;
    private String coln6C22;
    private String coln7C22;
    // item c2_3_1
    private String coln1C231;
    private String coln2C231;
    private String coln3C231;
    private String coln4C231;
    private String coln5C231;
    private String coln6C231;
    private String coln7C231;

    // item c2_3_2
    private String coln1C232;
    private String coln2C232;
    private String coln3C232;
    private String coln4C232;
    private String coln5C232;
    private String coln6C232;
    private String coln7C232;

    // item c2_3_3
    private String coln1C233;
    private String coln2C233;
    private String coln3C233;
    private String coln4C233;
    private String coln5C233;
    private String coln6C233;
    private String coln7C233;

    // item c2_3_4
    private String coln1C234;
    private String coln2C234;
    private String coln3C234;
    private String coln4C234;
    private String coln5C234;
    private String coln6C234;
    private String coln7C234;

    // Holiday clssification
    private Integer coln1HoliayClass;
    private Integer coln2HoliayClass;
    private Integer coln3HoliayClass;
    private Integer coln4HoliayClass;
    private Integer coln5HoliayClass;
    private Integer coln6HoliayClass;
    private Integer coln7HoliayClass;

    // Date inf0
    private DateInformation coln1Info;
    private DateInformation coln2Info;
    private DateInformation coln3Info;
    private DateInformation coln4Info;
    private DateInformation coln5Info;
    private DateInformation coln6Info;
    private DateInformation coln7Info;

    // item c2_3_5
    private String fromTo;
    private String D11;
    private String D12;
    private String D21;
    private String D22;
    private String D26;
    private String D27;
    private String D23;
    private String D24;
    private String D25;
    private Map<Integer, Map<Integer, String>> holiday;
    private Integer weekNo;
}
