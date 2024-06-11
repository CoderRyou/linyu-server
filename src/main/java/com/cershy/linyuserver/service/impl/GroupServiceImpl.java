package com.cershy.linyuserver.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cershy.linyuserver.entity.Group;
import com.cershy.linyuserver.mapper.GroupMapper;
import com.cershy.linyuserver.service.FriendService;
import com.cershy.linyuserver.service.GroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cershy.linyuserver.vo.group.CreateGroupVo;
import com.cershy.linyuserver.vo.group.DeleteGroupVo;
import com.cershy.linyuserver.vo.group.UpdateGroupVo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.security.auth.callback.Callback;
import java.util.List;

/**
 * <p>
 * 分组表 服务实现类
 * </p>
 *
 * @author heath
 * @since 2024-05-18
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Lazy
    @Resource
    FriendService friendService;

    @Override
    public List<Group> getGroupByUserId(String userId) {
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getUserId, userId).orderByAsc(Group::getName);
        return list(queryWrapper);
    }

    @Override
    public boolean createGroup(String userId, CreateGroupVo createGroupVo) {
        Group group = new Group();
        group.setId(IdUtil.randomUUID());
        group.setUserId(userId);
        group.setName(createGroupVo.getGroupName());
        return save(group);
    }

    @Override
    public boolean updateGroup(String userId, UpdateGroupVo updateGroupVo) {
        LambdaUpdateWrapper<Group> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Group::getName, updateGroupVo.getGroupName())
                .eq(Group::getUserId, userId)
                .eq(Group::getId, updateGroupVo.getGroupId());
        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean deleteGroup(String userId, DeleteGroupVo deleteGroupVo) {
        //将该分组下好友设置为未分组
        friendService.updateGroupId(userId, deleteGroupVo.getGroupId(), "0");
        //删除分组
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getId, deleteGroupVo.getGroupId())
                .eq(Group::getUserId, userId);
        return remove(queryWrapper);
    }
}
