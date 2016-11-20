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
			

			function drawCircle(x, y){
				var canvas = document.getElementById('geofenceCanvas');

				// Make sure we don't execute when canvas isn't supported
				if(canvas.getContext)
				{
					var ctx = canvas.getContext('2d');
					ctx.beginPath();
					var relativeX=(parseFloat(canvas.width)/10.0)+parseFloat(x);
					var relativeY = (parseFloat(canvas.height)/10.0) +parseFloat(y);
					var radius = 10;
					
					ctx.arc(relativeX, relativeY, radius, 0, 2*Math.PI, false);
					ctx.fillStyle='green';
					ctx.fill();
					ctx.stroke();
					ctx.closePath();
				}
			}
			
			function drawLine(x1,y1,x2,y2){
				var canvas = document.getElementById('geofenceCanvas');

				// Make sure we don't execute when canvas isn't supported
				if(canvas.getContext)
				{
					var ctx = canvas.getContext('2d');
					ctx.beginPath();
					var relativeX1=(parseFloat(canvas.width)/10.0)+parseFloat(x1);
					var relativeY1 = (parseFloat(canvas.height)/10.0)+parseFloat(y1);
					var relativeX2=(parseFloat(canvas.width)/10.0)+parseFloat(x2);
					var relativeY2 = (parseFloat(canvas.height)/10.0)+parseFloat(y2);
	
					ctx.moveTo(relativeX1,relativeY1);
					ctx.lineTo(relativeX2,relativeY2);
					ctx.stroke();
					ctx.closePath();
				}
			}
			
			var bc1Position = "${bc1Position}";
			
			if(bc1Position != '')
			{
				drawCircle(0,0);
			}
			
			var lengthX = "${lengthX}";
			
			if(lengthX != '')
			{
				drawCircle(lengthX, 0);
				drawLine(0,0,lengthX,0);
			}
			
			var lengthY = "${lengthY}";
			
			if(lengthY != '')
			{
				drawCircle(lengthX, lengthY);
				drawCircle(0, lengthY);
				drawLine(lengthX,0,lengthX,lengthY);
				drawLine(lengthX,lengthY,0,lengthY);
				drawLine(0,lengthY,0,0);
			}
			
			var userLocation = "${userLocation}";
			if(userLocation != '')
			{
				drawCircle('${userLocation.x}', '${userLocation.y}');
			}
			
		});
		
		
	</script>
</head>
<body>

	
	<canvas id="geofenceCanvas" width="500" height="500">
	
	</canvas>

</body>
</html>