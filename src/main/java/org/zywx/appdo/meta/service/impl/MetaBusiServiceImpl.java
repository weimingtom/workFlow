package org.zywx.appdo.meta.service.impl;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.appdo.common.DataGrid;
import org.zywx.appdo.common.core.biz.BaseBizImpl;
import org.zywx.appdo.common.core.dao.BaseDao;
import org.zywx.appdo.common.enums.EnableEnum;
import org.zywx.appdo.common.enums.FieldTodoEnum;
import org.zywx.appdo.common.enums.TodoVarEnum;
import org.zywx.appdo.common.exception.FlowBusinessRuntimeException;
import org.zywx.appdo.common.page.PageBean;
import org.zywx.appdo.common.page.PageParam;
import org.zywx.appdo.flow.dao.BpmTodoDao;
import org.zywx.appdo.flow.entity.BpmTodo;
import org.zywx.appdo.flow.entity.Staff;
import org.zywx.appdo.flow.service.WorkflowUserService;
import org.zywx.appdo.meta.dao.MetaBusiDao;
import org.zywx.appdo.meta.entity.MetaBusi;
import org.zywx.appdo.meta.entity.MetaCustom;
import org.zywx.appdo.meta.entity.MetaCustomField;
import org.zywx.appdo.meta.entity.MetaTemplate;
import org.zywx.appdo.meta.service.MetaBusiService;
import org.zywx.appdo.utils.MyStringUtils;
import org.zywx.appdo.utils.PropertyTools;

import com.alibaba.dubbo.common.json.JSONObject;

@Service
public class MetaBusiServiceImpl extends BaseBizImpl<MetaBusi>implements MetaBusiService {

	@Autowired
	private MetaBusiDao metaBusiDao;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private BpmTodoDao bpmTodoDao;
	@Autowired
	private WorkflowUserService userService;

	@Override
	protected BaseDao<MetaBusi> getDao() {
		return metaBusiDao;
	}

	/*
	 * description：保存单据模板 author：xingshen.zhao date：2015年10月23日
	 */
	@Override
	public Long saveMetaBusi(MetaBusi metaBusi, String parseForm) {
		try {
			metaBusi.setCreatetime(new Date());
			if (metaBusi.getId() == null) {
				metaBusi.setVersion(metaBusiDao.findMaxVersion() + 1);
			} else {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("busiid", metaBusi.getId());
				List<BpmTodo> list = bpmTodoDao.getList(paramMap);
				if (list != null && list.size() != 0) {
					metaBusi.setId(null);
					metaBusi.setVersion(metaBusiDao.findMaxVersion() + 1);
				}
			}
			UUID uuid = UUID.randomUUID();
			metaBusi.setEnable(EnableEnum.ENABLE.getValue());
			// 写入文件路径
			String path = PropertyTools.getPropertyByKey("filePath") + System.getProperty("file.separator")
					+ metaBusi.getTenantId();
			File file = new File(path);
			if (!file.exists())
				file.mkdir();
			String fileName = path + System.getProperty("file.separator") + uuid;
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
			out.write(parseForm);
			out.flush();
			out.close();
			metaBusi.setBusipath(fileName);
			return metaBusiDao.save(metaBusi);
		} catch (Exception e) {
			throw new FlowBusinessRuntimeException("模板保存失败", e);
		}
	}

