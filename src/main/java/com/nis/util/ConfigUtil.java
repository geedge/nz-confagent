package com.nis.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtil {
	@Value("${ipaddr}")
	private String ipaddr;
	@Value("${ymlPath}")
	private String ymlPath;
	@Value("${prometheusPort}")
	private	String prometheusPort;
}
