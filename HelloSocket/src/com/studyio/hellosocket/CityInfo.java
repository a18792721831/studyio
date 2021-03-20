package com.studyio.hellosocket;

import lombok.Data;

import java.util.List;

/**
 * @author jiayq
 * @Date 2021-03-12
 */
@Data
public class CityInfo {
    private String code;
    private String name;
    private List<CityInfo> children;
    private int level;

    public CityInfo() {
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public List<CityInfo> getChildren() {
        return this.children;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChildren(List<CityInfo> children) {
        this.children = children;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CityInfo)) return false;
        final CityInfo other = (CityInfo) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$code = this.getCode();
        final Object other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$children = this.getChildren();
        final Object other$children = other.getChildren();
        if (this$children == null ? other$children != null : !this$children.equals(other$children)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CityInfo;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $code = this.getCode();
        result = result * PRIME + ($code == null ? 43 : $code.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $children = this.getChildren();
        result = result * PRIME + ($children == null ? 43 : $children.hashCode());
        return result;
    }

    public String toString() {
        return "CityInfo(code=" + this.getCode() + ", name=" + this.getName() + ", children=" + this.getChildren() + ")";
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
