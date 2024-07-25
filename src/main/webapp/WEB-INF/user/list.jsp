<%@ include file="common.jspf" %>

<!DOCTYPE html>
<html lang="kr">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>SLiPP Java Web Programming</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link href="../../static/css/bootstrap.min.css" rel="stylesheet">
    <!--[if lt IE 9]>
    <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link href="../../static/css/styles.css" rel="stylesheet">
</head>
<body>
<%@ include file="navbar.jspf" %>
<div class="container" id="main">
   <div class="col-md-10 col-md-offset-1">
      <div class="panel panel-default">
          <table class="table table-hover">
              <thead>
                <tr>
                    <th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="user" items="${users}" varStatus="status">
                  <tr>
                      <td>${status.index + 1}</td>
                      <td><a href="/users/${user.userId}">${user.userId}</a></td>
                      <td>${user.nickname}</td>
                      <td>${user.email}</td>
                  </tr>
              </c:forEach>
              </tbody>
          </table>
        </div>
    </div>
</div>

<!-- script references -->
<script src="../../static/js/jquery-2.2.0.min.js"></script>
<script src="../../static/js/bootstrap.min.js"></script>
<script src="../../static/js/scripts.js"></script>
	</body>
</html>