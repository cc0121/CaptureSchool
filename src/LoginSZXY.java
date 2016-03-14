import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class LoginSZXY {
    // private static CloseableHttpClient httpClient = HttpClients.createDefault();
    private static DefaultHttpClient httpClient = new DefaultHttpClient();

    /**
     * 数字校园，模拟POST登陆
     *
     * @param username 学号
     * @param password 密码
     * @return
     */
    public static int getLoginInfo(String username, String password) {
        System.out.println("--------Get Cookie for Login---------");
        String loginUrl = "http://ids1.ahau.edu.cn/amserver/UI/Login";
        String indexUrl = "http://i.ahau.edu.cn/index.portal";
        String infoUrl = "http://i.ahau.edu.cn/pnull.portal?.f=f86&.pmn=view&action=informationCenterAjax&.ia=false&.pen=pe33";
        int htmlCode = 0;

        //模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
        HttpPost httpPost = new HttpPost(loginUrl);
        HttpGet indexHttpGet = new HttpGet(indexUrl);
        HttpGet infoHttpGet = new HttpGet(infoUrl);
        System.out.println("Login request line : " + httpPost.getRequestLine());
        System.out.println("Get index post line: " + indexHttpGet.getRequestLine());
        System.out.println("Get information post line: " + infoHttpGet.getRequestLine());
        try {
            //设置登陆信息，POST提交数据
            List<NameValuePair> nvpsLogin = new ArrayList<NameValuePair>();
            nvpsLogin.add(new BasicNameValuePair("Login.Token1", "13100501"));
            nvpsLogin.add(new BasicNameValuePair("Login.Token2", "Peace0528"));
            nvpsLogin.add(new BasicNameValuePair("goto", "http://i.ahau.edu.cn/loginSuccess.portal"));
            nvpsLogin.add(new BasicNameValuePair("gotoOnFail", "http://i.ahau.edu.cn/loginFailure.portal"));    //“学生”的 urlEncode编码
            httpPost.setEntity(new UrlEncodedFormEntity(nvpsLogin));

            // 执行Post请求进行登录
            CloseableHttpResponse loginPostResponse = httpClient.execute(httpPost);
            // 执行Get请求获取首页
            CloseableHttpResponse indexGetResponse = httpClient.execute(indexHttpGet);
            // 执行Get请求获取详细个人信息
            CloseableHttpResponse infoGetResponse = httpClient.execute(infoHttpGet);

            try {
                // 获取返回数据
                HttpEntity loginEntity = loginPostResponse.getEntity();
                System.out.println("Response Line: " + loginPostResponse.getStatusLine());
                System.out.println("Cookie: " + loginPostResponse.getFirstHeader("Set-Cookie").getValue());
                htmlCode = loginPostResponse.getStatusLine().getStatusCode();
                EntityUtils.consume(loginEntity);

                // 获取得到的首页
                HttpEntity indexGetEntity = indexGetResponse.getEntity();
                String indexContent = EntityUtils.toString(indexGetEntity);
                System.out.println(indexContent);
                System.out.println("Response Line: " + indexGetResponse.getStatusLine());
                System.out.println("Cookie: " + indexGetResponse.getFirstHeader("Set-Cookie").getValue());
                htmlCode = indexGetResponse.getStatusLine().getStatusCode();
                EntityUtils.consume(indexGetEntity);

                Document content = Jsoup.parse(indexContent);
                Elements info = content.select("div.composer > ul > li");
                StudentInfo studentInfo = new StudentInfo();

                studentInfo.setName(info.get(0).text().split("，")[0]);
                studentInfo.setStudentID(info.get(1).text().split("：")[1]);
                GlobalDataUtil.studentID = info.get(1).text().split("：")[1];
                studentInfo.setIdentity(info.get(2).text().split("：")[1]);
                studentInfo.setDepartment(info.get(3).text().split("：")[1]);
                Elements notifyInfo = content.select("div#pf385"); // 获取表格

                // 获取得到的个人详细信息
                HttpEntity infoGetEntity = infoGetResponse.getEntity();
                System.out.println(EntityUtils.toString(infoGetEntity));
                System.out.println("Response Line: " + infoGetResponse.getStatusLine());
                System.out.println("Cookie: " + infoGetResponse.getFirstHeader("Set-Cookie").getValue());
                htmlCode = infoGetResponse.getStatusLine().getStatusCode();
                EntityUtils.consume(infoGetEntity);
            } finally {
                loginPostResponse.close();
                indexGetResponse.close();
                infoGetResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlCode;    // 返回网页状态码
    }
    public static void main(String args[]){
        getLoginInfo("13100501", "Peace0528");
    }
}