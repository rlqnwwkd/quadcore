<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
 
<title>Insert title here</title>
	<script  src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script type="text/javascript" src="https://service.iamport.kr/js/iamport.payment-1.1.1.js">
	</script>
	
	<script type="text/javascript">
	
		var IMP = window.IMP;
		IMP.init('imp13443240');
		
		IMP.request_pay({
		    pay_method : 'card', // 'card':신용카드, 'trans':실시간계좌이체, 'vbank':가상계좌, 'phone':휴대폰소액결제
		    merchant_uid : 'merchant_' + new Date().getTime(),
		    name : '주문명:공기',
		    amount : 1000,
		    buyer_email : 'kbh3983@nate.com',
		    buyer_name : '권봉혁',
		    buyer_tel : '010-6657-3983',
		    buyer_addr : '서울특별시 강남구 삼성동',
		    buyer_postcode : '123-456',
		    app_scheme : 'iamporttest'
		}, function(rsp) {
		    if ( rsp.success ) {
		        var msg = '결제가 완료되었습니다.';
		        msg += '고유ID : ' + rsp.imp_uid;
		        msg += '상점 거래ID : ' + rsp.merchant_uid;
		        msg += '결제 금액 : ' + rsp.paid_amount;
		        msg += '카드 승인번호 : ' + rsp.apply_num;
		    } else {
		        var msg = '결제에 실패하였습니다.';
		        msg += '에러내용 : ' + rsp.error_msg;
		    }
		 
		    alert(msg);
		});
	</script>
	
</head>
<body>
webview!! <br/>
${name }
</body>
</html>
