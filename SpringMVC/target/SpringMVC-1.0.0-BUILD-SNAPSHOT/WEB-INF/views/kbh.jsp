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
		    pay_method : 'card', // 'card':�ſ�ī��, 'trans':�ǽð�������ü, 'vbank':�������, 'phone':�޴����Ҿװ���
		    merchant_uid : 'merchant_' + new Date().getTime(),
		    name : '�ֹ���:����',
		    amount : 1000,
		    buyer_email : 'kbh3983@nate.com',
		    buyer_name : '�Ǻ���',
		    buyer_tel : '010-6657-3983',
		    buyer_addr : '����Ư���� ������ �Ｚ��',
		    buyer_postcode : '123-456',
		    app_scheme : 'iamporttest'
		}, function(rsp) {
		    if ( rsp.success ) {
		        var msg = '������ �Ϸ�Ǿ����ϴ�.';
		        msg += '����ID : ' + rsp.imp_uid;
		        msg += '���� �ŷ�ID : ' + rsp.merchant_uid;
		        msg += '���� �ݾ� : ' + rsp.paid_amount;
		        msg += 'ī�� ���ι�ȣ : ' + rsp.apply_num;
		    } else {
		        var msg = '������ �����Ͽ����ϴ�.';
		        msg += '�������� : ' + rsp.error_msg;
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
