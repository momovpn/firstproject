package com.xuanwu.mos.sdk;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.xuanwu.mos.HostInfo;
import com.xuanwu.mos.HostInfo.ConnectionType;
import com.xuanwu.mos.MessageData;
import com.xuanwu.mos.PostMsg;
import com.xuanwu.mos.PostMsgBuilder;
import com.xuanwu.mos.common.entity.Account;
import com.xuanwu.mos.common.entity.BusinessType;
import com.xuanwu.mos.common.entity.GsmsResponse;
import com.xuanwu.mos.common.entity.MOMsg;
import com.xuanwu.mos.common.entity.MTPack;
import com.xuanwu.mos.common.entity.MTReport;
import com.xuanwu.mos.common.util.Interceptor;
import com.xuanwu.mos.common.util.MediaUtil;

/**
 * 玄武MOS SDK示例代码
 * @version 5.1
 */
public class PostMsgTest {
	
	public static void main(String[] args) throws Exception {
		final PostMsg pm = PostMsgBuilder.getInstance() // 保持单例模式
				.setShortConnMode(false) // 设置是否以短连接方式交互, 建议为false(即长连接以提高性能)
				.setSoTimeout(15000) // Socket读写超时时间(毫秒)
				.setMaxConnForMT(2) // 每个账号MT最大并发连接数(下发), 若发送量较大可调整为3-7
				.setMaxConnForMO(2) // 每个账号MO最大并发连接数(获取上行/状态报告)
				.build();
		
		pm.getGwHost().setHost("127.0.0.1", 8090); // 设置网关的IP和port, 用于发送信息
		pm.getWsHost().setHost("127.0.0.1", 8088); // 设置网关的IP和port, 用于获取账号信息、上行、状态报告
		
		// 网络耗时调试功能(仅需要时配置)
		pm.setInterceptor(new Interceptor() {
			public void beforeMtSend(long waitConnTime, long loginTime, MTPack pack) {
				// 加上自定义的日志输出
				System.out.println("MT before send [" + pack.getBatchID() 
						+ "] wait:" + waitConnTime + ", login:" + loginTime + ", now:" + System.currentTimeMillis());
			}
		});
		
		// 下发失败内部自动重试(仅需要时配置)
		pm.getGwHost().setRetryTimes(1); // 重试次数
		pm.getGwHost().setRetryInterval(500); // 重试间隔: 毫秒
		
		// 设置代理服务器(仅需要时配置)
		// settingProxyServer(pm);
		
		Account account = new Account("admin@test1", "123456"); // 单账号
		// account.setSessionKey("16位长度的英文或数字密钥"); // 配置通信安全密钥对敏感数据加密(用于高安全性要求的场景, 同时增加少量性能损耗)
		
		doSendSms(pm, account); // 短信下行
//		doSendMms(pm, account); // 彩信下行
		
//		doGetAccountInfo(pm, account); // 获取账号信息
//		doModifyPwd(pm, account); // 修改密码
		
//		doGetMos(pm, account); // 获取上行信息
//		doGetReports(pm, account); // 获取状态报告

	}
	
	/**
	 * 短信下发样例, 单次调用最大下发手机号码数为1万。
	 */
	public static void doSendSms(PostMsg pm, Account ac) throws Exception {
		MTPack pack = new MTPack();
		pack.setBatchID(UUID.randomUUID());
		pack.setBatchName("短信测试批次");
		pack.setMsgType(MTPack.MsgType.SMS);
		//pack.setBizType(2); // 设置业务类型
		pack.setDistinctFlag(true); // 是否进行号码去重
		ArrayList<MessageData> msgs = new ArrayList<MessageData>();
		
		/** 单发, 一号码一内容 */
		pack.setSendType(MTPack.SendType.GROUP);
		msgs.add(new MessageData("137000000X1", "短信单发测试"));
		pack.setMsgs(msgs);
		
		/** 群发, 多号码同一内容 */
		/*pack.setSendType(MTPack.SendType.MASS);
		String content = "短信群发测试";
		msgs.add(new MessageData("137000000X1", content));
		msgs.add(new MessageData("137000000X2", content));
		pack.setMsgs(msgs);*/
		
		/** 组发, 多号码多内容 */
		/*pack.setSendType(MTPack.SendType.GROUP);
		msgs.add(new MessageData("137000000X1", "短信组发测试1"));
		msgs.add(new MessageData("137000000X2", "短信组发测试2"));
		pack.setMsgs(msgs);*/
		
		/** 使用模板方式发送, 设置模板编号(静态模板将以模板内容发送; 动态模板需要发送变量值) */
		/*pack.setTemplateNo("8973febf65e144d492d070dc8c55b46c");
		msgs.add(new MessageData("137000000X1", "[]")); // 1. 静态模板(不需要传变量, 空数组即可)
		msgs.add(new MessageData("137000000X2", "[\"测试用户\",\"123456\"]")); // 2. 动态模板设置依次替换的参数
		pack.setMsgs(msgs);
		*/
		
		GsmsResponse resp = pm.post(ac, pack);
		System.out.println(resp);
	}
	
