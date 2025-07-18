package ch.qos.logback.access.tomcat_10_1;

import ch.qos.logback.access.common.spi.IAccessEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class ListAppender extends AppenderBase<IAccessEvent>  {

    public List<IAccessEvent> list = new ArrayList<>();

    @Override
    protected void append(IAccessEvent eventObject) {
        list.add(eventObject);
    }
}
