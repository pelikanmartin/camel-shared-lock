package eu.mpelikan.camel.spring.processor;

import org.apache.camel.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartStopProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(StartStopProcessor.class.getName());

    public void process(Exchange exchange) throws Exception {
        ProducerTemplate template = exchange.getContext().createProducerTemplate();

        String routeId = exchange.getProperty("routeId", String.class);
        Boolean startRoute = Boolean.valueOf(exchange.getProperty("startRoute", String.class));
        ServiceStatus status = exchange.getContext().getRouteStatus(routeId);

        String controlString = "controlbus:route?routeId=" + routeId + "&action=";

        if (StringUtils.isEmpty(routeId) || startRoute == null) {
            // nothing to do here
            return;
        }

        if (startRoute) {
            if (status.isStopped()) {
                template.sendBody(controlString + "start", null);
            } else {
                // do nothing, route is either started or changing state
            }
        } else {
            if (status.isStarted()) {
                template.sendBody(controlString + "stop", null);
            } else {
                // do nothing, route is either stopped or changing state
            }
        }
    }

    public void printRoutesStatus(Exchange exchange) {
        LOG.info("first route is active: " + exchange.getContext().getRouteStatus("first-route").isStarted());
        LOG.info("second route is active: " + exchange.getContext().getRouteStatus("second-route").isStarted());
    }
}
