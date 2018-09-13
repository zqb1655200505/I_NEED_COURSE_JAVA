import net.sf.json.JSONArray;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;

public class Main {


    public static final String CHARTSET = "UTF-8";
    public static String userAgent =  "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729)";
    public static final String entryURL="http://grdms.bit.edu.cn/yjs/dwr/call/plaincall/YYPYCommonDWRController.pyJxjhSelectCourse.dwr";


    public static void main(String[] args) throws InterruptedException {
        String cookie=txt2String(new File("cookie.txt"));
        String dataContent=txt2String(new File("configure.txt"));
        dataContent=dataContent.substring(0,dataContent.length()-2);
        String[] dataContents=dataContent.split(System.lineSeparator()+System.lineSeparator());
        //System.out.println(dataContents.length);
        grabLesson(dataContents,cookie);
    }


    public static void grabLesson(String[] dataContents,String cookies) throws InterruptedException {
        HttpPost httpost=null;
        String status;
        String msg="";
        while (true)
        {
            try {
                for(String dataContent : dataContents)
                {
                    httpost = new HttpPost(entryURL); // 设置响应头信息
                    httpost.addHeader("Accept","*/*");
                    httpost.addHeader("Content-Type", "text/plain");
                    httpost.addHeader("Cookie",cookies);
                    httpost.addHeader("Host","grdms.bit.edu.cn");
                    httpost.addHeader("User-Agent",userAgent);
                    RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000)
                            .setConnectTimeout(3000).build();
                    httpost.setConfig(requestConfig);
                    httpost.setEntity(new StringEntity(dataContent, "UTF-8"));
                    String returnStr = null;
                    HttpResponse response = HttpClientBuilder.create().build().execute(httpost);
                    returnStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    int start=returnStr.indexOf("[");
                    int end=returnStr.indexOf("]")+1;
                    returnStr=returnStr.substring(start,end);
                    JSONArray array=JSONArray.fromObject(returnStr);
                    status=array.get(0).toString();
                    msg=new String(array.get(1).toString().getBytes(),CHARTSET);

                    if(status.equals("success"))
                    {
                        System.out.println("抢课成功，即将自动退出！！");
                    }
                    else if(status.equals("failure"))
                    {
                        System.out.println("抢课失败，失败原因为："+msg);
                    }
                    else
                    {
                        System.out.println("状态未知");
                        System.out.println("status："+status);
                        System.out.println("msg："+msg);
                    }
                    Thread.sleep(1000);
                }
            }
            catch (Exception e) {
                System.out.println("请求错误,若多次出现该错误，请检查配置文件！！！");
                Thread.sleep(1000);
            }
            finally {
                httpost.releaseConnection();
            }
        }

    }


    public static String txt2String(File file)
    {
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s+System.lineSeparator());
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
}
