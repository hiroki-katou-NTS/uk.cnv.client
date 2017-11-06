package nts.uk.shr.com.time.calendar.period;

import nts.gul.util.range.ComparableRange;
import nts.gul.util.value.DiscreteValue;

public abstract class GeneralPeriod<S extends GeneralPeriod<S, T>, T extends Comparable<T> & DiscreteValue<T>>
		implements ComparableRange<S, T> {

	private final T start;
	private final T end;
	
	GeneralPeriod(T start, T end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public T start() {
		return this.start;
	}

	@Override
	public T end() {
		return this.end;
	}

	@Override
	public T startNext(boolean isIncrement) {
		return this.start.nextValue(isIncrement);
	}

	@Override
	public T endNext(boolean isIncrement) {
		return this.end.nextValue(isIncrement);
	}
	
	public boolean isEndMax() {
		return this.end.compareTo(this.max()) == 0;
	}
	
	public S newSpanWithMaxEnd() {
		return this.newSpan(this.start, this.max());
	}
	
	protected abstract T max();
}
