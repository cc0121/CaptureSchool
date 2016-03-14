import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ImitateLogin {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * 高校教务系统，模拟POST登陆
     *
     * @param username 学号
     * @param password 密码
     * @return
     */
    public static int getLoginCookie(String username, String password) {
        System.out.println("--------Get Cookie for Login---------");
        String loginUrl = "http://zf.ahau.edu.cn/Default2.aspx";
        int htmlCode = 0;

        //模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
        HttpPost httpPost = new HttpPost(loginUrl);
        System.out.println("request line : " + httpPost.getRequestLine());

        try {
            //设置登陆信息，POST提交数据
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("__VIEWSTATE", "dDwtMTgzNTQyOTM0O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDExPjs+O2w8dDxwPDtwPGw8b25jbGljazs+O2w8d2luZG93LmNsb3NlKClcOzs+Pj47Oz47Pj47Pj47bDxDaGVja0JveDE7Pj4jTSDOG8YeEoLssydszmMECrKaiw=="));
            nvps.add(new BasicNameValuePair("yh", username));
            nvps.add(new BasicNameValuePair("kl", password));
            nvps.add(new BasicNameValuePair("RadioButtonList1", "%D1%A7%C9%FA"));    //“学生”的 urlEncode编码
            nvps.add(new BasicNameValuePair("Button1", "%B5%C7++%C2%BC"));        //“登录”的 urlEncode编码
            nvps.add(new BasicNameValuePair("CheckBox1", "on"));              // 代表接受许可

            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpClient.execute(httpPost);

            try {
                // 获取返回数据
                HttpEntity entity = response.getEntity();
                System.out.println("Response Line: " + response.getStatusLine());
                System.out.println("Cookie: " + response.getFirstHeader("Set-Cookie").getValue());
                htmlCode = response.getStatusLine().getStatusCode();
                EntityUtils.consume(entity);
            } finally {
                response.close();
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
        getLoginCookie("13100501", "xyz1994528");
    }
}