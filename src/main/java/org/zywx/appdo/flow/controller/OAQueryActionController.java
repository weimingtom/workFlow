package org.zywx.appdo.flow.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.appdo.bean.QueryMongBean;
import org.zywx.appdo.common.AjaxResult;
import org.zywx.appdo.flow.dao.BpmApproveDao;
import org.zywx.appdo.flow.entity.BpmApprove;
import org.zywx.appdo.flow.entity.BpmTodo;
import org.zywx.appdo.flow.service.BpmApproveService;
import org.zywx.appdo.flow.service.BpmTodoService;
import org.zywx.appdo.flow.service.WorkflowService;
import org.zywx.appdo.meta.entity.MetaBusi;
import org.zywx.appdo.meta.entity.MetaCustom;
import org.zywx.appdo.meta.entity.MetaCustomField;
import org.zywx.appdo.meta.entity.MetaTenant;
import org.zywx.appdo.meta.service.MetaBusiService;
import org.zywx.appdo.meta.service.MetaCustomFieldService;
import org.zywx.appdo.meta.service.MetaCustomService;
import org.zywx.appdo.meta.service.MetaTenantService;
import org.zywx.appdo.utils.MyJsonUtil;
import org.zywx.appdo.utils.PropertyTools;

import com.alibaba.dubbo.common.json.JSONObject;

/**
 * 工作流对外请求动作接口
 * 
 * @author zorro
 *
 */
@Controller
@RequestMapping(value = "/workFlowAction")
public class OAQueryActionController{
	protected Logger logger = LoggerFactory.getLogger(getClass());

	// 返回状态码 000 成功 001 失败
	Map<String, Object> paraMap = null;
	// 初始化服务
	@Autowired
	private MetaBusiService metaBusiService;// 单据类型信息服务
	@Autowired
	private MetaCustomFieldService metaCustomFieldService;// 单据类型字段服务
	@Autowired
	private MetaTenantService metaTenantService;
	@Autowired
	private MetaCustomService metaCustomService;
	@Autowired
	private WorkflowService workflowService;
	@Autowired
	private BpmApproveService bpmApproveService;
	@Autowired
	private BpmTodoService bpmTodoService;
	@Autowired
	private BpmApproveDao bpmApproveDao;
	// 变量集合map
	AjaxResult ajaxResult = new AjaxResult();

