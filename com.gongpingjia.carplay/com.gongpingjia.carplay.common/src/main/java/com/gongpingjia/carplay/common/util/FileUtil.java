package com.gongpingjia.carplay.common.util;

import com.gongpingjia.carplay.common.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by 123 on 2015/11/4.
 */
public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 将指定本地路径的文件转换成byte数组
     *
     * @param filePath
     * @return
     */
    public static byte[] buildFileBytes(String filePath) {
        return buildFileBytes(new File(filePath));
    }

    /**
     * 将文件转换成byte数组
     *
     * @param file
     * @return
     */
    public static byte[] buildFileBytes(File file) {
        if (!file.exists()) {
            LOG.info("User avatar is not exist in the local server");
            return new byte[0];
        }

        try {
            return buildFileBytes(new FileInputStream(file), false);
        } catch (ApiException e) {
            LOG.warn(e.getMessage());
        } catch (FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return new byte[0];
    }


    /**
     * 将网络媒体文件转换成字节流
     *
     * @param multiFile 网络媒体文件
     * @return 返回字节数组
     * @throws ApiException
     */
    public static byte[] buildFileBytes(MultipartFile multiFile) throws ApiException {
        try {
            return buildFileBytes(multiFile.getInputStream(), true);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
        return new byte[0];
    }

    /**
     * 根据文件流，转换成byte数组
     *
     * @param inputStream 文件流
     * @return 返回文件byte数组
     * @throws ApiException 文件读写异常
     */
    private static byte[] buildFileBytes(InputStream inputStream, boolean throwException) throws ApiException {
        BufferedInputStream bis = null;
        ByteArrayOutputStream out = null;
        byte[] fileContent = null;

        try {
            bis = new BufferedInputStream(inputStream);
            out = new ByteArrayOutputStream();

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = bis.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }

            fileContent = out.toByteArray();

        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            if (throwException) {
                throw new ApiException("上传文件失败");
            }
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    LOG.warn("Close BufferedInputStream bis failure at finally");
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.warn("Close ByteArrayOutputStream out failure at finally");
                }
            }
        }
        return fileContent;
    }
}
