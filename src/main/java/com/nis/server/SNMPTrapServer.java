package com.nis.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nis.entity.AlertManagerEnum;
import com.nis.entity.AlertRule;
import com.nis.entity.ConfagentMetrics;
import com.nis.entity.SysConfig;
import com.nis.entity.TrapMessage;
import com.nis.service.AlertRuleService;
import com.nis.service.SysConfigService;




/**
 * 本类用于监听代理进程的Trap信息
 * 1.加载alert_rule表内置规则（有且只有一条）
 * 2.启动snmp_server
 * 3.接收trap信息 
 * 4.组装prometheus_alert格式
 * 5.发送到web端接口处理
 * 
 * @author YJS
 * 
 */
@Component
@Order(1)
public class SNMPTrapServer implements CommandResponder,ApplicationRunner{// implements CommandResponder, extends Service
	private static Logger logger = LoggerFactory.getLogger(SNMPTrapServer.class);
	private static SNMPTrapServer ts = null;
	private AlertRule alertRule;
	@Value("${confagent.snmp.ip}")
	private String snmpIp;
	@Value("${confagent.snmp.trapThredPoolSize}")
	private String snmpTrapThredPoolSize;
	@Value("${confagent.snmp.trapPort}")
	private String snmpTrapPort;
	@Autowired
	private AlertRuleService alertRuleService;
	@Autowired
	private SysConfigService sysConfigService;
	private String alertApi;
	private String alertPathPrefix;
	@Autowired
	private ConfagentMetrics confagentMetrics;
	/*public static SNMPTrapServer getInstance() throws IOException{
		if(ts == null){
			ts = new SNMPTrapServer();
			logger.info("SNMP Trap Server 启动成功");
		}else{
			logger.info("SNMP TrapServer 已启动");
		}
		return ts;
	}*/
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		initServer();
	}
	
	public void initServer() throws Exception{
		logger.info("snmpTrapThredPoolSize is {}",snmpTrapThredPoolSize);
		ThreadPool threadPool = ThreadPool.create("Trap", Integer.valueOf(snmpTrapThredPoolSize));
		logger.info("snmpTrapThredPoolSize is {}",snmpTrapThredPoolSize);
		MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,	new MessageDispatcherImpl());
		Address listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:0.0.0.0/" + snmpTrapPort)); // 本地IP与监听端口
		logger.info("snmpTrapPort is {}",snmpTrapPort);
		logger.info("ipaddr is {}",snmpIp);
		TransportMapping transport = null;
		
