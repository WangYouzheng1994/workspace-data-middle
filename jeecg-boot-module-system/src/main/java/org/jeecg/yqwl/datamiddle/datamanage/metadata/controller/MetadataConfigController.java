package org.jeecg.yqwl.datamiddle.datamanage.metadata.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.yqwl.datamiddle.datamanage.metadata.entity.MetadataConfig;
import org.jeecg.yqwl.datamiddle.datamanage.metadata.service.IMetadataConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: metadata_config
 * @Author: jeecg-boot
 * @Date:   2022-05-10
 * @Version: V1.0
 */
@Api(tags="metadata_config")
@RestController
@RequestMapping("/datamanage/metadataConfig")
@Slf4j
public class MetadataConfigController extends JeecgController<MetadataConfig, IMetadataConfigService> {
	@Autowired
	private IMetadataConfigService metadataConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param metadataConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "metadata_config-分页列表查询")
	@ApiOperation(value="metadata_config-分页列表查询", notes="metadata_config-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(MetadataConfig metadataConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<MetadataConfig> queryWrapper = QueryGenerator.initQueryWrapper(metadataConfig, req.getParameterMap());
		Page<MetadataConfig> page = new Page<MetadataConfig>(pageNo, pageSize);
		IPage<MetadataConfig> pageList = metadataConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param metadataConfig
	 * @return
	 */
	@AutoLog(value = "metadata_config-添加")
	@ApiOperation(value="metadata_config-添加", notes="metadata_config-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody MetadataConfig metadataConfig) {
		metadataConfigService.save(metadataConfig);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param metadataConfig
	 * @return
	 */
	@AutoLog(value = "metadata_config-编辑")
	@ApiOperation(value="metadata_config-编辑", notes="metadata_config-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody MetadataConfig metadataConfig) {
		metadataConfigService.updateById(metadataConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "metadata_config-通过id删除")
	@ApiOperation(value="metadata_config-通过id删除", notes="metadata_config-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		metadataConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "metadata_config-批量删除")
	@ApiOperation(value="metadata_config-批量删除", notes="metadata_config-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.metadataConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "metadata_config-通过id查询")
	@ApiOperation(value="metadata_config-通过id查询", notes="metadata_config-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		MetadataConfig metadataConfig = metadataConfigService.getById(id);
		if(metadataConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(metadataConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param metadataConfig
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MetadataConfig metadataConfig) {
        return super.exportXls(request, metadataConfig, MetadataConfig.class, "metadata_config");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, MetadataConfig.class);
    }

}
