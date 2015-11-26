package org.zywx.appdo.flow.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.cxf.common.i18n.Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.appdo.common.DataGrid;
import org.zywx.appdo.common.core.biz.BaseBizImpl;
import org.zywx.appdo.common.core.dao.BaseDao;
import org.zywx.appdo.common.page.PageBean;
import org.zywx.appdo.common.page.PageParam;
import org.zywx.appdo.flow.dao.BpmTodoDao;
import org.zywx.appdo.flow.entity.BpmTodo;
import org.zywx.appdo.flow.entity.RunProcessBean;
import org.zywx.appdo.flow.entity.Staff;
import org.zywx.appdo.flow.service.BpmTodoService;
import org.zywx.appdo.flow.service.WorkflowUserService;
import org.zywx.appdo.utils.MyStringUtils;
import org.zywx.appdo.workflow.WorkflowTaskService;

@Service
public class BpmTodoServiceImpl extends BaseBizImpl<BpmTodo> implements BpmTodoService {

	@Autowired
	private BpmTodoDao bpmTodoDao;
	@Autowired
	private WorkflowTaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private WorkflowUserService userService;

	@Override
	protected BaseDao<BpmTodo> getDao() {
		return bpmTodoDao;
	}

	/**
	 * 待签收数据
	 */
	@Override
	public DataGrid dataGridClaim(String tenantId, String userId, List<String> candidateGroups, DataGrid dataGrid) {
		// 任务查询
		TaskQuery query = taskService.createTaskQuery().taskTenantId(tenantId).or().taskCandidateUser(userId)
				.taskCandidateGroupIn(candidateGroups).endOr().orderByTaskCreateTime().desc().active();
		if (query == null || query.list() == null || query.count() == 0) {
			return dataGrid;
		}
		// 总数
		// 分页10条
		List<Task> claimList = query.list();
		List<BpmTodo> listTodo = new ArrayList<BpmTodo>();
		BpmTodo bpmTodo = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (Task task : claimList) {
			paramMap.put("instanceid", task.getProcessInstanceId());
			bpmTodo = super.getByMap(tenantId, paramMap);
			if (bpmTodo == null){
				continue;
			}
			bpmTodo.setTaskId(task.getId());
			bpmTodo.setUrl(task.getFormKey());
			bpmTodo.setNodeName(task.getName());
			bpmTodo.setCreatetime(bpmTodo.getCreatetime());
			bpmTodo.setSubmitAt(MyStringUtils.getDateStr(task.getCreateTime()));
			bpmTodo.setSubmitUser(getLastUserInfo(task.getProcessInstanceId()));
			listTodo.add(bpmTodo);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = new PageBean<BpmTodo>(pageParam, claimList.size(), listTodo);
		dataGrid.setRows(listTodo);
		dataGrid.setTotal( claimList.size());
		dataGrid.setPageCount(metaList.getTotalPage());
		return dataGrid;
	}

	/**
	 * 待办数据
	 */
	@Override
	public DataGrid dataGridTodo(String tenantId, String userId, DataGrid dataGrid) {
		TaskQuery query = taskService.createTaskQuery().taskTenantId(tenantId).taskAssignee(userId)
				.orderByTaskCreateTime().desc().active();
		if (query == null || query.list() == null || query.count() == 0) {
			return dataGrid;
		}
		// 分页10条
		List<Task> claimList = query.list();
		List<BpmTodo> listTodo = new ArrayList<BpmTodo>();
		BpmTodo bpmTodo = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (Task task : claimList) {
			paramMap.put("instanceid", task.getProcessInstanceId());
			bpmTodo = super.getByMap(tenantId, paramMap);
			if (null == bpmTodo) {
				continue;
			}
			if (userId.equals(bpmTodo.getUserId()))
				continue;
			bpmTodo.setTaskId(task.getId());
			bpmTodo.setUrl(task.getFormKey());
			bpmTodo.setNodeName(task.getName());
			bpmTodo.setSubmitUser(getLastUserInfo(task.getProcessInstanceId()));
			bpmTodo.setSubmitAt(MyStringUtils.dateToString(task.getCreateTime(), "YYYY-MM-dd HH:mm:ss"));
			listTodo.add(bpmTodo);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = new PageBean<BpmTodo>(pageParam, claimList.size(), listTodo);
		dataGrid.setRows(listTodo);
		dataGrid.setTotal(claimList.size());
		dataGrid.setPageCount(metaList.getTotalPage());
		return dataGrid;
	}

	/**
	 * 已办数据
	 */
	@Override
	public DataGrid dataGridApproved(String tenantId, String userId, DataGrid dataGrid) {

		HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskTenantId(tenantId)
				.taskAssignee(userId).finished().orderByTaskCreateTime().desc();
		if (query == null || query.list() == null || query.count() == 0) {
			return dataGrid;
		}
		// 总数
		List<HistoricTaskInstance> hisList = query.list();
		List<BpmTodo> listHis = new ArrayList<BpmTodo>();
		BpmTodo bpmTodo = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (HistoricTaskInstance task : hisList) {
			paramMap.put("instanceid", task.getProcessInstanceId());
			bpmTodo = super.getByMap(tenantId, paramMap);
			if (bpmTodo == null || userId.equals(bpmTodo.getUserId())){
				continue;
			}
			bpmTodo.setTaskId(task.getId());
			bpmTodo.setUrl(task.getFormKey());
			bpmTodo.setSubmitUser(getLastUserInfo(task.getProcessInstanceId()));
			bpmTodo.setNodeName(task.getName());
			bpmTodo.setSubmitAt(MyStringUtils.getDateStr(task.getEndTime()));
			bpmTodo.setEndTime(MyStringUtils.getDateStr(task.getEndTime()));
			listHis.add(bpmTodo);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = new PageBean<BpmTodo>(pageParam, hisList.size(), listHis);
		dataGrid.setRows(listHis);
		dataGrid.setTotal(hisList.size());
		dataGrid.setPageCount(metaList.getTotalPage());
		return dataGrid;
	}

	@Override
	public BpmTodo queryBpmTodoByTaskId(String tenantId, String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("instanceid", task.getProcessInstanceId());
		List<BpmTodo> list = super.getList(tenantId, paramMap);
		if (list != null && list.size() != 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 加载数据 ,根据参数设置是否草稿 办理
	 */
	@Override
	public DataGrid dataGridDraft(Map<String, Object> paraMap, DataGrid dataGrid) {
		Map<String, Object> paramMap = paraMap;
		String instanceid = "-1";
		paramMap.put("instanceid", instanceid);
		paramMap.put("sort", "createtime");
		paramMap.put("dir", "desc");
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = super.getPage(paraMap.get("tenantId").toString(), pageParam, paramMap);
		dataGrid.setRows(metaList.getList());
		dataGrid.setTotal(metaList.getTotalCount());
		dataGrid.setPageCount(metaList.getTotalPage());
		return dataGrid;
	}

	/**
	 * 获取我提交的数据
	 */
	@Override
	public DataGrid dataGridSubmit(String tenantId, String userId, DataGrid dataGrid) {
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
				.processInstanceTenantId(tenantId).startedBy(userId).orderByProcessInstanceStartTime().desc();
		if (query == null || query.list() == null || query.count() == 0) {
			return dataGrid;
		}
		// 总数
		List<HistoricProcessInstance> submitList = query.list();
		List<BpmTodo> listTodo = new ArrayList<BpmTodo>();
		BpmTodo bpmTodo = null;
		List<Task> tasks = null;
		String name = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (HistoricProcessInstance process : submitList) {
			name = null;
			paramMap.put("instanceid", process.getId());
			bpmTodo = super.getByMap(tenantId, paramMap);
			if (bpmTodo == null){
				continue;
			}
			tasks = taskService.createTaskQuery().processInstanceId(process.getId()).list();
			if (tasks != null && tasks.size() != 0) {
				for (Task task : tasks) {
					if (name != null) {
						name += "," + task.getName();
					} else {
						name = task.getName();
					}
				}
				bpmTodo.setNodeName(name);
			}
			listTodo.add(bpmTodo);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = new PageBean<BpmTodo>(pageParam, submitList.size(), listTodo);
		dataGrid.setPageCount(metaList.getTotalPage());
		dataGrid.setRows(listTodo);
		dataGrid.setTotal(submitList.size());
		return dataGrid;
	}

	/**
	 * 查询指定用户发起的流程 （流程历史 用户发起 结束/未结束）
	 */
	@Override
	public DataGrid getMyIsUnfinishTaskList(Map<String, Object> paraMap, DataGrid dataGrid) {
		String userId = paraMap.get("userId").toString();
		String tenantId = paraMap.get("tenantId").toString();
		String isFinished = paraMap.get("isFinished").toString();
		// finished --> 完成的流程 unfinish --> 还在运行中的流程
		List<HistoricProcessInstance> myList = null;
		HistoricProcessInstanceQuery query = null;
		if ("1".equals(isFinished)) {// 已完成
			query = historyService.createHistoricProcessInstanceQuery().finished().startedBy(userId)
					.orderByProcessInstanceStartTime().desc();
			myList = query.list();
		} else {// 未完成
			query = historyService.createHistoricProcessInstanceQuery().unfinished().startedBy(userId)
					.orderByProcessInstanceStartTime().desc();
			myList = query.list();
		}
		List<RunProcessBean> list = new ArrayList<RunProcessBean>();
		// 查询数据
		Map<String, Object> querymap = new HashMap<String, Object>();
		querymap.put("tenantId", tenantId);
		RunProcessBean runProcessBean = null;
		for (HistoricProcessInstance process : myList) {
			runProcessBean = new RunProcessBean();
			querymap.put("instanceid", process.getId());
			BpmTodo todo = super.getByMap(tenantId, querymap);
			if (todo == null || todo.getIsback().equals("1") || todo.getIsrevoke().equals("1")){
				continue;
			}
			runProcessBean.setTitle(todo.getTitle());
			runProcessBean.setBusiName(todo.getBusiname());
			runProcessBean.setBusinessKey(process.getBusinessKey());
			runProcessBean.setProcName(process.getName());
			runProcessBean.setProcId(process.getId());
			runProcessBean.setDefId(process.getProcessDefinitionId());
			runProcessBean.setCreatetime(todo.getCreatetime());
			runProcessBean.setBusiid(todo.getBusiid());
			runProcessBean.setMetaid(todo.getMetaid());
			runProcessBean.setTenantId(todo.getTenantId().toString());
			if ("1".equals(isFinished)) {
				runProcessBean.setEndTime(MyStringUtils.dateToString(process.getEndTime(), "YYYY-MM-dd HH:mm:ss"));
			}
			list.add(runProcessBean);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<RunProcessBean> metaList = new PageBean<RunProcessBean>(pageParam, list.size(), list);
		dataGrid.setRows(list);
		dataGrid.setTotal(list.size());
		dataGrid.setPageCount(metaList.getTotalPage());

		return dataGrid;
	}

	/**
	 * 得到上一步的提交人
	 * 
	 * @param TaskDefinitionKey
	 * @return
	 */
	private String getLastUserInfo(String instanceid) {
		List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(instanceid).variableName("submitUser").list();
		if (list != null && list.size() > 0) {
			HistoricVariableInstance dataInfo = list.get(0);
			if (dataInfo.getValue() != null) {
				Staff staff = userService.getByUniqueField(dataInfo.getValue().toString());
				return staff.getFullName();
			} else {
				return "";
			}
		}
		return "";
	}

	/**
	 * 
	 * updateMyProcInst(更新 收回 被退回数据) <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param BpmTodoService
	 *            <br/>
	 * @return void <br/>
	 * @method @param paraMap{isback,isrevoke,prco_inset_id} <br/>
	 * @Exception 异常对象 <br/>
	 */
	@Override
	public void updateMyProcInst(Map<String, Object> paraMap) throws Exception {
		if (null == paraMap.get("isback") || null == paraMap.get("isrevoke") || null == paraMap.get("prco_inset_id")) {
			throw new NullPointerException("更新错误，参数有值为空！");
		} else {
			// 更新
			getDao().updateBySqlId(paraMap, "updateMyProcInst");
		}
	}

	/**
	 * getCommitBackToMyProcList(申请人查看我收回数据) bpm_todo.isrevoke=1 <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param OAQueryDataController
	 *            <br/>
	 * @return String <br/>
	 * @method @param request
	 * @method @param jsonParam isback=1 被退回
	 * @method @return <br/>
	 * @Exception 异常对象 <br/>
	 */
	@Override
	public DataGrid getRevokeBackToMyProcList(Map<String, Object> paramMap, DataGrid dataGrid) {
		String tenantId = paramMap.get("tenantId").toString();
		String userId = paramMap.get("userId").toString();
		TaskQuery query = taskService.createTaskQuery().taskTenantId(tenantId).taskAssignee(userId)
				.orderByTaskCreateTime().desc().active();
		List<Task> claimList = query.list();
		if (query == null || query.list() == null || query.count() == 0) {
			return dataGrid;
		}
		List<BpmTodo> listTodo = new ArrayList<BpmTodo>();
		BpmTodo bpmTodo = null;
		String isrevoke = paramMap.get("isrevoke").toString();
		for (Task task : claimList) {
			paramMap.put("instanceid", task.getProcessInstanceId());
			bpmTodo = super.getByMap(tenantId, paramMap);
			if (null == bpmTodo) {
				continue;
			}
			if ("1".equals(isrevoke) && bpmTodo.getIsrevoke().equals(isrevoke)) {
				bpmTodo.setTaskId(task.getId());
				bpmTodo.setUrl(task.getFormKey());
				bpmTodo.setNodeName(task.getName());
				bpmTodo.setSubmitUser(getLastUserInfo(task.getProcessInstanceId()));
				bpmTodo.setSubmitAt(MyStringUtils.getDateStr(task.getCreateTime()));
			}
			listTodo.add(bpmTodo);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = new PageBean<BpmTodo>(pageParam, claimList.size(), listTodo);
		dataGrid.setRows(listTodo);
		dataGrid.setTotal(claimList.size());
		dataGrid.setPageCount(metaList.getTotalPage());
		return dataGrid;
	}

	/**
	 * getCommitBackToMyProcList(申请人查看被退回数据) bpm_todo.isback=1 <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param OAQueryDataController
	 *            <br/>
	 * @return String <br/>
	 * @method @param request
	 * @method @param jsonParam isback=1 被退回
	 * @method @return <br/>
	 * @Exception 异常对象 <br/>
	 */
	@Override
	public DataGrid getCommitBackToMyProcList(Map<String, Object> paramMap, DataGrid dataGrid) {
		String tenantId = paramMap.get("tenantId").toString();
		String userId = paramMap.get("userId").toString();
		TaskQuery query = taskService.createTaskQuery().taskTenantId(tenantId).taskAssignee(userId)
				.orderByTaskCreateTime().desc().active();
		List<Task> claimList = query.list();
		if (query == null || query.list() == null || query.count() == 0) {
			return dataGrid;
		}
		List<BpmTodo> listTodo = new ArrayList<BpmTodo>();
		BpmTodo bpmTodo = null;
		String isback = paramMap.get("isback").toString();
		for (Task task : claimList) {
			paramMap.put("instanceid", task.getProcessInstanceId());
			bpmTodo = super.getByMap(tenantId, paramMap);
			if (null == bpmTodo) {
				continue;
			}
			if ("1".equals(isback) && bpmTodo.getIsback().equals(isback)) {
				bpmTodo.setTaskId(task.getId());
				bpmTodo.setUrl(task.getFormKey());
				bpmTodo.setNodeName(task.getName());
				bpmTodo.setSubmitUser(getLastUserInfo(task.getProcessInstanceId()));
				bpmTodo.setSubmitAt(MyStringUtils.getDateStr(task.getCreateTime()));
			}
			listTodo.add(bpmTodo);
		}
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<BpmTodo> metaList = new PageBean<BpmTodo>(pageParam, claimList.size(), listTodo);
		dataGrid.setPageCount(metaList.getTotalPage());
		dataGrid.setRows(listTodo);
		dataGrid.setTotal(claimList.size());
		return dataGrid;
	}
}
