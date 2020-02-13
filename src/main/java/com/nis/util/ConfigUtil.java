package com.nis.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtil {
	@Value("${confagent.ipaddr}")
	private String ipaddr;
	@Value("${confagent.ymlPath}")
	private String ymlPath;
	@Value("${confagent.prometheusPort}")
	private	String prometheusPort;
}
