<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Получение запроса</title>
</head>

${ERROR}
<br>
<form id="get" action="test" method="post"></form>
<p>Имя БД: </p>
<p><input name="nameDB" form="get"> </p>
<p>Имя таблицы: </p>
<p><input name="nameTable" form="get"></p>

<p><input name="getRadio" type="radio" value="create" form="get" checked> getCreate</p>
<p><input name="getRadio" type="radio" value="select" form="get"> getSelect</p>
<p><input name="getRadio" type="radio" value="update" form="get"> getUpdate</p>

<p><input type="submit" form="get" value="Получить запрос"></p>
</body>
</html>
