package xyz.tehbrian.tfcplugin.util.msg;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.tehbrian.tfcplugin.TFCPlugin;
import xyz.tehbrian.tfcplugin.util.MiscUtils;

public class MsgBuilder {

    private String msgKey;
    private String msgString;
    private String prefixKey;
    private String prefixString;
    private Object[] formats;

    public MsgBuilder def(final String msgKey) {
        this.msgKey = msgKey;
        this.prefixKey = "prefixes.server.prefix";
        return this;
    }

    public MsgBuilder msgKey(final String msgKey) {
        this.msgKey = msgKey;
        return this;
    }

    public MsgBuilder msgString(final String msgString) {
        this.msgString = msgString;
        return this;
    }

    public MsgBuilder prefixKey(final String prefixKey) {
        this.prefixKey = prefixKey;
        return this;
    }

    public MsgBuilder prefixString(final String prefixString) {
        this.prefixString = prefixString;
        return this;
    }

    public MsgBuilder formats(final Object... formats) {
        this.formats = formats;
        return this;
    }

    public String build() {
        FileConfiguration config = TFCPlugin.getInstance().getConfig();
        StringBuilder sb = new StringBuilder();

        if (this.prefixKey != null) {
            sb.append(config.getString(this.prefixKey)).append(" ");
        } else if (this.prefixString != null) {
            sb.append(this.prefixString).append(" ");
        }

        if (this.msgKey != null) {
            sb.append(config.getString(this.msgKey));
        } else if (this.msgString != null) {
            sb.append(this.msgString);
        }

        String message = sb.toString();

        if (this.formats != null) {
            message = String.format(message, this.formats);
        }

        return MiscUtils.color(message);
    }
}
