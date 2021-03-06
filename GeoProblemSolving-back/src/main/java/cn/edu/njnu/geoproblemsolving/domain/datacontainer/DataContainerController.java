package cn.edu.njnu.geoproblemsolving.domain.datacontainer;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

/**
 * @ClassName DataContainerController
 * @Description
 * @Author Zhiyi
 * @Date 2019/12/16  21:58
 * @Version 1.0.0
 */
@RestController
@RequestMapping(value = "/dataContainer")
public class DataContainerController {
    @Autowired
    DataIContainerService dataIContainerService;

    @RequestMapping(value = "/addDataItem", method = RequestMethod.POST)
    public Object addDataItem(HttpServletRequest request) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("file");
        String header = part.getHeader("Content-Disposition");
        String filename2 = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
        // 获取文件名
        String fileName = part.getName();
        File file = File.createTempFile(part.getName(), "." + FilenameUtils.getExtension(filename2));//创建临时文件
        FileUtils.copyInputStreamToFile(multiRequest.getPart("file").getInputStream(), file);
        return dataIContainerService.addDataItem(file);
    }

    @RequestMapping(value = "/download/{uid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathVariable("uid") String uid) {
        return dataIContainerService.download(uid);
    }

    //    无需配置文件的上传接口
    @RequestMapping(value = "/uploadSingle", method = RequestMethod.POST)
    public JsonResult uploadData(HttpServletRequest request) throws IOException, ServletException {
        String userId = (String) request.getSession().getAttribute("userId");
        String userName = (String) request.getSession().getAttribute("userName");
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("datafile");
        String header = part.getHeader("Content-Disposition");
        String filename2 = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
        //  获取文件后缀名
        String suffix = "." + FilenameUtils.getExtension(filename2);
        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
        FileUtils.copyInputStreamToFile(part.getInputStream(), file);
        return ResultUtils.success(dataIContainerService.upload(file,userId,userName));
    }

}
