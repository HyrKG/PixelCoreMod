package cn.hyrkg.pixelgame.module.dialog;

import java.util.UUID;

import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.JsonContent;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.PropertyShader;
import cn.hyrkg.fastforge_v2.spigotlink.pixelcore.forgeui.SharedProperty;

public class PropertyDialog extends PropertyShader {

	public final JsonContent<String> title = cStr("title");
	public final JsonContent<String> targetName = cStr("tname");
	public final JsonContent<UUID> targetUid = cUUID("tuid");

	public PropertyDialog(SharedProperty property) {
		super(property);
	}

}
