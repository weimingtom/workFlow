<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	request.setAttribute("ctx", request.getContextPath());
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>

<html>
<title>登录</title>
<%@ include file="../../../common/common.jsp"%>
<base href="<%=basePath%>">
<head>
<link href="${ctx}/workflow/js/uploadify/themes/default.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/workflow/js/uploadify/themes/uploadify.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${ctx}/workflow/js/uploadify/jquery.uploadify.v2.1.4-min.js"></script>
<script type="text/javascript" src="${ctx}/workflow/js/uploadify/swfobject.js"></script>
<script type="text/javascript" src="${ctx}/workflow/js/uploadify/jquery.uploadify.v2.1.4.js"></script>
<script type="text/javascript" src="${ctx}/workflow/js/uploadify/org_domain_lang_zh.js"></script>

<script type="text/javascript">
	function login() {
		var json = {
			"userId" : $("#user_id").val(),
			"userName" : $("#user_id").find("option:selected").text(),
			"deptId" : "1",
			"deptName" : "产品开发部",
			"tenantId" : "611",
			"tenantName" : "zywx",
			"suffix" : "/workflow/formList"
		};

		var con = JSON.stringify(json);
		$.ajax({
			type : "post",
			url : "http://localhost:8080/appdo-web-flow/initLogin/init",
			contentType : "application/json",
			data : con,
			success : function(data) {
			},
			error : function(error) {
			}
		});
	}
	//提交保存操作
	function submit(op) {
		var json = {"tenantId":"611",
				"entityTypeId":"24",
				"entity":{
					"input_date":"2015-11-13",
					"dept":"企业级移动应用研发部",
					"user_info":$("#user_id").find("option:selected").text(),
					"oper_type":"2",
					"reason_info":"出差，去约会",
					"dept_opinion":"",
					"hr_opinion":"",
					"dgm_opinion":"",
					"gm_opinion":"",
					"billKey":"1",
					"metaid":"32",
					"userType":"员工",//分管副总经理  部门负责人
					"countday":3},
				"operateUserId":$("#user_id").val(),
				"userId":$("#user_id").val(),
				"userType":"员工",
				"entityId":"E150f56771da3139",
				"operateTypeId":"01",
				"objectId":"2015年11月12229日",
				"createdAt":"2015-11-11T07:20:34.079Z"
				};
		var con = JSON.stringify(json);
		$.ajax({
			type : "post",
			url : "http://localhost:8080/appdo-web-flow/workFlowAction/saveForm/"+op,
			contentType : "application/json",
			data : con,
			success : function(data) {
				alert(data.info);
			},
			error : function(error) {
			}
		});
	}
	//我提交的单据类型
	function getMySubmitList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"billKey" : "",
			"metaid" : "",
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getMySubmitList",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	//获取模板数据
	function getModel() {
		var json = {
			"tenantId" : 611,
			"userId" : $("#user_id").val(),
			"billKey" : 20,
			"metaid" : 20
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",//getBillTypeList getBillTypeFileById getBillTypeTemplateFileds
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getBillTypeFileById",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data.info);
					},
					error : function(error) {
					}
				})
	}

	//获取单据类型数据
	function getBillTypeList() {
		var json = {
			"tenantId" : "611",
			"userId" : "",
			"billKey" : "",
			"metaid" : ""
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",//getBillTypeList getBillTypeFileById getBillTypeTemplateFileds
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getBillTypeList",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取单据类型字段信息
	function getBillTypeTemplateFileds() {
		var json = {
			"tenantId" : "611",
			"userId" : "",
			"billKey" : "20",
			"metaid" : "20"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",//getBillTypeList getBillTypeFileById getBillTypeTemplateFileds
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getBillTypeTemplateFileds",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取草稿箱数据
	function getDraftList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getDraftList",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}

	//获取进行中流程实例
	function getProcessInstanceList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"rowCnt" : "10",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getProcessInstanceList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取已完成流程实例
	function getProcessInstanceHisList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"metaid" : "20",
			"prco_inset_id" : "",
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getProcessInstanceHisList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取历史审批数据
	function getHistoryList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"metaid" : "20",
			"prco_inset_id" : "115007",
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getHistoryList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取代签收列表
	function getClaimList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"metaid" : "",
			"prco_inset_id" : "",
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getClaimList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取待办数据
	function getTodoList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getTodoList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//获取已办数据
	function getApprovedList() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"metaid" : "20",
			"prco_inset_id" : "",
			"rowCnt" : "30",
			"pageNo" : "1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getApprovedList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//签收任务
	function claimTask() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"taskId" : "65072"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowAction/claimTask",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data.info);
					},
					error : function(error) {
					}
				})
	}
	//提交任务
	function commitTask() {
		var json = {
				"tenantId" : "611",
				"userId" : $("#user_id").val(),
				"taskId" : "65072",
				"approve":"true",
				"approveResult":"同意...."
		}
		var data = JSON.stringify(json)
		$.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowAction/commitTask",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data.info);
					},
					error : function(error) {
					}
				})
	}
	
	//流程定义信息
	function getProcessDefList() {
		var json = {
				"tenantId" : "611",
				"userId" : $("#user_id").val(),
				"metaid" : "",
				"prco_inset_id" : "De16438f2-cf1c-4d84-950b-da3c19363696",
				"rowCnt" : "30",
				"pageNo" : "1"
			}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getProcessDefList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//流程定义信息
	function delDraftData() {
		var json = {
				"tenantId" : "611",
				"businessKey" : "56455959865ccdd1743b3f84",
			}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowAction/delDraftData",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	//流程定义信息
	function getToRejectTaskList() {
		var json = {
				"prco_inset_id" : "80015",
			}
		var data = JSON.stringify(json)
		$.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getToRejectTaskList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data.info);
					},
					error : function(error) {
					}
				})
	}
	
	
	/**
	*是否办结查询
	*/
	function getMyInvolvedProcList(isEnd) {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"metaid" : "20",
			"rowCnt" : "10",
			"pageNo" : "1",
			"isFinished":isEnd
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getMyInvolvedProcList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	function getMyProcList(fin) {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"metaid" : "20",
			"prco_inset_id" : "",
			"rowCnt" : "30",
			"pageNo" : "1",
			"isFinished":fin
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getProcessInstanceList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	//收回
	function backTask() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"prco_inset_id" : "65001"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowAction/backTask",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	//驳回
	function rejectTask() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"prco_inset_id" : "",
			"targetTaskKey":"",
			"approveResult":""
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowAction/rejectTask",
					contentType : "application/json",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	
	function getProcIMG() {
		var json = {
			"prco_inset_id" : "115007"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getFlowImage",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	
	function rejectTask() {
		var json = {
			"targetTaskKey" : "17508",
			"taskId" : "22517",
			"tenantId":"611"
		
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowAction/rejectTask",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	
	//获取被退回数据
	function getTodoList_back() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"rowCnt" : "10",
			"pageNo" : "1",
			"isback":"1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getCommitBackToMyProcList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
	
	//获取我收回数据
	function getTodoList_revoke() {
		var json = {
			"tenantId" : "611",
			"userId" : $("#user_id").val(),
			"rowCnt" : "10",
			"pageNo" : "1",
			"isrevoke":"1"
		}
		var data = JSON.stringify(json)
		$
				.ajax({
					type : "post",
					url : "http://localhost:8080/appdo-web-flow/workFlowData/getRevokeBackToMyProcList",
					contentType : "application/json;charset=UTF-8",
					data : data,
					success : function(data) {
						alert(data);
					},
					error : function(error) {
					}
				})
	}
</script>
</head>
<body>
		<br/>
		<input type="button" id="login" value="登录" onclick="login()" /> 
	<div>
	<br/>
		<input type="button" id="getModel" value="单据模板" onclick="getModel()" />
		<input type="button" id="getBillTypeList" value="单据类型列表" onclick="getBillTypeList()" />
		<input type="button" id="getBillTypeTemplateFileds" value="单据类型字段" onclick="getBillTypeTemplateFileds()" />
	</div>
	<div>
	<br/>
	
		<input type="button" id="getDraftList" value="草稿数据" onclick="getDraftList()" />
		<input type="button" id="getClaimList" value="待签收" onclick="getClaimList()" /> 
		<input type="button" id="getTodoList" value="待办理" onclick="getTodoList()" />
		<input type="button" id="getApprovedList" value="已办理" onclick="getApprovedList()" />
		<input type="button" id="getHistoryList" value="审批轨迹" onclick="getHistoryList()" />
		<input type="button" id="getProcessInstanceList" value="流转中" onclick="getProcessInstanceList()" />
		
	</div>
	<div>
	<br/>
		<input type="button" id="submit" value="保存" onclick="submit(0)" />
		<input type="button" id="submit" value="提交" onclick="submit(1)" />
		<input type="button" id="claimTask" value="签收" onclick="claimTask()" />
		<input type="button" id="commitTask" value="办理" onclick="commitTask()" />
		<input type="button" id="backTask" value="收回" onclick="backTask()" />
		<input type="button" id="rejectTask" value="驳回" onclick="rejectTask()" />
		<input type="button" id="delDraftData" value="删除草稿" onclick="delDraftData()" />
		
	</div>
	<div>
		<input type="button" id="getMySubmitList" value="我提交的单据" onclick="getMySubmitList()" />
		<input type="button" id="myBackTask" value="被退回的单据" onclick="getTodoList_back()" />
		<input type="button" id="myRevokeData" value="我收回的单据" onclick="getTodoList_revoke()" />
		<input type="button" id="getProcessDefList" value="查询流程定义信息" onclick="getProcessDefList()" />
		<input type="button" id="getToRejectTaskList" value="可驳回列表" onclick="getToRejectTaskList()" />
		<input type="button" id="getFlowImage" value="流程图" onclick="getProcIMG()"/>
		<input type="button" id="getMyInvolvedProcList" value="我发起的已办结流程" onclick="getMyInvolvedProcList(1)" />
		<input type="button" id="getMyInvolvedProcList" value="我发起的未办结流程" onclick="getMyInvolvedProcList(0)" />
		
	</div>
	<div>
	<br/>
	<br/>
	<div>
	<b>操作人员：</b>
		<select id="user_id">
			<option value="ZY0502" selected>苏荣秋</option>
			<option value="ZY0315">李文</option>
			<option value="ZY0889">人力资源</option>
			<option value="ZY0001">王国春</option>
			<option value="ZY0057">杜辉</option>
			<option value="ZY0024">陈雪</option>
		</select>
	</div>
	<br/>
	<a href="/appdo-web-flow/settingMeta/metaCustomList">元数据管理-管理后台</a>
	<br>
	<a href="/appdo-web-flow/settingModel/modelList">模型管理-管理后台</a>
	<br>
	<a href="/appdo-web-flow/workflow/formList">测试表单</a>
	<br>
	<a href="/appdo-web-flow/workflow/draftList">草稿箱</a>
	<br>
	<a href="/appdo-web-flow/workflow/claimList">待签收</a>
	<br>
	<a href="/appdo-web-flow/workflow/todoList">待办理</a>
	<br>
	<a href="/appdo-web-flow/workflow/approvedList">已办</a>
	<br>
	<a href="/appdo-web-flow/workflow/submitList">我的单据</a>
	<br>
	<a href="/appdo-web-flow/workflow/processInstanceList">管理员流程跟踪</a>
	</div>
	
	
</body>
</html>