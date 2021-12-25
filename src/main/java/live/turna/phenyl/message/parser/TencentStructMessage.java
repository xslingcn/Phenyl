
package live.turna.phenyl.message.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TencentStructMessage {

    @SerializedName("app")
    @Expose
    public String app;
    @SerializedName("desc")
    @Expose
    public String desc;
    @SerializedName("view")
    @Expose
    public String view;
    @SerializedName("ver")
    @Expose
    public String ver;
    @SerializedName("prompt")
    @Expose
    public String prompt;
    @SerializedName("meta")
    @Expose
    public Meta meta;
    @SerializedName("config")
    @Expose
    public Config config;

    public class Config {

        @SerializedName("autosize")
        @Expose
        public Boolean autosize;
        @SerializedName("ctime")
        @Expose
        public Integer ctime;
        @SerializedName("forward")
        @Expose
        public Boolean forward;
        @SerializedName("token")
        @Expose
        public String token;
        @SerializedName("type")
        @Expose
        public String type;

    }

    public class Meta {

        @SerializedName("news")
        @Expose
        public News news;

    }

    public class News {

        @SerializedName("action")
        @Expose
        public String action;
        @SerializedName("android_pkg_name")
        @Expose
        public String androidPkgName;
        @SerializedName("app_type")
        @Expose
        public Integer appType;
        @SerializedName("appid")
        @Expose
        public Integer appid;
        @SerializedName("ctime")
        @Expose
        public Integer ctime;
        @SerializedName("desc")
        @Expose
        public String desc;
        @SerializedName("jumpUrl")
        @Expose
        public String jumpUrl;
        @SerializedName("preview")
        @Expose
        public String preview;
        @SerializedName("source_icon")
        @Expose
        public String sourceIcon;
        @SerializedName("source_url")
        @Expose
        public String sourceUrl;
        @SerializedName("tag")
        @Expose
        public String tag;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("uin")
        @Expose
        public Integer uin;

    }

}