	/*
	 * description：读取文件信息 author：xingshen.zhao date：2015年10月23日
	 */
	@Override
	public String findForm(String filepath) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStreamReader iSr = new InputStreamReader(new FileInputStream(filepath), "UTF-8");
			BufferedReader bufR = new BufferedReader(iSr);
			String line = null;
			while ((line = bufR.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String analysisVar(Class cla, Object entity, String str, Task task) {
		String regex = "\\$\\{(.*?)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		String var = null;
		String[] vars = null;
		List<Long> ids = null;
		String id = null;
		while (matcher.find()) {
			var = matcher.group(1);
			vars = var.split("\\.");
			if (vars.length == 0) {
				throw new FlowBusinessRuntimeException("变量为空");
			} else if (vars.length == 1) {
				if (var.equals(PropertyTools.getPropertyByKey("startUserId"))) {
					// 从流程变量中获取制单人id
					try {
						ids = new ArrayList<Long>();
						id = taskService.getVariable(task.getId(), PropertyTools.getPropertyByKey("startUserId"))
								.toString();
						ids.add(Long.parseLong(id));
						Staff staff = userService.getByUniqueField(id);
						var = staff.getFullName();
						if (var == null) {
							var = PropertyTools.getPropertyByKey("noUser");
						}
					} catch (Exception e) {
						var = PropertyTools.getPropertyByKey("noUser");
					}
				} else {
					// 读取元数据属性
					if (cla == null) {
						var = ((JSONObject) entity).getString(var);
					} else {
						var = invokeAttribute(cla, entity, var);
					}
				}
			} else {
				// 读取元数据关联系统管理的数据
				if (vars[0].equals(TodoVarEnum.VAR_USER.getValue())) {
					// 根据{user.id}获取指定用户vars[1]
					try {
						ids = new ArrayList<Long>();
						id = vars[1];
						ids.add(Long.parseLong(id));
						Staff staff = userService.getByUniqueField(id);
						var = staff.getFullName();
						if (var == null) {
							var = PropertyTools.getPropertyByKey("noUser");
						}
					} catch (Exception e) {
						var = PropertyTools.getPropertyByKey("noUser");
					}
				} else if (vars[0].equals(TodoVarEnum.VAR_DICT.getValue())) {
					// 根据{dict.key.value}方式获取字典表中的值
				} else if (vars[0].equals(TodoVarEnum.VAR_DEPT.getValue())) {
					try {
						// 根据{dept.id}方式获取部门名称vars[1]
						ids = new ArrayList<Long>();
						ids.add(Long.parseLong(vars[1]));
						Staff staff = userService.getByUniqueField(vars[1]);
						var = staff.getFullName();
					} catch (Exception e) {
						var = "未知部门";
					}
				} else if (vars[0].equals(TodoVarEnum.VAR_ENUMS.getValue())) {
					try {
						// ${ApptypeEnum.COMPLETE.0}
						cla = Class.forName("org.zywx.appdo.common.enums." + vars[1]);
						Method getValue = cla.getMethod("getValue");
						Method getName = cla.getMethod("getName");
						Object[] objs = cla.getEnumConstants();
						for (Object obj : objs) {
							if (vars[2].equals(getValue.invoke(obj))) {
								var = getName.invoke(obj).toString();
								continue;
							}
						}
					} catch (Exception e) {
						var = "未知枚举";
					}
				} else {
					throw new FlowBusinessRuntimeException("未知的变量类型");
				}

			}
			matcher.appendReplacement(sb, var);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 反射获取元数据中的数据
	 * 
	 * @param cla
	 * @param entity
	 * @param attribute
	 * @return
	 */
	private String invokeAttribute(Class cla, Object entity, String attribute) {
		try {
			PropertyDescriptor pd = new PropertyDescriptor(attribute, cla);
			Method m = pd.getReadMethod();// 获得get方法
			return m.invoke(entity).toString();
		} catch (Exception e) {
			throw new RuntimeException("读取元数据失败");
		}
	}

	@Override
	public String saveRestfulForm(String id, JSONObject json, MetaCustom metaCustom, List<MetaCustomField> listField,
			String tenantId, String key, Map<String, Object> variables) {
		try {
			Authentication
					.setAuthenticatedUserId(variables.get(PropertyTools.getPropertyByKey("startUserId")).toString());
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(key, id, variables,
					tenantId);
			Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskTenantId(tenantId)
					.singleResult();
			BpmTodo bpmTodo = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("businessKey", id);
			List<BpmTodo> listTodo = bpmTodoDao.getList(paramMap);
			if (listTodo != null && listTodo.size() != 0) {
				bpmTodo = listTodo.get(0);
			} else {
				bpmTodo = new BpmTodo();
			}
			for (MetaCustomField field : listField) {
				if (field.getFieldtodo().equals(FieldTodoEnum.NAME.getValue())) {
					bpmTodo.setTitle(analysisVar(null, json, field.getTodotemplate(), task));
				} else if (field.getFieldtodo().equals(FieldTodoEnum.CODE.getValue())) {
					bpmTodo.setCode(analysisVar(null, json, field.getTodotemplate(), task));
				}
				bpmTodo.setMetaid(field.getMetaid().toString());
			}
			taskService.complete(task.getId());
			if (bpmTodo.getTitle() == null) {
				bpmTodo.setTitle(PropertyTools.getPropertyByKey("toTitle"));
			}
			bpmTodo.setBusinessKey(id);
			bpmTodo.setInstanceid(processInstance.getId());
			bpmTodo.setTenantId(Long.parseLong(tenantId));
			bpmTodo.setCreatetime(MyStringUtils.now("YYYY-MM-DD HH:mm:SS"));
			bpmTodo.setBusiid(variables.get("metabusiid").toString());
			bpmTodo.setYear(MyStringUtils.getNowYear());
			bpmTodo.setUserId(json.getString("userId"));
			bpmTodo.setUserName(json.getString("user_info"));
			bpmTodoDao.save(bpmTodo);
			return processInstance.getId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlowBusinessRuntimeException(e);
		}
	}

	@Override
	public DataGrid dataGridBusi(String tenantId, long metaid, DataGrid dataGrid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sort", "version");
		paramMap.put("dir", "desc");
		paramMap.put("metaid", metaid);
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<MetaBusi> metaList = super.getPage(tenantId, pageParam, paramMap);
		dataGrid.setRows(metaList.getList());
		dataGrid.setTotal(metaList.getTotalCount());
		return dataGrid;
	}

	/*
	 * description：保存表单，提交流程 author：xingshen.zhao date：2015年10月28日
	 */
	@Override
	public String saveRestfulForm(String id, JSONObject json, MetaCustom metaCustom, List<MetaCustomField> listField,
			String tenantId, String metabusiid) {
		try {
			BpmTodo bpmTodo = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tenantId", tenantId);
			paramMap.put("businessKey", id);
			List<BpmTodo> listTodo = bpmTodoDao.getList(paramMap);
			if (listTodo != null && listTodo.size() != 0) {
				bpmTodo = listTodo.get(0);
			} else {
				bpmTodo = new BpmTodo();
			}
			for (MetaCustomField field : listField) {
				if (field.getFieldtodo().equals(FieldTodoEnum.NAME.getValue())) {
					bpmTodo.setTitle(
							((JSONObject) json).getString(field.getFieldcode()) + "未提交的" + metaCustom.getMetaname());
				} else if (field.getFieldtodo().equals(FieldTodoEnum.CODE.getValue())) {
					bpmTodo.setCode(((JSONObject) json).getString(field.getFieldcode()));
				}
				bpmTodo.setMetaid(field.getMetaid().toString());
			}
			if (bpmTodo.getTitle() == null) {
				bpmTodo.setTitle(PropertyTools.getPropertyByKey("toTitle"));
			}
			bpmTodo.setInstanceid("-1");
			bpmTodo.setBusinessKey(id);
			bpmTodo.setTenantId(Long.parseLong(tenantId));
			bpmTodo.setCreatetime(MyStringUtils.now("YYYY-MM-DD HH:mm:SS"));
			bpmTodo.setBusiid(metabusiid);
			bpmTodo.setYear(MyStringUtils.getNowYear());
			bpmTodo.setUserId(json.getString("userId"));
			bpmTodo.setUserName(json.getString("user_info"));
			bpmTodoDao.save(bpmTodo);
			return bpmTodo.getBusinessKey();
		} catch (Exception e) {
			throw new FlowBusinessRuntimeException(e);
		}
	}

	@Override
	public MetaTemplate getFileBlobByTemplateId(Map<String, Object> map) {
		return metaBusiDao.getFileBlobByTemplateId(map);
	}

}
