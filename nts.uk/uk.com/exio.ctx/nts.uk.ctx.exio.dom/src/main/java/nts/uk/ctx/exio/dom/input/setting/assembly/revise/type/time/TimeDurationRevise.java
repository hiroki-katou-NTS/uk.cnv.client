package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time;

import nts.gul.util.Either;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;

import java.util.Optional;

public class TimeDurationRevise extends TimeRevise {
    public TimeDurationRevise(HourlySegment hourly, Optional<TimeBaseNumber> baseNumber, Optional<TimeBase60Delimiter> delimiter, Optional<TimeBase10Rounding> rounding) {
        super(hourly, baseNumber, delimiter, rounding);
    }

    @Override
    protected Either<ErrorMessage, Integer> toMinutes(String target) {
        return super.getDelimiter().get().toMinutes(target);
    }
}
