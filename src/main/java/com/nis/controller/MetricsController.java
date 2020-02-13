package com.nis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nis.entity.ConfagentMetrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@RestController
public class MetricsController{
	@Autowired
	private ConfagentMetrics confagentMetrics;
	
    @RequestMapping(value = "/metrics", method= RequestMethod.GET)
    public ModelAndView counter1() {
    	confagentMetrics.bindTo(new SimpleMeterRegistry());
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("redirect:/prometheus");
    	return mv;
    }
}