package com.shiro.common;


import lombok.Data;

import java.io.Serializable;


@Data
public class DeviceInfo implements Serializable {
    private String phone;

    private String pc;

    private String tablet;


    public String getByType(String type) {
        switch (type) {
            case "phone":
                return phone;
            case "pc":
                return pc;
            case "tablet":
                return tablet;
        }
        return null;
    }

    public void setByType(String type, String sessionId) {
        switch (type) {
            case "phone":
                this.setPhone(sessionId);
                break;
            case "pc":
                this.setPc(sessionId);
                break;
            case "tablet":
                this.setTablet(sessionId);
                break;
        }
    }
}
