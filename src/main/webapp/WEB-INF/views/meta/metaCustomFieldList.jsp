<%@ page language="java" pageEncoding="utf-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="../../../common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>元数据字段管理</title>
<%@ include file="../../../common/common.jsp"%>

</head>
<body>
	<div class="easyui-layout" style="width: 100%; height: 100%;"
		fit="true">
	<div region="north" split="false" border="false" class="st_head">
		<div class="titleTop">
			<span><img class="titleImage" src="${ctx }/static/images/daohang.png">
			</span>
			<span>元数据管理->元数据->字段</span>
		</div>
	</div>
	<div  region="center" style=" overflow-y: hidden;padding:5px;">
	<table id="metaCustomFieldTable" class="easyui-datagrid" 
	    url="${ctx}/settingMeta/dataGridMetaCustomField/${id}" //数据加载路径 
        title="字段管理" //表格标题
        iconCls="icon-table"//表格图标
        rownumbers="true" 		//是否显示行号
        pagination="true" 		//是否使用分页
        toolbar="#toolbar"  	//工具栏绑定
        pageList="[10,20,30]" 	//页大小选择
        pageSize="10" 			//每页的条数
        striped="true" 			//行条纹
        collapsible="false"  	//显示可折叠按钮
        loadMsg="正在加载，请稍后"	//加载提示
        singleSelect="true"		//是否只能选择单行
        fitColumns="true"
        fit="true"
        >
	    <thead >
	        <tr align="center">
	            <!-- <th field="ck"  checkbox="true" ></th> -->
	            <th field="id" hidden="true">id</th> 
