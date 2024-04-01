

package com.tcc.modules.oss.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.common.validator.ValidatorUtils;
import com.tcc.common.validator.group.AliyunGroup;
import com.tcc.common.validator.group.LocalGroup;
import com.tcc.common.validator.group.QcloudGroup;
import com.tcc.common.validator.group.QiniuGroup;
import com.tcc.modules.oss.cloud.CloudStorageConfig;
import com.tcc.modules.oss.cloud.OSSFactory;
import com.tcc.modules.oss.entity.SysOssEntity;
import com.tcc.modules.oss.service.SysOssService;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.service.SysConfigService;
import com.google.gson.Gson;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 文件上传
 *
 * @author 
 */
@RestController
@RequestMapping("sys/oss")
public class SysOssController extends AbstractBackController {
	@Autowired
	private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;

    private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;
	
	/**
	 * 列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:oss:all")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysOssService.queryPage(params);

		return R.ok().put("page", page);
	}


    /**
     * 云存储配置信息
     */
    @GetMapping("/config")
    @RequiresPermissions("sys:oss:all")
    public R config(){
        CloudStorageConfig config = sysConfigService.getConfigObject(getOrgId(), KEY, CloudStorageConfig.class);

        return R.ok().put("config", config);
    }


	/**
	 * 保存云存储配置信息
	 */
	@PostMapping("/saveConfig")
	@RequiresPermissions("sys:oss:all")
	public R saveConfig(@RequestBody CloudStorageConfig config){
		//校验类型
		ValidatorUtils.validateEntity(config);

		if(config.getType() == Constant.CloudService.QINIU.getValue()){
			//校验七牛数据
			ValidatorUtils.validateEntity(config, QiniuGroup.class);
		}else if(config.getType() == Constant.CloudService.ALIYUN.getValue()){
			//校验阿里云数据
			ValidatorUtils.validateEntity(config, AliyunGroup.class);
		}else if(config.getType() == Constant.CloudService.QCLOUD.getValue()){
			//校验腾讯云数据
			ValidatorUtils.validateEntity(config, QcloudGroup.class);
		}else if(config.getType() == Constant.CloudService.LOCAL.getValue()){
			//校验本地存储数据
			ValidatorUtils.validateEntity(config, LocalGroup.class);
		}

        sysConfigService.updateValueByKey(getOrgId(), KEY, new Gson().toJson(config));

		return R.ok();
	}
	

	/**
	 * 上传文件
	 */
	@PostMapping("/upload")
	public R upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new WTDPException("上传文件不能为空");
		}

		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String url = OSSFactory.build(getOrgId()).uploadSuffix(file.getBytes(), suffix);

		String fileType = FileTypeUtil.getType(file.getInputStream());
		if(StrUtil.isNotBlank(fileType)){
			suffix = fileType;
		}else{
			suffix = suffix.replace(".", "");
		}
		int type = 4;
		long duration = 0L;
		if(FileUtils.isImgBySuffix(suffix)){
			type = 1;
		}else if(FileUtils.isAudioBySuffix(suffix)){
			type = 2;
			//duration = FileUtils.getMidiaDuration(file);
		}else if(FileUtils.isVideoBySuffix(suffix)){
			type = 3;
			//duration = FileUtils.getMidiaDuration(file);
		}

		//保存文件信息
		SysOssEntity ossEntity = new SysOssEntity();
		ossEntity.setUrl(url);
		ossEntity.setCreateDate(new Date());
		ossEntity.setSuffix(suffix);
		ossEntity.setName(file.getOriginalFilename());
		ossEntity.setSize(file.getSize());
		ossEntity.setType(type);
		ossEntity.setDuration(duration);
		sysOssService.save(ossEntity);
		R r = R.ok();
		r.putAll(JSONUtil.parseObj(ossEntity));
		return r;
	}

	/**
	 * 上传文件
	 */
	@PostMapping("/uploadVoice")
	@RequiresPermissions("sys:oss:all")
	public R uploadVoice(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new WTDPException("上传文件不能为空");
		}
		String fileName = file.getOriginalFilename();
		// 文件后缀
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		// 用uuid作为文件名，防止生成的临时文件重复
		String uuid = IdUtil.simpleUUID();
		File localFile = File.createTempFile(uuid, suffix);
		file.transferTo(localFile);
		// 处理后的文件存储路径
		String outFilePath = localFile.getParent() + File.separator + uuid + "_" + RandomUtil.randomNumbers(3) + suffix;
		// 音频文件处理
		boolean vnResult = FfmpegUtils.volumeNormalizer(localFile.getPath(), outFilePath);
		if(!vnResult){
			return R.error("音量标准化失败");
		}
		// 原文件删除
		localFile.delete();
		// 处理后的文件
		File readyFile = new File(outFilePath);
		// 文件上传
		String url = OSSFactory.build(getOrgId()).uploadSuffix(new FileInputStream(readyFile), suffix);
		//保存文件信息
		SysOssEntity ossEntity = new SysOssEntity();
		ossEntity.setUrl(url);
		ossEntity.setCreateDate(new Date());
		sysOssService.save(ossEntity);
		// 获取音频时长
		BigDecimal time = NumberUtil.round(getMp3Time(readyFile), 3);
		long fileSize = file.getSize();
		readyFile.delete();
		return R.ok().put("fileUrl", url)
				.put("fileDuration", time)
				.put("fileSize", fileSize)
				.put("fileName", fileName);
	}

	public double getMp3Time(File file) {
		double t = 0;
		try {
			MP3File f = (MP3File) AudioFileIO.read(file);
			t = f.getMP3AudioHeader().getPreciseTrackLength();
			BigDecimal b = new BigDecimal(t);
			t = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + 0.026;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	@RequiresPermissions("sys:oss:all")
	public R delete(@RequestBody Long[] ids){
		sysOssService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
