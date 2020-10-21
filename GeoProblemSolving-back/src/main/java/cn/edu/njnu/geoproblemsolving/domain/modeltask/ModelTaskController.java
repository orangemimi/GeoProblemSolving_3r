package cn.edu.njnu.geoproblemsolving.domain.modeltask;

import cn.edu.njnu.geoproblemsolving.Utils.ResultUtils;
import cn.edu.njnu.geoproblemsolving.domain.support.JsonResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/modelTask")
public class ModelTaskController {
    @Autowired
    ModelTaskService modelTaskService;

    @RequestMapping(value = "/getModelBehavior/{doi}", method = RequestMethod.GET)
    public Object readProject(@PathVariable("doi") String doi) {
        return ResultUtils.success(modelTaskService.getComputeModel(doi));
    }

    @RequestMapping(value = "/getAllService/{asc}/{page}", method = RequestMethod.GET)
    public Object getAllService(@PathVariable("asc") String asc,@PathVariable("page") String page) {
        return modelTaskService.getAllService(asc,page);
    }

    @RequestMapping(value = "/createTask/{pid}/{userId}", method = RequestMethod.GET)
    JsonResult createTask(@PathVariable("pid") String pid, @PathVariable("userId") String userId) {
        return ResultUtils.success(modelTaskService.createTask(pid, userId));
    }

//    无需配置文件的上传接口
//    @RequestMapping(value = "/uploadSingle", method = RequestMethod.POST)
//    public JsonResult uploadData(HttpServletRequest request) throws IOException, ServletException {
//        String userId = (String) request.getSession().getAttribute("userId");
//        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
//        Part part = multiRequest.getPart("file");
//        String header = part.getHeader("Content-Disposition");
//        String filename2 = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
//       //  获取文件后缀名
//        String suffix = "." + FilenameUtils.getExtension(filename2);
//        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
//        FileUtils.copyInputStreamToFile(multiRequest.getPart("file").getInputStream(), file);
//        String url = modelTaskService.upload(file,userId);
//        return ResultUtils.success(url);
//    }

    //    需配置文件的上传接口
//    @RequestMapping(value = "/uploadFileForm", method = RequestMethod.POST)
//    public Object uploadFile(HttpServletRequest request) throws IOException, ServletException {
//        MultipartHttpServletRequest request1 = (MultipartHttpServletRequest) request;
//        Collection<Part> files = request1.getParts();
//        String userId = (String) request.getSession().getAttribute("userId");
//        return ResultUtils.success(modelTaskService.uploadFileForm(files,userId));
//    }


    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    JsonResult invoke(@RequestBody JSONObject obj) {
        return ResultUtils.success(modelTaskService.invoke(obj));
    }

    @RequestMapping(value = "/getRecord", method = RequestMethod.POST)
    JsonResult getResult(@RequestBody JSONObject data) {
        return ResultUtils.success(modelTaskService.getRecord(data));
    }

}
