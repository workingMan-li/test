package cn.itcast.core.controller.upload;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.itcast.core.entity.Result;
import cn.itcast.utils.fdfs.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;

	/**
	 * 
	 * @Title: uploadFile
	 * @Description: 商品图片上传
	 * @param file
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/uploadFile.do")
	public Result uploadFile(MultipartFile file){
		try {
			// 将附件上传到FastDFS上
			String conf = "classpath:fdfs/fdfs_client.conf";
			FastDFSClient fastDFSClient = new FastDFSClient(conf);
			String filename = file.getOriginalFilename(); // xxx.jpg
			String extName = FilenameUtils.getExtension(filename);
			String path = fastDFSClient.uploadFile(file.getBytes(), extName, null);
			// 常量维护：接口、配置文件中、枚举
			path = FILE_SERVER_URL + path;
			return new Result(true, path);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}
}
