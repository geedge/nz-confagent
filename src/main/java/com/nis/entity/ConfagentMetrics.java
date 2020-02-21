package com.nis.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nis.service.EndpointService;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;

@Component
public class ConfagentMetrics implements MeterBinder {
	
	public Counter snmpCounter;
    public Map<String, Double> map;
    public Counter confagentUpTimeCounter;
    @Autowired
    private EndpointService endpointService;
    
    ConfagentMetrics() {
        map = new HashMap<>();
    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
    	
    	this.snmpCounter = Counter.builder("nz_confagent_snmptrap_nums")
                .tags(new String[]{"name", "snmpNums"})
                .description("snmp alert count").register(meterRegistry);
    	this.confagentUpTimeCounter = Counter.builder("nz_confagent_up_time")
                .tags(new String[]{"name", "upTime"})
                .description("confagent up time").register(meterRegistry);
        if(map==null) {
        	map=new HashMap<>();
        }
        List<MetricsDto> endpointMetrics = endpointService.queryEndpointMetrics();
        if(endpointMetrics!=null && endpointMetrics.size()>0) {
        	for(int i=0;i<endpointMetrics.size();i++) {
        		// 这里将统计值扔进map操作是避免弱引用  统计结果出现NaN
        		map.put("endpointNums"+i, endpointMetrics.get(i).getTotal());
        		String key="endpointNums"+i;
        		Gauge.builder("nz_confagent_endpoint_nums", map, x -> x.get(key))
                .tags("id",endpointMetrics.get(i).getId().toString(),"host",endpointMetrics.get(i).getHost())
                .description("Alert message statistics info")
                .register(meterRegistry);
        	}
        }else {
        	map.put("endpointNums", 0.0);
    		String key="endpointNums";
    		Gauge.builder("nz_confagent_endpoint_nums", map, x -> x.get(key))
            .tags("id","null","host","null")
            .description("Alert message statistics info")
            .register(meterRegistry);
        }
    }
}
