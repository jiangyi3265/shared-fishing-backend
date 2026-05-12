package com.ruoyi.fishing.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.fishing.domain.FishGroupFishing;
import com.ruoyi.fishing.domain.FishGroupMember;
import com.ruoyi.fishing.mapper.FishGroupFishingMapper;
import com.ruoyi.fishing.service.IFishGroupService;
import com.ruoyi.fishing.service.IFishUserService;

@Service
public class FishGroupServiceImpl implements IFishGroupService
{
    @Autowired private FishGroupFishingMapper mapper;
    @Autowired private IFishUserService userService;

    @Override public FishGroupFishing selectById(Long groupId) {
        FishGroupFishing g = mapper.selectById(groupId);
        if (g != null) g.setMembers(mapper.selectMembers(groupId));
        return g;
    }
    @Override public List<FishGroupFishing> selectList(FishGroupFishing q) { return mapper.selectList(q); }
    @Override public List<FishGroupFishing> selectActiveList(Long venueId) { return mapper.selectActiveList(venueId); }
    @Override public List<FishGroupFishing> selectByUser(Long userId) { return mapper.selectByUser(userId); }

    @Override
    @Transactional
    public FishGroupFishing create(FishGroupFishing g) {
        userService.assertNotBlacklisted(g.getUserId());
        if (g.getMaxMembers() == null || g.getMaxMembers() < 2) g.setMaxMembers(4);
        g.setCurrentCount(1);
        g.setStatus(0);
        mapper.insert(g);

        FishGroupMember m = new FishGroupMember();
        m.setGroupId(g.getGroupId());
        m.setUserId(g.getUserId());
        m.setRole("creator");
        mapper.insertMember(m);
        return g;
    }

    @Override
    @Transactional
    public int join(Long groupId, Long userId) {
        userService.assertNotBlacklisted(userId);
        FishGroupFishing g = mapper.selectById(groupId);
        if (g == null) throw new ServiceException("拼场不存在");
        if (g.getStatus() != 0) throw new ServiceException("该拼场已满员或已结束");

        FishGroupMember existing = mapper.selectMember(groupId, userId);
        if (existing != null) throw new ServiceException("您已加入该拼场");

        if (g.getCurrentCount() >= g.getMaxMembers()) throw new ServiceException("人数已满");

        FishGroupMember m = new FishGroupMember();
        m.setGroupId(groupId);
        m.setUserId(userId);
        m.setRole("member");
        mapper.insertMember(m);
        mapper.incrementCount(groupId);

        if (g.getCurrentCount() + 1 >= g.getMaxMembers()) {
            mapper.updateStatus(groupId, 1);
        }
        return 1;
    }

    @Override
    @Transactional
    public int quit(Long groupId, Long userId) {
        FishGroupFishing g = mapper.selectById(groupId);
        if (g == null) throw new ServiceException("拼场不存在");
        if (g.getUserId().equals(userId)) throw new ServiceException("发起人不能退出，请取消拼场");

        FishGroupMember existing = mapper.selectMember(groupId, userId);
        if (existing == null) throw new ServiceException("您未加入该拼场");

        mapper.updateMemberStatus(groupId, userId, 1);
        mapper.decrementCount(groupId);
        if (g.getStatus() == 1) mapper.updateStatus(groupId, 0);
        return 1;
    }

    @Override
    @Transactional
    public int cancel(Long groupId, Long userId) {
        FishGroupFishing g = mapper.selectById(groupId);
        if (g == null) throw new ServiceException("拼场不存在");
        if (userId != null && !g.getUserId().equals(userId)) throw new ServiceException("只有发起人可以取消");
        mapper.updateStatus(groupId, 3);
        return 1;
    }
}
