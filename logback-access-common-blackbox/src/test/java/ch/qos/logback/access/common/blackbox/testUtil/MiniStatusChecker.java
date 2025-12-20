package ch.qos.logback.access.common.blackbox.testUtil;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MiniStatusChecker {

    public static final int UNKNOWN_LEVEL = -1;
    StatusManager sm;

    public MiniStatusChecker(StatusManager sm) {
        this.sm = sm;
    }

    public MiniStatusChecker(Context context) {
        this.sm = context.getStatusManager();
    }

    public boolean containsMatch(int level, String regex) {
        List<Status> filteredList = sm.getCopyOfStatusList();
        if(level != -1) {
            filteredList = sm.getCopyOfStatusList().stream().filter(s -> s.getLevel() == level).collect(Collectors.toList());
        }
        Pattern p = Pattern.compile(regex);
        for (Status status :filteredList) {
            String msg = status.getMessage();
            Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsMatch(String regex) {
        return containsMatch(UNKNOWN_LEVEL, regex);
    }

        public int matchCount(String regex) {
        int count = 0;
        Pattern p = Pattern.compile(regex);
        for (Status status : sm.getCopyOfStatusList()) {
            String msg = status.getMessage();
            Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                count++;
            }
        }
        return count;
    }
}
