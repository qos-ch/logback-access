package ch.qos.logback.access.common.boolex;

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * A very simple {@link ch.qos.logback.core.boolex.EventEvaluator EventEvaluator}
 * which checks whether the status of {@link IAccessEvent access event} is equal
 * to a status code given as a parameter.
 *
 * @since 2.0.6
 */
public class StatusCodeEventEvaluator extends EventEvaluatorBase<IAccessEvent> {

    final static int UNSET = -1;
    int statusCode = UNSET;

    @Override
    public void start() {
        if(statusCode == UNSET) {
            addWarn("No status code set");
            return;
        }
        super.start();
    }
    @Override
    public boolean evaluate(IAccessEvent iAccessEvent) throws NullPointerException, EvaluationException {
        if (!isStarted()) {
            throw new IllegalStateException("Evaluator [" + this + "] was called in stopped state");
        }
        boolean result = (iAccessEvent.getStatusCode() == statusCode);
        return result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
