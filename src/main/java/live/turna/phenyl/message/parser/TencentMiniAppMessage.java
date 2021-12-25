

package live.turna.phenyl.message.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TencentMiniAppMessage {

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

    public String getPrompt() {
        return prompt;
    }

    public Meta getMeta() {
        return meta;
    }

    public class Config {

        @SerializedName("autoSize")
        @Expose
        public Integer autoSize;
        @SerializedName("ctime")
        @Expose
        public Integer ctime;
        @SerializedName("forward")
        @Expose
        public Integer forward;
        @SerializedName("height")
        @Expose
        public Integer height;
        @SerializedName("token")
        @Expose
        public String token;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("width")
        @Expose
        public Integer width;

    }

    public class Detail1 {

        @SerializedName("appid")
        @Expose
        public String appid;
        @SerializedName("desc")
        @Expose
        public String desc;
        @SerializedName("gamePoints")
        @Expose
        public String gamePoints;
        @SerializedName("gamePointsUrl")
        @Expose
        public String gamePointsUrl;
        @SerializedName("host")
        @Expose
        public Host host;
        @SerializedName("icon")
        @Expose
        public String icon;
        @SerializedName("preview")
        @Expose
        public String preview;
        @SerializedName("qqdocurl")
        @Expose
        public String qqdocurl;
        @SerializedName("scene")
        @Expose
        public Integer scene;
        @SerializedName("shareTemplateData")
        @Expose
        public ShareTemplateData shareTemplateData;
        @SerializedName("shareTemplateId")
        @Expose
        public String shareTemplateId;
        @SerializedName("showLittleTail")
        @Expose
        public String showLittleTail;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("url")
        @Expose
        public String url;

        public String getDesc() {
            return desc;
        }

        public String getQqdocurl() {
            return qqdocurl;
        }


    }

    public class Host {

        @SerializedName("nick")
        @Expose
        public String nick;
        @SerializedName("uin")
        @Expose
        public Integer uin;

    }

    public class Meta {

        @SerializedName("detail_1")
        @Expose
        public Detail1 detail1;

        public Detail1 getDetail1() {
            return detail1;
        }
    }

    public class ShareTemplateData {
    }

}