package com.ruoyi.web.controller.monitor;

import java.util.List;

import com.ruoyi.framework.shiro.service.SysPasswordService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.service.ISysLogininforService;

/**
 * 系统访问记录
 * YRJ（4.17）
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {
    private String prefix = "monitorr/logininfor";

    @Autowired
    private ISysLogininforService logininforService;

    @Autowired
    private SysPasswordService passwordService;

    @RequiresPermissions("monitor:logininfor:view")
    @GetMapping
    public String logininfor() {
        return prefix + "/logininfor";
    }

    /**
     * 查询系统登录日志集合
     */
    @RequiresPermissions("monitor:logininfor:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysLogininfor logininfor) {
        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return getDataTable(list);
    }

    /**
     * 导出
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("monitor:logininfor:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysLogininfor logininfor) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        return util.exportExcel(list,"登录日志");
    }

    /**
     * 删除
     */
    @Log(title = "登录日志",businessType = BusinessType.DELETE)
    @RequiresPermissions("monitor:logininfor:romove")
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        return toAjax(logininforService.delectLogininforByIds(ids));
    }

    /**
     * 关闭
     */
    @Log(title = "登录日志",businessType = BusinessType.CLEAN)
    @RequiresPermissions("monitor:logininfor:clean")
    @PostMapping("/clean")
    @ResponseBody
    public AjaxResult clean(){
        logininforService.cleanLogininfor();
        return success();
    }

    /**
     * 账户解锁
     */
    @RequiresPermissions("monitor:logininfor:unlock")
    @Log(title = "账户解锁",businessType = BusinessType.OTHER)
    @PostMapping("/unlock")
    @ResponseBody
    public AjaxResult unlock(String loginName){
        passwordService.clearLoginRecordCache(loginName);
        return success();
    }

}

