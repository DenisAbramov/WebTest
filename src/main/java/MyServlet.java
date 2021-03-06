import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;
import dbServise.DBConnect;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/test")
public class MyServlet extends HttpServlet{

    Connection dbConnection = null;
    DatabaseMetaData meta;
    Statement stmt = null;
    String stringConnect;
    String login;
    String password;

   @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");


        stringConnect = req.getParameter("stringConnect");
        login = req.getParameter("login");
        password = req.getParameter("password");
        String nameDB = req.getParameter("nameDB");
        String nameTable = req.getParameter("nameTable");

        if(stringConnect.length() > 0 && login.length() > 0 && password.length() > 0 && nameDB.length() > 0 && nameTable.length() > 0) {

            String nameMethod = req.getParameter("getRadio");
            String rezult = null;

            try {

                if (nameMethod.equals("create")) {
                    rezult = getCreate(nameDB, nameTable);
                } else if (nameMethod.equals("select")) {
                    rezult = getSelect(nameDB, nameTable);
                } else if (nameMethod.equals("update")) {
                    rezult = getUpdate(nameDB, nameTable);
                }
            }
            catch (NullPointerException | StringIndexOutOfBoundsException e)
            {
                if(e instanceof NullPointerException){
                    req.setAttribute("ERROR", "Неверное значение в одном из полей: Строка подключения, Имя пользователя, Пароль пользователя, имя БД");
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
                    dispatcher.forward(req, resp);}
                else
                {
                    req.setAttribute("ERROR", "Вы ввели неправильное имя таблицы");
                    RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
                    dispatcher.forward(req, resp);}
            }

            req.setAttribute("SQL", rezult);
        }
        else
            req.setAttribute("SQL", "Вероятно Вы не заполнили какое-то поле");

        RequestDispatcher dispatcher = req.getRequestDispatcher("/post.jsp");
        dispatcher.forward(req, resp);
    }

    public String getCreate(String schemaName, String tableName)  {

        if(stringConnect.contains("mysql"))
        dbConnection =  DBConnect.getMySQLConnection(schemaName, stringConnect, login, password);
        else
        dbConnection = DBConnect.getPostgreSQLConnection(schemaName, stringConnect, login, password);

        StringBuilder sb = new StringBuilder();
        StringBuilder primary = new StringBuilder();
        ResultSet pr;
        ResultSet rs;
        ResultSetMetaData rsmd;

        try {
            meta  = (DatabaseMetaData) dbConnection.getMetaData();
            stmt = dbConnection.createStatement();
            pr = meta.getPrimaryKeys(null, null, tableName);
            rs = stmt.executeQuery("SELECT * FROM "  + tableName);
            rsmd = (ResultSetMetaData) rs.getMetaData();

            while (pr.next()) {
                String columnName = pr.getString("COLUMN_NAME");
                primary.append(columnName + ", ");
            }


            int countColumn = rsmd.getColumnCount();
            for(int i = 1; i <= countColumn; i++)
            {
                sb.append(rsmd.getColumnName(i) + " ").append(rsmd.getColumnTypeName(i) + "(" + rsmd.getColumnDisplaySize(i) + "), ");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
           try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String prim = primary.toString().trim();

        String autoinc = "PRIMARY KEY (" + prim.substring(0,prim.length()-1) + ") ";


        return  "CREATE TABLE " + tableName + "("
                + sb.toString() + " " + autoinc + ")";
    }


    public String getSelect(String schemaName, String tableName)  {

        if(stringConnect.contains("mysql"))
            dbConnection =  DBConnect.getMySQLConnection(schemaName, stringConnect, login, password);
        else
            dbConnection = DBConnect.getPostgreSQLConnection(schemaName, stringConnect, login, password);

        StringBuilder sb = new StringBuilder();
        ResultSet rs;
        ResultSetMetaData rsmd;
        String prim = null;

        try {
            meta  = (DatabaseMetaData) dbConnection.getMetaData();

            prim = getPrimaryKeys(meta, tableName);

            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM "  + tableName);
            rsmd = (ResultSetMetaData) rs.getMetaData();

            int countColumn = rsmd.getColumnCount();
            for(int i = 1; i <= countColumn; i++)
            {
                sb.append(rsmd.getColumnName(i) + ", ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
           try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String data = sb.toString().trim();

        return "SELECT " + data.substring(0, data.length()-1) + " FROM " + tableName + " WHERE " + prim.substring(0,prim.length()-4);
    }

    public String getUpdate(String schemaName, String tableName)  {

        if(stringConnect.contains("mysql"))
            dbConnection =  DBConnect.getMySQLConnection(schemaName, stringConnect, login, password);
        else
            dbConnection = DBConnect.getPostgreSQLConnection(schemaName, stringConnect, login, password);

        StringBuilder sb = new StringBuilder();
        ResultSet rs;
        ResultSetMetaData rsmd;
        String prim = null;

        try {
            meta  = (DatabaseMetaData) dbConnection.getMetaData();

            prim =getPrimaryKeys(meta, tableName);

            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM "  + tableName);
            rsmd = (ResultSetMetaData) rs.getMetaData();

            int countColumn = rsmd.getColumnCount();
            for(int i = 1; i <= countColumn; i++)
            {
                sb.append(rsmd.getColumnName(i)).append(" = ?, ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String data = sb.toString().trim();

        return  "UPDATE " + tableName + " SET " + data.substring(0, data.length()-1) + " WHERE " + prim.substring(0, prim.length()-4);
    }


    private String getPrimaryKeys(DatabaseMetaData m, String tableName)
    {
        StringBuilder primary = new StringBuilder();
        ResultSet pr;
        try {
            pr = m.getPrimaryKeys(null, null, tableName);
            while (pr.next()) {
                String columnName = pr.getString("COLUMN_NAME");
                primary.append(columnName + " = ? AND ");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return primary.toString().trim();
    }


}