	/**
	 * 彩信下发范例
	 */
	public static void doSendMms(PostMsg pm, Account ac) throws Exception {
		MTPack pack = new MTPack();
		pack.setBatchID(UUID.randomUUID());
		pack.setBatchName("彩信测试批次-2条");
		pack.setMsgType(MTPack.MsgType.MMS);
		//pack.setBizType(1);
		pack.setDistinctFlag(false);
		ArrayList<MessageData> msgs = new ArrayList<MessageData>();

		String path = PostMsgTest.class.getClassLoader().getResource("mms_test").getPath();
		path = URLDecoder.decode(path, "utf-8");
		
		// 设置公共彩信资源
		pack.setMedias(MediaUtil.getMediasFromFolder(path));
		
//		/** 单发，一号码一内容 */
//		msgs.add(new MessageData("134000000X1", null));
//		pack.setMsgs(msgs);
		
		/** 群发，多号码一内容 */
		pack.setSendType(MTPack.SendType.MASS);
		msgs.add(new MessageData("134000000X1", null));
		msgs.add(new MessageData("134000000X2", null));
		pack.setMsgs(msgs);
		
		/** 组发，多号码多内容 */
		/*pack.setSendType(MTPack.SendType.GROUP);
		//设置私有彩信资源
		MessageData msg1 = new MessageData("134000000X1", null);
		msg1.setMedias(MediaUtil.getMediasFromFolder("F:/mms_test/msg1"));
		msgs.add(msg1);
		
		MessageData msg2 = new MessageData("134000000X2", null);
		msg2.setMedias(MediaUtil.getMediasFromFolder("F:/mms_test/msg2"));
		msgs.add(msg2);
		
		pack.setMsgs(msgs);
		*/
		
		GsmsResponse resp = pm.post(ac, pack);
		System.out.println(resp);
	}
	
	/**
	 * 获取账号信息
	 */
	public static void doGetAccountInfo(PostMsg pm, Account ac) throws Exception {
		System.out.println(pm.getAccountInfo(ac));   // 获取账号详细信息
		
		List<BusinessType> bizTypes = pm.getBizTypes(ac); // 获取账号绑定业务类型
		for (BusinessType bizType : bizTypes) {
			System.out.println(bizType);
		}
	}
	
	/**
	 * 获取上行信息
	 */
	public static void doGetMos(PostMsg pm, Account ac) throws Exception {
		List<MOMsg> moList = pm.getMOMsgs(ac, 100);
		if (moList != null && moList.size() > 0) {
			String confirmId = null;
			for (MOMsg mo : moList) {
				System.out.println(mo);
				confirmId = mo.getReserve();
			}
			
			// 数据确认, 当数据处理完毕需要发送确认回执, 否则会重复获取
			if (!pm.isAutoConfirm()) {
				pm.confirmMoRequest(ac, confirmId);
			}
		}
	}
	
	/**
	 * 获取状态报告
	 */
	public static void doGetReports(PostMsg pm, Account ac) throws Exception {
		List<MTReport> reportList = pm.getReports(ac, 200);
		if (reportList != null && reportList.size() > 0) {
			String confirmId = null;
			for (MTReport report : reportList) {
				System.out.println(report);
				confirmId = report.getReserve();
			}
			
			// 数据确认，当数据处理完毕，需要发送确认回执，否则会重复获取
			if (!pm.isAutoConfirm()) {
				pm.confirmReportRequest(ac, confirmId);
			}
		}
	}
	
	/**
	 * 修改密码
	 */
	public static void doModifyPwd(PostMsg pm, Account ac) throws Exception {
		System.out.println(pm.modifyPassword(ac, "你的复杂密码"));
	}
	
	/**
	 * 设置代理服务器
	 */
	public static void settingProxyServer(PostMsg postMsg) {
		HostInfo proxyHost = postMsg.getProxy().getProxyHost();
		proxyHost.setType(ConnectionType.SOCKET5);
		proxyHost.setHost("192.168.70.129", 1080);
		//proxyHost.setUsername("admin");
		//proxyHost.setPassword("123456");
	}
	
}
