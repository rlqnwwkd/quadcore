<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
	<style>
		#geofenceCanvas{
			border:1px solid black;
		}
	</style>
	<script  src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script type="text/javascript">
	
		$(document).ready(function(){
			var radius = 10;
			var screenWidth = 500;
			var actualWidth;
			// 사각
			if( ${geofenceDTO.type} == 0 || ${geofenceDTO.type}==1 )
			{
				actualWidth = ${geofenceDTO.BC2X} - ${geofenceDTO.BC1X};
			}
			// 삼각
			else if( ${geofenceDTO.type} == 2 || ${geofenceDTO.type}==3 )
			{
				actualWidth = ${geofenceDTO.BC3X} - ${geofenceDTO.BC2X};
			}
			// 확대 
			var screenRatio = screenWidth /  actualWidth;
			var startingPoint_X =330;
			var startingPoint_Y =( screenWidth ) /5.0;
			
		
			
			// 사용자 위치 AJAX 가져오기
			function userPositionGetAjax(){
				$.ajax({
					url:"/SpringMVC/getUserLocation.do?geofenceId=46032",
					error:function(){
						clearCanvas();
						drawGeofence();
					},
					success:function(result){
						var users = result.split("&");
						
						// 다지우기
						clearConvas();
						drawGeofence();
						
						
						for(i =0; i< users.length ; i++)
						{
							var userId = users[i].split(",")[0];
							var userX = users[i].split(",")[1];
							var userY = users[i].split(",")[2];
							
							// 사용자위치 그리기
							drawCircle(userX, userY, 'black');
						}
					},
					complete:function(){
						setTimeout(userPositionGetAjax, 1000);
					}
				});
			}
			
			
			
			
			function drawGeofence(){				
				
				// ROOM_TYPE_CUSTOMIZE
				if(${geofenceDTO.type}==0)
				{
					drawGeofence_Rectangle();
				}
				// ROOM_TYPE_RECTANGLE_12_7
				else if(${geofenceDTO.type==1})
				{
					drawGeofence_Rectangle();
				}
				// ROOM_TYPE_TRIANGLE_6_6
				else if(${geofenceDTO.type==2})
				{
					drawGeofence_Triangle();
				}
				// ROOM_TYPE_TRIANGLE_3_3
				else if(${geofenceDTO.type==3})
				{
					drawGeofence_Triangle();
				}
				
			}
			
			function drawGeofence_Triangle()
			{
				/*
				alert("screenRatio : "+screenRatio +", startingPoint : "+startingPoint_X+","+startingPoint_Y+", bc : "
						+${geofenceDTO.BC1X}+","+${geofenceDTO.BC1Y}
						+${geofenceDTO.BC2X}+","+${geofenceDTO.BC2Y}
						+${geofenceDTO.BC3X}+","+${geofenceDTO.BC3Y}
						+${geofenceDTO.BC4X}+","+${geofenceDTO.BC4Y}
				);
				*/
				
				// 지오펜스 그리기
				drawCircle(${geofenceDTO.BC1X}, ${geofenceDTO.BC1Y}, 'blue');
				drawCircle(${geofenceDTO.BC2X}, ${geofenceDTO.BC2Y}, 'red');
				drawCircle(${geofenceDTO.BC3X}, ${geofenceDTO.BC3Y}, 'magenta');
				
				drawLine(${geofenceDTO.BC2X}, ${geofenceDTO.BC1Y},${geofenceDTO.BC3X},${geofenceDTO.BC1Y} );
				drawLine(${geofenceDTO.BC3X}, ${geofenceDTO.BC1Y},${geofenceDTO.BC3X},${geofenceDTO.BC3Y} );
				drawLine(${geofenceDTO.BC3X}, ${geofenceDTO.BC3Y},${geofenceDTO.BC2X},${geofenceDTO.BC2Y} );
				drawLine(${geofenceDTO.BC2X}, ${geofenceDTO.BC2Y},${geofenceDTO.BC2X},${geofenceDTO.BC1Y} );
			
				
				// 결제존 -> 그리는거 마저 하기
				drawRectangle(${geofenceDTO.ZONE_X1},${geofenceDTO.ZONE_Y1},${geofenceDTO.ZONE_X2},${geofenceDTO.ZONE_Y2},'red');
			}
			
			function drawGeofence_Rectangle()
			{
				
				// 지오펜스 그리기
				drawCircle(${geofenceDTO.BC1X}, ${geofenceDTO.BC1Y}, 'blue');
				drawCircle(${geofenceDTO.BC2X}, ${geofenceDTO.BC2Y}, 'red');
				drawCircle(${geofenceDTO.BC3X}, ${geofenceDTO.BC3Y}, 'magenta');
				drawCircle(${geofenceDTO.BC4X}, ${geofenceDTO.BC4Y}, 'cyan');
				
				drawLine(${geofenceDTO.BC1X}, ${geofenceDTO.BC1Y},${geofenceDTO.BC2X},${geofenceDTO.BC2Y} );
				drawLine(${geofenceDTO.BC2X}, ${geofenceDTO.BC2Y},${geofenceDTO.BC3X},${geofenceDTO.BC3Y} );
				drawLine(${geofenceDTO.BC3X}, ${geofenceDTO.BC3Y},${geofenceDTO.BC4X},${geofenceDTO.BC4Y} );
				drawLine(${geofenceDTO.BC4X}, ${geofenceDTO.BC4Y},${geofenceDTO.BC1X},${geofenceDTO.BC1Y} );
			
				
				// 결제존 -> 그리는거 마저 하기
				drawRectangle(${geofenceDTO.ZONE_X1},${geofenceDTO.ZONE_Y1},${geofenceDTO.ZONE_X2},${geofenceDTO.ZONE_Y2},'red');
			}
			
			function clearConvas(){
				var canvas = document.getElementById('geofenceCanvas');
				var ctx = canvas.getContext('2d');
				ctx.clearRect(0,0,canvas.width, canvas.height);
			}
			
			function drawRectangle(x1,y1,x2,y2,color){
				
				x1 = x1 * screenRatio;
		        y1 = y1 * screenRatio;
		        x2 = x2 * screenRatio;
		        y2 = y2 * screenRatio;
				
		        var relativeX1 = startingPoint_X+parseFloat(x1);
				var relativeY1 = startingPoint_Y+parseFloat(y1);
				var relativeX2 = startingPoint_X+parseFloat(x2);
				var relativeY2 = startingPoint_Y+parseFloat(y2);
				
				var canvas = document.getElementById('geofenceCanvas');
				
				if(canvas.getContext)
				{
					var width = relativeX2-relativeX1;
					var height= relativeY2-relativeY1;
					
					var ctx = canvas.getContext('2d');
					ctx.beginPath();
					
					ctx.rect(relativeX1,relativeY1,width,height);
					ctx.lineWidth='2';
					ctx.strokeStyle=color;
					ctx.stroke();
					ctx.closePath();
				}
			}
			
			function drawCircle(x, y, color){
				
				x = x * screenRatio;
				y = y * screenRatio;
				
				var canvas = document.getElementById('geofenceCanvas');
				// Make sure we don't execute when canvas isn't supported
				if(canvas.getContext)
				{
					var ctx = canvas.getContext('2d');
					ctx.beginPath();
					var relativeX = startingPoint_X + parseFloat(x);
					var relativeY = startingPoint_Y + parseFloat(y);
					
					ctx.arc(relativeX, relativeY, radius, 0, 2*Math.PI, false);
					ctx.fillStyle=color;
					ctx.fill();
					ctx.closePath();
				}
			}
			
			
			function drawLine(x1,y1,x2,y2){		
				
				x1 = x1 * screenRatio;
		        y1 = y1 * screenRatio;
		        x2 = x2 * screenRatio;
		        y2 = y2 * screenRatio;
				
				var canvas = document.getElementById('geofenceCanvas');

				// Make sure we don't execute when canvas isn't supported
				if(canvas.getContext)
				{
					var ctx = canvas.getContext('2d');
					ctx.beginPath();
					var relativeX1 = startingPoint_X+parseFloat(x1);
					var relativeY1 = startingPoint_Y+parseFloat(y1);
					var relativeX2 = startingPoint_X+parseFloat(x2);
					var relativeY2 = startingPoint_Y+parseFloat(y2);
					ctx.lineWidth='2';
					ctx.strokeStyle='green';
					ctx.moveTo(relativeX1,relativeY1);
					ctx.lineTo(relativeX2,relativeY2);
					ctx.stroke();
					ctx.closePath();
				}
			}
			
			
			////////////////////////////////////////////
			// App start
			/////////////////////////////////////////////
			var geofenceDTO = "${geofenceDTO}";
			
			if(geofenceDTO)
			{
				drawGeofence();
			}
			
			userPositionGetAjax();
			
		});
		
	</script>
</head>
<style>
	#geofenceCanvas
	{
		
		background-image: url("resources/img/tiles.PNG");
		background-repeat: repeat;
		
	}
	
	#geofenceId
	{
		height: 100px;
		width: 1150px;
		border : 1px solid;
	}
	
	html, body
	{
		width: 100%;
		height: 100%;
	}
	

</style>
<body>

	<header>
		<div id= "geofenceId">
			<span style="font-size: xx-large; font-style: italic;">
				Geofence ID : ${geofenceDTO.id } <br/>
				Geofence Name : ${geofenceDTO.name }	
			</span>
		</div>
	</header>
	

	<canvas id="geofenceCanvas"  width="1150" height="1200">
	
	</canvas>
	

</body>
</html>





