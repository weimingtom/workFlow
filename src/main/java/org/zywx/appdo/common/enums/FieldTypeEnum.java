package org.zywx.appdo.common.enums;

import java.util.HashMap;
import java.util.Map;
/**
 * 字段类别
 * @author 秦飞
 *
 */
public enum FieldTypeEnum {

	DATE("日期", "date"),TEXT("文本框", "input"), TEXTAREA("多行文本", "textarea"), SELECT("下拉菜单", "select"), CHECKBOX("复选框", "checkbox");
	private String name;
	
	private String value;
	
	private FieldTypeEnum(String name,String value) {
		this.name=name;
		this.value=value;
	}
	//覆盖方法
	@Override
	public String toString() {
		return this.value+"_"+this.name;
	}
	
	public String getName(){
		return name;
	}
	
	public String getValue(){
		return value;
	}
	
	public Map<String,String> getMap(){
		Map<String,String>	map=new HashMap<String, String>();
		map.put("name", name);
		map.put("value", value);
		return map;
	}
}
