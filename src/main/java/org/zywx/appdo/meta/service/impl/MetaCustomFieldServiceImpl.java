package org.zywx.appdo.meta.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.appdo.bean.QueryWorkFlowAndFieldBean;
import org.zywx.appdo.common.DataGrid;
import org.zywx.appdo.common.core.biz.BaseBizImpl;
import org.zywx.appdo.common.core.dao.BaseDao;
import org.zywx.appdo.common.page.PageBean;
import org.zywx.appdo.common.page.PageParam;
import org.zywx.appdo.meta.dao.MetaCustomFieldDao;
import org.zywx.appdo.meta.entity.MetaCustomField;
import org.zywx.appdo.meta.service.MetaCustomFieldService;

@Service
public class MetaCustomFieldServiceImpl extends BaseBizImpl<MetaCustomField> implements MetaCustomFieldService {

	@Autowired
	private MetaCustomFieldDao metaCustomFieldDao;

	@Override
	protected BaseDao<MetaCustomField> getDao() {
		return metaCustomFieldDao;
	}

	@Override
	public DataGrid dataGridMetaCustomField(String tenantId, long metaid, DataGrid dataGrid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sort", "createtime");
		paramMap.put("dir", "desc");
		paramMap.put("metaid", metaid);
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<MetaCustomField> metaList = super.getPage(tenantId, pageParam, paramMap);
		dataGrid.setRows(metaList.getList());
		dataGrid.setTotal(metaList.getTotalCount());
		return dataGrid;
	}

	@Override
	public List<MetaCustomField> getTemplateFieldSByIdAndMetaId(Map<String, Object> map) {
		// TODO 描述此功能
		return metaCustomFieldDao.getTemplateFieldSByIdAndMetaId(map);
	}

	/**
	 * 返回字段类型列表
	 */
	@Override
	public DataGrid getCustomFieldApp(String tenantId, long metaid, DataGrid dataGrid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sort", "id");
		paramMap.put("dir", "asc");
		paramMap.put("metaid", metaid);
		PageParam pageParam = new PageParam(dataGrid.getPage(), dataGrid.getPageSize());
		PageBean<MetaCustomField> metaList = super.getPage(tenantId, pageParam, paramMap);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 查询数据
		Map<String, Object> map;
		for (MetaCustomField fields : metaList.getList()) {
			map = new HashMap<String, Object>();
			map.put("fieldCode", fields.getFieldcode());
			map.put("fieldName", fields.getFieldname());
			map.put("fieldType", fields.getFieldtype());
			map.put("ifEdit", fields.getIfEdit());
			map.put("ifVisible", fields.getIfVisible());
			map.put("id", fields.getId());
			map.put("ifAuto", fields.getIfAuto());
			map.put("defaultValue", fields.getDefaultValue());
			map.put("isOpinion", fields.getIsOpinion());
			list.add(map);
		}
		dataGrid.setRows(list);
		dataGrid.setTotal(list.size());
		dataGrid.setPageCount(1);
		return dataGrid;

	}

	/**
	 * 
	 * addWorkFlowNodesByModelId(增加流程绑定表单模版方法) <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param MetaCustomFieldService
	 *            <br/>
	 * @return void <br/>
	 * @method @param map <br/>
	 * @Exception 异常对象 <br/>
	 */
	public void addWorkFlowNodesByModelId(Map<String, Object> map) {

	}

	/**
	 * 
	 * getWorkFlowNodesList(查询流程绑定字段信息 ，主要传给 app 和 pc) <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param MetaCustomFieldService
	 *            <br/>
	 * @return List<QueryWorkFlowAndFieldBean> <br/>
	 * @method @param map
	 * @method @return <br/>
	 * @Exception 异常对象 <br/>
	 */
	public List<QueryWorkFlowAndFieldBean> getWorkFlowNodesList(Map<String, Object> map) {
		return null;
	}

	/**
	 * 
	 * updateQueryWorkFlowAndFieldBean(更新流程节点与意见字段信息) <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param MetaCustomFieldService
	 *            <br/>
	 * @return void <br/>
	 * @method @param map <br/>
	 * @Exception 异常对象 <br/>
	 */
	public void updateQueryWorkFlowAndFieldBean(Map<String, Object> map) {

	}

	/**
	 * 
	 * deleteQueryWorkFlowAndFieldBean(删除流程节点与意见字段信息) <br/>
	 * 
	 * @user pengpeng.yuan@zymobi.com
	 * @param MetaCustomFieldService
	 *            <br/>
	 * @return void <br/>
	 * @method @param map <br/>
	 * @Exception 异常对象 <br/>
	 */
	public void deleteQueryWorkFlowAndFieldBean(Map<String, Object> map) {

	}

}
