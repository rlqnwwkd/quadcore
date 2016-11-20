<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>

<style>
	html,body 
	{ 
		height:100%;
		overflow:hidden;
		background-image: url("resources/img/tiles.PNG");
	} 
	
	#title
	{
		margin-left:30%;
		margin-top:30%;
		width: 500px; 
		height:500px; 
		background-color: #555;
		color: #fff;
		font-weight: bold;
		padding: 10px;
		-moz-border-radius: 5px;
		-webkit-border-radius: 5px;
	}
	
	
	#title>table
	{
		width: 100%;
		height: 100%;
	}
	
	#title tr
	{
		background-color: #fff;
	}
	
	#title td
	{
		width: 100%;
		background-color: #fff;
	}
	
	
	section.gradient button {
		height:100px;
		width:100%;
		font-size:x-large;
		color: #fff;
		text-shadow: -2px 2px #346392;
		background-color: #cce6ff;
		background-image: linear-gradient(top, #6496c8, #346392);
		box-shadow: inset 0 0 0 1px #27496d;
		border: none;
		border-radius: 15px;
	}
	
	section.gradient button:hover,
	section.gradient button.hover {
	  box-shadow: inset 0 0 0 1px #27496d,0 5px 15px #193047;
	}
	
	section.gradient button:active,
	section.gradient button.active {
	  box-shadow: inset 0 0 0 1px #27496d,inset 0 5px 30px #193047;
	}
	
	#logo
	{
		width:210px;
		height:200px;
		background: url("resources/img/quadcore_logo.PNG");	
		background-repeat: no-repeat;
	}
	
</style>

<script  src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script>
	$(document).ready(function(){
		
		var monitorBtn = document.getElementById('monitoring');
		var managePaymentBtn = document.getElementById('managePayment');
		
		
		// 모니터링 화면
		monitorBtn.onclick = function monitorBtnClicked()
		{
			location.href="http://192.168.43.97:8080/SpringMVC/geofence.do"
		}
		
		// 결제 화면
		managePaymentBtn.onclick=function managePaymentBtnClicked()
		{
			location.href="http://admin.iamport.kr/users/login"
		}
	});
	
</script>
<body>

	<header>
		<div id="title">
			<table>
				<tr>
					<td style="height: 200px;" align="center">
						
						<div id="logo">
						</div>
						<div>
							<span style="font-size: xx-large; font-style: italic;">QuadCore</span>
						</div>
					</td>
				</tr>
				
				<tr>
					<td style="height: 100px">
						<section class="gradient">
						    <button id="monitoring">Monitoring</button>
						</section>
					</td>
				</tr>
				
				<tr>
					<td style="height: 100px">
						<section class="gradient">
						    <button id="managePayment">Manage Payment</button>
						</section>
					</td>
				</tr>
			</table>
		</div>
	</header>
	


</body>
</html>