//	    			 对TCP与UDP协议进行处理
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
			logger.debug("this listenAddress type is UDP");
		} else {
			transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
			logger.debug("this listenAddress type is TCP");
		}
		
		Snmp snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();
		snmp.addCommandResponder(this);
	}
	 
    private OID getAuthProtocol(String auth){
    	if (auth.equals("MD5")) {
  		 	return  AuthMD5.ID;
        } else if (auth.equals("SHA")) {
        	return  AuthSHA.ID;
        }else{
        	return null;
        }
    }
    
    private OID getPrivProtocol(String priv){
        if (priv.equals("DES")) {
      	  return PrivDES.ID;
        } else if ((priv.equals("AES128"))
                || (priv.equals("AES"))) {
      	  return PrivAES128.ID;
        } else if (priv.equals("AES192")) {
      	  return PrivAES192.ID;
        } else if (priv.equals("AES256")) {
      	  return PrivAES256.ID;
        }else{
        	return null;
        }
    }

	
	
	
	/**
	 * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息 当接收到trap时，会自动进入这个方法
	 * 
	 * @param respEvnt
	 */
	
	public void processPdu(CommandResponderEvent commandresponderevent)
	{
		try
		{
			//考虑数据库数据可能存在变化 由仅查询一次更改为每次都确认一下数据信息
			//加载alert_rule表内置规则
			alertRule=alertRuleService.queryBulidInRule();
			List<SysConfig> sysConfigs = sysConfigService.queryList();
			if(!sysConfigs.isEmpty()) {
				for(SysConfig sysconfig : sysConfigs) {
					if(sysconfig.getParamKey().equals(AlertManagerEnum.ALERT_API.getValue())) {
						alertApi=sysconfig.getParamValue();
					}else if(sysconfig.getParamKey().equals(AlertManagerEnum.ALERT_PATH_PREFIX.getValue())){
						alertPathPrefix=sysconfig.getParamValue();
					}
				}
				logger.info("sysconfig alert api info {}",alertApi);
				logger.info("sysconfig alert path prefix info {}",alertPathPrefix);
			}else {
				logger.error("Sysconfig missing alert_api or alert_path_prefix data");
				return;
			}
			logger.info("processPdu start");
			logger.info("alertRule info is {}",JSON.toJSON(alertRule));
			if(alertRule==null) {
				logger.error(" alert rule info is null ");
				return;
			}
			logger.info((new StringBuilder("receive snmp event：")).append(commandresponderevent.getPDU()).toString());
			UdpAddress udpaddress = (UdpAddress)commandresponderevent.getPeerAddress();
			String s = udpaddress.getInetAddress().getHostAddress();
			PDU pdu = commandresponderevent.getPDU();
			logger.info((new StringBuilder("receive TRap:")).append(pdu).toString());
			TrapMessage trapmessageinfo = new TrapMessage();
			trapmessageinfo.setAgentSendIP(s);
			setOIDList(trapmessageinfo, pdu);
			setValueList(trapmessageinfo, pdu);
			trapmessageinfo.setOriginalPDU(pdu);
			if (pdu instanceof PDUv1)
			{
				PDUv1 pduv1 = (PDUv1)pdu;
				trapmessageinfo.setTrapVersion(1);
				String s1 = pduv1.getEnterprise().toString();
				trapmessageinfo.setTrapOID(s1);
				String s2 = pduv1.getAgentAddress().toString();
				trapmessageinfo.setPduAgentIP(s);
				trapmessageinfo.setTrapV1SpecificType(pduv1.getSpecificTrap());
				trapmessageinfo.setTrapV1GenericType(pduv1.getGenericTrap());
				trapmessageinfo.setTrapName(trapmessageinfo.getTrapOID());
				switch (pduv1.getGenericTrap())
				{
				case 0: // '\0'
//					trapmessageinfo.setTrapName("设备冷启动");
					trapmessageinfo.setTrapName("Device Cold Start");
					break;

				case 1: // '\001'
//					trapmessageinfo.setTrapName("设备热启动");
					trapmessageinfo.setTrapName("Device Hot Start");
					break;

				case 2: // '\002'
//					trapmessageinfo.setTrapName("接口关闭");
					trapmessageinfo.setTrapName("The Interface Is Closed");
					break;

				case 3: // '\003'
//					trapmessageinfo.setTrapName("接口启用");
					trapmessageinfo.setTrapName("Interface Enabled");
					break;

				case 4: // '\004'
//					trapmessageinfo.setTrapName("SNMP认证失败");
					trapmessageinfo.setTrapName("SNMP Authentication Failed");
					break;

				case 5: // '\005'
//					trapmessageinfo.setTrapName("EGP邻居丢失");
					trapmessageinfo.setTrapName("EGP Neighbor Lost");
					break;
				}
			} else
			{
				trapmessageinfo.setTrapVersion(2);
				Variable variable = trapmessageinfo.getOIDValue(SnmpConstants.snmpTrapAddress.toString());
				logger.info("pdu.snmpTrapAddress() =-=-= "+variable);
				if (variable != null)
					trapmessageinfo.setPduAgentIP(variable.toString());
				Variable variable1 = trapmessageinfo.getOIDValue(SnmpConstants.snmpTrapOID.toString());
				if (variable1 != null)
					trapmessageinfo.setTrapOID(variable1.toString());
				trapmessageinfo.setTrapName(trapmessageinfo.getTrapOID());
				if (variable1 != null)
					if (variable1.equals(SnmpConstants.coldStart))
//						trapmessageinfo.setTrapName("设备冷启动");
						trapmessageinfo.setTrapName("Device Cold Start");
					else
					if (variable1.equals(SnmpConstants.warmStart))
//						trapmessageinfo.setTrapName("设备热启动");
						trapmessageinfo.setTrapName("Device Hot Start");
					else
					if (variable1.equals(SnmpConstants.linkDown))
//						trapmessageinfo.setTrapName("接口关闭");
						trapmessageinfo.setTrapName("The Interface Is Closed");
					else
					if (variable1.equals(SnmpConstants.linkUp))
//						trapmessageinfo.setTrapName("接口启用");
						trapmessageinfo.setTrapName("Interface Enabled");
					else
					if (variable1.equals(SnmpConstants.authenticationFailure))
//						trapmessageinfo.setTrapName("SNMP认证失败");
						trapmessageinfo.setTrapName("SNMP Authentication Failed");
					/*else //系统默认的类别不能匹配时，再使用自定义的trap信息库。是否需要分别自定义snmpV1和snmpValue2的trap信息库（定义到文件或者定义到数据库，必须体统重新加载的界面接口）
					if(getTrapName(variable1)!=null &&!variable1.equals("")){
						trapmessageinfo.setTrapName(getTrapName(variable1));
					}else{
//						throw new Exception("未被定义的SNMPTRAP类型！");
						throw new Exception("Undefined SNMPTRAP type!");
					}*/
			}
			StringBuffer trapInfo = new StringBuffer();
			trapInfo.append("trapmessageinfo.getTrapName()："+trapmessageinfo.getTrapName());
			trapInfo.append("; trapmessageinfo.getAgentSendIP()："+trapmessageinfo.getAgentSendIP());
			trapInfo.append("; trapmessageinfo.getOriginalPDU()："+trapmessageinfo.getOriginalPDU());
			trapInfo.append("; trapmessageinfo.getTrapVersion()："+trapmessageinfo.getTrapVersion());
			trapInfo.append("; trapmessageinfo.getTrapOID()："+trapmessageinfo.getTrapOID());
			trapInfo.append("; trapmessageinfo.getPduAgentIP()："+trapmessageinfo.getPduAgentIP());
			trapInfo.append("; trapmessageinfo.getTrapV1GenericType()："+trapmessageinfo.getTrapV1GenericType());
			trapInfo.append("; trapmessageinfo.getTrapV1SpecificType()："+trapmessageinfo.getTrapV1SpecificType());
			logger.info("agent IP："+s+"  trap message："+trapInfo.toString());
			
			// 组装prometheus_alert格式
			Integer alertName = alertRule.getId();
			String severity = alertRule.getSeverity();
			String asset = trapmessageinfo.getAgentSendIP();
			String oid = trapmessageinfo.getTrapOID();
			String value=trapmessageinfo.getOriginalPDU().getVariableBindings().toString();
			
			StringBuilder description = new StringBuilder();
			description.append("version:"+trapmessageinfo.getTrapVersion());
			description.append(",agentip:"+trapmessageinfo.getAgentSendIP());
			description.append(",oid:"+trapmessageinfo.getTrapOID());
			description.append(",value:"+value);
			String trapName = trapmessageinfo.getTrapName();
			if(trapName!=null&&trapName.length()>0) {
				description.append(",trapName:"+trapName);
			}
			StringBuilder summary=new StringBuilder("snmptrap:");
			summary.append(trapmessageinfo.getAgentSendIP()+"(");
			summary.append(trapmessageinfo.getTrapOID()+":"+value+")");
			boolean snmptrap=true;
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String startsAt = sdf.format(new Date());
			
			JSONObject labels =new JSONObject();
			labels.put("alertname", alertName.toString());
			labels.put("severity",severity);
			labels.put("asset", asset);
			labels.put("oid", oid);
			
			JSONObject annotations =new JSONObject();
			annotations.put("description", description.toString());
			annotations.put("summary", summary.toString());
			
			JSONObject result =new JSONObject();
			result.put("labels", labels);
			result.put("annotations", annotations);
			result.put("snmptrap", snmptrap);
			result.put("startsAt", startsAt);
			
			JSONArray mess =new JSONArray();
			mess.add(result);
			
			logger.info("send alert info is {}",JSON.toJSON(mess));
			
			// 将告警信息发送到web端处理
			RestTemplate restTemplate=new RestTemplate();
			restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity entity =new HttpEntity(mess,headers);
			String url="http://"+alertApi+alertPathPrefix+"/api/v1/alerts";
			Object postForEntity = restTemplate.postForEntity(url, entity,Object.class);
			logger.info("post success info {}",JSON.toJSON(postForEntity));
			confagentMetrics.snmpCounter.increment();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			logger.error((new StringBuilder("Error parsing UDP trap:")).append(exception.getMessage()).toString());
		}finally{
			
		}
	}
	
	public String getTrapName(Variable variable1){
		String trapName = "";
		Map allTrapTypeDefine = new HashMap();
		trapName = allTrapTypeDefine.get(variable1.toString()).toString();
		return trapName;
	}
	
	void setOIDList(TrapMessage trapmessageinfo, PDU pdu)
	{
		Vector vector = pdu.getVariableBindings();
		String s;
//		for(int i=0;i<vector.size();i++){
//			VariableBinding vbing = (VariableBinding)vector.elementAt(i);
//			s = vbing.getOid().toString();
//			trapmessageinfo.getTrapPDUOIDs().add(s);
//		}
		for (Iterator iterator = vector.iterator(); iterator.hasNext(); trapmessageinfo.getTrapPDUOIDs().add(s))
		{
			VariableBinding variablebinding = (VariableBinding)iterator.next();
			s = variablebinding.getOid().toString();
		}

	}

	void setValueList(TrapMessage trapmessageinfo, PDU pdu)
	{
		Vector vector = pdu.getVariableBindings();
		Variable variable;
		for (Iterator iterator = vector.iterator(); iterator.hasNext(); trapmessageinfo.getTrapPDUOIDValues().add(variable))
		{
			VariableBinding variablebinding = (VariableBinding)iterator.next();
			variable = variablebinding.getVariable();
		}

	}


	/** */
	/**
	 * 解决snmp4j中文乱码问题
	 */
	public static String getChinese(String octetString) {
		try {
			String[] temps = octetString.split(":");
			byte[] bs = new byte[temps.length];
			for (int i = 0; i < temps.length; i++)
				bs[i] = (byte) Integer.parseInt(temps[i], 16);

			return new String(bs, "GB2312");
		} catch (Exception e) {
			return null;
		}
	}

	protected static String printVariableBindings(PDU response) {
		String strCom = "";
		for (int i = 0; i < response.size(); i++) {
			VariableBinding vb = response.get(i);
			String[] str = vb.getVariable().toString().toUpperCase().split(":");

			String strOut = "";
			int intLength = str.length;
			System.out.println("长度==> " + intLength);
			for (int j = 0; j < intLength; j++) {
				strOut += str[j];
			}
			strCom += vb.getVariable();
			if (str.length != 1) {
				System.out.println("==第行=vb.getVariable()=" + SNMPTrapServer.toStringHex(strOut));//显示中文 
			}
		}
		return strCom;

	}
	public static void  main(String [] args) {
		/*try {
			SNMPTrapServer.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	/** 
	 * 将十进六制转换成为中文 
	 */
	private static String hexString = "0123456789ABCDEF";
	
	public static String toStringHex(String bytes) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		//将每2位16进制整数组装成一个字节 
		for (int i = 0; i < bytes.length(); i += 2) {
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		}
		return new String(baos.toByteArray());
	}
}
