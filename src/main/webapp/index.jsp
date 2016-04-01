<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Получение запроса</title>
</head>

${ERROR}
<br>
<form id="get" action="test" method="post"></form>
<p>Строка подключения (в формате jdbc:типБД://hostname:port/): </p>
<p><input name="stringConnect" form="get"></p>
<p>Имя пользователя: </p>
<p><input name="login" form="get"> </p>
<p>Пароль пользователя: </p>
<p><input name="password" form="get"></p>
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
