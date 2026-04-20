package com.bintang.service;

import com.bintang.entity.Setting;
import com.bintang.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SettingService {
    
    @Autowired
    private SettingRepository settingRepository;
    
    public String getSettingValue(String key, String defaultValue) {
        return settingRepository.findBySettingKey(key)
                .map(Setting::getSettingValue)
                .orElse(defaultValue);
    }
    
    public void updateSetting(String key, String value, String description) {
        Setting setting = settingRepository.findBySettingKey(key)
                .orElse(new Setting());
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setDescription(description);
        settingRepository.save(setting);
    }
}
