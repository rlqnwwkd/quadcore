<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    
<html>
	<head>
		<title>trilateration.js</title>
		<script  src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
		<script type="text/javascript" src="trilateration.js"></script>
		<script type="text/javascript">
		function trilaterate(p1, p2, p3, return_middle)
		{
			// based on: https://en.wikipedia.org/wiki/Trilateration
			
			// some additional local functions declared here for
			// scalar and vector operations
			function sqr(a)
			{
				return a * a;
			}
			
			function norm(a)
			{
				return Math.sqrt(sqr(a.x) + sqr(a.y) + sqr(a.z));
			}
			
			function dot(a, b)
			{
				return a.x * b.x + a.y * b.y + a.z * b.z;
			}
			
			function vector_subtract(a, b)
			{
				return {
					x: a.x - b.x,
					y: a.y - b.y,
					z: a.z - b.z
				};
			}
			
			function vector_add(a, b)
			{
				return {
					x: a.x + b.x,
					y: a.y + b.y,
					z: a.z + b.z
				};
			}
			
			function vector_divide(a, b)
			{
				return {
					x: a.x / b,
					y: a.y / b,
					z: a.z / b
				};
			}
			
			function vector_multiply(a, b)
			{
				return {
					x: a.x * b,
					y: a.y * b,
					z: a.z * b
				};
			}
			
			function vector_cross(a, b)
			{
				return {
					x: a.y * b.z - a.z * b.y,
					y: a.z * b.x - a.x * b.z,
					z: a.x * b.y - a.y * b.x
				};
			}
			
			var ex, ey, ez, i, j, d, a, x, y, z, b, p4;
			
			ex = vector_divide(vector_subtract(p2, p1), norm(vector_subtract(p2, p1)));
			
			i = dot(ex, vector_subtract(p3, p1));
			a = vector_subtract(vector_subtract(p3, p1), vector_multiply(ex, i));
			ey = vector_divide(a, norm(a));
			ez =  vector_cross(ex, ey);
			d = norm(vector_subtract(p2, p1));
			j = dot(ey, vector_subtract(p3, p1));
			
			x = (sqr(p1.r) - sqr(p2.r) + sqr(d)) / (2 * d);
			y = (sqr(p1.r) - sqr(p3.r) + sqr(i) + sqr(j)) / (2 * j) - (i / j) * x;
			
			b = sqr(p1.r) - sqr(x) - sqr(y);
			
			// floating point math flaw in IEEE 754 standard
			// see https://github.com/gheja/trilateration.js/issues/2
			if (Math.abs(b) < 0.0000000001)
			{
				b = 0;
			}
			
			z = Math.sqrt(b);
			
			// no solution found
			if (isNaN(z))
			{
				return null;
			}
			
			a = vector_add(p1, vector_add(vector_multiply(ex, x), vector_multiply(ey, y)))
			p4a = vector_add(a, vector_multiply(ez, z));
			p4b = vector_subtract(a, vector_multiply(ez, z));
			
			if (z == 0 || return_middle)
			{
				return a;
			}
			else
			{
				return [ p4a, p4b ];
			}
		}
		
		function initialize()
		{
		
			canvas = document.getElementById("canvas1");
			ctx = canvas.getContext("2d");
		
			p1 = { x:   0, y:   0, z:  0, r: 320 };
			p2 = { x: 500, y: 200, z:  0, r: 370 };
			p3 = { x: 400, y: 500, z: 40, r: 350 };
			
			p4 = trilaterate(p1, p2, p3);

			if (p4 !== null)
			{
				ctx.fillStyle = "#111";
			}
			else
			{
				ctx.fillStyle = "#800";
			}
		
			
			ctx.fillRect(0, 0, canvas.width, canvas.height);
			
			ctx.fillStyle = "#f00";
			ctx.strokeStyle = ctx.fillStyle;
			ctx.fillRect(20 + p1.x - 2, 20 + p1.y - 2, 5, 5);
			ctx.beginPath();
			ctx.arc(20 + p1.x, 20 + p1.y, p1.r, 0, 2 * Math.PI);
			ctx.stroke();
			
			ctx.fillStyle = "#0f0";
			ctx.strokeStyle = ctx.fillStyle;
			ctx.fillRect(20 + p2.x - 2, 20 + p2.y - 2, 5, 5);
			ctx.beginPath();
			ctx.arc(20 + p2.x, 20 + p2.y, p2.r, 0, 2 * Math.PI);
			ctx.stroke();
			
			ctx.fillStyle = "#00f";
			ctx.strokeStyle = ctx.fillStyle;
			ctx.fillRect(20 + p3.x - 2, 20 + p3.y - 2, 5, 5);
			ctx.beginPath();
			ctx.arc(20 + p3.x, 20 + p3.y, p3.r, 0, 2 * Math.PI);
			ctx.stroke();
			
			if (p4 !== null)
			{
				if (p4 instanceof Array)
				{
					ctx.fillStyle = "#0ff";
					ctx.fillRect(20 + p4[0].x - 2, 20 + p4[0].y - 2, 5, 5);
					
					ctx.fillStyle = "#ff0";
					ctx.fillRect(20 + p4[1].x - 2, 20 + p4[1].y - 2, 5, 5);
				}
				else
				{
					ctx.fillStyle = "#fff";
					ctx.fillRect(20 + p4.x - 2, 20 + p4.y - 2, 5, 5);
				}
			}

		}
		
		$(document).ready(function(){
			
			var canvas, ctx;
			initialize();

			
		});
		</script>
		
	</head>
	<body>
		<h1>Trilateration in 3D</h1>
		
		<p>
			<em>
			"In geometry, trilateration is the process of determining absolute
			or relative locations of points by measurement of distances, using
			the geometry of circles, spheres or triangles.<br/>
			<br/>
			In addition to its interest as a geometric problem, trilateration
			does have practical applications in surveying and navigation,
			including global positioning systems (GPS). In contrast to
			triangulation, it does not involve the measurement of angles."</em>
			
			&mdash; <a href="https://en.wikipedia.org/wiki/Trilateration">Wikipedia</a>
		</p>
		
		<p>
			The trilateration can result zero, one or two solutions, in these
			cases trilaterate() will return <em>null</em>, an Object with
			{ x, y, z } coordinates or an Array with two Objects with
			{ x, y, z } coordinates, respectively.
		</p>
		
		<p>
			There is an optional fourth parameter after the three points for
			trilaterate() for the case of two solutions to return the middle
			of them as one point.
		</p>
		
		<p>
			In this example...
			<ul>
				<li>the canvas will turn red if no solution can be found,</li>
				<li>a white dot will show the solution if one can be found,</li>
				<li>a yellow and a cyan dot will show the two solutions if there
				are two of them.</li>
			</ul>
		</p>
		
		<p>
			Note: the display of points and distances are projected to the X-Y
			plane so the Z coordinates cannot be seen, but they are taken into
			consideration by trilaterate()!
		</p>
		
		<canvas id="canvas1" width="800" height="800">
		</canvas>
		
		
	</body>
</html>