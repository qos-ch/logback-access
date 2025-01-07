package ch.qos.logback.access.common.blackbox.testUtil;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniStatusChecker {

    StatusManager sm;

    public MiniStatusChecker(StatusManager sm) {
        this.sm = sm;
    }

    public MiniStatusChecker(Context context) {
        this.sm = context.getStatusManager();
    }

    public boolean containsMatch(String regex) {
        Pattern p = Pattern.compile(regex);
        for (Status status : sm.getCopyOfStatusList()) {
            String msg = status.getMessage();
            Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                return true;
            }
        }
        return false;
    }
}
