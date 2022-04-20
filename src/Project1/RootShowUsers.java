package Project1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RootShowUsers extends ViewBaseServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //TODO 后端需增添校验防止xss脚本攻击
        //TODO 需对文件内容和后缀进行校验，防止文件上传漏洞，顺便完成后缀自动补全功能
        //TODO 限制用户操作频率和最大操作数
        HttpSession session = request.getSession(false);//不创建新ID
        if (session != null) { //判断session是否存在
            if (!session.isNew() && request.getHeader("referer")!=null && session.getAttribute("username") != null) {//判断session是否新的，但似乎无用
                Referer_Check RC = new Referer_Check(request.getHeader("referer"), "NetDisk/RootPage.html");
                Referer_Check RC1 = new Referer_Check(request.getHeader("referer"), "NetDisk/RootShowUsers.html");
                Referer_Check RC2 = new Referer_Check(request.getHeader("referer"), "NetDisk/RSU");//TODO 此处函数有待优化
                Referer_Check RC3 = new Referer_Check(request.getHeader("referer"), "NetDisk/ShowError");
                Referer_Check RC4 = new Referer_Check(request.getHeader("referer"), "NetDisk/RootEmailExist.html");
                Referer_Check RC5 = new Referer_Check(request.getHeader("referer"), "NetDisk/RootSignUpSuccess.html");
                Referer_Check RC6 = new Referer_Check(request.getHeader("referer"), "NetDisk/RootSignUpFail.html");
                if ((!RC.check() || !RC1.check() || !RC2.check() || !RC3.check() || !RC4.check() || !RC5.check() || !RC6.check()) && session.getAttribute("username").equals("ROOT")) {  //验证来源链接
                    //TODO 将用户姓名邮箱传入session.users
                    Connection conn = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    String path = this.getServletContext().getRealPath("/WEB-INF/classes/DB_Info.properties");
                    DB_Connect connect = new DB_Connect();
                    try {
                        conn = connect.connect(path);
                        String sql = "select email,name from mypan.users;";
                        ps = conn.prepareStatement(sql);
                        rs = ps.executeQuery();
                        int i = 0;
                        while (rs.next()) {
                            i = i+1;
                        }
                        String[][] users = new String[3][i];
                        rs = ps.executeQuery();
                        i = 0;
                        while (rs.next()){
                            users[0][i] = String.valueOf(i);
                            users[1][i] = rs.getString("name");
                            users[2][i] = rs.getString("email");
                            i = i+1;
                        }
                        session.setAttribute("users",users);
                        session.setAttribute("usersnum",i);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try{
                            if (rs != null){
                                rs.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try{
                            if (ps != null){
                                ps.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try{
                            if (conn != null){
                                conn.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    super.processTemplate("RootShowUsers", request, response);
                } else response.sendRedirect("403.html");
            } else response.sendRedirect("403.html");
        } else response.sendRedirect("login.html");//如果session过期
    }
}