<!-- 	            <th field="ifVisible" hidden="true">ifVisible</th> 
	            <th field="ifEdit" hidden="true">ifEdit</th> 
	            <th field="ifAuto" hidden="true">ifAuto</th>
	            <th field="ifVariable" hidden="true">ifVariable</th> -->
	            <th field="fieldname" width="15%" >字段名称</th>
	            <th field="fieldcode" width="15%">字段编码</th>
	            <!-- <th field="fieldtype" width="30%" >字段类型</th> -->
	            <th field="todotemplate" width="30%" >待办模板</th>
	            <th field="defaultValue" width="30%" >默认值</th>
	            <th field="createtime" width="30%" >创建日期</th>
	            <th field="_operate" formatter="formatOper" width="10%">操作</th><!-- 自定义列  -->
			</tr>
	    </thead>
	</table>
	<!-- <div id="enlarge_images" class="easyui-dialog" closed = true title=""
			style="float:left; width:400px;border:0;"
			> -->
			<!-- <div id="enlarge_images" 
			style="width:400px;display:none"
			> -->
	<div id="enlarge_images" style="display:none;position: absolute;">			
			<img id="limg" src="">
	</div>
	<div id="toolbar" class="lb_search">
		<div  style="float:right">
			<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" onclick="showDiv()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" onclick="window.location.href='${ctx}/settingMeta/metaCustomList'">返回</a>
		</div>
	</div>
	</div>
	<div id="showDiv" class="easyui-dialog" 
	
			style="width:440px;padding:20px 20px"
			closed="true" buttons="#edit-buttons"  data-options="resizable:true,modal:true">
		<form id="metaAddForm" method="post" style="margin-left:30px;" enctype='multipart/form-data'>
			<table width="100%">
				<tr class="fitem">
					<td><label>字段名称：</label><input type="hidden" name="metaCustomField.id"><input type="hidden" name="metaCustomField.metaid" value="${id }"></td>
					<td><input type="text" class="easyui-validatebox txt" style="height:25px;" name="metaCustomField.fieldname"></td>
				</tr>
				<tr class="fitem">
					<td><label>字段编码：</label></td>
					<td><input type="text" name="metaCustomField.fieldcode" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				<tr class="fitem">
					<td><label>字段类型：</label></td>
					<td>
						<select name="metaCustomField.fieldtype" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listType }" var="type"  varStatus="id">
							<option value="${type.value }">${type.name }</option>
						</c:forEach>
						</select>
				</tr>
 				<tr class="fitem">
					<td><label>是否可见：</label></td>
					<td>
						<select name="metaCustomField.ifVisible" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="vis"  varStatus="id">
							<option value="${vis.value }">${vis.name }</option>
						</c:forEach>
						</select>
						<label>是否可编辑：</label>
						<select name="metaCustomField.ifEdit" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="edit"  varStatus="id">
							<option value="${edit.value }">${edit.name }</option>
						</c:forEach>
						</select>
						<label>是否意见字段：</label>
						<select name="metaCustomField.isOpinion" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="show"  varStatus="id">
							<option value="${show.value }">${show.name }</option>
						</c:forEach>
						</select>
				</tr>
				<tr class="fitem">
					<td><label>是否变量：</label></td>
					<td>
						<select name="metaCustomField.ifVariable" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="variable"  varStatus="id">
							<option value="${variable.value }">${variable.name }</option>
						</c:forEach>
						</select>
						<label>是否自动填充：</label>
						<select name="metaCustomField.ifAuto" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="auto"  varStatus="id">
							<option value="${auto.value }">${auto.name }</option>
						</c:forEach>
						</select>
				</tr>
				<tr class="fitem">
					<td><label>默认值：</label></td>
					<td><input type="text" name="metaCustomField.defaultValue" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				
				<tr class="fitem">
					<td><label>待办类型：</label></td>
					<td>
						<select name="metaCustomField.fieldtodo" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listTodo }" var="todo"  varStatus="id">
							<option value="${todo.value }">${todo.name }</option>
						</c:forEach>
						</select>
					</td>
				</tr>
				<tr class="fitem">
					<td><label>待办模板：</label></td>
					<td><input type="text" name="metaCustomField.todotemplate" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				<tr class="fitem">
					<td><label>备注：</label></td>
					<td><textarea rows="3" cols="40" name="metaCustomField.remark"></textarea>
				</tr>
			</table>
		</form>
		<div id="edit-buttons">
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:85px;" iconCls="icon-ok" onclick="addAd(this)">确定</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:85px;" iconCls="icon-cancel" onclick="javascript:$('#showDiv').dialog('close')">取消</a>
		</div>
	</div>
	
	<div id="showEditDiv" class="easyui-dialog" 
			style="width:440px;padding:20px 20px"
			closed="true" buttons="#edit-buttons"  data-options="resizable:true,modal:true">
		<form id="metaEditForm" method="post" style="margin-left:30px;"  enctype='multipart/form-data'>
			<table width="100%">
				<tr class="fitem">
					<td><label>字段名称：</label><input type="hidden"  id="metaCustomField.id" name="metaCustomField.id"><input type="hidden" id="metaCustomField.metaid" name="metaCustomField.metaid" value="${id }"></td>
					<td><input type="text" class="easyui-validatebox txt" style="height:25px;" id="metaCustomField.fieldname" name="metaCustomField.fieldname"></td>
				</tr>
				<tr class="fitem">
					<td><label>字段编码：</label></td>
					<td><input type="text" id="metaCustomField.fieldcode" name="metaCustomField.fieldcode" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				<tr class="fitem">
					<td><label>字段类型：</label></td>
					<td>
						<select id="metaCustomField.fieldtype" name="metaCustomField.fieldtype" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listType }" var="type"  varStatus="id">
							<option value="${type.value }">${type.name }</option>
						</c:forEach>
						</select>
				</tr>
				<tr class="fitem">
					<td><label>是否可见：</label></td>
					<td>
						<select id="metaCustomField.ifVisible" name="metaCustomField.ifVisible" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="vis"  varStatus="id">
							<option value="${vis.value }">${vis.name }</option>
						</c:forEach>
						</select>
						<label>是否可编辑：</label>
						<select id="metaCustomField.ifEdit" name="metaCustomField.ifEdit" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="edit"  varStatus="id">
							<option value="${edit.value }">${edit.name }</option>
						</c:forEach>
						</select>
						<label>是否意见字段：</label>
						<select id="metaCustomField.isOpinion" name="metaCustomField.isOpinion" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="show" varStatus="id">
							<option value="${show.value }">${show.name }</option>
						</c:forEach>
						</select>
				</tr>
				<tr class="fitem">
					<td><label>是否变量：</label></td>
					<td>
						<select id="metaCustomField.ifVariable" name="metaCustomField.ifVariable" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="variable"  varStatus="id">
							<option value="${variable.value }">${variable.name }</option>
						</c:forEach>
						</select>
						<label>是否自动填充：</label>
						<select id="metaCustomField.ifAuto" name="metaCustomField.ifAuto" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listIf }" var="auto"  varStatus="id">
							<option value="${auto.value }">${auto.name }</option>
						</c:forEach>
						</select>
				</tr>
				 
				<tr class="fitem">
					<td><label>默认值：</label></td>
					<td><input type="text" id="metaCustomField.defaultValue" name="metaCustomField.defaultValue" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				
				<!-- 
				<tr class="fitem">
					<td><label>校验方式：</label></td>
					<td><input type="text" id="metaCustomField.checktype" name="metaCustomField.checktype" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				 -->
				<tr class="fitem">
					<td><label>待办类型：</label></td>
					<td>
						<select id="metaCustomField.fieldtodo" name="metaCustomField.fieldtodo" class="easyui-validatebox" style="margin-top:10px;height:25px;">
						<c:forEach items="${listTodo }" var="todo"  varStatus="id">
							<option value="${todo.value }">${todo.name }</option>
						</c:forEach>
						</select>
					</td>
				</tr>
				<tr class="fitem">
					<td><label>待办模板：</label></td>
					<td><input type="text" id="metaCustomField.todotemplate" name="metaCustomField.todotemplate" class="easyui-validatebox txt" maxlength="100" style="margin-top:10px;height:25px;" ></td>
				</tr>
				<tr class="fitem">
					<td><label>备注：</label></td>
					<td><textarea rows="3" cols="40" id="metaCustomField.remark" name="metaCustomField.remark"></textarea>
				</tr>
			</table>
		</form>
		
		<%-- <form action="<%=request.getContextPath()%>/license/importLicense" method="post" name="license_form" id="license_form" enctype='multipart/form-data' style="margin-left:30px;">
		</form> --%>
		
		<div id="edit-buttons">
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:85px;" iconCls="icon-ok" onclick="edit(this)">确定</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:85px;" iconCls="icon-cancel" onclick="javascript:$('#showEditDiv').dialog('close')">取消</a>
		</div>
	</div>
	</div>
	
