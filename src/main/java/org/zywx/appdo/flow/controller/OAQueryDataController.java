package org.zywx.appdo.flow.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.appdo.common.AjaxResult;
import org.zywx.appdo.common.DataGrid;
import org.zywx.appdo.flow.entity.Staff;
import org.zywx.appdo.flow.service.BpmApproveService;
import org.zywx.appdo.flow.service.BpmTodoService;
import org.zywx.appdo.flow.service.WorkflowService;
import org.zywx.appdo.flow.service.WorkflowUserService;
import org.zywx.appdo.meta.entity.MetaBusi;
import org.zywx.appdo.meta.entity.MetaCustomField;
import org.zywx.appdo.meta.entity.MetaTemplate;
import org.zywx.appdo.meta.service.MetaBusiService;
import org.zywx.appdo.meta.service.MetaCustomFieldService;
import org.zywx.appdo.utils.MyJsonUtil;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

/**
 * 工作流对外提供数据接口
 * 
 * @author zorro
 *
 */
@Controller
@RequestMapping(value = "/workFlowData")
public class OAQueryDataController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	// 返回状态码 000 成功 001 失败
	private String status;
	private String returnJsonString;// 返回json格式
	private String result;// 返回字符串
	private String exception="";//返回异常
	private int rows = 10;
	private int page = 1;
	Map<String, Object> paraMap = null;
	// 初始化服务
	@Autowired
	private BpmTodoService bpmTodoService;
	@Autowired
	private MetaBusiService metaBusiService;// 单据类型信息服务
	@Autowired
	private MetaCustomFieldService metaCustomFieldService;// 单据类型字段服务
	@Autowired
	private WorkflowUserService userService;
	@Autowired
	private BpmApproveService bpmApproveService;
	@Autowired
	private WorkflowService workflowService;
	// 变量集合map
	private Map<String, Object> map = new HashMap<String, Object>();
	AjaxResult ajaxResult = new AjaxResult();

	/**
	 * 返回到所有单据类型
	 * tenantId--租户id
	 * @param users
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getBillTypeList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getBillTypeList(@RequestBody String jsonParam, HttpServletRequest request,
			HttpServletResponse response) {
		List<MetaBusi> retList = null;
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			// 格式转码
			response.setCharacterEncoding("utf-8");
			retList=metaBusiService.getList(paraMap.get("tenantId").toString(), null);
			status = "000";
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			returnJsonString = MyJsonUtil.loadJsonArrayDataByList(retList, status,exception);
		}
		return returnJsonString;
	}

	/**
	 * 根据单据id得到类型模版
	 * billKey---业务流程对应单据id
	 * tenantId---业务流程id
	 * metaid---流程id
	 * @param users
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getBillTypeFileById", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getBillTypeFileById(@RequestBody String jsonParam, HttpServletRequest request,
			HttpServletResponse response) {
		paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
		// 格式转码
		map.put("id", paraMap.get("billKey"));
		map.put("tenantID", paraMap.get("tenantId"));
		map.put("metaid", paraMap.get("metaid"));// 对应模版id
		MetaTemplate template = metaBusiService.getFileBlobByTemplateId(map);
		returnJsonString = template.getId() + "notezymobi.com" + template.getFileValue();
		return returnJsonString;
	}

	/**
	 * 获取字段列表json信息
	 * tenantId---业务流程id
	 * metaid---流程id
	 * @param para
	 * @param metaid
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getBillTypeTemplateFileds", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getBillTypeTemplateFileds(@RequestBody String jsonParam, HttpServletRequest request,
			HttpServletResponse response) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				dataGrid.setPageSize(100);
				dataGrid.setPage(1);
				String metaId =paraMap.get("metaid").toString();
				String tenantId = paraMap.get("tenantId").toString();
				dataGrid = metaCustomFieldService.getCustomFieldApp(tenantId, Long.valueOf(metaId), dataGrid);
				status = "000";
			}
			status = "000";
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			returnJsonString = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return returnJsonString;
	}

	/**
	 * 获取草稿箱列表
	 * 
	 * @param request
	 * @param dataGrid
	 * @param rows
	 * @return
	 */
	@RequestMapping(value = "getDraftList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getDraftList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				dataGrid = bpmTodoService.dataGridDraft(paraMap, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

	/**
	 * 得到待签收列表
	 * 
	 * @param para
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getClaimList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getClaimList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				String userId =  paraMap.get("userId").toString();
				String tenantId =  paraMap.get("tenantId").toString();
				List<String> groupList = new ArrayList<String>();
				Staff staff = userService.getByUniqueField(userId);
				groupList.add(staff.getDptId());
				dataGrid = bpmTodoService.dataGridClaim(tenantId, userId, groupList, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

	/**
	 * 得到待办列表
	 * @param request
	 * @param jsonParam
	 * @return
	 */
	@RequestMapping(value = "getTodoList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getTodoList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				String userId =  paraMap.get("userId").toString();
				String tenantId =  paraMap.get("tenantId").toString();
				dataGrid = bpmTodoService.dataGridTodo(tenantId, userId, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

	/**
	 * 获取已办列表数据
	 * 
	 * @param request
	 * @param jsonParam
	 * @return
	 */
	@RequestMapping(value = "getApprovedList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getApprovedList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				String userId =  paraMap.get("userId").toString();
				String tenantId =  paraMap.get("tenantId").toString();
				dataGrid = bpmTodoService.dataGridApproved(tenantId, userId, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

	/**
	 * 获取审批历史
	 * 
	 * @param request
	 * @param jsonParam
	 * @return
	 */
	@RequestMapping(value = "getHistoryList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getHistoryList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				String userId =  paraMap.get("userId").toString();
				String tenantId =  paraMap.get("tenantId").toString();
				String instanceId =  paraMap.get("prco_inset_id").toString();
				dataGrid = bpmApproveService.dataGridApprove(userId, tenantId, instanceId, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

	/**
	 * 执行中流程实例接口
	 * @param request
	 * @param dataGrid
	 * @param rows
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "getProcessInstanceList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getProcessInstanceList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				paraMap.put("isFinished", "0");
				dataGrid = bpmTodoService.getMyIsUnfinishTaskList(paraMap, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}
	
	/**
	 * 已完成流程实例接口
	 * @param request
	 * @param dataGrid
	 * @param rows
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "getProcessInstanceHisList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getProcessInstanceHisList(HttpServletRequest request, @RequestBody String jsonParam){
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				paraMap.put("isFinished", "1");
				dataGrid = bpmTodoService.getMyIsUnfinishTaskList(paraMap, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

	/**
	 * 我提交的数据接口
	 * @param request
	 * @param dataGrid
	 * @param rows
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "getMySubmitList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getMySubmitList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				String userId =  paraMap.get("userId").toString();
				String tenantId =  paraMap.get("tenantId").toString();
				dataGrid =  bpmTodoService.dataGridSubmit(tenantId,userId, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}
	
	/**
	 * 流程信息列表
	 * @param request
	 * @param dataGrid
	 * @param rows
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "getProcessDefList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getProcessDefList(HttpServletRequest request, @RequestBody String jsonParam) {
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				String tenantId =  paraMap.get("tenantId").toString();
				String id =  paraMap.get("prco_inset_id").toString();
				dataGrid =  workflowService.dataGridProcessDef(tenantId, id, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		
		return result;
	}
	
	/**
	 * 加载可以驳回的节点列表
	 * @param request
	 * @param dataGrid
	 * @param rows
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "getToRejectTaskList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getToRejectTaskList(HttpServletRequest request, @RequestBody String jsonParam) {
		List<Map<String, Object>> list=null;
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				String taskId =  paraMap.get("prco_inset_id").toString();
				list = workflowService.toRejectTask(taskId);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayDataByList(list, status,exception);
		}
		return result;
	}
	
	/**
	 * 查询流程图片
	 * 
	 * @param request
	 * @param jsonParam
	 * @return
	 */
	@RequestMapping(value = "getFlowImage", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getFlowImage(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonParam) {
		paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
		// 查询执行节点
		String executionId =  paraMap.get("prco_inset_id").toString();
		InputStream imageStream = workflowService.readResource(executionId);
		// 输出资源内容到相应对象
		byte[] b = null;
		try { 
			b = new byte[imageStream.available()];
			imageStream.read(b);
			BASE64Encoder encoder = new BASE64Encoder();
			result = encoder.encode(b);// 返回Base64编码过的字节数组字符串
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				imageStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 
	 * getMyInvolvedProcList(得到我参与的流程)    <br/> 
	 * @user pengpeng.yuan@zymobi.com 
	 * @param   OAQueryDataController  <br/>  
	 * @return  String  <br/>
	 * @method  @param request
	 * @method  @param jsonParam
	 * @method  @return  <br/>  
	 * @Exception 异常对象    <br/>
	 */
	@RequestMapping(value = "getMyInvolvedProcList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getMyInvolvedProcList(HttpServletRequest request, @RequestBody String jsonParam){
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")&&!"".equals(paraMap.get("rowCnt"))) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")&&!"".equals(paraMap.get("pageNo"))) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				dataGrid = bpmTodoService.getMyIsUnfinishTaskList(paraMap, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}
	
	/**
	 * 被退回的数据<br/>
	 * isback是否被退回 数据 isback=1查看被退回<br/>
	 * isrevoke是否是我收回的数据 isrevoke=1查看我收回<br/>
	 * 不能出现isback isrevoke值都为1的情况
	 * getCommitBackToMyProcList(申请人查看被退回数据) bpm_todo.isback=1    <br/> 
	 * @user pengpeng.yuan@zymobi.com 
	 * @param   OAQueryDataController  <br/>  
	 * @return  String  <br/>
	 * @method  @param request
	 * @method  @param jsonParam isback=1 被退回
	 * @method  @return  <br/>  
	 * @Exception 异常对象    <br/>
	 */
	@RequestMapping(value = "getCommitBackToMyProcList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getCommitBackToMyProcList(HttpServletRequest request, @RequestBody String jsonParam){
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				//审批人退回的时候要设置isback=0入库
				//isback是否被退回 数据  isback=1查看被退回
				//isrevoke是否是我收回的数据  isrevoke=1查看我收回
				//当isback isrevoke值都为0的时候，就是查看正常待办数据
				//再次版办理 收回 被退回数据的时候 ，在办理环节中将两个值都设置为0入库 以便出现多次退回数据
				//不能出现isback isrevoke值都为1的情况
				dataGrid = bpmTodoService.getCommitBackToMyProcList(paraMap, dataGrid);
				status = "000";
			}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}
	
	/**
	 * 我收回的数据<br/>
	 * isback是否被退回 数据 isback=1查看被退回<br/>
	 * isrevoke是否是我收回的数据 isrevoke=1查看我收回<br/>
	 * 不能出现isback isrevoke值都为1的情况
	 * getCommitBackToMyProcList(申请人查看我收回数据) bpm_todo.isrevoke=1    <br/> 
	 * @user pengpeng.yuan@zymobi.com 
	 * @param   OAQueryDataController  <br/>  
	 * @return  String  <br/>
	 * @method  @param request
	 * @method  @param jsonParam isback=1 被退回
	 * @method  @return  <br/>  
	 * @Exception 异常对象    <br/>
	 */
	@RequestMapping(value = "getRevokeBackToMyProcList", method = RequestMethod.POST, produces = "plain/text; charset=UTF-8")
	@ResponseBody
	public String getRevokeBackToMyProcList(HttpServletRequest request, @RequestBody String jsonParam){
		DataGrid dataGrid = new DataGrid();
		try {
			paraMap = MyJsonUtil.JsonToMap(JSONObject.fromObject(jsonParam));
			if (null != paraMap) {
				if (null != paraMap.get("rowCnt")) {
					rows = Integer.parseInt(paraMap.get("rowCnt").toString());
				}
				if (null != paraMap.get("pageNo")) {
					page = Integer.parseInt(paraMap.get("pageNo").toString());
				}
				dataGrid.setPageSize(rows);
				dataGrid.setPage(page);
				//审批人退回的时候要设置isback=0入库
				//isback是否被退回 数据  isback=1查看被退回
				//isrevoke是否是我收回的数据  isrevoke=1查看我收回
				//当isback isrevoke值都为0的时候，就是查看正常待办数据
				//再次版办理 收回 被退回数据的时候 ，在办理环节中将两个值都设置为0入库 以便出现多次退回数据
				//不能出现isback isrevoke值都为1的情况
				dataGrid = bpmTodoService.getRevokeBackToMyProcList(paraMap, dataGrid);
				status = "000";
				}
		} catch (Exception e) {
			status = "001";
			exception=e.getMessage();
		}finally {
			result = MyJsonUtil.loadJsonArrayPagrDataByList(dataGrid, status,exception);
		}
		return result;
	}

}
