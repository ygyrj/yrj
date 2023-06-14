package com.ruoyi.system.service.impl;

import java.text.MessageFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.mapper.SysRoleMenuMapper;
import com.ruoyi.system.service.ISysMenuService;

/**
 * 菜单 业务层处理
 * YRJ(5.10)
 * @author ruoyi
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService
{
    //字符串许可证
    public static final String PREMISSION_STRING = "PERMS[\"{0}\"]";

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询菜单
     * 
     * @param user 用户信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenusByUser(SysUser user) {
        List<SysMenu> menus = new LinkedList<SysMenu>();
        //管理员显示所有菜单
        if(user.isAdmin()){
            menus=menuMapper.selectMenuNormalAll();
        }else {
            //其他用户菜单
            menus=menuMapper.selectMenusByUserId(user.getUserId());
        }
        return menus;
    }

    /**
     * 查询菜单集合（根据条件查询）
     * 根据“菜单名称”，“是否显示”条件查询
     * @return 所有菜单信息(List)
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList = null;
        if (SysUser.isAdmin(userId)){
            //查询管理员菜单（菜单名称，是否显示）
            menuList=menuMapper.selectMenuList(menu);
        }else {
            //查询普通用户所有菜单
            menu.getParams().put("userId",userId);
            menuList=menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }


    /**
     * 查询菜单集合(All，无查询条件)
     *  查询所有菜单，无查询条件
     * @return 所有菜单信息
     */
    @Override
    public List<SysMenu> selectMenuAll(Long userId) {
        List<SysMenu> menuList = null;
        if (SysUser.isAdmin(userId)){
            //查询管理员菜单（无查询条件）
            menuList=menuMapper.selectMenuAll();
        }else {
            //查询普通用户所有菜单
            menuList=menuMapper.selectMenuAllByUserId(userId);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */


    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */


    /**
     * 根据角色ID查询菜单
     * 
     * @param role 角色对象
     * @return 菜单列表
     */
    @Override
    public List<Ztree> roleMenuTreeData(SysRole role, Long userId) {
        Long roleId=role.getRoleId();
        List<Ztree> ztrees = new ArrayList<Ztree>();
        //查询所有菜单信息
        List<SysMenu> menuList = selectMenuAll(userId);
        if (StringUtils.isNotNull(roleId)){
            List<String> roleMenuList=menuMapper.selectMenuTree(roleId);
            ztrees=initZtree(menuList,roleMenuList,true);
        }else {
            ztrees=initZtree(menuList,null,true);
        }
        return ztrees;
    }

    /**
     * 查询所有菜单
     * 
     * @return 菜单列表
     */
    @Override
    public List<Ztree> menuTreeData(Long userId) {
        List<SysMenu> menuList=selectMenuAll(userId);
        List<Ztree> ztrees = initZtree(menuList);
        return ztrees;
    }


    /**
     * 查询系统所有权限
     * 
     * @return 权限列表
     */
    @Override
    public Map<String, String> selectPermsAll(Long userId) {
        LinkedHashMap<String,String> section = new LinkedHashMap<>();
        List<SysMenu> parmissions=selectMenuAll(userId);
        if (StringUtils.isNotEmpty(parmissions)){
            for (SysMenu menu:parmissions) {
                section.put(menu.getUrl(),MessageFormat.format(PREMISSION_STRING,menu.getPerms()));
            }
        }
        return section;
    }


    /**
     * 对象转菜单树
     * 
     * @param menuList 菜单列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysMenu> menuList){
        return initZtree(menuList,null,false);
    }

    /**
     * 对象转菜单树
     * 
     * @param menuList 菜单列表
     * @param roleMenuList 角色已存在菜单列表
     * @param permsFlag 是否需要显示权限标识
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysMenu> menuList,List<String> roleMenuList,boolean permsFlag){
        List<Ztree> ztrees = new ArrayList<Ztree>();
        boolean isCheck = StringUtils.isNotNull(roleMenuList);
        for (SysMenu menu:menuList) {
            Ztree ztree = new Ztree();
            ztree.setId(menu.getMenuId());
            ztree.setpId(menu.getParentId());
            ztree.setName(transMenuName(menu,permsFlag));
            ztree.setTitle(menu.getMenuName());
            if (isCheck){
                //contains 包含
                ztree.setChecked(roleMenuList.contains(menu.getMenuId() + menu.getPerms()));
            }
            ztrees.add(ztree);
        }
        return ztrees;
    }

    public String transMenuName(SysMenu menu,boolean permsFlag){
        StringBuffer sb = new StringBuffer();
        sb.append(menu.getMenuName());
        if (permsFlag){
            sb.append("<font color =\"#888\">&nbsp;&nbsp;&nbsp;"+menu.getPerms()+"</font>");
        }
        return toString();
    }

    /**
     * 删除菜单管理信息
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteMenuById(menuId);
    }



    /**
     * 根据菜单ID查询信息
     * 
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * 查询子菜单数量
     * 
     * @param parentId 父级菜单ID
     * @return 结果
     */
    @Override
    public int selectCountMenuByParentId(Long parentId) {
        return menuMapper.selectCountMenuByParentId(parentId);
    }

    /**
     * 查询菜单使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int selectCountRoleMenuByMenuId(Long menuId) {
        return roleMenuMapper.selectCountRoleMenuByMenuId(menuId);
    }


    /**
     * 新增保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insertMenu(menu);
    }


    /**
     * 修改保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateMenu(menu);
    }

    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu) {
        Long menuId=StringUtils.isNull(menu.getMenuId()) ? -1L :menu.getMenuId();
        SysMenu info=menuMapper.checkMenuNameUnique(menu.getMenuName(),menu.getParentId());
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()){
            return UserConstants.MENU_NAME_NOT_UNIQUE;
        }
        return UserConstants.MENU_NAME_UNIQUE;
    }

    /**
     * 根据父节点的ID获取所有子节点
     * 
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */


    /**
     * 递归列表
     * 
     * @param list
     * @param t
     */


    /**
     * 得到子节点列表
     */


    /**
     * 判断是否有子节点
     */

}