</body>
<script type="text/javascript">
//增加自定义列
function formatOper(val,row,index){
	var format = '<a href="javascript:void(0)" onclick="showEidt('+index+')">编辑</a>&nbsp;<a href="javascript:void(0)" onclick="removeAd('+row.id+')">删除</a>';
	return format ;
} 

function showDiv(){
	$('#showDiv').dialog('open').dialog('setTitle','添加字段');
	$("#showDiv input:text").val("");
	$("#showDiv select").val("");
	$("#showDiv textarea").val("");
}
//添加
function addAd(){
	jQuery.ajax({
		url : '${ctx}/settingMeta/saveMetaCustomField',
		type : 'post',
		data : $('#metaAddForm').serialize(),
		dataType : 'json',
		success : function(msg){
			var obj = eval(msg);
			if(obj.status == 'ok'){
				$('#showDiv').dialog('close');
				$('#metaCustomFieldTable').datagrid('reload');	// reload the user data
			}else {
				$.messager.alert('提示',obj.info,'info');
			}
		},
		error: function(msg){
			var obj = eval(msg);
			$.messager.alert('提示',obj.info,'info');
		}
		
	})
	
}

function showEidt(index){
	var row = $('#metaCustomFieldTable').datagrid('getRows')[index];
	if (row){  
		$('#showEditDiv').dialog('open').dialog('setTitle','编辑字段');
		document.getElementById("metaCustomField.id").value=row.id;
		document.getElementById("metaCustomField.metaid").value=row.metaid;
		document.getElementById("metaCustomField.fieldname").value=row.fieldname;
		document.getElementById("metaCustomField.fieldcode").value=row.fieldcode;
		document.getElementById("metaCustomField.ifAuto").value=row.ifAuto;
		document.getElementById("metaCustomField.ifEdit").value=row.ifEdit;
		document.getElementById("metaCustomField.ifVisible").value=row.ifVisible;
		document.getElementById("metaCustomField.ifVariable").value=row.ifVariable;
		document.getElementById("metaCustomField.fieldtodo").value=row.fieldtodo;
		document.getElementById("metaCustomField.fieldtype").value=row.fieldtype;
		document.getElementById("metaCustomField.todotemplate").value=row.todotemplate;
		document.getElementById("metaCustomField.remark").value=row.remark;
		document.getElementById("metaCustomField.defaultValue").value=row.defaultValue;
		document.getElementById("metaCustomField.isOpinion").value=row.isOpinion;
	}
}
//编辑
function edit(obj){
	
	jQuery.ajax({
		url : '${ctx}/settingMeta/editMetaCustomField',
		type : 'post',
		data : $('#metaEditForm').serialize(),
		dataType : 'json',
		success : function(msg){
			var obj = eval(msg);
			if(obj.status == 'ok'){
				$('#showEditDiv').dialog('close');
				$('#metaCustomFieldTable').datagrid('reload');	// reload the user data
			}else{
				$.messager.alert('提示',obj.info,'info');
			}
		},
		error: function(msg){
			var obj = eval(msg);
			$.messager.alert('提示',obj.info,'info');
		}
		
	})
}


function removeAd(id){
	if (id!=""){
		$.messager.confirm('删除','确定要删除吗?',function(r){
			if (r){
				jQuery.ajax({
					url : '${ctx}/settingMeta/delMetaCustomField/'+id,
					type : 'post',
					dataType : 'json',
					success : function(msg){
						var obj = eval(msg);
						if(obj.status == 'ok'){
							$('#metaCustomFieldTable').datagrid('reload');	// reload the user data
						}else{
							$.messager.alert('提示',obj.info,'info');
						}
					},
					error: function(msg){
						var obj = eval(msg);
						$.messager.alert('提示',obj.info,'info');
					}
					
				})
			}
		});
	}else{
		$.messager.alert("提示","请选择操作项！","info");
	}
}
</script>
</html>