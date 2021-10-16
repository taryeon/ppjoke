package com.example.ppjoke.model;

public class Destination {

    private boolean asStarter;
    private boolean needLogin;
    private String pageUrl;
    private String clazName;
    private int id;
    private boolean isFragement;

    public boolean isAsStarter() {
        return asStarter;
    }

    public void setAsStarter(boolean asStarter) {
        this.asStarter = asStarter;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getClazName() {
        return clazName;
    }

    public void setClazName(String clazName) {
        this.clazName = clazName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFragement() {
        return isFragement;
    }

    public void setFragement(boolean isFragement) {
        this.isFragement = isFragement;
    }
}
