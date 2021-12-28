package nts.uk.ctx.at.request.app.find.application.optitem.optitemdto;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.optitem.*;

import java.util.Optional;

@Setter
@Getter
public class CalcResultRangeDto implements CalcResultRangeSetMemento {
    /**
     * The upper check.
     */
    private boolean upperCheck;

    /**
     * The lower check.
     */
    private boolean lowerCheck;

    // ===================== Optional ======================= //
    /**
     * The number upper.
     */
    private Double numberUpper;

    /**
     * The number lower.
     */
    private Double numberLower;

    /**
     * The time upper.
     */
    private Integer timeUpper;

    /**
     * The time lower.
     */
    private Integer timeLower;

    /**
     * The amount upper.
     */
    private Integer amountUpper;

    /**
     * The amount lower.
     */
    private Integer amountLower;

    private Integer timeInputUnit;

    private Integer numberInputUnit;

    private Integer amountInputUnit;


    @Override
    public void setUpperLimit(CalcRangeCheck upper) {
        this.upperCheck = upper == CalcRangeCheck.SET;
    }


    @Override
    public void setLowerLimit(CalcRangeCheck lower) {
        this.lowerCheck = lower == CalcRangeCheck.SET;
    }


    @Override
    public void setNumberRange(Optional<NumberRange> range) {
        if (range.isPresent()
                && range.get().getDailyTimesRange().isPresent()
                && range.get().getDailyTimesRange().get().getLowerLimit().isPresent()) {
            this.numberLower = range.get().getDailyTimesRange().get().getLowerLimit().get().v().doubleValue();

        }
        if (range.isPresent()
                && range.get().getDailyTimesRange().isPresent()
                && range.get().getDailyTimesRange().get().getUpperLimit().isPresent()) {
            this.numberUpper = range.get().getDailyTimesRange().get().getUpperLimit().get().v().doubleValue();
        }
    }


    @Override
    public void setTimeRange(Optional<TimeRange> range) {
        if (range.isPresent()
                && range.get().getDailyTimeRange().isPresent()
                && range.get().getDailyTimeRange().get().getLowerLimit().isPresent()) {
            this.timeLower = range.get().getDailyTimeRange().get().getLowerLimit().get().v();

        }
        if (range.isPresent()
                && range.get().getDailyTimeRange().isPresent()
                && range.get().getDailyTimeRange().get().getUpperLimit().isPresent()) {
            this.timeUpper = range.get().getDailyTimeRange().get().getUpperLimit().get().v();
        }
    }


    @Override
    public void setAmountRange(Optional<AmountRange> range) {
        if (range.isPresent()
                && range.get().getDailyAmountRange().isPresent()
                && range.get().getDailyAmountRange().get().getLowerLimit().isPresent()) {
            this.amountLower = range.get().getDailyAmountRange().get().getLowerLimit().get().v();

        }
        if (range.isPresent()
                && range.get().getDailyAmountRange().isPresent()
                && range.get().getDailyAmountRange().get().getUpperLimit().isPresent()) {
            this.amountUpper = range.get().getDailyAmountRange().get().getUpperLimit().get().v();
        }
    }
}
