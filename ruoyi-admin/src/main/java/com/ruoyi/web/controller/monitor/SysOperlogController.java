package com.ruoyi.web.controller.monitor;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.service.ISysOperLogService;

/**
 * 操作日志记录
 * YRJ(4.17)
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController{
    private String prefix = "monitor/ooperlog";

    @Autowired
    private ISysOperLogService operLogService;

    @RequiresPermissions("monitor:operlog:view")
    @GetMapping()
    public String operLog(){
        return prefix + "/operrlog";
    }

    @RequiresPermissions("monitor:operlog:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysOperLog operLog){
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    /**
     * 导出
     */
    @Log(title = "操作日志",businessType = BusinessType.EXPORT)
    @RequiresPermissions("monitor:operlog:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysOperLog operLog){
        List<SysOperLog> list=operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        return util.exportExcel(list,"操作日志");
    }

    /**
     * 删除
     */
    @Log(title = "操作日志",businessType = BusinessType.DELETE)
    @RequiresPermissions("monitor:operlog:remove")
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        return toAjax(operLogService.deleteOperLogByIds(ids));
    }

    /**
     * 查询日志详情
     */
    @RequiresPermissions("monitor:operlog:detail")
    @GetMapping("/detail/{operId}")
    public String detail(@PathVariable("operId") Long operId,ModelMap mmap){
        mmap.put("operLog",operLogService.selectOperLogById(operId));
        return prefix + "/detail";
    }

    @RequiresPermissions("monitor:operlog:remove")
    @Log(title = "操作日志",businessType = BusinessType.CLEAN)
    @PostMapping("/clean")
    @ResponseBody
    public AjaxResult clean(){
        operLogService.cleanOperLog();
        return success();
    }

}

