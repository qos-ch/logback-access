package ch.qos.logback.access.common.boolex;

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A very simple {@link ch.qos.logback.core.boolex.EventEvaluator EventEvaluator}
 * which checks whether the status of {@link IAccessEvent access event} is equal
 * to a status code given as a parameter.
 *
 * @since 2.0.6
 */
public class RequestURIEventEvaluator extends EventEvaluatorBase<IAccessEvent> {

    String regex;
    private Pattern pattern;

    @Override
    public void start() {
        if (regex == null) {
            addError("regex is required");
            return;
        }

        try {
            addInfo("Compiling pattern [" + regex + "]");
            this.pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            addError("Invalid regular expression: " + regex);
            return;
        }
        super.start();
    }

    @Override
    public boolean evaluate(IAccessEvent iAccessEvent) throws NullPointerException, EvaluationException {
        if (!isStarted()) {
            throw new IllegalStateException("Evaluator [" + this + "] was called in stopped state");
        }
        String requestURI = iAccessEvent.getRequestURI();
        if(requestURI == null) {
            return false;
        }

        java.util.regex.Matcher matcher = pattern.matcher(requestURI);
        return matcher.find();
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
