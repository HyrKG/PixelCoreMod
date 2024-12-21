package cn.hyrkg.pixelgame.dto.hud;

public class ToastPacket {
	// toast类型
	public ToastType type;
	// json数据
	public String j;
	// 唯一ID，为空则以文本内容为ID
	public String sn = null;
	// 持续时间（秒）
	public long d;
}
