package com.easychat.redis;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    public Long getUserHeartBeat(String userId) {
        return (Long) redisUtils.get(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId);
    }

    public void saveHeartBeat(String userId) {
        redisUtils.setex(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId, System.currentTimeMillis(), Constants.REDIS_KEY_EXPIRES_HEART_BEAT);
    }

    public void removeHeartBeat(String userId) {
        redisUtils.delete(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId);
    }

    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto) {
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken(), tokenUserInfoDto, Constants.REDIS_TIME_1DAY * 2);
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getUserId(), tokenUserInfoDto.getToken(), Constants.REDIS_TIME_1DAY * 2);
    }

    public TokenUserInfoDto getTokenUserInfoDto(String token) {
        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
        return tokenUserInfoDto;
    }

    public SysSettingDto getSysSetting() {
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        sysSettingDto = sysSettingDto == null ? new SysSettingDto() : sysSettingDto;
        return sysSettingDto;
    }

    public void saveSysSetting(SysSettingDto sysSettingDto) {
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingDto);
    }

    public void cleanUserContact(String userId) {
        redisUtils.delete(Constants.REDIS_KEY_USER_CONTACT + userId);
    }

    public void addUserContactBatch(String userId, List<String> contactList) {
        redisUtils.lpushAll(Constants.REDIS_KEY_USER_CONTACT + userId, contactList, Constants.REDIS_TIME_2DAY);
    }

    public List<String> getUserContactList(String userId) {
        return (List<String>)redisUtils.getQueueList(Constants.REDIS_KEY_USER_CONTACT + userId);
    }
}