	/**
	 * 保存或提交单据
	 * @param saveOrsubmit
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "saveForm/{saveOrsubmit}")
	@ResponseBody
	public AjaxResult saveForm(@PathVariable("saveOrsubmit") String saveOrsubmit, @RequestBody String jsonParam,
			HttpServletRequest request) {
		// 解析字符串，取参数值
		QueryMongBean mongBean = QueryMongBean.getQueryMongBean(jsonParam);
		JSONObject entity = (JSONObject) mongBean.getEntity();
		Long metaid = Long.valueOf( entity.get("metaid").toString());
		String busiId =  entity.get("billKey").toString();
		String userId = mongBean.getUserId();
		entity.put("userId", userId);
		String tenantId = mongBean.getTenantId();
		String id = mongBean.getObjectId();
		try {
			MetaCustom metaCustom = metaCustomService.getById(tenantId, metaid);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("metaid", metaid);
			List<MetaCustomField> listField = metaCustomFieldService.getList(tenantId, paramMap);
			//存储模版验证变量
			paramMap.put("tenantId", tenantId);
			paramMap.put("id", busiId);
			//流程设置变量
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(PropertyTools.getPropertyByKey("startUserId"), userId);
			variables.put(PropertyTools.getPropertyByKey("metaCustomId"), metaCustom.getId());
			variables.put(PropertyTools.getPropertyByKey("metaBusiId"), busiId);
			
			//判断TODO表中是否存在businessKey
			Map<String, Object> todoMap = new HashMap<String, Object>();
			todoMap.put("businessKey", id);
			BpmTodo todo = bpmTodoService.getByMap(tenantId, todoMap);
			if(null !=todo && !"-1".equals(todo.getInstanceid())){
				ajaxResult.setStatus("001");
				ajaxResult.setResult(id);
				ajaxResult.setInfo("该数据已存在流程!");
				return ajaxResult;
			}
			//获取流程id
			MetaBusi metaBusi = metaBusiService.getById(tenantId, Long.valueOf(busiId));
			//List<MetaTenant> metaTenantList = metaTenantService.getDeployModelListByMap(paramMap);
			//MetaTenant metaTenant = null;
			if (metaBusi.getFlowid()==null||"".equals(metaBusi.getFlowid())) {
				ajaxResult.setStatus("001");
				ajaxResult.setResult(id);
				ajaxResult.setInfo("没有找到此流程!");
				return ajaxResult;
			}/*else{
				metaTenant = metaTenantList.get(0);
				if (!metaTenant.getDeploystate().equals("1")) {
					ajaxResult.setStatus("001");
					ajaxResult.setResult(id);
					ajaxResult.setInfo(metaTenant.getModelname() + "流程尚未部署，无法进行操作。\n请部署该流程。");
					return ajaxResult;
				}
			}*/
			variables.put("userType", mongBean.getUserType());
			paramMap.put("leaveDay", entity.get("countday"));//临时存放
			setFlowVariablesMap(variables, paramMap);
			variables.put("submitUser", userId);
			// 保存单据
			if (saveOrsubmit.endsWith("1")) {
				id = metaBusiService.saveRestfulForm(id, entity, metaCustom, listField, tenantId, metaBusi.getFlowid(), variables);
				//记载办理轨迹
				BpmApprove bpmApprove=new BpmApprove();
				bpmApprove.setApprove("true");
				bpmApprove.setApproveresult("申请人提交申请");
				bpmApprove.setInstanceid(id);
				bpmApprove.setUserId(userId);
				bpmApprove.setApptype("制单");
				bpmApprove.setTaskid("填写申请");
				bpmApprove.setCreatetime(new Date());
				bpmApprove.setTenantId(Long.parseLong(tenantId));
				bpmApproveDao.save(bpmApprove);
				ajaxResult.setStatus("000");
				ajaxResult.setResult(id);
				ajaxResult.setInfo("提交成功!");
			} else {
				id = metaBusiService.saveRestfulForm(id, entity, metaCustom, listField, tenantId, busiId + "");
				ajaxResult.setStatus("000");
				ajaxResult.setResult(id);
				ajaxResult.setInfo("保存成功!");
			}
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setResult(id);
			ajaxResult.setInfo("操作失败!");
			e.printStackTrace();
		}
		return ajaxResult;
	}
	
	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @param request
	 * @return
	 */
	@RequestMapping("claimTask")
	@ResponseBody
	public AjaxResult claimTask(@RequestBody String jsonParam, HttpServletRequest request) {
		paraMap=MyJsonUtil.convertJsonToMap(jsonParam);
		try {
			String taskId= paraMap.get("taskId").toString();
			String userId= paraMap.get("userId").toString();
			workflowService.claimTask(taskId, userId);
			ajaxResult.setStatus("000");
			ajaxResult.setInfo("签收成功");
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setInfo("签收失败");
		}
		return ajaxResult;
	}
	
	/**
	 * 办理任务提交流程
	 * @param taskId
	 * @param approve
	 * @param approveResult
	 * @param request
	 * @return
	 */
	@RequestMapping("commitTask")
	@ResponseBody
	public AjaxResult commitTask(@RequestBody String jsonParam,HttpServletRequest request) {
		paraMap=MyJsonUtil.convertJsonToMap(jsonParam);
		try {
			String taskId= paraMap.get("taskId").toString();
			String tenantId= paraMap.get("tenantId").toString();
			String approve= paraMap.get("approve").toString();//是否审批通过 // true  送下一步/ false 退回操作 
			String approveResult= paraMap.get("approveResult").toString();
			bpmApproveService.completeTask(taskId, approve, approveResult,tenantId);
			ajaxResult.setStatus("000");
			ajaxResult.setInfo("提交成功");
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setInfo("提交失败" + e.getMessage());
		}
		return ajaxResult;
	}
	
	/**
	 * 驳回操作
	 * @param jsonParam
	 * @param request
	 * @return
	 */
	@RequestMapping("rejectTask")
	@ResponseBody
	public AjaxResult rejectTask(@RequestBody String jsonParam,HttpServletRequest request) {
		paraMap=MyJsonUtil.convertJsonToMap(jsonParam);
		try {
			String taskId= paraMap.get("taskId").toString();
			String taskKey= paraMap.get("targetTaskKey").toString();//驳回至节点
			String tenantId= paraMap.get("tenantId").toString();
			String approveResult= paraMap.get("approveResult").toString();//驳回原因
			ajaxResult.setResult(bpmApproveService.rejectTask(taskId, taskKey, approveResult,tenantId));
			ajaxResult.setStatus("000");
			ajaxResult.setInfo("驳回成功");
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setInfo("驳回失败" + e.getMessage());
		}
		return ajaxResult;
	}
	
	
	/**
	 * 收回操作 更新操作 只有申请人具有此操作
	 * @param jsonParam
	 * @param request
	 * @return
	 */
	@RequestMapping("backTask")
	@ResponseBody
	public AjaxResult backTask(@RequestBody String jsonParam,HttpServletRequest request) {
		paraMap=MyJsonUtil.convertJsonToMap(jsonParam);
		try {
			String userId= paraMap.get("userId").toString();
			String prco_inset_id= paraMap.get("prco_inset_id").toString();//实例id
			String tenantId= paraMap.get("tenantId").toString();
			ajaxResult.setResult(bpmApproveService.backTask(tenantId, prco_inset_id,userId));
			ajaxResult.setStatus("000");
			ajaxResult.setInfo("收回成功");
			//更新操作 只有申请人具有此操作
			paraMap.put("isback", 0);
			paraMap.put("isrevoke", 1);
			bpmTodoService.updateMyProcInst(paraMap);
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setInfo("收回失败：" + e.getMessage());
		}
		return ajaxResult;
	}
	
	/**
	 * 挂起/恢复流程
	 * @param opttype
	 * @param instanceid
	 * @param request
	 * @return
	 */
	@RequestMapping("isSuspended/{opttype}")
	@ResponseBody
	public AjaxResult isSuspended(@PathVariable("opttype") String opttype,@RequestBody String jsonParam, HttpServletRequest request) {
		paraMap=MyJsonUtil.convertJsonToMap(jsonParam);
		try {
			String instanceid = paraMap.get("prco_inset_id").toString();
			if (opttype.equals("active")) {
				workflowService.activateProcessInstanceById(instanceid);
			} else if (opttype.equals("suspend")) {
				workflowService.suspendProcessInstanceById(instanceid);
			}
			ajaxResult.setStatus("000");
			ajaxResult.setInfo("操作成功");
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setInfo("操作失败");
		}
		return ajaxResult;
	}
	
	/**
	 * 删除草稿箱内容
	 * @param jsonParam
	 * @param request
	 * @return
	 */
	@RequestMapping("delDraftData")
	@ResponseBody
	public AjaxResult delDraftData(@RequestBody String jsonParam, HttpServletRequest request) {
		paraMap=MyJsonUtil.convertJsonToMap(jsonParam);
		try {
			String businessKey = paraMap.get("businessKey").toString();
			String tenantId = paraMap.get("tenantId").toString();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("businessKey", businessKey);
			paramMap.put("tenantId", tenantId);
			paramMap.put("instanceid", "-1");
			bpmTodoService.deleteByConditions(tenantId, paramMap);
			ajaxResult.setStatus("000");
			ajaxResult.setInfo("删除成功");
		} catch (Exception e) {
			ajaxResult.setStatus("001");
			ajaxResult.setInfo("删除失败" + e.getMessage());
		}
		return ajaxResult;
	}
	
	/**
	 * 
	 * setFlowVariablesMap(根据不同流程设置各个流程模版需要的流程变量参数值)    <br/> 
	 * @user pengpeng.yuan@zymobi.com 
	 * @param   OAQueryActionController  <br/>  
	 * @return  void  <br/>
	 * @method  @param variables  paraMap<br/>  
	 * @Exception 异常对象    <br/>
	 */
	public void setFlowVariablesMap(Map<String, Object> variables, Map<String, Object> paraMap) {
		List<MetaTenant> metaTenantList = metaTenantService.getDeployModelListByMap(paraMap);
		// 循环设置模版的变量
		for (MetaTenant metaTenant : metaTenantList) {
			Long id = metaTenant.getId();
			if (id == 83) {// 请假模版
				// 请假天数
				variables.put("leaveDay", paraMap.get("leaveDay"));
				paraMap.remove("leaveDay");// 移除临时变量
			}
		}
	}
}
