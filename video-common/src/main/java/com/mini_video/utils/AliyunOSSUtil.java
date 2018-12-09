package com.mini_video.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AliyunOSSUtil {

    private static Log log = LogFactory.getLog(AliyunOSSUtil.class);

    private OSSClient client;

    @Autowired
    private CommonConstants commonConstants;

    public OSSClient getClient() {
        return client;
    }

    public void setClient(OSSClient client) {
        this.client = client;
    }


    public AliyunOSSUtil() {
    }

    public void setOssClient() {

        if (client == null) {
            String accessKeyId = commonConstants.getAccessKey();
            String accessKeySecret = commonConstants.getAccessSecret();
            client = new OSSClient(commonConstants.getEndpoint(), accessKeyId, accessKeySecret);
        }
    }


    public String getBucketName() {
        return commonConstants.getBucketName();
    }


    public String getEndpoint() {
        return commonConstants.getEndpoint();
    }

    public void close() {
        client.shutdown();
    }

    /**
     * @param fileName
     * @return
     */
    public InputStream getOssFileStream(String fileName) {
        setOssClient();
        String bucketName = getBucketName();
        OSSObject ossObject = client.getObject(bucketName, fileName);
        return ossObject.getObjectContent();
    }


    public File downloadOssFileToTmp(String fileName, String tempDir) {
        setOssClient();
        String bucketName = getBucketName();
        OSSObject ossObject = client.getObject(bucketName, fileName);
        InputStream inputStream = ossObject.getObjectContent();
        String downLoadFileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        File htmlFile = new File(tempDir + "/" + downLoadFileName);
        try {
            FileOutputStream fos = new FileOutputStream(htmlFile);
            byte[] car = new byte[1024];
            int L;
            while ((L = inputStream.read(car)) != -1) {
                if (car.length != 0) {
                    fos.write(car, 0, L);
                }
            }
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {

        }
        return htmlFile;

    }

    /**
     * 流式下载
     *
     * @param fileName
     * @param response
     * @return
     */
    public boolean downloadOssFile(String fileName, HttpServletResponse response) {
        try {
            setOssClient();
            String bucketName = getBucketName();
            OSSObject ossObject = client.getObject(bucketName, fileName);
            // 已缓冲的方式从字符输入流中读取文本，缓冲各个字符，从而提供字符、数组和行的高效读取
//        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));

            InputStream inputStream = ossObject.getObjectContent();
            //缓冲文件输出流
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            //通知浏览器以附件形式下载
            //这里设置一下让浏览器弹出下载提示框，而不是直接在浏览器中打开
            String downLoadFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            downLoadFileName = URLEncoder.encode(downLoadFileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + downLoadFileName);

            byte[] car = new byte[1024];
            int L;
            while ((L = inputStream.read(car)) != -1) {
                if (car.length != 0) {
                    outputStream.write(car, 0, L);
                }
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }

        } catch (IOException e) {
            log.error(e.toString());
            return false;
        } catch (OSSException e) {
            log.error(e.toString());
            return false;
        }
        return true;
    }

    public String uploadFile(String key, String file) {

        String resultStr = null;
        try {
            setOssClient();
            String bucketName = getBucketName();
            log.info("bucketName: " + bucketName);
            log.info("key: " + key);
            log.info("file: " + file);

            PutObjectResult putResult = client.putObject(bucketName, key, new File(file));
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return resultStr;
    }


    public String uploadFile(String key, File file) {
        String resultStr = null;
        try {
            setOssClient();
            String bucketName = getBucketName();
            log.info("bucketName: " + bucketName);
            log.info("key: " + key);
            log.info("file: " + file);

            PutObjectResult putResult = client.putObject(bucketName, key, file);
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return resultStr;
    }


    public String uploadFileByUrl(String key, String urlStr) throws IOException {
        String resultStr = null;
        try {
            setOssClient();
            String bucketName = getBucketName();
            log.info("bucketName: " + bucketName);
            log.info("key: " + key);
            log.info("urlStr: " + urlStr);

            InputStream inputStream = new URL(urlStr).openStream();
            PutObjectResult putResult = client.putObject(bucketName, key, inputStream);
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return resultStr;
    }


    public String uploadFileProgressByUrl(String key, String urlStr) throws IOException {
        String resultStr = null;
        try {
            setOssClient();
            String bucketName = getBucketName();
            log.info("bucketName: " + bucketName);
            log.info("key: " + key);
            log.info("urlStr: " + urlStr);

            InputStream inputStream = new URL(urlStr).openStream();
//            PutObjectResult putResult = client.putObject(bucketName, key, inputStream);
            // 带进度条的上传
            PutObjectResult putResult = client.putObject(new PutObjectRequest(bucketName, key, inputStream).
                    <PutObjectRequest>withProgressListener(new PutObjectProgressListener()));
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return resultStr;
    }


    public boolean deleteObject(String key) {
        boolean status = false;
        try {
            setOssClient();
            String bucketName = getBucketName();
            log.info("bucketName : " + bucketName);
            client.deleteObject(bucketName, key);
            status = true;
        } catch (Exception e) {
            log.error(e.toString(), e);
            status = false;
        }
        return status;

    }


    public boolean deleteObjects(List<String> keys) {
        boolean status = false;
        try {
            setOssClient();
            String bucketName = getBucketName();
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
            request.withKeys(keys);
            client.deleteObjects(request);
            status = true;
        } catch (Exception e) {
            log.error(e.toString());
            status = false;
        }
        return status;

    }


    /**
     * @param inputStream
     * @Author:Canyon
     * @param: * @param key
     * @Description:
     * @Date:12:11 2017/10/30
     */
    public String uploadInputStream(String key, InputStream inputStream) {

        String resultStr = null;

        try {
            setOssClient();
            String bucketName = getBucketName();
            PutObjectResult putResult = client.putObject(bucketName, key, inputStream);
            //解析结果
            resultStr = putResult.getETag();
            log.info("resultStr--" + resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }

    public String uploadProgressInputStream(String key, InputStream inputStream, long size) {

        String resultStr = null;

        try {
            setOssClient();
            String bucketName = getBucketName();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream);
            PutObjectProgressListener progressListener = new PutObjectProgressListener(size, 81920);
            putObjectRequest.withProgressListener(progressListener);

            PutObjectResult putResult = client.putObject(putObjectRequest);
            log.info("progress:  " + progressListener.bytesWritten);
            //解析结果
            resultStr = putResult.getETag();
            log.info("resultStr--" + resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }


    public String uploadFileStream(String key, String file) {
        String resultStr = null;
        try {
            setOssClient();

            String bucketName = getBucketName();

            File ff = new File(file);
            //文件名
            String fileName = ff.getName();
            //文件大小
            Long fileSize = ff.length();
            //以输入流的形式上传文件
            InputStream ism = new FileInputStream(file);
            //创建上传ObjectMetadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(ism.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));

            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");

            PutObjectResult putResult = client.putObject(bucketName, key, ism, metadata);
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }


    public String uploadFileRequest(String key, String filename) {
        String resultStr = null;
        try {
            setOssClient();
            String bucketName = getBucketName();
            log.info("bucketName: " + bucketName + " key: " + key + " filename: " + filename);

            // 设置断点续传请求
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            // 指定上传的本地文件
            uploadFileRequest.setUploadFile(filename);
            // 指定上传并发线程数
            uploadFileRequest.setTaskNum(5);
            // 指定上传的分片大小 1M
            uploadFileRequest.setPartSize(1 * 1024 * 1024);
            // 开启断点续传
            uploadFileRequest.setEnableCheckpoint(true);
            // 断点续传上传
            UploadFileResult uploadFileResult = client.uploadFile(uploadFileRequest);
            resultStr = uploadFileResult.getMultipartUploadResult().getETag();
            String location = uploadFileResult.getMultipartUploadResult().getLocation();
            log.info("eTag: " + resultStr + " location:" + location);
            // 关闭client
//            client.shutdown();
        } catch (Throwable e) {
            log.error(e.toString());
        }
        return resultStr;
    }


    public static String getContentType(String fileName) {
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        //默认返回类型
        return "image/jpeg";
    }


    /**
     * 上传进度条
     */
    static class PutObjectProgressListener implements ProgressListener {

        private long bytesWritten = 0;
        private long totalBytes = -1;
        private boolean succeed = false;
        private String sessionId = null;
        private MData paramMap = new MData();

        private long totalBytesSelf = -1;

        public PutObjectProgressListener(long totalBytesSelf) {
            this.totalBytesSelf = totalBytesSelf;
        }

        public PutObjectProgressListener(long totalBytesSelf, String sessionId) {
            this.totalBytesSelf = totalBytesSelf;
            this.sessionId = sessionId;
        }

        public PutObjectProgressListener(long totalBytesSelf, long bytesWritten) {
            this.totalBytesSelf = totalBytesSelf;
            this.bytesWritten = bytesWritten;
        }

        public PutObjectProgressListener() {
        }

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            long bytes = progressEvent.getBytes();
            ProgressEventType eventType = progressEvent.getEventType();
            switch (eventType) {
                case TRANSFER_STARTED_EVENT:
                    System.out.println("Start to upload......");
                    break;

                case REQUEST_CONTENT_LENGTH_EVENT:
                    this.totalBytes = bytes;
                    System.out.println(this.totalBytes + " bytes in total will be uploaded to OSS");
                    break;

                case REQUEST_BYTE_TRANSFER_EVENT:
                    this.bytesWritten += bytes;
                    if (this.totalBytes != -1) {
                        int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                        System.out.println(bytes + " bytes have been written at this time, upload progress: " +
                                percent + "%(" + this.bytesWritten + "/" + this.totalBytes + ")");
                    } else {
                        int percent = (int) (this.bytesWritten * 100.0 / this.totalBytesSelf);
                        System.out.println(bytes + " bytes have been written at this time, upload ratio: unknown " +
                                percent + "%(" + this.bytesWritten + "/...)");

                    }
                    break;

                case TRANSFER_COMPLETED_EVENT:
                    this.succeed = true;
                    System.out.println("Succeed to upload, " + this.bytesWritten + " bytes have been transferred in total");
                    break;

                case TRANSFER_FAILED_EVENT:
                    System.out.println("Failed to upload, " + this.bytesWritten + " bytes have been transferred");
                    break;

                default:
                    break;
            }
        }

        public boolean isSucceed() {
            System.out.println("is successed");
            return succeed;
        }
    }

    /**
     * 获取Policy签名等信息
     *
     * @param dir            存储在bucket的目录
     * @param expiredSeconds 过期时间
     * @return
     */
    public PostObjectPolicy getPostObjectPolicy(String dir, long expiredSeconds) {
        setOssClient();
        long expireEndTime = System.currentTimeMillis() + expiredSeconds * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = getClient().generatePostPolicy(expiration, policyConds);
        byte[] binaryData = new byte[0];
        try {
            binaryData = postPolicy.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = getClient().calculatePostSignature(postPolicy);

        PostObjectPolicy policy = new PostObjectPolicy();
        policy.setAccessId(commonConstants.getAccessKey());
        policy.setHost(commonConstants.getOssHost());
        policy.setDir(dir);
        policy.setExpire(String.valueOf(expireEndTime / 1000));
        policy.setPolicy(encodedPolicy);
        policy.setSignature(postSignature);
        return policy;
    }


    /**
     * 验证回调是否是阿里发送的
     *
     * @param request
     * @param ossCallbackBody
     * @return
     * @throws NumberFormatException
     * @throws IOException
     */
    public static boolean verifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody) {
        boolean ret = false;
        try {
            String autorizationInput = new String(request.getHeader("Authorization"));
            String pubKeyInput = request.getHeader("x-oss-pub-key-url");
            byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
            byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
            String pubKeyAddr = new String(pubKey);
            //地址判断，固定的
            if (!pubKeyAddr.startsWith("http://gosspublic.alicdn.com/") && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")) {
                log.error("pub key addr must be oss addrss");
                return false;
            }
            String retString = executeGet(pubKeyAddr);
            retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
            retString = retString.replace("-----END PUBLIC KEY-----", "");
            String queryString = request.getQueryString();
            String uri = request.getRequestURI();
            String decodeUri = java.net.URLDecoder.decode(uri, "UTF-8");
            String authStr = decodeUri;
            if (queryString != null && !queryString.equals("")) {
                authStr += "?" + queryString;
            }
            authStr += "\n" + new String(ossCallbackBody.getBytes("UTF-8"), "UTF-8");
            ret = doCheck(authStr, authorization, retString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 通过oss查exif
     *
     * @param path
     * @return
     */
    public Map<String, Map<String, String>> queryExifInfo(String path) {
        String url = commonConstants.getOssHost() + "/" + path + "?x-oss-process=image/info;";
        return null;
    }


    private static boolean doCheck(String content, byte[] sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes());
            boolean bverify = signature.verify(sign);
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String executeGet(String url) {
        BufferedReader in = null;

        String content = null;
        try {
            // 定义HttpClient
            @SuppressWarnings("resource")
            DefaultHttpClient client = new DefaultHttpClient();
            // 实例化HTTP方法
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            content = sb.toString();
        } catch (Exception e) {
        } finally {
            if (in != null) {
                try {

                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return content;
        }
    }


}
