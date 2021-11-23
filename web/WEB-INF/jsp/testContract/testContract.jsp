<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>
<style>
a {
	color: black
}

a:hover {
	color: blue
}

.aclick {
	cursor: pointer;
	font-size: 14px;
	font-weight: bold;
	width: 75%;
}
.aclickTwo {
	cursor: pointer;
	font-size: 14px;
	font-weight: normal;
	width: 75%;
}
</style>
</head>
<body class="body-pading" leftmargin="0" topmargin="" marginwidth="0"
	marginheight="0">
	<div id='layout'>
		<table id="leaderLnstructionsTable" width="100%"
			style="border-collapse: separate; border-spacing: 0px 15px;">
		</table>
	</div>
	
	<script type="text/javascript">
	$(function(){
		getSectionList();
	});
	var isRequest = true;
	var isRequestTwo = true;
	/* 栏目 */
	function getSectionList(){
		$.ajax({
			url:"/seeyon/columnController.do?method=getColumnData&regulatoryContract=5",
			dataType:"json",
			async:true,
			type:"POST",
			success:function(object){
				if(object!=null&&object!==""){
					var html = "";
					html += "<tr>" +
					"<td style='font-size: 12px ;margin-left: 10px'>"+ "经办人" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "经办部门" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "申请日期" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "合同编号" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "合同名称" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "合同金额" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "累计已付金额" +"</td>"+
					"<td style='font-size: 12px;margin-left: 10px'>"+ "单点登录url" +"</td>" +
							"</tr>";
					 for(var i = 0; i < object.length; i++){
						html += "<tr>" +
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0001 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0002 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0003 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0004 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0005 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0006 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>"+ object[i].field0033 +"</td>"+
						"<td style='font-size: 12px;margin-left: 10px'>";
						if(object[i].field0036!=""){
							html += "<a href='"+ object[i].field0036 +"' target='view_window'>"+ "跳转" +"</a></td></tr>";
						}
					 }
					 $("#leaderLnstructionsTable").html(html);
				}
			}
		});
		if (isRequest) {
	        isRequest = false;
	        window.setInterval("getSectionList()", 5000);
	    }
	}
	</script>
</body>
</html>