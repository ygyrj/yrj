package com.ruoyi.system.service.impl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 参数配置 服务层实现
 * YRJ(4.23)
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;



    /**
     * 项目启动时，初始化参数到缓存
     */


    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId) {
        return configMapper.selectConfigById(configId);
    }


    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue=Convert.toStr(CacheUtils.get(getCacheName(),getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)){
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig=configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig)){
            CacheUtils.put(getCacheName(),getCacheKey(configKey),retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }


    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
       int row =configMapper.insertConfig(config);
       if (row>0){
           CacheUtils.put(getCacheName(),getCacheKey(config.getConfigKey()),config.getConfigValue());
       }
        return row;
    }


    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        SysConfig temp = configMapper.selectConfigById(config.getConfigId());
        if (StringUtils.equals(temp.getConfigKey(),config.getConfigKey())){
            CacheUtils.remove(getCacheName(),getCacheKey(temp.getConfigKey()));
        }
        config.setUpdateTime(new Date());
        int row = configMapper.updateConfig(config);
        if (row > 0){
            CacheUtils.put(getCacheName(),getCacheKey(config.getConfigKey()),config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数配置对象
     * 
     * @param ids 需要删除的数据ID
     */
    @Override
    public void deleteConfigByIds(String ids) {
        Long[] configIds=Convert.toLongArray(ids);
        for (Long configId:configIds) {
            SysConfig config=selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES,config.getConfigType())){
//              // String.format("内置参数【%1$s】"，%s表示字符串，1：后面第一位，$：占位符
                throw new ServiceException(String.format("内置参数【%1$s】",
                        config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            CacheUtils.remove(getCacheName(),getCacheKey(config.getConfigKey()));
        }
    }


    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configList=configMapper.selectConfigList(new SysConfig());
        for (SysConfig config:configList) {
            CacheUtils.put(getCacheName(),getCacheKey(config.getConfigKey()),config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        CacheUtils.removeAll(getCacheName());
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        Long configId = StringUtils.isNull(config.getConfigId()) ? 1L:config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()){
            return UserConstants.CONFIG_KEY_NOT_UNIQUE;
        }
        return UserConstants.CONFIG_KEY_UNIQUE;
    }

    /**
     * 获取cache name
     * 
     * @return 缓存名
     */
    private String getCacheName(){
        return Constants.SYS_CONFIG_CACHE;
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey){
        return Constants.SYS_CONFIG_KEY + configKey;
    }

}
